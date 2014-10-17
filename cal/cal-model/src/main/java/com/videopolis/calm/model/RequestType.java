package com.videopolis.calm.model;

import java.io.Serializable;

/**
 * A request type interface allowing to narrow a CAL request. This interface is
 * empty (and <u>should remain empty</u>) because it is a pure informative
 * notion. <br>
 * <br>
 * This interface defines a type of request. A {@link RequestType} allows APIS
 * callers to indicate that it does not require the full set of information for
 * an element but only a subset of it. <br>
 * <br>
 * It is the responsibility of the <u>CAL service implementor</u> to understand
 * the request type to optimize its processing. It is the responsibility of the
 * <u>APIS caller</u>, after the response is returned, to only work with the
 * requested subset of information.<br>
 * Generic {@link RequestType} are defined as static constants of this
 * interface. CAL service providers may provide new {@link RequestType} that
 * they understand for the specific need of this content. CAL providers willing
 * to provide such custom types should expose an interface named by the module
 * code concatenated with "RequestType" and provide static {@link RequestType}
 * constants implementation.<br><br>
 * <u>Example :</u><br>
 * <code>public interface GaeaRequestType {<br>
 * &nbsp;&nbsp;// Add explicit Jdoc comment here <br>
 * &nbsp;&nbsp;RequestType CUSTOM = new RequestType() {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;private static final long serialVersionUID = 1;<br>
 * &nbsp;&nbsp;}<br>
 * }<br>
 * </code><br>
 * and then referencing this request type in an APIS call could be made through :<br>
 * <code>GaeaRequestType.CUSTOM</code><br>
 * 
 * 
 * @author Christophe Fondacci
 * 
 */
public interface RequestType extends Serializable {

    /** Default full request type */
    RequestType FULL = new RequestType() {
	/** Serialization unique key */
	private static final long serialVersionUID = -4499232016959338675L;
    };
    /** Latitude and longitude information is requested */
    RequestType LAT_LONG = new RequestType() {
	/** Serialization unique key */
	private static final long serialVersionUID = -6176496185431591558L;
    };
    /** Element's unique key identifier is requested */
    RequestType ID_ONLY = new RequestType() {
	/** Serialization unique key */
	private static final long serialVersionUID = -5156338233641543275L;
    };
}
