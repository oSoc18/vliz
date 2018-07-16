package seabedhabitat;

import seabedhabitat.bizz.BBox;

public class Factory implements IFactory {

	@Override
	public BBox newBbox() {
		return new BBox();
	}

}
