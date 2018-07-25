package main;

import feature.FeatureCollection;
import feature.Rectangle;
import feature.SurfaceCount;

public interface LayerProvider {

	FeatureCollection retrieve(Rectangle bbox, String type, String dividingProperty, boolean cacheOnly);

	SurfaceCount retrieveStats(Rectangle bbox, String type, String dividingProperty);

}
