package com.nextep.proto.services;

import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.videopolis.xml.service.JaxbService;

/**
 * A service designed to work with navigation resources, without caring where
 * and how they are stored.
 * 
 * Implementors should extend {@code AbstractNavigationResourceService} instead
 * of implementing this interface directly
 * 
 * @author julien
 * 
 */
public interface NavigationResourceService {

	/**
	 * Returns a resource's URL
	 * 
	 * @param name
	 *            Resource name
	 * @return Resource file
	 */
	URL getResource(String name);

	/**
	 * Returns a resource as File.
	 * 
	 * This is not supported by all the implementation and may throw an
	 * UnsupportedOperationException.
	 * 
	 * @param name
	 * @return Resource's file
	 */
	File getResourceAsFile(String name);

	/**
	 * Returns an InputStream to a resource
	 * 
	 * @param name
	 *            Resource name
	 * @return Resource InputStream
	 */
	InputStream getResourceAsStream(String name);

	/**
	 * Loads a set of properties from a resource
	 * 
	 * @param name
	 *            Resource name
	 * @return Properties
	 */
	Properties loadPropertiesFromResource(String name);

	/**
	 * Unmarshalls an XML resource
	 * 
	 * @param jaxbService
	 *            JaxbService instance used to unmarshall the XML data
	 * @param resultClass
	 *            The expected result class
	 * @param name
	 *            Resource name
	 * @return Unmarshalled XML
	 */
	<T> T unmarshallXmlFromResource(JaxbService jaxbService,
			Class<T> resultClass, String name);

	/**
	 * A close() method which will handle {@code IOException} and throw
	 * {@code NavigationResourceException} instead
	 * 
	 * @param closeable
	 *            Anything than can be closed
	 */
	void close(Closeable closeable);
}
