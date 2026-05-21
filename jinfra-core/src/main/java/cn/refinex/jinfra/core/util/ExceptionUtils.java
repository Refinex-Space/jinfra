package cn.refinex.jinfra.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具。
 *
 * @author refinex
 * @since 0.1.0
 */
public final class ExceptionUtils {

    private ExceptionUtils() {
        throw new AssertionError("No cn.refinex.jinfra.core.util.ExceptionUtils instances");
    }

    /**
     * 获取最深层 cause。
     *
     * @param throwable 异常
     * @return 最深层 cause；传入 null 时返回 null
     */
    public static Throwable rootCause(Throwable throwable) {
        if (throwable == null) {
            return null;
        }
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root;
    }

    /**
     * 获取最深层 cause 的消息。
     *
     * @param throwable 异常
     * @return 最深层 cause 消息；传入 null 时返回 null
     */
    public static String rootCauseMessage(Throwable throwable) {
        Throwable root = rootCause(throwable);
        return root == null ? null : root.getMessage();
    }

    /**
     * 将异常堆栈转为字符串。
     *
     * @param throwable 异常
     * @return 堆栈字符串；传入 null 时返回空字符串
     */
    public static String stackTraceToString(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.flush();
        return stringWriter.toString();
    }

    /**
     * 判断异常链中是否包含指定异常类型。
     *
     * @param throwable 异常
     * @param causeType 异常类型
     * @return 如果异常链包含该类型则返回 true
     */
    public static boolean isCausedBy(Throwable throwable, Class<? extends Throwable> causeType) {
        if (throwable == null || causeType == null) {
            return false;
        }
        Throwable current = throwable;
        while (current != null) {
            if (causeType.isInstance(current)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }
}
