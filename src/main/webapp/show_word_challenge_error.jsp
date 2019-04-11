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
    var page = parseInt(GetQueryString("page"));
    var id = parseInt(GetQueryString("id"));
    var size = 15;
    var all_url = url+"/abnormalData/wordChallengeAbnormalClockUser.do?challengeId="+id;
    $(document).ready(function(){
        $.ajax({
            url:all_url,
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
                    $("#info").append('<tr>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['oldInsist']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['realInsist']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['oldChallengeInsist']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['realChallengeInsist']+'</td>'+
                        '<td style="width: 6%;"><button style="margin-left: 5px;" onclick="single('+"'"+data[i]['user_id']+"',"+ "'"+id+"'"+')">纠正</button></td>'+
                        '</tr>');
                }
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
    <h1>单词挑战可能出现异常的用户</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="info">
        <tr>
            <td>用户头像</td>
            <td>用户名</td>
            <td>目前坚持天数</td>
            <td>正确坚持总天数</td>
            <td>目前单词挑战坚持天数</td>
            <td>正确坚持挑战天数</td>
            <td>操作</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function single(userId, id) {
        if (confirm("你确定要纠正？纠正不可返回现状态")){
        $.ajax({
            url:url+"/abnormalData/wordChallengeAbnormalClockUserCorrect.do",
            type:'POST',
            data:{
                userId:userId,
                challengeId:id
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