package cn.joyconn.utils.comm;

import java.math.BigInteger;
import com.google.common.primitives.Longs;


/**
 * @author eric.zsp
 */
public class TypeConvert {

    /**
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexFun(byte[] bytes) {
        try {
            StringBuilder buf = new StringBuilder(bytes.length * 2);
            // 使用String的format方法进行转换
            for (byte b : bytes) {
                buf.append(String.format("%02x", new Integer(b & 0xff)));
            }

            return buf.toString();
        } catch (Exception ex) {
            return "";
        }
    }
    // public static String bytesToHexFun(byte[] bytes) {
    //     try {
    //         BigInteger bi = new BigInteger(1, bytes);
    //         return String.format("%0" + (bytes.length << 1) + "X", bi);
    //     } catch (Exception ex) {
    //         return "";
    //     }
    // }
    public static int hexCharToInt(char c) {
        if ((c >= '0') && (c <= '9')) {
            return (c - '0');
        }
        if ((c >= 'A') && (c <= 'F')) {
            return (c - 'A' + 10);
        }
        if ((c >= 'a') && (c <= 'f')) {
            return (c - 'a' + 10);
        }

        throw new RuntimeException("invalid hex char '" + c + "'");
    }
  
    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        try {
            if (str == null || "".equals(str.trim())) {
                return new byte[0];
            }
            int len = str.length(); 
            byte[] bytes = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                bytes[(i / 2)] = (byte) (Character.digit(str.charAt(i), 16) << 4 |  Character.digit(str.charAt(i+1), 16));
            }

            return bytes;
        } catch (Exception ex) {
            return new byte[0];
        }
    }


    public static int getUint16(int i) {
        return i & 0x0000ffff;
    }

    public static long getUint32(long l) {
        return l & 0x00000000ffffffff;
    }


    public static double getHexValue(String str, String dataType, double rate) {
        // byte[] bytes =null; 
        Integer intValue = 0;
        Long longValue = 0L;
        Double doubleValue = 0.0;
        Float floatValue = 0f;
        StringBuffer sb = null;
        BigInteger bigInteger=  null;
        switch (dataType.toLowerCase()) {
            case "float":
                 floatValue = Float.intBitsToFloat(new BigInteger(str.trim() ,16).intValue());
                
                 return floatValue * rate;
            case "double":
                // bytes=toBytes(str);
                // doubleValue = bytes2Double(bytes);
                longValue = Long.valueOf(str.trim(),16);
                doubleValue = Double.longBitsToDouble(longValue);
                return doubleValue * rate;
            case "uint64":
                longValue = Long.parseUnsignedLong(str, 16);
                return longValue * rate;
            case "int64":
                longValue = Long.parseLong(str, 16);
                return longValue * rate;
            case "uint32":
                longValue = Long.parseLong(str, 16);
                return longValue * rate;
            case "int32":                
                bigInteger =new BigInteger(str,16);
                intValue = bigInteger.intValue();
                return intValue.intValue() * rate;
            case "int32_tn":     
                        
                    bigInteger =new BigInteger(str,16);
                    if(bigInteger.longValue()>2147483647){
                        intValue =Long.valueOf( bigInteger.longValue()-2147483648l).intValue()*-1;
                    }else{
                        intValue = bigInteger.intValue();
                    }                    
                    return intValue.intValue() * rate;
            case "uint16":
                intValue = Integer.parseInt(str, 16);
                return getUint16(intValue) * rate;
            case "int16":
                intValue = Integer.parseInt(str, 16);
                return intValue.shortValue() * rate;
            case "int16_tn": 
                intValue = Integer.parseInt(str, 16);
                if(intValue>32767){
                    intValue =Integer.valueOf(intValue-32768).intValue()*-1;
                }else{
                    intValue = intValue.intValue();
                }                    
                return intValue.intValue() * rate;
            // case "bcd"://DL645 string 此处为兼容太原会展中心项目
            //     // sb = new StringBuffer();
            //     // tempResult="";
            //     // for(int a=str.length();a>0;a-=2){
                    
            //     //     intValue = Integer.parseInt(str.substring(a-2,a), 16);
            //     //     tempResult =  Integer.toHexString(intValue);
                    
            //     //     sb.append(getOString(tempResult, 2));
            //     // }
            //     // tempResult=sb.toString();
            //     return Long.valueOf(str) * rate;
            case "byte":  
                intValue = Integer.parseInt(str, 16);             
                return intValue.byteValue() * rate;          
            default:
                return 0;
        }
    }

    public static double getHexValueDL645(String str, String dataType, double rate) {
        // byte[] bytes =null; 
        Integer intValue = 0;
        Long longValue = 0L;
        Double doubleValue = 0.0;
        Float floatValue = 0f;
        StringBuffer sb = null;
        switch (dataType.toLowerCase()) {
            case "float":
                 floatValue = Float.intBitsToFloat(new BigInteger(str.trim() ,16).intValue());
                
                 return floatValue * rate;
            case "double":
                // bytes=toBytes(str);
                // doubleValue = bytes2Double(bytes);
                longValue = Long.valueOf(str.trim(),16);
                doubleValue = Double.longBitsToDouble(longValue);
                return doubleValue * rate;
            case "uint64":
                longValue = Long.parseUnsignedLong(str, 16);
                return longValue * rate;
            case "int64":
                longValue = Long.parseLong(str, 16);
                return longValue * rate;
            case "uint32":
                longValue = Long.parseLong(str, 16);
                return longValue * rate;
            case "int32":                
                BigInteger bigInteger =new BigInteger(str,16);
                intValue = bigInteger.intValue();
                return intValue.intValue() * rate;
            case "uint16":
                intValue = Integer.parseInt(str, 16);
                return getUint16(intValue) * rate;
            case "int16":
                intValue = Integer.parseInt(str, 16);
                return intValue.shortValue() * rate;
            case "byte":  
                intValue = Integer.parseInt(str, 16);             
                return intValue.byteValue() * rate;
            case "string645"://DL645 string 此处为兼容太原会展中心项目
                sb = new StringBuffer();
                String tempResult="";
                for(int a=str.length();a>0;a-=2){
                    
                    intValue = Integer.parseInt(str.substring(a-2,a), 16)-51;
                    tempResult =  Integer.toHexString(intValue);
                    
                    sb.append(getOString(tempResult, 2));
                }
                tempResult=sb.toString();
                return Double.parseDouble(tempResult) * rate;
                // if(tempResult.equalsIgnoreCase("7e")){
                //     //对7e字符串特殊处理
                //     return 1 * rate ;
                // }else{
                //     return Double.parseDouble(tempResult) * rate;
                // }
            case "time645"://DL645 time  (yyMMddHHmm) //DL645 string 此处为兼容太原会展中心项目
                sb = new StringBuffer();
                // String tempResult="";
                for(int a=str.length();a>0;a-=2){                        
                    intValue = Integer.parseInt(str.substring(a-2,a), 16)-51;
                    tempResult =  Integer.toHexString(intValue);                    
                    sb.append(getOString(tempResult, 2));
                }
                longValue = Long.valueOf(sb.toString());
                return longValue;     
            case "bcd"://DL645 string 此处为兼容太原会展中心项目
                return Long.valueOf(str) * rate;
                // sb = new StringBuffer();
                // tempResult="";
                // for(int a=str.length();a>0;a-=2){
                    
                //     // intValue = Integer.parseInt(str.substring(a-2,a), 16);
                //     intValue = Integer.parseInt(str.substring(a-2,a));
                //     tempResult =  Integer.toHexString(intValue);
                    
                //     sb.append(getOString(tempResult, 2));
                // }
                // tempResult=sb.toString();
                // return Long.valueOf(str) * rate;
                // if(tempResult.equalsIgnoreCase("7e")){
                //     //对7e字符串特殊处理
                //     return 1 * rate ;
                // }else{
                //     return Double.parseDouble(tempResult) * rate;
                // }
            default:
                return 0;
        }
    }
    public static double getBitValue(String str, int numberIndex, double rate) {
        String convertStr = str;
        boolean needAppendLeft = false;
        if (convertStr.length() % 2 != 0) {
            convertStr = "0" + convertStr;
            needAppendLeft = true;
        }
        String binaryString = hexString2binaryString(convertStr);
        if (needAppendLeft) {
            binaryString = binaryString.substring(4);
        }

        int result = binaryString.charAt(numberIndex) == '0' ? 0 : 1;
        if (rate < 0) {
            result = result == 0 ? 1 : 0;
        }
        return result;
    }

    public static double getBitValue_DL645(String str, int numberIndex, double rate) {
        StringBuffer sb = new StringBuffer();
        Integer intValue = 0;
        String tempResult="";
        for(int a=str.length();a>0;a-=2){                        
            intValue = Integer.parseInt(str.substring(a-2,a), 16)-51;
            tempResult =  Integer.toHexString(intValue);                    
            sb.append(getOString(tempResult, 2));
        }
        String convertStr = sb.toString();
        boolean needAppendLeft = false;
        if (convertStr.length() % 2 != 0) {
            convertStr = "0" + convertStr;
            needAppendLeft = true;
        }
        String binaryString = hexString2binaryString(convertStr);
        if (needAppendLeft) {
            binaryString = binaryString.substring(4);
        }

        int result = binaryString.charAt(numberIndex) == '0' ? 0 : 1;
        if (rate < 0) {
            result = result == 0 ? 1 : 0;
        }
        return result;
    }

    public static String getHexString(Double value,String dataType, double rate) {
        byte[] bytes =null;   
        Integer intValue = 0;
        Long longValue = 0L;
        // Double doubleValue = 0.0;
        // Float floatValue = 0f;   
        // Short shortValue = 0;    
        value = value/ rate;
        String tempResult = "" ;
        String tempResult1 = "" ;
        switch (dataType.toLowerCase()) {
            case "float":
                // floatValue = value.floatValue();
                // tempResult = Float.toHexString(floatValue);
                // floatValue=doubleValue.floatValue();
                tempResult= Integer.toHexString(Float.floatToIntBits(value.floatValue()));
                tempResult = getOString(tempResult, 8);
                tempResult = tempResult.substring(tempResult.length()-8);
                return tempResult;
                // return  Integer.toHexString(Float.floatToIntBits(floatValue));
                // intValue=Float.floatToIntBits(floatValue);
                // bytes = intToByte(intValue);
                // break;
            case "double":
                tempResult= Long.toHexString(Double.doubleToLongBits(value));
                tempResult = getOString(tempResult, 16);
                tempResult = tempResult.substring(tempResult.length()-16);
                // doubleValue = value;       
                // // doubleValue = Double.valueOf(value.toString());
                // bytes = double2Bytes(doubleValue);                
                // tempResult = bytesToHexFun(bytes);
                // // tempResult = Double.toHexString(doubleValue); 
                // // bytes = double2Bytes(doubleValue);
                // tempResult = getOString(tempResult, 16);
                // tempResult = tempResult.substring(tempResult.length()-16);
                return tempResult;
                // break;
            case "uint64":   
                //此类型可能会溢出
                longValue = value.longValue();
                bytes = Longs.toByteArray(longValue);
                tempResult =bytesToHexFun(bytes);
                tempResult = tempResult.substring(tempResult.length()-16);
                return tempResult;
            case "int64":    
                longValue = value.longValue();
                // bytes = Longs.toByteArray(longValue);
                
                tempResult = Long.toHexString(longValue);
                tempResult = getOString(tempResult, 16);
                tempResult = tempResult.substring(tempResult.length()-16);
                return tempResult;

                // break;
            case "uint32":            
                value.intValue();
                longValue =  value.longValue();
                tempResult = Long.toHexString(longValue);
                // return tempResult.substring(4);
                tempResult = getOString(tempResult, 8);
                tempResult = tempResult.substring(tempResult.length()-8);
                return tempResult;
                // break;
            case "int":
            case "int32":
                intValue=value.intValue();
                
                tempResult = Integer.toHexString(intValue);
                tempResult = getOString(tempResult, 8);
                tempResult = tempResult.substring(tempResult.length()-8);
                return tempResult;
                // bytes = intToByte(intValue);
                // break;
            case "uint16":            
                intValue=value.intValue();
                
                tempResult = Integer.toHexString(intValue);
                tempResult = getOString(tempResult, 4);
                tempResult = tempResult.substring(tempResult.length()-4);
                return tempResult;
                // break;
            case "int16":            
                
                 intValue=value.intValue();
                
                tempResult = Integer.toHexString(intValue);
                tempResult = getOString(tempResult,4);
                tempResult = tempResult.substring(tempResult.length()-4);
                return tempResult;
            case "byte":  
                intValue=value.intValue();               
                tempResult = Integer.toHexString(intValue);
                tempResult = getOString(tempResult,2);
                tempResult = tempResult.substring(tempResult.length()-2);
                return tempResult;
                // break;      
            case "string645"://DL645 string 此处为兼容太原会展中心项目
                longValue=value.longValue();   
                tempResult = Long.toHexString(longValue);
                if(tempResult.length()%2==1){
                    tempResult="0"+tempResult;
                }
                StringBuffer sb = new StringBuffer();
                for(int a=tempResult.length();a>0;a-=2){
                    
                    intValue = Integer.parseInt(tempResult.substring(a-2,a), 16)+51;
                    tempResult =  Integer.toHexString(intValue);
                    
                    sb.append(getOString(tempResult, 2));
                }
                tempResult=sb.toString();
                return tempResult;
                // if(tempResult.equalsIgnoreCase("7e")){
                //     //对7e字符串特殊处理
                //     return 1 * rate ;
                // }else{
                //     return Double.parseDouble(tempResult) * rate;
                // }
            // case "time645"://DL645 time  (yyMMddHHmm) //DL645 string 此处为兼容太原会展中心项目
            //     sb = new StringBuffer();
            //     // String tempResult="";
            //     for(int a=str.length();a>0;a-=2){                        
            //         intValue = Integer.parseInt(str.substring(a-2,a), 16)-51;
            //         tempResult =  Integer.toHexString(intValue);                    
            //         sb.append(getOString(tempResult, 2));
            //     }
            //     longValue = Long.valueOf(sb.toString());
            //     return longValue;     
            case "bcd"://DL645 string 此处为兼容太原会展中心项目
                intValue=value.intValue();   
                tempResult = Integer.toHexString(intValue);
                return tempResult;     
            default:
                return null;
        }
        // if(bytes==null){
        //     return "";
        // }else{
        //     return bytesToHexFun(bytes);
        // }
    }

    public static String getBitString(int value,int numberIndex, int length) {
        if(numberIndex>=length){
            return "";
        }
        String convertStr = getOString("0",length);
        if(length>(numberIndex+1)){
            convertStr = convertStr.substring(0, numberIndex)+value+convertStr.substring(numberIndex+1);
        }else{
            convertStr = convertStr.substring(0, numberIndex)+value;
        }
       
        String result = binaryString2hexString(convertStr);
       
        return result;
    }
    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            return "";
        }
        String bString = "", tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
            bString += tmp.substring(tmp.length() - 4);
        }
        return bString;
    }

    public static String binaryString2hexString(String bString) {
        if (bString == null || "".equals(bString) || bString.length() % 8 != 0) {
            return "";
        }
        StringBuffer tmp = new StringBuffer();
        int iTmp = 0;
        for (int i = 0; i < bString.length(); i += 4) {
            iTmp = 0;
            for (int j = 0; j < 4; j++) {
                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
            }
            tmp.append(Integer.toHexString(iTmp));
        }
        return tmp.toString();
    }

    public static short byte2short(byte[] bytes) {
        byte high = bytes[0];
        byte low = bytes[1];
        short z = (short) (((high & 0x00FF) << 8) | (0x00FF & low));
        return z;

    }

    public static byte[] short2Byte(short x) {
        byte high = (byte) (0x00FF & (x >> 8));//定义第一个byte
        byte low = (byte) (0x00FF & x);//定义第二个byte
        byte[] bytes = new byte[2];
        bytes[0] = high;
        bytes[1] = low;
        return bytes;
    }


    /** 
     * int到字节数组的转换. 
     */  
    public static byte[] intToByte(int number) {  
        int temp = number;  
        byte[] b = new byte[4];  
        for (int i = 0; i < b.length; i++) {  
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位  
            temp = temp >> 8;// 向右移8位  
        }  
        return b;  
    }  
    
