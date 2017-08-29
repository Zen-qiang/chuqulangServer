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
<link href="css/activity.css" rel="stylesheet">
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
	<script type="text/javascript">
		$(function() {
			var ulstring = "<ul style='width:130px;top: 50px;z-index=99'>"
					+ "<a style='margin-left:-20px;color:grey;text-decoration: none;'><span class='glyphicon glyphicon-user'></span>&nbsp;&nbsp;会员中心</a><br>"
					+ "<hr style='width:100%;border-top:1px solid #D5D5D5; margin-top: 2px;margin-left:-20px;'>"
					+ "<a style='margin-left:-20px;color:grey;text-decoration: none;'><span class='glyphicon glyphicon-cloud-upload'></span>&nbsp;&nbsp;发布活动</a><br>"
					+ "<hr style='width:100%;border-top:1px solid #D5D5D5; margin-top: 2px;margin-left:-20px;'>"
					+ "<a style='margin-left:-20px;color:grey;text-decoration: none;'><span class='glyphicon glyphicon-edit'></span>&nbsp;&nbsp;发布话题</a><br>"
					+ "<hr style='width:100%;border-top:1px solid #D5D5D5; margin-top: 2px;margin-left:-20px;'>"
					+ "<a style='margin-left:-20px;color:grey;text-decoration: none;'><span class='glyphicon glyphicon-cloud'></span>&nbsp;&nbsp;消息</a><br>"
					+ "<hr style='width:100%;border-top:1px solid #D5D5D5; margin-top: 2px;margin-left:-20px;'>"
					+ "<a style='margin-left:-20px;color:grey;text-decoration: none;'><span class='glyphicon glyphicon-cog'></span>&nbsp;&nbsp;设置</a><br>"
					+ "<hr style='width:100%;border-top:1px solid #D5D5D5; margin-top: 2px;margin-left:-20px;'>"
					+ "</ul>";
			$("#topdaohang")
					.popover(
							{
								trigger : 'manual',  
								placement : 'bottom',
								html : 'true',
								content : ulstring,
								animation : false
							}).on(
							"mouseenter",
							function() {
								var _this = this;
								$(this).popover("show");
								$(this).siblings(".popover").on("mouseleave",
										function() {
											$(_this).popover('hide');
										});
							}).on("mouseleave", function() {
						var _this = this;
						setTimeout(function() {
							if (!$(".popover:hover").length) {
								$(_this).popover("hide")
							}
						}, 100);
					});
		});
	</script>
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
					<A data-container="body">
					<span id="topdaohang" class="glyphicon glyphicon glyphicon-user"
						style="color: white;" aria-hidden="true"></span>
					</A>
				</form>
			</div>
		</div>
	</nav>
	<!-- 滚动图片 -->
	<div id="myCarousel" class="carousel slide" data-ride="carousel">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
			<li data-target="#myCarousel" data-slide-to="1"></li>
			<li data-target="#myCarousel" data-slide-to="2"></li>
		</ol>
		<div class="carousel-inner" role="listbox">
			<div class="item active">
				<img class="first-slide" src="resources/img/loginfind.jpg" alt="First slide">
				<div class="container hidden-xs">
					<div class="carousel-caption">
						<button class="btn btn-lg download-btn pull-left">发布活动</button>
					</div>
				</div>
			</div>
			<div class="item">
				<img class="second-slide" src="resources/img/loginfind.jpg" alt="Second slide">
				<div class="container hidden-xs">
					<div class="carousel-caption">
						<button class="btn btn-lg download-btn pull-left">发布活动</button>
					</div>
				</div>
			</div>
			<div class="item">
				<img class="third-slide" src="resources/img/loginfind.jpg" alt="Third slide">
				<div class="container hidden-xs">
					<div class="carousel-caption">
						<button class="btn btn-lg download-btn pull-left">发布活动</button>
					</div>
				</div>
			</div>
		</div>
		<a class="left carousel-control" href="#myCarousel" role="button"
			data-slide="prev"> <span class="sr-only">Previous</span>
		</a> <a class="right carousel-control" href="#myCarousel" role="button"
			data-slide="next"> <span class="sr-only">Next</span>
		</a>
	</div>
	
	
	
	<!-- 中间的标签部分 -->
	<div class="bodyContent bodyGray">
		<div class="container">
			<div class="jumbotron" style="background-color: white;">
			<div class="row">
				<div class="widget hidden-md hidden-sm hidden-xs">
						<ul class="list-inline li a">
							<li><a href="#">精选街舞</a></li>
							<li><a href="#">全部</a></li>	
							<li><a href="#">popping</a></li>
							<li><a href="#">locking</a></li>
							<li><a href="#">popping</a></li>
							<li><a href="#">breaking</a></li>
							<li><a href="#">jazz</a></li>
							<li><a href="#">wacking</a></li>
							<li><a href="#">reggae</a></li>
							<li><a href="#">其它</a></li>
						</ul>
				</div>
				</div>
				<div class="row">
				<div class="widget hidden-md hidden-sm hidden-xs">
						<ul class="list-inline li a">
							<li><a href="#">热门桌游</a></li>
							<li><a href="#">全部</a></li>	
							<li><a href="#">狼人杀</a></li>
							<li><a href="#">德州扑克</a></li>
							<li><a href="#">三国杀</a></li>
							<li><a href="#">大富翁</a></li>
							<li><a href="#">UNO</a></li>
							<li><a href="#">万智牌</a></li>
							<li><a href="#">其它</a></li>
						</ul>
				</div>
				</div>
				<div class="row">
				<div class="widget hidden-md hidden-sm hidden-xs">
						<ul class="list-inline li a">
							<li><a href="#">其他乐趣</a></li>
							<li><a href="#">全部</a></li>	
							<li><a href="#">看电影</a></li>
							<li><a href="#">cospaly</a></li>
							<li><a href="#">新玩法</a></li>
							<li><a href="#">其它</a></li>
						</ul>
				</div>
				
				<div class="row widget hidden-lg" style="background-color: grey;line-height: 50px;
				margin-top: -30px;">
					<div class="col-xs-10 col-sm-11 col-md-11">
						分类
					</div>
					<div class="col-xs-2 col-sm-1 col-md-1">
						<span onclick="fenlei()" class="glyphicon glyphicon-list glyphicon "></span>
					</div>
				</div>
				<!-- 隐藏 列表 -->
				<div id="hiddenclassify" class="row widget hidden-lg" style="background-color: #eee;display: none;
				height: 250px;margin-top: -34px;">
						<div class="col-xs-11 col-sm-12	col-md-12">
							<h4>精选街舞</h4>
						</div>
						<div class="col-xs-2 col-sm-2 col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">全部</A>
						</div>
						<div class="col-xs-2 col-sm-2 col-md-1" style="margin-left: -15px;">
							<A href="#" style="text-decoration: none;color: grey;">popping</A>
						</div>
						<div class="col-xs-2 col-sm-2 col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">locking</A>
						</div>
						<div class="col-xs-2 col-sm-2 col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">popping</A>
						</div>
						<div class="col-xs-2 col-sm-2 col-md-1"><A href="#" style="text-decoration: none;color: grey;">breaking</A></div>
						<div class="col-xs-2 col-sm-2 col-md-1"><A href="#" style="text-decoration: none;color: grey;">jazz</A></div>
						<div class="col-xs-2 col-sm-2 col-md-1"><A href="#" style="text-decoration: none;color: grey;">wacking</A></div>
						<div class="col-xs-2 col-sm-2 col-md-1"><A href="#" style="text-decoration: none;color: grey;">reggae</A></div>
						<div class="col-xs-2 col-sm-2 col-md-1"><A href="#" style="text-decoration: none;color: grey;">其它</A></div>
						<div class="col-xs-12 col-sm-12	col-md-12"
						style="border-top: 2px solid white;margin-top: 4px;">
							<h4>热门桌游</h4>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">全部</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1"
						 style="margin-left: -15px;">
							<A href="#" style="text-decoration: none;color: grey;">狼人杀</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">Holdem</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">三国杀</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">大富翁</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">UNO</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">万智牌</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">其它</A>
						</div>
						<div class="col-xs-12 col-sm-12	col-md-12"
						style="border-top: 2px solid white;margin-top: 4px;">
							<h4>其他乐趣</h4>
						</div>
						<div class="col-xs-2 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">全部</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">看电影</A>
						</div>
						<div class="col-xs-2 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">cosplay</A>
						</div>
						<div class="col-xs-3 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">新玩法</A>
						</div>
						<div class="col-xs-2 col-sm-2	col-md-1">
							<A href="#" style="text-decoration: none;color: grey;">其它</A>
						</div>
				</div>
				<script type="text/javascript">
					function fenlei() {
						$("#hiddenclassify").stop(true, false).slideToggle();
					}
				</script>
				<div class="row">
				<div class="col-xs-8 col-sm-8	col-md-7 col-lg-8">
				</div>
				<div class="col-xs-12 col-sm-12	col-md-5 col-lg-4"
				style="margin-top: -10px;">
					<div class="row">
						<div class="col-xs-4 col-md-4">
							<a href="#"  style="text-decoration: none;color: grey;" class="glyphicon glyphicon-th-large">默认排序</a>
						</div>
						<div class="col-xs-4 col-md-4">
							<a href="#"  style="text-decoration: none;color: grey;" class="glyphicon glyphicon-time">时间排序</a>
						</div>
						<div class="col-xs-4 col-md-4">
							<a href="#"  style="text-decoration: none;color: grey;" class="glyphicon glyphicon-tasks">活动状态</a>
						</div>
					</div>
				</div>
				</div>
				<hr>
		<div class="row placeholders activitymargin">
          <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="activitydetails"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
            <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
          </div>
          
          		<div class="row placeholders activitymargin">
          <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
            <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
          </div>
          
          		<div class="row placeholders activitymargin">
          <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
            <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
          </div>
          
          		<div class="row placeholders activitymargin">
          <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
           <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
            <div class="col-xs-12 col-sm-6 col-md-3 col-lg-3 placeholder activitymargin2">
            <a href="loginactivity"><img class="bodyImage" src="resources/img/activitylist.png"></a>
              <h4>世博园kaboy街舞对战</h4>
              <span class="text-muted">
				<a href="loginactivity" class="glyphicon glyphicon-map-marker"
				style="text-decoration: none;color: grey;">徐家汇体育馆2.5km</a>
			  </span>
			  <!-- 价格 -->
			  <div class="row">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div class="fontlineheight" style="width: 30px;height: 30px;background-color: black;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-4 col-lg-4 placeholder fontlineheight">
			  		Kevin
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-5 placeholder fontred">
			  		25元起
			  	</div>
			  </div>
			  <!-- 人数 -->
			  <div class="row fontlineheight2">
			  	<div class="col-xs-1 col-sm-1 col-md-2 col-lg-2 placeholder">
			  		<div style="width: 22px;height: 22px;background-color: green;border-radius: 50%;"></div>
			  	</div>
			  	<div class="col-sm-5 col-xs-5 col-md-5 col-lg-4 placeholder">
			  		好友参加
			  	</div>
			  	<div class="col-xs-5 col-sm-5 col-md-12 col-lg-6 placeholder">
			  		<span class="glyphicon glyphicon-user"></span>报名中6/8人
			  	</div>
			  </div>
            </div>
          </div>
          <!-- 分页 -->
				<div class="row">
					<div class="col-xs-2 col-md-4 col-sm-3 col-lg-4"></div>
					<div class="col-xs-8 col-md-4  col-sm-6 col-lg-4" style="margin-top: 15px;">
							<ul class="pagination">
								<li><a href="#" style="color: black;background-color: orange;margin-left: 15px;">1</a></li>
								<li><a href="#" style="color: white;background-color: #e1dfdf;margin-left: 15px;">2</a></li>
								<li><a href="#" style="color: white;background-color: #e1dfdf;margin-left: 15px;">3</a></li>
								<li><a href="#" style="color: white;background-color: #e1dfdf;margin-left: 15px;">4</a></li>
							</ul>
					</div>
					<div class="col-xs-2 col-md-4  col-sm-3 col-lg-4"></div>
				</div>
			</div>
		</div>
	</div>
	</div>

	<!-- 脚部 -->
	<footer class="main-footer">
        <div class="container">
            <div class="row">
            	<div class="col-xs-12 col-sm-12	col-md-3 col-lg-3 widget hidden-xs hidden-sm">
					<img style="width: 150px;height: 80px;margin-top: -19px;" src="resources/img/logo.svg">
	            </div>
                <div class="col-xs-12 col-sm-12	col-md-2 col-lg-2 widget">
					<h4 class="title">关于我们</h4>
					<div class="tag-cloud ">
						<ul class="list-unstyled">
							<li><a href="#" title="网站介绍" target="_blank">网站介绍</a></li>
							<li><a href="#" title="网站介绍" target="_blank">联系我们</a></li>
							<li><a href="#" title="网站介绍" target="_blank">网站合作</a></li>
						</ul>
                     </div>
	            </div>
	            <div class="col-xs-12 col-sm-12	col-md-2 col-lg-2 widget">
					<h4 class="title">精品活动</h4>
					<div class="tag-cloud ">
						<ul class="list-unstyled">
							<li><a href="#" title="网站介绍" target="_blank">街舞活动</a></li>
							<li><a href="#" title="网站介绍" target="_blank">热门桌游</a></li>
							<li><a href="#" title="网站介绍" target="_blank">新玩法</a></li>
						</ul>
                     </div>
	            </div>
	            <div class="col-xs-12 col-sm-12	col-md-2 col-lg-2 widget">
					<h4 class="title">快捷导航</h4>
					<div class="tag-cloud ">
						<ul class="list-unstyled">
							<li><a href="#" title="网站介绍" target="_blank">发现活动</a></li>
							<li><a href="#" title="网站介绍" target="_blank">兴趣圈子</a></li>
							<li><a href="#" title="网站介绍" target="_blank">其他内容</a></li>
						</ul>
                     </div>
	            </div>
	            <div class="col-sm-2 widget hidden-md hidden-lg">
					<img class="img-responsive" src="resources/img/logo.svg" style="width:50%;">
	            </div>
	            <div class="col-sm-2 col-xs-12 col-md-2 col-lg-3">
					<img style="width: 90px; height: 90px;margin-bottom: 8px;margin-top: -2px;" class="img-responsive" src="resources/img/code.jpg">
	            </div>
	            <div class="col-sm-8 col-xs-12 col-md-3">
					联系我们：021-68821662<br>邮箱：service@dingliantech.com
	            </div>
	        </div>
	        <div class="row">
				<div class="col-sm-12" style="text-align: center;">
					<span>Copyright © <a href="http://www.dingliantech.com">鼎莲科技</a></span>
					| <span><a href="http://www.miibeian.gov.cn/" target="_blank">沪ICP备17027166号-1</a></span>
				</div>
			</div>
        </div>
    </footer>
</body>
</html>