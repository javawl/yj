package com.yj.service;

import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

public interface ITokenService {

    ServerResponse<String> wx_token(String portrait, String nickname, String gender, HttpSession session, String code);

    ServerResponse<String> wxReturnSessionKey(String code);

    ServerResponse<Map<String, Object>> wx_platform_token(String portrait, String nickname, String gender, HttpSession session, String code);

    ServerResponse<String> setWxPlatformUserUnionId(HttpServletRequest request, HttpSession session);
}
