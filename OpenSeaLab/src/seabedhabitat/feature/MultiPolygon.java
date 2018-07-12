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
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Polygon p : polygons) {
			sb.append(p.getCoordinates());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		return sb.append("]").toString();
	}

}
