<%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/3/16
  Time: 14:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" xmlns:v-bind="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <link rel="stylesheet" href="https://file.ourbeibei.com/l_e/static/css/challenge_share.css">
    <!--<link rel="stylesheet" href="challenge_share.css">-->
    <style type="text/css"></style>
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <title>背呗背单词</title>
</head>
<body>
<div id="app" class="container">
    <img v-bind:src="haeder_img" class="header-img" alt="">
    <img v-bind:src="content_img" class="tmp-img" alt="">
    <div class="header-portrait">
        <img v-bind:src="portrait" class="circle-img" alt="">
        <span>我已坚持背单词{{days}}天，已有{{friends}}位好友与我一起学习</span>
    </div>
    <div class="bottom-button-container">
        <div style="position: relative;width: 100%;height: 100%;display: flex;flex-direction: column;justify-content: center;">
            <div class="text-container">
                <div class="normal-text white">背单词 上背呗</div>
                <div class="huge-text-container">
                    <span class="white">坚持打卡瓜分</span><span class="yellow">万元现金</span>
                </div>
                <div class="min-text white">活动倒计时：{{hours}}:{{minutes}}:{{seconds}}</div>
            </div>

            <div class="qr-code-container">
                <div class="qr-code">
                    <img src="https://file.ourbeibei.com/l_e/static/images/qrcode_for_gh.jpg" alt="">
                </div>
                <span class="white">扫码了解活动</span>
            </div>
        </div>

    </div>
</div>
<script src="https://file.ourbeibei.com/l_e/static/js/challenge_share.js"></script>
<!--<script src="challenge_share.js"></script>-->
</body>
</html>