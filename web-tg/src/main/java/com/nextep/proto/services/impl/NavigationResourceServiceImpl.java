package com.nextep.proto.services.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextep.proto.exceptions.ResourceException;
import com.nextep.proto.services.base.AbstractNavigationResourceService;

/**
 * Default implementation of {@code NavigationResourceService}
 * 
 * This implementation will look for resources either in the classpath or in the
 * filesystem, depending on the provided path. If the path begins with
 * {@code classpath:} the resources will be searched in the classpath, otherwise
 * in the filesystem
 * 
 * @author julien
 * 
 */
public class NavigationResourceServiceImpl extends
		AbstractNavigationResourceService {

	private static final Log LOGGER = LogFactory
			.getLog(NavigationResourceServiceImpl.class);

	/** Prefix for classpath-based pathes */
	private static final String CLASSPATH_PREFIX = "classpath:";

	/** Whether or not the path denotes a classpath path */
	private boolean isClasspath;

	/** The actual path */
	private String path;

	/** The classloader to load resources from */
	private ClassLoader classLoader;

	/**
	 * Sets the path
	 * 
	 * @param path
	 */
	public void setPath(final String path) {
		if (path.startsWith(CLASSPATH_PREFIX)) {
			isClasspath = true;
			this.path = path.substring(CLASSPATH_PREFIX.length());
			LOGGER.info("Will look for resources into classpath path "
					+ this.path);
		} else {
			isClasspath = false;
			this.path = path;
			LOGGER.info("Will look for resources into filesystem path "
					+ this.path);
		}
	}

	/**
	 * Sets the {@link ClassLoader} to use.
	 * 
	 * The object can be of any type.
	 * <ul>
	 * <li>If it's a {@link ClassLoader} instance, it will be used as is</li>
	 * <li>If it's a {@link Class} instance, it's {@link ClassLoader} will be
	 * used</li>
	 * <li>If it's any other object, the {@link ClassLoader} which loaded its
	 * {@link Class} will be used.</li>
	 * </ul>
	 * 
	 * @param object
	 *            Any object
	 */
	public void setClassLoader(final Object object) {
		if (object instanceof ClassLoader) {
			classLoader = (ClassLoader) object;
		} else {
			if (object instanceof Class<?>) {
				classLoader = ((Class<?>) object).getClassLoader();
			} else {
				classLoader = object.getClass().getClassLoader();
			}
		}
	}

	/**
	 * Returns the classloader to use, falling back to the system one.
	 * 
	 * @return ClassLoader to use
	 */
	private ClassLoader getClassLoader() {
		if (classLoader == null) {
			return NavigationResourceServiceImpl.class.getClassLoader();
		} else {
			return classLoader;
		}
	}

	@Override
	public URL getResource(final String name) {
		if (isClasspath) {
			return getClasspathResource(name);
		} else {
			return getFileResource(name);
		}
	}

	/**
	 * Finds a classpath resource
	 * 
	 * @param name
	 *            Name
	 * @return URL
	 */
	private URL getClasspathResource(final String name) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Getting resource: " + path + "/" + name);
		}
		final URL url = getClassLoader().getResource(path + "/" + name);
		if (url == null) {
			throw new ResourceException("No such resource : " + name);
		}
		return url;
	}

	/**
	 * Finds a file resource
	 * 
	 * @param name
	 *            Name
	 * @return URL
	 */
	private URL getFileResource(final String name) {
		try {
			return getResourceFile(name).toURI().toURL();
		} catch (final MalformedURLException e) {
			throw new ResourceException(e.getMessage(), e);
		}
	}

	@Override
	public File getResourceAsFile(final String name) {
		if (isClasspath) {
			throw new UnsupportedOperationException();
		} else {
			return getResourceFile(name);
		}
	}

	/**
	 * Finds a resource file
	 * 
	 * @param name
	 *            Name
	 * @return File
	 */
	private File getResourceFile(final String name) {
		final File resourceFile = new File(path + File.separator + name);
		if (!resourceFile.exists()) {
			throw new ResourceException("No such resource : " + name);
		}
		return resourceFile;
	}
}
