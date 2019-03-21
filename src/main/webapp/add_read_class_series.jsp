<%@ page import="com.yj.common.Const" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>后台管理</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
</head>
<script type="text/javascript">
    <%
        String url = Const.DOMAIN_NAME;
    %>
    var url = "<%=url %>";
    var all_url = url+"/admin/showReadClassBook.do?page=1&size=10000";
    var data1;
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                data1 = result["data"];
                for(var i = 0; i < data1.length; i++){
                    if (i == 0){
                        //第一个被选中
                        $("#book1").append('<option value ="'+ data1[i]['id'] +'" selected>'+ data1[i]['name'] +'</option>');
                    }else {
                        $("#book1").append('<option value ="'+ data1[i]['id'] +'">'+ data1[i]['name'] +'</option>');
                    }
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
<div>
    <table>
        <tr>
            <td>标题：</td>
            <td><input name="title" type="text"></td>
        </tr>
        <tr>
            <td>词汇量：</td>
            <td><input id="word_need_number" name="word_need_number" type="text"></td>
        </tr>
        <tr>
            <td>指导老师二维码图片：</td>
            <td><input id="pic" name="pic" type="file"></td>
        </tr>
    </table>
</div>
<div id="inner">
    <h2>选择书籍</h2>
    <select id="book1">
    </select><br><br>
    <%--<div id="div1" class="text">--%>
    <%--<p>第1段文字</p>--%>
    <%--</div>--%>
</div>
<div>
    <button id="add_text">添加一本书籍</button>
    <button id="btn1">提交</button>
</div>
<div id="special">

</div>
<script>
    <%
        String root_url = Const.FTP_PREFIX + "js/wangEditor.min.js";
    %>
    var url = "<%=url %>";
    //        var url = 'http://47.107.62.22:8080';
    var root_url = "<%=root_url %>";
</script>
<!-- 注意， 只需要引用 JS，无需引用任何 CSS ！！！-->
<script type="text/javascript" src="<%=root_url %>"></script>
<script type="text/javascript">
    var flag = 1;
    // 获取get参数的方法
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    var class_id = parseInt(GetQueryString("id"));
    document.getElementById('btn1').addEventListener('click', function () {
        var title = $('input[name=title]').val();
        var word_need_number = $('input[name=word_need_number]').val();
        if (typeof($('#pic')[0].files[0]) == "undefined"){
            alert("请选择老师二维码,不能为空！");
            return;
        }
        // 读取 html
        var result = [];
        //建立图片数组
        var formData = new FormData();
        for(var ii = 1; ii <= flag; ii++){
            //文本
            alert(ii+1);
            var single_json = {
                "book_id": $("#book"+ii).val(),
                "order": ii+1
            };
            result.push(single_json);
        }

        formData.append('pic', $('#pic')[0].files[0]);
        formData.append('title', title);
        formData.append('word_need_number', word_need_number);
        formData.append('class_id', class_id);
        formData.append('sentence', JSON.stringify(result));
        $.ajax({
            url:url+"/admin/upload_read_class_series_book.do",
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
    }, false);
    document.getElementById('add_text').addEventListener('click', function () {
        flag++;
        var div_name = "#div"+flag;
        //添加div
        $("#inner").append('<h2>选择书籍</h2> <select id="book' + flag + '"></select><br><br> ');
        for(var i = 0; i < data1.length; i++){
            if (i == 0){
                //第一个被选中
                $("#book"+flag).append('<option value ="'+ data1[i]['id'] +'" selected>'+ data1[i]['name'] +'</option>');
            }else {
                $("#book"+flag).append('<option value ="'+ data1[i]['id'] +'">'+ data1[i]['name'] +'</option>');
            }
        }
    }, false);
</script>
</body>
</html>
