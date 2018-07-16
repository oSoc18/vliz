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
		if (features.size() == 0) {
			throw new IllegalStateException("No elements in the featurecollection");
		}
		StringBuilder result = new StringBuilder();
		result.append("{ \"type\": \"FeatureCollection\", \"features\": \n [");
		for (Feature f : features) {
			result.append(f.toGeoJSON());
			result.append(", ");
		}
		result.delete(result.length() - 2, result.length());
		return result.append("]\n}").toString();
	}

	@Override
	public String toString() {
		return toGeoJSON();
	}

	public FeatureCollection clippedWith(Rectangle r) {
		List<Feature> features = new ArrayList<>();
		for (Feature feature : this.features) {
			Feature f = feature; //.clippedWith(r);
			if (f != null) {
				features.add(f);
			}
		}
		return new FeatureCollection(features);
	}
}
