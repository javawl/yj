<%--
  Created by IntelliJ IDEA.
  User: 63254
  Date: 2019/3/13
  Time: 15:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="en" xmlns:v-bind="http://www.w3.org/1999/xhtml" xmlns:v-on="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
    <title>背呗阅读报名</title>
    <link href="https://file.ourbeibei.com/l_e/static/css/book_sign_up.css" rel="stylesheet" type="text/css"/>
    <style type="text/css"></style>
    <script src="https://file.ourbeibei.com/l_e/static/node_modules/vue/dist/vue.js"></script>
    <script src="https://file.ourbeibei.com/l_e/static/js/jquery.js"></script>
    <script src="https://res.wx.qq.com/open/js/jweixin-1.4.0.js"></script>
</head>
<body>
<div class="container" id="app">
    <div class="header-container">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_book_sign_up.png" alt="header"/>
        <canvas id="class-info" width="200" height="100"></canvas>
        <div class="text-container">
            <div class="label-text">第{{periods}}期</div>
            <div class="label-text" style="margin-top: 0.7rem">开课时间{{st}}</div>
        </div>
    </div>
    <div class="label-container">
        <div class="label" v-bind:class="{select: select_page === 1, normal: select_page === 2}"
             v-on:click="onPageSelect(1)">
            课程介绍
            <div class="baseline" v-if="select_page === 1"></div>
        </div>
        <div class="label" v-bind:class="{select: select_page === 2, normal: select_page === 1}"
             v-on:click="onPageSelect(2)">
            书单介绍
            <div class="baseline" v-if="select_page === 2"></div>
        </div>
    </div>
    <!--<div id="content_container" style="width: 100%;">-->
    <div class="info-container" v-if="select_page === 1">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_1.png" alt="test1">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_2.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_3.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_4.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_5.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_6.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_7.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_8.png" alt="">
        <img src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_9.png" alt="">
        <div class="last-image-container">
            <img style="width: 100%;" src="https://file.ourbeibei.com/l_e/static/images/bg_reading_info_10.png" alt="">
            <div class="info-sign-people-text">
                <span class="normal-text">已报名阅读人数</span>
                <span class="huge-text">{{people}}</span>
                <span class="normal-text">人</span>
                <div>
                    <span class="normal-text">你也快来试试吧</span>
                </div>
            </div>
        </div>

    </div>
    <div class="detail-container" v-else>
        <div class="header-label">
            <div class="line"></div>
            <span>1期书单</span>
            <div class="line"></div>
        </div>
        <div id="content" style="width: 100%;display: flex;flex-direction: column;align-items: center">
            <div class="book-container" v-on:click="onSelectBook(index)" v-for="(key, index) in book_list" v-bind:class="{animated: index === select}">
                <div class="list-title-container">
                    <div style="height: 2rem;width: 0.2rem;background-color: white;margin-left: 1.5rem;"></div>
                    <div class="list-title">{{key.list_title}}</div>
                    <div class="list-word-demand">词汇量要求 {{key.demand}}</div>
                    <div class="selection" v-bind:class="{selected: select === index}">{{select === index ? '已选' :
                        '选择'}}
                    </div>
                </div>
                <div class="book-content-container">
                    <div class="book-title-container">
                        <div v-for="item in key.books" class="book-title">{{item.title}}</div>
                    </div>
                    <div class="book-image-container">
                        <img v-for="(item, idx) in key.books" alt="" v-bind:src="item.img"
                             v-bind:style="{ zIndex: 10 - idx, right: (idx * 2.5 + 2) + 'rem'}">
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!--</div>-->

    <div class="bottom-button-container">
        <button v-if="select_page === 1" class="appoint-button pink" id="sign_button" v-on:click="selectSignPage()"
                style="line-height: 5.5rem;font-size: 2.3rem">立即报名
        </button>

        <div v-else style="width: 100%;height: 100%;">
            <div v-bind:class="{gray: is_reserved === true || select === undefined, pink: !(is_reserved === true || select === undefined)}"
                 class="appoint-button" v-if="signType === 'reserved'" v-on:click="onAppoint()">
                <div v-if="is_reserved === false" style="display: flex;flex-direction: column;align-items: center">
                    <span style="margin-top: 10px">预约下一期</span>
                    <div class="sign-people">已有{{people}}人报名</div>
                </div>
                <div v-else>
                    <span style="line-height: 7rem">您已预约下一期</span>
                </div>
            </div>
            <div class="sign-button-container" v-else>

                <span class="origin-pay-container"
                      v-bind:class="{'darker-gray': select !== undefined, gray: select === undefined}"
                      v-on:click="onSignTap()">￥99.9<span>直接支付</span></span>
                <span class="discount-pay-container"
                      v-bind:class="{pink: select !== undefined, gray: select === undefined}"
                      v-on:click="onAssistTap()">￥59.9<span>发起助力</span></span>

            </div>

        </div>
    </div>

</div>
<script src="https://file.ourbeibei.com/l_e/static/js/book_sign_up.js"></script>
<!--<script src="book_sign_up.js"></script>-->

</body>
</html>