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
		
		String xml = seabedHabitatUCC.getData("2.122", "51.428", "3.03", "51.548");
		
		//System.out.println("hello");
		
		/*String coord1= arg0.getParameter("coord1").trim();
		String coord2= arg0.getParameter("coord2").trim();
		String coord3= arg0.getParameter("coord3").trim();
		String coord4= arg0.getParameter("coord4").trim();
		String xml = seabedHabitatUCC.getData(coord1,coord2,coord3,coord4);*/
		//System.out.println(xml);
	
		/*
		String action = arg0.getParameter("action");
		switch(action) {
			case "stat":
				//String coord1= arg0.getParameter("Coord1");
				//String coord2= arg0.getParameter("Coord2");
				//String coord3= arg0.getParameter("Coord3");
				//String coord4= arg0.getParameter("Coord4");
				//System.out.println(coord1 +" "+ coord2 +" "+ coord3+ " "+ coord4 );
				String xml = seabedHabitatUCC.getData("2.122", "51.428", "3.03", "51.548");
				System.out.println(xml);
				break;
		}
		//super.doGet(arg0, arg1);
		 
		 */
	}
	
}
