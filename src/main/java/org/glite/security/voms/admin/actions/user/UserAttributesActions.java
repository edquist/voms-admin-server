/*******************************************************************************
 *Copyright (c) Members of the EGEE Collaboration. 2006. 
 *See http://www.eu-egee.org/partners/ for details on the copyright
 *holders.  
 *
 *Licensed under the Apache License, Version 2.0 (the "License"); 
 *you may not use this file except in compliance with the License. 
 *You may obtain a copy of the License at 
 *
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *
 *Unless required by applicable law or agreed to in writing, software 
 *distributed under the License is distributed on an "AS IS" BASIS, 
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *See the License for the specific language governing permissions and 
 *limitations under the License.
 *
 * Authors:
 *     Andrea Ceccanti - andrea.ceccanti@cnaf.infn.it
 *******************************************************************************/
package org.glite.security.voms.admin.actions.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.glite.security.voms.admin.actionforms.UserAttributesActionsForm;
import org.glite.security.voms.admin.actions.BaseDispatchAction;
import org.glite.security.voms.admin.model.VOMSUser;
import org.glite.security.voms.admin.operations.users.DeleteUserAttributeOperation;
import org.glite.security.voms.admin.operations.users.FindUserOperation;
import org.glite.security.voms.admin.operations.users.SetUserAttributeOperation;
import org.glite.security.voms.admin.operations.users.UpdateUserOperation;


public class UserAttributesActions extends BaseDispatchAction {

	private static final Log log = LogFactory
			.getLog(UserAttributesActions.class);

	public ActionForward create(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserAttributesActionsForm aForm = (UserAttributesActionsForm) form;

		log.debug("aForm:" + aForm);
        
		VOMSUser u = (VOMSUser)FindUserOperation.instance(aForm.getUserId()).execute();

		storeUser(request,u);
		
		SetUserAttributeOperation.instance(u, aForm.getAttributeName(),
				aForm.getAttributeDescription(), aForm.getAttributeValue())
				.execute();

		return findSuccess(mapping);

	}

	public ActionForward edit(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserAttributesActionsForm aForm = (UserAttributesActionsForm) form;

		log.debug("aForm:" + aForm);

        VOMSUser u = (VOMSUser)FindUserOperation.instance(aForm.getUserId()).execute();

		// Just saving the user should be enough, changes should cascade to
		// edited attributes as well
		UpdateUserOperation.instance(u).execute();

		return findSuccess(mapping);
	}

	public ActionForward delete(
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		UserAttributesActionsForm aForm = (UserAttributesActionsForm) form;

		log.debug("aForm:" + aForm);
        
        VOMSUser u = (VOMSUser)FindUserOperation.instance(aForm.getUserId()).execute();
		
        storeUser(request,u);
        
		DeleteUserAttributeOperation.instance(u, aForm.getAttributeName())
				.execute();

		return findSuccess(mapping);
	}

}