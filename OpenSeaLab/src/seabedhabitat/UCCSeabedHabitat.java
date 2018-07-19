package seabedhabitat;

import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Rectangle;

public class UCCSeabedHabitat {
	private SeabedHabitatDAO seabedHabitatDAO;

	/**
	 * Constructs a use case controller that controls users requests.
	 * 
	 * @param seabedHabitatDAO
	 *            data access object
	 */
	public UCCSeabedHabitat(SeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	/**
	 * Retrieves a geojson file with features inside the bbox.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            layer type
	 * @return geojson file
	 */
	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		return seabedHabitatDAO.getFeatures(bbox, type);
	}

	/**
	 * Retrieves statistics (area percentages) of seabed habitat inside the bbox.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            layer type
	 * @return json file
	 */
	public String getStats(Rectangle bbox, String type) {
		return seabedHabitatDAO.getStats(bbox, type);
	}
}
