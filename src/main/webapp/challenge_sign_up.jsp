<%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/3/16
  Time: 10:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" xmlns:v-on="http://www.w3.org/1999/xhtml" xmlns:v-bind="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <title>背呗挑战赛</title>
    <link rel="stylesheet" href="https://file.ourbeibei.com/l_e/static/css/challenge_sign_up.css" type="text/css">
    <!--<link href="challenge_sign_up.css" type="text/css" rel="stylesheet" >-->
    <style type="text/css"></style>
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <script src="https://res.wx.qq.com/open/js/jweixin-1.4.0.js"></script>
</head>
<body>
<div id="app" style="height: 100%;width: 100%;">
    <div class="container">
        <!--可滚动元素-->
        <div style="position:relative;width: 100%;">
            <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_header_challenge.png" alt="">
            <div class="end-time-container" v-if="signType === 'formal'">
                <span class="normal-text">第{{periods}}期报名开启</span>
                <div class="end-time">
                    <span class="normal-text">仅剩{{rest_number}}个名额</span>
                    <span class="normal-text" style="margin-left: 1rem">{{hours}}:{{minutes}}:{{seconds}}</span>
                </div>

            </div>
            <img class="learn-img" src="https://file.ourbeibei.com/l_e/static/images/ic_try.png" alt="" v-on:click="onLearningClick()">
        </div>

        <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_challenge_1.png" alt="">
        <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_challenge_2.png" alt="">
        <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_challenge_3.png" alt="">
        <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_challenge_4.png" alt="">
        <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_challenge_5.png" alt="">
        <div style="position: relative;width: 100%;">
            <img class="bg-img" src="https://file.ourbeibei.com/l_e/static/images/bg_challenge_6.png" alt="">
            <div style="text-align: center;width: 100%;display: flex;justify-content: center">
                <span class="text-container normal-text" v-if="signType === 'formal'">仅剩&nbsp<span class="larger-text">{{rest_number}}</span>&nbsp个名额</span>
            </div>
            <span class="min-text">最终解释权归背呗背单词所有</span>
        </div>
    </div>

    <div class="bottom-button-container" v-if="status === 'no'">
        <div class="flex-container" v-if="signType === 'formal'">
            <span class="money-text pink">￥29.9</span>
            <div class="info-text-container">
                <span class="gray normal-text">90天全勤学习并打卡</span>
                <span class="pink normal-text">&nbsp;&nbsp;奖励<span class="pink huge-text">100</span> 元</span>
            </div>
            <button class="challenge-button" v-on:click="onChallengeSign()">立即挑战</button>
        </div>
        <div class="flex-container" v-else  style="text-align: center">
            <span class="purple huge-text" v-on:click="onAppoint()">预约下一期</span>
        </div>
    </div>
    <div class="bottom-button-container" v-else-if="status === 'yes'" style="background-color: lightgray;text-align: center">
        <div class="signed-text">您已经报名参加了</div>
    </div>

    <div class="mask" v-if="isShowDialog === true">
        <div class="dialog-container">
            <span class="label-text">还在犹豫？<span>联系督学老师了解下</span></span>
            <div class="portrait-container">
                <img src="https://file.ourbeibei.com/l_e/static/images/img_portrait_tmp.png" alt="">
            </div>
            <button class="pay-button" v-on:click="onRepay()">重新支付</button>
            <button class="chat-button" v-on:click="onTeacherTap()">跟TA聊聊</button>
        </div>

        <img class="close-img" src="https://file.ourbeibei.com/l_e/static/images/ic_dialog_close.png" alt="" v-on:click="onCloseDialog()">
    </div>
    <div class="mask" v-if="isShowQrCode === true">
        <div class="qr-code-container">
            <img src="https://file.ourbeibei.com/l_e/static/images/min_program_qr_code.jpg" alt="">
        </div>
        <img class="close-img" src="https://file.ourbeibei.com/l_e/static/images/ic_dialog_close.png" alt="" v-on:click="onCloseQrCode()">
    </div>
</div>
<script src="https://file.ourbeibei.com/l_e/static/js/challenge_sign_up.js"></script>
<!--<script src="challenge_sign_up.js"></script>-->
</body>
</html>