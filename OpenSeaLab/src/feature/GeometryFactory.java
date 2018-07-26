package feature;

import java.util.ArrayList;
import java.util.List;

import exceptions.FatalException;

public class GeometryFactory {

	/**
	 * Instantiate a {@link Point} object. This method should be called when parsing
	 * a json. file.
	 * 
	 * @param point
	 *            point coordinate, relevant size of list is two.
	 * @return {@link Geometry}, null if parameter null or size 0
	 */
	public static Geometry newPoint(List<Double> point) {
		if (point == null || point.size() == 0) {
			return null;
		}
		// Geojson specs : lon lat
		double lat = getDouble(point.get(1));
		double lon = getDouble(point.get(0));
		if (lat > 90 || lon > 180) {
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

	/**
	 * Instantiate a {@link Point} object. This method should be called when parsing
	 * an XML. file.
	 * 
	 * @param s
	 *            string that contains 2 double numbers and a space in between
	 * @return {@link Geometry}, null if parameter null
	 */
	public static Point newPoint(String s) {
		if (s == null) {
			return null;
		}
		String[] splitedS = s.split(" ");
		// XML spec : lat long
		return new Point(Double.parseDouble(splitedS[0]), Double.parseDouble(splitedS[1]));
	}

	/**
	 * Instantiate a {@link Polygon} object. This method should be called when
	 * parsing a json.
	 * 
	 * @param polygon
	 *            list of polygon coordinates
	 * @return {@link Geometry}, null if parameter null or size 0
	 */
	public static Polygon newPolygon(List<List<List<Double>>> polygon) {
		if (polygon == null || polygon.size() == 0) {
			return null;
		}
		Polygon pol = new Polygon();
		for (List<List<Double>> ring : polygon) {
			pol.addRing(createPoints(ring));
		}
		return pol;
	}

	public static Polygon newPolygon0(List<List<Double>> polygon) {
		if (polygon == null || polygon.size() == 0) {
			return null;
		}
		List<Point> l = new ArrayList<>();
		for (List<Double> point : polygon) {
			l.add((Point) newPoint(point));
		}
		return new Polygon(l);
	}

	/**
	 * Instantiate a {@link Polygon} object. This method should be called when
	 * parsing an XML.
	 * 
	 * @param s
	 *            string of polygon coordinates
	 * @return {@link Geometry}, null if parameter null
	 */
	public static Polygon newPolygon(String s) {
		if (s == null) {
			return null;
		}
		return new Polygon(createPoints(s));
	}

	/**
	 * Instantiate a {@link MultiPolygon} object. This method should be called when
	 * parsing a json.
	 * 
	 * @param multiPolygon
	 *            list of multipolygon coordinates
	 * @return {@link Geometry}, null if parameter null or size 0
	 */
	public static MultiPolygon newMultiPolygon(List<List<List<List<Double>>>> multiPolygon) {
		if (multiPolygon == null || multiPolygon.size() == 0) {
			return null;
		}
		List<Polygon> polygons = new ArrayList<>();
		for (List<List<Double>> polygon : multiPolygon.get(0)) {
			Polygon p = newPolygon0(polygon);
			polygons.add(p);
		}
		MultiPolygon multiPol = new MultiPolygon(polygons);
		return multiPol;
	}

	/**
	 * Instantiate a {@link LineString} object. This method should be called when
	 * parsing a json.
	 * 
	 * @param line
	 *            list of linestring coordinates
	 * @return {@link Geometry}, null if parameter null or size 0
	 */
	public static Geometry newLineString(List<List<Double>> line) {
		if (line == null || line.size() == 0) {
			return null;
		}
		return new LineString(createPoints(line));
	}

	/**
	 * Instantiate a {@link LineString} object. This method should be called when
	 * parsing an XML.
	 * 
	 * @param s
	 *            string of linestring coordinates
	 * @return {@link Geometry}, null if parameter null
	 */
	public static Geometry newLineString(String s) {
		if (s == null) {
			return null;
		}
		return new LineString(createPoints(s));
	}

	/**
	 * Instantiate a {@link MultiLineString} object. This method should be called
	 * when parsing a json.
	 * 
	 * @param lines
	 * @return {@link Geometry}, null if parameter null or size 0
	 */
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

	/**
	 * Instantiate a {@link MultiPoint} object. This method should be called when
	 * parsing a json.
	 * 
	 * @param points
	 *            list of multipoint coordinates
	 * @return {@link Geometry}, null if parameter null or size 0
	 */
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

	public static List<Point> createPoints(String s) {
		List<Point> l = new ArrayList<>();
		String[] sSplited = s.split(" ");
		for (int i = 0; i < sSplited.length; i += 2) {
			l.add((Point) newPoint(sSplited[i] + " " + sSplited[i + 1]));
		}
		return l;
	}

}
