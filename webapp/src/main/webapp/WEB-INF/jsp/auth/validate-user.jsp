<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<link rel="stylesheet" href="<c:url value='/resources/css/blocked.css'/>" />

<html lang="<c:out value="${pageContext.response.locale}" />">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title><spring:message code="validated.title"/></title>
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/cards.css'/>" />
</head>
<body>
<div class="auth-container">
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-logo success">
        <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
      </div>
      <h1 class="auth-title success-text"><spring:message code="validated.header"/></h1>
      <p class="auth-subtitle"><spring:message code="validated.subtitle"/></p>
    </div>

    <div class="alert alert-success">
      <div class="alert-icon">
        <svg xmlns="http://www.w3.org/2000/svg" class="alert-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
      </div>
      <div class="alert-content">
        <h4 class="alert-title"><spring:message code="validated.alert.title"/></h4>
        <p class="alert-message">
          <spring:message code="validated.alert.message"/>
          <c:if test="${not empty user}">
            <strong><c:out value="${user.username}" escapeXml="true" /></strong>
          </c:if>
        </p>
      </div>
    </div>

    <div class="form-card-content">
      <div class="validated-info">
        <h3 class="validated-section-title"><spring:message code="validated.what.next"/></h3>
        <p class="validated-text"><spring:message code="validated.next.steps"/></p>

        <div class="features-list">
          <div class="feature-item">
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 12h.01M12 12h.01M19 12h.01M6 12a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0zm7 0a1 1 0 11-2 0 1 1 0 012 0z" />
            </svg>
            <div class="feature-text">
              <h4 class="feature-title"><spring:message code="validated.feature1.title"/></h4>
              <p class="feature-description"><spring:message code="validated.feature1.description"/></p>
            </div>
          </div>
          <div class="feature-item">
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
            <div class="feature-text">
              <h4 class="feature-title"><spring:message code="validated.feature2.title"/></h4>
              <p class="feature-description"><spring:message code="validated.feature2.description"/></p>
            </div>
          </div>
          <div class="feature-item">
            <svg xmlns="http://www.w3.org/2000/svg" class="feature-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            <div class="feature-text">
              <h4 class="feature-title"><spring:message code="validated.feature3.title"/></h4>
              <p class="feature-description"><spring:message code="validated.feature3.description"/></p>
            </div>
          </div>
        </div>

        <div class="help-section">
          <h4 class="help-title"><spring:message code="validated.help.title"/></h4>
          <p class="help-text"><spring:message code="validated.help.text"/></p>
          <div class="contact-method">
            <svg xmlns="http://www.w3.org/2000/svg" class="contact-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
            </svg>
            <a href="mailto:${email}" class="contact-link">
              <c:out value="${email}" escapeXml="true" />
            </a>
          </div>
        </div>
      </div>
    </div>

    <a href="<c:url value='/explore'/>" class="continue-button">
      <spring:message code="validated.continue.to.dashboard"/>
    </a>
  </div>

  <!-- Validation timestamp -->
  <c:if test="${not empty validationTime}">
    <div class="validation-time">
      <spring:message code="validated.timestamp"/> <strong><c:out value="${validationTime}" escapeXml="true" /></strong>
    </div>
  </c:if>
</div>

</body>
</html>