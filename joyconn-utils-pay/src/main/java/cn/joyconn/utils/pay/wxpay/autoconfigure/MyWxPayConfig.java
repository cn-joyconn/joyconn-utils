package cn.joyconn.utils.pay.wxpay.autoconfigure;

import com.github.wxpay.sdk.IWXPayDomain;
import com.github.wxpay.sdk.WXPayConfig;

import java.io.InputStream;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyWxPayConfig extends WXPayConfig {

    private static Lock lock =new ReentrantLock();
    private static InputStream certStream;

    private WxPayProperties wxPayProperties;
    private IWXPayDomain wxPayDomain;

    public MyWxPayConfig(WxPayProperties wxPayProperties) {
        this.wxPayProperties = wxPayProperties;
        this.wxPayDomain = new MyWxPayDomain();
    }

    @Override
    public String getAppID() {
        return wxPayProperties.getAppId();
    }

    @Override
    public String getMchID() {
        return wxPayProperties.getMchId();
    }

    @Override
    public String getKey() {
        return wxPayProperties.getKey();
    }

    @Override
    public InputStream getCertStream() {
        if(certStream==null){
            lock.lock();
            try{
                if(certStream==null) {
                    certStream = this.getClass().getResourceAsStream(wxPayProperties.getCertFile());
                }
            }catch (Exception ex){

            }finally {
                lock.unlock();
            }
        }
        return certStream;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return super.getHttpConnectTimeoutMs();
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return super.getHttpReadTimeoutMs();
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new MyWxPayDomain();
    }

    @Override
    public boolean shouldAutoReport() {
        return super.shouldAutoReport();
    }

    @Override
    public int getReportWorkerNum() {
        return super.getReportWorkerNum();
    }

    @Override
    public int getReportQueueMaxSize() {
        return super.getReportQueueMaxSize();
    }

    @Override
    public int getReportBatchSize() {
        return super.getReportBatchSize();
    }
}
