<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="app.title"/></title>
    <link href="<c:url value='/resources/css/index.css' />" rel="stylesheet"/>
    <link href="<c:url value='/resources/css/sidebar.css' />" rel="stylesheet"/>
</head>

<body>
<div class="layout-container">
    <!-- Include the sidebar component -->
    <jsp:include page="components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <!-- Welcome Header -->
            <div class="welcome-header">
                <h1 class="page-title">
                    <spring:message code="dashboard.welcome"/>
                </h1>

            </div>

            <!-- Content Grid -->
            <div class="content-grid">
                <!-- Newest Journeys -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-header-content">
                            <h2 class="card-title">
                                <spring:message code="dashboard.newest.journeys"/>
                            </h2>
                            <a href="<c:url value='/journeys'/>" class="view-all-link">
                                <spring:message code="dashboard.view.all" />
                            </a>
                        </div>
                        <p class="card-description">
                            <spring:message code="dashboard.newest.journeys.desc"/>
                        </p>
                    </div>

                    <!-- Journey List -->
                    <div class="list-container">
                        <c:if test="${empty journeys}">
                            <div class="empty-list">
                                <p class="empty-message">
                                    <spring:message code="dashboard.no.journeys"/>
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty journeys}">
                            <c:forEach var="journey" items="${journeys}" varStatus="status">
                                <div class="list-item">
                                    <div class="list-item-content">
                                        <div>
                                            <h3 class="item-title">
                                                <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                            </h3>
                                            <p class="item-subtitle">
                                                <c:out value="${journey.destinationUniversity.city}" /> -
                                                <c:out value="${journey.destinationUniversity}" />
                                            </p>
                                            <p class="item-details">
                                                <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                            </p>
                                        </div>
                                        <a href="<c:url value='/journeys/${journey.id}'/>" class="details-link">
                                            <spring:message code="dashboard.details"/>
                                            <svg class="arrow-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                            </svg>
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>

                <!-- Newest Events -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-header-content">
                            <h2 class="card-title">
                                <spring:message code="dashboard.newest.events"/>
                            </h2>
                            <a href="<c:url value='/events'/>" class="view-all-link">
                                <spring:message code="dashboard.view.all"/>
                            </a>
                        </div>
                        <p class="card-description">
                            <spring:message code="dashboard.newest.events.desc" />
                        </p>
                    </div>

                    <!-- Events List -->
                    <div class="list-container">
                        <c:if test="${empty events}">
                            <div class="empty-list">
                                <p class="empty-message">
                                    <spring:message code="dashboard.no.events"/>
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty events}">
                            <c:forEach var="event" items="${events}" varStatus="status">
                                <div class="list-item">
                                    <div class="list-item-content">
                                        <div>
                                            <h3 class="item-title">
                                                <c:out value="${event.eventCity}" />
                                            </h3>
                                            <p class="item-subtitle">
                                                <c:out value="${event.date}" />
                                            </p>
                                            <p class="item-details item-description">
                                                <c:out value="${event.description}" />
                                            </p>
                                        </div>
                                        <a href="<c:url value='/events/${event.id}'/>" class="details-link">
                                            <spring:message code="dashboard.details"/>
                                            <svg class="arrow-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                            </svg>
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>