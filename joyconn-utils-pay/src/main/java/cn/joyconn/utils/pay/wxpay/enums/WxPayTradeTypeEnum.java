package cn.joyconn.utils.pay.wxpay.enums;

public enum WxPayTradeTypeEnum {

    /**
     * H5支付
     */
    MWEB,

    /**
     * 公众号支付
     */
    JSAPI,

    /**
     * 原生扫码支付
     */
    NATIVE,

    /**
     * app支付
     */
    APP,

    /**
     * 刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口
     */
    MICROPAY;

    /**
     * 默认valueOf找不到匹配项会抛出异常，这里返回空
     *
     * @param tradeType
     * @return
     */
    public static WxPayTradeTypeEnum valueOfName(String tradeType) {
        WxPayTradeTypeEnum result = null;
        for (WxPayTradeTypeEnum item : values()) {
            if (item.equals(tradeType)) {
                result = item;
                break;
            }
        }
        return result;
    }
}
