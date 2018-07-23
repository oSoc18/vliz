package feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.owlike.genson.Genson;

public class FeatureCollectionBuilder {
	private final String geoJSON;
	public FeatureCollectionBuilder(String geoJSON) {
		this.geoJSON = geoJSON;
	}
	
	@SuppressWarnings("unchecked")
	public FeatureCollection create() {
		FeatureCollection fc = new FeatureCollection();
		Map<String, Object> map = deserialise();
		Integer totalFeatures = (Integer) map.get("totalFeatures");
		if(totalFeatures != null && totalFeatures == 0) return fc;
		ArrayList<Map<String,Object>> features = (ArrayList<Map<String, Object>>) map.get("features");
		for(Map<String, Object> feature : features) {
			fc.addFeature(createFeature(feature));
		}
		return fc;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Object> deserialise(){
		return new Genson().deserialize(geoJSON, Map.class);
	}
	
	@SuppressWarnings("unchecked")
	private Feature createFeature(Map<String, Object> feature) {
		Feature f = new Feature();
		f.setProperties((Map<String, Object>) feature.get("properties"));
		f.setGeometry(createGeometry((Map<String, Object>) feature.get("geometry")));
		f.setBbox(createBBox(((List<Double>)feature.get("bbox"))));
		return f;
	}
	
	@SuppressWarnings("unchecked")
	private Geometry createGeometry(Map<String, Object> geometry) {
		String type = (String) geometry.get("type");
		String key = "coordinates";
		switch(type) {
		case "Point":
			List<Double> point = (List<Double>) geometry.get(key); 
			return GeometryFactory.newPoint(point);
		case "Polygon":
			List<List<List<Double>>> polygon = (List<List<List<Double>>>) geometry.get(key);
			return GeometryFactory.newPolygon(polygon);
		case "MultiPolygon":
			List<List<List<List<Double>>>> multiPolygon = (List<List<List<List<Double>>>>) geometry.get(key);
			return GeometryFactory.newMultiPolygon(multiPolygon);
		case "MultiPoint":
			return GeometryFactory.newMultiPoint((List<List<Double>>) geometry.get(key));
		case "MultiLineString":
			return GeometryFactory.newMultiLineString((List<List<List<Double>>>) geometry.get(key));
		case "LineString":
			return GeometryFactory.newLineString((List<List<Double>>) geometry.get(key));
		default:
			return null;
		}
	}
	
	private Point[] createBBox(List<Double> bbox) {
		return new Point[] {new Point(bbox.get(1), bbox.get(0)), new Point(bbox.get(3), bbox.get(2))};
	}
}
