<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Home</title>
	
	<script type="text/javascript" src="resources/jquery-3.2.1.js"></script>
	
	<script type="text/javascript">
		$(function(){
			$('#verifynoBtn').click(function(){
				$.ajax({
				  url: 'user/sendCode',
				  data: {
					  phoneno : $('#phoneno').val(),
					  dataType : 'register'
				  },
				  success: function(response,status,xhr){
					  if (response.message) {
						alert(response.message);
					  }
					  console.log(response);
				  },
				  dataType: 'json'
				});
			});
			
			$('#verifynoBtn2').click(function(){
				$.ajax({
				  url: 'user/sendCode',
				  data: {
					  phoneno : $('#phoneno2').val(),
					  dateType : 'login'
				  },
				  success: function(response,status,xhr){
					  console.log(response);
				  },
				  dataType: 'json'
				});
			});
			
			$('#getUser').click(function(){
				$.ajax({
				  url: 'user/getUser',
				  data: {
					  userId : '3'
				  },
				  success: function(response,status,xhr){
					  console.log(response);
				  },
				  dataType: 'json'
				});
			});
			
			$('#launchActivity').click(function(){
                var tags = [];
                tags.push(2);
                tags.push(5);
                
                var friends = [];
                friends.push(1);
                friends.push(2);
                
                $.ajax({
                    url: 'activity/launchActivity',
					type : 'POST',
                    data: {
                    	typename : '个人组织',
                        isOpen : false,
                        password : 'admin',
                        tags : [2,1],
                        name : '金桥广场舞',
                        startTime : 1499124171120,
                    	userCount : 10,
                        charge : 'dutch',
                        cost : 100,
                        gps : 'gps',
                        address: 'address',
                        description : '室内三国杀',
                        limiter : '限男性',
                        pictures : 'picture',
                        phoneNo : '18270790997'
                    },
                    success: function(response,status,xhr){
                        console.log(response);
                    },
                    dataType: 'json'
                });
            });
			
		});
	</script>
</head>
<body>
<h2>测试页面</h2>
<P>  The time on the server is ${serverTime}. </P>

<img src="http://localhost:8081/chuqulang-resource/profile/18270790999/avatar.png" onerror="" />

<h2>账号登录：</h2>
<form action="user/login" method="post">
	<input type="hidden" value="username" name="type">
	用户名：<input type="text" name="phoneno" value="18270790997"/><br>
	密码：<input type="text" name="password" value="xxh131420"/><br>
	<button type="submit" >登录</button>
</form>

<h2>获取用户信息：</h2>
<form action="user/getUser" method="get">
	userId：<input type="text" name=userId value="2"/><br>
	<button type="submit" >获取用户信息</button>
</form>

<h2>删除好友：</h2>
<form action="chat/deleteFriend" method="post">
	faccid：<input type="text" name="faccid" value="18270790998"/><br>
	<button type="submit" >删除好友</button>
</form>

<h2>修改备注：</h2>
<form action="chat/updateFriend" method="post">
	faccid：<input type="text" name="faccid" value="18270790998"/><br>
	alias：<input type="text" name="alias" value="啊啊啊啊啊"/><br>
	<button type="submit" >修改备注</button>
</form>

<h2>添加好友：</h2>
<form action="chat/addFriend" method="post">
	faccid：<input type="text" name="faccid" value="18270790998"/><br>
	type：<input type="text" name="type" value="1"/><br>
	msg：<input type="text" name="msg" value="是否添加好友"/><br>
	<button type="submit" >添加好友</button>
</form>

<h2>发送聊天室消息：</h2>
<form action="chat/chatRoomSendMsg" method="post">
	roomid：<input type="text" name="roomid" value="10179360"/><br>
	msgId：<input type="text" name="msgId" value="c9e6c306-804f-4ec3-b8f0-573778829419"/><br>
	msgType：<input type="text" name="msgType" value="0"/><br>
	resendFlag：<input type="text" name="resendFlag" value="0"/><br>
	attach：<input type="text" name="attach" value="发送聊天室消息内容"/><br>
	<button type="submit" >发送聊天室消息</button>
</form>

<h2>请求聊天室地址：</h2>
<form action="chat/requestChatRoomAddr" method="post">
	roomid：<input type="text" name="roomid" value="10179360"/><br>
	clienttype：<input type="text" name="clienttype" value="1"/><br>
	<button type="submit" >请求聊天室地址</button>
