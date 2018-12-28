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
    var all_url = url+"/admin/show_word_challenge_info.do";
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
                var lottery_draw = result["data"]['word_challenge'];
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
                        '<td style="width: 4%;">'+data[i]['insist_day']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['reward']+'</td>'+
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
                        '<td style="width: 4%;">'+winner[i]['insist_day']+'</td>'+
                        '<td style="width: 4%;">'+winner[i]['reward']+'</td>'+
                        '</tr>');
                }
                $("#lottery_draw").append('<p>报名上限: '+ lottery_draw['upper_limit'] +'</p><br><p>可报名上限: '+ lottery_draw['enrollment'] +'</p><br><p>开始时间: '+ lottery_draw['st'] +'</p><br><p>结束时间： '+lottery_draw['et']+'</p>' +
                    '<br><p>期数： '+lottery_draw['periods']+'</p><br><p>盈亏： '+lottery_draw['profit_loss']+'</p><br><p>虚拟用户： '+lottery_draw['virtual_number']+'</p>')
//                if (result.status == 200){
//                    alert(result[0]);
//                }
                if (lottery_draw['whether_finish'] == '0'){
                    //没结束
                    $("#this_challenge_info").css("display",'none')
                    $("#remind").empty()
                    $("#remind").append("<h3>单词挑战未到结束时间不可清算</h3>")
                }else {
                    //已结束
                    if (lottery_draw['whether_settle_accounts'] == '0'){
                        //未清算过
                        $("#this_challenge_info").css("display",'none')
                    }else {
                        $("#remind").empty()
                        $("#this_challenge_info").css("display",'')
                        $("#this_challenge_info").append('<tr>'+
                            '<td>'+lottery_draw['aggregate_amount']+'</td>'+
                            '<td>'+lottery_draw['success_people']+'</td>'+
                            '<td>'+lottery_draw['success_rate']+'</td>'+
                            '<td>'+lottery_draw['reward_each']+'</td>'+
                            '<td>'+lottery_draw['invite_success']+'</td>'+
                            '<td>'+lottery_draw['loser']+'</td>'+
                            '<td>'+lottery_draw['profit_loss']+'</td>'+
                            '<td>'+lottery_draw['final_confirm']+'</td>'+
                            '<td><button style="margin-left: 5px;" onclick="settle_accounts('+"'"+id+"'"+')">再次结算</button><button style="margin-left: 5px;" onclick="final_confirm('+ "'"+id+"'"+')">最终确认</button></td>'+
                            '</tr>');
                    }
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
    <div id="lottery_draw">
        <h1>单词挑战</h1>
        <br>
    </div>
    <h1>成功用户</h1>
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
            <td>坚持天数</td>
        </tr>
    </table>
    <h1>单词挑战参与用户</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"><button style="float: right"><a href="show_virtual_user_challenge.jsp?page=1&size=15">展示虚拟用户</a></button></td>
            <td style="border-left: 0;">
                <button style="float: right"><a href="add_virtual_user_challenge.jsp">新建虚拟用户</a></button>
            </td>
        </tr>
        <tr>
            <td>序号</td>
            <td>头像</td>
            <td>昵称</td>
            <td>是否虚拟</td>
            <td>坚持天数</td>
            <td>盈亏</td>
            <td>操作</td>
        </tr>
    </table>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="this_challenge_info">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"><button style="float: right"><a href="show_virtual_user_challenge.jsp?page=1&size=15">展示虚拟用户</a></button></td>
            <td style="border-left: 0;">
                <button style="float: right"><a href="add_virtual_user_challenge.jsp">新建虚拟用户</a></button>
            </td>
        </tr>
        <tr>
            <td>总金额</td>
            <td>成功挑战用户数</td>
            <td>成功率</td>
            <td>奖励金金额/人</td>
            <td>邀请成功数</td>
            <td>挑战失败用户数</td>
            <td>最后营收</td>
            <td>是否最终确认</td>
            <td>操作</td>
        </tr>
    </table>
    <br>
    <div id="remind">
        <h3>单词挑战尚未结束，请点击结束按钮 <button id="over" onclick="settle_accounts(id)">结束(清算)</button></h3>
    </div>
    <table id="page">
    </table>
</center>
</body>
<script>
    function settle_accounts(id) {
        if (confirm("你确定要结算了？")){
            $.ajax({
                url:url+"/admin/settle_accounts.do",
                type:'POST',
                data:{
                    id:id
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
    function final_confirm(id) {
        if (confirm("你确定要最终确认了？确认后无法更改")){
            if (confirm("你真的确定要最终确认了？确认后无法更改")) {
                $.ajax({
                    url: url + "/admin/final_confirm.do",
                    type: 'POST',
                    data: {
                        id: id
                    },
                    dataType: 'json',
                    success: function (result) {
                        var code = result['code'];
                        var msg = result['msg'];
                        if (code != 200) {
                            alert(msg);
                        } else {
                            alert(msg);
                            history.go(0);
                        }
                    },
                    error: function (result) {
                        console.log(result);
                        alert("服务器出错！");
                    }
                });
            }
        }
    }
</script>
</html>
