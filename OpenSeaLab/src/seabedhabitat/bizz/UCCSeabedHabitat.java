package seabedhabitat.bizz;

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
import seabedhabitat.dal.ISeabedHabitatDAO;
import seabedhabitat.dal.SAXHandler;

public class UCCSeabedHabitat implements IUCCSeabedHabitat {
	private ISeabedHabitatDAO seabedHabitatDAO;
	
	public UCCSeabedHabitat(ISeabedHabitatDAO seabedHabitatDAO) {
		this.seabedHabitatDAO = seabedHabitatDAO;
	}
	
	/**
	 * Constructs a use case controller for seabed habitat layer.
	 * @param url the seabed habitat wfs call url
	 * @param cacheDir directory in which cached file will be saved
	 * @param cacheFormat the format of a cached file
	 */

	public File getData(BBoxDTO bbox) {
		((BBoxBiz)bbox).validateBBox();
		return seabedHabitatDAO.getGeoJson(bbox);
	}
}
