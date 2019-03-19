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
        function pic(){
            if (typeof($('#portrait')[0].files[0]) == "undefined"){
                alert("请选择完整图,不能为空！");
                return;
            }
            var formData = new FormData();
            formData.append('portrait', $('#portrait')[0].files[0]);
            formData.append('gender', $("#gender").val());
            formData.append('username', $("#username").val());
            formData.append('sign', $("#sign").val());
            $.ajax({
                url:url+"/admin/upload_virtual_user_platform.do",
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
        <h2>新建虚拟用户</h2>
        <table>
            <tr>
                <td align="left">
                    头像：
                </td>
                <td align="left">
                    <input type="file" id="portrait" value="上传"/>
                </td>
            </tr>
            <tr>
                <td align="left">
                    昵称：
                </td>
                <td align="left">
                    <input type="text" id="username">
                </td>
            </tr>
            <tr>
                <td align="left">
                    性别：
                </td>
                <td align="left">
                    <select id="gender">
                        <option value ="1" selected>女</option>
                        <option value ="0">男</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td align="left">
                    个性签名：
                </td>
                <td align="left">
                    <input type="text" id="sign">
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
