package vectorLayers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

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
	public VectorLayersDAO(String layerName, String url, String defaultType) {
		this.url = url;
		this.defaultType = defaultType;
		this.layerName = layerName;
	}

	public VectorLayersDAO(String layerName, AppContext context) {
		this(layerName, context.getProperty(layerName), context.getProperty(layerName + "-default-type"));
	}

	/**
	 * Fetches data from remote server and returns a {@link FeatureCollection}.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            the layer type
	 * @return {@link FeatureCollection}
	 */
	public FeatureCollection getFeatures(Rectangle bbox, String type) {
		type = type == null ? defaultType : type;
		try {
			if (url.contains("outputFormat=application/json") || url.endsWith("json")) {
				return fetchJSON(bbox, type);
			} else {
				return fetchXML(bbox, type);
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new FatalException(e);
		}
	}

	/**
	 * Gets the data from the WFS service and process it as XML.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            layer typeName
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
		FeatureCollection fc = userhandler.getFeatures();
		fc.deduplicate();
		return fc;
	}

	/**
	 * Gets data from WFS service and process it as json.
	 * 
	 * @param bbox
	 *            bounding box
	 * @param type
	 *            layer typeName
	 * @return {@link FeatureCollection}
	 * @throws IOException
	 */
	private FeatureCollection fetchJSON(Rectangle bbox, String type) {
		String URL = getFormattedURL(bbox, type);
		try (InputStream in = Util.fetchFrom(URL)) {
			ByteArrayOutputStream result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) != -1) {
				result.write(buffer, 0, length);
			}
			// StandardCharsets.UTF_8.name() > JDK 7
			return new FeatureCollectionBuilder(result.toString("UTF-8")).create();
		} catch (Exception e) {
			throw new FatalException(e);
		}

	}

	private String getFormattedURL(Rectangle bbox, String type) {
		if (layerName.equals("geology")) {
			return url.replace("{type}", type);
		}
		String bx = bbox.getMinLon() + "," + bbox.getMinLat() + "," + bbox.getMaxLon() + "," + bbox.getMaxLat();
		String URL = url.replace("{bbox}", bx).replace("{type}", type);
		return URL;

	}

}
