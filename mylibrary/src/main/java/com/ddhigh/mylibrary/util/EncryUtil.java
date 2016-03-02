package com.ddhigh.mylibrary.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/2/29 0029
 */
public class EncryUtil {
    /**
     * sha1加密
     *
     * @param str
     * @return
     */
    public static String sha1(String str) {
        byte[] digest = null;
        try {
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            alga.update(str.getBytes());
            digest = alga.digest();
            return byte2hex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * md5加密
     *
     * @param str
     * @return
     */
    public static String md5(String str) {
        byte[] digest = null;
        try {
            MessageDigest alga = MessageDigest.getInstance("MD5");
            alga.update(str.getBytes());
            digest = alga.digest();
            return byte2hex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (byte aB : b) {
            stmp = (Integer.toHexString(aB & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
