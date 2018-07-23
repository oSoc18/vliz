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
import vectorLayers.VectorLayersDAO;
import vectorLayers.VectorLayersServlet;
import vectorLayers.UCCVectorLayers;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws Exception {
		System.out.println("VLIZ Server 0.1");
		try {
			LOGGER.info("Loading app configuration...");
			AppContext appContext = new AppContext();
			AppContext.configLogger("log.properties");
			appContext.loadProperties(args.length == 0 ? "prod.properties" : args[0]);
			VectorLayersDAO vectorLayersDAO = new VectorLayersDAO(appContext,appContext.getProperty("seabedURL"),
					appContext.getProperty("default-type"));
			BathymetryDAO bathymetryDAO = new BathymetryDAO(appContext.getProperty("bathymetryURL"),
					appContext.getProperty("cache-dir"), appContext.getProperty("bathymetry-stat"));
			UCCVectorLayers uccVectorLayers = new UCCVectorLayers(vectorLayersDAO);
			UCCBathymetry uccBathymetry = new UCCBathymetry(bathymetryDAO);

			CachingManager cm = new CachingManager(appContext.getProperty("cache-dir"),
					appContext.getProperty("seabed-data"));
			CachingManager statsCache = new CachingManager(appContext.getProperty("cache-dir"),
					appContext.getProperty("seabed-stat"));

			startServer(Integer.parseInt(appContext.getProperty("port")), uccVectorLayers, uccBathymetry, cm,
					statsCache, appContext.getProperty("default-type"));
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "App configuration failed !", exc);
		}
	}

	private static void startServer(int port, UCCVectorLayers uccVectorLayers, UCCBathymetry uccBathymetry,
			CachingManager cm, CachingManager statsCache, String defaultType) throws Exception {
		LOGGER.info("Starting the server...");
		Server server = new Server(port);
		WebAppContext context = new WebAppContext();

		context.setResourceBase("www");

		HttpServlet seabedServlet = new VectorLayersServlet(uccVectorLayers, cm, statsCache, defaultType);
		HttpServlet bathymetryServlet = new BathymetryServlet(uccBathymetry);

		context.addServlet(new ServletHolder(new DefaultServlet()), "/");
		context.addServlet(new ServletHolder(seabedServlet), "/vectorLayer");
		context.addServlet(new ServletHolder(seabedServlet), "/vectorlayer");
		context.addServlet(new ServletHolder(bathymetryServlet), "/bathymetry");

		server.setHandler(context);
		server.start();
		LOGGER.info("The server is listening...");
	}

}
