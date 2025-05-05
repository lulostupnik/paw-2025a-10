<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
  <title><spring:message code="createUniversity.title"/></title>
  <!-- Include custom CSS -->
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
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
      </div>
      <h1 class="auth-title"><spring:message code="createUniversity.title"/></h1>
      <p class="auth-subtitle"><spring:message code="createUniversity.subtitle" text="Add a new university to the system"/></p>
    </div>

    <c:url var="createUniversityUrl" value="/universities/create"/>
    <form:form modelAttribute="createUniversityForm" action="${createUniversityUrl}" method="post" class="auth-form" id="universityForm" novalidate="true">
      <!-- Name Field -->
      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createUniversity.name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>

      <!-- Abbreviation Field -->
      <div class="form-group">
        <form:label path="abbreviation" cssClass="form-label required-field">
          <spring:message code="createUniversity.abbreviation"/>
        </form:label>
        <form:input path="abbreviation" id="abbreviation" type="text" cssClass="form-input ${not empty errors.getFieldError('abbreviation') ? 'error' : ''}" />
        <form:errors path="abbreviation" cssClass="error-message" />
      </div>

      <!-- City Field with Enhanced Autocomplete -->
      <div class="form-group">
        <form:label path="city" cssClass="form-label required-field">
          <spring:message code="createUniversity.city"/>
        </form:label>
        <div class="autocomplete-wrapper">
          <form:select path="city" id="city" cssClass="form-select ${not empty errors.getFieldError('city') ? 'error' : ''}" style="display: none;">
            <form:option value=""><spring:message code="createUniversity.city.select"/></form:option>
            <c:forEach var="item" items="${cities}">
              <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
            </c:forEach>
          </form:select>
          <input type="text" id="citySearch" class="form-input autocomplete-input" placeholder="<spring:message code="createUniversity.city.search" text="Type to search city..."/>" />
          <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
            <c:forEach var="item" items="${cities}">
              <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                <c:out value="${item.name}"/>
              </div>
            </c:forEach>
          </div>
          <!-- Container for selected cities -->
          <div id="selectedCities" class="selected-tags"></div>
        </div>
        <form:errors path="city" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <spring:message code="createUniversity.submit"/>
      </button>
    </form:form>

    <div class="auth-footer">
      <button type="button" name="back" class="auth-link" onClick="history.back()">
        <spring:message code="university.back" text="Back to universities"/>
      </button>
    </div>
  </div>
</div>

<!-- Include modularized JavaScript files -->
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/university-form.js'/>"></script>

</body>
</html>
