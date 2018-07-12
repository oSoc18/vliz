package seabedhabitat;

import java.io.File;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class UCCSeabedHabitat implements IUCCSeabedHabitat {
	private static final String BASEURL = "http://213.122.160.75/scripts/mapserv.exe?map=D:/Websites/"
			+ "MeshAtlantic/map/MESHAtlantic.map&service=wfs&version=1.1.0&request=GetFeature&typeName=EUSM2016_simplified200&srsName="
			+ "EPSG:4326&bbox=";

	public File getData(String minLong, String minLat, String maxLong, String maxLat) {
		try {
			String pathname = "cache/data-seabed-" + minLat + "-" + minLong + "-" + maxLat + "-" + maxLong + ".geojson";
			Path p = FileSystems.getDefault().getPath(pathname);
			Path cache = FileSystems.getDefault().getPath("cache");
			if(!Files.exists(cache) || !Files.isDirectory(cache)) {
				Files.createDirectory(cache);
				System.out.println("Cacing directory created");
			}
			if (!Files.exists(p)) {
				String bbox = minLong + "," + minLat + "," + maxLong + "," + maxLat;
				System.out.println("Querying WMS server for "+p);
				HttpURLConnection connection = (HttpURLConnection) new URL(BASEURL + bbox).openConnection();
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
				} 
			}
			return new File(pathname);
			// return userhandler.getFeatures().toGeoJSON();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
