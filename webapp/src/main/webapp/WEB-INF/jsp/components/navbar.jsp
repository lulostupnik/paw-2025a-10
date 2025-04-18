<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:url var="homeUrl" value="/explore" />
<c:url var="journeysUrl" value="/journeys" />
<c:url var="eventsUrl" value="/events" />
<c:url var="exploreUrl" value="/explore" />
<c:url var="loginUrl" value="/login" />
<c:url var="registerUrl" value="/register" />
<c:url var="logoutUrl" value="/logout" />
<c:url var="profileUrl" value="/profile" />
<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri'] != null
                          ? requestScope['javax.servlet.forward.request_uri']
                          : request.requestURI}" />

<link rel="stylesheet" href="<c:url value='/resources/css/layout/navbar.css'/>" />

<header class="topbar">
    <div class="topbar-container">
        <div class="topbar-left">
            <!-- Mobile menu toggle -->
            <button id="mobile-menu-toggle" class="mobile-menu-toggle" aria-label="Toggle Menu">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-menu-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </button>

            <!-- Logo -->
            <div class="topbar-logo">
                <a href="<c:url value='/'/>">
                    <svg xmlns="http://www.w3.org/2000/svg" class="logo-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <span class="logo-text"><spring:message code="app.name"/></span>
                </a>
            </div>
        </div>

        <!-- Main Navigation -->
        <nav class="topbar-nav" id="topbar-nav">
            <div class="topbar-nav-container">
                <a href="<c:out value='${exploreUrl}'/>"
                   class="topbar-nav-item ${fn:startsWith(uri, exploreUrl) ? 'active' : ''}">
                    <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                    </svg>
                    <span><spring:message code="nav.explore"/></span>
                </a>
                <a href="<c:out value='${journeysUrl}'/>"
                   class="topbar-nav-item ${fn:startsWith(uri, journeysUrl) ? 'active' : ''}">
                    <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                    </svg>
                    <span><spring:message code="nav.journeys"/></span>
                </a>
                <a href="<c:out value='${eventsUrl}'/>"
                   class="topbar-nav-item ${fn:startsWith(uri, eventsUrl) ? 'active' : ''}">
                    <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span><spring:message code="nav.events"/></span>
                </a>
            </div>
        </nav>

        <!-- Auth Section -->
        <div class="topbar-auth">
            <c:choose>
                <c:when test="${not empty username}">
                    <!-- User is logged in - show profile -->
                    <div class="topbar-user-profile">
                        <div class="dropdown">
                            <button class="topbar-profile-button" id="profile-dropdown-toggle">
                                <div class="profile-avatar">
                                    <c:choose>
                                        <c:when test="${not empty userProfileImage}">
                                            <img src="${userProfileImage}" alt="${username}" class="avatar-image" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="avatar-placeholder">
                                                    ${fn:substring(username, 0, 1).toUpperCase()}
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <span class="profile-name">${username}</span>
                                <svg xmlns="http://www.w3.org/2000/svg" class="dropdown-arrow" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
                                </svg>
                            </button>
                            <div class="dropdown-menu" id="profile-dropdown-menu">
                                <a href="<c:out value='${profileUrl}'/>" class="dropdown-item">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="dropdown-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                    </svg>
                                    <spring:message code="nav.profile"/>
                                </a>
                                <a href="<c:out value='${logoutUrl}'/>" class="dropdown-item dropdown-item-danger">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="dropdown-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                    </svg>
                                    <spring:message code="nav.logout"/>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- User is not logged in - show login/register buttons -->
                    <div class="auth-buttons">
                        <a href="<c:url value='${loginUrl}'/>" class="btn-secondary">
                            <spring:message code="auth.login"/>
                        </a>
                        <a href="<c:url value='${registerUrl}'/>" class="btn-outline">
                            <spring:message code="auth.register"/>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Mobile Navigation Overlay -->
    <div class="mobile-nav-overlay" id="mobile-nav-overlay"></div>

    <!-- Mobile Navigation Menu -->
    <div class="mobile-nav" id="mobile-nav">
        <div class="mobile-nav-header">
            <div class="mobile-nav-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="logo-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span class="logo-text"><spring:message code="app.name"/></span>
            </div>
            <button id="mobile-nav-close" class="mobile-nav-close">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-close-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                </svg>
            </button>
        </div>
        <div class="mobile-nav-content">
            <a href="<c:out value='${homeUrl}'/>"
               class="mobile-nav-item ${uri == homeUrl ? 'active' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                </svg>
                <span><spring:message code="nav.home"/></span>
            </a>
            <a href="<c:out value='${journeysUrl}'/>"
               class="mobile-nav-item ${fn:startsWith(uri, journeysUrl) ? 'active' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                </svg>
                <span><spring:message code="nav.journeys"/></span>
            </a>
            <a href="<c:out value='${eventsUrl}'/>"
               class="mobile-nav-item ${fn:startsWith(uri, eventsUrl) ? 'active' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
                <span><spring:message code="nav.events"/></span>
            </a>
            <a href="<c:out value='${aboutUrl}'/>"
               class="mobile-nav-item ${fn:startsWith(uri, aboutUrl) ? 'active' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                <span><spring:message code="nav.about"/></span>
            </a>

            <c:if test="${not empty username}">
                <a href="<c:out value='${profileUrl}'/>"
                   class="mobile-nav-item ${fn:startsWith(uri, profileUrl) ? 'active' : ''}">
                    <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                    </svg>
                    <span><spring:message code="nav.profile"/></span>
                </a>
            </c:if>
        </div>
        <div class="mobile-nav-footer">
            <c:choose>
                <c:when test="${not empty username}">
                    <a href="<c:out value='${logoutUrl}'/>" class="mobile-nav-logout">
                        <svg xmlns="http://www.w3.org/2000/svg" class="mobile-nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                        </svg>
                        <span><spring:message code="nav.logout"/></span>
                    </a>
                </c:when>
                <c:otherwise>
                    <div class="mobile-auth-buttons">
                        <a href="<c:url value='${loginUrl}'/>" class="btn-secondary mobile-btn">
                            <spring:message code="auth.login"/>
                        </a>
                        <a href="<c:url value='${registerUrl}'/>" class="btn-outline mobile-btn">
                            <spring:message code="auth.register"/>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>

<script src="<c:url value='/resources/js/navbar.js'/>"></script>