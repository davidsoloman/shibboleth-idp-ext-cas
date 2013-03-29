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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Describes a request to access a service (relying party) by a CAS client using a supported protocol.
 *
 * @author Marvin S. Addison
 */
public class CasServiceAccessMessage {
    /** Protocol used to specify service access request. */
    @Nonnull final Protocol protocol;

    /** Service URL */
    @Nonnull private final String service;

    /** Optional CAS ticket identifier. */
    @Nonnull private String ticket;

    /** CAS protocol renew flag. */
    private boolean renew;

    /** CAS protocol gateway flag. */
    private boolean gateway;


    public CasServiceAccessMessage(@Nonnull final Protocol protocol, @Nonnull final String service) {
        this.protocol = protocol;
        this.service = service;
    }

    @Nonnull public Protocol getProtocol() {
        return protocol;
    }

    @Nonnull public String getService() {
        return service;
    }

    @Nullable public String getTicket() {
        return ticket;
    }

    void setTicket(@Nonnull final String ticket) {
        this.ticket = ticket;
    }

    public boolean isRenew() {
        return renew;
    }

    void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public boolean isGateway() {
        return gateway;
    }

    void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }
}
