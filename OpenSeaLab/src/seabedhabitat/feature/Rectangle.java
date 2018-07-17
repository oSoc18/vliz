package seabedhabitat.feature;

import exceptions.BizzException;

public class Rectangle extends Geometry {

	private double minLat, minLon, maxLat, maxLon;

	public Rectangle(double minLat, double minLong, double maxLat, double maxLong) {
		super("Polygon");
		extendRectangle(minLat, minLong, maxLat, maxLong);

	}
	
	public Rectangle(String minLat, String minLong, String maxLat, String maxLong) {
		super("Polygon");
		if (minLat == null || minLat.isEmpty() || minLong == null || minLong.isEmpty() || maxLong == null
				|| minLong.isEmpty() || maxLat == null || maxLat.isEmpty()) {
			throw new BizzException("Missing argument(s). Please check your coordinates.");
		}
		try {
			extendRectangle(Double.parseDouble(minLat), Double.parseDouble(minLong), Double.parseDouble(maxLat), Double.parseDouble(maxLong));
		} catch (NumberFormatException nfe) {
			throw new BizzException("Please enter valid numbers.");
		}
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

	/**
	 * Rounds the bounding box, makes minimum lower and maximum higher. The coordinates are reordered if needed.
	 * @param minLat minimum latitude
	 * @param minLong minimum longitude
	 * @param maxLat maximum latitude
	 * @param maxLong maximum longitude
	 */
	private void extendRectangle(double minLat, double minLong, double maxLat, double maxLong) {
		if (minLat > maxLat) {
			double tempLat = minLat;

			minLat = maxLat;
			maxLat = tempLat;

		}
		if(maxLong < minLong) {
			double temLon = maxLong;
			
			maxLong = minLong;
			minLong = temLon;
		}

		this.minLat = Math.floor(minLat);
		this.minLon = Math.floor(minLong);
		this.maxLat = Math.ceil(maxLat);
		this.maxLon = Math.ceil(maxLong);
	}
}
