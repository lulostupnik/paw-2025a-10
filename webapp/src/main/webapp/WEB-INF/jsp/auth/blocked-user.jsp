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
    <title><spring:message code="blocked.title"/></title>
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/cards.css'/>" />
</head>
<body>
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="blocked.account"/></h1>
            <p class="auth-subtitle"><spring:message code="blocked.subtitle"/></p>
        </div>

        <div class="alert alert-error">
            <div class="alert-icon">
                <svg xmlns="http://www.w3.org/2000/svg" class="alert-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
            </div>
            <div class="alert-content">
                <h4 class="alert-title"><spring:message code="blocked.access.denied"/></h4>
                <p class="alert-message">
                    <c:choose>
                        <c:when test="${not empty reason}">
                            <c:out value="${reason}" escapeXml="true" />
                        </c:when>
                        <c:otherwise>
                            <spring:message code="blocked.default.reason"/>
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
        </div>

        <div class="form-card-content">
            <div class="blocked-info">
                <h3 class="blocked-section-title"><spring:message code="blocked.what.happened"/></h3>
                <p class="blocked-text"><spring:message code="blocked.explanation"/></p>

                <h3 class="blocked-section-title"><spring:message code="blocked.what.to.do"/></h3>
                <p class="blocked-text"><spring:message code="blocked.instructions"/></p>

                <div class="contact-info">
                    <h4 class="contact-title"><spring:message code="blocked.contact.us"/></h4>
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

        <a href="<c:url value='/'/>" class="back-button">
            <spring:message code="blocked.back.to.home"/>
        </a>
    </div>


    <c:if test="${not empty referenceId}">
        <div class="reference-id">
            <spring:message code="blocked.reference"/> <strong><c:out value="${referenceId}" escapeXml="true" /></strong>
        </div>
    </c:if>
</div>

</body>
</html>

