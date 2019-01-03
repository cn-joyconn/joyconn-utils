/**
 * Created by Eric.Zhang on 2017/3/13.
 */
package top.mortise.utils.uniqueID;
import top.mortise.utils.encrypt.AesUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Eric.Zhang on 2016/12/29.
 *  用户令牌
 */
public class LoginTokenID implements  Serializable{
    static String  token_ekey = "I?vek0w\\";
    String uid;
    String pwd;
    int timestamp;
    int machine;
    short pid;
    int increment;
    top.mortise.utils.uniqueID.DBObjectID uniqueObjectID;

    public String getUid(){
        return  uid;
    }
    public top.mortise.utils.uniqueID.DBObjectID getUniqueObjectID(){
        return uniqueObjectID;
    }
    public  int getTimestamp(){
        return timestamp;
    }
    public  int getMachine(){
        return machine;
    }
    public  int getIncrement(){
        return increment;
    }
    public  short getPid(){
        return pid;
    }
    public  String getPwd(){
        return pwd;
    }
    public  Date getDate(){
        return getUniqueObjectID().getDate();
    }

    /**
     *  由一个令牌字符串转换成令牌对象
     * @param tokenStr 令牌字符串
     * Created by Eric.Zhang on 2016/12/29.
     */
    public LoginTokenID(String tokenStr){
        if (tokenStr == null||tokenStr.length()<1)
        {
            return;
        }
        //解析令牌

        try
        {
            tokenStr = AesUtils.decryptStr(tokenStr,token_ekey);
        }
        catch (Exception e)
        {
            return;
        }
        String[] stemp = tokenStr.split("\f" );
        try
        {
            if (stemp.length == 4)
            {
                uid = stemp[0];
                pwd = stemp[1];
                uniqueObjectID = new top.mortise.utils.uniqueID.DBObjectID(stemp[2]);
                timestamp = Integer.parseInt(stemp[3]);
            }
            else if (stemp.length == 3)
            {
                uid = stemp[0];
                pwd = stemp[1];
                uniqueObjectID = new top.mortise.utils.uniqueID.DBObjectID(stemp[2]);
                timestamp = uniqueObjectID.getTimestamp();//.GetTimestampFromDateTime(DateTime.UtcNow);
            }

        }
        catch (Exception e)
        {
            return;
        }
        if (uniqueObjectID == null)
        {
            return;
        }
        machine = uniqueObjectID.getMachineIdentifier();
        pid = uniqueObjectID.getProcessIdentifier();
        increment = uniqueObjectID.getCounter();


    }

    /**
     *   由账号、密码生成一个令牌
     * @param userid 账号
     * @param password 密码
     * Created by Eric.Zhang on 2016/12/29.
     */
    public LoginTokenID(String userid, String password)
    {
        uniqueObjectID = new top.mortise.utils.uniqueID.DBObjectID();
        timestamp = uniqueObjectID.getTimestamp();
        machine = uniqueObjectID.getMachineIdentifier();
        pid = uniqueObjectID.getProcessIdentifier();
        increment = uniqueObjectID.getCounter();
        uid = userid;
        pwd = password;
    }

    /**
     *  由账号、密码、唯一ID生成一个令牌
     * @param userid 账号
     * @param password 密码
     * @param oldUniqueObjectID 唯一ID
     * Created by Eric.Zhang on 2016/12/29.
     */
    public LoginTokenID(String userid, String password, top.mortise.utils.uniqueID.DBObjectID oldUniqueObjectID)
    {
        uniqueObjectID = oldUniqueObjectID;
        timestamp = uniqueObjectID.getTimestamp();
        machine = uniqueObjectID.getMachineIdentifier();
        pid = uniqueObjectID.getProcessIdentifier();
        increment = uniqueObjectID.getCounter();
        uid = userid;
        pwd = password;
    }

    @Override
    public String toString()  {
        String result="";
        try{
            String temp=getUid() + "\f" + getPwd() + "\f" + getUniqueObjectID().toString() + "\f" + getTimestamp();
            result= AesUtils.encryptStr(temp,token_ekey );
        }
        catch (Exception e){

        }
        return result;

    }




}

