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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glite.security.voms.User;
import org.glite.security.voms.admin.actionforms.UserForm;
import org.glite.security.voms.admin.common.NotFoundException;
import org.glite.security.voms.admin.common.NullArgumentException;
import org.glite.security.voms.admin.common.PathNamingScheme;
import org.glite.security.voms.admin.common.VOMSSyntaxException;
import org.glite.security.voms.admin.database.AlreadyExistsException;
import org.glite.security.voms.admin.database.Auditable;
import org.glite.security.voms.admin.database.NoSuchAttributeException;
import org.glite.security.voms.admin.database.NoSuchMappingException;
import org.glite.security.voms.admin.database.VOMSInconsistentDatabaseException;




/**
 * 
 * 
 * 
 * @author andrea
 * 
 */
public class VOMSUser implements Serializable, Auditable, Comparable {

    private static final long serialVersionUID = -3815869585264835046L;

    public static final Log log = LogFactory.getLog( VOMSUser.class );

    public VOMSUser() {

    }

    Long id;
    VOMSCA ca;

    // Old information
    String dn;
    String cn;
    String certURI;
    
    // New membership information
    String name;
    String surname;
    String institution;
    String address;
    String phoneNumber;   
    String emailAddress;
    
    
    // Creation time and validity info
    Date creationTime;
    Date endTime;
        
    
    
    Set attributes = new HashSet();
    Set mappings = new TreeSet();
    Set<Certificate> certificates = new HashSet<Certificate>();

    public String getDn() {

        return dn;
    }

    public void setDn( String name ) {

        this.dn = name;
    }

    /**
     * @return Returns the cn.
     */
    public String getCn() {

        return cn;
    }

    /**
     * @param cn
     *            The cn to set.
     */
    public void setCn( String cn ) {

        this.cn = cn;
    }

    /**
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {

        return emailAddress;
    }

    /**
     * @param emailAddress
     *            The emailAddress to set.
     */
    public void setEmailAddress( String emailAddress ) {

        this.emailAddress = emailAddress;
    }

    public Long getId() {

        return id;
    }

    public void setId( Long id ) {

        this.id = id;
    }

    /**
     * @return Returns the ca.
     */
    public VOMSCA getCa() {

        return ca;
    }

    /**
     * @param ca
     *            The ca to set.
     */
    public void setCa( VOMSCA ca ) {

        this.ca = ca;
    }

    /**
     * @return Returns the certURI.
     */
    public String getCertURI() {

        return certURI;
    }

    /**
     * @param certURI
     *            The certURI to set.
     */
    public void setCertURI( String certURI ) {

        this.certURI = certURI;
    }

    public Set getAttributes() {

        return attributes;
    }

    public void setAttributes( Set attributes ) {

        this.attributes = attributes;
    }

    public Set getMappings() {

        return mappings;
    }

    public void setMappings( Set mappings ) {

        this.mappings = mappings;
    }

    public VOMSUserAttribute getAttributeByName( String name ) {

        Iterator i = attributes.iterator();

        while ( i.hasNext() ) {
            VOMSUserAttribute tmp = (VOMSUserAttribute) i.next();

            if ( tmp.getName().equals( name ) )
                return tmp;
        }

        return null;

    }

    public void deleteAttributeByName( String attrName ) {

        deleteAttribute( getAttributeByName( attrName ) );

    }

    public void deleteAttribute( VOMSUserAttribute val ) {

        if ( !attributes.contains( val ) )
            throw new NoSuchAttributeException( "Attribute \"" + val.getName()
                    + "\" undefined for user " + this );

        attributes.remove( val );

    }

    public void setAttribute( String name, String value ) {

        VOMSUserAttribute val = getAttributeByName( name );

        if ( val == null )
            throw new NoSuchAttributeException( "Attribute \"" + name
                    + "\" undefined for user \"" + dn + "\"." );

        val.setValue( value );

    }

    public void addAttribute( VOMSUserAttribute val ) {

        val.setUser( this );
        
        if ( attributes.contains( val ) ){
        	attributes.remove(val);
        }
        
        attributes.add( val );
    }

