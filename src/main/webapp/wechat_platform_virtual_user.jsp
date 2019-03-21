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
    var type = parseInt(GetQueryString("type"));
    var size = 15;
    var all_url = url+"/admin/show_virtual_user_platform.do?page="+page+"&size="+size+"&type="+type;
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                count += parseInt(result["msg"]);
                //计算页数
                var page_no = Math.ceil(count / size);
                if (page == 1){
                    $("#page").append('<td><p>第一页</p></td>');
                }else{
                    $("#page").append('<td><a href="'+url+'/wechat_platform_virtual_user.jsp?page=1&size='+size+'">第一页</a></td>');
                }
                var ff = 0;
                var f = 0;
                if (page > 4){
                    f += page - 4;
                }
                if (f != 0){
                    $("#page").append('<td><p>....</p></td>');
                }
                for(var z = f; z < page_no; z++){
                    var no = z + 1;
                    if (no == page){
                        $("#page").append('<td><p>'+no+'</p></td>');
                    }else {
                        $("#page").append('<td><a href="'+url+'/wechat_platform_virtual_user.jsp?page='+no+'&size='+size+'">'+no+'</a></td>');
                    }
                    if (ff == 8)break;
                    ff++;
                }
                if (ff != 8){
                    $("#page").append('<td><p>....</p></td>');
                }
                if (page == page_no){
                    $("#page").append('<td><p>最后一页</p></td>');
                }else{
                    $("#page").append('<td><a href="'+url+'/wechat_platform_virtual_user.jsp?page='+page_no+'&size='+size+'">最后一页</a></td>');
                }

                for(var i = 0; i < data.length; i++){
                    var string2;
                    if (data[i]['portrait']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 550px; max-height: 550px;" src="'+data[i]['portrait']+'">';
                    }
                    $("#author_info").append('<tr>'+
                        '<td style="width: 4%;">'+data[i]['id']+'</td>'+
                        '<td style="width: 4%;">'+string2+'</td>'+
                        '<td style="width: 4%;">'+data[i]['username']+'</td>'+
                        '<td style="width: 4%;">'+data[i]['gender']+'</td>'+
                        '<td style="width: 6%;">'+data[i]['personality_signature']+'</td>'+
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
    <h1>虚拟用户查看</h1>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="author_info">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;">
                <button style="float: right"><a href="add_platform_virtual_user.jsp">新建</a></button>
            </td>
        </tr>
        <tr>
            <td>序号</td>
            <td>头像</td>
            <td>昵称</td>
            <td>性别</td>
            <td>个性签名</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    // function del(id) {
    //     if (confirm("你确定要删除此虚拟用户？删除之后不可恢复！")){
    //         $.ajax({
    //             url:url+"/admin/delete_virtual_user.do",
    //             type:'POST',
    //             data:{
    //                 id:id
    //             },
    //             dataType:'json',
    //             success:function (result) {
    //                 var code = result['code'];
    //                 var msg = result['msg'];
    //                 if (code != 200){
    //                     alert(msg);
    //                 }else {
    //                     alert(msg);
    //                     history.go(0);
    //                 }
    //             },
    //             error:function (result) {
    //                 console.log(result);
    //                 alert("服务器出错！");
    //             }
    //         });
    //     }
    // }
</script>
</html>
