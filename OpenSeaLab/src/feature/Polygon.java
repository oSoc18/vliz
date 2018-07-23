package feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Polygon extends Geometry {

	private static final long serialVersionUID = 1L;
	private final List<Point> points;
	public Polygon(Point... points) {

		this(Arrays.asList(points));
	}

	public Polygon(List<Point> points) {

		super("Polygon");
		this.points = points;
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

		int pointsCount = points.size() - diff;
		Double	area = 0.0;
		Double d2r = Math.PI / 180;
		Point p1, p2;

		if (pointsCount > 2) {
			for (int i = 0; i < pointsCount; i++) {
				p1 = points.get(i);
				p2 = points.get((i + 1) % pointsCount);

				area += ((p2.getLon() - p1.getLon()) * d2r) *
					(2 + Math.sin(p1.getLat() * d2r) + Math.sin(p2.getLat() * d2r));
			}
			area = area * 6378137.0 * 6378137.0 / 2.0;
		}

		return Math.abs(area);

	}

	@Override
	public Polygon clippedWith(Rectangle r) {
		Polygon clipper = r.asPolygon();
		List<Point> newPolygon = new ArrayList<>(this.points); // copy the polygon to be clipped
		int len = clipper.points.size(); // normally a rectangle

		for (int i = 0; i < len; i++) {
			int curLen = newPolygon.size();
			List<Point> input = newPolygon;
			newPolygon = new ArrayList<>();

			Point A = clipper.points.get((i + len - 1) % len);
			Point B = clipper.points.get(i);
			for (int j = 0; j < curLen; j++) {

				Point P = input.get((j + curLen - 1) % curLen);
				Point Q = input.get(j);
				if (!isInside(A, B, Q)) {  // is Q inside of points A and B
					if (isInside(A, B, P)) { // is P outside of A and B 
						newPolygon.add(intersection(A, B, P, Q));
					}
					newPolygon.add(Q);
				} else if (!isInside(A, B, P)) // is P inside of A and B and Q outside of 
					newPolygon.add(intersection(A, B, P, Q));
			}
		}
		
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
