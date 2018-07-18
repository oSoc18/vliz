package seabedhabitat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.owlike.genson.Genson;

import exceptions.FatalException;
import main.Util;
import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Rectangle;

public class SeabedHabitatDAO {
	private static final Logger LOGGER = Logger.getLogger(SeabedHabitatDAO.class.getName());
	private final String url;
	private final String cacheDir;
	private final String dataPattern;
	private final String statPattern;
	private final String defaultType;

	/**
	 * Constructs a data object access that manages everything linked to pure data.
	 * 
	 * @param url
	 *            webservice url
	 * @param defaultType
	 *            type name of the seabed habitat
	 * @param cacheDir
	 *            directory of cached files
	 * @param dataPattern
	 *            geojson files pattern
	 * @param statPattern
	 *            stat files pattern
	 */
	public SeabedHabitatDAO(String url, String defaultType, String cacheDir, String dataPattern, String statPattern) {
		this.url = url;
		this.cacheDir = cacheDir;
		this.dataPattern = dataPattern;
		this.statPattern = statPattern;
		this.defaultType = defaultType;
	}

	/**
	 * Fetchs, saves and returns a geojson file.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            seabed habitat type
	 * @return geojson file
	 */
	public File getGeoJson(Rectangle bbox, String type) {
		if (type == null || type.isEmpty())
			type = defaultType;
		String id = type + "-" + bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-"
				+ bbox.getMaxLon();
		String pathname = cacheDir + "/" + dataPattern.replace("{id}", id);
		Path p = FileSystems.getDefault().getPath(pathname);
		if (!Files.exists(p)) {
			Path statsPath = FileSystems.getDefault()
					.getPath(cacheDir + "/" + statPattern.replace("{id}", id));
			process(bbox.extendRectangle(), statsPath, p, type);
		}
		return new File(pathname);
	}

	/**
	 * Fetchs, saves and returns statistics in json format.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            seabed habitat type
	 * @return a file of statistics
	 */
	public File getStats(Rectangle bbox, String type) {
		if (type == null || type.isEmpty())
			type = defaultType;
		String id = type + "-" + bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-"
				+ bbox.getMaxLon();
		String pathname = cacheDir + "/" + statPattern.replace("{id}", id);
		Path statsPath = FileSystems.getDefault().getPath(pathname);
		if (!Files.exists(statsPath)) {
			Path geojsonPath = FileSystems.getDefault().getPath(cacheDir + "/" + dataPattern.replace("{id}", id));
			process(bbox, statsPath, geojsonPath, type);
		}
		return new File(pathname);

	}

	private void process(Rectangle bbox, Path statsPath, Path geojsonPath, String type) {
		try {
			FeatureCollection fc = fetch(bbox, type);
			
			Map<String, Double> percentages = 
					fc.clippedWith(bbox).calculatePercentages();
			String stats = new Genson().serialize(percentages);
					
			store(fc.toGeoJSON(), geojsonPath);
			store(stats, statsPath);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new FatalException("For some reasons your request cannot be processed", e);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Gets the data from the WMS
	 * @param bbox
	 * @param type
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private FeatureCollection fetch(Rectangle bbox, String type)
			throws SAXException, IOException, ParserConfigurationException {
		String bx = bbox.getMinLon() + "," + bbox.getMinLat() + "," + bbox.getMaxLon() + "," + bbox.getMaxLat();
		LOGGER.log(Level.FINE, "Querying WMS server");
		String URL = url.replace("{bbox}", bx).replace("{type}", type);

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SAXHandler userhandler = new SAXHandler();
		saxParser.parse(Util.fetchFrom(URL), userhandler);
		LOGGER.log(Level.FINE, "Got result for bbox: " + bx);
		return userhandler.getFeatures();
	}

	/**
	 * Caches the given data
	 * @param data
	 * @param p
	 * @throws IOException
	 */
	private void store(String data, Path p) throws IOException {
		Path cache = FileSystems.getDefault().getPath(cacheDir);
		if (!Files.exists(cache) || !Files.isDirectory(cache)) {
			Files.createDirectory(cache);
			LOGGER.log(Level.FINE, "Caching directory created ");
		}
		try (Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
			writer.write(data);
			LOGGER.log(Level.FINE, "Cache file " + p + " created");
		} catch (IOException io) {
			throw new FatalException("The file cannot be saved", io);
		}
	}

}
