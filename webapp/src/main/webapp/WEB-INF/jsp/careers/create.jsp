<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
  <title>
    <c:choose>
      <c:when test="${isUpdate}">
        <spring:message code="editCareer.title" text="Edit Career"/>
      </c:when>
      <c:otherwise>
        <spring:message code="createCareer.title" text="Create Career"/>
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
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253" />
        </svg>
      </div>
      <h1 class="auth-title">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editCareer.title" text="Edit Career"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createCareer.title" text="Create Career"/>
          </c:otherwise>
        </c:choose>
      </h1>
      <p class="auth-subtitle">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editCareer.subtitle" text="Update career information"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createCareer.subtitle" text="Add a new career to the system"/>
          </c:otherwise>
        </c:choose>
      </p>
    </div>

    <c:choose>
      <c:when test="${isUpdate}">
        <c:url var="formAction" value="/careers/${careerId}/edit"/>
      </c:when>
      <c:otherwise>
        <c:url var="formAction" value="/careers/create"/>
      </c:otherwise>
    </c:choose>

    <form:form modelAttribute="createCareerForm" action="${formAction}" method="post" class="auth-form" id="careerForm" novalidate="true">
      <!-- Career Name Field -->
      <div class="form-group">
        <form:label path="name" cssClass="form-label required-field">
          <spring:message code="createCareer.name" text="Career Name"/>
        </form:label>
        <form:input path="name" id="name" type="text" cssClass="form-input ${not empty errors.getFieldError('name') ? 'error' : ''}" />
        <form:errors path="name" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <c:choose>
          <c:when test="${isUpdate}">
            <spring:message code="editCareer.submit" text="Update Career"/>
          </c:when>
          <c:otherwise>
            <spring:message code="createCareer.submit" text="Create Career"/>
          </c:otherwise>
        </c:choose>
      </button>
    </form:form>

    <div class="auth-footer">
      <button type="button" name="back" class="auth-link" onClick="history.back()">
        <spring:message code="career.back" text="Back to careers"/>
      </button>
    </div>
  </div>
</div>

<!-- Include JavaScript files -->
<script>
  document.addEventListener("DOMContentLoaded", () => {
    const careerForm = document.getElementById("careerForm");
    const nameInput = document.getElementById("name");

    // Focus on the field when the page loads
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

    // Form validation
    careerForm.addEventListener("submit", (event) => {
      let isValid = true;

      // Validate name
      if (!nameInput.value.trim()) {
        const errorElement = document.createElement("div");
        errorElement.className = "error-message";
        errorElement.textContent = "Career name is required";

        const existingError = nameInput.parentNode.querySelector(".error-message");
        if (!existingError) {
          nameInput.parentNode.appendChild(errorElement);
        }

        nameInput.classList.add("error");
        isValid = false;
      } else {
        nameInput.classList.remove("error");
        const existingError = nameInput.parentNode.querySelector(".error-message");
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