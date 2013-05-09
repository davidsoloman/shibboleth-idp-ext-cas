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

package net.shibboleth.idp.cas.ticket;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.idp.cas.ticket.metadata.MetaDataUtil;
import org.joda.time.Instant;
import org.opensaml.util.storage.StorageRecord;
import org.opensaml.util.storage.StorageService;

/**
 * Delegates persistence operations to a {@link org.opensaml.util.storage.StorageService}.
 *
 * @author Marvin S. Addison
 */
public class DelegatingTicketStore implements TicketStore {

    /** Storage service context name. */
    public static final String CONTEXT = "http://jasig.org/cas/tickets";

    /** Storage service to which ticket persistence operations are delegated. */
    @Nonnull private final StorageService storageService;


    /**
     * Creates a new instance.
     *
     * @param delegate Storage service to which ticket persistence operations are delegated.
     */
    public DelegatingTicketStore(@Nonnull final StorageService delegate) {
        this.storageService = delegate;
    }

    @Override
    public void add(final @Nonnull Ticket ticket) {
        try {
            final boolean result = storageService.createString(
                    CONTEXT,
                    MetaDataUtil.getKey(ticket),
                    MetaDataUtil.getValue(ticket),
                    MetaDataUtil.getExpiration(ticket));
            if (!result) {
                throw new RuntimeException("Could not add ticket for unspecified reason.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Storage service error occurred while adding ticket", e);
        }
    }

    @Nullable
    @Override
    public Ticket get(final @Nonnull String id) {
        final StorageRecord record;
        try {
            record = storageService.readString(CONTEXT, id);
        } catch (IOException e) {
            throw new RuntimeException("Storage service error occurred while fetching ticket", e);
        }
        final Ticket ticket = new Ticket();
        MetaDataUtil.setKey(ticket, id);
        MetaDataUtil.setValue(ticket, record.getValue());
        MetaDataUtil.setExpiration(ticket, record.getExpiration());
        return ticket;
    }

    @Override
    public void remove(final @Nonnull String id) {
        try {
            storageService.deleteString(CONTEXT, id);
        } catch (IOException e) {
            throw new RuntimeException("Storage service error occurred while adding ticket", e);
        }
    }

    @Override
    public int expunge() {
        try {
            storageService.reap(CONTEXT);
        } catch (IOException e) {
            throw new RuntimeException("Storage service error occurred while removing expired tickets.", e);
        }
        return -1;
    }
}
