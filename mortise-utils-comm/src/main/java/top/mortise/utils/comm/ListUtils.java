package top.mortise.utils.comm;
import java.util.List;

/**
 * Created by Eric.Zhang on 2017/4/19.
 */
public class ListUtils {
    public static String join(List<Integer> list){
       return join(list,",");
    }
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
}
