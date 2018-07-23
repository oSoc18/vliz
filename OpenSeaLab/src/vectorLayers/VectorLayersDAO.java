package vectorLayers;

import java.io.IOException;
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
import feature.Rectangle;
import main.AppContext;
import main.Util;

public class VectorLayersDAO {
	private static final Logger LOGGER = Logger.getLogger(VectorLayersDAO.class.getName());
	private final String url;

	/**
	 * Constructs a data object access to retrieve data from the remote server.
	 * 
	 * @param url
	 *            webservice url
	 * @param defaultType
	 *            type name of the seabed habitat
	 */
	public VectorLayersDAO(String url) {
		this.url = url;
	}
	
	public VectorLayersDAO(String layerName, AppContext context) {
		this(context.getProperty(layerName));
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
		try {
			fc = fetchXML(bbox, type == null ? defaultType : type);
			fc = fc.clippedWith(bbox);
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
		try {
			FeatureCollection fc = fetchXML(bbox, type == null ? defaultType : type);
			Map<String, Double> stats = fc.clippedWith(bbox).calculateTotals().calculatePercentages();
			return new Genson().serialize(stats);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new FatalException(e);
		}
	}

	/**
	 * Gets the data from the WMS
	 * 
	 * @param bbox
	 * @param type
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private FeatureCollection fetchXML(Rectangle bbox, String type)
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

}
