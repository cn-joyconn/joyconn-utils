package top.mortise.utils.webApiResult;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Eric.Zhang on 2017/3/13.
 */
@ApiModel
public enum ResultCode {
    @ApiModelProperty(value = "登录成功")
    LoginSucess ,
    @ApiModelProperty(value = "操作成功")
    OperateOk ,
    @ApiModelProperty(value = "用户未登录")
    NoLogin ,
    @ApiModelProperty(value = "用户锁定")
    UserLocck ,
    @ApiModelProperty(value = "登录id错误")
    LoginIdError,
    @ApiModelProperty(value = "密码错误")
    LoginPassError ,
    @ApiModelProperty(value = "登录失败")
    LoginFail ,
    @ApiModelProperty(value = "操作失败")
    OperateFail ,
    @ApiModelProperty(value = "验证码错误")
    CehckCodeError  ,
    @ApiModelProperty(value = "参数错误")
    ParamsError ,
    @ApiModelProperty(value = "没有权限")
    NoRule ,
    @ApiModelProperty(value = "没有结果")
    NoResult  ,
    @ApiModelProperty(value = "服务器错误")
    ServiceError  ,
    @ApiModelProperty(value = "数据访问错误")
    DbError  ,
    @ApiModelProperty(value = "字段重复")
    FiledRepeat ,
    @ApiModelProperty(value = "参数签名验证失败")
    VerifyFail ,
    @ApiModelProperty(value = "余额不足")
    CreditNotEnough,
    @ApiModelProperty(value = "商品已失效")
    GoodsInvalid,
    @ApiModelProperty(value = "支付失败")
    PayError


}
