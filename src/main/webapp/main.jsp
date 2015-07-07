<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
	<c:url var="post_url"  value="/doc" />
	<form action="${post_url}">
		<label for="projectList">Select a project</label>
		<select name="projectList">
			<c:forEach var="i">
				<option id="project_${i}" value="${requestScope.projects[i]}>${requestScope.projects[i]}</option>	
			</c:forEach>
		</select>
		<p>Write versions range:</p>
		<label for="minVer">Min version</label>
		<input type="text" name="minVer" />
		<label for="maxVer">Max version</label>
		<input type="text" name="maxVer" />
		<input type="submit" value="Get Release Notes" />
	</form>
	
	<c:out value="${requestScope.accounts}" />
	<%-- <c:out value="${requestScope.projects}" /> --%>
	
	<a href="/doc">Create test google doc</a>
<jsp:include page="footer.jsp" />