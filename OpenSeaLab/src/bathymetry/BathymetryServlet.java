package bathymetry;

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

import exceptions.BizzException;
import exceptions.FatalException;
import main.Util;

public class BathymetryServlet extends DefaultServlet {
	private static final Logger LOGGER = Logger.getLogger(BathymetryServlet.class.getName());
	private static final long serialVersionUID = 1L;

	private final UCCBathymetry uccBathymetry;

	public BathymetryServlet(UCCBathymetry uccBathymetry) {
		super();
		this.uccBathymetry = uccBathymetry;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		switch (action) {
		case "getStats":
			getStats(req, resp);
			break;
		default:
			sendError(resp, "Unknown action", HttpServletResponse.SC_NOT_FOUND);
			break;
		}
	}

	private void getStats(HttpServletRequest req, HttpServletResponse resp) {
		try {
			responseJSON(uccBathymetry.getStats(Util.getBBox(req)), resp);
		} catch (BizzException b) {
			LOGGER.log(Level.FINE, b.getMessage());
			sendError(resp, b.getMessage(), HttpServletResponse.SC_BAD_REQUEST);
			return;
		} catch (FatalException f) {
			LOGGER.log(Level.INFO, f.getMessage(), f);
			return;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", e);
			sendError(resp, "Something happened, we can't respond to your request.",
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}
	}

	private void sendError(HttpServletResponse resp, String msg, int code) {
		try {
			resp.sendError(code, msg);
		} catch (IOException exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}

	private static void responseJSON(File f, HttpServletResponse resp) {
		try (ServletOutputStream sos = resp.getOutputStream()) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			LOGGER.fine("Trying to get " + f.toPath());
			Files.copy(f.toPath(), sos);
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Unexpected behavior", exc);
		}
	}
}
