<jsp:directive.include file="/WEB-INF/jsp/include/header.jsp" />
<%@ page import="java.lang.String" %>
<%@ page import="org.jasig.cas.client.validation.Assertion" %>
<%
  final String service = "https://localhost:8443" + request.getServletContext().getContextPath() + "/proxied/";
  final Assertion assertion = (Assertion) session.getAttribute("_const_cas_assertion_");
  final String proxyTicket = assertion.getPrincipal().getProxyTicketFor(service);
  final String redirectUrl = service + "?ticket=" + proxyTicket;
  response.sendRedirect(redirectUrl);
%>
<jsp:directive.include file="/WEB-INF/jsp/include/footer.jsp" />
