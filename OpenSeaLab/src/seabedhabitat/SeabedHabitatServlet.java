package seabedhabitat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

import exceptions.FatalException;
import seabedhabitat.bizz.BBoxDTO;
import seabedhabitat.bizz.IUCCSeabedHabitat;

public class SeabedHabitatServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SeabedHabitatServlet.class.getName());
	private IUCCSeabedHabitat seabedHabitatUCC;
	private IFactory factory;

	public SeabedHabitatServlet(IUCCSeabedHabitat seabedHabitatUCC, IFactory factory) {
		this.seabedHabitatUCC = seabedHabitatUCC;
		this.factory = factory;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
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
				System.out.println("hi");
				getStats(req, resp);
				break;
			default:
				sendError(resp, "", HttpServletResponse.SC_NOT_FOUND);
				break;
			}
		}
	}

	private BBoxDTO getBBox(HttpServletRequest req) {
		BBoxDTO bbox = factory.newBbox();
		bbox.setMinLat(req.getParameter("minLat"));
		bbox.setMinLong(req.getParameter("minLong"));
		bbox.setMaxLat(req.getParameter("maxLat"));
		bbox.setMaxLong(req.getParameter("maxLong"));
		return bbox;
	}

	private void getGeoJSON(HttpServletRequest req, HttpServletResponse resp) {
		BBoxDTO bbox = getBBox(req);
		File geoJSON = null;
		try {
			geoJSON = seabedHabitatUCC.getGeoJSON(bbox);
		} catch (FatalException f) {
			LOGGER.log(Level.INFO, f.getMessage(), f);
			sendError(resp, f.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", e);
			sendError(resp, "Something happened, we can't respond to your request.",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
		responseJSON(geoJSON, resp);
	}

	private void getStats(HttpServletRequest req, HttpServletResponse resp) {
		seabedHabitatUCC.getStats(getBBox(req));
	}

	private void responseJSON(String s, HttpServletResponse resp) {
		try (ServletOutputStream sos = resp.getOutputStream()) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			sos.print(s);
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

	private static void responseJSON(File f, HttpServletResponse resp) {
		try (ServletOutputStream sos = resp.getOutputStream()) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			// LOGGER.fine("Trying to get " + f.toPath());
			System.out.println("Trying to get " + f.toPath());
			Files.copy(f.toPath(), sos);
			// sos.flush();
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

	private void sendError(HttpServletResponse resp, String msg, int code) {
		try {
			resp.sendError(code, msg);
		} catch (IOException exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

}
