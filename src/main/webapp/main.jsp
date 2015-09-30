<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
	<c:out value="${gglResult}" /><br/>
	<c:out value="${attResult}" />
<jsp:include page="footer.jsp" />