<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>上海鼎莲软件科技有限公司</title>

<link href="static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="css/index.css" rel="stylesheet">
<link href="css/communitytalk.css" rel="stylesheet">
<link href="css/activitydetails.css" rel="stylesheet">
<script type="text/javascript" src="resources/jquery-3.2.1.js"></script>
<script src="static/bootstrap/js/bootstrap.min.js"></script>

<script type="text/javascript">
	$(function() {
		for (i in document.images) {
			document.images[i].ondragstart = imgdragstart;
		}
	});

	function imgdragstart() {
		return false;
	}
</script>
</head>
<body>
	<!-- 发现活动导航栏 -->
	<nav class="navbar navbar-inverse navbar-static-top">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#navbar" aria-expanded="false"
					aria-controls="navbar">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a href="#"><img src="resources/img/logo.svg" class="navbar-brand"></a>
			</div>
			<div id="navbar" class="collapse navbar-collapse navbar-right">
				<ul class="nav navbar-nav">
					<li><a href="index">首页</a></li>
					<li class="active"><a href="#">发现活动</a></li>
					<li><a href="community">兴趣圈子</a></li>
					<li><a href="#contact">常见问题</a></li>
				</ul>
				<form class="navbar-form navbar-right">
					<input type="text" class="form-control" placeholder="请输入关键字">
					<A href="#" onclick="userinfo()" data-container="body">
					<span class="glyphicon glyphicon glyphicon-user"
						style="color: white;" aria-hidden="true"></span>
					</A>
				</form>
			</div>
		</div>
	</nav>
	<!-- 大图片 -->
	<div class="bodyContent bodyGray">
	  <div class="row">
	   <div class="col-xs-1 col-sm-1 col-md-3"></div>
	   <div class="col-xs-10 col-sm-10 col-md-6">
			<div class="row divstyle">
				<img class="bodyImage" style="background-size: 100% 100%" src="resources/img/discoveryactivities.jpg">
			</div>
				<!-- 文字开始 -->
			<div class="row divstyle">
				<div class="col-xs-10 col-sm-11 col-md-11 col-lg-11 gongaofont2">
					<span style="font-size: 16px;">上海曹路羽毛球4人双打</span>
				</div>
				<div class="col-xs-2 col-sm-1 col-md-1 col-lg-1 gongaofont3">
					<span class="glyphicon glyphicon-chevron-right"></span>
				</div>
			</div>
			
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2">
					<img class="bodyImage imgstyle2" src="resources/img/discoveryactivities.jpg">
				</div>
				<div class="col-xs-8 col-sm-5 col-md-5 col-lg-7">
					<table style="margin-top: 15px;">
						<tr>
							<th>上海曹路临时聊天</th>
						</tr>
						<tr style="height: 30px;">
							<td style="color: grey;font-size: 12px;">[32条]你就是那个戴绿帽子的！</td>
						</tr>
					</table>
				</div>
				<div class="col-xs-12 col-xs-4  col-lg-3 widget hidden-xs" style="margin-top: 25px;text-align: right;">
					<span style="color: grey;">10-19&nbsp;23：05：02</span>
				</div>
			</div>
			
			
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span class="spanfont">状态</span>
				</div>
				<div class="col-xs-8 col-sm-10 col-md-9 col-lg-10 gongaofont2">
					<span style="font-size: 18px;">正在报名</span>
				</div>
			</div>
			
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span class="spanfont">组织者</span>
				</div>
				<div class="col-xs-6 col-sm-9 col-md-8 col-lg-9 gongaofont2">
					<span style="font-size: 18px;">曹老板</span>
				</div>
				<div class="col-xs-2 col-sm-1 col-md-1 col-lg-1 gongaofont3">
					<span class="glyphicon glyphicon-earphone"></span>
				</div>
			</div>
			
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span class="spanfont">时间</span>
				</div>
				<div class="col-xs-12 col-sm-10 col-md-9 col-lg-10 gongaofont2">
					<span style="font-size: 18px;">5.8&nbsp;&nbsp;16:00-18:00&nbsp;&nbsp;星期一</span>
				</div>
			</div>
			
			<div class="row divstyle">
				<div class="col-xs-12 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span  class="spanfont">地址</span>
				</div>
				<div class="col-xs-10 col-sm-9 col-md-8 col-lg-9 gongaofont2">
					<span style="font-size: 17px;">浦东曹路体育馆浦东北路123号</span>
				</div>
				<div class="col-xs-2 col-sm-1 col-md-1 col-lg-1 gongaofont3">
					<span class="glyphicon glyphicon-map-marker"></span>
				</div>
			</div>
			
			<div class="row divstyle" style="margin-top: 10px;">
				<div class="col-xs-4 col-sm-6 col-md-3 col-lg-2 gongaofont2">
					<span style="font-size: 16px;">报名信息</span>
				</div>
				<div class="col-xs-8 col-sm-6 col-md-9 col-lg-10 gongaofont3">
					<img class="bodyImage imgstyle" src="resources/img/discoveryactivities.jpg">
					<img class="bodyImage imgstyle" src="resources/img/discoveryactivities.jpg">
					<img class="bodyImage imgstyle" src="resources/img/discoveryactivities.jpg">
					<span style="color: grey;">more</span><span class="glyphicon glyphicon-chevron-right"></span>
				</div>
			</div>
			<!-- 第二部分 -->
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span class="spanfont">类型</span>
				</div>
				<div class="col-xs-8 col-sm-10 col-md-9 col-lg-10 gongaofont2">
					<span style="font-size: 18px;">羽毛球</span>
				</div>
			</div>
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span class="spanfont">费用</span>
				</div>
				<div class="col-xs-8 col-sm-10 col-md-9 col-lg-10 gongaofont2">
					<span style="font-size: 18px;">￥20/人</span>
				</div>
			</div>
			<div class="row divstyle">
				<div class="col-xs-4 col-sm-2 col-md-3 col-lg-2 gongaofont2">
					<span class="spanfont">报名权限</span>
				</div>
				<div class="col-xs-8 col-sm-10 col-md-9 col-lg-10 gongaofont2">
					<span style="font-size: 18px;">非公开（仅限好友）</span>
				</div>
			</div>
			<div class="row" style="background-color: white;">
				<div class="col-xs-12 col-sm-12 col-md-12 gongaofont2">
					<span style="font-size: 16px;">活动备注</span>
				</div>
			</div>
			<div class="row">
				<div class="col-xs-12 col-sm-12 col-md-12" style="background-color: white;height: 150px;">
				手机打电话方便不舒服就好比飞机和速度加快是快捷回复都是靠积分实际发生的看尽繁华
						就是山东矿机发货速度快就放回口袋设计和富士康将大幅回调空间是看店家发货速度加快
						发货速度快就符合当时省的麻烦您深度开发...
				</div>
			</div>
			<div class="row" style="background-color: #ffd200;margin-top: 40px;">
				<div class="col-xs-12 col-sm-12 col-md-12"
				 style="line-height: 50px;text-align: center;">
					<a href="#" style="text-decoration: none;color: black;">
					<span class="glyphicon glyphicon-edit" style="font-size: 16px;">报名参加</span>
					</a>
				</div>
			</div>
		</div>
		<div class="col-xs-1 col-sm-1 col-md-3"></div>
	  </div>
	 </div>
 </body>
</html>