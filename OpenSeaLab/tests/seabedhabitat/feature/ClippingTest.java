package seabedhabitat.feature;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import feature.Point;
import feature.Polygon;
import feature.Rectangle;

public class ClippingTest {
	
	@SuppressWarnings("static-method")
	@Test
	public void testClipping() throws Exception {
		Rectangle r = new Rectangle(0, 0, 1, 2);
		Point p = new Point(0.5, 0.5);
		assertTrue(r.containsPoint(p));
		assertTrue(p.clippedWith(r) != null);
		
		
		Polygon poly = new Rectangle(0.1, 0.1, 0.9, 0.9).asPolygon();
		Polygon poly0 = poly.clippedWith(r);
		assertTrue(poly.toGeoJSON().equals(poly0.toGeoJSON()));
		
		Polygon poly1 = new Rectangle(0.1, 0.1, 0.9, 2.1).asPolygon().clippedWith(r);
		String expected = "\"geometry\": { \"type\": \"Polygon\", \"coordinates\": [[ [0.1, 0.1], [2.0, 0.1], [2.0, 0.9], [0.1, 0.9] ]] }";
		assertTrue(expected.equals(poly1.toGeoJSON()));
		
	}

}
