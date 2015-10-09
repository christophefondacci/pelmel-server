package com.nextep.proto.media.test;

import org.junit.Test;

public class FairWorkload {

	public int getMostWork(int[] folders, int workers) {
		return getMinMostWork(folders, workers, 0);
	}

	public int getMinMostWork(int[] folders, int workers, int start) {
		int minMostWork = 9999;
		for (int i = start; i < folders.length - workers + 1; i++) {
			int work = getWork(folders, start, i);
			int currentWork = work;
			int otherWork = -1;
			if (workers > 1) {
				otherWork = getMinMostWork(folders, workers - 1, i + 1);
				currentWork = Math.max(currentWork, otherWork);
			}
			minMostWork = Math.min(minMostWork, currentWork);
			System.out.println("   * " + start + "->" + i + ": work=" + work + " / otherWork=" + otherWork + " | MIN="
					+ minMostWork);
		}
		return minMostWork;
	}

	public int getWork(int[] folders, int start, int end) {
		int work = 0;
		for (int i = start; i <= end; i++) {
			work += folders[i];
		}
		return work;
	}

	public int getMostWork(int[] folders, int workers, int start) {
		int mostWork = 0;
		for (int i = start; i < folders.length - workers + 1; i++) {
			int work = getWork(folders, start, i);
			mostWork = Math.max(work, mostWork);
			if (workers > 1) {
				int otherMostWork = getMostWork(folders, workers - 1, i + 1);
				mostWork = Math.max(mostWork, otherMostWork);
			}
		}
		return mostWork;
	}

	@Test
	public void test() {
		int work = getMostWork(new int[] { 10, 20, 30, 40, 50 }, 3); // , 40,
																		// 50,
																		// 60,
																		// 70,
																		// 80,
																		// 90 },
																		// 3);
		System.out.println(work);
	}
}