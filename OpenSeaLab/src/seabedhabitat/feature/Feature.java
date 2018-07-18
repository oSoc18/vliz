package seabedhabitat.feature;

import java.util.HashMap;
import java.util.Map;

public class Feature {
	private final String type ="Feature";
	private Point[] bbox = new Point[2];
	private Geometry geometry;
	private Map<String, Object> properties = new HashMap<>();
	
	public Point[] getBbox() {
		return bbox;
	}
	public void setBbox(Point[] bbox) {
		this.bbox = bbox;
	}
	public Geometry getGeometry() {
		return geometry;
	}
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
	public String getType() {
		return type;
	}
	
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String toGeoJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ \"type\": \"");
		sb.append(type);
		sb.append("\", \"bbox\": [");
		sb.append(bbox[0].getLat()+","+bbox[0].getLon()+","+bbox[1].getLat()+","+bbox[1].getLon()+ "],");
		sb.append("\n");
		sb.append(geometry.toGeoJSON());
		sb.append("\n");
		sb.append(", \"properties\": { ");
		for(Map.Entry<String, Object> entries : properties.entrySet()) {
			if(entries.getValue() instanceof String) {
				sb.append("\""+entries.getKey()+"\": \""+entries.getValue()+"\", ");
			} else {
				//TODO if needed
			}
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(" }}");
		return sb.toString();
	}
	
	@Override
	public String toString() {
		return toGeoJSON();
	}
	

	
	
	public Feature clippedWith(Rectangle r) {
		Feature f=  new Feature();
		f.bbox = r.asBBox();
		f.geometry = this.geometry.clippedWith(r);
		f.properties = this.properties;
		return f;
	}
	
}
