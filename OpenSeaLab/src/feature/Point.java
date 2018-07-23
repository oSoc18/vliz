package feature;

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
		return "[" + lon + ", " + lat + "]";
	}

	@Override
	public double surfaceArea() {
		return 0;
	}

	@Override
	public Geometry clippedWith(Rectangle r) {
		if(r.containsPoint(this)) {
			return this;
		}
		return null;
	}

}
