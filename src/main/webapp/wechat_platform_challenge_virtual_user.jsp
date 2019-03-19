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
    $(document).ready(function(){
        $.ajax({
            url: url+"/admin/showPlatformChallengeVirtualUser.do?id="+challenge_id,
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
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['word_number']+'</td>'+
                        '<td style="width: 6%;"><input type="text" id="add_number' + data[i]['user_id'] +'"><button style="margin-left: 5px;" onclick="user_add_day('+"'"+data[i]['user_id']+"',"+ "'"+challenge_id+"'"+')">添加</button></td>'+
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
    <h1>参与虚拟用户</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td>昵称</td>
            <td>头像</td>
            <td>挑战单词数</td>
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
    function user_add_day(user_id, c_id) {
        if (confirm("你确定要增加？")){
            $.ajax({
                url:url+"/admin/platform_challenge_virtual_user_day_add.do",
                type:'POST',
                data:{
                    id:challenge_id,
                    user_id:user_id,
                    number:$("#add_number" + user_id).val()
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
    function user_info(user_id,series_id) {
        window.location.href = "show_read_class_series_userInfo.jsp?user_id="+user_id+"&series_id="+series_id;
    }
</script>
</html>
