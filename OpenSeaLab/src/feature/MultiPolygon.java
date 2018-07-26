package feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPolygon extends Geometry {

	private static final long serialVersionUID = 1L;
	private final List<Polygon> polygons;

	public MultiPolygon(Polygon... polygons) {
		this(Arrays.asList(polygons));
	}

	public MultiPolygon() {
		this(new ArrayList<>());
	}

	public MultiPolygon(List<Polygon> polygons) {
		this(polygons, new ArrayList<>());
	}

	public MultiPolygon(List<Polygon> polygons, List<Polygon> exteriorRings) {
		super("MultiPolygon");
		this.polygons = polygons;
	}

	public void addInteriorPolygon(Polygon polygon) {
		this.polygons.add(polygon);
	}

	@Override
	public String getCoordinates() {
		StringBuilder sb = new StringBuilder();
		// Main ring with holes
		sb.append("[");
		for (Polygon p : polygons) {
			sb.append("\n");
			sb.append(p.getCoordinates());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append("], ");

		sb = sb.delete(sb.length() - 2, sb.length());

		return sb.toString();
	}

	@Override
	public double surfaceArea() {
		double area = polygons.get(0).surfaceArea();
		
		// start index is ONE, as the first polygon is the 'exterior ring', while the rest are interior rings cutting surface away
		for (int i = 1; i < polygons.size(); i++) {
			area -= polygons.get(i).surfaceArea();
		}
		return area;
	}

	@Override
	public MultiPolygon clippedWith(Rectangle r) {
		List<Polygon> newPolys = new ArrayList<>();
		for (Polygon polygon : polygons) {
			Polygon n = polygon.clippedWith(r);
			if (n != null) {
				newPolys.add(n);
			}
		}

		if (newPolys.isEmpty()) {
			return null;
		}
		return new MultiPolygon(newPolys);
	}

}
