<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
  <title>
    <c:choose>
      <c:when test="${isUpdate}">
        <spring:message code="editInterest.title" text="Edit Interest"/>
      </c:when>
      <c:otherwise>
        <spring:message code="createInterest.title" text="Create Interest"/>
      </c:otherwise>
    </c:choose>
  </title>
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
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
        </svg>
      </div>
      <h1 class="auth-title">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editInterest.title" text="Edit Interest"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createInterest.title" text="Create Interest"/>
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="auth-subtitle">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editInterest.subtitle" text="Update interest information"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createInterest.subtitle" text="Add a new interest to the system"/>
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

    <form:form modelAttribute="createInterestForm" action="${formAction}" method="post" class="auth-form" id="interestForm" novalidate="true">
      <!-- English Name Field -->
      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createInterest.name" text="English Name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editInterest.submit" text="Update Interest"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createInterest.submit" text="Create Interest"/>
          </c:otherwise>
        </c:choose>
      </button>
    </form:form>

    <div class="auth-footer">
      <button type="button" name="back" class="auth-link" onClick="history.back()">
        <spring:message code="interest.back" text="Back to interests"/>
      </button>
    </div>
  </div>
</div>

<!-- Include JavaScript files -->
<script>
  document.addEventListener("DOMContentLoaded", () => {
    const interestForm = document.getElementById("interestForm");
    const nameEnInput = document.getElementById("name");

    // Focus on the first field when the page loads
    nameEnInput.focus();

    // Optional: Auto-capitalize first letter of each word
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

    capitalizeFirstLetter(nameEnInput);

    // Optional: Form validation
    interestForm.addEventListener("submit", (event) => {
      let isValid = true;

      // Validate English name
      if (!nameEnInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "English name is required";

        const existingError = nameEnInput.parentNode.querySelector(".error-message");
        if (!existingError) {
          nameEnInput.parentNode.appendChild(errorElement);
        }

        nameEnInput.classList.add("error");
        isValid = false;
      } else {
        nameEnInput.classList.remove("error");
        const existingError = nameEnInput.parentNode.querySelector(".error-message");
        if (existingError) {
          existingError.remove();
        }
      }

      if (!isValid) {
        event.preventDefault();
      }
    });
  });
</script>

</body>
</html>