<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>上海鼎莲软件科技有限公司</title>

<link href="static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="css/index.css" rel="stylesheet">
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
				<a href="#"><img src="resources/img/logo.svg"
					class="navbar-brand"></a>
			</div>
			<div id="navbar" class="collapse navbar-collapse navbar-right">
				<ul class="nav navbar-nav">
					<li><a href="index">首页</a></li>
					<li><a href="findactivity">发现活动</a></li>
					<li class="active"><a href="#">兴趣圈子</a></li>
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

	<div class="bodyContent">
		<div class="container">
			<div class="content-text-area">
				<hr class="hr-short">
				<span style="font-size: 48px;color: grey;font-weight:lighter;">在这里</span><br>
				<span style="font-size: 48px;color: grey;font-weight: lighter;">我们能找到彼此的认同</span><br>
				<h4 class="text-muted">沉默划过天空，漫无目的街道尽头，为兴趣而活的你我，终将在此相遇。</h4>
				<hr>
			</div>
		</div>
	</div>

	<!-- 中间部分 -->
	<div class="bodyContent bodyGray">
		<div class="container">
			<div class="row" style="background-color: white;padding:10px 15px">
				<div class="col-xs-12 col-md-6 col-sm-12 col-lg-6 imageColumn">
					<div class="row">
						<div class="col-xs-6 col-md-6 col-sm-6 col-lg-6 imageColumn">
							<img class="bodyImage" src="resources/img/兴趣圈子1_03.jpg">
						<div class="picturefont">
						<A href="#" style="text-decoration: none;color: white;"><span>#街舞</span></A>
						</div>
						</div>
						<div class="col-xs-6 col-md-6 imageColumn">
							<img class="bodyImage" src="resources/img/兴趣圈子1_05.jpg">
							<div class="picturefont">
								<A href="#" style="text-decoration: none; color: white;"><span>#桌游</span></A>
							</div>
						</div>
						<div class="col-xs-12 col-md-12 col-sm-12 col-lg-12 imageColumn"
						 style="margin-top: -30px;">
							<img class="bodyImage" src="resources/img/兴趣圈子1_09.jpg">
							<div class="picturefont">
								<A href="#" style="text-decoration: none; color: white;"><span>#其它</span></A>
							</div>
						</div>
					</div>
					
				</div>
				<div class="col-xs-12 widget col-md-6 imageColumn" style="padding:20px;">
					<div>
						<h2 class="top-list-title">Top全站排行榜</h2>
						<a href="communitytalk" class="top-list-item"><span class="top-list-label top-list-label-default">1</span>美国街舞鬼才MAKI首<span class="pull-right">&gt;</span></a>
						<a href="#" class="top-list-item"><span class="top-list-label top-list-label-default">2</span>我发现的狼人杀新套路，很脏~<span class="pull-right">&gt;</span></a>
						<a href="#" class="top-list-item"><span class="top-list-label top-list-label-default">3</span>街舞世博园训练基地昨天全是美女<span class="pull-right">&gt;</span></a>
						<a href="#" class="top-list-item"><span class="top-list-label top-list-label-gray">4</span>这种国外的游戏规则666啊<span class="pull-right">&gt;</span></a>
						<a href="#" class="top-list-item"><span class="top-list-label top-list-label-gray">5</span>牛人自制桌游卡牌<span class="pull-right">&gt;</span></a>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="carousel-inner" role="listbox" style="background: url(resources/img/兴趣圈子1_11.jpg);height: 560px;">
		<div class="container" style="text-align: center;color: white;">
			<div class="row">
				<div class="col-lg-3"></div>
				<div class="col-xs-12 col-md-12 col-sm-12 col-lg-6" style="height: 560px;margin-top: 150px;">
					<h3 style="margin-bottom: 50px;">看看我属于哪个圈子吧</h3>
					<ul class="list-inline community">
						<li><a>locking</a></li>
						<li><a>locking</a></li>
						<li><a>locking</a></li>
						<li><a>locking</a></li>
					</ul>
					<ul class="list-inline community">
						<li><a>locking</a></li>
						<li><a>locking</a></li>
						<li><a>locking</a></li>
						<li><a>locking</a></li>
						<li><a>locking</a></li>
					</ul>
					<ul class="list-inline community">
						<li><a>locking</a></li>
						<li><a>locking</a></li>
						<li><a>locking</a></li>
					</ul>
				</div>
				<div class="col-lg-3"></div>
			</div>
		</div>	
	</div>

	<!-- 随便看看 -->
	<div class="bodyContent">
		<div class="container">
			<div class="content-text-area">
				<h2 class="text-muted">随便看看吧~</h2>
			</div>
			<div class="row">
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
			</div>
			<div class="row">
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
				<div class="col-xs-6 col-md-3 col-sm-6 col-lg-3 imageColumn">
					<img class="bodyImage" src="resources/img/兴趣圈子8.png">
				</div>
			</div>
		  <div class="row">
		  	<div class="col-xs-4 col-sm-4 col-md-4">
		  	</div>
		  	<div class="col-xs-4 col-sm-4 col-md-4 footbut"
		  	style="background-color: #ffd200;">
		  		<a href="#" style="text-decoration: none;color: black;">
		  		<span style="font-size: 16px;">换一批</span>
				</a>
		  	</div>
		  	<div class="col-xs-4 col-sm-4 col-md-4">
		  	</div>
		  </div>
		</div>
	</div>

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