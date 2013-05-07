/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.idp.cas.flow;

/**
 * CAS protocol flow event identifiers.
 *
 * @author Marvin S. Addison
 */
public enum Events {

    /** IdP SSO session not found. */
    SessionNotFound,

    /** IdP SSO session found. */
    SessionFound,

    /** CAS protocol renew flag specified to force authentication. */
    RenewRequested,

    /** Service ticket created for service access request. */
    TicketCreated,

    /** Ticket creation failed due to an error. */
    TicketCreationFailed;


    /**
     * Converts enumeration name to an identifier suitable for a Spring Webflow event identifier.
     *
     * @return Events enumeration name with first letter lower-cased.
     */
    public String id() {
        return this.name().substring(0, 1).toLowerCase() + this.name().substring(1);
    }
}