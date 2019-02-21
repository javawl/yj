<%@ page import="com.yj.common.Const" %><%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2018/11/17
  Time: 21:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <style type="text/css">
        body{ margin: 0 auto;padding: 0;}
        .top{ width: 100%;height: 75px;background-color: grey;}
        .left_bar{ width: 13%;height: 693px; background-color: lightgray; float: left;}
        .min_body{ width: 100%;}
        .title{ font-size: 20px; margin-top: 27px; margin-left: 50px; font-weight: bolder;float: left;}
        .exit{ font-size: 20px; margin-top: 27px; margin-right: 25px; font-weight: bolder;float: right;}
        .first_p{ margin-top: 0; padding-top: 16px;}
        .main{ width: 87%; height: 693px; float: right;}
    </style>
    <meta charset="UTF-8">
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript">
        function change_src(src){
            $("#iframepage").attr("src", src);
        }
    </script>
</head>

<body>
    <%--顶部--%>
    <div class="top">
        <p onclick="change_src('admin_main.jsp')" class="title">超级无敌后台管理系统</p>
        <p class="exit">退出</p>
    </div>
    <div class="min_body">
        <%--左侧导航栏--%>
        <div class="left_bar">
            <center>
                <p class="first_p"><a href="word_page.jsp?page=1&size=15&type=1&condition=undefined" style="text-decoration: none; color: black;">单词列表</a></p>
                <p onclick="change_src('show_users.jsp?page=1&size=15')">用户列表</p>
                <p onclick="change_src('show_daily_pic.jsp?page=1&size=15')">每日一句</p>
                <%--<p>广告位</p>--%>
                <p onclick="change_src('show_welfare_service.jsp?page=1&size=15')">福利社</p>
                <p onclick="change_src('show_advice.jsp?page=1&size=15')">用户反馈</p>
                <p onclick="change_src('show_author.jsp?page=1&size=15')">创建账号</p>
                <p onclick="change_src('show_data.jsp?page=1&size=15')">数据查看</p>
                <p onclick="change_src('show_feeds.jsp?page=1&size=15')">feeds流</p>
                <p onclick="change_src('lottery_draw.jsp?page=1&size=15')">抽奖赢奖品</p>
                <p onclick="change_src('show_word_challenge.jsp?page=1&size=15')">单词挑战</p>
                <p onclick="change_src('show_withdraw_cash.jsp?page=1&size=15')">提现申请</p>
                <%--<p onclick="change_src('read_class_book.jsp?page=1&size=15')">阅读书籍</p>--%>
                <%--<p onclick="change_src('show_read_class.jsp?page=1&size=15')">阅读挑战</p>--%>
            </center>
        </div>
        <div class="main">
            <iframe id="iframepage" src="admin_main.jsp" frameborder="0" name="mainFrame" width="100%" height="693px" style="display: flex;box-sizing: border-box;flex-direction: row;justify-content: center;align-content: center"></iframe>
        </div>
    </div>
</body>
</html>
