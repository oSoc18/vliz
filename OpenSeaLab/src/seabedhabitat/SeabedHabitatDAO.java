package seabedhabitat;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.owlike.genson.Genson;

import exceptions.FatalException;
import seabedhabitat.feature.Feature;
import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Geometry;
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
	 * @param url
	 * @param defaultType
	 * @param cacheDir
	 * @param dataPattern
	 * @param statPattern
	 */
	public SeabedHabitatDAO(String url, String defaultType, String cacheDir, String dataPattern, String statPattern) {
		this.url = url;
		this.cacheDir = cacheDir;
		this.dataPattern = dataPattern;
		this.statPattern = statPattern;
		this.defaultType = defaultType;
	}

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
			process(bbox, statsPath, p, type);
		}
		return new File(pathname);
	}

	public File getStats(Rectangle bbox, String type) {
		if (type == null || type.isEmpty())
			type = defaultType;
		String id = type + "-" + bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-"
				+ bbox.getMaxLon();
		String pathname = cacheDir + "/" + statPattern.replace("{id}", id);
		Path statsPath = FileSystems.getDefault().getPath(pathname);
		if (!Files.exists(statsPath)) {
			Path geojsonPath = FileSystems.getDefault()
					.getPath(cacheDir + "/" + dataPattern.replace("{id}", id));
			process(bbox, statsPath, geojsonPath, type);
		}
		return new File(pathname);
	}

	private void process(Rectangle bbox, Path statsPath, Path geojsonPath, String type) {
		try {
			FeatureCollection fc = fetch(bbox, type);
			String stats = calculateStatistics(fc, bbox);
			store(fc.toGeoJSON(), geojsonPath);
			store(stats, statsPath);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new FatalException("For some reasons your request cannot be processed", e);
		} catch (Exception e) {
			throw e;
		}
	}

	private FeatureCollection fetch(Rectangle bbox, String type)
			throws SAXException, IOException, ParserConfigurationException {
		String bx = bbox.getMinLon() + "," + bbox.getMinLat() + "," + bbox.getMaxLon() + "," + bbox.getMaxLat();
		LOGGER.log(Level.FINE, "Querying WMS server");
		HttpURLConnection connection = (HttpURLConnection) new URL(url.replace("{bbox}", bx).replace("{type}", type))
				.openConnection();
		connection.setReadTimeout(20000);
		connection.setConnectTimeout(20000);
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.connect();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SAXHandler userhandler = new SAXHandler();
		saxParser.parse(connection.getInputStream(), userhandler);
		LOGGER.log(Level.FINE, "Got result for bbox: " + bx);
		return userhandler.getFeatures();
	}

	@SuppressWarnings("static-method")
	private String calculateStatistics(FeatureCollection fc, Rectangle r) {
		List<Feature> features = fc.getFeatures();

		double sum = 0;
		Map<String, Double> sums = new HashMap<>();
		Map<String, Double> percentages = new HashMap<>();

		for (Feature f : features) {
			Geometry geo = f.getGeometry().clippedWith(r);
			Map<String, Object> m = f.getProperties();
			String name = (String) m.get("AllcombD");
			Double s = sums.get(name);
			if (s == null) {
				sums.put(name, geo.surfaceArea());
			} else {
				sums.put(name, sums.get(name) + geo.surfaceArea());
			}
			sum += geo.surfaceArea();
		}

		for (Map.Entry<String, Double> entries : sums.entrySet()) {
			double d = entries.getValue() / sum * 100;
			percentages.put(entries.getKey(), d);
		}
		return new Genson().serialize(percentages);
	}

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