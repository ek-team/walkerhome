package com.pharos.walker.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class DataTransformUtil {
    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public static String bytes2HexString(final byte[] bytes) {
        if (bytes == null) return "";
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }
    public static ArrayList<String> conversionArrayData(String data, int len) {
        ArrayList<String> strings = new ArrayList<>();
        while (data.length() >= len) {
            strings.add(data.substring(0, len));
            data = data.substring(len);
        }
        return strings;
    }
    /**
     * Hex string to bytes.
     * <p>e.g. hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }</p>
     *
     * @param hexString The hex string.
     * @return the bytes
     */
    public static byte[] hexString2Bytes(String hexString) {
        if (isSpace(hexString)){
            return null;
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Int(hexBytes[i]) << 4 | hex2Int(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Int(final char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }
    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    /**
     * 校验码 — 蓝牙
     * CheckSum = ~(ID+Length+ Parameter1+……..+Parameter N)
     * 如果计算结果大于255，则后面字节的值设为校验码的值。
     *
     * @param content
     * @return
     */
    public static String checkSum(byte[] content) {
        int result = 0;
        for (int i = 0; i < content.length; i++) {
            result += content[i];
        }
        return Byte2Hex((byte) ((~result) & 0xFF));
    }
    //1字节转2个Hex字符
    public static String Byte2Hex(byte inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }
    public static String toHexString(byte b) {
        return (((b & 0xff) < 0x10) ? "0" : "") + Integer.toString(b & 0xff, 16).toUpperCase(Locale.ENGLISH);
    }
    //Hex字符串转int
    public static int HexToInt(String inHex){
        int result = 0;
        try {
            result = Integer.parseInt(inHex, 16);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private static long lastClickTime;

    public static boolean setTimeInterval(int INTERVAL) {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= INTERVAL) {
            flag = true;
            lastClickTime = curClickTime;
        }
        return flag;
    }
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        StringBuffer sb = null;
        while (strLen < strLength) {
            sb = new StringBuffer();
//            sb.append("0").append(str);// 左补0
            sb.append(str).append("0");//右补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }
    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(hexDigits[bit]);
            bit = bs[i] & 0x0f;
            sb.append(hexDigits[bit]);
        }
        return sb.toString().trim();
    }
    //1字节转2个Hex字符
    public static String Byte2Hex(int inByte) {
        return String.format("%02x", inByte).toUpperCase();
    }
    public static List<String> stringToList(String string){
        List<String> stringList = new ArrayList<>();
        String dealResult = trimBothEndsChars(string,"\\[","\\]");
        String [] strings = dealResult.split(",");
        for (int i = 0; i < strings.length; i++){
            stringList.add(i,strings[i]);
        }
        return stringList;
    }
    /**
     * 去除字符串首尾两端指定的字符
     * */
    public static String trimBothEndsChars(String srcStr, String startSp,String endSp) {
        String regex = "^" + startSp + "*|" + endSp + "*$";
        return srcStr.replaceAll(regex, "");
    }
}
