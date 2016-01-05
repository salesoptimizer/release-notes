<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
	${logoPath}
	<div class="result-box">
		<c:if test="${not empty errorMsg}">
			<span class="errorMsg"><c:out value="${errorMsg}" /></span><br/>
		</c:if>
		<c:if test="${not empty gglResult}">
			<b><c:out value="${gglResult}" /></b><br/>
		</c:if>
		<c:if test="${not empty attResult}">
			<b><c:out value="${attResult}" /></b>
		</c:if>
	</div>
<jsp:include page="footer.jsp" />