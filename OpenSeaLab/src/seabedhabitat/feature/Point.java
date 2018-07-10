package seabedhabitat.feature;

public class Point implements Geometry {

	private final double lat, lon;

	public Point(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	@Override
	public String toGeoJSON() {
		// TODO Auto-generated method stub
		return null;
	}

}
