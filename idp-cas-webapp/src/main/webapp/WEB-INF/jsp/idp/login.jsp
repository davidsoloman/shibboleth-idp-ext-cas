<jsp:directive.include file="../include/header.jsp" />

<h1>Login</h1>

<c:if test="${not empty validationError}" >
  <p id="error">${validationError}</p>
</c:if>

<form action="${flowExecutionUrl}" method="post">
  <fieldset>
    <div class="field">
      <label for="username">Username:</label>
      <input id="username" type="text" name="username" />
    </div>
    <div class="field">
      <label for="password">Password:</label>
      <input id="password" type="password" name="password" />
    </div>
    <div class="field">
      <label for="donotcache">Disable SSO:</label>
      <input id="donotcache" type="checkbox" name="donotcache" value="1" />
    <div id="field">
      <button type="submit" name="_eventId_proceed">Login</button>
    </div>
  </fieldset>
</form>

<jsp:directive.include file="../include/footer.jsp" />
