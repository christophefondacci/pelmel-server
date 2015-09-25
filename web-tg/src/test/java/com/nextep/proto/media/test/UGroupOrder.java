package com.nextep.proto.media.test;

import java.util.Arrays;

import org.junit.Test;

public class UGroupOrder {

	public int[] findOrders(int N) {
		// Building list
		int count = 0;
		int[] numbers = new int[N];
		for (int i = 1; i < N; i++) {
			boolean hasCommonDivider = false;
			for (int divider = 2; divider <= i; divider++) {
				if ((i % divider) == 0 && (N % divider) == 0) {
					hasCommonDivider = true;
					break;
				}
			}
			if (!hasCommonDivider) {
				numbers[count++] = i;
			}
		}
		System.out.println("Numbers: " + Arrays.toString(numbers));
		// Computing orders
		int[] orders = new int[count];
		for (int i = 0; i < count; i++) {
			int order = 1;
			int currentValue = numbers[i];
			System.out.println("> Processing " + currentValue);
			while ((currentValue % N) != 1) {

				currentValue = (currentValue * numbers[i]);
				if (currentValue > N) {
					currentValue = currentValue % N;
				}
				order++;
				System.out
						.println("   >> Order " + order + ": " + currentValue + " % " + N + " = " + (currentValue % N));
			}
			orders[i] = order;
		}
		return orders;
	}

	@Test
	public void test() {
		findOrders(51);
	}
}