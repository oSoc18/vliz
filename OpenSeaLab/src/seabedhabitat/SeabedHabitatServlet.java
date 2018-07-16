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
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		String action = arg0.getParameter("action");
		System.out.println("hello");
		if (action != null) {
			switch (action) {
			case "getGeoJSON":
				BBoxDTO bbox = factory.newBbox();
				bbox.setMinLat(arg0.getParameter("minLat"));
				bbox.setMinLong(arg0.getParameter("minLong"));
				bbox.setMaxLat(arg0.getParameter("maxLat"));
				bbox.setMaxLong(arg0.getParameter("maxLong"));
				File geoJSON = null;
				try {
					geoJSON = seabedHabitatUCC.getData(bbox);
				} catch (FatalException f) {
					LOGGER.log(Level.INFO, f.getMessage(), f);
					sendError(arg1, f.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Unexpected behavior", e);
					sendError(arg1, "Something happened, we can't respond to your request.",
							HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
				}
				responseJSON(geoJSON, arg1);
				break;
			}
		}
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

			Files.copy(f.toPath(), sos);
			System.out.println("Cache hit on "+f.toPath());
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
