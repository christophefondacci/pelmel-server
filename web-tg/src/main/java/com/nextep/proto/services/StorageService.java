package com.nextep.proto.services;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface for a storage service that can write information to an underlying
 * storage.
 * 
 * @author cfondacci
 * 
 */
public interface StorageService {

	/**
	 * Writes the given information streamed from input stream to the underlying
	 * storage under the name provided
	 * 
	 * @param name
	 *            the name that will uniquely identify the location of the
	 *            information to write in the storage
	 * @param inputStream
	 *            the {@link InputStream} to read the information from
	 */
	void writeStream(String name, InputStream inputStream) throws IOException;

	void writeStream(String name, String contentType, InputStream inputStream)
			throws IOException;
}
