package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import exceptions.FatalException;
import feature.FeatureCollection;
import feature.FeatureCollectionBuilder;
import feature.Rectangle;
import feature.SurfaceCount;

/**
 * Mainly meant for testing
 */
public class FromJSONFileLayer implements LayerProvider {
	private final FeatureCollection fc;

	public FromJSONFileLayer(String file) throws IOException {

		FeatureCollectionBuilder fcb = new FeatureCollectionBuilder(
				String.join("\n", Files.readAllLines(Paths.get(file))));
		fc = fcb.create();
	}

	@Override
	public FeatureCollection retrieve(Rectangle bbox, String type, String dividingProperty, boolean cacheOnly,
			String geomType) {
		System.out.println("RETRIEVING!");
		return fc;
	}

	@Override
	public SurfaceCount retrieveStats(Rectangle bbox, String type, String dividingProperty, String geomType) {
		throw new FatalException("Not supported");
	}

}
