<%@ page import="com.yj.common.Const" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <script>
        <%
            String url = Const.DOMAIN_NAME;
            String root_url = Const.FTP_PREFIX;
        %>
        var url = "<%=url %>";
        //        var url = 'http://47.107.62.22:8080';
        var root_url = "<%=root_url %>";
        function pic(){
            if (typeof($('#portrait')[0].files[0]) == "undefined"){
                alert("请选择完整图,不能为空！");
                return;
            }
            var formData = new FormData();
            formData.append('portrait', $('#portrait')[0].files[0]);
            formData.append('username', $("#username").val());
            $.ajax({
                url:url+"/admin/uploadGameShare.do",
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
        <h2>上传小游戏分享图片</h2>
        <table>
            <tr>
                <td align="left">
                    图片：
                </td>
                <td align="left">
                    <input type="file" id="portrait" value="上传"/>
                </td>
            </tr>
            <tr>
                <td align="left">
                    文案：
                </td>
                <td align="left">
                    <input type="text" id="username">
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
