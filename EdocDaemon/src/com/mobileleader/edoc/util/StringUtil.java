package com.mobileleader.edoc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열함수유틸
 */
public class StringUtil
{

	/**
	 * isEmpty
	 *
	 * @param value
	 *            대상문자열
	 * @return 빈값여부(빈값:true)
	 */
	public static boolean isEmpty(String value) {
		if (value == null || "".equals(value.trim()))
			return true;
		return false;
	}

	/**
	 * isNotEmpty
	 *
	 * @param value
	 *            대상문자열
	 * @return 빈값여부(빈값:false)
	 */
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	/**
	 * nvl
	 *
	 * @param value
	 *            대상문자열
	 * @return 결과문자열
	 */
	public static String nvl(String value) {
		return (value == null ? "" : value.trim());
	}

	/**
	 * nvl
	 *
	 * @param value
	 *            대상문자열
	 * @param dftvalue
	 *            디폴트문자열
	 * @return 결과문자열
	 */
	public static String nvl(String value, String dftvalue) {
		if (value == null || "".equals(value.trim())) {
			return dftvalue;
		} else {
			return value.trim();
		}
	}

	/**
	 * nullToSpace
	 *
	 * @param value
	 *            대상문자열
	 * @return 결과문자열
	 */
	public static String nullToSpace(String value) {
		if (value == null || "".equals(value)) {
			return " ";
		} else {
			return value;
		}
	}

	/**
	 * asciiToString
	 *
	 * @param value
	 *            대상문자열
	 * @return 결과문자열
	 */
	public static String asciiToString(int value) {
		String retVal = "";
		retVal = Character.toString((char) value);

		return retVal;
	}

	/**
	 * cutString
	 *
	 * @param value
	 *            대상문자열
	 * @param size
	 *            결과문자열길이
	 * @return 결과문자열
	 */
	public static String cutString(String value, int size) {
		try {
			if (value == null) {
				return value;
			}
			int len = value.length();
			int cnt = 0;
			int index = 0;
			while ((index < len) && (cnt < size)) {
				if (value.charAt(index++) < 256)
					++cnt;
				else {
					cnt += 2;
				}
			}
			if (index < len)
				value = value.substring(0, index);
		} catch (Exception e) {
			return null;
		}
		return value;
	}

	/**
	 * value의 왼쪽에서 시작하여 length 만큼의 "결과문자열"을 리턴한다.<br>
	 * value의 길이가 length보다 클 경우 "결과문자열"은 value의 왼쪽에서 시작하여 length 크기 만큼 잘린 것이 된다.<br>
	 * value의 길이가 length보다 작을 경우 "결과문자열"은 value의 오른쪽(끝)에 모자라는 개수만큼 지정된 padValue가
	 * 추가되어 생성된다.
	 *
	 * @param value
	 *            대상문자열
	 * @param padValue
	 *            더해지는문자
	 * @param length
	 *            결과문자열길이
	 * 
	 * @return 결과문자열
	 */
	public static String padLeft(String value, char padValue, int length) {
		if (value == null)
			value = "";

		byte[] orgByte = value.getBytes();
		int orglength = orgByte.length;

		if (orglength < length) // add Padding character
		{
			byte[] paddedBytes = new byte[length];

			int padlength = length - orglength;

			for (int i = 0; i < padlength; i++) {
				paddedBytes[i] = (byte) padValue;
			}

			System.arraycopy(orgByte, 0, paddedBytes, padlength, orglength);

			return new String(paddedBytes);
		} else if (orglength > length) // 주어진 길이보다 남는다면, 주어진 길이만큼만 잘른다.
		{
			byte[] paddedBytes = new byte[length];
			System.arraycopy(orgByte, 0, paddedBytes, 0, length);
			return new String(paddedBytes);
		}

		return new String(orgByte);
	}

	/**
	 * padRight
	 *
	 * @param value
	 *            대상문자열
	 * @param padValue
	 *            더해지는문자
	 * @param length
	 *            결과문자열길이
	 * @return 결과문자열
	 */
	public static String padRight(String value, char padValue, int length) {
		if (value == null)
			value = "";

		byte[] orgByte = value.getBytes();
		int orglength = orgByte.length;

		if (orglength < length) // add Padding character
		{
			byte[] paddedBytes = new byte[length];

			System.arraycopy(orgByte, 0, paddedBytes, 0, orglength);
			while (orglength < length) {
				paddedBytes[orglength++] = (byte) padValue;
			}
			return new String(paddedBytes);
		} else if (orglength > length) // 주어진 길이보다 남는다면, 주어진 길이만큼만 잘른다
		{
			byte[] paddedBytes = new byte[length];
			System.arraycopy(orgByte, 0, paddedBytes, 0, length);
			return new String(paddedBytes);
		}

		return new String(orgByte);
	}