</form>

<h2>切换聊天室状态：</h2>
<form action="chat/toggleCloseChatRoom" method="post">
	roomid：<input type="text" name="roomid" value="10179360"/><br>
	valid：<input type="text" name="valid" value="true"/><br>
	<button type="submit" >切换聊天室状态</button>
</form>

<h2>更新聊天室：</h2>
<form action="chat/updateChatRoom" method="post">
	roomid：<input type="text" name="roomid" value="10179360"/><br>
	name：<input type="text" name="name" value="聊天室tt"/><br>
	announcement：<input type="text" name="announcement" value="公告tt"/><br>
	<button type="submit" >更新聊天室</button>
</form>

<h2>创建聊天室：</h2>
<form action="chat/createChatRoom" method="post">
	eventId：<input type="text" name="eventId" value=""/><br>
	name：<input type="text" name="name" value="聊天室"/><br>
	announcement：<input type="text" name="announcement" value="公告"/><br>
	<button type="submit" >创建聊天室</button>
</form>

<h2>发送信息：</h2>
<form action="chat/sendMessage" method="post">
	to：<input type="text" name="to" value="helloworld"/><br>
	type：<input type="text" name="type" value="0"/><br>
	body：<input type="text" name="body" value="message"/><br>
	<button type="submit" >发送信息</button>
</form>

<h2>我的活动</h2>
<form action="activity/getUserActivityList">
dataType : <input type="text" name="dataType" value="0"/><br>
start : <input type="text" name="start" value="0"/><br>
pagesize : <input type="text" name="pagesize" value="20"/><br>
	<button type="submit" >我的活动</button>
</form>

<h2>退出圈子</h2>
<form action="discover/exitCoterie" method="post">
coterieId : <input type="text" name="coterieId" value=""/><br>
	<button type="submit" >退出圈子</button>
</form>

<h2>加入圈子</h2>
<form action="discover/joinCoterie" method="post">
coterieId : <input type="text" name="coterieId" value=""/><br>
	<button type="submit" >加入圈子</button>
</form>

<h2>话题点赞</h2>
<form action="discover/praiseTopic" method="get">
topicId : <input type="text" name="topicId" value=""/><br>
	<button type="submit" >话题点赞</button>
</form>

<h2>搜索圈子或者话题</h2>
<form action="discover/searchActivityOrTopic" method="get">
keyword : <input type="text" name="keyword" value=""/><br>
	<button type="submit" >搜索圈子或者话题</button>
</form>

<h2>关注用户</h2>
<form action="user/followUser" method="post">
userId : <input type="text" name="userId" value=""/><br>
	<button type="submit" >关注用户</button>
</form>

<h2>获取标签列表</h2>
<form action="activity/getTagList" method="get">
typeNameId : <input type="text" name="typeNameId" value=""/><br>
	<button type="submit" >获取标签列表</button>
</form>

<h2>获取活动类型,根据description字段获取所有typeName</h2>
<form action="activity/getActivityType" method="get">
type : <input type="text" name="type" value="活动类型"/><br>
	<button type="submit" >>获取活动类型</button>
</form>

<h2>获取用户关注/粉丝</h2>
<form action="user/getUserAttention" method="get">
isAttention : <input type="text" name="isAttention" value="true"/><br>
start : <input type="text" name="start" value="0"/><br>
pagesize : <input type="text" name="pagesize" value="1"/><br>
	<button type="submit" >获取用户关注</button>
</form>


<h2>获取联系人</h2>
<form action="user/getContacts" method="get">
	<button type="submit" >获取联系人</button>
</form>

<h2>获取我的兴趣</h2>
<form action="user/getTags" method="post">
	<button type="submit" >获取我的兴趣</button>
</form>

<h2>获取评论信息</h2>
<form action="discover/getTopicComments" method="post">
	topicId：<input type="text" name="topicId"/><br>
	<button type="submit" >获取评论信息</button>
</form>

<h2>评论话题</h2>
<form action="discover/commentTopic" method="post">
	topicId：<input type="text" name="topicId"/><br>
	comment：<input type="text" name="comment"/><br>
	<button type="submit" >评论话题</button>
</form>

<h2>发布话题</h2>
<form action="discover/editDiscover" method="post">
	coterieId：<input type="text" name="coterieId"/><br>
	img：<input type="text" name="img"/><br>
	description：<input type="text" name="description"/><br>
	<button type="submit" >发布话题</button>
