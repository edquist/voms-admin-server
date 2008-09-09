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
package org.glite.security.voms.admin.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.security.voms.admin.common.Constants;
import org.glite.security.voms.admin.dao.VOMSAdminDAO;
import org.glite.security.voms.admin.operations.VOMSContext;
import org.glite.security.voms.admin.operations.VOMSPermission;



public class ACL implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog( ACL.class );
    
    Long id;

    VOMSGroup group = null;

    Boolean defaultACL;

    VOMSRole role = null;

    // FIXME: Make permissions a sorted collection!
    Map permissions = new HashMap();

    public ACL() {

     
    }

    public ACL( VOMSGroup g, VOMSRole r, boolean isDefaultACL ) {

        this.group = g;
        this.role = r;
        this.defaultACL = new Boolean( isDefaultACL );
    }

    public ACL( VOMSGroup g, boolean isDefaultACL ) {

        this( g, null, isDefaultACL );
    }

    public VOMSGroup getGroup() {

        return group;
    }

    public void setGroup( VOMSGroup group ) {

        this.group = group;
    }

    public Map getPermissions() {

        return permissions;
    }

    public void setPermissions( Map permissions ) {

        this.permissions = permissions;
    }

    public VOMSRole getRole() {

        return role;
    }

    public void setRole( VOMSRole role ) {

        this.role = role;
    }

    public boolean equals( Object other ) {

        if ( this == other )
            return true;
        if ( !( other instanceof ACL ) )
            return false;

        ACL that = (ACL) other;

        if ( that == null )
            return false;

        if ( getGroup().equals( that.getGroup() ) ) {

            if ( ( getRole() == null ) && ( that.getRole() == null ) ) {

                return getDefaultACL().equals( that.getDefaultACL() );
            }

            if ( ( getRole() != null ) && ( that.getRole() != null ) )
                return getRole().equals( that.getRole() );
        }

        return false;
    }

    public int hashCode() {

        int result = 14;

        result = 29 * result + getGroup().hashCode();

        if ( getRole() != null )
            result = 29 * result + getRole().hashCode();

        return result;
    }

    public Boolean getDefaultACL() {

        return defaultACL;
    }

    public void setDefaultACL( Boolean defaultACL ) {

        this.defaultACL = defaultACL;
    }

    public Long getId() {

        return id;
    }

    public void setPermissions( VOMSAdmin a, VOMSPermission p ) {

        getPermissions().put( a, p );

    }

    public void removePermissions( VOMSAdmin a ) {

        getPermissions().remove( a );

    }

    public void setId( Long id ) {

        this.id = id;
    }

    public boolean isDefautlACL() {

        return defaultACL.booleanValue();
    }

    public VOMSPermission getPermissions( VOMSAdmin a ) {

        return (VOMSPermission) getPermissions().get( a );

    }
    
    public Map getRolePermissions(){
        
        Map result = new HashMap();
                
        Iterator entries = permissions.entrySet().iterator();
                
        while(entries.hasNext()){
            
            Map.Entry entry = (Map.Entry)entries.next();
            
            VOMSAdmin admin = (VOMSAdmin)entry.getKey();
            
            if (admin.getCa().getSubjectString().equals( Constants.ROLE_CA))
                result.put( admin, entry.getValue() );
                
        }
        
        return result;
    }
    
    public Map getGroupPermissions(){
        
        Map result = new HashMap();
        
        Iterator entries = permissions.entrySet().iterator();
                
        while(entries.hasNext()){
            
            Map.Entry entry = (Map.Entry)entries.next();
            
            VOMSAdmin admin = (VOMSAdmin)entry.getKey();
            
            if (admin.getCa().getSubjectString().equals( Constants.GROUP_CA))
                result.put( admin, entry.getValue() );
                
        }
        
        return result;
    }
    
    public Set getAdminsWithPermissions(VOMSPermission requiredPermission){
        
        Set results = new HashSet();
        
        Iterator entries = permissions.entrySet().iterator();
        
        while (entries.hasNext()){
            
            Map.Entry entry = (Map.Entry) entries.next();
            
            VOMSPermission p = (VOMSPermission) entry.getValue();
            VOMSAdmin a = (VOMSAdmin)entry.getKey();
            if (p.satisfies( requiredPermission ) && ((a.isGroupAdmin() || a.isRoleAdmin())))
                results.add( entry.getKey() );
        }
        
        return results;
        
    }
    public VOMSPermission getAnyAuthenticatedUserPermissions(){
        
        VOMSAdmin anyAuthenticatedUserAdmin = VOMSAdminDAO.instance().getAnyAuthenticatedUserAdmin();
        return (VOMSPermission) permissions.get( anyAuthenticatedUserAdmin );
    }
    
    public Map getExternalPermissions(){
        
        Map result = new HashMap();
        
        Iterator admins = permissions.keySet().iterator();
        
        while (admins.hasNext()){
            
            VOMSAdmin admin  = (VOMSAdmin) admins.next();
            
            if ((admin.getDn().equals( Constants.ANYUSER_ADMIN )) ||          
            (!admin.getDn().startsWith(Constants.INTERNAL_DN_PREFIX)))
                result.put(admin,permissions.get(admin));
            
        }
        
        return result;
    }
    public VOMSContext getContext(){
        
        return VOMSContext.instance(getGroup(),getRole());
    }
    
    public String toString() {
    
        return ToStringBuilder.reflectionToString(this);
        
    }
}