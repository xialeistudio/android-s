package com.ddhigh.overtime.exception;

public class AppBaseException extends Exception {
    public static final int COMMON_ERROR_UNDEFINED = 0x0001;
    public static final int COMMON_ERROR_NO_NETWORK = 0x0002;
    private final int code;

    public AppBaseException(String detailMessage, int code) {
        super(detailMessage);
        this.code = code;
    }
}
