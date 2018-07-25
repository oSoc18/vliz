package feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiLineString extends Geometry {
	private static final long serialVersionUID = 1L;
	private final List<LineString> lines;

	public MultiLineString(List<LineString> lines) {
		super("MultLineString");
		this.lines = lines;
	}

	public MultiLineString(LineString... lines) {
		this(Arrays.asList(lines));
	}
	
	public MultiLineString() {
		this(new ArrayList<>());
	}

	public void addLineString(LineString line) {
		lines.add(line);
	}

	@Override
	public String getCoordinates() {
		StringBuilder sb = new StringBuilder();
		for (LineString line : lines) {
			sb.append("[");
			sb.append(line.getCoordinates() + ", ");
			sb.append("]");
		}
		return sb.delete(sb.length() - 2, sb.length()).toString();
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
