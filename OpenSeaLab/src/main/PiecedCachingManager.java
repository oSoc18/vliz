package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import feature.FeatureCollection;
import feature.Rectangle;
import feature.Square;
import feature.SurfaceCount;
import vectorLayers.VectorLayersDAO;

public class PiecedCachingManager {
	private static final Logger LOGGER = Logger.getLogger(PiecedCachingManager.class.getName());
	private final VectorLayersDAO nonCacheProvider;
	private final CachingManager caching;
	private final CachingManager statisticsCaching;
	private int squaresDone = 0;

	public PiecedCachingManager(VectorLayersDAO nonCacheProvider, CachingManager caching, CachingManager stats) {
		this.nonCacheProvider = nonCacheProvider;
		this.caching = caching;
		this.statisticsCaching = stats;
	}

	/**
	 * This method will attempt to load as much data from the cache. Missing pieces
	 * will be retrieved from the 'nonCacheProvider' (thus the WMS-server) and be
	 * cached as well. To optimize caching, data is retrieved as squares of one by
	 * one degree and are saved.
	 * 
	 * @param bbox
	 * @param type
	 * @return
	 */
	public FeatureCollection retrieve(Rectangle bbox, String type, boolean onlyUseCache) {

		Rectangle extended = bbox.extendRectangle();
		FeatureCollection total = new FeatureCollection();
		for (int lat = (int) extended.getMinLat(); lat < extended.getMaxLat(); lat++) {
			for (int lon = (int) extended.getMinLon(); lon < extended.getMaxLon(); lon++) {
				FeatureCollection found = loadAndCachePart(lat, lon, type, onlyUseCache);
				if (bbox.edgePoint(lat, lon) && found != null) {
					found = found.clippedWith(bbox);
				}
				total = total.joinWith(found);
			}
		}
		return total;
	}

	/**
	 * Delegates the hard work to {@link #retrieve(Rectangle, String, boolean)}
	 * method with the argument onlyUseCache equals to false.
	 * 
	 * @param bbox
	 * @param type
	 * @return
	 */
	public FeatureCollection retrieve(Rectangle bbox, String type) {
		return retrieve(bbox, type, false);
	}

	public SurfaceCount retrieveStats(Rectangle bbox, String type) {
		Rectangle extended = bbox.extendRectangle();

		SurfaceCount sc = new SurfaceCount();

		for (int lat = (int) extended.getMinLat(); lat < extended.getMaxLat(); lat++) {
			for (int lon = (int) extended.getMinLon(); lon < extended.getMaxLon(); lon++) {

				Rectangle searched = new Rectangle(lat, lon, lat + 1, lon + 1);

				if (bbox.edgePoint(lat, lon)) {
					// we can't load the statistic of cache as this is an edgepoint
					FeatureCollection found = loadAndCachePart(lat, lon, type, false);
					sc = sc.merge(found.clippedWith(bbox).calculateTotals());
					continue;
				}

				if (statisticsCaching.isInCache(searched, type)) {
					// statistics are in cache!
					SurfaceCount found = statisticsCaching.restore(searched, type);
					sc = sc.merge(found);
					continue;
				}

				{
					// statistics are not in the cache yet
					FeatureCollection found = loadAndCachePart(lat, lon, type, false);
					sc = sc.merge(found.calculateTotals());
				}

			}
		}
		return sc;
	}

	/**
	 * This method is made to give a huge bbox (such as the entire coverage of the
	 * layer), which will then call the remote WFS server once. It will get a ton of
	 * data back, which it'll start chopping up in pieces of one latitude*one
	 * longitude.
	 * 
	 * (Note: it will start to check if the cache is empty for this layer+type. If
	 * not, the method will not do anything)
	 * 
	 * @param bbox
	 * @param type
	 */
	public void loadAndCacheAll(Rectangle bbox, String type) {
		bbox = bbox.extendRectangle();
		if (caching.isInCache(new Square(bbox.getMinLat(), bbox.getMinLon()), type)) {
			// already cached! Abort
			return;
		}

		System.out.println("Computer freeze incoming...");
		FeatureCollection fromServer = nonCacheProvider.getFeatures(bbox, type);
		System.out.println("Everything is loaded. Start caching...");

		int squaresGoal = (int) ((bbox.getMaxLat() - bbox.getMinLat()) * (bbox.getMaxLon() - bbox.getMinLon()));
		int squaresFormat = ("" + squaresGoal).length();

		ExecutorService threads = Executors.newFixedThreadPool(4);

		System.out.printf("%" + squaresFormat + "d/%d", squaresDone, squaresGoal);
		for (int lat = (int) bbox.getMinLat(); lat < bbox.getMaxLat(); lat++) {
			for (int lon = (int) bbox.getMinLon(); lon < bbox.getMaxLon(); lon++) {
				final Square s = new Square(lat, lon);
				Runnable task = new Runnable() {
					@Override
					public void run() {
						FeatureCollection toCache = fromServer.clippedWith(s);
						caching.store(toCache, s, type);
						SurfaceCount stats = toCache.calculateTotals();
						statisticsCaching.store(stats, s, type);

						squaresDone++;
						System.out.printf("\r%" + squaresFormat + "d/%d", squaresDone, squaresGoal);
					}

				};
				threads.submit(task);
			}
		}

	}

	private FeatureCollection loadAndCachePart(int lat, int lon, String type, boolean onlyUseCache) {
		Rectangle searched = new Square(lat, lon);
		FeatureCollection found = null;
		if (caching.isInCache(searched, type)) {
			found = caching.restore(searched, type);
		}

		if (found == null && !onlyUseCache) {
			// caching file might have gotten corrupted and might have returned null
			found = nonCacheProvider.getFeatures(searched, type);
			caching.store(found, searched, type);
			SurfaceCount stats = found.calculateTotals();
			statisticsCaching.store(stats, searched, type);
		}
		return found;
	}

}
