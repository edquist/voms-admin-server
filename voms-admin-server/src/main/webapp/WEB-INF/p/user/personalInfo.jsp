<%@include file="/WEB-INF/p/shared/taglibs.jsp"%>
<p>
<s:form 
  id="savePersonalInformationForm"
  action="save-personal-information"
  namespace="/user"
  theme="bootstrap"
  cssClass="form-horizontal">
  
  <s:token/>
  <s:hidden name="userId" value="%{id}" />
  <s:textfield name="theName" label="Given name" size="40"
      value="%{name}" placeholder="Insert the user name..." />
      
    <s:textfield name="theSurname" label="Family name" size="40"
      value="%{surname}" placeholder="Insert the user family name..." />
      
    <s:textfield name="theInstitution" label="Institution"
      size="40" value="%{institution}" />
      
    <s:textarea name="theAddress" label="Address" rows="4"
      cols="30" value="%{address}" />
      
    <s:textfield name="thePhoneNumber" disabled="false" label="Phone" size="40"
       value="%{phoneNumber}" />
      
    <s:textfield name="theEmailAddress" disabled="false" label="Email"
      size="40" value="%{emailAddress}" />
    
    <div class="form-group">
      <div class="col-sm-offset-3 col-sm-9">
	    <sj:submit cssClass="btn btn-primary" 
	      value="%{'Save personal information'}" 
	      validate="true" validateFunction="bootstrapValidation"
	      />
	      </div>
    </div>
</s:form>
</p>

