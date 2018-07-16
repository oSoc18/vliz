package seabedhabitat.bizz;

public interface BBoxDTO {
	void setMinLat(String minLat);
	void setMinLong(String minLong);
	
	void setMaxLat(String maxLat);
	void setMaxLong(String maxLong);
	
	String getMinLat();
	String getMinLong();
	
	String getMaxLat();
	String getMaxLong();
}
