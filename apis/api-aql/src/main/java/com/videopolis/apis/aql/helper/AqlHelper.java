package com.videopolis.apis.aql.helper;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.videopolis.apis.aql.exception.AqlException;
import com.videopolis.apis.aql.exception.QueryParsingException;
import com.videopolis.apis.aql.handler.AqlHandler;
import com.videopolis.apis.aql.parser.AqlLexer;
import com.videopolis.apis.aql.parser.AqlParser;

/**
 * An helper used to work on AQL queries
 * 
 * @author julien
 * 
 */
public final class AqlHelper {

    private AqlHelper() {
    }

    /**
     * Parse an AQL Query given as a String and use a provided
     * {@link AqlHandler} to handle parsing events.
     * 
     * @param input
     *            Input string
     * @param handler
     *            Handler to use
     * @throws AqlException
     *             If the query can't be parsed
     */
    public static final void parse(String input, AqlHandler handler)
	    throws AqlException {
	try {
	    final AqlLexer aqlLexer = new AqlLexer(new ANTLRStringStream(input));
	    final AqlParser aqlParser = new AqlParser(new CommonTokenStream(
		    aqlLexer));
	    aqlParser.setAqlHandler(handler);
	    aqlParser.query();
	} catch (RecognitionException e) {
	    throw new AqlException(e.getMessage(), e);
	} catch (QueryParsingException e) {
	    throw new AqlException(e.getMessage(), e);
	}
    }
}
