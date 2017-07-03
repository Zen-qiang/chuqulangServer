<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
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
					  userId : '1'
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
                        typename : '室内活动',
                        isOpen : true,
                        tags : [2,5],
                        shortname : '浦东三国杀',
                    	retime : new Date(),
                        number : 10,
                        charge : true,
                        cost : 100,
                        gps : 'gps',
                        description : '室内三国杀',
                        limiter : '限男性',
                        pictures : 'picture',
                        friends : friends,
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
<h2>Hello world!</h2>
<P>  The time on the server is ${serverTime}. </P>

<h2>获取标签列表</h2>
<form action="activity/getTagList" method="get">
typeName : <input type="text" name="typeName" value="常规标签"/><br>
	<button type="submit" >获取标签列表</button>
</form>

<h2>获取用户关注</h2>
<form action="user/getUserAttention" method="post">
	<button type="submit" >获取用户关注</button>
</form>
<h2>获取用户粉丝</h2>
<form action="user/getUserFollow" method="post">
	<button type="submit" >获取用户粉丝</button>
</form>

<h2>获取联系人</h2>
<form action="user/getContacts" method="post">
	<button type="submit" >获取联系人</button>
</form>

<h2>获取我的兴趣</h2>
<form action="user/getTags" method="post">
	<button type="submit" >获取我的兴趣</button>
</form>

<h2>获取评论信息</h2>
<form action="discover/getDiscoverComments" method="post">
	topicId：<input type="text" name="discoverid"/><br>
	<button type="submit" >获取评论信息</button>
</form>

<h2>评论话题</h2>
<form action="discover/commentDiscover" method="post">
	topicId：<input type="text" name="discoverid"/><br>
	comment：<input type="text" name="comment"/><br>
	<button type="submit" >评论话题</button>
</form>

<h2>发布话题</h2>
<form action="discover/editDiscover" method="post">
	ringId：<input type="text" name="ringId"/><br>
	img：<input type="text" name="img"/><br>
	description：<input type="text" name="description"/><br>
	<button type="submit" >发布话题</button>
</form>

<h2>获取话题列表</h2>
<form action="discover/getDiscoverList" method="post">
	ringId：<input type="text" name="ringId"/><br>
	<button type="submit" >获取话题列表</button>
</form>

<h2>获取圈子列表</h2>
<form action="discover/getRingList" method="post">
	tagId：<input type="text" name="tagid"/><br>
	type：<input type="text" name="type" value="new"/><br>
	<button type="submit" >获取圈子列表</button>
</form>

<h2>获取所有活动</h2>
<a href="activity/getAllActivity">获取所有活动</a>

<h2>收藏|取消收藏活动</h2>
<form action="activity/changeFavStatus" method="post">
	eventId：<input type="text" name="eventId" value="2"/><br>
	userId：<input type="text" name="userId" value="2"/><br>
	isFav：<input type="text" name="isFav" value="true"/><br>
	<button type="submit" >收藏|取消收藏活动</button>
</form>

<h2>获取活动详情</h2>
<form action="user/getActivityInfo" method="post">
	eventId：<input type="text" name="eventId" value="2"/><br>
	<button type="submit" >获取活动详情</button>
</form>

<h2>获取活动列表</h2>
<form action="user/getActivityList" method="post">
	userId：<input type="text" name="userId" value="2"/><br>
	<button type="submit" >获取活动列表</button>
</form>

<h2>活动报名</h2>
<form action="activity/signUp" method="post">
	eventId：<input type="text" name="eventId"/><br>
	userId : <input type="text" name="userId"/><br>
	<button type="submit" >活动报名</button>
</form>

<h2>发起活动</h2>
<button type="button" id="launchActivity">launchActivity</button>

<h2>更改头像：</h2>
<form action="user/updatePicture" method="post">
	<input type="hidden" name="userId" value="1"/>
	新头像：<input type="file" name="url"/><br>
	<button type="submit" >更改头像</button>
</form>

<h2>更改签名：</h2>
<form action="user/changeSignLog" method="post">
	<input type="hidden" name="userId" value="1"/>
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

<h2>账号登录：</h2>
<form action="user/login" method="post">
	<input type="hidden" value="username" name="type">
	用户名：<input type="text" name="phoneno" value="18270791343"/><br>
	密码：<input type="text" name="password" value="admin"/><br>
	<button type="submit" >登录</button>
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

<a href="user/test">TEST</a>


</body>
</html>
