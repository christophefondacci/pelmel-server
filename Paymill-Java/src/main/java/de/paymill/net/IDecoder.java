/**
 * 
 */
package de.paymill.net;

import java.lang.reflect.Type;

import de.paymill.PaymillException;

/**
 * Decoder interface, specifiyng methods required for decoding objects from
 * strings.
 * 
 * @author Johannes Klose <johannes.klose@raketen-projekte.de>
 */
public interface IDecoder {

	/**
	 * Decodes a string into an object of the given type. Throws an
	 * {@link PaymillException} if an error occurs while decoding the
	 * string.
	 * 
	 * @param data
	 *            The data to decode.
	 * @param type
	 *            The (class) type of the object to construct.
	 * @return The decoded object.
	 * @throws PaymillException
	 */
	public <T> T decode(String data, Type type);

	/**
	 * Decodes an error response into an error object. Throws an
	 * {@link PaymillException} if the json string can't be decoded into
	 * an error object.
	 * 
	 * @param data
	 *            The error message to decode.
	 * @return The decoded error object.
	 * @throws PaymillException
	 */
	public ApiException decodeError(String data);

	/**
	 * Set the character set of the encoded string.
	 * 
	 * @param charset
	 */
	public void setCharset(String charset);
}
