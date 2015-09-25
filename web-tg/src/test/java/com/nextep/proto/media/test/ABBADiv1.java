package com.nextep.proto.media.test;

import org.junit.Test;

import junit.framework.Assert;

public class ABBADiv1 {

	public String canObtain(String initial, String target) {
		if (target.equals(initial)) {
			System.out.println(initial + " / " + target + " => Possible");
			return "Possible";
		} else {
			String result = "Impossible";
			if (target.endsWith("A")) {
				result = canObtain(initial, target.substring(0, target.length() - 2));

			}
			if (target.startsWith("B") && "Impossible".equals(result)) {
				result = canObtain(initial, reverse(target.substring(1)));
			}

			System.out.println(initial + " / " + target + " => " + result);
			return result;

		}
	}

	private String reverse(String s) {
		final char[] cars = s.toCharArray();
		final char[] reverseCars = new char[cars.length];
		for (int i = cars.length - 1; i >= 0; i--) {
			reverseCars[cars.length - 1 - i] = cars[i];
		}
		return new String(reverseCars);
	}

	@Test
	public void test() {
		Assert.assertEquals("Possible", canObtain("AAABBAABB", "BAABAAABAABAABBBAAAAAABBAABBBBBBBABB"));
	}
}
