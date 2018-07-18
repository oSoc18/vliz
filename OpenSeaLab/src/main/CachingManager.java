package main;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.FatalException;
import seabedhabitat.feature.Rectangle;

public class CachingManager {
	private static final Logger LOGGER = Logger.getLogger(Util.class.getName());
	private final String cache;
	private final String pattern;

	public CachingManager(String cache, String pattern) throws IOException {
		this.cache = cache;
		this.pattern = pattern;
		Path cacheDir = FileSystems.getDefault().getPath(cache);
		if (!Files.exists(cacheDir) || !Files.isDirectory(cacheDir)) {
			Files.createDirectory(cacheDir);
			LOGGER.log(Level.FINE, "Caching directory created ");
		}
	}

	public void store(String data, Rectangle bbox, String type) {
		Path p = getPath(bbox, type);
		try (Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
			writer.write(data);
			LOGGER.log(Level.FINE, "Cache file " + p + " created");
		} catch (IOException io) {
			throw new FatalException("The file cannot be saved", io);
		}
	}

	public void store(String data, Rectangle bbox) {
		store(data, bbox, null);
	}

	private Path getPath(Rectangle bbox, String type) {
		return FileSystems.getDefault().getPath(cache + "/" + pattern.replace("{id}", getId(bbox, type)));
	}

	private String getId(Rectangle bbox, String type) {
		if (type == null) {
			type = "";
		} else {
			type += "-";
		}
		return type + bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-" + bbox.getMaxLon();
	}

	public boolean isInCache(Path p) {
		return Files.exists(p);
	}

}
