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
        <spring:message code="editCity.title" text="Edit City"/>
      </c:when>
      <c:otherwise>
        <spring:message code="createCity.title" text="Create City"/>
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
            <spring:message code="editInterest.title" text="Edit City"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createInterest.title" text="Create City"/>
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="auth-subtitle">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editInterest.subtitle" text="Update city information"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createInterest.subtitle" text="Add a new city to the system"/>
          </c:otherwise>
        </c:choose>
      </p>
    </div>

    <c:choose>
      <c:when test="${isUpdate}">
        <c:url var="formAction" value="/interests/${interestId}/edit"/>
      </c:when>
      <c:otherwise>
        <c:url var="formAction" value="/interests/create"/>
      </c:otherwise>
    </c:choose>

    <form:form modelAttribute="createInterestForm" action="${formAction}" method="post" class="auth-form" id="cityForm" novalidate="true">

      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createInterest.name" text="City Name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editInterest.submit" text="Update City"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createInterest.submit" text="Create City"/>
          </c:otherwise>
        </c:choose>
      </button>
    </form:form>

    <div class="auth-footer">
      <a  class="auth-link" href="<c:url value='/dashboard/interests'/>" class="btn-text">
        <spring:message code="city.back" text="Back to cities"/>
      </a>
    </div>
  </div>
</div>


<script>
  document.addEventListener("DOMContentLoaded", () => {
    const cityForm = document.getElementById("cityForm");
    const nameInput = document.getElementById("name");
    const countryInput = document.getElementById("country");
    const countrySearch = document.getElementById("countrySearch");
    const countryDropdown = document.getElementById("countryDropdown");
    const selectedCountry = document.getElementById("selectedCountry");
    const countryItems = document.querySelectorAll("#countryDropdown .autocomplete-item");

    // Focus on the first field when the page loads
    nameInput.focus();

    // Auto-capitalize first letter of each word
    function capitalizeFirstLetter(input) {
      input.addEventListener("blur", function () {
        if (this.value) {
          this.value = this.value
                  .split(" ")
                  .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
                  .join(" ");
        }
      });
    }

    capitalizeFirstLetter(nameInput);
    capitalizeFirstLetter(countrySearch);

      });
</script>

</body>
</html>