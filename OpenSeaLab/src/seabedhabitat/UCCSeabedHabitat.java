package seabedhabitat;

import java.io.File;

import seabedhabitat.feature.Rectangle;

public class UCCSeabedHabitat {
	private SeabedHabitatDAO seabedHabitatDAO;

	public UCCSeabedHabitat(SeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	public File getGeoJSON(Rectangle bbox, String type) {
		return seabedHabitatDAO.getGeoJson(bbox, type);
	}

	public File getStats(Rectangle bbox, String type) {
		return seabedHabitatDAO.getStats(bbox, type);
	}
}
