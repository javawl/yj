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
    var challenge_id = parseInt(GetQueryString("id"));
    var all_url = url+"/admin/showReadClassSeries.do?id="+challenge_id;
    $(document).ready(function(){
        $("#add_new").attr('href',"add_read_class_series.jsp?id="+challenge_id);
        $.ajax({
            url: url+"/admin/showPlatformChallengeSeriesUser.do?id="+challenge_id,
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
                        '<td style="width: 4%;">'+ "1" + data[i]['user_id']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['insist_day']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['word_number']+'</td>'+
                        '<td style="width: 6%;"><button style="margin-left: 5px;" onclick="user_add_day('+"'"+data[i]['user_id']+"',"+ "'"+challenge_id+"'"+')">添加</button><button style="margin-left: 5px;" onclick="del('+"'"+data[i]['user_id']+"',"+ "'"+challenge_id+"'"+')">删除</button></td>'+
                        '</tr>');
                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
        $.ajax({
            url:url+"/admin/show_platform_challenge_info.do?id="+challenge_id,
            type:'POST',
            data:{
                id:challenge_id,
                reward:$("#reward").val()
            },
            dataType:'json',
            success:function (result) {
                var lottery_draw = result["data"];
                if (lottery_draw['whether_finish'] == '0'){
                    //没结束
                    $("#this_challenge_info").css("display",'none')
                    $("#remind").empty()
                    $("#remind").append("<h3>阅读挑战未到结束时间不可清算</h3>")
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
                            '<td>'+lottery_draw['loser']+'</td>'+
                            '<td>'+lottery_draw['profit_loss']+'</td>'+
                            // '<td>'+lottery_draw['final_confirm']+'</td>'+
                            '<td><button style="margin-left: 5px;" onclick="settle_accounts()">结算</button>' +
                            // '<button style="margin-left: 5px;" onclick="final_confirm('+ "'"+id+"'"+')">最终确认</button>' +
                            '</td>'+
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
    <h1>参与用户</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td>学号</td>
            <td>昵称</td>
            <td>头像</td>
            <td>挑战天数</td>
            <td>挑战单词数</td>
            <td>操作</td>
        </tr>
    </table>
    <h1>本期阅读挑战详情和结算</h1>
    <br>
    请输入给每位成功用户发放金额<input id="reward" type="text">
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="this_challenge_info">
        <tr>
            <td>总金额(资金池)</td>
            <td>成功挑战用户数</td>
            <td>成功率</td>
            <td>用户得到金额/人</td>
            <td>挑战失败用户数</td>
            <td>最后营收</td>
            <%--<td>是否最终确认</td>--%>
            <td>操作</td>
        </tr>
    </table>
    <div id="remind">
    </div>
    <table id="page">
    </table>
</center>
</body>
<script>
    function settle_accounts() {
        if (confirm("你确定要结算了？")){
            if ($("#reward").val().length === 0){
                alert("给每位成功用户发放金额不能为空！");
                return;
            }
            if (!isNotANumber($("#reward").val())){
                alert("请输入数字！");
                return;
            }
            $.ajax({
                url:url+"/admin/settle_accounts_platform_challenge.do",
                type:'POST',
                data:{
                    id:challenge_id
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
    function user_add_day(user_id, c_id) {
        if (confirm("你确定要加一？")){
            $.ajax({
                url:url+"/admin/platform_challenge_day_add.do",
                type:'POST',
                data:{
                    id:challenge_id,
                    user_id:user_id
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
    function check_series_book(id) {
        window.location.href = "read_series_book.jsp?id="+id;
    }
    function check_chapter_new_word(id) {
        window.location.href = "read_class_chapter_new_word.jsp?id="+id+"&book_id="+book_id;
    }
    function del(user_id, c_id) {
        if (confirm("你确定要删除？删除之后不可恢复！")){
            $.ajax({
                url:url+"/admin/deletePlatformChallengeUser.do",
                type:'POST',
                data:{
                    user_id:user_id,
                    id: challenge_id
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
    function user_info(user_id,series_id) {
        window.location.href = "show_read_class_series_userInfo.jsp?user_id="+user_id+"&series_id="+series_id;
    }
</script>
</html>
