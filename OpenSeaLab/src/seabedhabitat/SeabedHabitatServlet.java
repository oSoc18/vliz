package seabedhabitat;

import java.io.IOException;

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
		switch (action) {
			case "geoJSON":
				String geoJSON = seabedHabitatUCC.getData("-1.822721", "44.468489", "-4.448453", "45.453393");
				responseJSON(geoJSON, arg1);
				break;
		}
	}

	private void responseJSON(String s, HttpServletResponse resp) {
		try {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			ServletOutputStream sos = resp.getOutputStream();
			sos.print(s);
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

}