    /** 
     * 字节数组到int的转换. 
     */  
    public static int byteToInt(byte[] b) {  
        int s = 0;  
        int s0 = b[0] & 0xff;// 最低位  
        int s1 = b[1] & 0xff;  
        int s2 = b[2] & 0xff;  
        int s3 = b[3] & 0xff;  
        s3 <<= 24;  
        s2 <<= 16;  
        s1 <<= 8;  
        s = s0 | s1 | s2 | s3;  
        return s;  
    } 
    /** 
     * 字节数组到double的转换. 
     */  
    public static double getDouble(byte[] b) {    
        long m;    
        m = b[0];    
        m &= 0xff;    
        m |= ((long) b[1] << 8);    
        m &= 0xffff;    
        m |= ((long) b[2] << 16);    
        m &= 0xffffff;    
        m |= ((long) b[3] << 24);    
        m &= 0xffffffffl;    
        m |= ((long) b[4] << 32);    
        m &= 0xffffffffffl;    
        m |= ((long) b[5] << 40);    
        m &= 0xffffffffffffl;    
        m |= ((long) b[6] << 48);    
        m &= 0xffffffffffffffl;    
        m |= ((long) b[7] << 56);    
        return Double.longBitsToDouble(m);    
    }  
    /** 
     * double到字节数组的转换. 
     */  
    public static byte[] doubleToByte(double num) {    
        byte[] b = new byte[8];    
        long l = Double.doubleToLongBits(num);    
        for (int i = 0; i < 8; i++) {    
            b[i] = new Long(l).byteValue();    
            l = l >> 8;    
        }  
        return b;  
    }  
    /** 
     * long类型转成byte数组 
     */  
    public static byte[] longToByte(long number) {  
        long temp = number;  
        byte[] b = new byte[8];  
        for (int i = 0; i < b.length; i++) {  
            b[i] = new Long(temp & 0xff).byteValue();// 将最低位保存在最低位 temp = temp  
                                                        // >> 8;// 向右移8位  
        }  
        return b;  
    }  
    
