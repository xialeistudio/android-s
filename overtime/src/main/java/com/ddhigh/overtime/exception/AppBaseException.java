package com.ddhigh.overtime.exception;

public class AppBaseException extends Exception {
    public static final int COMMON_ERROR_UNDEFINED = 0x0001;
    public int code;

    public AppBaseException(String detailMessage, int code) {
        super(detailMessage);
        this.code = code;
    }
}
