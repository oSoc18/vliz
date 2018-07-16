package seabedhabitat.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Polygon extends Geometry {
	private final List<Point> points;
	private final int surface;
	private int clippedSurface;
	public Polygon(Point... points) {
		this(Arrays.asList(points));
	}

	public Polygon(List<Point> points) {
		super("Polygon");
		this.points = points;
		surface = points.size();
	}

	public int getSurface() {
		return surface/2;
	}

	public int getClippedSurface() {
		return clippedSurface/2;
	}

	@Override
	public String getCoordinates() {
		StringBuilder sb = new StringBuilder();
		sb.append("[[ ");
		for (Point point : points) {
			sb.append(point.getCoordinates());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(" ]]");
		return sb.toString();
	}

	@Override
	public double surfaceArea() {
		int diff = 0;
		if (points.get(0).equals(points.get(points.size() - 1))) {
			diff = 1;
		}
		double surface = 0.0;
		for (int i = 0; i < points.size() - diff; i++) {
			Point p = points.get(i);
			Point n = points.get((i + 1) % points.size());
			surface += (n.getLat() - p.getLat()) * (p.getLon() + n.getLon());
		}

		return Math.abs(surface) / 2;

	}

	@Override
	public Polygon clippedWith(Rectangle r) {
		Polygon clipper = r.asPolygon();
		List<Point> newPolygon = new ArrayList<>(this.points);
		int len = clipper.points.size();
		for (int i = 0; i < len; i++) {

			int curLen = newPolygon.size();

			List<Point> input = newPolygon;
			newPolygon = new ArrayList<>();

			Point A = clipper.points.get((i + len - 1) % len);
			Point B = clipper.points.get(i);

			for (int j = 0; j < curLen; j++) {

				Point P = input.get((j + curLen - 1) % curLen);
				Point Q = input.get(j);

				if (isInside(A, B, Q)) {
					if (!isInside(A, B, P)) {
						newPolygon.add(intersection(A, B, P, Q));
					}
					newPolygon.add(Q);
				} else if (isInside(A, B, P))
					newPolygon.add(intersection(A, B, P, Q));
			}
		}
		
		clippedSurface = newPolygon.size();
		
		if(newPolygon.isEmpty()) {
			return null;
		}
		
		return new Polygon(newPolygon);
	}

	private static boolean isInside(Point a, Point b, Point c) {
		return (a.getLat() - c.getLat()) * (b.getLon() - c.getLon()) > (a.getLon() - c.getLon())
				* (b.getLat() - c.getLat());
	}

	private static Point intersection(Point a, Point b, Point p, Point q) {
		double A1 = b.getLon() - a.getLon();
		double B1 = a.getLat() - b.getLat();
		double C1 = A1 * a.getLat() + B1 * a.getLon();

		double A2 = q.getLon() - p.getLon();
		double B2 = p.getLat() - q.getLat();
		double C2 = A2 * p.getLat() + B2 * p.getLon();

		double det = A1 * B2 - A2 * B1;
		double x = (B2 * C1 - B1 * C2) / det;
		double y = (A1 * C2 - A2 * C1) / det;

		return new Point(x, y);
	}

}
