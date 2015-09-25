package com.nextep.proto.media.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UnionOfIntervals {

	public int nthElement(int[] lowerBound, int[] upperBound, int n) {

		int min = 2000000000;
		List<Integer> globalList = new ArrayList<>();
		for (int i = 0; i < lowerBound.length; i++) {
			final List<Integer> list = new ArrayList<>();
			for (int number = lowerBound[i]; number <= upperBound[i]; number++) {
				list.add(number);
			}
			int listIndex = 0;
			int globalIndex = 0;
			final List<Integer> result = new ArrayList<>();
			while (listIndex < list.size() || globalIndex < globalList.size()) {
				int listVal = list.size() > listIndex ? list.get(listIndex) : 2000000000;
				int globalVal = globalList.size() > globalIndex ? globalList.get(globalIndex) : 2000000000;
				if (listVal < globalVal || listVal == globalVal) {
					result.add(listVal);
					listIndex++;
				} else if (globalVal < listVal) {
					result.add(globalVal);
					globalIndex++;
				}
			}
			globalList = result;
		}
		return globalList.get(n);
	}

	public int nthElement(int lower, int upper, int n) {
		if (upper < lower) {
			return -1;
		} else if (n == lower) {
			return lower;
		} else if (n == upper) {
			return upper;
		} else {
			int median = (lower + upper) / 2;
			if (median > n) {
				return nthElement(lower, median - 1, n);
			} else if (median < n) {
				return nthElement(median + 1, upper, n);
			} else {
				return median;
			}
		}
	}

	@Test
	public void test() {
		nthElement(new int[] { 1, 3 }, new int[] { 4, 5 }, 3);
	}
}