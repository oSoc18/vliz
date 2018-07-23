package main;

import java.io.IOException;
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
			BathymetryDAO bathymetryDAO = new BathymetryDAO(appContext.getProperty("bathymetry"),
					appContext.getProperty("cache-dir"), appContext.getProperty("bathymetry-stat"));
			UCCBathymetry uccBathymetry = new UCCBathymetry(bathymetryDAO);

			startServer(appContext, uccBathymetry);
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "App configuration failed !", exc);
		}
	}

	private static void startServer(AppContext appContext, UCCBathymetry uccBathymetry) throws Exception {
		LOGGER.info("Starting the server...");
		Server server = new Server(Integer.parseInt(appContext.getProperty(("port"))));
		WebAppContext context = new WebAppContext();

		context.setResourceBase("www");
		context.addServlet(new ServletHolder(new DefaultServlet()), "/");

		initVectorLayerServlet("seabed", appContext, context);
		initVectorLayerServlet("physics", appContext, context);

		HttpServlet bathymetryServlet = new BathymetryServlet(uccBathymetry);
		context.addServlet(new ServletHolder(bathymetryServlet), "/bathymetry");

		server.setHandler(context);
		server.start();
		LOGGER.info("The server is listening...");
	}

	private static void initVectorLayerServlet(String layerName, AppContext appContext, WebAppContext context) throws IOException {
		String defaultType = appContext.getProperty(layerName+"-default-type");
		VectorLayersDAO vectorLayersDAO = new VectorLayersDAO(appContext.getProperty(layerName),defaultType);

		UCCVectorLayers uccVectorLayers = new UCCVectorLayers(vectorLayersDAO);

		CachingManager dataCache = new CachingManager(layerName, appContext.getProperty("cache-dir"),
				"data-{id}.FeatureCollection");
		CachingManager statsCache = new CachingManager(layerName, appContext.getProperty("cache-dir"),
				"stats-{id}.SurfaceCount");

		PiecedCachingManager pcm = new PiecedCachingManager(uccVectorLayers, dataCache, statsCache);

		HttpServlet seabedServlet = new VectorLayersServlet(pcm, defaultType);

		context.addServlet(new ServletHolder(seabedServlet), "/"+layerName);
	}

}
