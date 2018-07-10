package seabedhabitat;

import java.io.IOException;

import javax.servlet.ServletException;
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
		// TODO Auto-generated method stub
		String action = arg0.getParameter("action");
		switch(action) {
			case "stat":
				String xml = seabedHabitatUCC.getData("2.122", "51.428", "3.03", "51.548");
				System.out.println(xml);
				break;
		}
		//super.doGet(arg0, arg1);
	}
	
}
