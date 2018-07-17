package seabedhabitat.feature;

import org.junit.Test;

public class ClippingPolygonArea {

	@SuppressWarnings("static-method")
	@Test
	public void test() {
		Point  p0 = new Point(45, 4);
		Point  p1 = new Point(45, 6);
		Point  p2 = new Point(43, 4);
		Point  p3 = new Point(43, 6);
		Point  p4 = new Point(45, 4);

		
		Polygon poly = new Polygon(p0, p1, p2,p3,p4);
		
		//Point  p0 = new Point(45.437539, -4.439634);
		//Point  p1 = new Point(45.435623, -4.43505);
		//Point  p2 = new Point(45.434623, -4.441717);
		//Point  p3 = new Point(45.437539, -4.439634);
		
		Rectangle r = new Rectangle(44, 3,49, 7);
		
		Polygon clippedPoly = poly.clippedWith(r);
		
		System.out.println(clippedPoly);
		
		
		
		
		
	}

}
