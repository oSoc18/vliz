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

	@Override
	public double surfaceArea() {
		double area = 0;
		for (Polygon polygon : polygons) {
			area += polygon.surfaceArea();
		}
		return area;
	}

	@Override
	public MultiPolygon clippedWith(Rectangle r) {
		List<Polygon> newPolyes = new ArrayList<>();
		for (Polygon polygon : polygons) {
			Polygon n = polygon.clippedWith(r);
			if(n != null) {
				newPolyes.add(n);
			}
		}
		if(newPolyes.isEmpty()) {
			return null;
		}
		MultiPolygon mp = new MultiPolygon(newPolyes);
		return mp;
	}

}
