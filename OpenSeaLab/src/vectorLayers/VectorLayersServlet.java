package vectorLayers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import com.owlike.genson.Genson;

import exceptions.BizzException;
import exceptions.FatalException;
import feature.FeatureCollection;
import feature.Rectangle;
import main.LayerProvider;
import main.PiecedCachingManager;
import main.Util;

public class VectorLayersServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(VectorLayersServlet.class.getName());
	private final LayerProvider cm;
	private final String defaultType;
	private final String dividingProperty;

	/**
	 * 
	 * @param cm
	 *            {@link PiecedCachingManager}
	 * @param defaultType
	 *            the default typeName of a layer
	 */
	public VectorLayersServlet(LayerProvider cm, String defaultType, String dividingProperty) {
		this.cm = cm;
		this.defaultType = defaultType;
		this.dividingProperty = dividingProperty;

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String action = req.getParameter("action");
			if (action != null) {
				switch (action) {
				case "getGeoJSON":
					getGeoJSON(req, resp);
					break;
				case "getStats":
					getStats(req, resp);
					break;
				default:
					sendError(resp, "Unknown action", HttpServletResponse.SC_NOT_FOUND);
					break;
				}
			} else {
				sendError(resp, "Missing parameter : action", HttpServletResponse.SC_BAD_REQUEST);
			}

		} catch (BizzException b) {
			LOGGER.log(Level.FINE, b.getMessage());
			sendError(resp, b.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
		} catch (FatalException f) {
			LOGGER.log(Level.INFO, f.getMessage(), f);
			sendError(resp, "Something went wrong: " + f.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", e);
			sendError(resp, "Something happened, we can't respond to your request.",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Process the getGeoJSON request.
	 * 
	 * @param req
	 * @param resp
	 */
	private void getGeoJSON(HttpServletRequest req, HttpServletResponse resp) {
		Rectangle bbox = Util.getBBox(req);
		FeatureCollection fc = cm.retrieve(bbox, getType(req), getDividingProperty(req),  getCacheOnly(req), getGeomType(req));
		responseFromString(fc.toGeoJSON(), resp);
	}

	/**
	 * Process the getStats request.
	 * 
	 * @param req
	 * @param resp
	 */
	private void getStats(HttpServletRequest req, HttpServletResponse resp) {
		Rectangle bbox = Util.getBBox(req);
		HashMap<String, Double> fc = cm.retrieveStats(bbox, getType(req), getDividingProperty(req), getGeomType(req)).calculatePercentages();
		fc.remove(null);
		responseFromString(new Genson().serialize(fc), resp);
	}

	private String getType(HttpServletRequest req) {
		return req.getParameter("type") == null ? defaultType : req.getParameter("type");
	}
	
	private String getDividingProperty(HttpServletRequest req) {
		return req.getParameter("dividingProperty") == null ? dividingProperty : req.getParameter("dividingProperty");
	}
	
	private String getGeomType(HttpServletRequest req) {
		return req.getParameter("geomType") == null ? "polygon" : req.getParameter("geomType");
	}
	
	private static boolean getCacheOnly(HttpServletRequest req) {
		return req.getParameter("cacheOnly") == null ? false : Boolean.parseBoolean(req.getParameter("cacheOnly"));
	}

	/**
	 * Sends an error.
	 * 
	 * @param resp
	 *            {@link HttpServletResponse}
	 * @param msg
	 * @param code
	 *            error code.
	 */
	@SuppressWarnings("static-method")
	private void sendError(HttpServletResponse resp, String msg, int code) {
		try {
			resp.sendError(code, msg);
		} catch (IOException exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

	/**
	 * Sends response to the calling process.
	 * 
	 * @param data
	 * @param resp
	 *            {@link HttpServletResponse}
	 */
	private static void responseFromString(String data, HttpServletResponse resp) {
		try (BufferedWriter sos = new BufferedWriter(new OutputStreamWriter(resp.getOutputStream()))) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			sos.write(data);
			sos.flush();
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

}
