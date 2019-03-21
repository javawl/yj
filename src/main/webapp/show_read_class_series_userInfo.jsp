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
    var series_id = parseInt(GetQueryString("series_id"));
    var user_id = parseInt(GetQueryString("user_id"));
    $(document).ready(function(){
        $.ajax({
            url: url+"/admin/show_read_class_series_userInfo.do?user_id="+user_id+"&series_id="+series_id,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    var string1;
                    var string2;
                    if (data[i]['portrait']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 50px; max-height: 50px;" src="'+data[i]['portrait']+'">';
                    }
                    $("#author_info").append('<tr>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['book_name']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['chapter_name']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['set_time']+'</td>'+
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
    <h1>用户在本系列的打卡行为</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td>头像</td>
            <td>昵称</td>
            <td>书籍</td>
            <td>章节</td>
            <td>打卡时间</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
</html>
