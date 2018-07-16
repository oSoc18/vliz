package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.Properties;
import java.util.logging.LogManager;

import exceptions.FatalException;

public class AppContext {
	private Properties props;

	public AppContext() {
		props = new Properties();
	}

	/**
	 * Retruns an instance of the requested class.
	 * 
	 * @param cls
	 *            a .class object
	 * @param args
	 *            arguments of cls constructor
	 * @return instance of cls
	 */
	public Object newInstance(Class<?> cls, Object... args) {
		Constructor<?>[] construc = cls.getDeclaredConstructors();
		try {
			for (Constructor<?> cons : construc) {
				cons.setAccessible(true);
				int counter = 0;
				if (cons.getParameterCount() != args.length) {
					continue;
				}
				Parameter[] param = cons.getParameters();
				for (Parameter par : param) {
					Object argComp = args[counter].getClass();
					Class<?> parClass = par.getType();
					if (!argComp.equals(par.getType())) {
						Class<?> argInterface = null;
						Class<?>[] interfaces = args[counter].getClass().getInterfaces();
						for (Class<?> c : interfaces) {
							if (parClass.equals(c)) {
								argInterface = c;
								break;
							}
						}
						if (argInterface == null) {
							break;
						}
					}
					counter++;
				}
				if (counter == args.length) {
					return cons.newInstance(args);
				}
			}
		} catch (IllegalArgumentException | InstantiationException | IllegalAccessException
				| InvocationTargetException exc) {
			throw new FatalException(exc);
		}
		return null;
	}
	// TODO remove cached file

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
