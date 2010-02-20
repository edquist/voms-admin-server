/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Authors:
 * 	Andrea Ceccanti (INFN)
 */
package org.glite.security.voms.admin.view.actions.search;

import java.util.Map;

import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.interceptor.SessionAware;
import org.glite.security.voms.admin.operations.search.BaseSearchOperation;
import org.glite.security.voms.admin.persistence.dao.SearchResults;
import org.glite.security.voms.admin.view.actions.BaseAction;
import org.glite.security.voms.admin.view.actions.SearchData;

import com.opensymphony.xwork2.validator.annotations.VisitorFieldValidator;

@ParentPackage("base")
public abstract class BaseSearchAction extends BaseAction implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String USER_SEARCH_NAME = "user";
	public static final String GROUP_SEARCH_NAME = "group";
	public static final String ROLE_SEARCH_NAME = "role";
	public static final String ATTRIBUTE_SEARCH_NAME = "attribute";

	public static final String MEMBER_SEARCH_NAME = "member";

	public static final String GROUP_MEMBER_SEARCH_NAME = "group-member";
	public static final String ROLE_MEMBER_SEARCH_NAME = "role-member";

	protected SearchData searchData;

	protected SearchResults searchResults;

	protected Map<String, Object> session;
	
	@VisitorFieldValidator(appendPrefix = true, message = "Invalid input:  ")
	public SearchData getSearchData() {
		return searchData;
	}

	public void setSearchData(SearchData searchData) {
		this.searchData = searchData;
	}

	@Override
	public String execute() throws Exception {

		searchResults = (SearchResults) BaseSearchOperation.instance(
				getSearchData()).execute();

		session.put("searchData", getSearchData());
		session.put("searchResults", searchResults);
		
		return SUCCESS;

	}

	public SearchResults getSearchResults() {
		return searchResults;
	}

	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}

	protected void initSearchData(String searchType) {

		if (getSearchData() == null) {
			SearchData sd = new SearchData();
			sd.setType(searchType);
			setSearchData(sd);

		} else
			getSearchData().setType(searchType);
	}
	
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
