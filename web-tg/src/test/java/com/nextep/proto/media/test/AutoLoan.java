package com.nextep.proto.media.test;

import org.junit.Test;

public class AutoLoan {

	public double interestRate(double price, double monthlyPayment, int loanTerm) {
		return interestRate(0.0f, 0.1f, price, monthlyPayment, loanTerm);
	}

	public double interestRate(double minInterest, double maxInterest, double price, double monthlyPayment,
			int loanTerm) {
		double interest = (minInterest + maxInterest) / 2;
		double val = price;
		for (int i = 0; i < loanTerm; i++) {
			val = val + val * interest - monthlyPayment;
		}
		System.out.println("I=" + interest + " -> VAL=" + val);

		if (val < -0.00001f) {
			return interestRate(interest, maxInterest, price, monthlyPayment, loanTerm);
		} else if (val > 0.00001f) {
			return interestRate(minInterest, interest, price, monthlyPayment, loanTerm);
		} else {
			return interest;
		}
	}

	@Test
	public void test() {
		interestRate(2000.0f, 510.0f, 4);
	}
}