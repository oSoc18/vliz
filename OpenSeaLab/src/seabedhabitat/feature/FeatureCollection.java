package seabedhabitat.feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
	private List<Feature> features = new ArrayList<>();
	private Point[] bbox = new Point[2];
	
	public List<Feature> getFeatures() {
		return features;
	}
	
	public Point[] getBbox() {
		return bbox;
	}
	
	public String toGeoJSON() {
		String result = "{ \"type\": \"FeatureCollection\", \"features\": ";
		for(Feature f : features) {
			result+=f.toGeoJSON();
		}
		return result+" }";
	}
	
	@Override
	public String toString() {
		return toGeoJSON();
	}
}
