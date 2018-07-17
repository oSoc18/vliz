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
	private final String url;
	private final String cacheDir;
	private final String dataPattern;
	private final String statPattern;

	public SeabedHabitatDAO(String url, String cacheDir, String dataPattern, String statPattern) {
		this.url = url;
		this.cacheDir = cacheDir;
		this.dataPattern = dataPattern;
		this.statPattern = statPattern;
	}

	public File getGeoJson(Rectangle bbox) {
			String id = bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-" + bbox.getMaxLon();
			String pathname = cacheDir + "/" + dataPattern.replace("{id}", id);
			Path p = FileSystems.getDefault().getPath(pathname);
			if (!Files.exists(p)) {
				Path statsPath = FileSystems.getDefault().getPath(cacheDir + "/" + statPattern.replace("{id}", id));
				process(bbox, statsPath, p);
			}
			return new File(pathname);
	}

	public File getStats(Rectangle bbox) {
		String id = bbox.getMinLat() + "-" + bbox.getMinLon() + "-" + bbox.getMaxLat() + "-" + bbox.getMaxLon();
		String pathname = cacheDir + "/" + statPattern.replace("{id}", id);
		Path statsPath = FileSystems.getDefault().getPath(pathname);
		if (!Files.exists(statsPath)) {
			Path geojsonPath = FileSystems.getDefault().getPath(cacheDir + "/" + dataPattern.replace("{id}", id));
			process(bbox, statsPath, geojsonPath);
		}
		return new File(pathname);
	}

	private void process(Rectangle bbox, Path statsPath, Path geojsonPath) {
		try {
			FeatureCollection fc = fetch(bbox);
			fc = fc.clippedWith(bbox);
			String stats = calculateStatistics(fc, bbox);
			store(fc.toGeoJSON(), geojsonPath);
			store(stats, statsPath);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new FatalException("For some reasons your request cannot be processed", e);
		} catch (Exception e) {
			throw e;
		}
	}

	private FeatureCollection fetch(Rectangle bbox) throws SAXException, IOException, ParserConfigurationException {
		String bx = bbox.getMinLon() + "," + bbox.getMinLat() + "," + bbox.getMaxLon() + "," + bbox.getMaxLat();
		System.out.println("Querying WMS server");
		HttpURLConnection connection = (HttpURLConnection) new URL(url.replace("{bbox}", bx)).openConnection();
		connection.setReadTimeout(20000);
		connection.setConnectTimeout(20000);
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.connect();

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SAXHandler userhandler = new SAXHandler();
		saxParser.parse(connection.getInputStream(), userhandler);
		System.out.println("Got result");
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
			System.out.println("Cacing directory created");
		}
		try (Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
			writer.write(data);
			System.out.println("Cache file " + p + " created");
		} catch (IOException io) {
			throw new FatalException("The file cannot be saved", io);
		}
	}

}
