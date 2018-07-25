package feature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.owlike.genson.Genson;

public class FeatureCollectionBuilder {
	private final String geoJSON;

	/**
	 * Constructs a feature collection builder for the string parameter.
	 * 
	 * @param geoJSON
	 *            a json string that has geojson specs.
	 */
	public FeatureCollectionBuilder(String geoJSON) {
		this.geoJSON = geoJSON;
	}

	/**
	 * Creates a feature collection from the attribute "geoJSON".
	 * 
	 * @return {@link FeatureCollection}
	 */
	@SuppressWarnings("unchecked")
	public FeatureCollection create() {
		FeatureCollection fc = new FeatureCollection();
		Map<String, Object> map = deserialise();
		ArrayList<Map<String, Object>> features = (ArrayList<Map<String, Object>>) map.get("features");
		if (features != null) {
			for (Map<String, Object> feature : features) {
				fc.addFeature(createFeature(feature));
			}
		}
		return fc;
	}

	/**
	 * This method uses {@link Genson} to deserialise the attribute geoJSON.
	 * 
	 * @return a map
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> deserialise() {
		return new Genson().deserialize(geoJSON, Map.class);
	}

	/**
	 * Creates a feature. The parameter should respect a geojson feature specs.
	 * 
	 * @param feature
	 *            a map that contains feature data
	 * @return {@link Feature}
	 */
	@SuppressWarnings("unchecked")
	private static Feature createFeature(Map<String, Object> feature) {
		Feature f = new Feature();
		f.setProperties((Map<String, Object>) feature.get("properties"));
		f.setGeometry(createGeometry((Map<String, Object>) feature.get("geometry")));
		f.setBbox(createBBox(((List<Double>) feature.get("bbox"))));
		return f;
	}

	/**
	 * Creates a geometry. The parameter should respect a geojson geometry specs.
	 * 
	 * @param geometry
	 *            a map that contains geometry data.
	 * @return {@link Geometry}, can return null
	 */
	@SuppressWarnings("unchecked")
	private static Geometry createGeometry(Map<String, Object> geometry) {
		String type = (String) geometry.get("type");
		String key = "coordinates";
		switch (type) {
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

	/**
	 * Creates a bounding box. The parameter should respect a geojson bbox specs.
	 * 
	 * @param bbox
	 *            list of bbox data
	 * @return bounding box. Basically table of points.
	 */
	private static Point[] createBBox(List<Double> bbox) {
		if (bbox == null)
			return new Point[2];
		// in a geojson file the coordinates has this flow: lon, lat
		return new Point[] { new Point(bbox.get(1), bbox.get(0)), new Point(bbox.get(3), bbox.get(2)) };
	}
}
