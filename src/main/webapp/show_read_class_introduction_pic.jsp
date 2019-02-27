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
                url:url+"/admin/showReadClassIntroductionPic.do",
                type:'GET',
                dataType:'json',
                success:function (result) {
                    var data = result["data"];
                    for(var i = 0; i < data.length; i++){
                        var string1;
                        var string2;
                        if (data[i]['pic']==''){
                            string1 = '此资源为空'
                        }else {
                            string1 = '<img style="max-width: 550px; max-height: 800px;" src="'+data[i]['pic']+'">';
                        }
                        $("#special").append('<tr>'+
                            '<td style="width: 4%;">'+data[i]['order']+'</td>'+
                            '<td style="width: 4%;">'+string1+'</td>'+
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
    <h1>报名页介绍页的往期人的评论图片</h1>

    <table cellpadding="8" width="87%" border="1" cellspacing="0" id="special">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;">
            </td>
            <td style="border-left: 0;">
                <button style="float: right" id="select">
                    <a href="add_read_class_introduction_pic.jsp">新增</a>
                </button>
            </td>
        </tr>
        <tr>
            <td>顺序</td>
            <td>图片</td>
            <td>操作</td>
        </tr>
    </table>



    <table id="page">
    </table>
</center>
</body>
<script>
    function del(id) {
        if (confirm("你确定要删除此图片？删除之后不可恢复！")){
            $.ajax({
                url:url+"/admin/deleteReadClassIntroductionPic.do",
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