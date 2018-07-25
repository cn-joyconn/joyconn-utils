package top.mortise.utils.pay.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mortise.utils.pay.alipay.autoconfigure.AliPayConfig;
import top.mortise.utils.pay.alipay.request.AppPayRequest;
import top.mortise.utils.pay.alipay.request.PagePayRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Slf4j
public class AliPayService {
    /**
     * 日志
     *
     * @return
     */
    public static Logger getLogger() {
        Logger logger = LoggerFactory.getLogger("alipay java sdk");
        return logger;
    }
    private AlipayClient alipayClient;
    private AliPayConfig aliPayConfig;

    public AliPayService(AliPayConfig aliPayConfig) {
        this.aliPayConfig = aliPayConfig;
        this.alipayClient = new DefaultAlipayClient(
                aliPayConfig.getServerUrl(), aliPayConfig.getAppid(),
                aliPayConfig.getPrivateKey(), aliPayConfig.getFormat(), aliPayConfig.getCharset(),
                aliPayConfig.getAlipayPublicKey(), aliPayConfig.getSignType());
    }

    public AlipayClient getAlipayClient() {
        return alipayClient;
    }

    public AliPayConfig getAliPayConfig() {
        return aliPayConfig;
    }

    /**
     * 验证支付宝通知签名是否合法
     *
     * @param paramsMap
     * @return
     * @throws AlipayApiException
     */
    public boolean rsaCheckV1(Map<String, String> paramsMap) throws AlipayApiException {
        return AlipaySignature.rsaCheckV1(paramsMap, aliPayConfig.getAlipayPublicKey(), aliPayConfig.getCharset(), paramsMap.get("sign_type"));
    }

    /**
     * 获取支付宝App授权字符串
     *
     * @param targetId
     * @return
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    public String getInfoStr(String targetId) throws AlipayApiException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        sb.append("apiname=com.alipay.account.auth")
                .append("&app_id=").append(aliPayConfig.getAppid())
                .append("&app_name=").append("mc")
                .append("&auth_type=AUTHACCOUNT")
                .append("&biz_type=openservice")
                .append("&method=alipay.open.auth.sdk.code.get")
                .append("&pid=").append(aliPayConfig.getSellerId())
                .append("&product_id=APP_FAST_LOGIN")
                .append("&scope=kuaijie")
                .append("&sign_type=").append(aliPayConfig.getSignType())
                .append("&target_id=").append(targetId);
        String sign = AlipaySignature.rsaSign(sb.toString(), aliPayConfig.getPrivateKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        sb.append("&sign=").append(URLEncoder.encode(sign, aliPayConfig.getCharset()));
        return sb.toString();
    }

    /**
     * 获取支付宝App支付字符串
     *
     * @param request
     * @return
     * @throws AlipayApiException
     * @throws UnsupportedEncodingException
     */
    public String getOrderStr(AppPayRequest request) throws AlipayApiException, UnsupportedEncodingException {
        String originalStr = request.getOriginalStr();
        getLogger().debug("original string：{}", originalStr);
        String sign = AlipaySignature.rsaSign(originalStr, aliPayConfig.getPrivateKey(), aliPayConfig.getCharset(), aliPayConfig.getSignType());
        getLogger().debug("signed string：{}", originalStr);
        request.setSign(sign);
        String encodedStr = request.getEncodedStr();
        getLogger().debug("encoded string：{}", originalStr);
        return encodedStr;
    }

    /**
     * 获取支付宝PC网页支付Form表单字符串
     *
     * @param pagePayRequest
     * @return
     * @throws AlipayApiException
     */
    public String getFormStr(PagePayRequest pagePayRequest) throws AlipayApiException {
        return alipayClient.pageExecute(pagePayRequest.generate()).getBody(); //调用SDK生成表单
    }

    /**
     * 查询交易
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeQueryResponse query(AlipayTradeQueryRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 退款
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeRefundResponse refund(AlipayTradeRefundRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 结算
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeOrderSettleResponse settle(AlipayTradeOrderSettleRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 关闭交易
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeCloseResponse close(AlipayTradeCloseRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 取消交易
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeCancelResponse cancel(AlipayTradeCancelRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 退款查询
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeFastpayRefundQueryResponse refundQuery(AlipayTradeFastpayRefundQueryRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 交易预创建
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradePrecreateResponse pay(AlipayTradePrecreateRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 创建交易
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradeCreateResponse pay(AlipayTradeCreateRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 支付交易
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayTradePayResponse pay(AlipayTradePayRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 支付宝转账
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayFundTransToaccountTransferResponse transfer(AlipayFundTransToaccountTransferRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 支付宝转账查询
     *
     * @param request
     * @return
     * @throws AlipayApiException
     */
    public AlipayFundTransOrderQueryResponse transferQuery(AlipayFundTransOrderQueryRequest request) throws AlipayApiException {
        return alipayClient.execute(request);
    }

    /**
     * 根据授权码换取OAuth2 Token
     *
     * @param authCode
     * @return
     * @throws AlipayApiException
     */
    public AlipaySystemOauthTokenResponse getAuthToken(String authCode) throws AlipayApiException {
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setGrantType("authorization_code");
        request.setCode(authCode);
        return alipayClient.execute(request);
    }

    /**
     * 根据access_token换取用户信息
     *
     * @param accessToken
     * @return
     * @throws AlipayApiException
     */
    public AlipayUserInfoShareResponse getUserInfo(String accessToken) throws AlipayApiException {
        AlipayUserInfoShareRequest request = new AlipayUserInfoShareRequest();
        return alipayClient.execute(request, accessToken);
    }
}
