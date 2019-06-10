package com.yj.service;

import com.yj.common.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface IOperationService {

    ServerResponse<List<Map<String,Object>>> foundPageShowDatingCare(HttpServletRequest request);
}
