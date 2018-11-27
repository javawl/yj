package com.yj.service;

import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ITokenService {

    ServerResponse<List> wx_token(String code);
}
