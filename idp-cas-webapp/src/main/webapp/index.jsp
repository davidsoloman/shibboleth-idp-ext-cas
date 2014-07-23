<jsp:directive.include file="/WEB-INF/jsp/include/header.jsp" />

<h1>Demo Start Page</h1>

<p>Click the button below to begin, which links to a CAS protected area.</p>
<p>You will be redirected to the IdP login page. Use any of the following credentials to authenticate:</p>
<ul>
    <li>john/password</li>
    <li>paul/password</li>
    <li>george/password</li>
    <li>ringo/password</li>
</ul>

<div class="big-buttons" style="margin-top:40px">
  <a class="button" href="protected/">Start</a>
</div>

<jsp:directive.include file="/WEB-INF/jsp/include/footer.jsp" />
