package feature;

import java.util.ArrayList;
import java.util.List;

import exceptions.FatalException;

public class GeometryFactory {
	public static Geometry newPoint(List<?> point) {
		if (point == null || point.size() == 0) {
			return null;
		}
		// Geojson specs : lon lat
		double lat = getDouble(point.get(1));
		double lon = getDouble(point.get(0));
		if(lat > 90 || lon > 180)
		{
			throw new FatalException("Wrong coordinate system. Latitude is above 90 degrees or longitude above 180");
		}
		
		
		
		return new Point(lat, lon);
	}

	private static double getDouble(Object val) {
		if (val instanceof Long) {
			return ((Long) val).doubleValue();
		}
		return (double) val;
	}

	public static Geometry newPoint(String s) {
		if (s == null) {
			return null;
		}
		String[] splitedS = s.split(" ");
		// XML spec : lat long
		return new Point(Double.parseDouble(splitedS[0]), Double.parseDouble(splitedS[1]));
	}

	public static Geometry newPolygon(List<List<List<Double>>> polygon) {
		if (polygon == null || polygon.size() == 0) {
			return null;
		}
		List<Point> l = new ArrayList<>();
		for (List<List<Double>> coords : polygon) {
			for (List<Double> point : coords) {
				l.add((Point) newPoint(point));
			}
		}
		return new Polygon(l);
	}

	public static Geometry newPolygon(String s) {
		if (s == null) {
			return null;
		}
		return new Polygon(createPoints(s));
	}

	public static Geometry newMultiPolygon(List<List<List<List<Double>>>> multiPolygon) {
		if (multiPolygon == null || multiPolygon.size() == 0) {
			return null;
		}
		MultiPolygon multiPol = new MultiPolygon();
		for (List<List<List<Double>>> polygon : multiPolygon) {
			multiPol.addPolygon((Polygon) newPolygon(polygon));
		}
		return multiPol;
	}

	public static Geometry newLineString(List<List<Double>> line) {
		if (line == null || line.size() == 0) {
			return null;
		}
		return new LineString(createPoints(line));
	}

	public static Geometry newLineString(String s) {
		if (s == null) {
			return null;
		}
		return new LineString(createPoints(s));
	}

	public static Geometry newMultiLineString(List<List<List<Double>>> lines) {
		if (lines == null || lines.size() == 0) {
			return null;
		}
		List<LineString> list = new ArrayList<>();
		for (List<List<Double>> line : lines) {
			list.add((LineString) newLineString(line));
		}
		return new MultiLineString(list);
	}

	public static Geometry newMultiPoint(List<List<Double>> points) {
		if (points == null || points.size() == 0) {
			return null;
		}
		return new MultiPoint(createPoints(points));
	}

	private static List<Point> createPoints(List<List<Double>> points) {
		List<Point> list = new ArrayList<>();
		for (List<Double> l : points) {
			list.add((Point) newPoint(l));
		}
		return list;
	}

	private static List<Point> createPoints(String s) {
		List<Point> l = new ArrayList<>();
		String[] sSplited = s.split(" ");
		for (int i = 0; i < sSplited.length; i += 2) {
			l.add((Point) newPoint(sSplited[i] + " " + sSplited[i + 1]));
		}
		return l;
	}

}
