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
					<li><a href="index">首页</a></li>
					<li><a href="findactivity">发现活动</a></li>
					<li class="active"><a href="community">兴趣圈子</a></li>
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
	
	<div class="bodyContent bodyGray">
		<div class="container">
			<div class="communitytalknav">
				首页&nbsp;>&nbsp;兴趣圈子&nbsp;>&nbsp;论坛
			</div>
			<div class="row communitytalknav2">
				<div class="col-xs-8 col-sm-8 col-md-8">
					<img class="bodyImage logoimg"
					  src="resources/img/chuqulanglogo.png">
				</div>
				<div class="col-xs-4 col-sm-4 col-md-4 gongaofont4">
					<h3>出去浪论坛</h3>
				</div>
			</div>
			<!-- 圈子文化 -->
			<div class="row communitytalknav2" style="background: url(resources/img/culturalbackground.png);height: 50px;">
				<div class="col-xs-8 col-sm-8 col-md-8 gongaofont2">
					圈子文化-趣味爱好兴趣门户-社区/论坛
				</div>
				<div class="col-xs-4 col-sm-4 col-md-4 gongaofont">
					公告：上海MIKI街舞赛事举办啦！2017.08.13
				</div>
			</div>
			<div class="row" style="background-color: white;">
				<div class="col-xs-10 col-sm-10 col-md-10 gongaofont2">
					今日：5488|昨日：1038|帖子：2239412|会员：1037149|欢迎新会员：麻瓜一号
				</div>
				<div class="col-xs-2 col-sm-2 col-md-2 gongaofont3">
					最新回复
				</div>
			</div>
			<!-- 发新帖 -->
			<div class="row communitytalknav2" style="background-color: white;">
				<div class="col-xs-6 col-sm-6 col-md-6 gongaofont2">
						<a href="#" class="">
						<span class="label label-warning sendnew">发新帖</span></a>
				</div>
				<div class="col-xs-6 col-sm-6 col-md-6 gongaofont3">
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew1">返回</span>
					</a>
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew2">上一页</span>
					</a>
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew">1</span>
					</a>
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew2">2</span>
					</a>
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew2">3</span>
					</a>
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew2">4</span>
					</a>
					<a href="#" style="text-decoration: none;">
					<span class="label label-warning sendnew2">下一页</span>
					</a>
				</div>
			</div>
			<!-- 中间部分导航 -->
					
			<div class="row communitytalknav2"  style="background: url(resources/img/pink.png);">
				<div class="col-xs-3 col-sm-3 col-md-3 col-lg-2 gongaofont2">
					查看：451&nbsp;&nbsp;&nbsp;回复：65
				</div>
				<div class="col-xs-5 col-sm-5 col-md-5 col-lg-7 gongaofont2"
				style="font-weight: bold;font-size: 15px;">
					我在世博园这儿看到了miki街舞。
				</div>
				<div class="col-xs-4 col-sm-4 col-md-4 col-lg-3 gongaofont3">
					只看楼主&nbsp;&nbsp;&nbsp;收藏&nbsp;&nbsp;&nbsp;回复
				</div>
			</div>
			<!-- 中间部分开始 -->
			<div class="row" style="background-color: #fffcf1;margin-top: 5px;">
				<div class="col-xs-2 col-sm-2 col-md-2 col-lg-2">
				<!-- 楼主 -->
					<div>
						<table>
							<tr>
								<th><span class="label label-warning sendnew">楼主</span></th>
							</tr>
							<tr>
								<td><p style="width: 100px;height: 100px;border-radius: 50%;border: 1px solid black;
						line-height: 100px;text-align: center">
						这里是照片</p></td>
							</tr>
							<tr>
								<td style="text-align: center;">瓜皮中单</td>
							</tr>
						</table>
						<p  class="pfont">8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8</p>
						<p>&nbsp;圈子&nbsp;
						帖子&nbsp;精华</p>
						<p class="pfont">8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						8</p>
						<p>&nbsp;视屏&nbsp;
						活动</p>
						<p style="background-color: white;border: 1px solid grey;width: 100px;
						text-align: center;"><span style="color: red;">12星</span>最强逗比</p>
						<p>经验值：3245</p>
						<hr>
					</div>
					<div>
						<table>
							<tr>
								<td><p style="width: 100px;height: 100px;border-radius: 50%;border: 1px solid black;
						line-height: 100px;text-align: center">
						这里是照片</p></td>
							</tr>
							<tr>
								<td style="text-align: center;">瓜皮辅助</td>
							</tr>
						</table>
						<p style="margin-left: 15px;color: #ffd200;">8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8</p>
						<p>&nbsp;圈子&nbsp;
						帖子&nbsp;精华</p>
						<p style="margin-left: 15px;color: #ffd200;">8&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						8</p>
						<p>&nbsp;视屏&nbsp;
						活动</p>
						<p style="background-color: white;border: 1px solid grey;width: 100px;
						text-align: center;"><span style="color: red;">45星</span>究极进化</p>
						<p>经验值：32545</p>
						<hr>
					</div>
				</div>
				<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10"
				 style="background-color: white;">
				 <!--  -->
				 <!-- <div class="container"> -->
			<!-- <div class="jumbotron" style="background-color: white;"> -->
				 <p style="margin-top: 20px;">啊哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈</p>
				 <div class="row">
				 	<div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 bordergrey">
				 		<div class="row">
				 			<div class="col-xs-4 col-sm-4 col-md-4 col-lg-4">
				 				<img class="bodyImage" src="resources/img/forumactivities.png">
				 			</div>
				 			<div class="col-xs-8 col-sm-8 col-md-8 col-lg-8">
				 				<div class="row">
				 					<div class="col-xs-9 col-sm-9 col-md-9 col-lg-9">
				 						<h5>来玩桌游大暴走</h5>
				 					</div>
				 					<div class="col-xs-3 col-sm-3 col-md-3 col-lg-3">
				 						<p style="width: 12px;height: 12px;background-color: #60ce5b;border-radius: 50%;
				 						margin-top: 10px;margin-left: 20px;"></p>
				 					</div>
				 				</div>
				 				<div class="row">
				 					<div class="col-xs-12 col-sm-12 col-md-3 col-lg-3">
				 						<p class="pfont2">个人组织</p>
				 					</div>
				 					<div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
				 						<p class="pfont2">2017/04/23发布</p>
				 					</div>
				 					<div class="col-xs-12 col-sm-12 col-md-3 col-lg-3">
				 						<p class="pfont2">进行中</p>
				 					</div>
				 				</div>
				 				<div class="row">
				 					<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				 						<label style="background-color: #989898;width: 45px;height: 20px;
				 						color:white; text-align: center;border-radius: 10px;"
				 						 class="pfont2">
				 						桌游
				 						</label>
				 						<label style="width: 80px;height: 20px;
				 						color:red; text-align: center;border-radius: 10px;
				 						border: 1px solid red;"
				 						 class="pfont2">
				 						报名中3/6人
				 						</label>
				 						<label style="color:red;" class="pfont2">
				 						25元起
				 						</label>
				 					</div>
				 				</div>
				 				<div class="row">
				 					<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				 						<label style="font-size: 12px;">
				 						今天16：00-18：00&nbsp;&nbsp;&nbsp;&nbsp;星期二
				 						</label>
				 					</div>
				 				</div>
				 				<div class="row">
				 					<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
				 						<p class="pfont2"><span class="glyphicon glyphicon-map-marker">
				 						曹路体育馆&nbsp;2.5km</span></p>
				 					</div>
				 				</div>
				 			</div>
				 		</div>
				 	</div>
				 </div>
			<div class="row" style="margin-top: 100px;border-top: 1px dotted grey;">
				<div class="col-xs-3 col-sm-3 col-md-1 gongaofont2">
					举报
				</div>
				<div class="col-xs-6 col-sm-6 col-md-9 gongaofont2"
				style="text-align: right;">
					来自iPhone手机端1楼2017-07-15&nbsp;07：23
				</div>
				<div class="col-xs-3 col-sm-3 col-md-2 gongaofont3">
					说一句
				</div>
			</div>
			<!-- 视屏 -->
				 <div class="row" style="border-top: 1px solid grey;">
				 	<div class="col-xs-12 col-sm-12 col-md-12">
				 		<p class="communitytalknav2">我去参加过上次的了，这是我拍的视屏~</p>
				 		<p style="width: 350px;height: 200px;background-color: #333333;">
				 			<span style="color: white;line-height: 200px;">这是假的，放不了，别被骗了</span>
				 		</p>
				 	</div>
				 </div>
			<div class="row" style="margin-top: 100px;border-top: 1px dotted grey;">
				<div class="col-xs-3 col-sm-3 col-md-1 gongaofont2">
					举报
				</div>
				<div class="col-xs-6 col-sm-6 col-md-9 gongaofont2"
				style="text-align: right;">
					来自iPhone手机端1楼2017-07-15&nbsp;07：23
				</div>
				<div class="col-xs-3 col-sm-3 col-md-2 gongaofont3">
					收起来
				</div>
			</div>
			<!-- 下面的评论 -->
			 <div class="commentdiv commentdiv4">
				<div class="row commentdiv2">
				<div class="col-xs-7 col-sm-7 col-md-7 gongaofont2">
				 	<img class="bodyImage" src="resources/img/loginfind.jpg"
				 	style="width: 50px;height: 50px;border-radius: 50%;
				 	margin-left: 20px;">
				 	我去参加过上次的了，这是我拍的视屏~
				</div>
				<div class="col-xs-5 col-sm-5 col-md-5 gongaofont3">
					<span  class="commentdiv4">2017-07-15 07：23&nbsp;回复</span>
				</div>
				</div>
				
				<div class="row commentdiv2">
				<div class="col-xs-7 col-sm-7 col-md-7 gongaofont2">
				 	<img class="bodyImage" src="resources/img/loginfind.jpg"
				 	style="width: 50px;height: 50px;border-radius: 50%;
				 	margin-left: 20px;">
				 	我去参加过上次的了，这是我拍的视屏~
				</div>
				<div class="col-xs-5 col-sm-5 col-md-5 gongaofont3">
					<span  class="commentdiv4">2017-07-15 07：23&nbsp;回复</span>
				</div>
				</div>
				
				<div class="row commentdiv2">
				<div class="col-xs-7 col-sm-7 col-md-7 gongaofont2">
				 	<img class="bodyImage" src="resources/img/loginfind.jpg"
				 	style="width: 50px;height: 50px;border-radius: 50%;
				 	margin-left: 20px;">
				 	我去参加过上次的了，这是我拍的视屏~
				</div>
				<div class="col-xs-5 col-sm-5 col-md-5 gongaofont3">
					<span  class="commentdiv4">2017-07-15 07：23&nbsp;回复</span>
				</div>
				</div>
				<div class="row commentdiv2">
				<div class="col-xs-7 col-sm-7 col-md-7 gongaofont2">
				 	<img class="bodyImage" src="resources/img/loginfind.jpg"
				 	style="width: 50px;height: 50px;border-radius: 50%;
				 	margin-left: 20px;">
				 	我去参加过上次的了，这是我拍的视屏~
				</div>
				<div class="col-xs-5 col-sm-5 col-md-5 gongaofont3">
					<span  class="commentdiv4">2017-07-15 07：23&nbsp;回复</span>
				</div>
				</div>
				<div class="row commentdiv2">
				<div class="col-xs-7 col-sm-7 col-md-7 gongaofont2">
				 	<img class="bodyImage" src="resources/img/loginfind.jpg"
				 	style="width: 50px;height: 50px;border-radius: 50%;
				 	margin-left: 20px;">
				 	我去参加过上次的了，这是我拍的视屏~
				</div>
				<div class="col-xs-5 col-sm-5 col-md-5 gongaofont3">
					<span class="commentdiv4">2017-07-15 07：23&nbsp;回复</span>
				</div>
				</div>
				
				<div class="row">
				<div class="col-xs-3 col-sm-3 col-md-9 gongaofont2">
					<span style="color: grey;" class="commentdiv3">还有14条回复</span>
					点击查看
				</div>
				<div class="col-xs-3 col-sm-3 col-md-3 gongaofont3">
					<A href="#"><span class="commentdiv4">说一句</span></A>
				</div>
			</div>
				
			</div>
			
				 
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