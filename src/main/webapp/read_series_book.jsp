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
    // 获取get参数的方法
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    var page = parseInt(GetQueryString("page"));
    var type = parseInt(GetQueryString("type"));
    var series_id = parseInt(GetQueryString("id"));
    var size = 15;
    var all_url = url+"/admin/showReadClassSeriesBook.do?&id="+series_id;
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    var string2;
                    if (data[i]['pic']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 550px; max-height: 550px;" src="'+data[i]['pic']+'">';
                    }
                    $("#daily_data").append('<tr>'+
                        '<td>'+data[i]['id']+'</td>'+
                        '<td>'+data[i]['name']+'</td>'+
                        // '<td id="introduction'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"'"+')">'+data[i]['introduction']+'</td>'+
                        '<td>'+data[i]['introduction']+'</td>'+
                        // '<td onclick="upload_pic_click('+"'"+data[i]['id']+"'"+')">'+string2+'</td>'+
                        '<td>'+string2+'</td>'+
                        // '<td id="author'+data[i]['id']+'" onclick="change_author('+"'"+data[i]['id']+"'"+')"><div style="word-wrap:break-word">'+data[i]['author']+'</div></td>'+
                        '<td>'+data[i]['author']+'</div></td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
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
    //--------------------------------------------------------------------
    // 修改图片(点击隐藏上传框)
    //用来上传图片
    var pic_id;
    function upload_pic_click(id) {
        pic_id = id;
        document.getElementById("pic").click();
    }
    function upload_pic() {
        alert(pic_id);
        var formData = new FormData();
        formData.append('upload_file', $('#pic')[0].files[0]);
        formData.append('id', pic_id);
        $.ajax({
            url:url+"/admin/uploadReadClassPic.do",
            type:'POST',
            data:formData,
            dataType:'json',
            processData: false,
            contentType: false,
            success:function (result) {
                var code = result['code'];
                var msg = result['msg'];
                if (code != 200){
                    alert(msg);
                }else {
                    alert(msg);
                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
    }
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
    // 修改简介(添加输入框)
    //判断是否有输入框
    var exist_introduction = 0;
    function change_sent(id){
        if (exist_introduction === 0){
            exist_introduction = 1;
            flag_id = id;
            var input_id = "book_introduction" + id;
            $("#introduction"+id).empty();
            $("#introduction"+id).append('简介：<input id='+ input_id + ' type="text"><br><button onclick="upload_sent('+"'"+id+"'"+')">提交</button>');
        }
    }
    // 修改简介(上传)
    function upload_sent(id) {
        $.ajax({
            url:url+"/admin/update_class_book_info.do",
            type:'POST',
            data:{
                inner: document.getElementById("book_introduction" + id).value,
                id: id,
                type: "introduction"
            },
            dataType:'json',
            async: false,
            success:function (result) {
                var code = result['code'];
                var msg = result['msg'];
                if (code != 200){
                    alert(msg);
                }else {
                    alert(msg);
                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
        exist_introduction = 0;
        history.go(0);
        $("html, body").scrollTop(0).animate({scrollTop: $("#introduction"+id).offset().top});
    }
    //--------------------------------------------------------------------
    //--------------------------------------------------------------------
    // 修改作者(添加输入框)
    //判断是否有输入框
    var exist_author = 0;
    function change_author(id){
        if (exist_author === 0){
            exist_author = 1;
            flag_id = id;
            var input_id = "book_author" + id;
            $("#author"+id).empty();
            $("#author"+id).append('作者：<input id='+ input_id + ' type="text"><br><button onclick="upload_exist_author('+"'"+id+"'"+')">提交</button>');
        }
    }
    // 修改作者(上传)
    function upload_exist_author(id) {
        $.ajax({
            url:url+"/admin/update_class_book_info.do",
            type:'POST',
            data:{
                inner: document.getElementById("book_author" + id).value,
                id: id,
                type: "author"
            },
            dataType:'json',
            async: false,
            success:function (result) {
                var code = result['code'];
                var msg = result['msg'];
                if (code != 200){
                    alert(msg);
                }else {
                    alert(msg);
                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
        exist_author = 0;
        history.go(0);
        $("html, body").scrollTop(0).animate({scrollTop: $("#author"+id).offset().top});
    }
    //--------------------------------------------------------------------
</script>
<body>
<center>
    <h1>阅读书籍</h1>
    <input type="file" id="pic" value="上传" style="display: none;" onchange="upload_pic(id)" />
    <br>
    <table cellpadding="6" width="87%" border="1" cellspacing="0" id="daily_data">
        <%--<tr>--%>
        <%--<td style="border-right: 0;"></td>--%>
        <%--<td style="border-left: 0;border-right: 0;"></td>--%>
        <%--<td style="border-left: 0;border-right: 0;"></td>--%>
        <%--<td style="border-left: 0;border-right: 0;"></td>--%>
        <%--<td style="border-left: 0;border-right: 0;"></td>--%>
        <%--<td style="border-left: 0;border-right: 0;"></td>--%>
        <%--<td style="border-left: 0;">--%>
        <%--<button style="float: right" id="select">--%>
        <%--<a href="add_welfare_service.jsp">新增</a>--%>
        <%--</button>--%>
        <%--</td>--%>
        <%--</tr>--%>
        <tr>
            <td>序号</td>
            <td>书籍名称</td>
            <td>书籍简介</td>
            <td>书籍图片</td>
            <td>书籍作者</td>
            <td>章节数</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function check_chapter(id) {
        window.location.href = "read_class_chapter.jsp?id="+id;
    }
</script>
</html>
