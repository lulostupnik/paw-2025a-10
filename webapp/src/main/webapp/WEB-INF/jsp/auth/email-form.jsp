<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="<c:out value="${pageContext.response.locale}" />">
<head>
  <title><spring:message code="forgotpassword.title"/></title>
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<div class="auth-container">
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-logo">
        <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
        </svg>
      </div>
      <h1 class="auth-title"><spring:message code="forgotpassword.title"/></h1>
      <p class="auth-subtitle"><spring:message code="forgotpassword.subtitle" text="Enter your email to reset your password"/></p>
    </div>

    <c:if test="${not empty successMessage}">
      <div class="alert alert-success">
        <div class="alert-icon">
          <svg xmlns="http://www.w3.org/2000/svg" class="alert-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <div class="alert-content">
          <p class="alert-message"><c:out value="${successMessage}" /></p>
        </div>
      </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
      <div class="alert alert-error">
        <div class="alert-icon">
          <svg xmlns="http://www.w3.org/2000/svg" class="alert-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
          </svg>
        </div>
        <div class="alert-content">
          <p class="alert-message"><c:out value="${errorMessage}" /></p>
        </div>
      </div>
    </c:if>

    <c:url var="forgotPasswordUrl" value="/forgot_pass"/>
    <form:form modelAttribute="emailForm" action="${forgotPasswordUrl}" method="post" class="auth-form">
      <div class="form-group">
        <form:label path="email" cssClass="form-label required-field">
          <spring:message code="forgotpassword.email"/>
        </form:label>
        <form:input path="email" id="forgot-email" cssClass="required form-input ${not empty errors.getFieldError('email') ? 'error' : ''}"
                    placeholder="example@email.com" />
        <form:errors path="email" cssClass="error-message" />
      </div>

      <button type="submit" class="form-button">
        <spring:message code="forgotpassword.submit" text="Reset Password"/>
      </button>
    </form:form>

    <div class="auth-footer">
      <spring:message code="forgotpassword.remember" text="Remember your password?"/>
      <a href="<c:url value='/login'/>" class="auth-link">
        <spring:message code="forgotpassword.login" text="Sign in"/>
      </a>
    </div>
  </div>
</div>
</body>
</html>