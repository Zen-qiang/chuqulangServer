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
		var pictures = [];
		var pic = "";
		function gotoBase (e) {
	        console.log(e.target.files[0])
	        var file = e.target.files[0]
	        var reader = new FileReader()
	        reader.readAsDataURL(file)
	        reader.onload = function () {
	            //pictures.push(this.result);
	            pic = this.result;
	        }
	    }
		$(function(){
			$('#commonRequestSubmit').click(function(){
				var url = $('#url').val();
				url = 'api/' + url;
				
				var method = $('#method').val();
				var paramName1 = $('#paramName1').val();
				var paramValue1 = $('#paramValue1').val();
				var paramName2 = $('#paramName2').val();
				var paramValue2 = $('#paramValue2').val();
				var paramName3 = $('#paramName3').val();
				var paramValue3 = $('#paramValue3').val();
				var paramName4 = $('#paramName4').val();
				var paramValue4 = $('#paramValue4').val();
				var paramName5 = $('#paramName5').val();
				var paramValue5 = $('#paramValue5').val();
				var paramName6 = $('#paramName6').val();
				var paramValue6 = $('#paramValue6').val();
				var paramName7 = $('#paramName7').val();
				var paramValue7 = $('#paramValue7').val();
				var paramName8 = $('#paramName8').val();
				var paramValue8 = $('#paramValue8').val();
				var paramName9 = $('#paramName9').val();
				var paramValue9 = $('#paramValue9').val();
				var paramName10 = $('#paramName10').val();
				var paramValue10 = $('#paramValue10').val();
				
				var data = {};
				if (paramName1) {
					data[paramName1] = paramValue1;
				}
				if (paramName2) {
					data[paramName2] = paramValue2;
				}
				if (paramName3) {
					data[paramName3] = paramValue3;
				}
				if (paramName4) {
					data[paramName4] = paramValue4;
				}
				if (paramName5) {
					data[paramName5] = paramValue5;
				}
				if (paramName6) {
					data[paramName6] = paramValue6;
				}
				if (paramName7) {
					data[paramName7] = paramValue7;
				}
				if (paramName8) {
					data[paramName8] = paramValue8;
				}
				if (paramName9) {
					data[paramName9] = paramValue9;
				}
				if (paramName10) {
					data[paramName10] = paramValue10;
				}
				
				console.log(data);
				
				if (method == 'get') {
					$.get(url, data, function(result) {
						console.log(result);
					});
				} else {
					$.post(url, data, function(result){
	                	console.log(result);
	                });
				}
            });
			
			$('#test').click(function(){
                $.post("api/editCoterie",{
                	userId : $('#userId').val(),
                	coterieId : $('#coterieId').val(),
                	picture : pic
                },function(result){
                	console.log(result);
                });
                
            });
		});
	</script>
</head>
<body>
<h2>测试页面</h2>
<P>  The time on the server is ${serverTime}. </P>

<!-- <img src="resources/dllogo.svg" onerror="" /> -->

<h2>账号登录：</h2>
<form action="user/login" method="post">
	<input type="hidden" value="username" name="type">
	用户名：<input type="text" name="phoneno" value="18270790997"/><br>
	密码：<input type="text" name="password" value="xxh131420"/><br>
	<button type="submit" >登录</button>
</form>

