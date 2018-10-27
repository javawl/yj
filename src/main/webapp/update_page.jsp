<%@ page import="com.yj.common.Const" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>修改页面</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <style type="text/css" rel="stylesheet">
        body{ margin: 0 auto; padding: 0; width: 100%;}
        h1{ margin-top: 3rem;}
    </style>
    <script type="text/javascript">
        <%
            String url = Const.DOMAIN_NAME;
            String root_url = Const.FTP_PREFIX;
        %>
        var url = "<%=url %>";
//        var url = 'http://47.107.62.22:8080';
        var root_url = "<%=root_url %>";
        var word;
        function GetQueryString(name)
        {
            var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if(r!=null)return  unescape(r[2]); return null;
        }
        var id = GetQueryString("id");
        $(document).ready(function(){
            $.ajax({
                url:url+"/admin/get_single_word_info.do?id="+id,
                type:'GET',
                dataType:'json',
                success:function (result) {
                    var data1 = result["data"];
                    $("#word").val(data1['word']);
                    word = data1['word'];
                    $("#shanbei").val(data1['meaning']);
                    $("#bcz_meaning").val(data1['real_meaning']);
                    $("#md_meaning").val(data1['meaning_Mumbler']);
                    $("#phonetic_symbol_en").val(data1['phonetic_symbol_en']);
                    $("#phonetic_symbol_us").val(data1['phonetic_symbol_us']);
                    $("#phonetic_symbol_en_mumbler").val(data1['phonetic_symbol_en_Mumbler']);
                    $("#phonetic_symbol_us_mumbler").val(data1['phonetic_symbol_us_Mumbler']);
                    $("#phonetic_symbol").val(data1['phonetic_symbol']);
                    $("#sentence_en").val(data1['sentence']);
                    $("#sentence_cn").val(data1['sentence_cn']);
                },
                error:function (result) {
                    console.log(result);
                    alert("服务器出错！");
                }
            });
        });
        function submit(){
            if (confirm("你确定要更新此单词？更新之后不可恢复！")){
                $.ajax({
                    url:url+"/admin/update_main.do",
                    type:'POST',
                    data:{
                        id:id,
                        word:$("#word").val(),
                        meaning:$("#shanbei").val(),
                        real_meaning:$("#bcz_meaning").val(),
                        meaning_Mumbler:$("#md_meaning").val(),
                        phonetic_symbol_en:$("#phonetic_symbol_en").val(),
                        phonetic_symbol_us:$("#phonetic_symbol_us").val(),
                        phonetic_symbol_en_Mumbler:$("#phonetic_symbol_en_mumbler").val(),
                        phonetic_symbol_us_Mumbler:$("#phonetic_symbol_us_mumbler").val(),
                        phonetic_symbol:$("#phonetic_symbol").val(),
                        sentence:$("#sentence_en").val(),
                        sentence_cn:$("#sentence_cn").val()
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
        }
        function pic(){
            var formData = new FormData();
            formData.append('upload_file', $('#pic')[0].files[0]);
            formData.append('word', word);
            $.ajax({
                url:url+"/admin/upload_pic.do",
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
    </script>
</head>
<body>
    <div id="main">
        <center>
            <h2>基本信息修改</h2>
            <table>
                <tr>
                    <td align="left">
                        单词：
                    </td>
                    <td>
                        <input id="word" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        扇贝意思：
                    </td>
                    <td>
                        <input id="shanbei" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        百词斩意思：
                    </td>
                    <td>
                        <input id="bcz_meaning" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        美刀意思：
                    </td>
                    <td>
                        <input id="md_meaning" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        金山英式音标：
                    </td>
                    <td>
                        <input id="phonetic_symbol_en" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        金山美式音标：
                    </td>
                    <td>
                        <input id="phonetic_symbol_us" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        美刀英式音标：
                    </td>
                    <td>
                        <input id="phonetic_symbol_en_mumbler" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        美刀美式音标：
                    </td>
                    <td>
                        <input id="phonetic_symbol_us_mumbler" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        百词斩音标：
                    </td>
                    <td>
                        <input id="phonetic_symbol" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        百词斩例句英文：
                    </td>
                    <td>
                        <input id="sentence_en" type="text">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        百词斩例句中文：
                    </td>
                    <td>
                        <input id="sentence_cn" type="text">
                    </td>
                </tr>
                <tr><td> </td><td><input type="submit" value="提交" onclick="submit()"></td></tr>
            </table>
        </center>
    </div>
    <div id="static">
        <center>
            <h2>静态资源更新</h2>
            <table>
                <tr>
                    <td align="left">
                        单词图片
                    </td>
                    <td align="left">
                        <input type="file" id="pic" value="上传"/>
                    </td>
                    <td align="left">
                        <button onclick="pic()">提交</button>
                    </td>
                </tr>
            </table>
        </center>
    </div>
</body>
</html>
