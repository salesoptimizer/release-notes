<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
	<div class="result-box">
		<b><c:out value="${gglResult}" /></b><br/>
		<b><c:out value="${attResult}" /></b>
	</div>
<jsp:include page="footer.jsp" />