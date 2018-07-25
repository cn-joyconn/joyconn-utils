package top.mortise.utils.pay.alipay.autoconfigure;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mortise.utils.pay.alipay.AliPayService;

@Configuration
@EnableConfigurationProperties(AliPayConfig.class)
public class AliPayAutoConfiguration {

    @Autowired
    private AliPayConfig aliPayConfig;

    @Bean
    public AliPayService aliPayService() {
        return new AliPayService(aliPayConfig);
    }

}
