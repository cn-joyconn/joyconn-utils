package top.mortise.utils.pay.wxpay.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Coupon {

    /**
     * 代金券或立减优惠ID,
     * String(20)
     */
    private String coupon_id;

    /**
     * CASH--充值代金券
     * NO_CASH---非充值优惠券
     * String
     */
    private String coupon_type;

    /**
     * 单个代金券或立减优惠支付金额
     */
    private Integer coupon_fee;

    public static List<Coupon> parse(Map<String, String> responseParamMap) {
        List<Coupon> couponList = new ArrayList<>();
        if (responseParamMap.get("coupon_count") != null) {
            int coupon_count = Integer.valueOf(responseParamMap.get("coupon_count"));
            for (int i = 0; i < coupon_count; i++) {
                Coupon coupon = new Coupon();
                coupon.setCoupon_id(responseParamMap.get("coupon_id_" + i));
                coupon.setCoupon_type(responseParamMap.get("coupon_type_" + i));
                coupon.setCoupon_fee(Integer.valueOf("coupon_fee_" + i));
                couponList.add(coupon);
            }
        }
        return couponList;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public Integer getCoupon_fee() {
        return coupon_fee;
    }

    public void setCoupon_fee(Integer coupon_fee) {
        this.coupon_fee = coupon_fee;
    }
}
