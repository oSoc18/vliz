package bathymetry;

import java.io.File;

import feature.Rectangle;

public class UCCBathymetry {
	private BathymetryDAO bathymetryDAO;
	
	public UCCBathymetry(BathymetryDAO bathymetryDAO) {
		this.bathymetryDAO = bathymetryDAO;
	}
	
	public File getStats(Rectangle bbox) {
		return bathymetryDAO.getStats(bbox);
	}
	
}
