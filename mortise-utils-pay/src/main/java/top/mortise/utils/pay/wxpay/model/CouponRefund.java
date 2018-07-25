package top.mortise.utils.pay.wxpay.model;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;


public class CouponRefund {

    /**
     * 退款代金券ID
     * String(20)
     */
    private String coupon_refund_id;

    /**
     * CASH--充值代金券
     * NO_CASH---非充值优惠券
     * String
     */
    private String coupon_type;

    /**
     * 单个代金券退款金额
     * 代金券退款金额<=退款金额，退款金额-代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠
     */
    private Integer coupon_refund_fee;

    public static List<CouponRefund> parse(Map<String, String> responseParamMap) {
        List<CouponRefund> couponRefundList = Lists.newArrayList();
        if (responseParamMap.containsKey("coupon_refund_count")) {
            int coupon_refund_count = Integer.valueOf(responseParamMap.get("coupon_refund_count"));
            for (int i = 0; i < coupon_refund_count; i++) {
                CouponRefund couponRefund = new CouponRefund();
                couponRefund.setCoupon_refund_id(responseParamMap.get("coupon_refund_id_" + i));
                couponRefund.setCoupon_type(responseParamMap.get("coupon_type_" + i));
                couponRefund.setCoupon_refund_fee(Integer.valueOf("coupon_refund_fee_" + i));
                couponRefundList.add(couponRefund);
            }
        }
        return couponRefundList;
    }

    public String getCoupon_refund_id() {
        return coupon_refund_id;
    }

    public void setCoupon_refund_id(String coupon_refund_id) {
        this.coupon_refund_id = coupon_refund_id;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public Integer getCoupon_refund_fee() {
        return coupon_refund_fee;
    }

    public void setCoupon_refund_fee(Integer coupon_refund_fee) {
        this.coupon_refund_fee = coupon_refund_fee;
    }
}
