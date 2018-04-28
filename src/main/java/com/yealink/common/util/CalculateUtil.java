package com.yealink.common.util;

import java.util.Random;

public class CalculateUtil {

	public static String generateDigitRandomCode(int sum) {
		Random rd = new Random();
		String n = "";
		do {
			int iTemp = rd.nextInt();
			int getNum;
			if (iTemp != Integer.MIN_VALUE) {
				getNum = Math.abs(rd.nextInt()) % 10 + 48;
			} else {
				getNum = 40;
			}
			char num1 = (char) getNum;
			String dn = Character.toString(num1);
			n = n + dn;
		} while (n.length() < sum);
		return n;
	}

	public static String generateMixRandomCode(int sum) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < sum; i++) {
			int number = random.nextInt(62);

			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

}
