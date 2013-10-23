package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.ServiceTicketRequest;
import net.shibboleth.idp.cas.protocol.ServiceTicketResponse;
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
}
