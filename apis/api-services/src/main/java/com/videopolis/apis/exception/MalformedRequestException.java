package com.videopolis.apis.exception;

/**
 * A specific APIS exception indicating a malformed APIS request.
 * 
 * @author Christophe Fondacci
 * 
 */
public class MalformedRequestException extends ApisException {

    /** Serialization UID */
    private static final long serialVersionUID = -6171219073958871651L;

    /**
     * Unique constructor of this exception accepting a message to describe the
     * reason why the request is malformed.
     * 
     * @param reason
     *            reason of the malformed request
     */
    public MalformedRequestException(String reason) {
	super(reason);
    }

}
