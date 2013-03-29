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
package net.shibboleth.idp.cas;

/**
 * Describes Jasig CAS server protocols supported by Shibboleth IdPv3 CAS integration components.
 *
 * @author Marvin S. Addison
 */
public enum Protocol {
    /** CAS protocol 2.0. */
    Cas20("service", "ticket"),

    /** SAML 1.1 protocol (as used by Jasig CAS server). */
    Saml11("TARGET", "SAMLart");

    /** Name of CAS 2.0 protocol renew request parameter. */
    public static final String RENEW_ARTIFACT_NAME = "renew";

    /** Name of CAS 2.0 protocol gateway request parameter. */
    public static final String GATEWAY_ARTIFACT_NAME = "gateway";

    /** Name of service artifact in this protocol. */
    private final String serviceArtifactName;

    /** Name of ticket artifact in this protocol. */
    private final String ticketArtifactName;

    private Protocol(final String serviceArtifact, final String ticketArtifact) {
        this.serviceArtifactName = serviceArtifact;
        this.ticketArtifactName = ticketArtifact;
    }

    public String getServiceArtifactName() {
        return serviceArtifactName;
    }

    public String getTicketArtifactName() {
        return ticketArtifactName;
    }
}
