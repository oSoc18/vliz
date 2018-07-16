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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import exceptions.FatalException;
import seabedhabitat.bizz.BBoxDTO;

public class SeabedHabitatDAO implements ISeabedHabitatDAO {
	private final String url;
	private final String cacheDir;
	private final String pattern;
	
	public SeabedHabitatDAO(String url,String cacheDir, String pattern) {
		this.url = url;
		this.cacheDir = cacheDir;
		this.pattern = pattern;
	}

	@Override
	public File getGeoJson(BBoxDTO bbox) {
		try {
			String pathname = cacheDir+"/"+pattern.replace("{id}", bbox.getMinLat() + "-" + bbox.getMinLong() + "-" + bbox.getMaxLat() + "-" + bbox.getMaxLong());
			Path p = FileSystems.getDefault().getPath(pathname);
			Path cache = FileSystems.getDefault().getPath(cacheDir);
			if(!Files.exists(cache) || !Files.isDirectory(cache)) {
				Files.createDirectory(cache);
				System.out.println("Cacing directory created");
			}
			if (!Files.exists(p)) {
				String bx = bbox.getMinLong()+ "," + bbox.getMinLat() + "," + bbox.getMaxLong() + "," + bbox.getMaxLat();
				System.out.println("Querying WMS server for "+p);
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
				System.out.println("Got result for "+p);
				try(Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)){
					writer.write(userhandler.getFeatures().toGeoJSON());
					System.out.println("Cache file "+p+" created");
				} catch(IOException io) {
					throw new FatalException("The file cannot be saved", io);
				}
			}
			return new File(pathname);
		} catch(IOException | SAXException | ParserConfigurationException e) {
			throw new FatalException("For some reasons your request cannot be processed", e);
		} catch (Exception e) {
			throw e;
		}
	}
}
