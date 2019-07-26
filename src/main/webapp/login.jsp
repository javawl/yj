<%@ page import="com.yj.common.Const" %><%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/7/7
  Time: 15:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>登录</title>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery-3.2.1.min.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/aes.js"></script>
    <link rel="stylesheet" type="text/css" href="https://file.ourbeibei.com/l_e/static/css/login.css" />
</head>
<body>
<center>
    <p class="logo">登录</p>
    <div class="form">
        <form><div class="input_font">用户名 <input type="text" id="username" class="input" placeholder="(用户名)"></div>

            <div class="line"></div>
            <div class="input_font">密码 <input type="password" id="password" class="input"></div>
            <div class="line"></div>
            <div class="remember">
                <div class="remember_inner">
                    <div class="single_radio1">
                        <input id="a1" type="checkbox" name="remember" value="" class="radio"/><div id="block1" class="block1"><div id="circle1" class="circle"><div id="div1"></div></div>记住账号</div>
                    </div>
                    <div class="single_radio2">
                        <input id="a2" type="checkbox" name="remember" value="" class="radio"/><div id="block2" class="block1"><div id="circle2" class="circle"><div id="div2"></div></div>记住密码</div>
                    </div>
                </div>
            </div>
            <div class="submit">
                <p class="bottom_font">登录</p>
            </div>
            <div class="tail">
                <a href="https://www.ourbeibei.com" class="left">官网首页</a>
                <a href="https://file.ourbeibei.com/l_e/static/html/for_joke.html" class="right">忘记密码？</a>
            </div>
        </form>
    </div>
</center>
<center><div class="alert1"><center class="center">请输入用户名</center></div></center>
<center><div class="alert2"><center>请输入密码</center></div></center>
</body>
<script type="text/javascript">
    <%
        String url = Const.DOMAIN_NAME;
    %>
    var url = "<%=url %>";
    var info1 = window.localStorage.getItem("username");
    var info2 = window.localStorage.getItem("password");
    if (info1 != null){
        $("#username").attr('value',info1);
    }
    if (info2 != null){
        $("#password").attr('value',info2);
    }
    var flag = 1;
    $(".single_radio1").click(function () {
        if ($("#div1").css("display") == "none"){
            $("#div1").css("display","inline");
            $("#block1").css("color","#ff6600");
            $("#circle1").css("border","solid #ff6600 2px");
            $("#circle1").css("border","solid #ff6600 2px");
        }else {
            $("#div1").css("display","none");
            $("#block1").css("color","#333333");
            $("#circle1").css("border","solid #333333 2px");
        }
    });
    $(".single_radio2").click(function () {
        if ($("#div2").css("display") == "none"){
            $("#div2").css("display","inline");
            $("#block2").css("color","#ff6600");
            $("#circle2").css("border","solid #ff6600 2px");
            $("#circle2").css("border","solid #ff6600 2px");
        }else {
            $("#div2").css("display","none");
            $("#block2").css("color","#333333");
            $("#circle2").css("border","solid #333333 2px");
        }
    });
    $("body").click(function () {
        if ($(".logo").css("opacity")==0.4&&flag==3){
            $(".logo").css("opacity","1");
            $(".form").css("opacity","1");
            $(".alert1").css("display","none");
            $(".alert2").css("display","none");
            flag = 1;
        }
        if (flag == 2){
            flag = 3;
        }
    });
    $(".submit").click(function () {
        var username = encrypt($("#username").val());
        var password = encrypt($("#password").val());
        if (username === ''){
            $(".logo").css("opacity","0.4");
            $(".form").css("opacity","0.4");
            flag = 2;
            $(".alert1").css("display","inline");
            return;
        }
        if (password === ''){
            $(".logo").css("opacity","0.4");
            $(".form").css("opacity","0.4");
            flag = 2;
            $(".alert2").css("display","inline");
            return;
        }
        if ($("#div1").css("display") != "none"){
            window.localStorage.setItem("username",username);
        }else {
            window.localStorage.removeItem("username");
        }

        if ($("#div2").css("display") != "none"){
            window.localStorage.setItem("password",password);
        }else {
            window.localStorage.removeItem("password");
        }

        $.ajax({
            url:url + "/user/admin_login.do",
            type:'POST',
            dataType:'json',
            headers:{'Content-Type':'application/json;charset=utf8','username':username, 'password':password},
            success:function (result) {
                if (result.status == 200){
                    alert(result.msg);
                    window.location.href = url + "/admin.jsp";
                }else {
                    $(".logo").css("opacity","0.4");
                    $(".form").css("opacity","0.4");
                    flag = 2;
                    $(".alert1").css("display","inline");
                    $(".center").text(result.msg);
                    return;
                }
            },
            error:function (result) {
                var json_content = JSON.parse(result.responseText);
                $(".logo").css("opacity","0.4");
                $(".form").css("opacity","0.4");
                flag = 2;
                $(".alert1").css("display","inline");
                $(".center").text(json_content.msg);
                return;
            }
        });
    });


    /**
     * 加密（需要先加载lib/aes/aes.min.js文件）
     * @param word
     * @returns {*}
     */
    function encrypt(word){
        var key = CryptoJS.enc.Utf8.parse("yj147896325yjbdc");
        var srcs = CryptoJS.enc.Utf8.parse(word);
        var encrypted = CryptoJS.AES.encrypt(srcs, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
        return encrypted.toString();
    }

    /**
     * 解密
     * @param word
     * @returns {*}
     */
    function decrypt(word){
        var key = CryptoJS.enc.Utf8.parse("abcdefgabcdefg12");
        var decrypt = CryptoJS.AES.decrypt(word, key, {mode:CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7});
        return CryptoJS.enc.Utf8.stringify(decrypt).toString();
    }
</script>
</html>
