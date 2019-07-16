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
        *{ cursor:url("https://file.ourbeibei.com/l_e/static/icon/mouse_love.png"),auto;}
        body{ margin: 0 auto; padding: 0; width: 100%; background-image:url("https://file.ourbeibei.com/l_e/static/images/love_admin.png");background-repeat: repeat-y;background-size:100%;}
        h1{ margin-top: 3rem; color: hotpink;}
        .hidden_background{ float: left; margin-top: -2rem; margin-left: 2rem; width: 160px; height: 30px;letter-spacing:1.5px; border-radius:20px; font: bold 12px/25px Arial, sans-serif; text-align: center;background-image: linear-gradient(to right,  #FAB6D0, hotpink); color: snow;}
        .pick_button{width: 150px; height: 30px;letter-spacing:1.5px; border-radius:20px; font: bold 12px/25px Arial, sans-serif; text-align: center;background-image: linear-gradient(to right,  #FAB6D0, hotpink);}
        #select{width: 128px;}
        input[type=text] {
            width: 52px;
            box-sizing: border-box;
            border: 1px solid deeppink;
            border-radius: 20px;
            font-size: 10px;
            color: snow;
            /*background-color: white;*/
            background-image: linear-gradient(to right,  #FAB6D0, hotpink);
            padding: 8px 20px 8px 8px;
            -webkit-transition: width 0.4s ease-in-out;
            transition: width 0.4s ease-in-out;
            outline: none;
        }
        input::-webkit-input-placeholder, textarea::-webkit-input-placeholder {
            /* WebKit browsers */
            color: snow;
        }
        input:-moz-placeholder, textarea:-moz-placeholder {
            /* Mozilla Firefox 4 to 18 */
            color: snow;
        }
        input::-moz-placeholder, textarea::-moz-placeholder {
            /* Mozilla Firefox 19+ */
            color: snow;
        }
        input:-ms-input-placeholder, textarea:-ms-input-placeholder {
            /* Internet Explorer 10+ */
            color: snow;
        }
        input[type=text]:focus { width: 100%;}
        .operation_button{
            border: 1px solid deeppink;
            border-radius: 20px;
            font-size: 10px;
            color: snow;
            background-image: linear-gradient(to right,  #FAB6D0, hotpink);
            padding: 8px 8px 8px 8px;
            float: left;
        }
        #nav ul{
            list-style: none;
            display: none;
            margin: 0;
            width: 150px;
            position: absolute;
            top: 50px;
            left: 5px;
            background-image: url("https://file.ourbeibei.com/l_e/static/images/pink_background.jpg");
            background-size: 100%;
            border: solid 1px hotpink;
            -webkit-border-radius: 10px;
            -moz-border-radius: 10px;
            border-radius: 10px;
            color: darkorange;
            font-size: 17px;
            padding: 5px 5px 5px 5px;
            --webkit-box-shadow: 0 1px 3px rgba(0,0,0, .3);
            -moz-box-shadow: 0 1px 3px rgba(0,0,0, .3);
            box-shadow: 0 1px 3px rgba(0,0,0, .3);
        }
        #nav td:hover > ul{
            display: block;
        }
        #nav td li:hover{
            /*background: none;*/
            /*background-color: snow;*/
            color: purple;
        }
        .pop_up_box{
            display: none;
            position: absolute;
            left: 50%;
            top: 50%;
            margin-left: -200px;
            margin-top: -300px;
            background-color: hotpink;
            width: 400px;
            height: 600px;
            border-radius: 50px;
            background-image: url("https://file.ourbeibei.com/l_e/static/images/admin_love_pop_up_bg.jpg");
            background-size: 100%;
            background-repeat: no-repeat;
            z-index: 5;
            list-style: none;
        }
        .rank{ width: 225px; height: 20px; color: snow; background-image: linear-gradient(to right,  #FAB6D0, hotpink); padding: 7px 7px 7px 7px; border-radius: 20px; border:solid 2px hotpink; margin-top: 6rem;}
        .time_title{ width: 225px; height: 20px; color: snow; background-image: linear-gradient(to right,  #FAB6D0, hotpink); padding: 7px 7px 7px 7px; border-radius: 20px; border:solid 2px hotpink; margin-top: 2rem;}
        .rank ul{
            list-style: none;
            display: none;
            margin: 0;
            width: 150px;
            position: absolute;
            top: 35px;
            left: 40px;
            background-image: url("https://file.ourbeibei.com/l_e/static/images/pink_background.jpg");
            background-size: 100%;
            border: solid 1px hotpink;
            -webkit-border-radius: 10px;
            -moz-border-radius: 10px;
            border-radius: 10px;
            color: darkorange;
            font-size: 17px;
            padding: 5px 5px 5px 5px;
            --webkit-box-shadow: 0 1px 3px rgba(0,0,0, .3);
            -moz-box-shadow: 0 1px 3px rgba(0,0,0, .3);
            box-shadow: 0 1px 3px rgba(0,0,0, .3);
        }
        .rank:hover > ul{
            display: block;
        }
        .rank li:hover{
            /*background: none;*/
            /*background-color: snow;*/
            color: purple;
        }
        .rank li{
            position: relative;
        }
        .heart{ position: absolute; left: 11px; display: inline; top: 3.5px;}
        .time_container{ width: 84px; float: left; margin-top: 2rem; }
        .confirm_button{ width: 150px; height: 20px; color: snow; background-image: linear-gradient(to right,  #FAB6D0, hotpink); padding: 7px 7px 7px 7px; border-radius: 20px; border:solid 2px hotpink; margin-top: 6rem; }
        .new_virtual_user{
            display: none;
            position: absolute;
            left: 50%;
            top: 50%;
            margin-left: -200px;
            margin-top: -300px;
            background-color: hotpink;
            width: 400px;
            height: 600px;
            border-radius: 50px;
            background-image: url("https://file.ourbeibei.com/l_e/static/images/admin_love_pop_up_bg.jpg");
            background-size: 100%;
            background-repeat: no-repeat;
            z-index: 5;
            list-style: none;
        }
        .first_line{ width: 170px; height: 40px; display: inline-block; }
        .first_line input{ width: 85px; }
        .second_line{ width: 100px; height: 40px; display: inline-block; }
        .second_line input{ width: 50px; }
        .new_virtual_user_line_box{width: 400px; height: 40px; display: block;  margin-top: 2rem; }
        .three_line{width: 400px; height: 40px; display: block;  margin-top: 2rem; }
        .three_line input{width: 100px; }
        .four_line{width: 400px; height: 40px; display: block;  margin-top: 2rem; }
        .four_line input{width: 100px; }
        .new_virtual_user_title{ width: 180px; height: 20px; color: snow; background-image: linear-gradient(to right,  #FAB6D0, hotpink); padding: 7px 7px 7px 7px; border-radius: 20px; border:solid 2px hotpink; margin-top: 4rem;}
        .upload_pink{ width: 60px; height: 60px; margin-top: 1.5rem; border: solid 1px hotpink; }
        .confirm_virtual_button{ width: 150px; height: 20px; color: snow; background-image: linear-gradient(to right,  #FAB6D0, hotpink); padding: 7px 7px 7px 7px; border-radius: 20px; border:solid 2px hotpink; margin-top: 2rem; }
        .tag_ul{
            list-style: none;
            display: block;
            margin: 0;
            width: 120px;
            background-image: url("https://file.ourbeibei.com/l_e/static/images/pink_background.jpg");
            background-size: 100%;
            border: solid 1px hotpink;
            -webkit-border-radius: 10px;
            -moz-border-radius: 10px;
            border-radius: 10px;
            color: darkorange;
            font-size: 17px;
            padding: 5px 5px 5px 5px;
            --webkit-box-shadow: 0 1px 3px rgba(0,0,0, .3);
            -moz-box-shadow: 0 1px 3px rgba(0,0,0, .3);
            box-shadow: 0 1px 3px rgba(0,0,0, .3);
        }
        .tag_ul li:hover{
            /*background: none;*/
            /*background-color: snow;*/
            color: purple;
        }
        .tag_delete{
            float: right;
            margin-right: 10px;
            margin-top: 4px;
        }
    </style>
</head>
<script type="text/javascript">
    var count = 0;
    <%
        String url = Const.DOMAIN_NAME;
        String root_url = Const.FTP_PREFIX;
    %>
    var url = "<%=url %>";
    var root_url = "<%=root_url %>";
    // 获取get参数的方法
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    //去除背景图片
    function cleanBackgroundPic() {
        document.getElementById("bd").style.background = "none";
    }
    var page = parseInt(GetQueryString("page"));
    var type = parseInt(GetQueryString("type"));
    var size = 15;
    var all_url = url+"/admin/showReadClassBook.do?page="+page+"&size="+size+"&type="+type;
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
                    $("#page").append('<td><a href="'+url+'/read_class_book.jsp?page=1&size='+size+'">第一页</a></td>');
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
                        $("#page").append('<td><a href="'+url+'/read_class_book.jsp?page='+no+'&size='+size+'">'+no+'</a></td>');
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
                    $("#page").append('<td><a href="'+url+'/read_class_book.jsp?page='+page_no+'&size='+size+'">最后一页</a></td>');
                }
                for(var i = 0; i < data.length; i++){
                    var string2;
                    var operationButton;
                    var tag;
                    if (data[i]['pic']==''){
                        string2 = '此资源为空'
                    }else {
                        string2 = '<img style="max-width: 550px; max-height: 550px;" src="'+data[i]['pic']+'">';
                    }

                    tag = '<ul class="tag_ul">' +
                        '    <li>标签一<img id="heart1" class="tag_delete" src="https://file.ourbeibei.com/l_e/static/icon/delete.png"></li>' +
                        '    <li>标签二<img id="heart2" class="tag_delete" src="https://file.ourbeibei.com/l_e/static/icon/delete.png"></li>' +
                        '    <li>标签三<img id="heart3" class="tag_delete" src="https://file.ourbeibei.com/l_e/static/icon/delete.png"></li>' +
                        '    <li>标签四<img id="heart4" class="tag_delete" src="https://file.ourbeibei.com/l_e/static/icon/delete.png"></li>' +
                        '    <li>标签五<img id="heart5" class="tag_delete" src="https://file.ourbeibei.com/l_e/static/icon/delete.png"></li>' +
                        '    <li style="position: relative"><div style="width: 85px;"><input style="height: 25px; margin-top: 4px;" type="text" placeholder="标签"></div><img id="heart5" style=" position: absolute; bottom: 6px; left: 94px;" src="https://file.ourbeibei.com/l_e/static/icon/add.png"> </li>' +
                        '</ul>';

                    data[i]['status'] = 2;
                    if (data[i]['status'] == 1){
                        operationButton = '<div class="operation_button" style="margin-left: 5px;width: 28px;" onclick="check_chapter('+"'"+data[i]['id']+"'"+')">通过</div><div class="operation_button" style="margin-left: 5px;width: 42px;" onclick="goBlack('+"'"+data[i]['id']+"'"+')">不合格</div>';
                    }else if (data[i]['status'] == 2) {
                        operationButton = '<div class="operation_button" style="margin-left: 5px;width: 28px;" onclick="matchingBoxPopUp('+"'"+data[i]['id']+"'"+')">展示</div><div class="operation_button" style="margin-left: 5px;width: 28px;" onclick="goBlack('+"'"+data[i]['id']+"'"+')">封号</div>';
                    }
                    $("#daily_data").append('<tr>'+
                        '<td>'+data[i]['id']+'</td>'+
                        // '<td>'+data[i]['name']+'</td>'+
                        '<td id="wx_name'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','wx_name'"+')">'+data[i]['name']+'</td>'+
                        '<td id="change_gender'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','change_gender'"+')">'+data[i]['name']+'</td>'+
                        '<td id="intention'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','intention'"+')">'+data[i]['name']+'</td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td id="status'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','status'"+')">'+data[i]['name']+'</td>'+
                        '<td id="signature'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','signature'"+')">'+data[i]['name']+'</td>'+
                        // '<td onclick="upload_pic_click('+"'"+data[i]['id']+"'"+')">'+string2+'</td>'+
                        // '<td id="author'+data[i]['id']+'" onclick="change_author('+"'"+data[i]['id']+"'"+')"><div style="word-wrap:break-word">'+data[i]['author']+'</div></td>'+
                        '<td><div>'+tag+'</div></td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td id="age'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','age'"+')">'+data[i]['name']+'</td>'+
                        '<td id="vip'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','vip'"+')">'+data[i]['name']+'</td>'+

                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td id="condition'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"','condition'"+')">'+data[i]['name']+'</td>'+
                        '<td><div>'+data[i]['chapter_number']+'</div></td>'+
                        '<td>'+ operationButton +'</td>'+
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
            url:url+"/admin/uploadReadClassPic",
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
    var existFlag;
    var htmlFlag;
    function change_sent(id, type){
        if (exist_introduction === 0){
            var input_id = "dating_" + type;
            existFlag = type + id;
            htmlFlag = $("#" + existFlag).html();
            event.stopPropagation();//阻止冒泡
            var submitButton = '<div class="operation_button" style="width: 25px;" onclick="upload_sent('+"'"+id+"','"+input_id+"'"+')">提交</div>';
            exist_introduction = 1;
            flag_id = id;
            $("#"+type+id).empty();
            $("#"+type+id).append('输入：<input id='+ input_id + ' type="text"><br>'+submitButton);
        }else {
            // 复原
            exist_introduction = 0;
            $("#" + existFlag).html(htmlFlag);
            var input_id = "dating_" + type;
            existFlag = type + id;
            event.stopPropagation();//阻止冒泡
            htmlFlag = $("#" + existFlag).html();
            var submitButton = '<div class="operation_button" style="width: 25px;" onclick="upload_sent('+"'"+id+"','"+input_id+"'"+')">提交</div>';
            exist_introduction = 1;
            flag_id = id;
            $("#"+type+id).empty();
            $("#"+type+id).append('输入：<input id='+ input_id + ' type="text"><br>'+submitButton);
        }
    }
    // 修改简介(上传)
    function upload_sent(id, type) {
        var targetUrl;
        var paramName;
        switch (type) {
            case "wx_name":
                targetUrl = "/lzy/update_name.do";
                paramName = 'name';
                break;
            case "change_gender":
                targetUrl = "/lzy/update_gender.do";
                paramName = "gender";
                break;
            case "intention":
                targetUrl = "/lzy/update_intention.do";
                paramName = "intention";
                break;
            case "signature":
                targetUrl = "/lzy/update_signature.do";
                paramName = "signature";
                break;
            case "age":
                targetUrl = "/lzy/update_age.do";
                paramName = "age";
                break;
            case "status":
                targetUrl = "/lzy/update_status.do";
                paramName = "status";
                break;
            case "vip":
                targetUrl = "/lzy/update_vip.do";
                paramName = "vip";
                break;
            case "condition":
                targetUrl = "/lzy/update_condition.do";
                paramName = "condition";
                break;
            default:
                alert("非法类别");
        }
        var formData = new FormData();
        formData.append(paramName, document.getElementById(type + id).value);
        formData.append('id', id);
        $.ajax({
            url:url+targetUrl,
            type:'POST',
            data:formData,
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
        $("html, body").scrollTop(0).animate({scrollTop: $("#"+type+id).offset().top});
    }
    $(document).click(function(){
        // 复原
        exist_introduction = 0;
        $("#" + existFlag).html(htmlFlag);
    });

</script>
<body id="bd">
<center>
    <div class="hidden_background" onclick="cleanBackgroundPic()">隐藏背景</div>
    <h1>恋爱卡片</h1>
    <%--弹出选择展位框--%>
    <div class="pop_up_box" id="pop_up_box" onclick="outside = false">
        <div class="rank" style="position: relative;">
            <p style="margin: 0px;">选择卡片位</p>
            <ul>
                <li>卡片位 一<img id="heart1" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 二<img id="heart2" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 三<img id="heart3" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 四<img id="heart4" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 五<img id="heart5" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 六<img id="heart6" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 七<img id="heart7" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 八<img id="heart8" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 九<img id="heart9" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
                <li>卡片位 十<img id="heart10" class="heart" src="https://file.ourbeibei.com/l_e/static/icon/heart.png"></li>
            </ul>
        </div>
        <div class="time_title">选择日期</div>
        <div class="time_container" style="margin-left: 74px;"><input type="text" name="year" placeholder="年份"></div>
        <div class="time_container"><input type="text" name="month" placeholder="月份(xx)"></div>
        <div class="time_container"><input type="text" name="year" placeholder="日(xx)"></div>
        <input type="text" style="display: none;" id="card_id">
        <div class="confirm_button">完成</div>
    </div>
    <div class="new_virtual_user" id="new_virtual_user" onclick="outside_new = false">
        <div class="new_virtual_user_title">
            <p style="margin: 0;">新建虚拟用户</p>
        </div>
        <div class="new_virtual_user_line_box">
            <div class="first_line">
                <input type="text" name="wx_name" placeholder="微信名">
            </div>
            <div class="first_line">
                <input type="text" name="age" placeholder="年龄">
            </div>
        </div>
        <div class="new_virtual_user_line_box">
            <div class="second_line">
                <input type="text" name="gender" placeholder="性别">
            </div>
            <div class="second_line">
                <input type="text" name="intention" placeholder="意向性别">
            </div>
            <div class="second_line">
                <input type="text" name="views" placeholder="曝光量">
            </div>
        </div>
        <div class="three_line">
            <input type="text" name="signature" placeholder="个性签名">
        </div>
        <div class="four_line">
            <input type="text" name="institutions" placeholder="机构/学校">
        </div>
        <img class="upload_pink" src="https://file.ourbeibei.com/l_e/static/icon/upload_pink.png">
        <div class="confirm_virtual_button">完成</div>
    </div>
    <input type="file" id="pic" value="上传" style="display: none;" onchange="upload_pic(id)" />
    <br>
    <table cellpadding="6" width="87%" border="1" cellspacing="0" id="daily_data" style="border-color: pink;">
        <tr>
            <td style="border-right: 0;" colspan="4">
                <input type="text" name="search" placeholder="搜索帅哥">
            </td>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;">
                <div style="float: right; " id="select" class="pick_button" onclick="matchingNewVirtualUserBoxPopUp()">
                    <span style="color: snow;" onclick="matchingNewVirtualUserBoxPopUp()">新增虚拟用户</span>
                </div>
            </td>
        </tr>
        <tr id="nav">
            <td>
                <p>用户id</p>
            </td>
            <td>
                <p>名字</p>
            </td>
            <td style="position: relative;">
                <p>性别</p>
                <ul>
                    <li>男</li>
                    <li>女</li>
                </ul>
            </td>
            <td>
                <p>意向性别</p>
            </td>
            <td>
                <p>照片</p>
            </td>
            <td style="position: relative;">
                <p>状态</p>
                <ul>
                    <li>通过</li>
                    <li>未审核</li>
                </ul>
            </td>
            <td>
                <p>个性签名</p>
            </td>
            <td>
                <p>标签</p>
            </td>
            <td>
                <p>年龄</p>
            </td>
            <td style="position: relative;">
                <p>vip</p>
                <ul>
                    <li>是vip</li>
                    <li>不是vip</li>
                </ul>
            </td>
            <td style="position: relative;">
                <p>是否虚拟</p>
                <ul>
                    <li>虚拟</li>
                    <li>真实</li>
                </ul>
            </td>
            <td>
                <p>曝光量</p>
            </td>
            <td>
                <p>最近次展示时间</p>
            </td>
            <td>
                <p>展示次数</p>
            </td>
            <td>
                <p>喜欢量</p>
            </td>
            <td>
                <p>匹配次数</p>
            </td>
            <td style="position: relative;">
                <p>匹配状态</p>
                <ul>
                    <li>正在匹配</li>
                    <li>未匹配</li>
                </ul>
            </td>
            <td>
                <p>匹配后未背天数</p>
            </td>
            <td>
                <p>操作</p>
            </td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function check_chapter(id) {
        // window.location.href = "read_class_chapter.jsp?id="+id;
    }
    function goBlack(id) {
        $.ajax({
            url:url+"/lzy/black.do",
            type:'POST',
            data:{
                id : id
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
    }
    var outside = true;
    var alertBox = document.getElementById("pop_up_box");
    function matchingBoxPopUp(id) {
        $(".pop_up_box").css("display", "block");
        $("#card_id").val(id);
        // 加了false才能显现出来
        outside = false;
    }
    document.body.addEventListener('click', function () {
        outside = true;
    }, true);
    document.body.addEventListener('click', function() {
        if(outside){
            alertBox.style.display = 'none';
        }
    });
    var outside_new = true;
    var alertNewBox = document.getElementById("new_virtual_user");
    function matchingNewVirtualUserBoxPopUp() {
        $(".new_virtual_user").css("display", "block");
        // 加了false才能显现出来
        outside_new = false;
    }
    document.body.addEventListener('click', function () {
        outside_new = true;
    }, true);
    document.body.addEventListener('click', function() {
        if(outside_new){
            alertNewBox.style.display = 'none';
        }
    })
</script>
</html>
