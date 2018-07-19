package seabedhabitat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import exceptions.BizzException;
import exceptions.FatalException;
import main.CachingManager;
import main.Util;
import seabedhabitat.feature.FeatureCollection;
import seabedhabitat.feature.Rectangle;

public class SeabedHabitatServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SeabedHabitatServlet.class.getName());
	private final UCCSeabedHabitat seabedHabitatUCC;

	private final CachingManager cm;
	private final String defaultType;

	public SeabedHabitatServlet(UCCSeabedHabitat seabedHabitatUCC, CachingManager cm, String defaultType) {
		this.seabedHabitatUCC = seabedHabitatUCC;
		this.cm = cm;
		this.defaultType = defaultType;

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
		}
	}

	private void getGeoJSON(HttpServletRequest req, HttpServletResponse resp) {
		try {
			Rectangle bbox = Util.getBBox(req);

			/*
			 * By extending the bbox, rectangles will have a bigger chance of being the same
			 * This way, caching works more reliably
			 */
			Rectangle extended = bbox.extendRectangle();

			String type = getType(req);
			if (cm.isInCache(extended, type)) {
				FeatureCollection fc = cm.restore(extended, type);
				fc = fc.clippedWith(bbox);
				responseFromString(fc.toGeoJSON(), resp);
			} else {
				FeatureCollection fc = seabedHabitatUCC.getFeatures(extended, type);
				cm.store((Serializable) fc, extended, type);
				responseFromString(fc.clippedWith(bbox).toGeoJSON(), resp);
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

	private String getType(HttpServletRequest req) {
		return req.getParameter("type") == null ? defaultType : req.getParameter("type");
	}

	private void getStats(HttpServletRequest req, HttpServletResponse resp) {
		try {

			Rectangle bbox = Util.getBBox(req);
			String type = getType(req);
			String stats = seabedHabitatUCC.getStats(bbox, type);
			responseFromString(stats, resp);

		} catch (BizzException b) {
			LOGGER.log(Level.FINE, b.getMessage());
			sendError(resp, b.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
		} catch (FatalException f) {
			LOGGER.log(Level.INFO, f.getMessage(), f);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", e);
			sendError(resp, "Something happened, we can't respond to your request.",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@SuppressWarnings("static-method")
	private void sendError(HttpServletResponse resp, String msg, int code) {
		try {
			resp.sendError(code, msg);
		} catch (IOException exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

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
