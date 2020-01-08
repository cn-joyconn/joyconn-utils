package cn.joyconn.utils.comm;
import java.util.List;

/**
 * Created by Eric.Zhang on 2017/4/19.
 * @author  eric.zsp
 */
public class ListUtils {
    /**
     * list转换为用逗号分隔的字符串
     * @param list
     * @return
     */
    public static String join(List<Integer> list){
       return join(list,",");
    }

    /**
     * list转换为带分隔符的字符串
     * @param list
     * @param splitChar 分隔符
     * @return
     */
    public static String join(List<Integer> list,String splitChar){
        StringBuffer sb=new StringBuffer();
        if(list!=null){
            for(int i : list){
                sb.append(splitChar);
                sb.append(String.valueOf(i));
            }
        }
        if(sb.length()>0){
            return sb.substring(splitChar.length());
        }
        return "";
    }

    /**
     * list去重
     * @param list
     * @return
     */
    public   static   List  removeDuplicate(List list)  {
        for  ( int  i  =   0 ; i  <  list.size()  -   1 ; i ++ )  {
            for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )  {
                if  (list.get(j).equals(list.get(i)))  {
                    list.remove(j);
                }
            }
        }
        return list;
    }
}
