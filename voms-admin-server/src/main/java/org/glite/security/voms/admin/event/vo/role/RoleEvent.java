/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2016
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
 */
package org.glite.security.voms.admin.event.vo.role;

import org.apache.commons.lang.xwork.Validate;
import org.glite.security.voms.admin.event.MainEventDataPoints;
import org.glite.security.voms.admin.event.vo.VOEvent;
import org.glite.security.voms.admin.persistence.model.VOMSRole;
import org.glite.security.voms.admin.persistence.model.audit.AuditEvent;

@MainEventDataPoints("roleName")
public class RoleEvent extends VOEvent<VOMSRole> {

  public static final String ROLE_NAME = "roleName";

  protected RoleEvent(VOMSRole r) {

    super(r);
    Validate.notNull(r);
  }

  @Override
  protected void decorateAuditEvent(AuditEvent e) {

    e.addDataPoint(ROLE_NAME, getPayload().getName());

  }
}
