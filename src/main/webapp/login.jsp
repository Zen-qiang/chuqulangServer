<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<h2>Login：</h2>
	<form action="user/login" method="post">
		<input type="hidden" value="username" name="type"> username：<input
			type="text" name="phoneno" value="18270790997" /><br> password：<input
			type="text" name="password" value="admin" /><br>
		<button type="submit">登录</button>
	</form>
</body>
</html>