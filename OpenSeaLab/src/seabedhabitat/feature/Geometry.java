package seabedhabitat.feature;

public abstract class Geometry {

	private final String type;

	public Geometry(String type) {
		this.type = type;
	}

	public abstract String getCoordinates();

	/**
	 * Gives the "geometry"-part of the geojson
	 * 
	 * @param geo
	 * @return
	 */
	public String toGeoJSON() {
		StringBuilder result = new StringBuilder();
		result.append("\"geometry\": { \"type\": \"");
		result.append(type);
		result.append("\", \"coordinates\": ");
		result.append(getCoordinates());
		return result.append(" }").toString();
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return this.toGeoJSON();
	}

}
