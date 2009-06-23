package org.glite.security.voms.admin.jsp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.glite.security.voms.admin.model.VOMSGroup;
import org.glite.security.voms.admin.model.VOMSUser;
import org.glite.security.voms.admin.operations.users.FindUnassignedRoles;
import org.glite.security.voms.admin.operations.users.FindUserOperation;


public class UnassignedRoleMapTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	String userId;
	String var;
	
	@Override
	public int doStartTag() throws JspException {
		
		VOMSUser u = (VOMSUser) FindUserOperation.instance(Long.parseLong(userId)).execute();
		
		Map unassignedRoles = new HashMap();

        Iterator<VOMSGroup> groups = u.getGroups().iterator();

        while ( groups.hasNext() ) {

            VOMSGroup g = (VOMSGroup) groups.next();
            List roles = (List) FindUnassignedRoles.instance( u.getId(),
                    g.getId() ).execute();
            
            unassignedRoles.put( g.getId(), roles );
        }
		
        pageContext.setAttribute(var, unassignedRoles);
        
		return SKIP_BODY;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
}