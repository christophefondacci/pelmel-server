package com.videopolis.concurrent.test;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.videopolis.concurrent.service.TaskRunnerService;
import com.videopolis.concurrent.test.event.LoggerTaskListener;
import com.videopolis.concurrent.test.exception.BenchmarkFailureException;
import com.videopolis.concurrent.test.factory.TestCaseFactory;

/**
 * Performs a benchmark of different implementations
 *
 * @author julien
 *
 */
public class Benchmark {

    private static final Log LOGGER = LogFactory.getLog(Benchmark.class);

    private static final long STARTUP_DELAY = 5000;
    private static final long EXPECTED_DURATION = 3600;

    private final int launchCount;
    private final TaskRunnerService taskRunnerService;
    private final PrintStream timingPrintStream;

    /**
     * Default constructor
     *
     * @param launchCount
     *            How many times the test must be run
     * @param beanName
     *            Name of the bean of the service
     * @param timingFile
     *            Target file
     * @throws IOException
     */
    public Benchmark(final int launchCount, final String beanName,
	    final String timingFile) throws IOException {
	this.launchCount = launchCount;

	if (timingFile != null) {
	    timingPrintStream = new PrintStream(timingFile);
	} else {
	    timingPrintStream = null;
	}

	// Load Spring context
	final ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(
		"META-INF/concurrent/test/springContext.xml");
	applicationContext.registerShutdownHook();

	LOGGER.info("Will benchmark " + beanName);
	taskRunnerService = (TaskRunnerService) applicationContext
		.getBean(beanName);
    }

    /**
     * Main method
     *
     * @param args
     * @throws ParseException
     * @throws IOException
     * @throws BenchmarkFailureException
     */
    public static void main(final String[] args) throws ParseException,
	    IOException, BenchmarkFailureException {
	final Options options = new Options();
	options.addOption("n", true, "Number of times the test must be run");
	options.addOption("b", true, "The bean name to use");
	options.addOption("o", true, "Output file for time logging");

	final CommandLineParser parser = new PosixParser();
	final CommandLine commandLine = parser.parse(options, args);

	final int launchCount = Integer.valueOf(commandLine.getOptionValue("n",
		"1"));
	final String beanName = commandLine.getOptionValue("b");
	if (beanName == null) {
	    throw new ParseException("beanName (-b) must be set!");
	}
	final String timingFile = commandLine.getOptionValue("o", null);

	final Benchmark benchmark = new Benchmark(launchCount, beanName,
		timingFile);
	LOGGER.info("** Ready to launch benchmark **");
	benchmark.run();

	System.exit(0);
    }

    /**
     * Runs the benchmark
     *
     * @throws BenchmarkFailureException
     */
    @SuppressWarnings("unchecked")
    public void run() throws BenchmarkFailureException {
	try {
	    LOGGER.info("Starting in " + STARTUP_DELAY + " ms...");
	    Thread.sleep(STARTUP_DELAY);
	    LOGGER.info("Let's go!");

	    for (int i = 0; i < launchCount; i++) {
		final long start = System.currentTimeMillis();
		LOGGER.info("** Loop " + (i + 1) + " out of " + launchCount
			+ " **");
		taskRunnerService.execute(TestCaseFactory.createSimpleTestCase(
			taskRunnerService, new LoggerTaskListener<String>()));
		final long stop = System.currentTimeMillis();
		final long duration = stop - start;
		if (duration < EXPECTED_DURATION) {
		    throw new BenchmarkFailureException(
			    "Tasks execution finished in " + duration
				    + " ms. It must not be less than "
				    + EXPECTED_DURATION + " ms!");
		}
		if (timingPrintStream != null) {
		    timingPrintStream.println(stop - start);
		}
	    }
	    LOGGER.info("Done.");
	} catch (final InterruptedException e) {
	    LOGGER.error("Interrupted!");
	} finally {
	    if (timingPrintStream != null) {
		timingPrintStream.close();
	    }
	}
    }
}
