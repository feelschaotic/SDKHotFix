package com.feelschaotic.upload.utils
/**
 * @author feelschaotic
 * @create 2019/7/2.
 */
class EncryptUtil {

    String getRandomString(int length) {
        String chStr = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random()
        StringBuffer sb = new StringBuffer()
        for (int index = 0; index <= length - 1; index++) {
            int number = random.nextInt(62)
            sb.append(chStr.charAt(number))
        }
        return sb.toString()
    }
}