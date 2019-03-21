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
    var all_url = url+"/admin/show_lottery_draw_info.do";
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'POST',
            data:{
                id:id
            },
            dataType:'json',
            success:function (result) {
                var data = result["data"]['contestants'];
                var lottery_draw = result["data"]['lottery_draw'];
                var winner = result["data"]['winner'];
                for(var i = 0; i < data.length; i++){
                    var string1;
                    var string2;
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
                    $("#author_info").append('<tr>'+
                        '<td style="width: 4%;">'+data[i]['id']+'</td>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+string1+'</td>'+
                        '<td style="width: 6%;"><button style="margin-left: 5px;" onclick="win('+"'"+data[i]['id']+"',"+ "'"+id+"'"+')">设为中奖</button><button style="margin-left: 5px;" onclick="win('+"'"+data[i]['id']+"',"+ "'"+id+"'"+')">取消中奖</button></td>'+
                        '</tr>');
                }
                for(var i = 0; i < winner.length; i++){
                    var string1;
                    var string2;
                    if (winner[i]['portrait']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 50px; max-height: 50px;" src="'+winner[i]['portrait']+'">';
                    }
                    if (winner[i]['virtual']=='0'){
                        string1 = "真实用户"
                    }else {
                        string1 = "非真实用户"
                    }
                    $("#winner").append('<tr>'+
                        '<td style="width: 4%;">'+winner[i]['id']+'</td>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+winner[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+string1+'</td>'+
                        '</tr>');
                }
                var string3
                if (lottery_draw['prize_pic']==''){
                    string3 = '此资源为空'
                }else {
                    string3 = '<img style="max-width: 300px; max-height: 300px;" src="'+lottery_draw['prize_pic']+'">';
                }
                $("#lottery_draw").append('<p>奖品名称: '+ lottery_draw['prize'] +'</p><br><p>奖品图片： '+string3+'</p>')
//                if (result.status == 200){
//                    alert(result[0]);
//                }
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
    <div id="lottery_draw">
        <h1>奖品</h1>
        <br>
    </div>
    <h1>中奖用户</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="winner">
        <%--<tr>--%>
            <%--<td style="border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;">--%>
                <%--<button style="float: right"><a href="add_author.jsp">新建</a></button>--%>
            <%--</td>--%>
        <%--</tr>--%>
        <tr>
            <td>序号</td>
            <td>头像</td>
            <td>昵称</td>
            <td>是否虚拟</td>
        </tr>
    </table>
    <h1>抽奖参与用户</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"><button style="float: right"><a href="show_virtual_user.jsp?page=1&size=15">展示虚拟用户</a></button></td>
            <td style="border-left: 0;">
                <button style="float: right"><a href="add_virtual_user.jsp">新建虚拟用户</a></button>
            </td>
        </tr>
        <tr>
            <td>序号</td>
            <td>头像</td>
            <td>昵称</td>
            <td>是否虚拟</td>
            <td>操作</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function win(id,draw_id) {
        if (confirm("你确定要设置为中奖？")){
            $.ajax({
                url:url+"/admin/change_draw_win_status.do",
                type:'POST',
                data:{
                    id:id,
                    draw_id:draw_id
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
