package main;

import feature.FeatureCollection;
import feature.Rectangle;
import feature.SurfaceCount;

public interface LayerProvider {

	FeatureCollection retrieve(Rectangle bbox, String type, String dividingProperty, boolean cacheOnly, String geomType);

	SurfaceCount retrieveStats(Rectangle bbox, String type, String dividingProperty, String geomType);

}
