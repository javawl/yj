<%@ page import="com.yj.common.Const" %><%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2018/11/23
  Time: 19:02
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
    }
    var id = parseInt(GetQueryString("id"));
    var all_url = url+"/admin/showGameMonthChallengeMember.do";
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'POST',
            data:{
                id:id
            },
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    var string1;
                    var string2;
                    var string3;
                    if (data[i]['portrait']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 50px; max-height: 50px;" src="'+data[i]['portrait']+'">';
                    }
                    if (data[i]['status']=='real'){
                        string1 = "真实用户"
                    }else {
                        string1 = "非真实用户"
                    }
                    if (data[i]['wheather_gain']=='0'){
                        string3 = "未发放"
                    }else {
                        string3 = "已发放"
                    }
                    $("#author_info").append('<tr>'+
                        '<td style="width: 4%;">'+data[i]['rank']+'</td>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+string1+'</td>'+
                        '<td style="width: 4%;">'+data[i]['reward']+'</td>'+
                        '<td style="width: 4%;">'+string3+'</td>'+
                        '<td style="width: 6%;"><button style="margin-left: 5px;" onclick="win('+"'"+data[i]['user_id']+"',"+ "'"+id+"'"+')">设为已发奖金</button></td>'+
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
    <h1>小游戏挑战</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <%--<tr>--%>
            <%--<td style="border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;">--%>
                <%--<button style="float: right"></button>--%>
            <%--</td>--%>
        <%--</tr>--%>
        <tr>
            <td>排名</td>
            <td>头像</td>
            <td>昵称</td>
            <td>是否虚拟</td>
            <td>奖金</td>
            <td>是否已经发给用户</td>
            <td>操作</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function win(id,challenge) {
        if (confirm("你确定要设置为中奖？")){
            $.ajax({
                url:url+"/admin/changeGameRewardSent.do",
                type:'POST',
                data:{
                    id:id,
                    challenge_id:challenge
                },
                dataType:'json',
                success:function (result) {
                    var code = result['code'];
                    var msg = result['msg'];
                    if (code != 200){
                        alert(msg);
                    }else {
                        alert(msg);
                        history.go(0);
                    }
                },
                error:function (result) {
                    console.log(result);
                    alert("服务器出错！");
                }
            });
        }
    }
</script>
</html>
