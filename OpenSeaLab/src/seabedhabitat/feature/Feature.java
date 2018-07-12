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
	
	public void addProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String toGeoJSON() {
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, Object> entries : properties.entrySet()) {
			if(entries.getValue() instanceof String) {
				sb.append("\""+entries.getKey()+"\": \""+entries.getValue()+"\", ");
			} else {
				//TODO if needed
			}
		}
		String props = sb.toString().substring(0, sb.length() - 2); // drop the trailing comma
		return "{" + "\"type\": \"" + type +"\"" +","+ "\"bbox\": ["+bbox[0].getLat()+","+bbox[0].getLon()+","
				+bbox[1].getLat()+","+bbox[1].getLon()+ "],"+ geometry +", \"properties\": { "+props +" }" +"}";
	}
	
	@Override
	public String toString() {
		return toGeoJSON();
	}
	
	
}
