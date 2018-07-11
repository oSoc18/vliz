package seabedhabitat.feature;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class GeoJSONTests {

	@Test
	public void test() {
		Point  p0 = new Point(51.123, 4.123);
		assert("\"geometry\": { \"type\": \"Point\",\"coordinates\": [51.123, 4.123] }".equals(p0.toGeoJSON()));
		Point  p1 = new Point(51.345, 4.567);
		Point  p2 = new Point(51.678, 4.890);

		Polygon p = new Polygon(p0, p1, p2);
		assert("\"geometry\": { \"type\": \"Polygon\",\"coordinates\": [[ [51.123, 4.123], [51.345, 4.567], [51.678, 4.89] ]] }".equals(p.toGeoJSON()));
		
		Feature f = new Feature();
		f.setBbox(new Point[] {p0, p1});
		f.setGeometry(p);
		
		assert("{\"type\": \"Feature\",\"bbox\": [51.123,4.123,51.345,4.567],\"geometry\": { \"type\": \"Polygon\",\"coordinates\": [[ [51.123, 4.123], [51.345, 4.567], [51.678, 4.89] ]] }}".equals(f.toGeoJSON()));
		
		FeatureCollection fc = new FeatureCollection();
		fc.addFeature(f);
		System.out.println(fc.toGeoJSON());
		
	}

}
