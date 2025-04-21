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
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>

<body>
<div class="layout-container">
    <!-- Include the sidebar component -->

    <!-- Main Content -->
    <div class="main-content">
        <jsp:include page="components/navbar.jsp" />
        <div class="content-container">
            <div class="header-container">
                <h2 class="page-title"><spring:message code="nav.explore"/></h2>
            </div>

<%--            <!-- Hero Section -->--%>
<%--            <div class="hero-section">--%>
<%--                <div class="hero-content">--%>
<%--                    <h1 class="hero-title">--%>
<%--                        <spring:message code="dashboard.welcome"/>--%>
<%--                    </h1>--%>
<%--                    <p class="hero-description">--%>
<%--                        <spring:message code="dashboard.subtitle" />--%>
<%--                    </p>--%>
<%--                </div>--%>
<%--            </div>--%>

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
                            <c:if test="${hasJourney == false}">
                                <a href="<c:url value='/journeys/create'/>" class="empty-action-btn">
                                    <spring:message code="dashboard.create.journey" />
                                </a>
                            </c:if>
                        </div>
                    </c:if>

                    <c:if test="${not empty journeys}">
                        <c:forEach var="journey" items="${journeys}" varStatus="status">
                            <jsp:include page="journeys/journey-card.jsp">
                                <jsp:param name="journeyId" value="${journey.id}" />
                                <jsp:param name="city" value="${journey.destinationUniversity.city.name}" />
                                <jsp:param name="startDate" value="${journey.startDate}" />
                                <jsp:param name="endDate" value="${journey.endDate}" />
                                <jsp:param name="description" value="${journey.description}" />
                                <jsp:param name="profilePictureId" value="${journey.user.profilePictureId}" />
                                <jsp:param name="userName" value="${journey.user.username}" />
                                <jsp:param name="firstname" value="${journey.user.firstname}" />
                                <jsp:param name="lastname" value="${journey.user.lastname}"/>
                                <jsp:param name="country" value="${journey.destinationUniversity.city.country}"/>
                                <jsp:param name="university" value="${journey.destinationUniversity.name}"/>
                            </jsp:include>
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
                        <c:forEach items="${events}" var="event">
                            <c:set var="attend" value="false" />
                            <c:forEach items="${eventsAttended}" var="attendedEvent">
                                <c:if test="${attendedEvent.id == event.id}">
                                    <c:set var="attend" value="true" />
                                </c:if>
                            </c:forEach>
                            <jsp:include page="events/event-card.jsp">
                                <jsp:param name="eventId" value="${event.id}" />
                                <jsp:param name="city" value="${event.eventCity.name}" />
                                <jsp:param name="date" value="${event.date}" />
                                <jsp:param name="description" value="${event.description}" />
                                <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
                                <jsp:param name="attend" value="${attend}" />
                                <jsp:param name="firstname" value="${event.user.firstname}" />
                                <jsp:param name="lastname" value="${event.user.lastname}"/>
                                <jsp:param name="title" value="${event.title}"/>
                            </jsp:include>
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
