package cn.joyconn.utils.encrypt;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /***
     * MD5加密 生成32位md5码
     * @param inStr 待加密字符串
     * @return 返回32位md5码
     */
    //对MD5算法简要的叙述可以为：MD5以512位分组来处理输入的信息，且每一分组又被划分为16个32位子分组，经过了一系列的处理后，算法的输出由四个32位分组组成，将这四个32位分组级联后将生成一个128位散列值。
    public static String md5Encode(String inStr) throws Exception {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    public static String md5EncodeByMessageDigest(String str) throws NoSuchAlgorithmException {
        String encode = str;
        StringBuilder stringbuilder = new StringBuilder();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(encode.getBytes());
        byte[] strEncoded = md5.digest();
        for (int i = 0; i < strEncoded.length; i++) {
            if ((strEncoded[i] & 0xff) < 0x10) {
                stringbuilder.append("0");
            }
            stringbuilder.append(Long.toString(strEncoded[i] & 0xff, 16));
        }
        return stringbuilder.toString();
    }
    public String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {

        final char[] hexDigits = "0123456789ABCDEF".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i=0; i<bytes.length; i++) {
            ret.append(hexDigits[(bytes[i] >> 4) & 0x0f]);
            ret.append(hexDigits[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }
//    /**
//     * 测试主函数
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String args[]) throws Exception {
//        String str = new String("000000");
//        System.out.println("原始：" + str);
//        System.out.println("MD5后：" + md5Encode(str));
//    }
}

