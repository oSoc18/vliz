package main;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import exceptions.BizzException;
import feature.FeatureCollection;
import feature.Rectangle;
import feature.Square;
import feature.SurfaceCount;
import vectorLayers.VectorLayersDAO;

public class PiecedCachingManager implements LayerProvider {

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
	 * @param dividingProperty
	 *            feature identifier (categorie)
	 * @return
	 */
	public FeatureCollection retrieve(Rectangle bbox, String type, String dividingProperty, boolean onlyUseCache,
			String geomType) {

		Rectangle extended = bbox.extendRectangle();
		FeatureCollection total = new FeatureCollection();
		for (int lat = (int) extended.getMinLat(); lat < extended.getMaxLat(); lat++) {
			for (int lon = (int) extended.getMinLon(); lon < extended.getMaxLon(); lon++) {
				FeatureCollection found = loadAndCachePart(lat, lon, type, dividingProperty, onlyUseCache, geomType);
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
	 * @param dividingProperty
	 * @return
	 */
	public FeatureCollection retrieve(Rectangle bbox, String type, String dividingProperty, String geomType) {
		return retrieve(bbox, type, dividingProperty, false, geomType);
	}

	public SurfaceCount retrieveStats(Rectangle bbox, String type, String dividingProperty, String geomType) {
		Rectangle extended = bbox.extendRectangle();

		SurfaceCount sc = new SurfaceCount();

		for (int lat = (int) extended.getMinLat(); lat < extended.getMaxLat(); lat++) {
			for (int lon = (int) extended.getMinLon(); lon < extended.getMaxLon(); lon++) {

				Rectangle searched = new Rectangle(lat, lon, lat + 1, lon + 1);

				if (bbox.edgePoint(lat, lon)) {
					// we can't load the statistic of cache as this is an edgepoint
					FeatureCollection found = loadAndCachePart(lat, lon, type, dividingProperty, false, geomType);
					sc = sc.merge(found.clippedWith(bbox).calculateTotals(dividingProperty));
					continue;
				}

				if (statisticsCaching.isInCache(searched, type)) {
					// statistics are in cache!
					SurfaceCount found = statisticsCaching.restore(searched, type);
					sc = sc.merge(found);
					continue;
				}

				// statistics are not in the cache yet
				FeatureCollection found = loadAndCachePart(lat, lon, type, dividingProperty, false, geomType);
				sc = sc.merge(found.calculateTotals(dividingProperty));
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
	 * @param dividingProperty
	 */
	public void loadAndCacheAll(Rectangle bbox, String type, String dividingProperty, Runnable whenDone) {
		bbox = bbox.extendRectangle();
		if (caching.isInCache(new Square(bbox.getMinLat(), bbox.getMinLon()), type)) {
			// already cached! Abort
			LOGGER.info("Already cached: "+type);
			whenDone.run();
			return;
		}

		LOGGER.info("Computer freeze incoming...");
		final FeatureCollection fromServer = nonCacheProvider.getFeatures(bbox, type);
		LOGGER.info("Everything is downloaded and parsed. Start of clipping + caching...");

		int squaresGoal = (int) ((bbox.getMaxLat() - bbox.getMinLat()) * (bbox.getMaxLon() - bbox.getMinLon()));
		int squaresFormat = ("" + squaresGoal).length();

		ExecutorService threads = Executors.newFixedThreadPool(3);

		System.out.printf("%" + squaresFormat + "d/%d", squaresDone, squaresGoal);
		for (int lat = (int) bbox.getMinLat(); lat < bbox.getMaxLat(); lat++) {
			for (int lon = (int) bbox.getMinLon(); lon < bbox.getMaxLon(); lon++) {
				final Square s = new Square(lat, lon);
				Runnable task = new Runnable() {
					@Override
					public void run() {
						FeatureCollection toCache = fromServer.clippedWith(s);
						caching.store(toCache, s, type);
						SurfaceCount stats = toCache.calculateTotals(dividingProperty);
						statisticsCaching.store(stats, s, type);

						synchronized (threads) {
							squaresDone++;
							System.out.printf("\r%" + squaresFormat + "d/%d", squaresDone, squaresGoal);
							if(squaresDone == squaresGoal && whenDone != null) {
								LOGGER.info("All done with layer "+type);
								new Thread(whenDone).start();
							}
						}
					}

				};
				threads.submit(task);
			}
		}
		threads.shutdown();

	}

	private FeatureCollection loadAndCachePart(int lat, int lon, String type, String dividingProperty,
			boolean onlyUseCache, String geomType) {
		Rectangle searched = new Square(lat, lon);
		FeatureCollection found = null;
		if (caching.isInCache(searched, type)) {
			found = caching.restore(searched, type);
		}

		if (found == null && !onlyUseCache) {
			// caching file might have gotten corrupted and might have returned null
			found = nonCacheProvider.getFeatures(searched, type);
			caching.store(found, searched, type);
			SurfaceCount stats = null;
			if (geomType.equals("polygon")) {
				stats = found.calculateTotals(dividingProperty);
			} else {
				if (geomType.equals("point")) {
					// TODO call to point statistics method
				} else {
					if (geomType.equals("line")) {
						// TODO call to statistics method
					} else {
						throw new BizzException("Unknown geomType.");
					}
				}
			}
			statisticsCaching.store(stats, searched, type);
		}
		return found;
	}

}
