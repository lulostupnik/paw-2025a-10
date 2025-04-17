<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="app.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
</head>

<body>
<div class="layout-container">
    <!-- Include the sidebar component -->
    <jsp:include page="components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <!-- Hero Section -->
            <div class="hero-section">
                <div class="hero-content">
                    <h1 class="hero-title">
                        <spring:message code="dashboard.welcome"/>
                    </h1>
                    <p class="hero-description">
                        <spring:message code="dashboard.subtitle" />
                    </p>
                </div>
            </div>

            <!-- Recommended Journeys Section -->
            <section class="content-section">
                <div class="section-header">
                    <h2 class="section-title">
                        <spring:message code="dashboard.recommended.journeys" />
                    </h2>
                    <a href="<c:url value='/journeys'/>" class="view-all-link">
                        <spring:message code="dashboard.view.all" />
                        <svg xmlns="http://www.w3.org/2000/svg" class="view-all-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                        </svg>
                    </a>
                </div>
                <p class="section-description">
                    <spring:message code="dashboard.recommended.journeys.desc" />
                </p>

                <!-- Recommended Journeys Cards -->
                <div class="cards-grid">
                    <c:if test="${empty journeys}">
                        <div class="empty-state">
                            <div class="empty-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                                </svg>
                            </div>
                            <p class="empty-message">
                                <spring:message code="dashboard.no.journeys"/>
                            </p>
                            <a href="<c:url value='/journeys/create'/>" class="empty-action-btn">
                                <spring:message code="dashboard.create.journey" />
                            </a>
                        </div>
                    </c:if>

                    <c:if test="${not empty journeys}">
                        <c:forEach var="journey" items="${journeys}" varStatus="status">
                            <div class="card journey-card">
                                <div class="card-header">
                                    <div class="card-avatar">
                                        <c:if test="${not empty journey.user.profilePictureId}">
                                            <img src="<c:url value='/images/${journey.user.profilePictureId}'/>" alt="Profile" class="avatar-img">
                                        </c:if>
                                        <c:if test="${empty journey.user.profilePictureId}">
                                            <div class="avatar-placeholder">
                                                    ${fn:substring(journey.user.firstname, 0, 1)}${fn:substring(journey.user.lastname, 0, 1)}
                                            </div>
                                        </c:if>
                                    </div>
                                    <div class="card-meta">
                                        <h3 class="card-title">
                                            <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                        </h3>
                                        <p class="card-subtitle">
                                            <c:out value="${journey.destinationUniversity.city}" /> -
                                            <c:out value="${journey.destinationUniversity.name}" />
                                        </p>
                                    </div>
                                </div>
                                <div class="card-body">
                                    <div class="card-dates">
                                        <svg xmlns="http://www.w3.org/2000/svg" class="card-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                        <span class="card-date-range">
                                            <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                        </span>
                                    </div>
                                    <p class="card-description">
                                        <c:out value="${journey.description}" />
                                    </p>
                                </div>
                                <div class="card-footer">
                                    <a href="<c:url value='/journeys/${journey.id}'/>" class="card-btn">
                                        <spring:message code="dashboard.details"/>
                                        <svg class="card-btn-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                        </svg>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>
                </div>
            </section>

            <!-- Recommended Events Section -->
            <section class="content-section">
                <div class="section-header">
                    <h2 class="section-title">
                        <spring:message code="dashboard.recommended.events"/>
                    </h2>
                    <a href="<c:url value='/events'/>" class="view-all-link">
                        <spring:message code="dashboard.view.all"/>
                        <svg xmlns="http://www.w3.org/2000/svg" class="view-all-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                        </svg>
                    </a>
                </div>
                <p class="section-description">
                    <spring:message code="dashboard.recommended.events.desc"/>
                </p>

                <!-- Recommended Events Cards -->
                <div class="cards-grid">
                    <c:if test="${empty events}">
                        <div class="empty-state">
                            <div class="empty-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                                </svg>
                            </div>
                            <p class="empty-message">
                                <spring:message code="dashboard.no.events"/>
                            </p>
                            <a href="<c:url value='/events/create'/>" class="empty-action-btn">
                                <spring:message code="dashboard.create.event"/>
                            </a>
                        </div>
                    </c:if>

                    <c:if test="${not empty events}">
                        <c:forEach var="event" items="${events}" varStatus="status">
                            <div class="card event-card">
                                <c:if test="${not empty event.flyerImageId}">
                                    <div class="card-image">
                                        <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event Flyer" class="event-img">
                                    </div>
                                </c:if>
                                <div class="card-body">
                                    <h3 class="card-title">
                                        <c:out value="${event.eventCity.name}" />
                                    </h3>
                                    <div class="card-dates">
                                        <svg xmlns="http://www.w3.org/2000/svg" class="card-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                        </svg>
                                        <span class="card-date">
                                            <c:out value="${event.date}" />
                                        </span>
                                    </div>
                                    <p class="card-description">
                                        <c:out value="${event.description}" />
                                    </p>
                                </div>
                                <div class="card-footer">
                                    <a href="<c:url value='/events/${event.id}'/>" class="card-btn">
                                        <spring:message code="dashboard.details"/>
                                        <svg class="card-btn-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                        </svg>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:if>
                </div>
            </section>

            <!-- Quick Actions Section -->
            <section class="quick-actions-section">
                <h2 class="section-title">
                    <spring:message code="dashboard.quick.actions"/>
                </h2>
                <div class="quick-actions-grid">
                    <a href="<c:url value='/journeys/create'/>" class="quick-action-card">
                        <div class="quick-action-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" class="quick-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                            </svg>
                        </div>
                        <h3 class="quick-action-title">
                            <spring:message code="dashboard.create.journey"/>
                        </h3>
                        <p class="quick-action-desc">
                            <spring:message code="dashboard.create.journey.desc"/>
                        </p>
                    </a>
                    <a href="<c:url value='/events/create'/>" class="quick-action-card">
                        <div class="quick-action-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" class="quick-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                            </svg>
                        </div>
                        <h3 class="quick-action-title">
                            <spring:message code="dashboard.create.event" />
                        </h3>
                        <p class="quick-action-desc">
                            <spring:message code="dashboard.create.event.desc"/>
                        </p>
                    </a>
                    <a href="<c:url value='/journeys'/>" class="quick-action-card">
                        <div class="quick-action-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" class="quick-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </div>
                        <h3 class="quick-action-title">
                            <spring:message code="dashboard.explore.journeys"/>
                        </h3>
                        <p class="quick-action-desc">
                            <spring:message code="dashboard.explore.journeys.desc"/>
                        </p>
                    </a>
                    <a href="<c:url value='/events'/>" class="quick-action-card">
                        <div class="quick-action-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" class="quick-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </div>
                        <h3 class="quick-action-title">
                            <spring:message code="dashboard.explore.events" />
                        </h3>
                        <p class="quick-action-desc">
                            <spring:message code="dashboard.explore.events.desc" />
                        </p>
                    </a>
                </div>
            </section>
        </div>
    </div>
</div>
</body>
</html>
