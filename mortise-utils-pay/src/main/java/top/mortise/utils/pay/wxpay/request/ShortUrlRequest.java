package top.mortise.utils.pay.wxpay.request;



import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ShortUrlRequest {

    /**
     * 需要转换的URL，签名用原串，传输需URLencode
     */
    @NotNull(message = "URL链接long_url不能为空")
    private String long_url;

    public ShortUrlRequest(String long_url) throws UnsupportedEncodingException {
        this.long_url = URLEncoder.encode(long_url, "utf-8");
    }

    public String getLong_url() {
        return long_url;
    }

    public void setLong_url(String long_url) {
        this.long_url = long_url;
    }
}
