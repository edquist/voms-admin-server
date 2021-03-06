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
package org.glite.security.voms.admin.event.user.attribute;

import org.apache.commons.lang.Validate;
import org.glite.security.voms.admin.event.EventCategory;
import org.glite.security.voms.admin.event.user.UserEvent;
import org.glite.security.voms.admin.persistence.model.VOMSUser;
import org.glite.security.voms.admin.persistence.model.attribute.VOMSUserAttribute;
import org.glite.security.voms.admin.persistence.model.audit.AuditEvent;

public class UserAttributeEvent extends UserEvent {

  final VOMSUserAttribute attribute;

  public UserAttributeEvent(VOMSUser payload,
    VOMSUserAttribute attribute) {

    super(EventCategory.UserAttributeEvent, payload);
    Validate.notNull(attribute);
    this.attribute = attribute;
  }

  @Override
  protected void decorateAuditEvent(AuditEvent e) {

    super.decorateAuditEvent(e);

    e.addDataPoint("attributeName", attribute.getName());
    e.addDataPoint("attributeValue", attribute.getValue());
  }

  public VOMSUserAttribute getAttribute() {

    return attribute;
  }
}
