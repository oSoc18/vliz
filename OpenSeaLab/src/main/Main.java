package main;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import seabedhabitat.SeabedHabitatServlet;
import seabedhabitat.UCCSeabedHabitat;

public class Main {

	public static void main(String[] args) throws Exception {
		
		int port = 8080;
		Server server = new Server(port);
		WebAppContext context = new WebAppContext();

		context.setResourceBase("www");

		HttpServlet seabedServlet = new SeabedHabitatServlet(new UCCSeabedHabitat());

		context.addServlet(new ServletHolder(new DefaultServlet()), "/");
		context.addServlet(new ServletHolder(seabedServlet), "/seabed");
		
		server.setHandler(context);
		server.start();

	}

}
