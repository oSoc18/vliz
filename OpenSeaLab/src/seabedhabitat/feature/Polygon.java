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
		for (Point point : points) {
			sb.append(point.getCoordinates()+", ");
		}
		String result = sb.toString().substring(0, sb.length() - 2); // drop the trailing comma
		return "[[ " + result + " ]]";
	}

}
