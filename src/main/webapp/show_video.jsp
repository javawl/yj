<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>后台管理系统</title>
    <script src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript">
        var url = 'http://localhost:8088';
//        var url = 'http://123.207.85.37:8080';
        var root_url = 'http://47.107.62.22/l_e/';
        function GetQueryString(name)
        {
            var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
            var r = window.location.search.substr(1).match(reg);
            if(r!=null)return  unescape(r[2]); return null;
        }
        $(document).ready(function(){
            var id = GetQueryString("id");
            $.ajax({
                url:url+"/admin/get_word_video.do?id="+id,
                type:'GET',
                dataType:'json',
                success:function (result) {
                    var data = result["data"];
                    $("#all").append('<h3>视频个数：'+data.length+'</h3>')
                    for(var i = 0; i < data.length; i++) {
                        $("#all").append(
                                '<h3>出自：' + data[i]['video_name'] + '</h3>' +
                                '<h3>包含单词那句台词：' + data[i]['sentence'] + '  解释：'+data[i]['translation']+'  用法：'+data[i]['word_usage']+'</h3>' +
                                '<h3><audio src="'+root_url+data[i]['sentence_audio']+'" controls="controls"></audio></h3>' +
                                '<h3>该用法的使用率' + data[i]['rank'] + '%</h3>' +
                                '<h3>封面图片：<img src="' +root_url+ data[i]['img'] + '"></h3>' +
                                '<h3>视频：<video src="'+root_url+ data[i]['video'] + '" controls="controls"></video></h3>' +
                                '<div id="sub'+i+'"><h3>字幕:</h3></div>');
                        for(var j = 0; j < data[i]['subtitles'].length; j++){
                            $("#sub"+i).append(
                                '<p>片段第'+data[i]['subtitles'][j]['st']+'到第'+data[i]['subtitles'][j]['et']+'毫秒,台词：'+data[i]['subtitles'][j]['en']+'翻译：'+data[i]['subtitles'][j]['cn']+'</p>'
                            );
                        }
                    }
                },
                error:function (result) {
                    console.log(result);
                    alert("服务器出错！");
                }
            });
        });
    </script>
</head>
<body>
    <div id="all">

    </div>
</body>
</html>
