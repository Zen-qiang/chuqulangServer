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
				<a href="#"><img src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/logo.svg" class="navbar-brand"></a>
			</div>
			<div id="navbar" class="collapse navbar-collapse navbar-right">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">首页</a></li>
					<li><a href="findactivity">发现活动</a></li>
					<li><a href="community">兴趣圈子</a></li>
					<li><a href="#contact">常见问题</a></li>
				</ul>
				<form class="navbar-form navbar-right">
					<input type="text" class="form-control" placeholder="请输入关键字">
					<span class="glyphicon glyphicon glyphicon-user"
						style="color: white;" aria-hidden="true"></span>
				</form>
			</div>
		</div>
	</nav>

	<div id="myCarousel" class="carousel slide" data-ride="carousel">
		<!-- Indicators -->
		<ol class="carousel-indicators">
			<li data-target="#myCarousel" data-slide-to="0" class="active"></li>
		</ol>
		<div class="carousel-inner" role="listbox">
			<div class="item active">
				<img class="first-slide" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_02.jpg" alt="First slide">
				<button class="btn btn-lg dinglian-btn btn-disable dinglian-button">敬请期待</button>
			</div>
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
				<span style="font-size: 48px;color: grey;font-weight: 100;">和朋友</span>
				<br>
				<span style="font-size: 48px;color: grey;font-weight: 100;">找到附近玩乐</span><br>
				<div class="line"></div>
				<h3 class="text-muted">找玩伴，邀好友，让我们踹开枯燥生活</h3>
			</div>
			<div class="row">
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_06.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_08.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_10.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_12.png">
				</div>
			</div>
		</div>
	</div>

	<div class="bodyContent bodyGray">
		<div class="container">
			<div class="content-text-area">
				<span style="font-size: 48px;color: grey;font-weight: lighter;">兴趣圈</span>
				<br>
				<span style="font-size: 48px;color: grey;font-weight: lighter;">拉近你我距离</span><br>
				<div class="line"></div>
				<h3 class="text-muted">聊兴趣，秀动态，让我们与热爱聊起来</h3>
			</div>
			<div class="row">
				<div class="col-xs-12 col-md-6 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_18.png">
				</div>
				<div class="col-xs-12 col-md-6 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_19.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_21.png">
				</div>
				<div class="col-xs-6 col-md-3 imageColumn">
					<img class="bodyImage" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/home_23.png">
				</div>
			</div>
		</div>
	</div>

	<footer class="main-footer">
        <div class="container">
            <div class="row">
            	<div class="col-xs-12 col-sm-12	col-md-3 col-lg-3 widget hidden-xs hidden-sm">
					<img style="width: 150px;height: 80px;margin-top: -19px;" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/logo.svg">
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
					<img class="img-responsive" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/logo.svg" style="width:50%;">
	            </div>
	            <div class="col-sm-2 col-xs-12 col-md-2 col-lg-3">
					<img style="width: 90px; height: 90px;margin-bottom: 8px;margin-top: -2px;" class="img-responsive" src="http://langlang2go.oss-cn-shanghai.aliyuncs.com/website/code.jpg">
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