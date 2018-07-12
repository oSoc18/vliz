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
		StringBuilder result = new StringBuilder();
		for(Feature f : features) {
			result.append(f.toGeoJSON()+", ");
		}
		String r = result.toString().substring(0, result.length() - 2); // drop the trailing comma
		return "{ \"type\": \"FeatureCollection\", \"features\": \n [" + r +"]\n}";
	}
	
	@Override
	public String toString() {
		return toGeoJSON();
	}
}
