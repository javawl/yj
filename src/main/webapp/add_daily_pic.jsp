<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script>
//        var url = 'http://localhost:8088';
        var url = 'http://47.107.62.22:8080';
        function pic(){
            if (typeof($('#daily_pic')[0].files[0]) == "undefined"){
                alert("请选择完整图,不能为空！");
                return;
            }
            if (typeof($('#small_pic')[0].files[0]) == "undefined"){
                alert("请选择缩略图,不能为空！");
                return;
            }
            var formData = new FormData();
            formData.append('daily_pic', $('#daily_pic')[0].files[0]);
            formData.append('small_pic', $('#small_pic')[0].files[0]);
            formData.append('year', $("#year").val());
            formData.append('month', $("#month").val());
            formData.append('day', $("#day").val());
            $.ajax({
                url:url+"/admin/upload_daily_pic.do",
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
        <center style="margin-top: 300px;">
            <h2>每日一图更新</h2>
            <table>
                <tr>
                    <td align="left">
                        年份：
                    </td>
                    <td align="left">
                        <input type="text" id="year">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        月份：
                    </td>
                    <td align="left">
                        <input type="text" id="month">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        第几日（月份的第几日）：
                    </td>
                    <td align="left">
                        <input type="text" id="day">
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        每日一图完整图
                    </td>
                    <td align="left">
                        <input type="file" id="daily_pic" value="上传"/>
                    </td>
                </tr>
                <tr>
                    <td align="left">
                        每日一图截取图
                    </td>
                    <td align="left">
                        <input type="file" id="small_pic" value="上传"/>
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
