<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<title>Login</title>
<jsp:include page="header.jsp"></jsp:include>

<h1>Login</h1>
<form action="login" method="post">

	<table>
		<tr>
			<td>Enter email</td>
			<td><input type="email" name="email" required></td>
		</tr>
		<tr>
			<td>Enter password</td>
			<td><input type="password" name="password" required></td>
		</tr>
	</table>
	<input type="submit" value="Login"><br> Don't have an
	account?
	<c:url var="URL" value="register">
		<c:param name="param" value="${parameter}" />
	</c:url>
	<a href="<c:out value="${URL}"/>">Register here</a>
</form>
</body>
</html>