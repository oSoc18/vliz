package seabedhabitat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.servlet.DefaultServlet;

public class SeabedHabitatServlet extends DefaultServlet {
	private IUCCSeabedHabitat seabedHabitatUCC;

	public SeabedHabitatServlet(IUCCSeabedHabitat seabedHabitatUCC) {
		this.seabedHabitatUCC = seabedHabitatUCC;
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
		if (action != null) {
			switch (action) {
			case "getGeoJSON":
				double minLat = Double.parseDouble(arg0.getParameter("minLat"));
				double minLong = Double.parseDouble(arg0.getParameter("minLong"));
				double maxLat = Double.parseDouble(arg0.getParameter("maxLat"));
				double maxLong = Double.parseDouble(arg0.getParameter("maxLong"));
				File geoJSON = getGeoJSON(minLat, minLong, maxLat, maxLong);
				responseJSON(geoJSON, arg1);
				break;
			}
		}
	}

	private File getGeoJSON(double minLat, double minLong, double maxLat, double maxLong) {
		if (minLat > maxLat) {
			double tempLat = minLat;
			double tempLong = minLong;

			minLat = maxLat;
			maxLat = tempLat;

			minLong = maxLong;
			maxLong = tempLong;
		}

		minLat = Math.floor(minLat);
		minLong = Math.floor(minLong);
		maxLat = Math.ceil(maxLat);
		maxLong = Math.ceil(maxLong);

		return seabedHabitatUCC.getData(String.valueOf(minLong), String.valueOf(minLat), String.valueOf(maxLong),
				String.valueOf(maxLat));
	}

	private static void responseJSON(File f, HttpServletResponse resp) {
		try (ServletOutputStream sos = resp.getOutputStream()) {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			Files.copy(f.toPath(), sos);
			System.out.println("Cache hit on "+f.toPath());
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
