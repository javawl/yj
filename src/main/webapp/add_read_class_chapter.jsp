<%@ page import="com.yj.common.Const" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>后台管理</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
</head>
<script type="text/javascript">
    <%
        String url = Const.DOMAIN_NAME;
    %>
    var url = "<%=url %>";
    var all_url = url+"/admin/show_author_info.do?page=1&size=10000";
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                for(var i = 0; i < data.length; i++){
                    if (i == 0){
                        //第一个被选中
                        $("#author").append('<option value ="'+ data[i]['id'] +'" selected>'+ data[i]['username'] +'</option>');
                    }else {
                        $("#author").append('<option value ="'+ data[i]['id'] +'">'+ data[i]['username'] +'</option>');
                    }
                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
    });
    function change() {
        if ($('#select').val() === "input"){
            $('#inner').empty();
            $('#option').empty();
            $('#inner').append("<h2>第1段文字(别搞太长！！！)</h2>\n" +
                "    英文：<textarea id=\"en1\" style=\"width: 700px; height: 70px;\"></textarea><br> 中文：<textarea id=\"cn1\" style=\"width: 700px; height: 70px;\"></textarea>");
            $('#option').append("'<button id=\"add_text\" onclick=\"add_text_add()\">添加一段文字</button>\n" +
                "    <button id=\"btn1\">提交</button>");
        }else if ($('#select').val() === "order"){
            $('#inner').empty();
            $('#option').empty();
            $('#inner').append("<h2>第1个序号（数字）</h2>\n" +
                "    序号：<input  id=\"order1\">");
            $('#option').append("<button id=\"add_order\" onclick='add_order_add()'>添加一个序号</button>\n" +
                "    <button id=\"btn2\" onclick='btn2_onclick()'>提交</button>");
        }
    }
</script>
<body>
<div>
    <table>
        <tr>
            <td>标题：</td>
            <td><input name="title" type="text"></td>
        </tr>
        <tr>
            <td>章节号：</td>
            <td><input name="order" type="text"></td>
        </tr>
        <tr>
            <td>这一章文章的音频：</td>
            <td><input id="pic" name="pic" type="file"></td>
        </tr>
        <tr>
            <select id="select" onchange="change()">
                <option value ="input" selected>按输入</option>
                <option value ="order">按序号</option>
            </select>
        </tr>
    </table>
</div>
<div id="inner">
    <h2>第1段文字(别搞太长！！！)</h2>
    <%--<div id="div1" class="text">--%>
        <%--<p>第1段文字</p>--%>
    <%--</div>--%>
    英文：<textarea id="en1" style="width: 700px; height: 70px;"></textarea><br> 中文：<textarea id="cn1" style="width: 700px; height: 70px;"></textarea>
</div>
<div id="option">
    <button id="add_text" onclick="add_text_add()">添加一段文字</button>
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
    function isNotANumber(inputData) {
        //isNaN(inputData)不能判断空串或一个空格
        //如果是一个空串或是一个空格，而isNaN是做为数字0进行处理的，而parseInt与parseFloat是返回一个错误消息，这个isNaN检查不严密而导致的。
        if (parseFloat(inputData).toString() == "NaN") {
            //alert("请输入数字……");注掉，放到调用时，由调用者弹出提示。
            return false;
        } else {
            return true;
        }
    }
    var book_id = parseInt(GetQueryString("id"));
    document.getElementById('btn1').addEventListener('click', function () {
        var title = $('input[name=title]').val();
        var order = $('input[name=order]').val();
        if (typeof($('#pic')[0].files[0]) == "undefined"){
            alert("请选择音频,不能为空！");
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
                "en": $("#en"+ii).val(),
                "cn": $("#cn"+ii).val(),
                "order": ii+1
            };
            result.push(single_json);
        }

        formData.append('pic', $('#pic')[0].files[0]);
        formData.append('title', title);
        formData.append('order', order);
        formData.append('type', 'input');
        formData.append('book_id', book_id);
        formData.append('sentence', JSON.stringify(result));
        $.ajax({
            url:url+"/admin/upload_read_class_chapter.do",
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
    function btn2_onclick(){
        var title = $('input[name=title]').val();
        var order = $('input[name=order]').val();
        if (typeof($('#pic')[0].files[0]) == "undefined"){
            alert("请选择音频,不能为空！");
            return;
        }
        // 读取 html
        var result = [];
        //建立图片数组
        var formData = new FormData();
        for(var ii = 1; ii <= flag; ii++){
            //文本
            if (!isNotANumber($("#order"+ii).val())){
                alert("序号请输入数字！");
                return;
            }
            var single_json = {
                "sent_order": $("#order"+ii).val(),
                "order": ii+1
            };
            result.push(single_json);
        }

        formData.append('pic', $('#pic')[0].files[0]);
        formData.append('title', title);
        formData.append('order', order);
        formData.append('type', 'order');
        formData.append('book_id', book_id);
        formData.append('sentence', JSON.stringify(result));
        $.ajax({
            url:url+"/admin/upload_read_class_chapter.do",
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

    function add_text_add(){
        flag++;
        //添加div
        $("#inner").append('<h2>第'+flag+'段文字(嗯？别搞太长！！！)</h2> 英文：<textarea id="en'+flag+'"  style="width: 700px; height: 70px;"></textarea><br> 中文：<textarea id="cn'+flag+'" style="width: 700px; height: 70px;"></textarea>');
    }
    function add_order_add(){
        flag++;
        //添加div
        $("#inner").append('<h2>第'+flag+'个序号（数字）</h2>  序号：<input id="order'+flag+'">');
    }
</script>
</body>
</html>
