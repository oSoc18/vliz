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
		String result = "";
		for (Point point : points) {
			result += point.getCoordinates()+", ";
		}
		result = result.substring(0, result.length() - 2); // drop the trailing comma
		return "[" + result + "]";
	}

}
