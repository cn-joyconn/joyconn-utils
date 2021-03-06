package cn.joyconn.utils.comm;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Eric.Zhang on 2017/7/26.
 */
public class KetamaNodeLocator {
    private TreeMap<Long, String> ketamaNodeMap;

    public KetamaNodeLocator(List<String> nodes, int numReps) {
        ketamaNodeMap = new TreeMap<Long, String>();
        //对所有节点，生成nCopies个虚拟结点
        for (String node : nodes) {
            //每四个虚拟结点为一组
            for (int i = 0; i < numReps / 4; i++) {
                //getKeyForNode方法为这组虚拟结点得到惟一名称
                byte[] digest = computeMd5(node + i);
                /** Md5是一个16字节长度的数组，将16字节的数组每四个字节一组，分别对应一个虚拟结点，这就是为什么上面把虚拟结点四个划分一组的原因*/
                for (int h = 0; h < 4; h++) {
                    long m = hash(digest, h);
                    ketamaNodeMap.put(m,node);
                }
            }
        }
    }

    public KetamaNodeLocator(Map<String, Integer> dictionary) {
        ketamaNodeMap = new TreeMap<Long, String>();
        //对所有节点，生成nCopies个虚拟结点
        for (String node : dictionary.keySet()) {
            int numReps = dictionary.get(node) / 4 <= 0 ? 1 : dictionary.get(node) / 4;
            //每四个虚拟结点为一组
            for (int i = 0; i < numReps; i++) {
                //getKeyForNode方法为这组虚拟结点得到惟一名称
                byte[] digest = computeMd5(node + i);
                /** Md5是一个16字节长度的数组，将16字节的数组每四个字节一组，分别对应一个虚拟结点，这就是为什么上面把虚拟结点四个划分一组的原因*/
                for (int h = 0; h < 4; h++) {
                    long m = hash(digest, h);
                    ketamaNodeMap.put(m, node);
                }
            }
        }
    }

    public String getPrimary(String k) {
        byte[] digest = computeMd5(k);
        String rv = getNodeForKey(hash(digest, 0));
        return rv;
    }

    private String getNodeForKey(long hash) {
        Long tempKey = hash ;
        try {
            //如果找到这个节点，直接取节点，返回
            if (!ketamaNodeMap.containsKey(tempKey)) {
                List<Long> tailMap = new ArrayList<Long>();

                //得到大于当前key的那个子Map，然后从中取出第一个key，就是大于且离它最近的那个key 说明详见: http://www.javaeye.com/topic/684087
                for( Long key : ketamaNodeMap.keySet()){
                    if(key>tempKey){
                        tailMap.add(key);
                        break;
                    }
                }

                if (tailMap.size()>0) {
                    tempKey = tailMap.get(0);
                }
                else {
                    tempKey = ketamaNodeMap.firstEntry().getKey();
                }
            }
            String rv = ketamaNodeMap.get(tempKey);
            return rv;
        } catch(Exception ex)
        {

        }
        return "";
    }

    public static long hash(byte[] digest, int nTime) {
        long rv = ((long) (digest[3 + nTime * 4] & 0xFF) << 24)
                | ((long) (digest[2 + nTime * 4] & 0xFF) << 16)
                | ((long) (digest[1 + nTime * 4] & 0xFF) << 8)
                | ((long) digest[0 + nTime * 4] & 0xFF);

        return rv & 0xffffffffL; /* Truncate to 32-bits */
    }

    /**
     * Get the md5 of the given key.
     */

    public static byte[] computeMd5(String k) {
        byte[] strEncoded = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(k.getBytes());
            strEncoded = md5.digest();
        } catch (Exception ex) {

        }
        return strEncoded;
    }
}