	/**
	 * padRight
	 *
	 * @param value
	 *            대상문자열
	 * @param padValue
	 *            더해지는문자
	 * @param length
	 *            결과문자열길이
	 * @return 결과문자열
	 */
	public static String padRight(String value, char padValue, int length, String charset) throws Exception
	{
		if (value == null)
			value = "";

		byte[] orgByte = value.getBytes(charset);
		int orglength = orgByte.length;

		if (orglength < length) // add Padding character
		{
			byte[] paddedBytes = new byte[length];

			System.arraycopy(orgByte, 0, paddedBytes, 0, orglength);
			while (orglength < length) {
				paddedBytes[orglength++] = (byte) padValue;
			}
			return new String(paddedBytes);
		} else if (orglength > length) // 주어진 길이보다 남는다면, 주어진 길이만큼만 잘른다
		{
			byte[] paddedBytes = new byte[length];
			System.arraycopy(orgByte, 0, paddedBytes, 0, length);
			return new String(paddedBytes);
		}

		return new String(orgByte);
	}

	/**
	 * ObjToString
	 *
	 * @param obj
	 *            대상객체
	 * @param returnVal
	 *            디폴트문자열
	 * @return 결과문자열
	 */
	public static String objToString(Object obj, String returnVal) {
		if (obj == null || obj.toString().equals(""))
			return returnVal;
		return obj.toString();
	}

	/**
	 * ObjToInt
	 *
	 * @param obj
	 *            대상객체
	 * @return 결과숫자
	 */
	public static int objToInt(Object obj) {
		String val = objToString(obj, "0");
		return Integer.parseInt(val);
	}

	/**
	 * isValidEmail
	 *
	 * @param email
	 *            대상문자열
	 * @return 이메일유효성여부
	 */
	public static boolean isValidEmail(String email) {
		Pattern p = Pattern.compile("^[-_\\w]+(\\.\\w+)*@[-_\\w]+\\.\\w+(\\.\\w+)*$");
		Matcher m = p.matcher(email);

		return m.matches();
	}

	/**
	 * convertStringToByte
	 * 
	 * @param str
	 * @return 결과 Byte
	 */
	public static byte[] convertStringToByte(String str) {
		try {
			return str.getBytes("EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * convertByteToString
	 * 
	 * @param strByte
	 * @return 결과 문자열
	 */
	public static String convertByteToString(byte[] strByte) {
		try {
			return new String(strByte, "EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * convertStringToByte
	 * 
	 * @param str
	 *            대상문자열
	 * @param len
	 *            버퍼사이즈
	 * @return
	 */
	public static byte[] convertStringToByte(String str, int len) {
		try {
			byte[] strByte = str.getBytes("EUC-KR");
			byte[] tempByte = new byte[len];
			for (int i = 0; i < len; i++) {
				if (strByte.length > i) {
					tempByte[i] = strByte[i];
				} else {
					tempByte[i] = 0x20;
				}
			}
			return tempByte;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 파일 사이즈를 스트링10자리 문자로 변환 (Brp 전송용)
	 * 
	 * @throws FileNotFoundException
	 */
	public static String fileSizeToStr10(File srcFile) throws Exception {
		String str = "";
		if (!srcFile.exists())
			throw new Exception("파일이 존재하지 않습니다.");
		str = String.format("%010d", srcFile.length());
		return str;
	}

	public static boolean isPhoneNumber(String phoneNoStr)
	{
		String regEx = "(\\d{2,3})\\-(\\d{3,4})\\-(\\d{4})";
		return Pattern.matches(regEx, phoneNoStr);
	}

	public static boolean isDigit(String str)
	{
		for(int i = 0; i < str.length(); ++i)
			if(str.charAt(i) < '0' || '9' < str.charAt(i))
				return false;
		return true;
	}
	
	/**
	 * base64 인코딩 여부 확인
	 */
	public static boolean isBase64Encoding(String encStr){
		
		String regex = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		if(encStr.matches(regex)){
			return true;
		}else{
			return false;
		}
	}

}
