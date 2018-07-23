package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import exceptions.FatalException;
import feature.Rectangle;

public class Util {
	private static final Logger LOGGER = Logger.getLogger(Util.class.getName());

	public static void store(String data, Path p, String directory) {
		try {
			Path cache = FileSystems.getDefault().getPath(directory);
			if (!Files.exists(cache) || !Files.isDirectory(cache)) {
				Files.createDirectory(cache);
				LOGGER.log(Level.FINE, "Caching directory created ");
			}
			try (Writer writer = Files.newBufferedWriter(p, StandardCharsets.UTF_8)) {
				writer.write(data);
				LOGGER.log(Level.FINE, "Cache file " + p + " created");
			}
		} catch (IOException io) {
			throw new FatalException("The file cannot be saved", io);
		}
	}

	public static InputStream fetchFrom(String url) {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setReadTimeout(20000);
			connection.setConnectTimeout(20000);
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.connect();
			return connection.getInputStream();
		} catch (IOException e) {
			throw new FatalException(e);
		}

	}
	
	public static Rectangle getBBox(HttpServletRequest req) {
		return new Rectangle(req.getParameter("minLat"), req.getParameter("minLong"), req.getParameter("maxLat"),
				req.getParameter("maxLong"));
	}
}
