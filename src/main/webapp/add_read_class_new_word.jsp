<%@ page import="com.yj.common.Const" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script>
        <%
            String url = Const.DOMAIN_NAME;
            String root_url = Const.FTP_PREFIX;
        %>
        var url = "<%=url %>";
        //        var url = 'http://47.107.62.22:8080';
        var root_url = "<%=root_url %>";
        function GetQueryString(name)
        {
            var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if(r!=null)return  unescape(r[2]); return null;
        }
        var book_id = parseInt(GetQueryString("book_id"));
        var chapter_id = parseInt(GetQueryString("chapter_id"));
        function pic(){
            if (typeof($('#portrait')[0].files[0]) == "undefined"){
                alert("请选择完整图,不能为空！");
                return;
            }
            var formData = new FormData();
            formData.append('portrait', $('#portrait')[0].files[0]);
            formData.append('word', $("#word").val());
            formData.append('symbol', $("#symbol").val());
            formData.append('mean', $("#mean").val());
            formData.append('chapter_id', chapter_id);
            formData.append('book_id', book_id);
            $.ajax({
                url:url+"/admin/upload_read_class_new_word.do",
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
<div id="static">
    <center style="margin-top: 15%;">
        <h2>新增章节新单词</h2>
        <table>
            <tr>
                <td align="left">
                    音标的MP3：
                </td>
                <td align="left">
                    <input type="file" id="portrait" value="上传"/>
                </td>
            </tr>
            <tr>
                <td align="left">
                    音标：
                </td>
                <td align="left">
                    <input type="text" id="symbol">
                </td>
            </tr>
            <tr>
                <td align="left">
                    单词：
                </td>
                <td align="left">
                    <input type="text" id="word">
                </td>
            </tr>
            <tr>
                <td align="left">
                    意思：
                </td>
                <td align="left">
                    <input type="text" id="mean">
                </td>
            </tr>
            <tr>
                <td></td>
                <td align="left">
                    <button onclick="pic()">提交</button>
                </td>
            </tr>
        </table>
    </center>
</div>
</body>
</html>
