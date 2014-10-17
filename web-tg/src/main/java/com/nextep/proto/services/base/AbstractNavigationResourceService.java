package com.nextep.proto.services.base;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.nextep.proto.exceptions.ResourceException;
import com.nextep.proto.services.NavigationResourceService;
import com.videopolis.xml.service.JaxbService;

/**
 * An abstract base implementation of {@link NavigationResourceService}
 * 
 * Implementors should extend this base class instead of implementing
 * {@link NavigationResourceService} directly
 * 
 * @author julien
 * 
 */
public abstract class AbstractNavigationResourceService implements
		NavigationResourceService {

	@Override
	public InputStream getResourceAsStream(final String name) {
		try {
			final URL url = getResource(name);
			return url.openStream();
		} catch (final IOException e) {
			throw new ResourceException(e.getMessage(), e);
		}
	}

	@Override
	public Properties loadPropertiesFromResource(final String name) {
		try {
			final InputStream inputStream = getResourceAsStream(name);
			final Properties properties = new Properties();
			try {
				properties.load(inputStream);
			} finally {
				inputStream.close();
			}
			return properties;
		} catch (final IOException e) {
			throw new ResourceException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T unmarshallXmlFromResource(final JaxbService jaxbService,
			final Class<T> resultClass, final String name) {

		try {
			final InputStream inputStream = getResourceAsStream(name);
			try {
				final Object result = jaxbService.getUnmarshaller().unmarshal(
						inputStream);
				if (!resultClass.isInstance(result)) {
					throw new ResourceException("Provided class "
							+ resultClass.getCanonicalName()
							+ " is incompatible with effective object type "
							+ result.getClass().getCanonicalName());
				}
				return (T) result;
			} finally {
				inputStream.close();
			}
		} catch (final JAXBException e) {
			throw new ResourceException("Unable to unmarshall XML data: "
					+ e.getMessage(), e);
		} catch (final IOException e) {
			throw new ResourceException(e.getMessage(), e);
		}
	}

	@Override
	public File getResourceAsFile(final String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close(final Closeable closeable) {
		try {
			closeable.close();
		} catch (final IOException e) {
			throw new ResourceException(e.getMessage(), e);
		}
	}
}
