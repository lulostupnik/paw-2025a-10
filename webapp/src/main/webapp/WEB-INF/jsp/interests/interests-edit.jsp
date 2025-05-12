<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <title><spring:message code="editInterests.title" text="Edit Interests"/></title>

  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>

<div class="auth-container">
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-logo">
        <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
        </svg>
      </div>
      <h1 class="auth-title">
        <spring:message code="editInterests.title" text="Edit Interests"/>
      </h1>
      <p class="auth-subtitle">
        <spring:message code="editInterests.subtitle" text="Update your interests"/>
      </p>
    </div>

    <c:url var="formAction" value="/interests/edit"/>

    <form:form modelAttribute="editInterestsForm" action="${formAction}" method="post" class="auth-form" id="interestsForm" novalidate="true">

      <div class="form-group">
        <form:label path="interests" cssClass="form-label required-field">
          <spring:message code="event.interest"/>
        </form:label>


        <form:select path="interests" multiple="true" id="interestsSelect" style="display: none;" >
          <form:options items="${interests}" itemValue="id" itemLabel="name"/>
        </form:select>


        <div class="autocomplete-wrapper">
          <input type="text" id="interestSearch" class="autocomplete-input ${not empty errors.getFieldError('interests') ? 'error' : ''}"
                 placeholder="<spring:message code="event.interest.search" text="Search interests..."/>" />

          <div id="interestDropdown" class="autocomplete-dropdown" style="display: none;">
            <c:forEach var="item" items="${interests}">
              <div class="autocomplete-item" data-value="${item.id}">
                <c:out value="${item.name}"/>
              </div>
            </c:forEach>
          </div>

          <div id="selectedInterests" class="selected-tags required-selected-tags"></div>

          <form:errors path="interests" cssClass="error-message" />
        </div>
      </div>

      <button type="submit" class="form-button">
        <spring:message code="editInterests.submit" text="Update Interests"/>
      </button>
    </form:form>

    <div class="auth-footer">
      <a class="auth-link" href="<c:url value='/profile/interests'/>">
        <spring:message code="interests.back" text="Back to Interests"/>
      </a>
    </div>
  </div>
</div>


<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script>
  function htmlDecode(input) {
    const doc = new DOMParser().parseFromString(input, "text/html");
    return doc.documentElement.textContent;
  }
  window.apiBaseUrl = '<c:url value="/" />';
  previousInterests = [
    <c:forEach var="interest" items="${userInterests}" varStatus="status">
    {
      "name": htmlDecode("<c:out value='${interest.name}'/>"),
      "id": htmlDecode("<c:out value='${interest.id}'/>")
    }<c:if test="${!status.last}">,</c:if>
    </c:forEach>
  ];
</script>

<script src="<c:url value='/resources/js/edit-interest.js'/>"></script>

</body>
</html>