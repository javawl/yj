<%@ page import="com.yj.common.Const" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <style type="text/css" rel="stylesheet">
        body{ margin: 0 auto; padding: 0; width: 100%;}
        h1{ margin-top: 3rem;}
    </style>
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
        var all_url = url+"/various/daily_pic_info.do?page="+page+"&size="+size+"&type="+type;
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
                        $("#page").append('<td><a href="'+url+'?page=1&size='+size+'">第一页</a></td>');
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
                            $("#page").append('<td><a href="'+url+'?page='+no+'&size='+size+'">'+no+'</a></td>');
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
                        $("#page").append('<td><a href="'+url+'?page='+page_no+'&size='+size+'">最后一页</a></td>');
                    }

                    for(var i = 0; i < data.length; i++){
                        var string1;
                        var string2;
                        if (data[i]['daily_pic']==''){
                            string1 = '此资源为空'
                        }else {
                            string1 = '<img src="'+data[i]['daily_pic']+'">';
                        }
                        if (data[i]['small_pic']==''){
                            string2 = '此资源为空'
                        }else {
                            string2 = '<img src="'+data[i]['small_pic']+'">';
                        }
                        $("#special").append('<tr>'+
                                '<td style="width: 4%;">'+data[i]['id']+'</td>'+
                                '<td style="width: 4%;">'+string2+'</td>'+
                                '<td style="width: 4%;">'+string1+'</td>'+
                                '<td style="width: 4%;">'+data[i]['set_time']+'</td>'+
                                '<td style="width: 6%;"><button style="margin-left: 5px;" onclick="del('+"'"+data[i]['id']+"'"+')">删除</button></td>'+
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
</head>
<body>
<center>
    <h1>每日一图</h1>

    <table cellpadding="8" width="100%" border="1" cellspacing="0" id="special">
        <tr>
            <button style="float: right" id="select">
                <a href="add_daily_pic.jsp">新增</a>
            </button>
        </tr>
        <tr>
            <td style="width: 4%;">每日一图id</td>
            <td style="width: 4%;">每日一图截取图</td>
            <td style="width: 4%;">每日一图完整图</td>
            <td style="width: 4%;">日期</td>
            <td  style="width: 6%;">操作</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function del(id) {
        if (confirm("你确定要删除此单词？删除之后不可恢复！")){
            $.ajax({
                url:url+"/admin/delete_daily_pic.do",
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
</script>
</html>