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
    var all_url = url+"/admin/show_user_bill.do";
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type: 'POST',
            data: {
                id: id
            },
            dataType:'json',
            success:function (result) {
                var data = result["data"]['bill'];
                // count += parseInt(result["msg"]);
                // //计算页数
                // var page_no = Math.ceil(count / size);
                // if (page == 1){
                //     $("#page").append('<td><p>第一页</p></td>');
                // }else{
                //     $("#page").append('<td><a href="'+url+'/show_word_challenge.jsp?page=1&size='+size+'">第一页</a></td>');
                // }
                // var ff = 0;
                // var f = 0;
                // if (page > 4){
                //     f += page - 4;
                // }
                // if (f != 0){
                //     $("#page").append('<td><p>....</p></td>');
                // }
                // for(var z = f; z < page_no; z++){
                //     var no = z + 1;
                //     if (no == page){
                //         $("#page").append('<td><p>'+no+'</p></td>');
                //     }else {
                //         $("#page").append('<td><a href="'+url+'/show_word_challenge.jsp?page='+no+'&size='+size+'">'+no+'</a></td>');
                //     }
                //     if (ff == 8)break;
                //     ff++;
                // }
                // if (ff != 8){
                //     $("#page").append('<td><p>....</p></td>');
                // }
                // if (page == page_no){
                //     $("#page").append('<td><p>最后一页</p></td>');
                // }else{
                //     $("#page").append('<td><a href="'+url+'/show_word_challenge.jsp?page='+page_no+'&size='+size+'">最后一页</a></td>');
                // }

                for(var i = 0; i < data.length; i++){
                    $("#info").append('<tr>'+
                        '<td style="width: 4%;">'+data[i]['statement']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['bill']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['set_time']+'</td>'+
                        '</tr>');
                }
                var string2;
                if (result["data"]['portrait']==''){
                    string2 = '此资源为空'
                }else {
                    string2 = '<img style="max-width: 50px; max-height: 50px;" src="'+result["data"]['portrait']+'">';
                }

                $("#user").append('<p>用户名: '+ result["data"]['username'] +'</p><br><p>头像: '+ string2 +'</p><br>')
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
    <div id="user">
        <h1>用户参加单词挑战详情查看</h1>
        <br>
    </div>
    <h1>账单详情</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="info">
        <tr>
            <td>盈亏</td>
            <td>描述</td>
            <td>时间</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function single(id) {
        window.location.href = "single_word_challenge.jsp?id="+id;
    }
    function del(id) {
        if (confirm("暂无删除功能")){
            // $.ajax({
            //     url:url+"/admin/delete_lottery_draw.do",
            //     type:'POST',
            //     data:{
            //         id:id
            //     },
            //     dataType:'json',
            //     success:function (result) {
            //         var code = result['code'];
            //         var msg = result['msg'];
            //         if (code != 200){
            //             alert(msg);
            //         }else {
            //             alert(msg);
            //             history.go(0);
            //         }
            //     },
            //     error:function (result) {
            //         console.log(result);
            //         alert("服务器出错！");
            //     }
            // });
        }
    }
</script>
</html>