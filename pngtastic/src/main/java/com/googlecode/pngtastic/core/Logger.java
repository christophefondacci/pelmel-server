package com.googlecode.pngtastic.core;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Custom logger because I want to have zero dependancies; perhaps to be replaced with java.util logging.
 *
 * @author rayvanderborght
 */
public class Logger {
	private static final Log LOGGER = LogFactory.getLog(Logger.class);

	static final String NONE = "NONE";
	static final String DEBUG = "DEBUG";
	static final String INFO = "INFO";
	static final String ERROR = "ERROR";
	private static final List<String> LOG_LEVELS = Arrays.asList(NONE, DEBUG, INFO, ERROR);

	private final String logLevel;

	/** */
	Logger(String logLevel) {
		this.logLevel = (logLevel == null || !LOG_LEVELS.contains(logLevel.toUpperCase()))
				? INFO : logLevel.toUpperCase();
	}

	/**
	 * Write debug messages.
	 * Takes a varags list of args so that string concatenation only happens if the logging level applies.
	 */
	public void debug(String message, Object... args) {
		LOGGER.debug(String.format(message, args));
	}

	/**
	 * Write info messages.
	 * Takes a varags list of args so that string concatenation only happens if the logging level applies.
	 */
	public void info(String message, Object... args) {
		LOGGER.info(String.format(message, args));
	}

	/**
	 * Write error messages.
	 * Takes a varags list of args so that string concatenation only happens if the logging level applies.
	 */
	public void error(String message, Object... args) {
		LOGGER.error(String.format(message, args));
	}
}
