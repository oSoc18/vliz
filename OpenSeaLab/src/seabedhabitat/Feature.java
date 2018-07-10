package seabedhabitat;

import java.util.HashMap;
import java.util.Map;

public class Feature {
	private final String type ="Feature";
	private double[] bbox = new double[4];
	private Map<String, Object> geometry = new HashMap<>();
	private Map<String, Object> properties = new HashMap<>();
	
	public double[] getBbox() {
		return bbox;
	}
	public void setBbox(double[] bbox) {
		this.bbox = bbox;
	}
	public Map<String, Object> getGeometry() {
		return geometry;
	}
	public void setGeometry(Map<String, Object> geometry) {
		this.geometry = geometry;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	public String getType() {
		return type;
	}
	
	
	
}
