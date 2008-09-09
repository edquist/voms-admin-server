<%--
 Copyright (c) Members of the EGEE Collaboration. 2006.
 See http://www.eu-egee.org/partners/ for details on the copyright
 holders.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 Authors:
     Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://org.glite.security.voms.tags" prefix="voms"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<%@ page contentType="text/html; charset=UTF-8" %>

<div id="searchUsersPane">
	<html:form action="/SearchUser" method="post">
		<html:hidden property="firstResults" value="0" />
		<html:text property="text" styleClass="searchField" size="20" value="${searchResults.searchString }"/>	
		<voms:submit context="vo" permission="CONTAINER_READ|MEMBERSHIP_READ" styleClass="submitButton" value="Search users"/>
	</html:form>
</div> <!--  searchPane -->

<div id="createUserPane">
	<voms:authorized permission="CONTAINER_READ|CONTAINER_WRITE|MEMBERSHIP_READ|MEMBERSHIP_WRITE" context="vo">
		<html:link action="/PreCreateUser" styleClass="vomsLink">Create a new user</html:link>
	</voms:authorized>
</div> <!-- createUserPane -->

<div id="searchResultsPane">
<c:choose>
<c:when test="${! empty searchResults.results}">
	
	<div class="header2">
		Users:
	</div>
	
	<table class="table" cellpadding="0" cellspacing="0">

		<c:forEach var="vomsUser" items="${searchResults.results}" varStatus="status">
			<tr class="${ (status.index) %2 eq 0 ? 'tableRowEven': 'tableRowOdd'}">
				<td width="95%">
					<div class="userDN">
						<c:url value="/PreEditUser.do" var="editUserUrl">
						<c:param name="id" value="${vomsUser.id}"/>
					</c:url>
					<voms:link
						context="vo"
						permission="r"
						href="${editUserUrl}"
						styleClass="actionLink"
						disabledStyleClass="disabledLink"
						>
						<!-- <voms:formatDN dn="${vomsUser.dn}" fields="CN"/> -->
						${vomsUser.name} ${vomsUser.surname}
					</voms:link>
					</div>
					<!-- 
					<div class="userCA">
						<voms:formatDN dn="${vomsUser.ca.subjectString}" fields="CN,O"/>
					</div>
					-->
				</td>
				<td width="5%" style="text-align:right">
					<c:url value="/DeleteUser.do" var="deleteUserUrl">
						<c:param name="id" value="${vomsUser.id}"/>
					</c:url>
					<voms:link
						context="vo"
						permission="rw"
						href="javascript:ask_confirm('Delete user? \n${vomsUser}\n', '${deleteUserUrl}', 'User ${vomsUser.dn} not deleted.')"
						styleClass="actionLink"
						disabledStyleClass="disabledLink"
						>
						delete user
					</voms:link>
				</td>
			</tr>
		</c:forEach>

	</table> <!-- table -->

	<div class="resultsFooter">
		<voms:searchNavBar context="vo" 
			permission="r" 
			disabledLinkStyleClass="disabledLink"
			id="searchResults"
			linkStyleClass="navBarLink"
			searchURL="/SearchUser.do"
			styleClass="resultsCount"
			/>	
	</div><!-- resultsFooter -->

</c:when> 
<c:otherwise>
	<c:choose>
		<c:when test="${empty searchResults.searchString}">
			<div class="header2">
			No users defined in this vo.
			</div>
		</c:when>
		<c:otherwise>
			<div class="header2">
			No users found matching "${ searchResults.searchString }".		
			</div>
		</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>
</div> <!-- serchResultsPane -->