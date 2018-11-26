<%@ page import="com.yj.common.Const" %><%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2018/11/18
  Time: 21:49
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <style type="text/css" rel="stylesheet">
        body{ margin: 0 auto; padding: 0; width: 100%;}
        h1{ margin-top: 3rem;}
    </style>
</head>
<script type="text/javascript">
    var count = 0;
    <%
        String url = Const.DOMAIN_NAME;
        String root_url = Const.FTP_PREFIX;
    %>
    var url = "<%=url %>";
    //        var url = 'http://47.107.62.22:8080';
    var root_url = "<%=root_url %>";
    //        if (url1 == url || url1 == url+'/'){
    //            window.location.href=url+"/show_daily_pic.jsp?page=1&size=15"
    //        }
    // 获取get参数的方法
    function GetQueryString(name)
    {
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r!=null)return  unescape(r[2]); return null;
    }
    var page = parseInt(GetQueryString("page"));
    var type = parseInt(GetQueryString("type"));
    var size = 15;
    var all_url = url+"/admin/show_admin_data.do?page="+page+"&size="+size+"&type="+type;
    $(document).ready(function(){
        $.ajax({
            url:all_url,
            type:'GET',
            dataType:'json',
            success:function (result) {
                var data = result["data"];
                count += parseInt(result["msg"]);
                //计算页数
                var page_no = Math.ceil(count / size);
                if (page == 1){
                    $("#page").append('<td><p>第一页</p></td>');
                }else{
                    $("#page").append('<td><a href="'+url+'/show_data.jsp?page=1&size='+size+'">第一页</a></td>');
                }
                var ff = 0;
                var f = 0;
                if (page > 4){
                    f += page - 4;
                }
                if (f != 0){
                    $("#page").append('<td><p>....</p></td>');
                }
                for(var z = f; z < page_no; z++){
                    var no = z + 1;
                    if (no == page){
                        $("#page").append('<td><p>'+no+'</p></td>');
                    }else {
                        $("#page").append('<td><a href="'+url+'/show_data.jsp?page='+no+'&size='+size+'">'+no+'</a></td>');
                    }
                    if (ff == 8)break;
                    ff++;
                }
                if (ff != 8){
                    $("#page").append('<td><p>....</p></td>');
                }
                if (page == page_no){
                    $("#page").append('<td><p>最后一页</p></td>');
                }else{
                    $("#page").append('<td><a href="'+url+'/show_data.jsp?page='+page_no+'&size='+size+'">最后一页</a></td>');
                }
                $("#common_config").append('<tr>'+
                        '<td>'+data['common_data']['user_number']+'</td>'+
                        '</tr>');
                for(var i = 0; i < data['daily_data'].length; i++){
                    if (parseInt(data['daily_data'][i]['daily_add_user']) == 0){
                        two_day_retention = "0%";
                        seven_day_retention = "0%";
                        month_retention = "0%";
                    }else {
                        var two_day_retention = String(parseFloat(data['daily_data'][i]['two_day_retention'] * 1.0) / parseFloat(data['daily_data'][i]['daily_add_user']) * 100.00) + "%";
                        var seven_day_retention = String(parseFloat(data['daily_data'][i]['seven_day_retention'] * 1.0) / parseFloat(data['daily_data'][i]['daily_add_user']) * 100.00) + "%";
                        var month_retention = String(parseFloat(data['daily_data'][i]['month_retention'] * 1.0) / parseFloat(data['daily_data'][i]['daily_add_user']) * 100.00) + "%";
                    }

                    $("#daily_data").append('<tr>'+
                            '<td>'+data['daily_data'][i]['daily_add_user']+'</td>'+
                            '<td><div style="width: 400px;">'+data['daily_data'][i]['daily_app_start']+'</div></td>'+
                            '<td>'+data['daily_data'][i]['daily_finish_work']+'</td>'+
                            '<td>'+data['daily_data'][i]['dau']+'</td>'+
                            '<td>'+data['daily_data'][i]['mau']+'</td>'+
                            '<td>'+two_day_retention+'</td>'+
                            '<td>'+seven_day_retention+'</td>'+
                            '<td>'+month_retention+'</td>'+
                            '<td>'+data['daily_data'][i]['set_time']+'</td>'+
                            '</tr>');
                }
//                if (result.status == 200){
//                    alert(result[0]);
//                }
            },
            error:function (result) {
                console.log(result);
                alert("服务器出错！");
            }
        });
    });
</script>
<body>
<center>
    <h1>数据查看</h1>

    <table cellpadding="4" width="87%" border="1" cellspacing="0" id="common_config">
        <tr>
            <%--<td style="border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;border-right: 0;"></td>--%>
            <%--<td style="border-left: 0;">--%>
            <%--</td>--%>
        </tr>
        <tr>
            <td>用户量</td>
        </tr>
    </table>
    <br>
    <table cellpadding="9" width="87%" border="1" cellspacing="0" id="daily_data">
        <tr>
            <td style="border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;border-right: 0;"></td>
            <td style="border-left: 0;">
            </td>
        </tr>
        <tr>
            <td>日增用户数</td>
            <td>APP日启动次数</td>
            <td>日完成学习任务用户数</td>
            <td>DAU</td>
            <td>当月MAU</td>
            <td>次日留存率</td>
            <td>7日留存率</td>
            <td>30日留存率</td>
            <td>日期</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
</html>
