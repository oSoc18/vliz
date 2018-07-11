package seabedhabitat.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPolygon extends Geometry {
	private final List<Polygon> polygons; 
	
	public MultiPolygon(Polygon... polygons) {
		this(Arrays.asList(polygons));
		
	}
	
	public MultiPolygon() {
		this(new ArrayList<>());
	}
	
	public MultiPolygon(List<Polygon> polgons) {
		super("MultiPolygon");
		this.polygons = polgons;
	}
	
	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}

	@Override
	public String getCoordinates() {
		String result = "";
		for (Polygon p : polygons) {
			result += p.getCoordinates() + ", ";
		}
		result = result.substring(0, result.length() - 2); // drop the trailing comma
		return "[" + result + "]";
	}

}
