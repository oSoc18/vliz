package seabedhabitat.bizz;

import exceptions.BizzException;

public class BBox implements BBoxBiz{
	private String minLong;
	private String minLat;
	private String maxLong;
	private String maxLat;
	
	public String getMinLong() {
		return minLong;
	}

	public void setMinLong(String minLong) {
		this.minLong = minLong;
	}

	public String getMinLat() {
		return minLat;
	}

	public void setMinLat(String minLat) {
		this.minLat = minLat;
	}

	public String getMaxLong() {
		return maxLong;
	}

	public void setMaxLong(String maxLong) {
		this.maxLong = maxLong;
	}

	public String getMaxLat() {
		return maxLat;
	}

	public void setMaxLat(String maxLat) {
		this.maxLat = maxLat;
	}
	
	public void validateBBox() {
		if(minLat == null || minLat.isEmpty()
				|| minLong == null || minLong.isEmpty()
				|| maxLong == null || minLong.isEmpty()
				|| maxLat == null || maxLat.isEmpty()) {
			throw new BizzException("Missing argument(s). Please check your coordinates.");
		}
		
		double minLat = 0; 
		double minLong = 0;
		double maxLat = 0; 
		double maxLong = 0;
		
		try {
			minLat = Double.parseDouble(this.minLat);
			minLong = Double.parseDouble(this.minLong);
			maxLat = Double.parseDouble(this.maxLat);
			maxLong = Double.parseDouble(this.maxLong);
			
			if (minLat > maxLat) {
				double tempLat = minLat;
				double tempLong = minLong;

				minLat = maxLat;
				maxLat = tempLat;

				minLong = maxLong;
				maxLong = tempLong;
			}

			minLat = Math.floor(minLat);
			minLong = Math.floor(minLong);
			maxLat = Math.ceil(maxLat);
			maxLong = Math.ceil(maxLong);
			
			this.minLat = String.valueOf(minLat);
			this.minLong = String.valueOf(minLong);
			this.maxLat = String.valueOf(maxLat);
			this.maxLong = String.valueOf(maxLong);
		} catch(NumberFormatException nfe) {
			throw new BizzException("Please enter valide numbers.");
		}
 		
	}
	
}
