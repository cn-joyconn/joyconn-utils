package top.mortise.utils.pay.wxpay.autoconfigure;



import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "pay.wxpay")
public class WxPayProperties {

    private String appId;

    private String mchId;

    private String key;

    private String certFile;

    private String notifyUrl;

    private boolean autoReport;

    private boolean useSandbox;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCertFile() {
        return certFile;
    }

    public void setCertFile(String certFile) {
        this.certFile = certFile;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public boolean isAutoReport() {
        return autoReport;
    }

    public void setAutoReport(boolean autoReport) {
        this.autoReport = autoReport;
    }

    public boolean isUseSandbox() {
        return useSandbox;
    }

    public void setUseSandbox(boolean useSandbox) {
        this.useSandbox = useSandbox;
    }
}
