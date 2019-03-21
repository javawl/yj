<%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/3/16
  Time: 14:04
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" xmlns:v-on="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <title>单词挑战报名</title>
    <!--<link href="word_sign_up.css" type="text/css" rel="stylesheet"/>-->
    <link href="https://file.ourbeibei.com/l_e/static/css/word_sign_up.css" type="text/css" rel="stylesheet">
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://res.wx.qq.com/open/js/jweixin-1.4.0.js"></script>
    <style type="text/css"></style>
</head>
<body>
<div class="container" id="app">
    <div class="img-container">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_word_challenge_header.png" alt="单词挑战头图">
        <span class="label-text">第{{periods}}期</span>
    </div>
    <div class="img-container">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_word_challenge_info.png" alt="单词挑战详情">
        <!--<span class="label-rule">规则详情</span>-->
        <div class="challenge-label-text">
            <span class="normal-text">已有</span>
            <span class="huge-text">{{people}}</span>
            <span class="normal-text">人参加挑战</span>
        </div>
        <!--<div class="invite-container">-->
        <!--<span>邀好友获奖励</span>-->
        <!--</div>-->
        <span class="min-text">最终解释权归背呗背单词所有</span>
    </div>
    <button class="sign-button" v-on:click="onSignTap()">立即开始挑战</button>
    <div v-if="isShowDialog === true" class="mask">
        <div class="qr-code-container">
            <img src="https://file.ourbeibei.com/l_e/static/images/min_program_qr_code.jpg" alt="">
        </div>
        <img class="close-img" src="https://file.ourbeibei.com/l_e/static/images/ic_dialog_close.png" alt="" v-on:click="onCloseDialog()">
    </div>
</div>
<script src="https://file.ourbeibei.com/l_e/static/js/word_sign_up.js"></script>
<!--<script src="word_sign_up.js"></script>-->
</body>
</html>