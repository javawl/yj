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
</script>
<body>
    <div>
        <table>
            <tr>
                <td>标题：</td>
                <td><input name="title" type="text"></td>
            </tr>
            <tr>
                <td>作者id：</td>
                <td><select id="author">
                    </select></td>
            </tr>
            <tr>
                <td>分类：</td>
                <td><input name="kind" type="text"></td>
            </tr>
            <tr>
                <td>封面选择：</td>
                <td><input name="select" type="radio" value="0" onclick="document.getElementById('video').style.display = 'inline'"/>封面为视频</td>
                <td><input name="select" type="radio" value="1" onclick="document.getElementById('video').style.display = 'none'"/>封面为图片</td>
            </tr>
            <tr>
                <td>封面图片（视频也要上传封面图片）：</td>
                <td><input id="pic" name="pic" type="file"></td>
            </tr>
            <tr id="video" style="display: none">
                <td>封面视频（请上传MP4格式并且内嵌字幕）：</td>
                <td><input id="video_file" name="video" type="file"></td>
            </tr>
        </table>
    </div>
    <div id="inner">
        <h2>第1段文字（不超过800字，超过部分请新开一段）</h2>
        <div id="div1" class="text">
            <p>第1段文字</p>
        </div>
    </div>
    <div>
        <button id="add_pic">添加一张图片</button>
        <button id="add_text">添加一段文字</button>
        <button id="submit">获取html</button>
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
        var editor = new Array()
        var E = window.wangEditor;

        editor[1] = new E('#div1');
        editor[1].create();
        document.getElementById('submit').addEventListener('click', function () {
            alert(editor[1].txt.html());
        }, false);
        document.getElementById('btn1').addEventListener('click', function () {
            var select = $('input[name=select]:checked').val();
            var title = $('input[name=title]').val();
            var kind = $('input[name=kind]').val();
            var author = $("#author option:selected").val();
            if(select == undefined){
                alert("请选择封面的格式！");
                return;
            }
            if (typeof($('#pic')[0].files[0]) == "undefined"){
                alert("请选择封面图,不能为空！");
                return;
            }
            if (select == "0" && typeof($('#video_file')[0].files[0]) == "undefined") {
                alert("请选择视频,不能为空！");
                return;
            }
            // 读取 html
            var result = [];
            //建立图片数组
            var files_order = [];
            var formData = new FormData();
            for(var ii = 0; ii < flag; ii++){
                if (editor[ii+1] != undefined){
                    //文本
                    alert(ii+1);
                    var single_json = {
                        "inner": editor[ii+1].txt.html(),
                        "order": ii+1
                    };
                    result.push(single_json);
                }else {
                    //图片
                    //获取id
                    var pic_id = "#pic"+(ii+1).toString();
                    formData.append('file', $(pic_id)[0].files[0]);

                    //这里加一个order的数组
                    var single_order = {
                        "order": ii+1
                    };
                    files_order.push(single_order);

                }
            }

            formData.append('pic', $('#pic')[0].files[0]);
            if (select == "0"){
                formData.append('video_file', $('#video_file')[0].files[0]);
            }else {
                formData.append('video_file', null);
            }
            formData.append('title', title);
            formData.append('select', select);
            formData.append('kind', kind);
            formData.append('author', author);
            formData.append('sentence', JSON.stringify(result));
            formData.append('files_order', JSON.stringify(files_order));
            $.ajax({
                url:url+"/admin/upload_feeds_sentences.do",
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
            $("#inner").append('<h2>第'+flag+'段文字（不超过800字，超过部分请新开一段）</h2> <div id="div'+flag+'" class="text"> <p>第'+flag+'段文字</p></div>');
            //添加js
            editor[flag] = new E(div_name);
            editor[flag].create();
        }, false);
        document.getElementById('add_pic').addEventListener('click', function () {
            flag++;
            var div_name = "#div"+flag;
            //添加div
            $("#inner").append('<h2>上传需要插入的图片</h2> <input style="margin-bottom: 25px;" id="pic'+flag+'" type="file">');
        }, false);
    </script>
</body>
</html>
