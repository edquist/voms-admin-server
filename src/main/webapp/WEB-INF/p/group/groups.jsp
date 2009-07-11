<%@include file="/WEB-INF/p/shared/taglibs.jsp"%>


<h2>Groups</h2>

<div class="createTab">
  <voms:hasPermissions var="canCreate" 
            context="/${voName}" 
            permission="CONTAINER_READ|CONTAINER_WRITE"/>
  
  <s:if test="#attr.canCreate">
    <s:url action="create" namespace="/group" method="input" var="createGroupURL"/>
    <s:a href="%{createGroupURL}">New group</s:a>
  </s:if>
</div>
 
<div class="searchTab">

<s:form validate="true">
  <s:hidden name="searchData.type" value="%{'group'}"/>
  <s:textfield name="searchData.text" size="20"/>
  <s:submit value="%{'Search groups'}"/>
</s:form>

<s:if test='(searchResults.searchString eq null) and (searchResults.results.size == 0)'>
No groups found in this VO.
</s:if>
<s:elseif test="searchResults.results.size == 0">
  No groups found matching search string '<s:property value="searchResults.searchString"/>'.
</s:elseif> 
<s:else>
  <table
    class="table"
    cellpadding="0"
    cellspacing="0"
  >
    <s:iterator
      value="searchResults.results"
      var="group"
      status="rowStatus"
    >
      <tr class="tableRow">

        <td width="95%">
          <div class="groupName">
            <s:url action="edit" namespace="/group" var="editURL" method="load">
              <s:param name="groupId" value="id"/>
            </s:url>
            <s:a href="%{editURL}">
              <s:property value="name" />
            </s:a>
          </div>
         </td>
         <td>
          <voms:hasPermissions var="canDelete" 
            context="/${voName}" 
            permission="CONTAINER_READ|CONTAINER_WRITE"/>
            
          <s:if test="(not rootGroup) and #attr['canDelete']">
            <s:form action="delete" namespace="/group">
              <s:url value="/img/delete_16.png" var="deleteImg"/>
              <s:token/>
              <s:hidden name="groupId" value="%{id}"/>
              <s:submit src="%{deleteImg}" type="image"/>
            </s:form>
          </s:if>
          
         </td>
      </tr>
    </s:iterator>
  </table>
  
  <s:url action="search" namespace="/group" var="searchURL"/>
  
  <voms:searchNavBar context="vo" 
      permission="r" 
      disabledLinkStyleClass="disabledLink"
      id="searchResults"
      linkStyleClass="navBarLink"
      searchURL="${searchURL}"
      styleClass="resultsCount"
      />
</s:else>
</div>