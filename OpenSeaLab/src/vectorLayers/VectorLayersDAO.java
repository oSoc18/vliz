package vectorLayers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.owlike.genson.Genson;

import exceptions.FatalException;
import feature.FeatureCollection;
import feature.FeatureCollectionBuilder;
import feature.Rectangle;
import main.AppContext;
import main.Util;

public class VectorLayersDAO {
	private static final Logger LOGGER = Logger.getLogger(VectorLayersDAO.class.getName());
	private final String url;
	private final String defaultType;
	private final String layerName;

	/**
	 * Constructs a data object access to retrieve data from the remote server.
	 * 
	 * @param url
	 *            webservice url
	 * @param defaultType
	 *            type name of the seabed habitat
	 */
	public VectorLayersDAO(String layerName,String url, String defaultType) {
		this.url = url;
		this.defaultType = defaultType;
		this.layerName = layerName;
	}

	public VectorLayersDAO(String layerName, AppContext context) {
		this(layerName,context.getProperty(layerName), context.getProperty(layerName + "-default-type"));
	}

	/**
	 * Fetches, saves and returns a geojson file.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            seabed habitat type
	 * @return geojson file
	 */
	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		FeatureCollection fc;
		type = type == null ? defaultType : type;
		try {
			if (url.contains("outputFormat=application/json")) {
				fc = fetchJSON(bbox, type);
			} else {
				fc = fetchXML(bbox, type);
				fc = fc.clippedWith(bbox);
			}
			return fc;
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new FatalException(e);
		}
	}

	/**
	 * Fetches, saves and returns statistics in json format.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            seabed habitat type
	 * @return a file of statistics
	 */
	public String getStats(Rectangle bbox, String type) {
		type = type == null ? defaultType : type;
		try {
			FeatureCollection fc;
			if (url.contains("outputFormat=application/json")) {
				fc = fetchJSON(bbox, type);
			} else {
				fc = fetchXML(bbox, type);
			}
			Map<String, Double> stats = fc.clippedWith(bbox).calculateTotals().calculatePercentages();
			return new Genson().serialize(stats);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new FatalException(e);
		}
	}

	/**
	 * Gets the data from the WFS service and process it as XML.
	 * 
	 * @param bbox
	 * @param type
	 * @return {@link FeatureCollection}
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private FeatureCollection fetchXML(Rectangle bbox, String type)
			throws SAXException, IOException, ParserConfigurationException {
		LOGGER.log(Level.FINE, "Querying WMS server");
		String URL = getFormattedURL(bbox, type);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		SAXHandler userhandler = new SAXHandler();
		saxParser.parse(Util.fetchFrom(URL), userhandler);
		LOGGER.log(Level.FINE, "Got result from" + URL);
		return userhandler.getFeatures();
	}

	/**
	 * Gets data from WFS service and process it as json.
	 * 
	 * @param bbox
	 * @param type
	 * @return {@link FeatureCollection}
	 * @throws IOException
	 */
	private FeatureCollection fetchJSON(Rectangle bbox, String type) throws IOException {
		String URL = getFormattedURL(bbox, type);
		InputStream in = Util.fetchFrom(URL);
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		// StandardCharsets.UTF_8.name() > JDK 7
		FeatureCollection fc = new FeatureCollectionBuilder(result.toString("UTF-8")).create();
		return fc;

	}

	private String getFormattedURL(Rectangle bbox, String type) {
		if(layerName.equals("geology")) return url.replace("{type}", type);
		String bx = bbox.getMinLon() + "," + bbox.getMinLat() + "," + bbox.getMaxLon() + "," + bbox.getMaxLat();
		String URL = url.replace("{bbox}", bx).replace("{type}", type);
		return URL;

	}

}