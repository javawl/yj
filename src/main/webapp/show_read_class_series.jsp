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
    // 获取get参数的方法
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    var class_id = parseInt(GetQueryString("id"));
    var all_url = url+"/admin/showReadClassSeries.do?id="+class_id;
    $(document).ready(function(){
        $("#add_new").attr('href',"add_read_class_series.jsp?id="+class_id);
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    var string2;
                    if (data[i]['mp3']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<audio src="'+data[i]['mp3']+'" controls="controls"></audio>';
                    }
                    $("#daily_data").append('<tr>'+
                        '<td>'+data[i]['id']+'</td>'+
                        '<td id="name'+data[i]['id']+'" onclick="change_name('+"'"+data[i]['id']+"'"+')">'+data[i]['name']+'</td>'+
                        '<td id="introduction'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"'"+')">'+data[i]['word_number_need']+'</td>'+
                        // '<td>'+string2+'</td>'+
                        // '<td id="author'+data[i]['id']+'" onclick="change_author('+"'"+data[i]['id']+"'"+')"><div style="word-wrap:break-word">'+data[i]['author']+'</div></td>'+
                        // '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td><button style="margin-left: 5px;" onclick="check_series_book('+"'"+data[i]['id']+"'"+')">查看书籍</button><button style="margin-left: 5px;" onclick="del('+"'"+data[i]['id']+"'"+')">删除</button></td>'+
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
    // 修改词汇量(添加输入框)
    //判断是否有输入框
    var exist_introduction = 0;
    function change_sent(id){
        if (exist_introduction === 0){
            exist_introduction = 1;
            flag_id = id;
            var input_id = "book_introduction" + id;
            $("#introduction"+id).empty();
            $("#introduction"+id).append('词汇量描述：<input id='+ input_id + ' type="text"><br><button onclick="upload_sent('+"'"+id+"'"+')">提交</button>');
        }
    }
    // 修改词汇量(上传)
    function upload_sent(id) {
        $.ajax({
            url:url+"/admin/update_class_series_info.do",
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
    // 名字(添加输入框)
    //判断是否有输入框
    var exist_name = 0;
    function change_name(id){
        if (exist_name === 0){
            exist_name = 1;
            flag_id = id;
            var input_id = "series_name" + id;
            $("#name"+id).empty();
            $("#name"+id).append('名字：<input id='+ input_id + ' type="text"><br><button onclick="upload_exist_name('+"'"+id+"'"+')">提交</button>');
        }
    }
    // 名字(上传)
    function upload_exist_name(id) {
        $.ajax({
            url:url+"/admin/update_class_series_info.do",
            type:'POST',
            data:{
                inner: document.getElementById("series_name" + id).value,
                id: id,
                type: "name"
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
        exist_name = 0;
        history.go(0);
        $("html, body").scrollTop(0).animate({scrollTop: $("#name"+id).offset().top});
    }
    //--------------------------------------------------------------------
</script>
<body>
<center>
    <h1>系列</h1>
    <br>
    <table cellpadding="6" width="87%" border="1" cellspacing="0" id="daily_data">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;">
                <button style="float: right" id="select">
                    <a href="" id="add_new">新增</a>
                </button>
            </td>
        </tr>
        <tr>
            <td>序号</td>
            <td>系列名称</td>
            <td>词汇量需求</td>
            <td>操作</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function check_series_book(id) {
        window.location.href = "read_series_book.jsp?id="+id;
    }
    function check_chapter_new_word(id) {
        window.location.href = "read_class_chapter_new_word.jsp?id="+id+"&book_id="+book_id;
    }
    function del(id) {
        // if (confirm("你确定要删除？删除之后不可恢复！")){
        //     $.ajax({
        //         url:url+"/admin/deleteReadClassBookChapter.do",
        //         type:'POST',
        //         data:{
        //             id:id
        //         },
        //         dataType:'json',
        //         success:function (result) {
        //             var code = result['code'];
        //             var msg = result['msg'];
        //             if (code != 200){
        //                 alert(msg);
        //             }else {
        //                 alert(msg);
        //                 history.go(0);
        //             }
        //         },
        //         error:function (result) {
        //             console.log(result);
        //             alert("服务器出错！");
        //         }
        //     });
        // }
    }
</script>
</html>
