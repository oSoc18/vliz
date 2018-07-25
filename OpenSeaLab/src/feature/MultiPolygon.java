package feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPolygon extends Geometry {

	private static final long serialVersionUID = 1L;
	private final List<Polygon> polygons;
	private final List<Polygon> exteriorRings;

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
		this.exteriorRings = exteriorRings;
	}

	public void addExteriorPolygon(Polygon polygon) {
		this.exteriorRings.add(polygon);
	}

	public void addInteriorPolygon(Polygon polygon) {
		this.polygons.add(polygon);
	}

	@Override
	public String getCoordinates() {
		StringBuilder sb = new StringBuilder();
		// Main ring with holes
		if (polygons.size() > 0) {
			sb.append("[");
			for (Polygon p : polygons) {
				sb.append("\n");
				sb.append(p.getCoordinates());
				sb.append(", ");
			}
			sb.delete(sb.length() - 2, sb.length());
			sb.append("], ");
		}

		if (exteriorRings != null && exteriorRings.size() > 0) {
			// Extra exterior rings
			for (Polygon polygon : exteriorRings) {
				sb.append("\n[");
				sb.append(polygon.getCoordinates());
				sb.append("], ");
			}
		}
		sb = sb.delete(sb.length() - 2, sb.length());

		return sb.toString();
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
		List<Polygon> newPolys = new ArrayList<>();
		for (Polygon polygon : polygons) {
			Polygon n = polygon.clippedWith(r);
			if (n != null) {
				newPolys.add(n);
			}
		}
		List<Polygon> newExteriors = new ArrayList<>();
		for (Polygon polygon : exteriorRings) {
			Polygon n = polygon.clippedWith(r);
			if (n != null) {
				newExteriors.add(n);
			}
		}

		if (newPolys.isEmpty() && newExteriors.isEmpty()) {
			return null;
		}
		return new MultiPolygon(newPolys, exteriorRings);
	}

	public List<Polygon> getExteriorRings() {
		return exteriorRings;
	}

}
