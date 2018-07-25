package top.mortise.utils.pay.wxpay.autoconfigure;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mortise.utils.pay.wxpay.WxPayService;

/**
 * https://github.com/wxpay/WXPay-SDK-Java/tree/d7ecb4020780b676953ed7de58ef807bd871023f
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=11_1
 */
@Configuration
@EnableConfigurationProperties(WxPayProperties.class)
public class WxPayAutoConfiguration {

    @Bean
    public WxPayService wxPayService(WxPayProperties wxPayProperties) throws Exception {
        return new WxPayService(wxPayProperties);
    }
}
