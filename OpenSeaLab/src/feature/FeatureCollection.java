package feature;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FeatureCollection implements Serializable {
	private static final long serialVersionUID = 1L;
	private final List<Feature> features;

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
	public SurfaceCount calculateTotals(String dividingProperty) {
		double totalArea = 0;
		HashMap<String, Double> parts = new HashMap<>();
		parts.put("points", 0.0);
		for (Feature f : features) {
			Geometry geo = f.getGeometry();
			Map<String, Object> m = f.getProperties();
			String name = (String) m.get(dividingProperty); // used "AllcombD" previously
			Double s = parts.getOrDefault(name, 0.0);
			parts.put(name, s + geo.surfaceArea());
			totalArea += geo.surfaceArea();
			if (f.getGeometry().getType().equals("Point")) {
				parts.put("points", parts.getOrDefault("points", 0.0) + 1.0);
			}
		}
		return new SurfaceCount(totalArea, parts);
	}

	public FeatureCollection joinWith(FeatureCollection fc) {
		if (fc == null) {
			return this;
		}
		List<Feature> feats = new ArrayList<>(this.features.size() + fc.features.size());
		feats.addAll(this.features);
		feats.addAll(fc.features);
		return new FeatureCollection(feats);
	}
	
	public void deduplicate() {
		List<Feature> deduped = new ArrayList<>();
		List<Feature> toRemove = new ArrayList<>();
		for (Iterator<Feature> iterator = features.iterator(); iterator.hasNext();) {
			Feature feature = iterator.next();
			
			if(!feature.getGeometry().getType().equals("MultiPolygon")) {
				continue;
			}
			
			MultiPolygon mp = (MultiPolygon) feature.getGeometry();
			if(mp.getExteriorRings().size() > 0) {
				List<Polygon> polygons = mp.getExteriorRings();
				for (Polygon p : polygons) {
					Feature f = feature.copy();
					f.setGeometry(p);
					deduped.add(f);
				}
				toRemove.add(feature);
			}
		}
		features.removeAll(toRemove);
		features.addAll(deduped);
	}

	public void remove(Feature feature) {
		features.remove(feature);
	}

	public void removeLast() {
		features.remove(features.size() - 1);
	}

}
