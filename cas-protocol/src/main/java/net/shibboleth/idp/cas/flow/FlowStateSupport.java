package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.cas.protocol.*;
import net.shibboleth.idp.session.IdPSession;
import org.springframework.webflow.execution.RequestContext;

/**
 * Utility class that provides static methods for storing and retrieving data required by collaborating flow
 * action states.
 *
 * @author Marvin S. Addison
 */
public final class FlowStateSupport {

    /** Name of flow attribute containing {@link IdPSession}. */
    public static final String IDP_SESSION_KEY = "idpSession";

    /** Name of flow attribute containing {@link ProxyTicketRequest}. */
    public static final String PROXY_TICKET_REQUEST_KEY = "proxyTicketRequest";

    /** Name of flow attribute containing {@link ProxyTicketResponse}. */
    public static final String PROXY_TICKET_RESPONSE_KEY = "proxyTicketResponse";

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

    public static IdPSession getIdPSession(final RequestContext context) {
        return (IdPSession) context.getRequestScope().get(IDP_SESSION_KEY);
    }

    public static void setIdpSession(final RequestContext context, final IdPSession session) {
        context.getRequestScope().put(IDP_SESSION_KEY, session);
    }

    public static ServiceTicketRequest getServiceTicketRequest(final RequestContext context) {
        return (ServiceTicketRequest) context.getFlowScope().get(SERVICE_TICKET_REQUEST_KEY);
    }

    public static void setServiceTicketRequest(final RequestContext context, final ServiceTicketRequest request) {
        context.getFlowScope().put(SERVICE_TICKET_REQUEST_KEY, request);
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

    public static ProxyTicketRequest getProxyTicketRequest(final RequestContext context) {
        return (ProxyTicketRequest) context.getRequestScope().get(PROXY_TICKET_REQUEST_KEY);
    }

    public static void setProxyTicketRequest(final RequestContext context, final ProxyTicketRequest request) {
        context.getRequestScope().put(PROXY_TICKET_REQUEST_KEY, request);
    }


    public static ProxyTicketResponse getProxyTicketResponse(final RequestContext context) {
        return (ProxyTicketResponse) context.getRequestScope().get(PROXY_TICKET_RESPONSE_KEY);
    }

    public static void setProxyTicketResponse(final RequestContext context, final ProxyTicketResponse response) {
        context.getRequestScope().put(PROXY_TICKET_RESPONSE_KEY, response);
    }
}
