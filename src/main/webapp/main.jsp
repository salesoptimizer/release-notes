<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
	<c:if test="${requestScope.tickets == null}">
		<form action="/" method="post" style="width: 600px; margin: 50px 0 0 100px;">
			<label for="projectList">Select a project: </label>
			<select name="projectId" style="width: 150px;">
				<c:forEach var="project" items="${requestScope.projects}">
					<option value="<c:out value="${project.key}" />"><c:out value="${project.value}" /></option>
				</c:forEach>	
			</select>
			<p>Write versions range:</p>
			<label for="minVer">Min version: </label>
			<input type="text" name="minVer" style="width: 150px; margin-bottom: 10px;" /><br/>
			<label for="maxVer">Max version: </label>
			<input type="text" name="maxVer" style="width: 150px; margin-bottom: 10px;" /><br/>
			<input type="submit" value="Get tickets" />
		</form>
	</c:if>
	
	<c:if test="${requestScope.tickets != null}">
		<a href="/doc">Create test google doc</a>
	</c:if>
<jsp:include page="footer.jsp" />