    /** 
     * 字节数组到long的转换. 
     */  
    public static long byteToLong(byte[] b) {  
        long s = 0;  
        long s0 = b[0] & 0xff;// 最低位  
        long s1 = b[1] & 0xff;  
        long s2 = b[2] & 0xff;  
        long s3 = b[3] & 0xff;  
        long s4 = b[4] & 0xff;// 最低位  
        long s5 = b[5] & 0xff;  
        long s6 = b[6] & 0xff;  
        long s7 = b[7] & 0xff;  
    
        // s0不变  
        s1 <<= 8;  
        s2 <<= 16;  
        s3 <<= 24;  
        s4 <<= 8 * 4;  
        s5 <<= 8 * 5;  
        s6 <<= 8 * 6;  
        s7 <<= 8 * 7;  
        s = s0 | s1 | s2 | s3 | s4 | s5 | s6 | s7;  
        return s;  
    }  
    public static float bytes2Float(byte[] arr) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			value |= ((int) (arr[i] & 0xff)) << (4 * i);
		}
		return Float.intBitsToFloat(value);
    }

    public static double bytes2Double(byte[] arr) {
		long value = 0;
		for (int i = 0; i < 8; i++) {
			value |= ((long) (arr[i] & 0xff)) << (8 * i);
		}
		return Double.longBitsToDouble(value);
    }
    
    public static byte[] double2Bytes(double d) {
        long l = Double.doubleToRawLongBits(d);
        byte[] b = new byte[8];
        b[0] = (byte) (l & 0x000000000000FFL);

        b[1] = (byte) ((l & 0x0000000000FF00L) >> 8);

        b[2] = (byte) ((l & 0x0000000000FF0000L) >> 16);

        b[3] = (byte) ((l & 0x00000000FF000000L) >> 24);

        b[4] = (byte) ((l & 0x000000FF00000000L) >> 32);

        b[5] = (byte) ((l & 0x0000FF0000000000L) >> 40);

        b[6] = (byte) ((l & 0x00FF000000000000L) >> 48);

        b[7] = (byte) ((l & 0xFF00000000000000L) >> 56);
        return b;
    }

    //头部补0
    public static String getOString(String s,int lenth ){
        String result = s;
        while(result.length()<lenth ){
            result = "0"+result;
        }
        return result;
    }
    //尾部加0
    public static String getOString1(String s,int lenth ){
        String result = s;
        while(result.length()<lenth ){
            result = result+"0";
        }
        return result;
    }


    // private static String lockAddress(String lockAddress) {
         
    //     StringBuffer s1 = new StringBuffer(lockAddress);
    //     int index;
    //     for (index = 2; index < s1.length(); index += 3) {
    //         s1.insert(index, ',');
    //     }
    //     String[] array = s1.toString().split(",");
    //     String[] swapOrder = swapOrder(array);
    //     StringBuffer s2 = new StringBuffer();
    //     for (String string :swapOrder ) {
    //         s2.append(string);
    //     }
    //     return s2.toString();
         
    // }
     
     
     public static String[] swapOrder(String[] arr){
         int length = arr.length;
         for(int i=0;i<length/2;i++){ //只需一个循环，数组的一半就可以，第一个和最后一个交换，第二个和倒数第二个交换。。。
             String temp = arr[i];
             arr[i] = arr[length-1-i];
             arr[length-1-i] = temp;
             }
         return arr;
    }
}
