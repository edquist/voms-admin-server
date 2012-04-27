package org.glite.security.voms.admin.view.actions.register;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.glite.security.voms.admin.event.EventManager;
import org.glite.security.voms.admin.event.registration.VOMembershipRequestConfirmedEvent;
import org.glite.security.voms.admin.persistence.model.VOMSGroup;
import org.glite.security.voms.admin.persistence.model.request.Request.STATUS;
import org.glite.security.voms.admin.persistence.model.request.RequesterInfo;
import org.glite.security.voms.admin.view.actions.BaseAction;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;
import com.opensymphony.xwork2.validator.annotations.ValidatorType;


@ParentPackage("base")
@Results( { @Result(name = BaseAction.INPUT, location = "requestAttributes"),
	@Result(name = BaseAction.SUCCESS, location = "registrationConfirmed"),
	@Result(name = BaseAction.ERROR, location = "registrationConfirmationError")
})
public class RequestAttributesAction extends RegisterActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String confirmationId;
	
	List<String> requestedGroups;
	
	@Override
	public String execute() throws Exception {
		
		if (!registrationEnabled())
			return REGISTRATION_DISABLED;

		if (!getModel().getStatus().equals(STATUS.SUBMITTED)){
			addActionError("Your request has already been confirmed!");
			return ERROR;
		}
		
		if (getModel().getConfirmId().equals(confirmationId))
			request.setStatus(STATUS.CONFIRMED);
		else{
			addActionError("Wrong confirmation id!");
			return ERROR;
		}
				
		String manageURL = getBaseURL() + "/home/login.action";
		
		if (requestedGroups != null && !requestedGroups.isEmpty()){
			Integer requestedGroupsSize = requestedGroups.size();
			
			getModel().getRequesterInfo().addInfo(RequesterInfo.MULTIVALUE_COUNT_PREFIX+"requestedGroup",requestedGroupsSize.toString());
			
			for (int i=0; i < requestedGroupsSize; i++)
				getModel().getRequesterInfo().addInfo("requestedGroup"+i, requestedGroups.get(i));
			
		}

		EventManager.dispatch(new VOMembershipRequestConfirmedEvent(request,
				manageURL));
		
		return SUCCESS;
	}

	@RequiredFieldValidator(type = ValidatorType.FIELD, message = "A confirmation id is required!")
	public String getConfirmationId() {
		return confirmationId;
	}

	public void setConfirmationId(String confirmationId) {
		this.confirmationId = confirmationId;
	}

	public List<String> getRequestedGroups() {
		return requestedGroups;
	}

	public void setRequestedGroups(List<String> requestedGroups) {
		this.requestedGroups = requestedGroups;
	}
	
}