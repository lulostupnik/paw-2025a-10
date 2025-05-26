<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:url var="journeysUrl" value="/journeys" />
<c:url var="eventsUrl" value="/events" />
<c:url var="exploreUrl" value="/explore" />
<c:url var="loginUrl" value="/login" />
<c:url var="registerUrl" value="/register" />
<c:url var="logoutUrl" value="/logout" />
<c:url var="profileUrl" value="/profile/info" />
<c:url var="dashboardJourneysUrl" value="/dashboard/journeys" />
<c:url var="dashboardEventsUrl" value="/dashboard/events" />
<c:url var="dashboardUsersUrl" value="/dashboard/users" />
<c:url var="dashboardInterestsUrl" value="/dashboard/interests" />
<c:url var="dashboardCitiesUrl" value="/dashboard/cities" />
<c:url var="dashboardUniversitiesUrl" value="/dashboard/universities" />
<c:url var="dashboardCareersUrl" value="/dashboard/careers" />
<c:url var="careersUrl" value="/careers" />
<c:url var="interestsUrl" value="/interests" />
<c:url var="citiesUrl" value="/cities" />
<c:url var="universitiesUrl" value="/universities" />
<c:url var="usersUrl" value="/users" />
<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri'] != null
                          ? requestScope['javax.servlet.forward.request_uri']
                          : request.requestURI}" />



<c:set var="isAdminSectionActive"
       value="${fn:startsWith(uri, dashboardJourneysUrl)
            or fn:startsWith(uri, dashboardEventsUrl)
            or fn:startsWith(uri, dashboardUsersUrl)
            or fn:startsWith(uri, dashboardInterestsUrl)
            or fn:startsWith(uri, dashboardCitiesUrl)
            or fn:startsWith(uri, dashboardUniversitiesUrl)
            or fn:startsWith(uri, dashboardCareersUrl)
            or fn:startsWith(uri, careersUrl)
            or fn:startsWith(uri, interestsUrl)
            or fn:startsWith(uri, citiesUrl)
            or fn:startsWith(uri, usersUrl)
            or fn:startsWith(uri, universitiesUrl)}" />

<link rel="stylesheet" href="<c:url value='/resources/css/layout/navbar.css'/>" />

<header class="topbar">
    <div class="topbar-container">
        <div class="topbar-left">

            <button id="mobile-menu-toggle" class="mobile-menu-toggle" aria-label="Toggle Menu">
                <svg xmlns="http://www.w3.org/2000/svg" class="mobile-menu-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16" />
                </svg>
            </button>


            <div class="topbar-logo">
                <c:choose>
                <c:when test="${not empty user}">
                <a href="<c:url value='/explore'/>">
                    </c:when>
                    <c:otherwise>
                    <a href="<c:url value='/'/>">
                        </c:otherwise>
                        </c:choose>
                        <svg xmlns="http://www.w3.org/2000/svg" class="logo-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <span class="logo-text"><spring:message code="app.name"/></span>
                    </a>
            </div>

        </div>


        <nav class="topbar-nav" id="topbar-nav">
            <div class="topbar-nav-container">
                <c:if test="${ not empty user}">
                    <a href="<c:out value='${exploreUrl}'/>"
                       class="topbar-nav-item ${fn:startsWith(uri, exploreUrl) ? 'active' : ''}">
                        <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
                        </svg>
                        <span><spring:message code="nav.explore"/></span>
                    </a>
                </c:if>
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
                <sec:authorize access="hasRole('ADMIN')">
                    <a href="<c:out value="${dashboardJourneysUrl}"/>"
                       class="topbar-nav-item ${isAdminSectionActive ? 'active' : ''}">
                        <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                  d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
                        </svg>
                        <span><spring:message code="nav.dash"/></span>
                    </a>
                </sec:authorize>

            </div>
        </nav>


        <div class="topbar-auth">
            <c:choose>
                <c:when test="${not empty user}">

                    <div class="topbar-user-profile">
                        <div class="dropdown">
                            <button class="topbar-profile-button" id="profile-dropdown-toggle">
                                <span class="profile-avatar">
                                    <c:choose>
                                        <c:when test="${not empty user.profilePictureId && user.profilePictureId > 0}">
                                            <img src="<c:url value='/images/${user.profilePictureId}'/>" alt="<c:out value="${user.username}"/>" class="avatar-image" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="avatar-placeholder-navbar">
                                                    ${fn:substring(user.getUsername(), 0, 1).toUpperCase()}
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                                <span class="profile-name"><c:out value="${user.getUsername()}"/></span>
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

                    <div class="auth-buttons">
                        <a href="<c:out value='${loginUrl}'/>" class="btn-primary">
                            <spring:message code="auth.login"/>
                        </a>
                        <a href="<c:out value='${registerUrl}'/>" class="btn-outline">
                            <spring:message code="auth.register"/>
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>


    <div class="mobile-nav-overlay" id="mobile-nav-overlay"></div>


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
            <c:if test="${ not empty user}">
            <a href="<c:out value='${exploreUrl}'/>"
               class="topbar-nav-item ${fn:startsWith(uri, exploreUrl) ? 'active' : ''}">
                <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
                </svg>
                <span><spring:message code="nav.explore"/></span>
            </a>
            </c:if>
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
            <sec:authorize access="hasRole('ADMIN')">
                <a href="<c:out value="${dashboardJourneysUrl}"/>"
                   class="topbar-nav-item ${isAdminSectionActive ? 'active' : ''}">
                    <svg xmlns="http://www.w3.org/2000/svg" class="nav-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                              d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
                    </svg>
                    <span><spring:message code="nav.dash"/></span>
                </a>
            </sec:authorize>
        </div>






















    </div>
</header>

<script src="<c:url value='/resources/js/components/navbar.js'/>"></script>