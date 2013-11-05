<jsp:directive.include file="header.jsp" />
  <cas:authenticationSuccess>
    <cas:user>${fn:escapeXml(username)}</cas:user>
    <c:if test="${not empty pgtIou}"><cas:proxyGrantingTicket>${pgtIou}</cas:proxyGrantingTicket></c:if>
  </cas:authenticationSuccess>
<jsp:directive.include file="footer.jsp" />
