package com.yj.controller.portal;

import com.yj.common.ServerResponse;
import com.yj.pojo.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 63254 on 2018/8/20.
 */
public class BaseController {


    //Validate
    public static String PhoneValidate(String phone){
        //手机号码判断是否11位
        Pattern p = Pattern.compile("^[1-9][0-9]{10}$");

        Matcher m = p.matcher(phone);
        if (!m.find()){
            return "手机号码并非11位请重新输入！";
        }
        //null代表成功
        return null;
    }
}
