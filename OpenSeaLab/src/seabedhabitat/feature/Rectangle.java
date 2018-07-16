package seabedhabitat.feature;

public class Rectangle extends Geometry {

	private final double minLat, minLon, maxLat, maxLon;

	public Rectangle(double minLat, double minLong, double maxLat, double maxLong) {
		super("Polygon");
		this.maxLat = maxLat;
		this.minLat = minLat;
		this.maxLon = maxLong;
		this.minLon = minLong;

	}

	@Override
	public String getCoordinates() {
		return "[[ [" + minLon + ", " + minLat + "], [" + maxLon + ", " + maxLat + "] ]]";
	}

	public Polygon asPolygon() {
		return new Polygon(new Point(minLat, minLon), new Point(minLat, maxLon), new Point(maxLat, maxLon),
				new Point(maxLat, minLon));
	}

	@Override
	public double surfaceArea() {
		return (maxLat - minLat) * (maxLon - minLon);
	}

	@Override
	public Geometry clippedWith(Rectangle r) {
		// TODO Auto-generated method stub
		throw new IllegalArgumentException("No implementation yet");
	}

	public double getMaxLon() {
		return maxLon;
	}

	public double getMinLat() {
		return minLat;
	}

	public double getMinLon() {
		return minLon;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public boolean containsPoint(Point p) {
		if (minLat > p.getLat() || maxLat < p.getLat() || minLon > p.getLon() || maxLon < p.getLon()) {
			return false;
		}
		return true;
	}

	@Override
	public int getSurface() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getClippedSurface() {
		throw new UnsupportedOperationException();
	}
	
	
	public Point[] asBBox() {
		return new Point[] { new Point(minLat, minLon), new Point(maxLat, maxLon) };
	}

}
