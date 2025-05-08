<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Set the locale based on the session language --%>
<c:set var="lang" value="${not empty sessionScope.lang ? sessionScope.lang : pageContext.response.locale}" />
<fmt:setLocale value="${lang}" />
<fmt:setBundle basename="i18n.messages" />

<!DOCTYPE html>
<html lang="<c:out value='${lang}'/>">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="error.${errorType}.title" /></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <style>
        body {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }
    </style>
</head>
<body>
<div class="error-container">
    <div class="error-card error-type-${errorType}">
        <div class="error-icon-container">
            <c:choose>
                <c:when test="${errorType == '404'}">
                    <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M14 3v4a1 1 0 0 0 1 1h4"></path>
                        <path d="M5 8V5a2 2 0 0 1 2-2h7l5 5v11a2 2 0 0 1-2 2h-5"></path>
                        <circle cx="8" cy="16" r="6"></circle>
                        <path d="m9.5 14.5 2.5 2.5"></path>
                        <path d="m12 14.5-2.5 2.5"></path>
                    </svg>
                </c:when>
                <c:when test="${errorType == '403'}">
                    <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                        <path d="m14.5 9-5 5"></path>
                        <path d="m9.5 9 5 5"></path>
                    </svg>
                </c:when>
                <c:when test="${errorType == '500'}">
                    <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 2.5 8.242"></path>
                        <path d="M12 12v9"></path>
                        <path d="m8 17 4 4 4-4"></path>
                    </svg>
                </c:when>
                <c:otherwise>
                    <svg class="error-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="error-code">
            <c:choose>
                <c:when test="${not empty statusCode}">
                    <c:out value="${statusCode}" />
                </c:when>
                <c:otherwise>
                    <fmt:message key="error.${errorType}.code" />
                </c:otherwise>
            </c:choose>
        </div>

        <h1 class="error-title">
            <fmt:message key="error.${errorType}.title" />
        </h1>

        <p class="error-page-message">
            <fmt:message key="error.${errorType}.message" />
        </p>

        <div class="help-section">
            <h2 class="help-title">
                <fmt:message key="error.help.title" />
            </h2>

            <ul class="help-list">
                <c:if test="${errorType == '404'}">
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.404.help.1" /></span>
                    </li>
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.404.help.2" /></span>
                    </li>
                </c:if>

                <c:if test="${errorType == '403'}">
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.403.help.1" /></span>
                    </li>
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.403.help.2" /></span>
                    </li>
                </c:if>

                <c:if test="${errorType == '500'}">
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.500.help.1" /></span>
                    </li>
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.500.help.2" /></span>
                    </li>
                </c:if>

                <c:if test="${errorType == '415'}">
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.415.help.1" /></span>
                    </li>
                </c:if>

                <c:if test="${errorType == '405'}">
                    <li class="help-item">
                        <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="m9 18 6-6-6-6"></path>
                        </svg>
                        <span><fmt:message key="error.405.help.1" /></span>
                    </li>
                </c:if>

                <li class="help-item">
                    <svg class="help-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="m9 18 6-6-6-6"></path>
                    </svg>
                    <span><fmt:message key="error.help.contact" /></span>
                </li>
            </ul>
        </div>

        <div class="error-actions">
            <a href="<c:url value='/'/>" class="primary-action">
                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
                <fmt:message key="error.action.home" />
            </a>
        </div>

        <div class="secondary-actions">
            <a href="<c:url value='/journeys'/>" class="secondary-action-link">
                <svg class="secondary-action-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                    <polyline points="9 22 9 12 15 12 15 22"></polyline>
                </svg>
                <fmt:message key="nav.journeys" />
            </a>

            <a href="<c:url value='/events'/>" class="secondary-action-link">
                <svg class="secondary-action-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                    <line x1="16" y1="2" x2="16" y2="6"></line>
                    <line x1="8" y1="2" x2="8" y2="6"></line>
                    <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                <fmt:message key="nav.events" />
            </a>

            <a href="mailto:paw.2025a.10@gmail.com" class="secondary-action-link">
                <svg class="secondary-action-icon" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M21.5 12H16c-.7 2-2 3-4 3s-3.3-1-4-3H2.5"></path>
                    <path d="M5.5 5.1L2 12v6c0 1.1.9 2 2 2h16a2 2 0 0 0 2-2v-6l-3.4-6.9A2 2 0 0 0 16.8 4H7.2a2 2 0 0-1.8 1.1z"></path>
                </svg>
                <fmt:message key="error.action.contact" />
            </a>
        </div>
    </div>
</div>
</body>
</html>