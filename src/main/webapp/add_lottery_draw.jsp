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
            var reg = /^[0-9]{4}\-[0-9]{2}\-[0-9]{2}$/;
            if (!reg.test($("#et").val())){
                alert("格式是xxxx-xx-xx");
                return;
            }
            if (typeof($('#prize_pic')[0].files[0]) == "undefined"){
                alert("奖品图片不能为空！");
                return;
            }
            if (typeof($('#prize_tomorrow_pic')[0].files[0]) == "undefined"){
                alert("预告奖品图片不能为空！");
                return;
            }
            var formData = new FormData();
            formData.append('prize_pic', $('#prize_pic')[0].files[0]);
            formData.append('prize_tomorrow_pic', $('#prize_tomorrow_pic')[0].files[0]);
            formData.append('et', $("#et").val());
            formData.append('prize_tomorrow', $("#prize_tomorrow").val());
            formData.append('prize', $("#prize").val());
            $.ajax({
                url:url+"/admin/upload_lottery_draw.do",
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
        <h2>加入新的奖品</h2>
        <table>
            <tr>
                <td align="left">
                    奖品名称：
                </td>
                <td align="left">
                    <input type="text" id="prize">
                </td>
            </tr>
            <tr>
                <td align="left">
                    奖品预告名称：
                </td>
                <td align="left">
                    <input type="text" id="prize_tomorrow">
                </td>
            </tr>
            <tr>
                <td align="left">
                    开奖时间：
                </td>
                <td align="left">
                    <input type="text" id="et">  (注意！！格式是xxxx-xx-xx)
                </td>
            </tr>
            <tr>
                <td align="left">
                    奖品图片：
                </td>
                <td align="left">
                    <input type="file" id="prize_pic" value="上传"/>
                </td>
            </tr>
            <tr>
                <td align="left">
                    奖品预告的奖品图片：
                </td>
                <td align="left">
                    <input type="file" id="prize_tomorrow_pic" value="上传"/>
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
