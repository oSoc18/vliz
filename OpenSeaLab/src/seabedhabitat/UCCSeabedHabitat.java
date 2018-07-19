package seabedhabitat;

import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Rectangle;

public class UCCSeabedHabitat {
	private SeabedHabitatDAO seabedHabitatDAO;

	public UCCSeabedHabitat(SeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		return seabedHabitatDAO.getFeatures(bbox, type);
	}

}
