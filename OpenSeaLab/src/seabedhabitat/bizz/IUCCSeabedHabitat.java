package seabedhabitat.bizz;

import java.io.File;

import exceptions.FatalException;

public interface IUCCSeabedHabitat {
	/**
	 * Makes a call to the Seabed Habitat webservice (wfs) and sends back a geojson
	 * file with the result.
	 * 
	 * @param minLong
	 *            lower corner longitude
	 * @param minLat
	 *            lower corner latitude
	 * @param maxLong
	 *            upper corner longitude
	 * @param maxLat
	 *            upper corner latitude
	 * @return geojson file
	 */
	File getData(BBoxDTO bbox);
}
