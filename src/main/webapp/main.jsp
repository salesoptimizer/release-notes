<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<jsp:include page="header.jsp" />
	<c:if test="${requestScope.tickets == null}">
		<form action="/" method="post">
			<label for="projectList">Select a project</label>
			<select name="projectId">
				<c:forEach var="project" items="${requestScope.projects}">
					<option value="<c:out value="${project.key}" />"><c:out value="${project.value}" /></option>
				</c:forEach>	
			</select><br/>
			<p>Write versions range:</p>
			<label for="minVer">Min version</label>
			<input type="text" name="minVer" /><br/>
			<label for="maxVer">Max version</label>
			<input type="text" name="maxVer" /><br/>
			<input type="submit" value="Get tickets" />
		</form>
	</c:if>
	
	<%-- <c:if test="${requestScope.tickets != null}">
		<form action="/tickets/" method="post">
			<p>Selected project: <c:out value="${requestScope.projectName}" /></p>
			<p>Versions range: <c:out value="${requestScope.versionRange}" /></p>
			<c:forEach var="ticket" items="${requestScope.tickets}">
				Name : <c:out value="${ticket.value[0]}" /><br/>
				Version : <c:out value="${ticket.value[1]}" /><br/>
				Release Notes : <c:out value="${ticket.value[2]}" />
			</c:forEach><br/>
			<input type="submit" value="Get release notes" />
		</form>
	</c:if> --%>
	
<%-- 	<c:out value="${requestScope.accounts}" />
	<c:out value="${requestScope.projects}" /> --%>
	<c:if test="${requestScope.tickets != null}">
		<a href="/doc">Create test google doc</a>
	</c:if>
<jsp:include page="footer.jsp" />