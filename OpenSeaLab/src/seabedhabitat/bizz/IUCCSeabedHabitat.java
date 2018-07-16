package seabedhabitat.bizz;

import java.io.File;


public interface IUCCSeabedHabitat {
	/**
	 * Makes a call to the Seabed Habitat webservice (wfs) and sends back a geojson
	 * file with the result.
	 * 
	 * @param bbox
	 *            coordinates of selected area
	 * @return geojson file
	 */
	File getGeoJSON(BBoxDTO bbox);

	/**
	 * Makes statitics from seabed habitat data.
	 * 
	 * @param bbox
	 *            coordinates of selected area
	 * @return json file
	 */
	File getStats(BBoxDTO bbox);
}
