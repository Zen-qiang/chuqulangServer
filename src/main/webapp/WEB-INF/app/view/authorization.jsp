<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>正在跳转...</title>
<script type="text/javascript" src="../resources/jquery-3.2.1.js"></script>

<script type="text/javascript">
	$(function() {
		var code = '<%=request.getParameter("code")%>';
		var state = '<%=request.getParameter("state")%>';
		var redirectUrl = '<%=request.getParameter("redirectUrl")%>';
		console.log(code);
		console.log(redirectUrl);
		$.ajax({
			url : 'getAccessToken',
			data : {
				code : code,
				state : state
			},
			success : function(response, status, xhr) {
				console.log(response.data.openId);
				window.location.href = redirectUrl + "?openId=" + response.data.openId;
			},
			dataType : 'json'
		});
	});
</script>
</head>
<body>

</body>
</html>