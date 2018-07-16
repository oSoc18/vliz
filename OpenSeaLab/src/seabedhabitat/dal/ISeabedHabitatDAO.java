package seabedhabitat.dal;

import java.io.File;

import seabedhabitat.bizz.BBoxDTO;

public interface ISeabedHabitatDAO {
	File getGeoJson(BBoxDTO bbox);
	
	File getStats(BBoxDTO bbox);
}
