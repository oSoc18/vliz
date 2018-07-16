package seabedhabitat.bizz;

import java.io.File;
import seabedhabitat.dal.ISeabedHabitatDAO;

public class UCCSeabedHabitat implements IUCCSeabedHabitat {
	private ISeabedHabitatDAO seabedHabitatDAO;

	public UCCSeabedHabitat(ISeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}

	@Override
	public File getGeoJSON(BBoxDTO bbox) {
		((BBoxBiz) bbox).validateBBox();
		return seabedHabitatDAO.getGeoJson(bbox);
	}

	@Override
	public File getStats(BBoxDTO bbox) {
		((BBoxBiz)bbox).validateBBox();
		return seabedHabitatDAO.getStats(bbox);
	}
}
