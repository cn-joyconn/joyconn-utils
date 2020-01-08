package cn.joyconn.utils.pay.wxpay.enums;

public enum WxPayBillTypeEnum {

    /**
     * 返回当日所有订单信息，默认值
     */
    ALL,

    /**
     * 返回当日成功支付的订单
     */
    SUCCESS,

    /**
     * 返回当日退款订单
     */
    REFUND,

    /**
     * 返回当日充值退款订单
     */
    RECHARGE_REFUND;

    /**
     * 默认valueOf找不到匹配项会抛出异常，这里返回空
     *
     * @param tradeType
     * @return
     */
    public static WxPayBillTypeEnum valueOfName(String tradeType) {
        WxPayBillTypeEnum result = null;
        for (WxPayBillTypeEnum item : values()) {
            if (item.equals(tradeType)) {
                result = item;
                break;
            }
        }
        return result;
    }
}
