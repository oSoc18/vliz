package seabedhabitat.feature;

public class Point extends Geometry {

	private final double lat, lon;
	private int clippedSurface;
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
			clippedSurface = 1;
			return this;
		}
		return null;
	}

	@Override
	public int getSurface() {
		return 1;
	}

	@Override
	public int getClippedSurface() {
		return clippedSurface;
	}

}
