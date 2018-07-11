package seabedhabitat.feature;

public class Point extends Geometry {

	private final double lat, lon;

	public Point(double lat, double lon) {
		super("Point");
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	@Override
	public String getCoordinates() {
		return "[" + lat + ", " + lon + "]";
	}

}
