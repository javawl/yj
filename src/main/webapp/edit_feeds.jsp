<%@ page import="com.yj.common.Const" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>后台管理</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
</head>
<body>
    <div id="inner">
        <h2>第1段文字（不超过800字，超过部分请新开一段）</h2>
        <div id="div1" class="text">
            <p>第1段文字</p>
        </div>
    </div>
    <div>
        <button id="add_pic">添加一张图片</button>
        <button id="add_text">添加一段文字</button>
        <button id="submit">提交</button>
    </div>
    <script>
        <%
            String url = Const.DOMAIN_NAME;
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
        var flag_pic = 0;
        var editor = new Array()
        var E = window.wangEditor;

        editor[1] = new E('#div1');
        editor[1].create();
        document.getElementById('btn1').addEventListener('click', function () {
            // 读取 html
            alert(editor1.txt.html())
        }, false);
        document.getElementById('add_text').addEventListener('click', function () {
            flag++;
            var div_name = "#div"+flag;
            //添加div
            $("#inner").append('<h2>第'+flag+'段文字（不超过800字，超过部分请新开一段）</h2> <div id="div'+flag+'" class="text"> <p>第'+flag+'段文字</p></div>');
            //添加js
            editor[flag] = new E(div_name);
            editor[flag].create();
        }, false);
        document.getElementById('add_pic').addEventListener('click', function () {
            flag_pic++;
            var div_name = "#div"+flag;
            //添加div
            $("#inner").append('<h2>上传需要插入的图片</h2> <input style="margin-bottom: 25px;" id="pic'+flag_pic+'" type="file">');
        }, false);
    </script>
</body>
</html>
