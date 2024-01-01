package cn.joyconn.utils.webApiResult;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.PatternProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

// import io.swagger.annotations.ApiModel;
// import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */
@Tag(name = "状态码")
public enum ResultCode {
    /**
     * 登录成功
     */
    @Schema(description  = "登录成功")
    LoginSucess ,
    @Schema(description  = "操作成功")
    OperateOk ,
    @Schema(description  = "用户未登录")
    NoLogin ,
    @Schema(description  = "用户锁定")
    UserLocck ,
    @Schema(description  = "登录id错误")
    LoginIdError,
    @Schema(description  = "密码错误")
    LoginPassError ,
    @Schema(description  = "登录失败")
    LoginFail ,
    @Schema(description  = "令牌不存在")
    TokenNotExist ,
    @Schema(description  = "令牌错误")
    TokenFail ,
    @Schema(description  = "操作失败")
    OperateFail ,
    @Schema(description  = "验证码错误")
    CehckCodeError  ,
    @Schema(description  = "参数错误")
    ParamsError ,
    @Schema(description  = "没有权限")
    NoRule ,
    @Schema(description  = "没有结果")
    NoResult  ,
    @Schema(description  = "服务器错误")
    ServiceError  ,
    @Schema(description  = "数据访问错误")
    DbError  ,
    @Schema(description  = "字段重复")
    FiledRepeat ,
    @Schema(description  = "参数签名验证失败")
    VerifyFail ,
    @Schema(description  = "余额不足")
    CreditNotEnough,
    @Schema(description  = "商品已失效")
    GoodsInvalid,
    @Schema(description  = "支付失败")
    PayError,
    @Schema(description  = "手机号已存在")
    PhoneExisit,
    @Schema(description  = "邮箱已存在")
    EmailExisit,
    @Schema(description  = "用户名已存在")
    UserNameExisit,
    @Schema(description  = "时间戳错误")
    TimeStrErr,
    @Schema(description  = "校验值错误")
    SignStrErr,


}