    public boolean isMember( String groupName ) {

        if ( groupName == null )
            throw new NullArgumentException(
                    "Cannot org.glite.security.voms.admin.test membership in a null group!" );

        if ( !PathNamingScheme.isGroup( groupName ) )
            throw new VOMSSyntaxException(
                    "Group name passed as argument does not respect the VOMS FQAN syntax. ["
                            + groupName + "]" );

        Iterator i = getMappings().iterator();

        while ( i.hasNext() ) {

            VOMSMapping m = (VOMSMapping) i.next();
            if ( m.getGroup().getName().equals( groupName )
                    && m.isGroupMapping() )
                return true;
        }

        return false;

    }

    public boolean isMember( VOMSGroup g ) {

        if ( g == null )
            throw new NullArgumentException(
                    "Cannot org.glite.security.voms.admin.test membership in a null group!" );

        Iterator i = getMappings().iterator();

        while ( i.hasNext() ) {

            VOMSMapping m = (VOMSMapping) i.next();
            if ( m.getGroup().equals( g ) && m.isGroupMapping() )
                return true;
        }

        return false;

    }

    public void addToGroup( VOMSGroup g ) {

        log.debug( "Adding user \"" + this + "\" to group \"" + g + "\"." );

        VOMSMapping m = new VOMSMapping( this, g, null );
        if ( !getMappings().add( m ) )
            throw new AlreadyExistsException( "User \"" + this
                    + "\" is already a member of group \"" + g + "\"." );

        // g.getMappings().add( m );

        // Add this user to parent groups
        if ( !g.isRootGroup() ) {
            if ( !isMember( g.parent ) )
                addToGroup( g.parent );
        }

    }

    public void removeFromGroup( VOMSGroup g ) {

        log.debug( "Removing user \"" + this + "\" from group \"" + g + "\"." );

        dismissRolesInGroup( g );

        VOMSMapping m = new VOMSMapping( this, g, null );

        if ( getMappings().contains( m ) )
            getMappings().remove( m );

        else
            throw new NoSuchMappingException( "User \"" + this
                    + "\" is not a member of group \"" + g + "\"." );

    }

    public VOMSMapping assignRole( VOMSGroup g, VOMSRole r ) {

        if ( !isMember( g ) )
            throw new NoSuchMappingException( "User \"" + this
                    + "\" is not a member of group \"" + g + "\"." );

        VOMSMapping m = new VOMSMapping( this, g, r );
        if ( getMappings().contains( m ) )
            throw new AlreadyExistsException( "User \"" + this
                    + "\" already has role \"" + r + "\" in group \"" + g
                    + "\"." );

        log.debug( "Assigning role \"" + r + "\" to user \"" + this
                + "\" in group \"" + g + "\"." );

        getMappings().add( m );
        r.getMappings().add( m );

        return m;

    }

    public VOMSMapping dismissRole( VOMSGroup g, VOMSRole r ) {

        if ( !isMember( g ) )
            throw new NoSuchMappingException( "User \"" + this
                    + "\" is not a member of group \"" + g + "\"." );

        if ( !hasRole( g, r ) )
            throw new NoSuchMappingException( "User \"" + this
                    + "\" does not have role \"" + r + "\" in group \"" + g
                    + "\"." );

        log.debug( "Dismissing role \"" + r + "\" from user \"" + this
                + "\" in group \"" + g + "\"." );

        Iterator i = getMappings().iterator();
        boolean removed = false;

        VOMSMapping m = null;
        while ( i.hasNext() ) {
            m = (VOMSMapping) i.next();
            if ( m.isRoleMapping() ) {
                if ( m.getGroup().equals( g ) && m.getRole().equals( r ) ) {
                    i.remove();
                    r.getMappings().remove( m );
                    removed = true;
                }
            }
        }

        if ( !removed )
            throw new VOMSInconsistentDatabaseException(
                    "Error removing exiting role mapping!" );
        return m;

    }

