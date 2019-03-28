<%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/3/16
  Time: 14:03
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en" xmlns:v-bind="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <link href="https://file.ourbeibei.com/l_e/static/css/teacher.css" rel="stylesheet">
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <style type="text/css"></style>
    <title>背呗挑战赛</title>
</head>
<body>
<div class="container" id="app">
    <div class="content-container">
        <span class="huge-text darker-black" style="margin-top: 3.5rem">背呗督学老师</span>
        <img class="teacher-portrait" v-bind:src="portrait" alt="">
        <span class="min-text darker-black" style="margin-top: 0.2rem">{{teacher_name}}</span>
        <img v-bind:src="qr_code" class="qr-code" alt="">
        <span class="normal-text gray" style="margin-top: 3.5rem">长按添加了解活动</span>
    </div>
</div>
<script src="https://file.ourbeibei.com/l_e/static/js/teacher.js"></script>
<!--<script src="teacher.js"></script>-->
</body>
</html>