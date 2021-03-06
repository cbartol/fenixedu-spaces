<%--

    Copyright © 2014 Instituto Superior Técnico

    This file is part of FenixEdu Spaces.

    FenixEdu Spaces is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FenixEdu Spaces is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with FenixEdu Spaces.  If not, see <http://www.gnu.org/licenses/>.

--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<spring:url var="searchUrl" value="/spaces/search/"/>

<div class="page-header">
  <h1><spring:message code="title.space.management" text="Space Management"/><small><spring:message code="title.spaces.search" text="Pesquisa de Espaços"/></small></h1>
</div>

<form id="searchForm" class="form-inline" role="form" action="${searchUrl}" method="GET">
  		<div class="form-group">
   		  <label class="sr-only" for="searchSpace"><spring:message code="label.search" text="Nome"/></label>
    	  <input name="name" type="text" class="form-control" id="searchSpace" value="${name}" placeholder="<spring:message code="label.space.search.name" text="Nome do espaço"/>"></input>
  		</div>
  		 <button id="searchRequest" class="btn btn-default"><spring:message code="label.search" text="Procurar"/></button>
  	</form>

<c:choose>
	<c:when test="${not empty foundSpaces}">
			<table class="table">
				<thead>
					<tr>
						<th><spring:message code="label.spaces.type" text="Type"/></th>
						<th><spring:message code="label.spaces.name" text="Name"/></th>
						<th><spring:message code="label.spaces.number.sub.spaces" text="Number of Sub Spaces"/></th>
						<th><spring:message code="label.spaces.operations" text="Operations"/></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="space" items="${foundSpaces}">
						<spring:url value="/spaces/view/${space.externalId}" var="viewUrl" />
						
						<tr>
							<td>${space.classification.name.content}</td>
							<td>${space.name}</td>
							<td>${fn:length(space.children)}</td>
							<td>
								<a href="${viewUrl}" class="btn btn-default" title="View"><span class="glyphicon glyphicon-eye-open"></span></a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:when>
	<c:otherwise>
			<h3><spring:message code="label.empty.spaces" text="No available spaces." /></h3>
	</c:otherwise>
</c:choose>