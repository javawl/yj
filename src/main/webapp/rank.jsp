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
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <link href="https://file.ourbeibei.com/l_e/static/css/rank.css" rel="stylesheet">
    <!--<link href="rank.css" rel="stylesheet">-->
    <style type="text/css"></style>
    <title>挑战排行榜</title>
</head>
<body>
<div class="container" id="app">
    <span class="header-label">单词排行榜</span>
    <div class="rank-container">
        <div class="rank-header-container">
            <span class="label text-center">名词</span>
            <span class="label text-center">学员</span>
            <span class="label text-center">学号</span>
            <span class="label text-center">单词数</span>
        </div>
        <div class="rank-people-container" v-for="(item, index) in rank_list">
            <div class="rank">
                <img src="https://file.ourbeibei.com/l_e/static/images/ic_gold_medal.png" alt="" v-if="index === 0">
                <img src="https://file.ourbeibei.com/l_e/static/images/ic_silver_medal.png" alt="" v-else-if="index === 1">
                <img src="https://file.ourbeibei.com/l_e/static/images/ic_bronze_medal.png" alt="" v-else-if="index === 2">
                <span v-bind:class="{white: index < 3}">{{index + 1}}</span>
            </div>
            <span class="name">{{item.username}}</span>
            <span class="student-number">{{item.number}}</span>
            <span class="words-count">{{item.wordCount}}</span>
        </div>
    </div>
</div>
<script src="https://file.ourbeibei.com/l_e/static/js/rank.js"></script>
<!--<script src="rank.js"></script>-->
</body>
</html>