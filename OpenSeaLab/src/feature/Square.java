package feature;

public class Square extends Rectangle{
	
	public Square(double lat, double lon) {
		super(lat, lon, lat+1, lon+1);
	}

}
