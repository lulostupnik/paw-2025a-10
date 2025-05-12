<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <title>
    <c:choose>
      <c:when test="${isUpdate}">
        <spring:message code="updateUniversity.title"/>
      </c:when>
      <c:otherwise>
        <spring:message code="createUniversity.title"/>
      </c:otherwise>
    </c:choose>
  </title>

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
      <h1 class="auth-title">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="updateUniversity.title" text="Update University"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createUniversity.title"/>
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="auth-subtitle">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="updateUniversity.subtitle" text="Update university information"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createUniversity.subtitle" text="Add a new university to the system"/>
          </c:otherwise>
        </c:choose>
      </p>
    </div>

    <c:choose>
      <c:when test="${isUpdate}">
        <c:url var="formAction" value="/universities/${universityId}/edit"/>
      </c:when>
      <c:otherwise>
        <c:url var="formAction" value="/universities/create"/>
      </c:otherwise>
    </c:choose>

    <form:form modelAttribute="createUniversityForm" action="${formAction}" method="post" class="auth-form" id="universityForm" novalidate="true">

      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createUniversity.name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>


      <div class="form-group">
        <form:label path="abbreviation" cssClass="form-label required-field">
          <spring:message code="createUniversity.abbreviation"/>
        </form:label>
        <form:input path="abbreviation" id="abbreviation" type="text" cssClass="form-input ${not empty errors.getFieldError('abbreviation') ? 'error' : ''}" />
        <form:errors path="abbreviation" cssClass="error-message" />
      </div>


      <div class="form-group">
        <form:label path="city" cssClass="form-label required-field">
          <spring:message code="createUniversity.city"/>
        </form:label>
        <div class="autocomplete-wrapper">
          <select id="city" class="form-select ${not empty errors.getFieldError('city') ? 'error' : ''}" style="display: none;">
            <option value=""><spring:message code="createUniversity.city.select"/></option>
            <c:forEach var="item" items="${cities}">
              <option value="<c:out value="${item.name}"/>"/><c:out value="${item.name}"/></option>
            </c:forEach>
          </select>
            <c:set var="citySearch"><spring:message code="createUniversity.city.search"/></c:set>
          <form:input  path="city" type="text" id="citySearch" class="form-input autocomplete-input" placeholder="${citySearch}" />
          <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
            <c:forEach var="item" items="${cities}">
              <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                <c:out value="${item.name}"/>
              </div>
            </c:forEach>
          </div>

          <div id="selectedCity" class="selected-tags"></div>
        </div>
        <form:errors path="city" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="updateUniversity.submit" text="Update University"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createUniversity.submit"/>
          </c:otherwise>
        </c:choose>
      </button>
    </form:form>

    <div class="auth-footer">
      <a  class="auth-link" href="<c:url value='/dashboard/universities'/>" class="btn-text">
        <spring:message code="university.back" text="Back to universities"/>
      </a>
    </div>
  </div>
</div>




<script>
  function htmlDecode(input) {
    const doc = new DOMParser().parseFromString(input, "text/html");
    return doc.documentElement.textContent;
  }
    window.apiBaseUrl = '<c:url value="/" />';
    universitySelectedCity = htmlDecode('<c:out value="${createUniversityForm.city}"/>');
</script>
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/university-form.js'/>"></script>

</body>
</html>