<h2>接口测试</h2>
URL：<input type="text" id="url" value=""/><br>
METHOD：<input type="text" id="method" value="get"/><br>
NAME：<input type="text" id="paramName1" value=""/>VALUE：<input type="text" id="paramValue1" value=""/><br>
NAME：<input type="text" id="paramName2" value=""/>VALUE：<input type="text" id="paramValue2" value=""/><br>
NAME：<input type="text" id="paramName3" value=""/>VALUE：<input type="text" id="paramValue3" value=""/><br>
NAME：<input type="text" id="paramName4" value=""/>VALUE：<input type="text" id="paramValue4" value=""/><br>
NAME：<input type="text" id="paramName5" value=""/>VALUE：<input type="text" id="paramValue5" value=""/><br>
NAME：<input type="text" id="paramName6" value=""/>VALUE：<input type="text" id="paramValue6" value=""/><br>
NAME：<input type="text" id="paramName7" value=""/>VALUE：<input type="text" id="paramValue7" value=""/><br>
NAME：<input type="text" id="paramName8" value=""/>VALUE：<input type="text" id="paramValue8" value=""/><br>
NAME：<input type="text" id="paramName9" value=""/>VALUE：<input type="text" id="paramValue9" value=""/><br>
NAME：<input type="text" id="paramName10" value=""/>VALUE：<input type="text" id="paramValue10" value=""/><br>
<button type="button" id="commonRequestSubmit" >发送请求</button>

<h2>TEST：</h2>
userId：<input type="text" value="2" id="userId"/><br>
coterieId：<input type="text" value="4" id="coterieId"/><br>
<input type="file" value="转base64" id="input" onchange="gotoBase(event)"><br>
<button id="test">TEST</button>

<h2>圈子列表</h2>
<button type="button" id="getCoterieList">圈子列表</button>

<h2>我的聊天室：</h2>
<form action="chat/getChatrooms" method="get">
	<button type="submit" >我的聊天室</button>
</form>

<h2>创建聊天话题：</h2>
<form action="api/createActivityTopic" method="post">
	userId：<input type="text" name="userId" value="1"/><br>
	activityId：<input type="text" name="activityId" value="10"/><br>
	content：<input type="text" name="content" value="TTTTTTTT"/><br>
	<button type="submit" >创建聊天话题</button>
</form>

<h2>我的圈子：</h2>
<form action="api/getMyCoteries" method="get">
	userId：<input type="text" name="userId" value="1"/><br>
	dataType：<input type="text" name="dataType" value=""/><br>
	<button type="submit" >我的圈子</button>
</form>

<h2>创建圈子：</h2>
<button type="button" id="createCoterie">创建圈子</button>

<h2>获取用户名片：</h2>
<form action="user/getUserInfo" method="get">
	userId：<input type="text" name=userId value="1"/><br>
	accid:<input type="text" name=accid value="18270790997"/><br>
	<button type="submit" >获取用户名片</button>
</form>

<h2>获取用户信息：</h2>
<form action="user/getUser" method="get">
	userId：<input type="text" name=userId value="2"/><br>
	<button type="submit" >获取用户信息</button>
</form>

<h2>获取用户信息：</h2>
<form action="user/getUserByAccid" method="get">
	accid：<input type="text" name=accid value="18270790997"/><br>
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
<form action="activity/getUserActivityList" method="post">
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
<form action="api/praiseTopic" method="get">
userId：<input type="text" name="userId"/><br>
topicId : <input type="text" name="topicId" value=""/><br>
	<button type="submit" >话题点赞</button>
</form>

<h2>搜索圈子或者话题</h2>
<form action="api/searchActivityOrTopic" method="get">
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
parentId : <input type="text" name="parentId" value=""/><br>
	<button type="submit" >获取标签列表</button>
</form>

<h2>获取活动类型,根据description字段获取所有typeName</h2>
<form action="activity/getActivityType" method="get">
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
<form action="api/commentTopic" method="post">
userId：<input type="text" name="userId"/><br>
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
<form action="api/getActivityList" method="get">
category：<input type="text" name="category" value=""/><br>
status：<input type="text" name="status" value=""/><br>
orderby：<input type="text" name="orderby" value=""/><br>
start：<input type="text" name="start" value="0"/><br>
pagesize：<input type="text" name="pagesize" value="20"/><br>
keyword：<input type="text" name="keyword" value="浦东"/><br>
	<button type="submit" >获取活动列表</button>
</form>

<h2>活动报名</h2>
<form action="api/signUp" method="post">
	activityId：<input type="text" name="activityId"/><br>
	userId：<input type="text" name="userId"/><br>
	gender：<input type="text" name="gender"/><br>
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
