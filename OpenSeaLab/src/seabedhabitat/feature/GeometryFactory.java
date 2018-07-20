package seabedhabitat.feature;

import java.util.ArrayList;
import java.util.List;

public class GeometryFactory {
	public static Geometry getPoint(List<Double> point) {
		if (point == null || point.size() == 0) {
			return null;
		}
		return new Point(point.get(1), point.get(0));
	}

	public static Geometry getPolygon(List<List<List<Double>>> polygon) {
		if (polygon == null || polygon.size() == 0) {
			return null;
		}
		List<Point> l = new ArrayList<>();
		for (List<List<Double>> coords : polygon) {
			for (List<Double> point : coords) {
				l.add((Point) getPoint(point));
			}
		}
		return new Polygon(l);
	}

	public static Geometry getMultiPolygon(List<List<List<List<Double>>>> multiPolygon) {
		if (multiPolygon == null || multiPolygon.size() == 0) {
			return null;
		}
		MultiPolygon multiPol = new MultiPolygon();
		for (List<List<List<Double>>> polygon : multiPolygon) {
			multiPol.addPolygon((Polygon) getPolygon(polygon));
		}
		return multiPol;
	}

	public static Geometry getLineString() {
		return null;
	}

	public static Geometry getMultiLineString() {
		return null;
	}

	public static Geometry getMultiPoint() {
		return null;
	}

}
