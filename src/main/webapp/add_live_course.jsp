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
        function pic(){
            var reg = /^[0-9]{4}\-[0-9]{2}\-[0-9]{2}$/;
            if (!reg.test($("#et").val())){
                alert("格式是xxxx-xx-xx");
                return;
            }
            if (!reg.test($("#st").val())){
                alert("格式是xxxx-xx-xx");
                return;
            }
            // if (typeof($('#t1_qr')[0].files[0]) == "undefined"){
            //     alert("请选择老师二维码,不能为空！");
            //     return;
            // }
            //
            // if (typeof($('#t1_portrait')[0].files[0]) == "undefined"){
            //     alert("请选择老师头像,不能为空！");
            //     return;
            // }
            var formData = new FormData();
            formData.append('st', $("#st").val());
            formData.append('et', $("#et").val());
            // formData.append('t1_name', $("#t1_name").val());
            // formData.append('t1_qr', $('#t1_qr')[0].files[0]);
            // formData.append('t1_portrait', $('#t1_portrait')[0].files[0]);
            $.ajax({
                url:url+"/admin/uploadLiveCourse.do",
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
        <h2>添加挑战</h2>
        <table>
            <tr>(开始和结束当天算入计划中)</tr>
            <tr>
                <td align="left">
                    开始时间：
                </td>
                <td align="left">
                    <input type="text" id="st">  (注意！！格式是xxxx-xx-xx)
                </td>
            </tr>
            <tr>
                <td align="left">
                    结束时间：
                </td>
                <td align="left">
                    <input type="text" id="et">  (注意！！格式是xxxx-xx-xx)
                </td>
            </tr>
            <%--<tr>--%>
                <%--<td>老师名字：</td>--%>
                <%--<td><input id="t1_name" type="text"></td>--%>
                <%--<td>老师头像：</td>--%>
                <%--<td><input id="t1_portrait" name="pic" type="file"></td>--%>
                <%--<td>老师二维码图片：</td>--%>
                <%--<td><input id="t1_qr" name="pic" type="file"></td>--%>
            <%--</tr>--%>
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
