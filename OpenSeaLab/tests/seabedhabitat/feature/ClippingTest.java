package seabedhabitat.feature;

import static org.junit.Assert.*;

import org.hamcrest.core.Is;
import org.junit.Test;

public class ClippingTest {
	
	@Test
	public void testClipping() throws Exception {
		Rectangle r = new Rectangle(0, 0, 1, 2);
		Point p = new Point(0.5, 0.5);
		assertTrue(r.containsPoint(p));
		assertTrue(p.clippedWith(r) != null);
		
		
		Polygon poly = new Rectangle(0.1, 0.1, 0.9, 0.9).asPolygon();
	//	Polygon poly0 = poly.clippedWith(r);
	//	assertTrue(poly.toGeoJSON().equals(poly0.toGeoJSON()));
		
		Polygon poly1 = new Rectangle(0.1, 0.1, 0.9, 2.1).asPolygon().clippedWith(r);
		System.out.println(poly1);
		
	}

}