    public void dismissRolesInGroup( VOMSGroup g ) {

        if ( !isMember( g ) )
            throw new NoSuchMappingException( "User \"" + this
                    + "\" is not a member of group \"" + g + "\"." );

        Iterator i = getMappings().iterator();

        while ( i.hasNext() ) {

            VOMSMapping m = (VOMSMapping) i.next();
            if ( m.getGroup().equals( g ) && m.isRoleMapping() )
                i.remove();
        }

        return;
    }

    public boolean hasRole( VOMSGroup g, VOMSRole r ) {

        if ( !isMember( g ) )
            throw new NoSuchMappingException( "User \"" + this
                    + "\" is not a member of group \"" + g + "\"." );

        Iterator i = getMappings().iterator();

        while ( i.hasNext() ) {

            VOMSMapping m = (VOMSMapping) i.next();
            if ( m.isRoleMapping() ) {
                if ( m.getGroup().equals( g ) && m.getRole().equals( r ) )
                    return true;
            }
        }

        return false;
    }

    public boolean hasRole( String fqan ) {

        if ( !PathNamingScheme.isQualifiedRole( fqan ) )
            throw new IllegalArgumentException(
                    "Role name passed as argument is not a qualified role! ["
                            + fqan + "]" );

        String groupName = PathNamingScheme.getGroupName( fqan );
        String roleName = PathNamingScheme.getRoleName( fqan );

        Iterator i = getMappings().iterator();

        while ( i.hasNext() ) {

            VOMSMapping m = (VOMSMapping) i.next();
            if ( m.isRoleMapping() ) {
                if ( m.getGroup().getName().equals( groupName )
                        && m.getRole().getName().equals( roleName ) )
                    return true;
            }
        }

        return false;

    }

    public Set getGroups() {

        SortedSet res = new TreeSet();
        Iterator mIter = getMappings().iterator();
        while ( mIter.hasNext() ) {

            VOMSMapping m = (VOMSMapping) mIter.next();
            if ( m.isGroupMapping() )
                res.add( m.getGroup() );
        }

        return Collections.unmodifiableSortedSet( res );

    }

    public Set getRoles( VOMSGroup g ) {

        SortedSet res = new TreeSet();

        Iterator mIter = getMappings().iterator();
        while ( mIter.hasNext() ) {

            VOMSMapping m = (VOMSMapping) mIter.next();
            if ( m.isRoleMapping() && m.getGroup().equals( g ) )
                res.add( m.getRole() );
        }

        return Collections.unmodifiableSortedSet( res );
    }

    
    public Set getRoleMappings(){
        
        SortedSet res = new TreeSet();
        
        Iterator mIter = getMappings().iterator();
        
        while ( mIter.hasNext() ) {

            VOMSMapping m = (VOMSMapping) mIter.next();
            if (m.isRoleMapping())
                res.add( m.getFQAN() );
        }
        
        return res;
    }
    
    public Map getMappingsMap() {

        log.debug( "mappings.size(): " + getMappings().size() );
        if ( getMappings().size() == 0 )
            return null;

        Iterator i = getMappings().iterator();
        Map map = new TreeMap();

        while ( i.hasNext() ) {

            VOMSMapping m = (VOMSMapping) i.next();
            log.debug( "mapping: " + m );

            if ( m.isGroupMapping() ) {

                log.debug( "Added group mapping to map: " + m.getGroup() );
                map.put( m.getGroup(), new TreeSet() );

            } else if ( m.isRoleMapping() ) {

                if ( !map.containsKey( m.getGroup() ) ) {

                    Set s = new TreeSet();
                    s.add( m.getRole() );
                    map.put( m.getGroup(), s );

                    log.debug( "Added mapping to map: " + m );

                } else {

                    Set s = (Set) map.get( m.getGroup() );
                    s.add( m.getRole() );

                    // is this necessary?
                    map.put( m.getGroup(), s );
                    log.debug( "Added mapping to map:" + m.getRole() );
                }
            }
        }

        return map;

    }

    public VOMSUser populate( UserForm form ) {

        setDn( form.getDn() );
        // setCertURI( form.getCrlURI() );
        setCn( form.getCn() );
        setEmailAddress( form.getEmailAddress() );

        return this;
    }

