package main;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import bathymetry.BathymetryDAO;
import bathymetry.BathymetryServlet;
import bathymetry.UCCBathymetry;
import seabedhabitat.SeabedHabitatDAO;
import seabedhabitat.SeabedHabitatServlet;
import seabedhabitat.UCCSeabedHabitat;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws Exception {
		try {
			LOGGER.info("Loading app configuration...");
			AppContext appContext = new AppContext();
			appContext.configLogger("log.properties");
			appContext.loadProperties(args.length == 0 ? "prod.properties" : args[0]);
			SeabedHabitatDAO seabedHabitatDAO = new SeabedHabitatDAO(appContext.getProperty("seabedURL"),
					appContext.getProperty("default-type"), appContext.getProperty("cache-dir"),
					appContext.getProperty("seabed-data"), appContext.getProperty("seabed-stat"));
			BathymetryDAO bathymetryDAO = new BathymetryDAO(appContext.getProperty("bathymetryURL"),
					appContext.getProperty("cache-dir"), appContext.getProperty("bathymetry-stat"));
			UCCSeabedHabitat uccSeabedHabitat = new UCCSeabedHabitat(seabedHabitatDAO);
			UCCBathymetry uccBathymetry = new UCCBathymetry(bathymetryDAO);
			startServer(Integer.parseInt(appContext.getProperty("port")), uccSeabedHabitat,uccBathymetry);
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "App configuration failed !", exc);
		}
	}

	private static void startServer(int port, UCCSeabedHabitat uccSeabedHabit, UCCBathymetry uccBathymetry)
			throws Exception {
		LOGGER.info("Starting the server...");
		Server server = new Server(port);
		WebAppContext context = new WebAppContext();

		context.setResourceBase("www");

		HttpServlet seabedServlet = new SeabedHabitatServlet(uccSeabedHabit);
		HttpServlet bathymetryServlet = new BathymetryServlet(uccBathymetry);

		context.addServlet(new ServletHolder(new DefaultServlet()), "/");
		context.addServlet(new ServletHolder(seabedServlet), "/seabed");
		context.addServlet(new ServletHolder(bathymetryServlet), "/bathymetry");

		server.setHandler(context);
		server.start();
		LOGGER.info("The server is listening...");
	}

}
