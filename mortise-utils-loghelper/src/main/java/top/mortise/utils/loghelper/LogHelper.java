package top.mortise.utils.loghelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by Eric.Zhang on 2016/12/27.
 * 默认配置文件在 ./config/log4j.xml
 */
public class LogHelper {


    /**
     * 自动匹配请求类名，生成logger对象，此处 logger name 值为 [className].[methodName]() Line: [fileLine]
     *
     * @return Logger
     * @author Eriz.zsp
     */
    public static Logger logger() {
        // 最原始被调用的堆栈对象
        StackTraceElement caller = findCaller();
        if (null == caller) {return LoggerFactory.getLogger(LogHelper.class);}

        Logger log = LoggerFactory.getLogger(caller.getClassName() + "." + caller.getMethodName() + "() Line: " + caller.getLineNumber());

        return log;
    }

    /**
     * 自动匹配请求类名，生成logger对象，此处 logger name 值为 [className].[methodName]() Line: [fileLine]
     *
     * @return Logger
     * @author Eriz.zsp
     */
    public static Logger logger(String logName) {

        Logger log = LoggerFactory.getLogger(logName);
        return log;
    }


    /**
     * 获取最原始被调用的堆栈信息
     *
     * @return StackTraceElement
     * @author Eriz.zsp
     */
    private static StackTraceElement findCaller() {
        // 获取堆栈信息
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        if (null == callStack) {return null;}

        // 最原始被调用的堆栈信息
        StackTraceElement caller = null;
        // 日志类名称
        String logClassName = LogHelper.class.getName();
        // 循环遍历到日志类标识
        boolean isEachLogClass = false;

        // 遍历堆栈信息，获取出最原始被调用的方法信息
        for (StackTraceElement s : callStack) {
            // 遍历到日志类
            if (logClassName.equals(s.getClassName())) {
                isEachLogClass = true;
            }
            // 下一个非日志类的堆栈，就是最原始被调用的方法
            if (isEachLogClass) {
                if (!logClassName.equals(s.getClassName())) {
                    isEachLogClass = false;
                    caller = s;
                    break;
                }
            }
        }

        return caller;
    }
}
