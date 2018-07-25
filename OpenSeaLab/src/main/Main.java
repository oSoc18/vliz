package main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
import feature.Rectangle;
import vectorLayers.VectorLayersDAO;
import vectorLayers.VectorLayersServlet;

public class Main {
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

	public static void main(String[] args) throws Exception {
		System.out.println("VLIZ Server 0.4");
		try {
			LOGGER.info("Loading app configuration...");
			AppContext appContext = new AppContext();
			AppContext.configLogger("log.properties");
	
			
			if(args.length != 0 && args[0].equals("--populate-cache")) {
				appContext.loadProperties("prod.properties");
				
				List<String> knownLayers = 
						Arrays.asList(appContext.getProperty("known-layers").split(";"));
				
				
				Runnable lastAction = new Runnable() {
					
					@Override
					public void run() {
						System.out.println("All done, exiting now");
						System.exit(0);
					}
				};
				
				for (String layer : knownLayers) {
					lastAction = populateCache(appContext, layer, lastAction);
				}
				lastAction.run();
				
				return;
			}
			appContext.loadProperties(args.length == 0 ? "prod.properties" : args[0]);

			startServer(appContext);
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "App configuration failed !", exc);
		}
	}

	private static void startServer(AppContext appContext) throws Exception {
		LOGGER.info("Starting the server...");
		Server server = new Server(Integer.parseInt(appContext.getProperty(("port"))));
		WebAppContext context = new WebAppContext();

		BathymetryDAO bathymetryDAO = new BathymetryDAO(appContext.getProperty("bathymetry"),
				appContext.getProperty("cache-dir"), appContext.getProperty("bathymetry-stat"));
		UCCBathymetry uccBathymetry = new UCCBathymetry(bathymetryDAO);

		context.setResourceBase("www");
		context.addServlet(new ServletHolder(new DefaultServlet()), "/");

		initVectorLayerServlet("seabed", appContext, context);
		initVectorLayerServlet("physics", appContext, context);
		initVectorLayerServlet("geology", appContext, context);
		//initVectorLayerFromFile("test.json", context);
		// here you can add more layers as done previously 
		
		HttpServlet bathymetryServlet = new BathymetryServlet(uccBathymetry);
		context.addServlet(new ServletHolder(bathymetryServlet), "/bathymetry");

		server.setHandler(context);
		server.start();
		LOGGER.info("The server is listening...");
	}

	private static Runnable populateCache(final AppContext appContext, final String layerName, final Runnable whenDone) throws IOException {
		return new Runnable() {

			@Override
			public void run() {
				try {
					System.out.println("Running cache population for "+layerName);
					populateCache(appContext, "geology", new Rectangle(appContext, "geology"),
							appContext.getProperty(layerName + "-default-type"), appContext.getProperty(layerName + "-default-dividor"), whenDone);
				} catch (IOException e) {
					e.printStackTrace();
					whenDone.run();
				}
			}
			
		};

	}

	private static void populateCache(AppContext appContext, String layerName, Rectangle bbox, String type, String dividor, Runnable whenDone)
			throws IOException {
		PiecedCachingManager pcm = createPCM(layerName, appContext);
		pcm.loadAndCacheAll(bbox, type, dividor, whenDone);
	}
	
	@SuppressWarnings("unused")
	private static void initVectorLayerFromFile(String file, WebAppContext context) throws IOException {
		
		HttpServlet servlet = new VectorLayersServlet(new FromJSONFileLayer("test.json"), "", "");
		context.addServlet(new ServletHolder(servlet), "/test");
	}

	private static void initVectorLayerServlet(String layerName, AppContext appContext, WebAppContext context)
			throws IOException {
		String defaultType = appContext.getProperty(layerName + "-default-type");
		String defaultDividor = appContext.getProperty(layerName + "-default-dividor");

		PiecedCachingManager pcm = createPCM(layerName, appContext);
		HttpServlet seabedServlet = new VectorLayersServlet(pcm, defaultType, defaultDividor);

		context.addServlet(new ServletHolder(seabedServlet), "/" + layerName);
	}

	private static PiecedCachingManager createPCM(String layerName, AppContext appContext) throws IOException {
		String defaultType = appContext.getProperty(layerName + "-default-type");

		VectorLayersDAO vectorLayersDAO = new VectorLayersDAO(layerName, appContext.getProperty(layerName),
				defaultType);
		CachingManager dataCache = new CachingManager(layerName, appContext.getProperty("cache-dir"),
				"data-{id}.FeatureCollection");
		CachingManager statsCache = new CachingManager(layerName, appContext.getProperty("cache-dir"),
				"stats-{id}.SurfaceCount");

		PiecedCachingManager pcm = new PiecedCachingManager(vectorLayersDAO, dataCache, statsCache);
		return pcm;
	}

}
