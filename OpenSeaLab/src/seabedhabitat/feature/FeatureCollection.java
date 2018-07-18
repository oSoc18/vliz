package seabedhabitat.feature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureCollection implements Serializable{
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
			return "{ \"type\": \"FeatureCollection\", \"features\":[]}";
		}
		StringBuilder result = new StringBuilder();
		result.append("{ \"type\": \"FeatureCollection\", \"features\": \n [");
		for (Feature f : features) {
			result.append(f.toGeoJSON());
			result.append(", \n");
		}
		result.delete(result.length() - 3, result.length());
		return result.append("]\n}").toString();
	}

	@Override
	public String toString() {
		return toGeoJSON();
	}

	public FeatureCollection clippedWith(Rectangle r) {
		List<Feature> features = new ArrayList<>();
		for (Feature feature : this.features) {
			Feature f = feature.clippedWith(r);
			if (f != null) {
				features.add(f);
			}
		}
		return new FeatureCollection(features);
	}

	/**
	 * Will calculate the total area of each kind of surface and save that into
	 * 'totalsToBeFilled'. The total surface is returned
	 * 
	 * @return
	 */
	public double calculateTotals(Map<String, Double> totalsToBeFilled) {
		double totalArea = 0;
		for (Feature f : features) {
			Geometry geo = f.getGeometry();
			Map<String, Object> m = f.getProperties();
			String name = (String) m.get("WEB_CLASS"); // used "AllcombD" previously
			Double s = totalsToBeFilled.getOrDefault(name, 0.0);
			totalsToBeFilled.put(name, s + geo.surfaceArea());
			totalArea += geo.surfaceArea();
		}
		return totalArea;
	}

	public Map<String, Double> calculatePercentages() {
		Map<String, Double> totals = new HashMap<>();
		double total = calculateTotals(totals);
		for (String k : totals.keySet()) {
			totals.put(k, 100 * totals.get(k) / total);
		}
		return totals;
	}
	
	

}
