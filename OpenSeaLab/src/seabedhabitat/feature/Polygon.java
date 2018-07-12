package seabedhabitat.feature;

import java.util.Arrays;
import java.util.List;

public class Polygon extends Geometry{
	
	private final List<Point> points;
	
	public Polygon(Point... points) {
		this(Arrays.asList(points));
	}
	
	
	public Polygon(List<Point> points) {
		super("Polygon");
		this.points = points;
	}
	
	@Override
	public String getCoordinates() {
		StringBuilder sb = new StringBuilder();
		sb.append("[[ ");
		for (Point point : points) {
			sb.append(point.getCoordinates());
			sb.append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(" ]]");
		return sb.toString();
	}

}
