package seabedhabitat.feature;

import static org.junit.Assert.*;

import org.junit.Test;

public class GeoJSONTests {

	@Test
	public void test() {
		Point  p0 = new Point(51.123, 4.123);
		Point  p1 = new Point(51.345, 4.567);
		Point  p2 = new Point(51.678, 4.890);

		System.out.println(p0.toGeoJSON());
		
		Polygon p = new Polygon(p0, p1, p2);
		System.out.println(p.toGeoJSON());
	}

}
