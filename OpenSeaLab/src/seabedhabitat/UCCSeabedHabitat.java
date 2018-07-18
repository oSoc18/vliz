package seabedhabitat;

import java.io.File;

import seabedhabitat.feature.Rectangle;

public class UCCSeabedHabitat {
	private SeabedHabitatDAO seabedHabitatDAO;

	public UCCSeabedHabitat(SeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	public File getGeoJSON(Rectangle bbox) {
		return seabedHabitatDAO.getGeoJson(bbox);
	}

	public File getStats(Rectangle bbox) {
		return seabedHabitatDAO.getStats(bbox);
	}
}
