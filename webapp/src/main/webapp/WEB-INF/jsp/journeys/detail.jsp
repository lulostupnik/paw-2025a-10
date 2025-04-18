<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <title><spring:message code="journey.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<div class="layout-container">
    <!-- Include the sidebar component -->
    <!-- Main Content -->
    <div class="main-content">
        <jsp:include page="../components/navbar.jsp" />
        <div class="content-container">
            <!-- Back Link -->
            <div class="back-link-container">
                <a href="<c:url value="/journeys"/>" class="back-link">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
                    </svg>
                    <spring:message code="journey.back"/>
                </a>
            </div>

            <div class="detail-page-container">
                <!-- Journey Details Card -->
                <div class="detail-card">
                    <!-- Header with user info and reply button -->
                    <div class="detail-header">
                        <div class="detail-header-content">
                            <div class="user-avatar">
                                <c:if test="${not empty journey.user.profilePictureId}">
                                    <img src="<c:url value='/images/${journey.user.profilePictureId}'/>" alt="Profile" class="avatar-img">
                                </c:if>
                                <c:if test="${empty journey.user.profilePictureId}">
                                    <div class="avatar-placeholder">
                                            ${journey.user.firstname.charAt(0)}${journey.user.lastname.charAt(0)}
                                    </div>
                                </c:if>
                            </div>
                            <div class="user-info">
                                <h1 class="detail-title"><c:out value="${journey.user.firstname} ${journey.user.lastname}"/></h1>
                                <p class="detail-subtitle">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="detail-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                                    </svg>
                                    <c:out value="${journey.user.email}"/>
                                </p>
                            </div>
                        </div>
                        <div class="detail-actions">
                            <a href="<c:url value="/journeys/${journey.id}/reply"/>" class="btn-primary">
                                <svg xmlns="http://www.w3.org/2000/svg" class="btn-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                                </svg>
                                <spring:message code="journey.reply.button"/>
                            </a>
                        </div>
                    </div>

                    <!-- Destination Info -->
                    <div class="detail-section">
                        <h2 class="section-title">
                            <svg xmlns="http://www.w3.org/2000/svg" class="section-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                            </svg>
                            <spring:message code="journey.destination"/>
                        </h2>
                        <div class="destination-info">
                            <div class="destination-tags">
                                <span class="destination-tag">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="tag-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                                    </svg>
                                    <c:out value="${journey.destinationUniversity.city}"/>
                                </span>
                                <span class="destination-tag">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="tag-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                                    </svg>
                                    <c:out value="${journey.destinationUniversity.name}"/>
                                </span>
                            </div>
                        </div>
                    </div>

                    <!-- Date Info -->
                    <div class="detail-section">
                        <h2 class="section-title">
                            <svg xmlns="http://www.w3.org/2000/svg" class="section-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                            </svg>
                            <spring:message code="journey.dates"/>
                        </h2>
                        <div class="journey-dates-display">
                            <div class="date-range">
                                <div class="date-item">
                                    <span class="date-label"><spring:message code="journey.startDate"/>:</span>
<%--                                    <span class="date-value"><fmt:formatDate value="${journey.startDate}" pattern="MMMM d, yyyy" /></span>--%>
                                </div>
                                <div class="date-separator">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="date-arrow" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 8l4 4m0 0l-4 4m4-4H3" />
                                    </svg>
                                </div>
                                <div class="date-item">
<%--                                    <span class="date-label"><spring:message code="journey.endDate"/>:</span>--%>
<%--                                    <span class="date-value"><fmt:formatDate value="${journey.endDate}" pattern="MMMM d, yyyy" /></span>--%>
                                </div>
                            </div>
                            <div class="date-duration">
                                <svg xmlns="http://www.w3.org/2000/svg" class="duration-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                                </svg>
<%--                                <span class="duration-text">--%>
<%--                                    <c:set var="daysBetween" value="${(journey.endDate.time - journey.startDate.time) / (1000*60*60*24)}" />--%>
<%--&lt;%&ndash;                                    <fmt:formatNumber value="${daysBetween}" pattern="#0" /> <spring:message code="journey.days"/>&ndash;%&gt;--%>
<%--                                </span>--%>
                            </div>
                        </div>
                    </div>

                    <!-- Description -->
                    <div class="detail-section">
                        <h2 class="section-title">
                            <svg xmlns="http://www.w3.org/2000/svg" class="section-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            </svg>
                            <spring:message code="journey.description"/>
                        </h2>
                        <div class="description-content">
                            <p class="journey-description-text"><c:out value="${journey.description}"/></p>
                        </div>
                    </div>

                    <div class="event-section comments-section">
                        <h3 class="section-title">Comments</h3>
                        <c:choose>
                            <c:when test="${not empty journeyResponses}">
                                <ul class="comments-list">
                                    <c:forEach var="response" items="${journeyResponses}">
                                        <li class="comment-item">
                                            <div class="comment-header">
                                                <span class="comment-username"><c:out value="${response.username}"/></span>
                                                <span class="comment-date"><c:out value="${response.formattedDate}"/></span>

                                            </div>
                                            <p class="comment-message"><c:out value="${response.message}"/></p>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:when>
                            <c:otherwise>
                                <p class="no-comments">No comments yet. Be the first to say something!</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
