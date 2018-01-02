package top.mortise.utils.comm;

import top.mortise.utils.comm.ResultCode;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */
public class ResultObject<T> {
    private String code;
    private String errorMsg;
    private  T result;
    private  int allcount;

    public ResultObject(){
        code= ResultCode.OperateOk;
        errorMsg="";
    }
    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
    /**
     * 获取结果标示代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置结果标示代码
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取错误信息
     * @return
     */
    public String getErrorMsg() {
        return errorMsg;
    }

    /**
     * 设置错误信息
     * @param errorMsg
     */
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    /**
     * 获取数据总条数，只在分页查询时会用到
     * @return
     */
    public int getAllcount() {
        return allcount;
    }
    /**
     * 设置数据总条数，只在分页查询时会用到
     * @return
     */
    public void setAllcount(int allcount) {
        this.allcount = allcount;
    }


}
