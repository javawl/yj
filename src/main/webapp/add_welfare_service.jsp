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
            if (typeof($('#pic')[0].files[0]) == "undefined"){
                alert("请选择完整图,不能为空！");
                return;
            }
            var reg = /^[0-9]{4}\-[0-9]{2}\-[0-9]{2}\s[0-9]{2}:[0-9]{2}:[0-9]{2}$/;
            if (!reg.test($("#st").val())){
                alert("格式是xxxx-xx-xx xx:xx:xx");
                return;
            }
            if (!reg.test($("#et").val())){
                alert("格式是xxxx-xx-xx xx:xx:xx");
                return;
            }
            var formData = new FormData();
            formData.append('pic', $('#pic')[0].files[0]);
            formData.append('url', $("#url").val());
            formData.append('st', $("#st").val());
            formData.append('et', $("#et").val());
            $.ajax({
                url:url+"/admin/upload_welfare_service.do",
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
        <h2>新增福利社</h2>
        <table>
            <tr>
                <td align="left">
                    图片：
                </td>
                <td align="left">
                    <input type="file" id="pic" value="上传"/>
                </td>
            </tr>
            <tr>
                <td align="left">
                    跳转地址：
                </td>
                <td align="left">
                    <input type="text" id="url">
                </td>
            </tr>
            <tr>
                <td align="left">
                    开始时间：
                </td>
                <td align="left">
                    <input type="text" id="st">  (注意！！格式是xxxx-xx-xx xx:xx:xx)
                </td>
            </tr>
            <tr>
                <td align="left">
                    结束时间：
                </td>
                <td align="left">
                    <input type="text" id="et">  (注意！！格式是xxxx-xx-xx xx:xx:xx)
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
