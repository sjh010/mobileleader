package com.mobileleader.edoc.test;

import java.net.UnknownHostException;

public class Test {

	public static void main(String[] args) throws UnknownHostException {
		
		String value = "abccdscript";
		
		value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "$#41;");
		value = value.replaceAll("'", "&#39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
		value = value.replaceAll("script", "");
		
		System.out.println("Value : " + value);
		
	}
}
