package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import exceptions.FatalException;

public class AppContext {
	private Properties props;

	public AppContext() {
		props = new Properties();
	}

	/**
	 * Loads properties file.
	 * 
	 * @param properties
	 *            pathname of the properties file
	 * @return
	 */

	public boolean loadProperties(String properties) {
		try (FileInputStream file = new FileInputStream(properties)) {
			// System.setProperty("log4j.configurationFile", properties);
			props.load(file);
			//LogManager.getLogManager().readConfiguration(file);
			
			return true;
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
