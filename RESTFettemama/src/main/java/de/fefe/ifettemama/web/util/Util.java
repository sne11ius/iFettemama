package de.fefe.ifettemama.web.util;

/**
 *
 * @author sne11ius
 */
public class Util {

    public static String sanitize(String input) {
        //return input.replace("\u0008", "").replace("\u001b", "");
        StringBuffer result = new StringBuffer(input);
        int idx = result.length();
        while (idx-- > 0) {
            if (result.charAt(idx) < 0x20 &&
                    result.charAt(idx) != 0x9 &&
                    result.charAt(idx) != 0xA &&
                    result.charAt(idx) != 0xD) {
                result.deleteCharAt(idx);
            }
        }
        return result.toString();
    }
}
