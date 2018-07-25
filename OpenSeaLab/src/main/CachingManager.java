package main;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import exceptions.FatalException;
import feature.Rectangle;

public class CachingManager {
	private static final Logger LOGGER = Logger.getLogger(CachingManager.class.getName());
	private final String cache;
	private final String pattern;
	private final String layerName;

	public CachingManager(String layerName, String cache, String pattern) throws IOException {
		this.cache = cache + "/" + layerName;
		this.pattern = pattern;
		this.layerName = layerName;
	}

	/**
	 * Stores as file the argument data, the name will be a combination of the
	 * bounding box and the type.
	 * 
	 * @param data
	 *            the data to store
	 * @param bbox
	 *            the bounding box
	 * @param type
	 *            the type of the layer
	 */
	public void store(String data, Rectangle bbox, String type) {
		Path p = getPath(bbox, type);
		initCacheDir(p);
		try (Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
			if (!Files.exists(p.getParent())) {
				Files.createDirectories(p.getParent());
			}
			writer.write(data);
			LOGGER.log(Level.FINE, "Cache file " + p + " created");
		} catch (IOException io) {
			throw new FatalException("The file cannot be saved", io);
		}
	}

	/**
	 * Stores as file the argument ser, the name will be a combination of the
	 * bounding box and the type.
	 * 
	 * @param ser
	 *            a serializable object
	 * @param bbox
	 *            the bounding box
	 * @param type
	 *            the type of the layer
	 */
	public void store(Serializable ser, Rectangle bbox, String type) {
		Path p = getPath(bbox, type);
		initCacheDir(p);
		try (ObjectOutputStream fileOut = new ObjectOutputStream(Files.newOutputStream(p))) {
			fileOut.writeObject(ser);
			fileOut.flush();
		} catch (Exception e) {
			throw new FatalException(e);
		}
	}

	/**
	 * Retrieves a stored object.
	 * 
	 * @param bbox
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T restore(Rectangle bbox, String type) {
		try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(getPath(bbox, type)))) {
			return (T) in.readObject();
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "Could not load " + getPath(bbox, type) + ", purging it from cache", e);
			new File(getPath(bbox, type).toString()).delete();
			return null;
		}
	}

	/**
	 * Stores as file the argument data, the name will be a composed of the bounding
	 * box.
	 * 
	 * @param data
	 * @param bbox
	 */
	public void store(String data, Rectangle bbox) {
		store(data, bbox, null);
	}

	/**
	 * Returns a path object. The pathname will be a combination of the previously
	 * specified cache directory and the parameters of this method.
	 * 
	 * @param bbox
	 * @param type
	 * @return a {@link Path}
	 */
	public Path getPath(Rectangle bbox, String type) {
		return FileSystems.getDefault().getPath(cache + "/" + pattern.replace("{id}", getId(bbox, type)));
	}

	private String getId(Rectangle bbox, String type) {
		if (type == null) {
			return layerName + "_" + bbox.getMinLat() + "_" + bbox.getMinLon() + "_" + bbox.getMaxLat() + "_"
					+ bbox.getMaxLon();
		} else {
			return type + "/" + type + "_" + bbox.getMinLat() + "_" + bbox.getMinLon() + "_" + bbox.getMaxLat() + "_"
					+ bbox.getMaxLon();

		}
	}

	/**
	 * Checks whether a file exists with this method parameters in its name
	 * (following the specified pattern).
	 * 
	 * @param bbox
	 * @param type
	 * @return true if file found, false if not
	 */
	public boolean isInCache(Rectangle bbox, String type) {
		return Files.exists(getPath(bbox, type));
	}

	private static void initCacheDir(Path p) {
		if (!Files.exists(p.getParent())) {
			try {
				Files.createDirectories(p.getParent());
			} catch (IOException e) {
				throw new FatalException(e);
			}
		}
	}

}
