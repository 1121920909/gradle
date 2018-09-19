package com.hfut.issf.grade.service.Exception;

public class UsernameAlreadyException extends RuntimeException {
    public UsernameAlreadyException(String msg) {
        super(msg);
    }

    public UsernameAlreadyException() {
        super("用户名存在");
    }
}
