package com.nextep.proto.media.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class AB {
	private static final Log LOGGER = LogFactory.getLog(AB.class);

	private class StringWithPairs {
		String string;
		int pairs;

		public StringWithPairs(String string, int pairs) {
			this.string = string;
			this.pairs = pairs;
		}
	}

	private StringWithPairs buildString(int N, int K) {
		if (N == 0) {
			return new StringWithPairs("", 0);
		} else if (N == 1) {
			return new StringWithPairs("A", 0);
		} else {
			StringWithPairs sp = buildString(N - 1, K);
			if (sp.pairs >= K) {
				return new StringWithPairs(sp.string + "A", sp.pairs);
			} else {
				int aCount = 0;
				for (int i = 0; i < sp.string.length(); i++) {
					if (sp.string.charAt(i) == 'A') {
						aCount++;
					}
				}
				return new StringWithPairs(sp.string + "B", sp.pairs + aCount);
			}
		}
	}

	public String createString(int N, int K) {
		final StringBuilder buf = new StringBuilder();
		for (int i = 0; i < N; i++) {
			buf.append("A");
		}
		return adjustString(buf.toString(), N, K, 0);
	}

	public String adjustString(String template, int N, int K, int placedB) {
		final StringBuilder buf = new StringBuilder(template);
		if (template.length() < N) {
			return "";
		} else if (K == 0) {
			return buf.toString();
		} else if (K + placedB < N) {
			if (buf.charAt(K + placedB) == 'A') {
				buf.setCharAt(K + placedB, 'B');
				return buf.toString();
			} else {
				return "";
			}
		} else {
			int i = N - 1;
			while (i >= 0) {
				if (buf.charAt(i) == 'A') {
					buf.setCharAt(i, 'B');
					return adjustString(buf.toString(), N, K - i + placedB, placedB + 1);
				}
				i--;
			}
		}
		return "";
	}

	@Test
	public void test() {
		LOGGER.info("==== 3,2");
		final String val = createString(5, 8);

	}
}