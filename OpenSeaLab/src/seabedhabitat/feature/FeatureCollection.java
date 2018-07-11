package seabedhabitat.feature;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection {
	private final List<Feature> features;
	private Point[] bbox = new Point[2];
	
	public FeatureCollection(List<Feature> features) {
		this.features = features;
	}
	
	public FeatureCollection() {
		features = new ArrayList<>();
	}
	
	public List<Feature> getFeatures() {
		return features;
	}
	
	public void addFeature(Feature f) {
		features.add(f);
	}
	
	public Point[] getBbox() {
		return bbox;
	}
	
	public String toGeoJSON() {
		if(features.size() == 0) {
			throw new IllegalStateException("No elements in the featurecollection");
		}
		String result = "";
		for(Feature f : features) {
			result+=f.toGeoJSON()+", ";
		}
		result = result.substring(0, result.length() - 2); // drop the trailing comma
		return "{ \"type\": \"FeatureCollection\", \"features\": \n [" + result+"]\n}";
	}
	
	@Override
	public String toString() {
		return toGeoJSON();
	}
}
