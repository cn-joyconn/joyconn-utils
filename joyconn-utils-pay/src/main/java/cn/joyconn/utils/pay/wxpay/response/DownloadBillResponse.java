package cn.joyconn.utils.pay.wxpay.response;




public class DownloadBillResponse {

    /**
     * 返回状态码
     * 此字段是通信标识，非交易标识，交易是否成功需要查看result_code来判断
     * 必填，String(16)
     */
    private String return_code;

    /**
     * 返回信息
     * 非必填，String(128)
     */
    private String return_msg;

    /**
     * 账单数据
     */
    private String data;

    public boolean isSuccess() {
        return "SUCCESS".equals(return_code);
    }

    public String getReturn_code() {
        return return_code;
    }

    public void setReturn_code(String return_code) {
        this.return_code = return_code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
