package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketResponse;
import net.shibboleth.idp.cas.protocol.ServiceTicketValidationResponse;
import net.shibboleth.idp.cas.protocol.TicketValidationRequest;
import org.springframework.webflow.execution.RequestContext;

/**
 * Utility class that provides static methods for storing and retrieving data required by collaborating flow
 * action states.
 *
 * @author Marvin S. Addison
 */
public final class FlowStateSupport {

    /** Name of flow attribute containing {@link ServiceTicketRequest}. */
    public static final String SERVICE_TICKET_REQUEST_KEY = "serviceTicketRequest";

    /** Name of flow attribute containing {@link ServiceTicketResponse}. */
    public static final String SERVICE_TICKET_RESPONSE_KEY = "serviceTicketResponse";

    /** Name of flow attribute containing {@link TicketValidationRequest}. */
    public static final String TICKET_VALIDATION_REQUEST_KEY = "ticketValidationRequest";

    /** Name of flow attribute containing {@link ServiceTicketValidationResponse}. */
    public static final String SERVICE_TICKET_VALIDATION_RESPONSE_KEY = "serviceTicketValidationResponse";

    /** Protected constructor of utility class. */
    private FlowStateSupport() {}

    public static ServiceTicketRequest getServiceTicketRequest(final RequestContext context) {
        return (ServiceTicketRequest) context.getRequestScope().get(SERVICE_TICKET_REQUEST_KEY);
    }

    public static void setServiceTicketRequest(final RequestContext context, final ServiceTicketRequest request) {
        context.getRequestScope().put(SERVICE_TICKET_REQUEST_KEY, request);
    }

    public static ServiceTicketResponse getServiceTicketResponse(final RequestContext context) {
        return (ServiceTicketResponse) context.getRequestScope().get(SERVICE_TICKET_RESPONSE_KEY);
    }

    public static void setServiceTicketResponse(final RequestContext context, final ServiceTicketResponse response) {
        context.getRequestScope().put(SERVICE_TICKET_RESPONSE_KEY, response);
    }

    public static TicketValidationRequest getTicketValidationRequest(final RequestContext context) {
        return (TicketValidationRequest) context.getRequestScope().get(TICKET_VALIDATION_REQUEST_KEY);
    }

    public static void setTicketValidationRequest(final RequestContext context, final TicketValidationRequest request) {
        context.getRequestScope().put(TICKET_VALIDATION_REQUEST_KEY, request);
    }

    public static ServiceTicketValidationResponse getServiceTicketValidationResponse(final RequestContext context) {
        return (ServiceTicketValidationResponse) context.getRequestScope().get(SERVICE_TICKET_VALIDATION_RESPONSE_KEY);
    }

    public static void setServiceTicketValidationResponse(
            final RequestContext context, final ServiceTicketValidationResponse response) {
        context.getRequestScope().put(SERVICE_TICKET_VALIDATION_RESPONSE_KEY, response);
    }
}
