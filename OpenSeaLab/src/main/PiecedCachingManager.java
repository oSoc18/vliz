package main;

import feature.FeatureCollection;
import feature.Rectangle;
import feature.SurfaceCount;
import vectorLayers.UCCVectorLayers;

public class PiecedCachingManager {

	private final UCCVectorLayers nonCacheProvider;
	private final CachingManager caching;
	private final CachingManager statisticsCaching;
	private final String layerName;

	public PiecedCachingManager(String layerName, UCCVectorLayers nonCacheProvider, CachingManager caching, CachingManager stats) {
		this.nonCacheProvider = nonCacheProvider;
		this.caching = caching;
		this.statisticsCaching = stats;
		this.layerName = layerName;
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
		
		// TODO work out something decently for this case
		if(layerName.equals("geology")) return nonCacheProvider.getFeatures(bbox, type);
		
		
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

	private FeatureCollection loadAndCachePart(int lat, int lon, String type, boolean onlyUseCache) {
		Rectangle searched = new Rectangle(lat, lon, lat + 1, lon + 1);
		FeatureCollection found = null;
		if (caching.isInCache(searched, type)) {
			found = caching.restore(searched, type);
		} 
		
		if(found == null && !onlyUseCache){
			// caching file might have gotten corrupted and might have returned null
			found = nonCacheProvider.getFeatures(searched, type);
			caching.store(found, searched, type);
			SurfaceCount stats = found.calculateTotals();
			statisticsCaching.store(stats, searched, type);
		}
		return found;
	}

}