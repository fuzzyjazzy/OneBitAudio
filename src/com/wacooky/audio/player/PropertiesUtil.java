package com.wacooky.audio.player;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
/**
 * PropertiesUtil class provides convenient static methods to
 * 1) read/write properties file in "Home" directory,
 * 2) support string array as a property value. 
 * @author fujimori
 * @version 1.0 2016-06-10
 */
public class PropertiesUtil {
	/**
	 * 
	 * @param properties	Properties object
	 * @param name			file name of the properties resides in HOME directory.
	 * @param operation		String "load" or "save".
	 * @return result of operation int 0: ok -1: error
	 */
	static public int propertiesInUserDir( Properties properties, String name, String operation ) {
		Path path = Paths.get(System.getProperty("user.home"), name);
		try {
			if( "load".compareToIgnoreCase( operation ) == 0 ) {
				Reader rd = new FileReader( path.toString() );
				properties.load(rd);
				return 0;
			}
			if( "save".compareToIgnoreCase( operation ) == 0 ) {
				Writer wt = new FileWriter( path.toString() );
				properties.store(wt, "");
				return 0;
			}
		} catch (FileNotFoundException e) {
		;
		} catch (Exception e) {
			//System.out.println("Can not load properties from " + file.getName() );
		}
		return -1;
	}

	/**
	 * Retrieve string as array of the key whose value is store by append or appendUnique methods.
	 * .
	 * @param properties	Properties object.
	 * @param key			key of array
	 * @return	String[] 	read value
	 */
	static public String[] getPropertyAsArray(Properties properties, String key) {
		String list = properties.getProperty(key);
		if (list == null)
			return new String[0];
		return list.split(";");
	}

	/**
	 * Put strings at a key in a way that they can be retrieved as an array.
	 * 
	 * @param properties	Properties object.
	 * @param key			key of array.
	 * @param valuelist		values to be written.
	 */
	static public void put(Properties properties, String key, String... valuelist) {
		String str = null;
		for (String value : valuelist) {
			if (str == null)
				str = value;
			else
				str += (";" + value);
		}
		if (str != null)
			properties.setProperty(key, str);	
	}

	/**
	 * Add strings which to existing strings for the key.
	 *
	 * @param properties	Properties object.
	 * @param key			key of array.
	 * @param valuelist		values to be written.
	 */
	static public void append(Properties properties, String key, String... valuelist) {
		String str = properties.getProperty(key);
		for (String value : valuelist) {
			if (str == null)
				str = value;
			else
				str += (";" + value);
		}
		if (str != null)
			properties.setProperty(key, str);	
	}

	/**
	 * Add strings which are not equal (case sensitive) to any existing string for the key.
	 * 
	 * @param properties	Properties object.
	 * @param key			key of array.
	 * @param valuelist		values to be written.
	 */
	static public void appendUnique(Properties properties, String key, String... valuelist) {
		String[] values = getPropertyAsArray(properties,key);
		String str = null;
		for (String value : valuelist) {
			for (int i = -1; i < values.length; i++) {
				if (i >= 0 && value.compareTo(values[i]) == 0)
					continue;
				if (str == null)
					str = value;
				else
					str += (";" + value);
			}
		}
		if (str != null)
			properties.setProperty(key, str);
	}

}
