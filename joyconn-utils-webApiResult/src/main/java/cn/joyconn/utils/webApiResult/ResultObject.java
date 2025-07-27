package cn.joyconn.utils.webApiResult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */
@Tag(name ="返回值")
@JsonIgnoreProperties
public class ResultObject<T> {
    @Schema(description  = "查询的页面大小")
    private ResultCode code;
    @Schema(description  = "错误详细信息")
    private String errorMsg;
    @Schema(description  = "结果数据体")
    private  T result;
    @Schema(description  = "符合条件的总条数,只在分页查询中有效")
    private  Long allcount;



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
    public ResultCode getCode() {
        return code;
    }

    /**
     * 设置结果标示代码
     * @param code
     */
    public void setCode(ResultCode code) {
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
    public Long getAllcount() {
        return allcount;
    }
    /**
     * 设置数据总条数，只在分页查询时会用到
     * @return
     */
    public void setAllcount(Long allcount) {
        this.allcount = allcount;
    }


}