    public void fromUser(User u){
        
        if (u == null)
            throw new NullArgumentException("User passed as argument is null!");
        
        setCn( u.getCN() );
        setEmailAddress( u.getMail() );
    }
    public User asUser() {

        User u = new User();

        u.setDN( getDn() );
        u.setCA( getCa().getSubjectString() );
        u.setCN( getCn() );
        u.setMail( getEmailAddress() );
        u.setCertUri( getCertURI() );

        return u;
    }

    
    public static User[] collectionAsUsers( Collection c ) {

        if (c == null || c.isEmpty())
            return null;
        
        User[] users = new User[c.size()];
        
        int index = 0;
        Iterator i = c.iterator();
        
        while (i.hasNext())
            users[index++] = ((VOMSUser)i.next()).asUser();
        
        return users;
        
    }

    public String toString() {

        return name + " " + surname + " ("+id+")";
    }

    public boolean equals( Object other ) {

        
        if (this == other )
            return true;
        
        if (other == null)
            return false;
        
        if (!(other instanceof VOMSUser))
            return false;
        
        VOMSUser that = (VOMSUser)other;
        
        if (this.getId().equals( that.getId() ))
            return true;
        
        return false;

    }

    public int hashCode() {

        if (this == null)
            return 0;
        
        if (this.getId() != null)
            this.getId().hashCode();
        
        return super.hashCode();
        
    }

    public int compareTo( Object o ) {

        if ( this.equals( o ) )
            return 0;

        if (o == null)
            return 1;
        
        VOMSUser that = (VOMSUser) o;
        // Sort by surname, and then by name
        
        if (getSurname().equals( that.getSurname() )){
            
            return getName().compareTo( that.getName() );
        }
        
        return getSurname().compareTo( that.getSurname() );
    }

    public String getShortName(){
        return dn;
    }

    
    public Set<Certificate> getCertificates() {
    
        return certificates;
    }

    
    public void setCertificates( Set<Certificate> certificates ) {
    
        this.certificates = certificates;
    }
    
    public void addCertificate(Certificate cert){
        
        if (hasCertificate( cert ))
            throw new AlreadyExistsException("Certificate '"+cert+"' is already bound to user '"+this+"'.");
        
        getCertificates().add(cert);
        
    }
    
    public boolean hasCertificate(Certificate cert){
    
        return getCertificates().contains( cert );
    }
    
    public void removeCertificate(Certificate cert){
        
        if (!hasCertificate( cert ))
            throw new NotFoundException("Certificate '"+cert+"' is not bound to user '"+this+"'.");
        
        getCertificates().remove( cert );
        
    }

    
    public String getAddress() {
    
        return address;
    }

    
    public void setAddress( String address ) {
    
        this.address = address;
    }

    
    public Date getCreationTime() {
    
        return creationTime;
    }

    
    public void setCreationTime( Date creationTime ) {
    
        this.creationTime = creationTime;
    }

    
    public Date getEndTime() {
    
        return endTime;
    }

    
    public void setEndTime( Date endTime ) {
    
        this.endTime = endTime;
    }

    
    public String getInstitution() {
    
        return institution;
    }

    
    public void setInstitution( String institution ) {
    
        this.institution = institution;
    }

    
    public String getName() {
    
        return name;
    }

    
    public void setName( String name ) {
    
        this.name = name;
    }

    
    public String getPhoneNumber() {
    
        return phoneNumber;
    }

    
    public void setPhoneNumber( String phoneNumber ) {
    
        this.phoneNumber = phoneNumber;
    }

    
    public String getSurname() {
    
        return surname;
    }

    
    public void setSurname( String surname ) {
    
        this.surname = surname;
    }
    
    public String getFullName(){
        
        return this.getName()+" "+this.getSurname();
    }
    
    public Certificate getDefaultCertificate(){
        
        Iterator<Certificate> iter = getCertificates().iterator();

        if (iter.hasNext())
            return iter.next();
        
        return null;
        
        
    }
}