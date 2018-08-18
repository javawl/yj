package com.yj.common;

/**
 * Created by 63254 on 2018/8/15.
 */
public enum  ResponseCode {

    SUCCESS(200, "SUCCESS"),
    ERROR(400, "ERROR"),
    NEED_LOGIN(501, "NEED_LOGIN"),
    ILLEGAL_ARGUMENT(403, "NEED_LOGIN");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode(){
        return code;
    }
    public String getDesc(){
        return desc;
    }
}
