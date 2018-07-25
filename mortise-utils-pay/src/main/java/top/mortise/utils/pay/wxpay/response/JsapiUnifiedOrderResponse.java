package top.mortise.utils.pay.wxpay.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsapiUnifiedOrderResponse {

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

    //------------------以下字段在return_code为SUCCESS的时候有返回-----------------------

    /**
     * 公众号APPID
     * 必填，String(32)
     */
    private String appid;

    /**
     * 商户号
     * 必填，String(32)
     */
    private String mch_id;

    /**
     * 设备号
     * 非必填，String(32)
     */
    private String device_info;

    /**
     * 随机字符串
     * 必填，String(32)
     */
    private String nonce_str;

    /**
     * 签名
     * 必填，String(32)
     */
    private String sign;

    /**
     * 业务结果
     * 必填，String(16)
     */
    private String result_code;

    /**
     * 错误代码
     * 非必填，String(32)
     */
    private String err_code;

    /**
     * 错误代码描述
     * 非必填，	String(128)
     */
    private String err_code_des;

    //------------------以下字段在return_code 和result_code都为SUCCESS的时候有返回-----------------------

    /**
     * 交易类型
     * 必填，String(16)
     */
    private String trade_type;

    /**
     * 预支付交易会话标识
     * 必填，String(64)
     */
    private String prepay_id;

    public boolean isSuccess() {
        return "SUCCESS".equals(return_code) && "SUCCESS".equals(result_code);
    }
}
