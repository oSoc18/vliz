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

		String result = "\"geometry\": { " + "\"type\": \"" + getType() + "\"," + "\"coordinates\": "
				+ this.getCoordinates() + " }";
		return result;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return this.toGeoJSON();
	}

}
