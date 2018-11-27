package com.yj.service.impl;

import com.yj.common.ServerResponse;
import com.yj.dao.Common_configMapper;
import com.yj.dao.DictionaryMapper;
import com.yj.dao.FeedsMapper;
import com.yj.dao.UserMapper;
import com.yj.service.IFileService;
import com.yj.service.ITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Transactional(readOnly = false)
public class TokenServiceImpl implements ITokenService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DictionaryMapper dictionaryMapper;

    @Autowired
    private FeedsMapper feedsMapper;

    @Autowired
    private Common_configMapper common_configMapper;

    @Autowired
    private IFileService iFileService;

    @Autowired
    private ApplicationContext ctx;

    protected String code;
    protected String wxAppID;
    protected String wxAppSecret;
    protected String wxLoginUrl;

    @Override
    public ServerResponse<List> wx_token(String code){
//        //将页数和大小转化为limit
//        int start = (Integer.parseInt(page) - 1) * Integer.parseInt(size);
//        if (condition.equals("undefined")){
//            return ServerResponse.createBySuccess(dictionaryMapper.countWord(type),dictionaryMapper.selectAdminWords(start,Integer.parseInt(size),type));
//        }else {
//            return ServerResponse.createBySuccess(dictionaryMapper.countWord(type),dictionaryMapper.selectAdminWordsForSelect(start,Integer.parseInt(size),type,"%"+condition+"%"));
//        }
        return null;
    }
}
