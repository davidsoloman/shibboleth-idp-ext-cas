<jsp:directive.include file="/WEB-INF/jsp/include/header.jsp" />

<h1>CAS Protected Area</h1>

<p>Authenticated username: <b>${sessionScope._const_cas_assertion_.principal.name}</b></p>

<p>Attributes:</p>
<ul>
  <c:forEach items="${sessionScope._const_cas_assertion_.principal.attributes}" var="entry">
    <li>
      <strong>${entry.key}</strong>: ${entry.value}
    </li>
  </c:forEach>
</ul>
<div class="big-buttons" style="margin-top:40px">
  <a class="button" href="proxy.jsp">Proxy</a>
</div>

<jsp:directive.include file="/WEB-INF/jsp/include/footer.jsp" />
