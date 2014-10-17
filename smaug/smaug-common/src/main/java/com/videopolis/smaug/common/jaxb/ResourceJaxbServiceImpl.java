package com.videopolis.smaug.common.jaxb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBException;

import com.videopolis.smaug.common.exception.SearchReferenceException;
import com.videopolis.xml.service.impl.JaxbServiceImpl;

/**
 * An extension of the JAXB service which is based on a resource (file or
 * classpath).
 * 
 * @author Christophe Fondacci
 * 
 */
public class ResourceJaxbServiceImpl extends JaxbServiceImpl {

    private static final String TAG_CLASSPATH = "classpath:";
    private String resourcePath;

    @SuppressWarnings("unchecked")
    public <T> T unmarshallXmlFromResource(final Class<T> resultClass,
	    final String name) throws SearchReferenceException {

	try {
	    final InputStream inputStream = getResourceAsStream(name);
	    try {
		final Object result = getUnmarshaller().unmarshal(inputStream);
		if (!resultClass.isInstance(result)) {
		    throw new SearchReferenceException("Provided class "
			    + resultClass.getCanonicalName()
			    + " is incompatible with effective object type "
			    + result.getClass().getCanonicalName());
		}
		return (T) result;
	    } finally {
		inputStream.close();
	    }
	} catch (final JAXBException e) {
	    throw new SearchReferenceException(
		    "Unable to unmarshall XML data: " + e.getMessage(), e);
	} catch (final IOException e) {
	    throw new SearchReferenceException(e.getMessage(), e);
	}
    }

    /**
     * Retrieves the specified resource as an {@link InputStream}
     * 
     * @param name
     *            name of the resource to retrieve
     * @return the {@link InputStream} on the specified resource
     * @throws SearchReferenceException
     *             whenever we cannot locate the specified resource
     */
    public InputStream getResourceAsStream(final String name)
	    throws SearchReferenceException {
	try {
	    final URL url = getResource(name);
	    return url.openStream();
	} catch (final IOException e) {
	    throw new SearchReferenceException(e.getMessage(), e);
	}
    }

    /**
     * Retrieves a resource URL
     * 
     * @param name
     *            name of the resource to look for
     * @return the resource URL
     * @throws SearchReferenceException
     *             whenever we had problems to locate the resource
     */
    public URL getResource(String name) throws SearchReferenceException {
	if (resourcePath.startsWith(TAG_CLASSPATH)) {
	    String prefix = resourcePath;
	    if (!resourcePath.equals(TAG_CLASSPATH)) {
		prefix = prefix + File.separator;
	    }
	    return this.getClass().getClassLoader().getResource(name);
	} else {
	    try {
		return new File(resourcePath + File.separator + name).toURI()
			.toURL();
	    } catch (MalformedURLException e) {
		throw new SearchReferenceException("Invalid resource file '"
			+ name + "':" + e.getMessage(), e);
	    }
	}
    }

    public void setResourcePath(String resourcePath) {
	this.resourcePath = resourcePath;
    }
}
