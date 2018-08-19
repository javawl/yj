package com.yj.controller.portal;

import com.yj.common.Const;
import com.yj.common.ServerResponse;
import com.yj.pojo.User;
import com.yj.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@Controller
@RequestMapping("/user/")
public class UserController {

    //将Service注入进来
    @Autowired
    private IUserService iUserService;


    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session){
        //service-->mybatis->dao
        ServerResponse<User> response = iUserService.login(username,password);
        if (response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }

        return response;
    }

    /**
     * 登出接口
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * 注册接口
     * @param phone
     * @return
     */
    @RequestMapping(value = "register.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<String> register(String phone,Cookie cookie){

        return iUserService.register(phone);
    }


    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test.do", method = RequestMethod.GET)
    @ResponseBody
    public String test(){
        String s = "hello";
        return s;
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test1.do", method = RequestMethod.GET)
    @ResponseBody
    public String test1(HttpServletResponse httpServletResponse){
        Cookie cookie = new Cookie("id","5");
        cookie.setMaxAge(3600);
        httpServletResponse.addCookie(cookie);
        return "success";
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test2.do", method = RequestMethod.GET)
    @ResponseBody
    public String test2(@CookieValue("id") String testCookie){
        return testCookie;
    }

    /**
     * 测试
     * @return
     */
    @RequestMapping(value = "test3.do", method = RequestMethod.GET)
    @ResponseBody
    public String test3(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        String value = "";
        if (cookies != null) {
            // 遍历数组
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("id")) {
                    // 取出cookie的值
                    value = cookie.getValue();
//                    // 字符串转long
//                    long time = Long.parseLong(value);
//                    // 转成日期 Date
//                    Date date = new Date(time);
//                    // 创建一个显示的日期格式
//                    // 参数就是你想要显示的日期格式
//                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
//                    // 格式化时间
//                    String lastTime = dateFormat.format(date);
//                    // 响应回浏览器显示
//                    response.getWriter().write("上次访问时间" + lastTime);
                }
            }
        }
        return value;
    }
}
