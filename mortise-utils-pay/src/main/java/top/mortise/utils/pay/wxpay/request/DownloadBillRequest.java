package top.mortise.utils.pay.wxpay.request;



import org.hibernate.validator.constraints.Length;
import top.mortise.utils.pay.wxpay.enums.WxPayBillTypeEnum;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DownloadBillRequest {

    /**
     * 对账单日期
     */
    @Length(min = 8, max = 8, message = "对账单日期bill_date长度需为8位")
    @NotNull(message = "对账单日期bill_date不能为空")
    private String bill_date;

    /**
     * 账单类型
     * ALL，返回当日所有订单信息，默认值
     * SUCCESS，返回当日成功支付的订单
     * REFUND，返回当日退款订单
     * RECHARGE_REFUND，返回当日充值退款订单
     */
    private String bill_type;

    /**
     * 压缩账单
     * 非必传参数，固定值：GZIP，返回格式为.gzip的压缩包账单。不传则默认为数据流形式。
     */
    private String tar_type = "GZIP";

    public DownloadBillRequest(Date billDate, WxPayBillTypeEnum billType) {
        this.bill_date = new SimpleDateFormat("yyyyMMdd").format(billDate);
        this.bill_type = billType.name();
    }

    public String getBill_date() {
        return bill_date;
    }

    public void setBill_date(String bill_date) {
        this.bill_date = bill_date;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public String getTar_type() {
        return tar_type;
    }

    public void setTar_type(String tar_type) {
        this.tar_type = tar_type;
    }
}
