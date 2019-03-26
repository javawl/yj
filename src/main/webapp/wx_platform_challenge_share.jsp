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
    var size = 15;
    var all_url = url+"/admin/showWxPlatformSharePic.do?page="+page+"&size="+size+"&type="+type;
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    var string1;
                    var string2;
                    var string3;
                    if (data[i]['wx_platform_share_pic_top']==''){
                        string1 = '此资源为空'
                    }else {
                        string1 = '<img style="max-width: 300px; max-height: 300px;" src="'+data[i]['wx_platform_share_pic_top']+'">';
                    }
                    if (data[i]['wx_platform_share_pic_middle']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 300px; max-height: 300px;" src="'+data[i]['wx_platform_share_pic_middle']+'">';
                    }
                    if (data[i]['wx_platform_share_pic_outside']==''){
                        string3 = '此资源为空'
                    }else {
                        string3 = '<img style="max-width: 300px; max-height: 300px;" src="'+data[i]['wx_platform_share_pic_outside']+'">';
                    }
                    $("#daily_data").append('<tr>'+
                        '<td style="max-width: 500px;" onclick="upload_pic_click('+"'"+"uploadWxPlatformSharePicTop"+"'"+')">'+string1+'</td>'+
                        '<td style="max-width: 500px;" onclick="upload_pic_click('+"'"+"uploadWxPlatformSharePicMiddle"+"'"+')">'+string2+'</td>'+
                        '<td style="max-width: 500px;" onclick="upload_pic_click('+"'"+"uploadWxPlatformSharePicOutside"+"'"+')">'+string3+'</td>'+
                        '<td id="introduction'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"'"+')">'+data[i]['wx_platform_share_sent_outside']+'</td>'+
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
    // var pic_id;
    function upload_pic_click(id_name) {
        // pic_id = id;
        document.getElementById(id_name).click();
    }
    function upload_pic(id_name) {
        // alert(pic_id);
        var formData = new FormData();
        formData.append('upload_file', $('#'+id_name)[0].files[0]);
        // formData.append('id', pic_id);
        $.ajax({
            url:url+"/admin/"+ id_name +".do",
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
    // 修改外分享语句
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
    // 修改外分享语句(上传)
    function upload_sent(id) {
        $.ajax({
            url:url+"/admin/updateWxPlatformShareSent.do",
            type:'POST',
            data:{
                inner: document.getElementById("book_introduction" + id).value
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
</script>
<body>
<center>
    <h1>万元挑战赛分享图</h1>
    <input type="file" id="uploadWxPlatformSharePicTop" value="上传" style="display: none;" onchange="upload_pic('uploadWxPlatformSharePicTop')" />
    <input type="file" id="uploadWxPlatformSharePicMiddle" value="上传" style="display: none;" onchange="upload_pic('uploadWxPlatformSharePicMiddle')" />
    <input type="file" id="uploadWxPlatformSharePicOutside" value="上传" style="display: none;" onchange="upload_pic('uploadWxPlatformSharePicOutside')" />
    <br>
    <table cellpadding="6" width="87%" border="1" cellspacing="0" id="daily_data">
        <tr>
            <td>内分享上部图片</td>
            <td>内分享中部图片</td>
            <td>外分享图片</td>
            <td>外分享语句</td>
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
