package cn.muzisheng.pear.exception;

/**
 * 资源不存在。
 * code: 404
 **/
public class NotFoundException extends BaseException {
    public NotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
