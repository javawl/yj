<%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/3/16
  Time: 14:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" xmlns:v-on="http://www.w3.org/1999/xhtml" xmlns:v-bind="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <title>背呗背单词</title>
    <link rel="stylesheet" href="https://file.ourbeibei.com/l_e/static/css/challenge_sign_success.css" type="text/css">
    <!--<link href="challenge_sign_success.css" rel="stylesheet" type="text/css">-->
    <style type="text/css"></style>
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
</head>
<body>
<div class="container" id="app">
    <div class="content-container">
        <span class="huge-text darker-black bold" style="margin-top: 3.5rem">报名成功！</span>
        <span class="normal-text black" style="margin-top: 2.5rem">请收下你的学号</span>
        <div class="number-container">
            <span class="number">{{studentId}}</span>
        </div>
        <div class="time-container" v-if="st !== undefined && st !== null">
            <span class="normal-text black">开始时间：{{st}}</span>
            <span class="normal-text black">结束时间：{{et}}</span>
        </div>

        <span class="normal-text gray" style="margin-top: 2.5rem">快添加老师吧!</span>
        <span class="normal-text gray">老师带你认识更多学习小伙伴!</span>

        <img class="qr-code" v-bind:src="test" alt="" style="background-color: dodgerblue;margin-top: 2rem">

        <span class="normal-text gray" style="margin-top: 2rem">长按添加老师</span>

        <span class="normal-text bold pink" style="margin-top: 2.5rem">最终奖励金由老师发放哟~</span>

        <span class="min-text black" style="margin-top: 0.5rem">ps:备注请添加学号</span>

    </div>
    <button class="bottom-button-container" v-on:click="onNavigateTap()">前往小程序</button>
    <div v-if="isShowDialog === true" class="mask">
        <div class="qr-code-container">
            <img src="https://file.ourbeibei.com/l_e/static/images/min_program_qr_code.jpg" alt="">
        </div>
        <img class="close-img" src="https://file.ourbeibei.com/l_e/static/images/ic_dialog_close.png" alt="" v-on:click="onCloseDialog()">
    </div>
</div>
<script src="https://file.ourbeibei.com/l_e/static/js/challenge_sign_success.js"></script>
<!--<script src="challenge_sign_success.js"></script>-->
</body>
</html>