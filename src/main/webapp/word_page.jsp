<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <style type="text/css" rel="stylesheet">
        body{ margin: 0 auto; padding: 0; width: 100%;}
        h1{ margin-top: 3rem;}
    </style>
    <script type="text/javascript">
        var count = 0;
//                var url = 'http://localhost:8088';
        var url = 'http://47.107.62.22:8080';
        var root_url = 'http://47.107.62.22/l_e/';
        var url1 = document.URL;
        //用来上传图片
        var pic_id;
        //判断是否有输入框
        var exist = 0;
        var flag_id;
        var save;
        //        if (url1 == url || url1 == url+'/'){
        //            window.location.href=url+"/index.jsp?page=1&size=15&type=1"
        //        }
        function GetQueryString(name)
        {
            var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if(r!=null)return  unescape(r[2]); return null;
        }
        function upload_pic() {
            var formData = new FormData();
            formData.append('upload_file', $('#pic')[0].files[0]);
            formData.append('word', pic_id);
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
        function upload_sent(id,w) {
            $.ajax({
                url:url+"/admin/update_sent.do",
                type:'POST',
                data:{
                    word:w,
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
            exist = 0;
            history.go(0);
            $("html, body").scrollTop(0).animate({scrollTop: $("#s"+id).offset().top});
        }
        function upload_pic_click(id) {
            pic_id = id;
            document.getElementById("pic").click();
        }
        function change_sent(id,w,string){
            if (exist === 0){
                exist = 1;
                flag_id = id;
                save = string;
                $("#s"+id).empty();
                $("#s"+id).append('英文：<input id="sentence_en" type="text"><br>中文：<input id="sentence_cn" type="text"><br><button onclick="upload_sent('+"'"+id+"','"+w+"'"+')">提交</button>');
            }
        }
        var page = parseInt(GetQueryString("page"));
        var type = parseInt(GetQueryString("type"));
        var condition = GetQueryString("condition");
        var size = 15;
        var all_url = url+"/admin/get_word.do?page="+page+"&size="+size+"&type="+type+"&condition="+condition;
        $(document).ready(function(){
            $.ajax({
                url:all_url,
                type:'GET',
                dataType:'json',
                success:function (result) {
                    var data = result["data"];
                    count += parseInt(result["msg"]);
                    if(data.length == 0){
                        alert("暂未找到该单词信息")
                    }
                    //计算页数
                    var page_no = Math.ceil(count / size);
                    if (page == 1){
                        $("#page").append('<td><p>第一页</p></td>');
                    }else{
                        $("#page").append('<td><a href="'+url+'/word_page.jsp?page=1&size='+size+'&type='+type+'&condition='+condition+'">第一页</a></td>');
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
                            $("#page").append('<td><a href="'+url+'/word_page.jsp?page='+no+'&size='+size+'&type='+type+'&condition='+condition+'">'+no+'</a></td>');
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
                        $("#page").append('<td><a href="'+url+'/word_page.jsp?page='+page_no+'&size='+size+'&type='+type+'&condition='+condition+'">最后一页</a></td>');
                    }
                    for(var i = 0; i < data.length; i++){
                        var string1;
                        var string2;
                        var string3;
                        var string4;
                        var string5;
                        var string6;
                        var string7;

                        if (data[i]['pronunciation_en']==''){
                            string1 = '此资源为空（未爬到）'
                        }else {
                            string1 = '<button type="button"><a target="_blank"  style="color: black" href="'+root_url+data[i]['pronunciation_en']+'">查看</a></button>';
                        }
                        if (data[i]['pronunciation_us']==''){
                            string2 = '此资源为空（未爬到）'
                        }else {
                            string2 = '<button type="button"><a target="_blank"  style="color: black" href="'+root_url+data[i]['pronunciation_us']+'">查看</a></button>';
                        }
                        if (data[i]['pronunciation_en_Mumbler']==''){
                            string3 = '此资源为空（未爬到）'
                        }else {
                            string3 = '<button type="button"><a target="_blank"  style="color: black" href="'+root_url+data[i]['pronunciation_en_Mumbler']+'">查看</a></button>';
                        }
                        if (data[i]['pronunciation_us_Mumbler']==''){
                            string4 = '此资源为空（未爬到）'
                        }else {
                            string4 = '<button type="button"><a target="_blank"  style="color: black" href="'+root_url+data[i]['pronunciation_us_Mumbler']+'">查看</a></button>';
                        }
                        if (data[i]['pronunciation']==''){
                            string5 = '此资源为空（未爬到）'
                        }else {
                            string5 = '<button type="button"><a target="_blank"  style="color: black" href="'+root_url+data[i]['pronunciation']+'">查看</a></button>';
                        }
                        if (data[i]['sentence_audio']==''){
                            string6 = '此资源为空（未爬到）'
                        }else {
//                            string6 = '<button type="button"><a style="color: black" href="'+root_url+data[i]['sentence_audio']+'">查看</a></button>';
                            string6 = '<audio src="'+root_url+data[i]['sentence_audio']+'" controls="controls"></audio>';
                        }
                        if (data[i]['pic']==''){
                            string7 = '此资源为空（未爬到）'
                        }else {
//                            string7 = '<button type="button"><a style="color: black" href="'+root_url+data[i]['pic']+'">查看</a></button>';
                            string7 = '<img src="'+root_url+data[i]['pic']+'">';
                        }
                        $("#special").append('<tr>'+
                                '<td style="width: 4%;">'+data[i]['id']+'</td>'+
                                '<td style="width: 4%;">'+data[i]['word']+'</td>'+
                                '<td style="width: 4%;">'+data[i]['meaning']+'</td>'+
                                '<td style="width: 4%;">'+data[i]['real_meaning']+'</td>'+
                                '<td style="width: 4%;">'+data[i]['meaning_Mumbler']+'</td>'
//                              +  '<td style="width: 4%;">'+data[i]['phonetic_symbol_en']+'</td>'+
//                                '<td style="width: 4%;">'+string1+'</td>'
//                                +'<td style="width: 4%;">'+data[i]['phonetic_symbol_us']+'</td>'
//                                +'<td style="width: 4%;">'+string2+'</td>'
//                                +'<td style="width: 4%;">'+data[i]['phonetic_symbol_en_Mumbler']+'</td>'
//                                +'<td style="width: 4%;">'+string3+'</td>'
//                                +'<td style="width: 4%;">'+data[i]['phonetic_symbol_us_Mumbler']+'</td>'
//                                +'<td style="width: 4%;">'+string4+'</td>'
//                                +'<td style="width: 4%;">'+data[i]['phonetic_symbol']+'</td>'
//                                +'<td style="width: 4%;">'+string5+'</td>'
                                +'<td style="width: 20%;" id="s'+data[i]['id']+'" onclick="change_sent('+"'"+data[i]['id']+"'"+","+"'"+data[i]['word']+"',"+"'"+escape(data[i]['sentence'])+'<br>'+escape(data[i]['sentence_cn'])+"'"+')">'+data[i]['sentence']+'<br>'+data[i]['sentence_cn']+'</td>'
                                +'<td style="width: 4%;">'+string6+'</td>'
                                +'<td style="width: 4%;" onclick="upload_pic_click('+"'"+data[i]['word']+"'"+')">'+string7+'</td>'
                                +'<td style="width: 4%;"><button type="button"><a style="color: black" target="_blank" href="'+url+'/show_video.jsp?id='+data[i]['id']+'">查看</a></button></td>'+
                                '<td style="width: 6%;"><button onclick="edit('+"'"+data[i]['id']+"'"+')">修改</button><br><button style="margin-left: 5px;" onclick="del('+"'"+data[i]['word']+"'"+')">删除</button></td>'+
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
            $.ajax({
                url:url+"/admin/get_word_type.do",
                type:'GET',
                dataType:'json',
                success:function (result) {
                    var data1 = result["data"];
                    for(var i = 0; i < data1.length; i++){
                        if (parseInt(data1[i]["dictionary_type"]) == type){
                            $("#select").append('<option value ="'+data1[i]["dictionary_type"]+'" selected>'+data1[i]["plan"]+'</option>');
                        }else {
                            $("#select").append('<option value ="'+data1[i]["dictionary_type"]+'">'+data1[i]["plan"]+'</option>');
                        }
                    }
                },
                error:function (result) {
                    console.log(result);
                    alert("服务器出错！");
                }
            });
        });
        function change_type(){
            window.location.href=url+"/word_page.jsp?page="+page+"&size="+size+"&type="+$("#select").val()+"&condition="+$("#search").val();
        }
//        回车事件
        document.onkeyup = function (e) {//按键信息对象以函数参数的形式传递进来了，就是那个e
            var code = e.charCode || e.keyCode;  //取出按键信息中的按键代码(大部分浏览器通过keyCode属性获取按键代码，但少部分浏览器使用的却是charCode)
            if (code == 13) {
                //此处编写用户敲回车后的代码
                window.location.href=url+"/word_page.jsp?page="+page+"&size="+size+"&type="+$("#select").val()+"&condition="+$("#search").val();
            }
        }
    </script>
</head>
<body>
<center>
    <h1>单词详情页</h1>
    <input type="file" id="pic" value="上传" style="display: none;" onchange="upload_pic(id)" />
    <table cellpadding="8" width="100%" border="1" cellspacing="0" id="special">
        <tr>
            <td style="width: 4%;border-right: 0;"></td>
            <td style="width: 4%;border-left: 0;border-right: 0;"></td>
            <td style="width: 4%;border-left: 0;border-right: 0;"></td>
            <td style="width: 4%;border-left: 0;border-right: 0;"></td>
            <td style="width: 4%;border-left: 0;border-right: 0;"></td>
            <td  style="width: 20%;border-left: 0;border-right: 0;"></td>
            <td  style="width: 4%;border-left: 0;border-right: 0;"></td>
            <td  style="width: 4%;border-left: 0;border-right: 0;">搜索：<input type="text" id="search" placeholder="输入单词"></td>
            <td  style="width: 4%;border-left: 0;border-right: 0;"></td>
            <td  style="width: 6%;border-left: 0;">
                <select style="float: right" id="select" onchange="change_type()">
                </select>
            </td>
        </tr>
        <tr>
            <td style="width: 4%;">单词id</td>
            <td style="width: 4%;">单词</td>
            <td style="width: 4%;">单词的扇贝意思</td>
            <td style="width: 4%;">单词百词斩意思</td>
            <td style="width: 4%;">单词美刀意思</td>
            <%--<td style="width: 4%;">单词英式音标（金山词霸）</td>--%>
            <%--<td style="width: 4%;">单词英式发音（金山词霸）</td>--%>
            <%--<td style="width: 4%;">单词美式音标（金山词霸）</td>--%>
            <%--<td style="width: 4%;">单词美式发音（金山词霸）</td>--%>
            <%--<td style="width: 4%;">单词英式音标（美刀）</td>--%>
            <%--<td  style="width: 4%;">单词英式发音（美刀）</td>--%>
            <%--<td  style="width: 4%;">单词美式音标（美刀）</td>--%>
            <%--<td  style="width: 4%;">单词美式发音（美刀）</td>--%>
            <%--<td  style="width: 4%;">音标百词斩</td>--%>
            <%--<td  style="width: 4%;">单词音频百词斩</td>--%>
            <td  style="width: 20%;">例句百词斩</td>
            <td  style="width: 4%;">例句音频百词斩</td>
            <td  style="width: 4%;">图片百词斩</td>
            <td  style="width: 4%;">视频</td>
            <td  style="width: 6%;">操作</td>
        </tr>
    </table>
    <table id="page">
    </table>
</center>
</body>
<script>
    function edit(id) {
        window.location.href=url+'/update_page.jsp?id='+id;
    }
    function del(word) {
        if (confirm("你确定要删除此单词？删除之后不可恢复！")){
            $.ajax({
                url:url+"/admin/delete_word.do",
                type:'POST',
                data:{
                    word:word
                },
                dataType:'json',
                success:function (result) {
                    var code = result['code'];
                    var msg = result['msg'];
                    if (code != 200){
                        alert(msg);
                    }else {
                        alert(msg);
                        history.go(0);
                    }
                },
                error:function (result) {
                    console.log(result);
                    alert("服务器出错！");
                }
            });
        }
    }
</script>
</html>