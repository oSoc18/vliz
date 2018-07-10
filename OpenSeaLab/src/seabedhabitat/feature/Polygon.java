package seabedhabitat.feature;

import java.util.List;

public class Polygon {
	
	private final List<Point> points;
	
	public Polygon(List<Point> points) {
		this.points = points;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (Point point : points) {
			result += point.toString()+9", ";
		}
		return result;
	}

}
