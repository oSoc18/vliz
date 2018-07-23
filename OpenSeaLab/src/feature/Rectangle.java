package feature;

public class Rectangle extends Geometry {

	private final double minLat, minLon, maxLat, maxLon;

	public Rectangle(double minLat, double minLong, double maxLat, double maxLong) {
		super("Polygon");
		this.minLat = Math.min(minLat, maxLat);
		this.maxLat = Math.max(minLat, maxLat);
		this.minLon = Math.min(minLong, maxLong);
		this.maxLon = Math.max(minLong, maxLong);

	}

	public Rectangle(String minLat, String minLong, String maxLat, String maxLong) {
		this(validateArgument("minLat", minLat), validateArgument("minLong", minLong),
				validateArgument("maxLat", maxLat), validateArgument("maxLong", maxLong));
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

	public Point[] asBBox() {
		return new Point[] { new Point(minLat, minLon), new Point(maxLat, maxLon) };
	}

	private static double validateArgument(String argName, String argument) {
		if (argument == null || argument.isEmpty()) {
			throw new IllegalArgumentException("Missing argument: " + argName + ". Please check your coordinates.");
		}
		try {
			return Double.parseDouble(argument);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Could not parse argument " + argName + " as double, it contains " + argument, e);
		}
	}

	/**
	 * Rounds the bounding box, makes minimum lower and maximum higher. The
	 * coordinates are reordered if needed.
	 * 
	 * @param minLat
	 *            minimum latitude
	 * @param minLong
	 *            minimum longitude
	 * @param maxLat
	 *            maximum latitude
	 * @param maxLong
	 *            maximum longitude
	 */
	public Rectangle extendRectangle() {
		return new Rectangle(Math.floor(this.minLat), Math.floor(this.minLon), Math.ceil(this.maxLat),
				Math.ceil(this.maxLon));
	}

	public Rectangle bboxWith(Rectangle r) {
		return new Rectangle(Math.min(this.minLat, r.minLat), Math.min(this.minLon, r.minLon),
				Math.max(this.maxLat, r.maxLat), Math.max(this.maxLon, r.maxLon));
	}
	
	public boolean edgePoint(int lat, int lon) {
		return (Math.floor(this.minLat) == lat || Math.ceil(this.maxLat) == lat +1
				|| Math.floor(this.minLon) == lon || Math.ceil(this.maxLon) == lon+1);
	}
}