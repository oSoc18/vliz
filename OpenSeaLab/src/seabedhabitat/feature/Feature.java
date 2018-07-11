package seabedhabitat.feature;

import java.util.HashMap;
import java.util.Map;

public class Feature {
	private final String type ="Feature";
	private Point[] bbox = new Point[2];
	private Geometry geometry;
	//private Map<String, Object> properties = new HashMap<>();
	
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
	/*public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}*/
	public String getType() {
		return type;
	}
	
	
	public String toGeoJSON() {
		return "{" + "\"type\": \"" + type +"\"" +","+ "\"bbox\": ["+bbox[0].getLat()+","+bbox[0].getLon()+","+bbox[1].getLat()+","+bbox[1].getLon()+ "],"+ geometry+"}";
	}
	
	@Override
	public String toString() {
		return toGeoJSON();
	}
	
	
}
