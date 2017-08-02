<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">

<title>上海鼎莲软件科技有限公司</title>

<link href="static/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<link href="static/bootstrap/css/bootstrap-theme.min.css" rel="stylesheet">
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
				<a href="#"><img src="resources/img/logo.svg" class="navbar-brand"></a>
			</div>
			<div id="navbar" class="collapse navbar-collapse navbar-right">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">首页</a></li>
					<li><a href="#about">发现活动</a></li>
					<li><a href="#contact">兴趣圈子</a></li>
					<li><a href="#contact">常见问题</a></li>
				</ul>
				<form class="navbar-form navbar-right">
					<input type="text" class="form-control" placeholder="请输入关键字">
					<span class="glyphicon glyphicon glyphicon-user"
						style="margin-left: 20px" aria-hidden="true"></span>
				</form>
			</div>
		</div>
	</nav>

	<div id="myCarousel" class="carousel slide" data-ride="carousel">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
			<!-- <li data-target="#myCarousel" data-slide-to="1"></li>
			<li data-target="#myCarousel" data-slide-to="2"></li> -->
		</ol>
		<div class="carousel-inner" role="listbox">
			<div class="item active">
				<img class="first-slide" src="resources/img/首页_02.jpg" alt="First slide">
				<!-- <div class="container hidden-xs">
					<div class="carousel-caption">
						<button class="btn btn-lg download-btn btn-disable pull-left">敬请期待</button>
					</div>
				</div> -->
				<button class="btn btn-lg download-btn btn-disable dinglian-button">敬请期待</button>
			</div>
			<!-- <div class="item">
				<img class="second-slide" src="resources/img/首页_02.jpg" alt="Second slide">
				<div class="container hidden-xs">
					<div class="carousel-caption">
						<button class="btn btn-lg download-btn pull-left">敬请期待</button>
					</div>
				</div>
			</div>
			<div class="item">
				<img class="third-slide" src="resources/img/首页_02.jpg" alt="Third slide">
				<div class="container hidden-xs">
					<div class="carousel-caption">
						<button class="btn btn-lg download-btn pull-left">敬请期待</button>
					</div>
				</div>
			</div> -->
		</div>
		<a class="left carousel-control" href="#myCarousel" role="button"
			data-slide="prev"> <span class="sr-only">Previous</span>
		</a> <a class="right carousel-control" href="#myCarousel" role="button"
			data-slide="next"> <span class="sr-only">Next</span>
		</a>
	</div>

	<div class="bodyContent">
		<div class="container">
			<div class="content-text-area">
				<h1 class="text-muted">和朋友</h1>
				<h1 class="text-muted">找到附近玩乐</h1>
				<div class="line"></div>
				<h3 class="text-muted">找玩伴，邀好友，让我们踹开枯燥生活</h3>
			</div>
			<div class="row">
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="resources/img/首页_06.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="resources/img/首页_08.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="resources/img/首页_10.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="resources/img/首页_12.png">
				</div>
			</div>
		</div>
	</div>

	<div class="bodyContent bodyGray">
		<div class="container">
			<div class="content-text-area">
				<h1 class="text-muted">兴趣圈</h1>
				<h1 class="text-muted">拉近你我距离</h1>
				<div class="line"></div>
				<h3 class="text-muted">聊兴趣，秀动态，让我们与热爱聊起来</h3>
			</div>
			<div class="row">
				<div class="col-xs-12 col-md-6 imageColumn">
					<img class="bodyImage" src="resources/img/首页_18.png">
				</div>
				<div class="col-xs-12 col-md-6 imageColumn">
					<img class="bodyImage" src="resources/img/首页_24.png">
				</div>
			</div>
		</div>
	</div>

    <footer class="main-footer">
        <div class="container">
            <div class="row">
            	<div class="col-sm-3 widget hidden-xs hidden-sm">
					<img class="img-responsive center-block" src="resources/img/logo.svg">
	            </div>
                <div class="col-sm-2 widget">
					<h4 class="title">关于我们</h4>
					<div class="tag-cloud ">
						<ul class="list-unstyled">
							<li><a href="#" title="网站介绍" target="_blank">网站介绍</a></li>
							<li><a href="#" title="网站介绍" target="_blank">联系我们</a></li>
							<li><a href="#" title="网站介绍" target="_blank">网站合作</a></li>
						</ul>
                     </div>
	            </div>
	            <div class="col-sm-2 widget">
					<h4 class="title">精品活动</h4>
					<div class="tag-cloud ">
						<ul class="list-unstyled">
							<li><a href="#" title="网站介绍" target="_blank">街舞活动</a></li>
							<li><a href="#" title="网站介绍" target="_blank">热门桌游</a></li>
							<li><a href="#" title="网站介绍" target="_blank">新玩法</a></li>
						</ul>
                     </div>
	            </div>
	            <div class="col-sm-2 widget">
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
					<img class="img-responsive" src="resources/img/logo.svg" style="width:50%">
	            </div>
	            <div class="col-sm-2 widget">
					<img style="width: 100px; height: 100px" class="img-responsive" src="resources/img/code.jpg">
	            </div>
	        </div>
	        <div class="row">
				<div class="col-sm-12" style="text-align: center;">
					<span>Copyright &trade; <a href="http://www.dingliantech.com">鼎莲科技</a></span>
					| <span><a href="http://www.miibeian.gov.cn/" target="_blank">沪ICP备17027166号-1</a></span>
				</div>
			</div>
        </div>
    </footer>
</body>
</html>