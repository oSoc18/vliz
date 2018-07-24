package vectorLayers;

import feature.FeatureCollection;
import feature.Rectangle;

public class UCCVectorLayers {
	private VectorLayersDAO vectorLayersDAO;

	/**
	 * Use controller for vector layers. Give access to data layer and controls user
	 * inputs.
	 * 
	 * @param vectorLayersDAO
	 */
	public UCCVectorLayers(VectorLayersDAO vectorLayersDAO) {
		this.vectorLayersDAO = vectorLayersDAO;
	}

	/**
	 * Returns a feature collection. This method makes a call to
	 * {@link VectorLayersDAO} method getFeatures.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            typeName parameter of layers.
	 * @return {@link FeatureCollection}
	 */
	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		return vectorLayersDAO.getFeatures(bbox, type);
	}

}