</form>

<h2>获取话题列表</h2>
<form action="discover/getTopicList" method="post">
	coterieId：<input type="text" name="coterieId" value="0"/><br>
	start：<input type="text" name="start" value="0"/><br>
	pagesize：<input type="text" name="pagesize" value="20"/><br>
	orderby：<input type="text" name="orderby"/><br>
	<button type="submit" >获取话题列表</button>
</form>

<h2>获取圈子列表</h2>
<form action="discover/getCoterieList" method="post">
	typename：<input type="text" name="typename"/><br>
	tagid：<input type="text" name="tagid"/><br>
	start：<input type="text" name="start" value="0"/><br>
	pagesize：<input type="text" name="pagesize" value="20"/><br>
	orderby：<input type="text" name="orderby" value=""/><br>
	<button type="submit" >获取圈子列表</button>
</form>

<h2>获取所有活动</h2>
<form action="activity/getAllActivity" method="get">
	orderBy：	<input type="text" name="orderBy"/><br>
	category：	<input type="text" name="category"/><br>
	status：		<input type="text" name="status"/><br>
	keyword：	<input type="text" name="keyword"/><br>
	<button type="submit" >获取所有活动</button>
</form>

<h2>收藏|取消收藏活动</h2>
<form action="activity/changeFavStatus" method="post">
	eventId：<input type="text" name="eventId" value="2"/><br>
	isFav：<input type="text" name="isFav" value="true"/><br>
	<button type="submit" >收藏|取消收藏活动</button>
</form>

<h2>获取活动详情</h2>
<form action="activity/getActivityInfo" method="post">
	eventId：<input type="text" name="eventId" value="2"/><br>
	<button type="submit" >获取活动详情</button>
</form>

<h2>获取我的活动列表</h2>
<form action="activity/getActivityList" method="post">
category：<input type="text" name="category" value=""/><br>
status：<input type="text" name="status" value=""/><br>
orderby：<input type="text" name="orderby" value=""/><br>
start：<input type="text" name="start" value="0"/><br>
pagesize：<input type="text" name="pagesize" value="20"/><br>
isOwnList：<input type="text" name="isOwnList" value="false"/><br>
	<button type="submit" >获取活动列表</button>
</form>

<h2>活动报名</h2>
<form action="activity/signUp" method="post">
	eventId：<input type="text" name="eventId"/><br>
	<button type="submit" >活动报名</button>
</form>

<h2>发起活动</h2>
<button type="button" id="launchActivity">launchActivity</button>

<h2>更改头像：</h2>
<form action="user/updatePicture" enctype="multipart/form-data" method="post">
	<input type="file" name="file" />
	<button type="submit" >更改头像</button>
</form>

<form id="uploadForm" action="user/updatePicture" method="post" enctype="multipart/form-data">
	<input id="fileImage" type="file" name="file" multiple />
	<button type="submit" id="fileSubmit">多文件上传</button>
</form>

<!-- <form enctype="multipart/form-data" method="post">
	<input type="file" multiple=true onchange="doSomething(this.files)"/>
	<button type="submit" >多文件上传</button>
</form> -->

<h2>更改签名：</h2>
<form action="user/changeSignLog" method="post">
	新签名：<input type="text" name="signLog"/><br>
	<button type="submit" >更改签名</button>
</form>

<h2>修改密码：</h2>
<form action="user/changePassword" method="post">
	<input type="hidden" name="userId" value="1"/>
	新密码：<input type="text" name="newPassword"/><br>
	<button type="submit" >修改密码</button>
</form>


<h2>用户注册：</h2>
<form action="user/register" method="post">
	手机号码：<input type="text" name="phoneno" id="phoneno" value="18270790997"/><button type="button" id="verifynoBtn">获取验证码</button><br>
	密码：<input type="text" name="password"/><br>
	验证码：<input type="text" name="verifyno"/><br>
	<button type="submit" >注册</button>
</form>

<h2>手机登录：</h2>
<form action="user/login" method="post">
<input type="hidden" value="checkcode" name="type">
	手机号码：<input type="text" name="phoneno" id="phoneno2" value="18270790997"/><button type="button" id="verifynoBtn2">获取验证码</button><br>
	验证码：<input type="text" name="verifyno"/><br>
	<button type="submit" >登录</button>
</form>

<h2>获取用户信息：</h2>
<button type="button" id="getUser">getUser</button>


</body>
</html>
