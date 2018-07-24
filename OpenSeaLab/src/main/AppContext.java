package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.logging.LogManager;

import exceptions.FatalException;

public class AppContext {
	private Properties props;

	public AppContext() {
		props = new Properties();
	}

	/**
	 * Configures the logger.
	 * 
	 * @param path
	 *            pathname of the config file
	 */
	public static void configLogger(String path) {

		try (FileInputStream f = new FileInputStream(path)) {
			Path logDir = FileSystems.getDefault().getPath("log");
			if (!Files.exists(logDir) || !Files.isDirectory(logDir)) {
				Files.createDirectory(logDir);
			}
			LogManager.getLogManager().readConfiguration(f);
		} catch (IOException exc) {
			throw new FatalException(exc);
		}
	}

	/**
	 * Loads properties file.
	 * 
	 * @param properties
	 *            pathname of the properties file
	 * @return
	 */

	public void loadProperties(String properties) {
		try (FileInputStream file = new FileInputStream(properties)) {
			props.load(file);
		} catch (IOException exc) {
			throw new FatalException(exc);
		}
	}

	/**
	 * Returns the value linked to the key in the properties file.
	 * 
	 * @param key
	 *            property name
	 * @return property value
	 */
	public String getProperty(String key) {
		return this.props.getProperty(key);
	}
}
