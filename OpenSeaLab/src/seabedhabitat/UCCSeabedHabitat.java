package seabedhabitat;

import feature.FeatureCollection;
import feature.Rectangle;

public class UCCSeabedHabitat {
	private SeabedHabitatDAO seabedHabitatDAO;

	public UCCSeabedHabitat(SeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		return seabedHabitatDAO.getFeatures(bbox, type);
	}

}
