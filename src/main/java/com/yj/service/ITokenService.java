package com.yj.service;

import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

public interface ITokenService {

    ServerResponse<String> wx_token(String portrait, String nickname, String gender, HttpSession session, String code);

    ServerResponse<String> wxReturnSessionKey(String code);

    ServerResponse<String> wx_platform_token(String portrait, String nickname, String gender, HttpSession session, String code);

    ServerResponse<String> setWxPlatformUserUnionId(String code, HttpServletRequest request, HttpSession session);
}
