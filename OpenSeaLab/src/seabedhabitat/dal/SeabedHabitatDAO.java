package seabedhabitat.dal;

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
import seabedhabitat.bizz.BBoxDTO;
import seabedhabitat.feature.Feature;
import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Geometry;
import seabedhabitat.feature.Rectangle;

public class SeabedHabitatDAO implements ISeabedHabitatDAO {
	private final String url;
	private final String cacheDir;
	private final String dataPattern;
	private final String statPattern;
	
	public SeabedHabitatDAO(String url,String cacheDir, String dataPattern, String statPattern) {
		this.url = url;
		this.cacheDir = cacheDir;
		this.dataPattern = dataPattern;
		this.statPattern = statPattern;
	}

	@Override
	public File getGeoJson(BBoxDTO bbox) {
		try {
			String id = bbox.getMinLat() + "-" + bbox.getMinLong() + "-" + bbox.getMaxLat() + "-" + bbox.getMaxLong();
			String pathname = cacheDir + "/" + dataPattern.replace("{id}",
					id);
			Path p = FileSystems.getDefault().getPath(pathname);
			Path cache = FileSystems.getDefault().getPath(cacheDir);
			if (!Files.exists(cache) || !Files.isDirectory(cache)) {
				Files.createDirectory(cache);
				System.out.println("Cacing directory created");
			}
			if (!Files.exists(p)) {
				String bx = bbox.getMinLong() + "," + bbox.getMinLat() + "," + bbox.getMaxLong() + ","
						+ bbox.getMaxLat();
				Path statPath = FileSystems.getDefault().getPath(cacheDir+"/"+statPattern.replace("{id}", id));
				System.out.println("Querying WMS server for " + p);
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
				System.out.println("Got result for " + p);
				try (Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
					writer.write(userhandler.getFeatures().toGeoJSON());
					System.out.println("Cache file " + p + " created");
				} catch (IOException io) {
					throw new FatalException("The file cannot be saved", io);
				}

				FeatureCollection fc = userhandler.getFeatures();
				Rectangle r = new Rectangle(Double.parseDouble(bbox.getMinLat()), Double.parseDouble(bbox.getMinLong()),
						Double.parseDouble(bbox.getMaxLat()), Double.parseDouble(bbox.getMaxLong()));
				List<Feature> features = fc.getFeatures();
				
				double sum = 0;
				Map<String, Integer> clippedSums = new HashMap<>();
				Map<String, Double> clippedPercentages = new HashMap<>();
				
				for (Feature f : features) {
					Geometry geo = f.getGeometry();
					Map<String, Object> m = f.getProperties();
					String name = (String) m.get("AllcombD");
					Integer s = clippedSums.get(name);
					if(s == null) {
						clippedSums.put(name, geo.getSurface());
					} else {
						clippedSums.put(name, clippedSums.get(name)+geo.getSurface());
					}
					
					sum+=geo.getSurface();
				}
				
				for(Map.Entry<String, Integer> entries : clippedSums.entrySet()) {
					double d = entries.getValue()/sum*100;
					clippedPercentages.put(entries.getKey(), d);
					//System.out.println(entries.getKey()+" : "+d);
				}
				
				try (Writer writer = Files.newBufferedWriter(statPath, StandardCharsets.UTF_8)) {
					writer.write(new Genson().serialize(clippedPercentages));
					System.out.println("Cache file " + statPath + " created");
				} catch (IOException io) {
					throw new FatalException("The file cannot be saved", io);
				}


			}
			return new File(pathname);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			throw new FatalException("For some reasons your request cannot be processed", e);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public File getStats(BBoxDTO bbox) {
		// TODO Auto-generated method stub
		return null;
	}
}
