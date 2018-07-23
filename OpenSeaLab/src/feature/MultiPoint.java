package feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiPoint extends Geometry {
	private static final long serialVersionUID = 1L;
	private final List<Point> points;

	public MultiPoint(List<Point> points) {
		super("MultiPoint");
		this.points = points;
	}

	public MultiPoint(Point... points) {
		this(Arrays.asList(points));
	}

	public MultiPoint() {
		this(new ArrayList<>());
	}

	public void addPoint(Point point) {
		points.add(point);
	}

	@Override
	public String getCoordinates() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for (Point p : points) {
			sb.append(p.getCoordinates() + ", ");
		}
		return sb.delete(sb.length() - 2, sb.length()).append("]").toString();
	}

	@Override
	public double surfaceArea() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Geometry clippedWith(Rectangle r) {
		// TODO Auto-generated method stub
		return null;
	}

}
