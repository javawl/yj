package com.yj.service;

import com.yj.common.ServerResponse;
import com.yj.pojo.User;

/**
 * Created by 63254 on 2018/8/15.
 */
//这是一个接口
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(String user);
}
