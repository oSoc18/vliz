package vectorLayers;

import feature.FeatureCollection;
import feature.Rectangle;

public class UCCVectorLayers {
	private VectorLayersDAO seabedHabitatDAO;

	public UCCVectorLayers(VectorLayersDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		return seabedHabitatDAO.getFeatures(bbox, type);
	}

}
