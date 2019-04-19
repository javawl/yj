<%@ page import="com.yj.common.Const" %><%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2018/11/18
  Time: 21:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <style type="text/css" rel="stylesheet">
        body{ margin: 0 auto; padding: 0; width: 100%;}
        h1{ margin-top: 3rem;}
    </style>
</head>
<script type="text/javascript">
    var count = 0;
    <%
        String url = Const.DOMAIN_NAME;
        String root_url = Const.FTP_PREFIX;
    %>
    var url = "<%=url %>";
    //        var url = 'http://47.107.62.22:8080';
    var root_url = "<%=root_url %>";
    //        if (url1 == url || url1 == url+'/'){
    //            window.location.href=url+"/show_daily_pic.jsp?page=1&size=15"
    //        }
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
        // 获取get参数的方法
    }
    var courseId = parseInt(GetQueryString("id"));
    $(document).ready(function(){
        $.ajax({
            url: url+"/admin/showLiveCourseUserInfo.do?courseId="+courseId,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    var string2;
                    if (data[i]['portrait']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 50px; max-height: 50px;" src="'+data[i]['portrait']+'">';
                    }

                    var string3;
                    if (data[i]['invite_portrait']=='null'){
                        string3 = '此资源为空'
                    }else {
                        string3 = '<img style="max-width: 50px; max-height: 50px;" src="'+data[i]['invite_portrait']+'">';
                    }
                    $("#author_info").append('<tr>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['whether_help']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['set_time']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['invite_user_id']+'</td>'+
                        '<td style="width: 4%;">'+string3+'</td>'+
                        '<td style="width: 4%;">'+data[i]['invite_username']+'</td>'+
                        '</tr>');
                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
    });
</script>
<body>
<center>
    <h1>直播课程用户查看</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td>头像</td>
            <td>昵称</td>
            <td>报名价格</td>
            <td>报名时间</td>
            <td>邀请者id</td>
            <td>邀请者头像</td>
            <td>邀请者名字</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
</html>
