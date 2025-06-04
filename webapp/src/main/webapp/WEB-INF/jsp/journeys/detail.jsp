<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="journey.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
    <link rel="stylesheet" href="<c:url value="/resources/css/event-detail.css"/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <script src="<c:url value='/resources/js/confirm-delete.js'/>"></script>

</head>

<body>
<c:set var="interestPageSize" value="8" scope="request" />
<c:set var="chatPageSize" value="4" scope="request" />
<c:set var="eventsPageSize" value="6" scope="request" />

<div style="display: none;">
    <span id="i18n-journey.confirmDelete" data-message="<spring:message code='journey.confirmDelete' />"></span>
    <span id="i18n-journey.deleteWarning" data-message="<spring:message code='journey.deleteWarning' />"></span>
    <span id="i18n-journeyResponse.confirmDelete" data-message="<spring:message code='journeyResponse.confirmDelete' />"></span>
    <span id="i18n-journeyResponse.deleteWarning" data-message="<spring:message code='journeyResponse.deleteWarning' />"></span>
</div>

<div class="layout-container">
    <div class="main-content">
        <jsp:include page="../components/navbar.jsp" />
        <div class="content-container">

            <div class="back-navigation">
                <c:if test="${isOwner}">
                    <button onclick="goBack()" class="back-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <path d="M19 12H5"></path>
                            <path d="M12 19l-7-7 7-7"></path>
                        </svg>
                        <span><spring:message code="journey.detail.back.to.profile" /></span>
                    </button>
                </c:if>
                <c:if test="${not isOwner}">
                    <button onclick="goBack()" class="back-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                            <path d="M19 12H5"></path>
                            <path d="M12 19l-7-7 7-7"></path>
                        </svg>
                        <span><spring:message code="journey.detail.back.to.list" /></span>
                    </button>
                </c:if>
            </div>

            <div class="content-card journey-detail-card">

                <div class="journey-actions">
                    <div style="position: relative; display: inline-block;">
                        <button onclick="toggleActionMenu()" class="btn-action btn-menu" id="actionMenuButton" style="background: none; border: 1px solid #e0e0e0; border-radius: 6px; padding: 8px; cursor: pointer;">
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                                <circle cx="12" cy="12" r="1"></circle>
                                <circle cx="12" cy="5" r="1"></circle>
                                <circle cx="12" cy="19" r="1"></circle>
                            </svg>
                        </button>

                        <div id="actionDropdown" style="display: none; position: absolute; right: 0; top: 100%; background-color: white; min-width: 180px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); border-radius: 8px; z-index: 1000; border: 1px solid #e0e0e0; padding: 8px 0;">
                            <c:if test="${isOwner}">
                                <a href="<c:url value='/journeys/${journey.id}/update'/>" style="color: #333; padding: 12px 16px; text-decoration: none; display: flex; align-items: center; gap: 12px;" onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                    <span><spring:message code="journey.edit" text="Edit Journey" /></span>
                                </a>
                            </c:if>

                            <c:if test="${isOwner || pageContext.request.isUserInRole('ADMIN')}">
                                <c:url var="deleteUrl" value='/journeys/${journey.id}/delete'/>
                                <a href="<c:out value='${deleteUrl}'/>" style="color: #333; padding: 12px 16px; text-decoration: none; display: flex; align-items: center; gap: 12px;" onmouseover="this.style.backgroundColor='#fef2f2'; this.style.color='#dc2626'" onmouseout="this.style.backgroundColor='transparent'; this.style.color='#333'">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M3 6h18"></path>
                                        <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                        <line x1="10" y1="11" x2="10" y2="17"></line>
                                        <line x1="14" y1="11" x2="14" y2="17"></line>
                                    </svg>
                                    <span><spring:message code="journey.delete" text="Delete Journey" /></span>
                                </a>
                            </c:if>

                            <c:if test="${!isOwner}">
                                <c:url var="reportJourneyUrl" value='/reports/journeys/${journey.id}/create'/>
                                <a href="${reportJourneyUrl}"
                                   style="color: #333; padding: 12px 16px; text-decoration: none; display: flex; align-items: center; gap: 12px;"
                                   onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M12 9v4"></path>
                                        <path d="M12 17h.01"></path>
                                        <circle cx="12" cy="12" r="10"></circle>
                                    </svg>
                                    <span><spring:message code="journey.report" text="Report Journey" /></span>
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="journey-detail-header">



                    <div class="journey-user-info">
                        <div class="user-details">
                            <c:set var="escapedFirstname"><c:out value="${journey.user.firstname}"/></c:set>
                            <c:set var="escapedLastname"><c:out value="${journey.user.lastname}"/></c:set>
                            <h1 class="journey-title">
                                <spring:message arguments="${escapedFirstname},${escapedLastname}" code="journey.detail.section.title" />
                            </h1>
                            <div class="journey-meta">
                                <div class="journey-destination">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                        <circle cx="12" cy="10" r="3"></circle>
                                    </svg>
                                    <span class="destination-text">
                                        <c:out value="${journey.destinationUniversity.city}" /> -
                                        <c:out value="${journey.destinationUniversity.name}" />
                                    </span>
                                </div>
                                <div class="journey-dates">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                    </svg>
                                    <fmt:parseDate value="${journey.startDate}" pattern="yyyy-MM-dd" var="parsedStartDate" />
                                    <fmt:parseDate value="${journey.endDate}" pattern="yyyy-MM-dd" var="parsedEndDate" />
                                    <fmt:formatDate value="${parsedStartDate}" pattern="MMMM d, yyyy" var="formattedStartDate" />
                                    <fmt:formatDate value="${parsedEndDate}" pattern="MMMM d, yyyy" var="formattedEndDate" />
                                    <span class="date-range">
                                        <c:out value="${formattedStartDate}" /> → <c:out value="${formattedEndDate}" />
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                    <c:set var="creatorUser" value="${journey.user}" scope="request" />
                    <c:set var="creatorShowName" value="false" scope="request" />
                    <c:set var="isJourneyCreator" value="true" scope="request" />


                    <jsp:include page="/WEB-INF/jsp/components/creator.jsp" />

                    <div class="section-content">
                        <div class="journey-description-card">
                            <p class="journey-description-text"><c:out value="${journey.description}" /></p>
                        </div>
                    </div>

                </div>

                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                            </svg>
                            <spring:message code="journey.detail.interests" text="Interests" />
                        </h2>
                    </div>
                    <div class="section-content">
                        <c:if test="${empty interestPage.content}">
                            <div class="empty-state">
                                <div class="empty-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
                                        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                                    </svg>
                                </div>
                                <p class="empty-message">
                                    <spring:message code="journey.detail.no.interests" text="No interests to display" />
                                </p>
                            </div>
                        </c:if>
                        <c:if test="${not empty interestPage.content}">
                            <div class="interests-container">
                                <c:forEach var="userInterest" items="${interestPage.content}">
                                    <div class="interest-tag">
                                        <c:out value="${userInterest.interest}" />
                                    </div>
                                </c:forEach>
                            </div>
                            <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                                <jsp:param name="pageObjectTotalPages" value="${interestPage.totalPages}" />
                                <jsp:param name="currentPage" value="${interestPage.currentPage}" />
                                <jsp:param name="pageSize" value="${interestPageSize}" />
                                <jsp:param name="baseUrl" value="/journeys/${journey.id}?page=${journeyResponsesPage.currentPage}&size=${chatPageSize}&eventsPage=${eventsPage.currentPage}&eventsSize=${eventsPageSize}" />
                                <jsp:param name="paramName" value="interestsPage" />
                                <jsp:param name="sizeParamName" value="interestsSize" />
                            </jsp:include>
                        </c:if>

                    </div>
                </section>

                <!-- Events Section -->
                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                <line x1="3" y1="10" x2="21" y2="10"></line>
                            </svg>
                            <spring:message code="journey.detail.events" text="Events" />
                            <span class="count">(<c:out value="${eventsPage.totalPages}" />)</span>
                        </h2>
                        <button onclick="toggleEvents()" class="toggle-comments-btn" aria-label="Toggle events">
                            <span id="events-collapse-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                    <polyline points="18 15 12 9 6 15"></polyline>
                                </svg>
                            </span>
                            <span id="events-expand-icon" style="display: none;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                    <polyline points="6 9 12 15 18 9"></polyline>
                                </svg>
                            </span>
                        </button>
                    </div>

                    <div id="events-list" class="section-content events-list">
                        <c:if test="${empty eventsPage.content}">
                            <div class="empty-state">
                                <div class="empty-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                    </svg>
                                </div>
                                <p class="empty-message">
                                    <spring:message code="journey.detail.no.events" text="No events during this journey" />
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty eventsPage.content}">
                            <div class="journey-events-container">
                                <c:forEach var="event" items="${eventsPage.content}">
                                    <a href="<c:url value='/events/${event.id}'/>" class="journey-event-card-link">
                                        <div class="journey-event-card">
                                            <div class="journey-event-left">
                                                <c:if test="${not empty event.flyerImageId}">
                                                    <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event flyer" class="journey-event-image">
                                                </c:if>
                                                <c:if test="${empty event.flyerImageId}">
                                                    <div class="journey-event-image-placeholder">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                                                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                            <line x1="16" y1="2" x2="16" y2="6"></line>
                                                            <line x1="8" y1="2" x2="8" y2="6"></line>
                                                            <line x1="3" y1="10" x2="21" y2="10"></line>
                                                        </svg>
                                                    </div>
                                                </c:if>
                                            </div>
                                            <div class="journey-event-content">
                                                <h3 class="journey-event-title"><c:out value="${event.title}" /></h3>
                                                <div class="journey-event-meta">
                                                    <div class="journey-event-meta-item">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                                            <circle cx="12" cy="10" r="3"></circle>
                                                        </svg>
                                                        <span><c:out value="${event.city.name}" /></span>
                                                    </div>
                                                    <div class="journey-event-meta-item">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                                            <line x1="16" y1="2" x2="16" y2="6"></line>
                                                            <line x1="8" y1="2" x2="8" y2="6"></line>
                                                            <line x1="3" y1="10" x2="21" y2="10"></line>
                                                        </svg>
                                                        <fmt:parseDate value="${event.date}" pattern="yyyy-MM-dd" var="parsedEventDate" />
                                                        <fmt:formatDate value="${parsedEventDate}" pattern="MMM d, yyyy" var="formattedEventDate" />
                                                        <span><c:out value="${formattedEventDate}" /></span>
                                                    </div>
                                                    <c:if test="${not empty event.time}">
                                                        <div class="journey-event-meta-item">
                                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                <circle cx="12" cy="12" r="10"></circle>
                                                                <polyline points="12 6 12 12 16 14"></polyline>
                                                            </svg>
                                                            <span><c:out value="${event.time}" /></span>
                                                        </div>
                                                    </c:if>
                                                </div>
                                                <p class="journey-event-description"><c:out value="${event.description}" /></p>
                                            </div>
                                            <div class="journey-event-arrow">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                    <polyline points="9 18 15 12 9 6"></polyline>
                                                </svg>
                                            </div>
                                        </div>
                                    </a>
                                </c:forEach>
                            </div>
                            <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                                <jsp:param name="pageObjectTotalPages" value="${eventsPage.totalPages}" />
                                <jsp:param name="currentPage" value="${eventsPage.currentPage}" />
                                <jsp:param name="pageSize" value="${eventsPageSize}" />
                                <jsp:param name="baseUrl" value="/journeys/${journey.id}?page=${journeyResponsesPage.currentPage}&size=${chatPageSize}&interestsPage=${interestPage.currentPage}&interestsSize=${interestPageSize}" />
                                <jsp:param name="paramName" value="eventsPage" />
                                <jsp:param name="sizeParamName" value="eventsSize" />
                            </jsp:include>
                        </c:if>
                    </div>
                </section>

                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                            </svg>
                            <spring:message code="journey.detail.responses" />
                            <span class="count">(<c:out value="${commentsCount}" />)</span>
                        </h2>
                        <button onclick="toggleComments()" class="toggle-comments-btn" aria-label="Toggle comments">
                            <span id="collapse-icon">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                    <polyline points="18 15 12 9 6 15"></polyline>
                                </svg>
                            </span>
                            <span id="expand-icon" style="display: none;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                    <polyline points="6 9 12 15 18 9"></polyline>
                                </svg>
                            </span>
                        </button>
                    </div>

                    <div id="comments-list" class="section-content responses-list">
                        <c:if test="${empty journeyResponsesPage.content}">
                            <div class="empty-state">
                                <div class="empty-icon">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
                                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                    </svg>
                                </div>
                                <p class="empty-message">
                                    <spring:message code="journey.detail.no.responses" />
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty journeyResponsesPage.content}">
                            <c:set var="sortedResponses" value="${journeyResponsesPage.content}" />
                            <c:forEach var="response" items="${sortedResponses}" varStatus="status">
                                <div class="chat-message">
                                    <div class="response-header">
                                        <div class="response-user">
                                            <div class="response-avatar">
                                                <div class="avatar-placeholder">
                                                    <c:out value="${fn:substring(response.user.username, 0, 1)}" />
                                                </div>
                                            </div>
                                            <div class="response-user-info">
                                                <h3 class="response-username">
                                                    <c:out value="${response.user.username}" />
                                                </h3>
                                                <p class="response-date">
                                                    <fmt:parseDate value="${response.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                                    <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy HH:mm" var="formattedDate" />
                                                    <c:out value="${formattedDate}" />
                                                </p>
                                            </div>
                                        </div>

                                        <!-- Comment Actions Dropdown -->
                                        <div style="position: relative; display: inline-block;">
                                            <button onclick="toggleCommentMenu(${status.index})" class="btn-action" id="commentMenuButton${status.index}" style="background: none; border: 1px solid #e0e0e0; border-radius: 4px; padding: 6px; cursor: pointer;" aria-label="Comment actions">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                    <circle cx="12" cy="12" r="1"></circle>
                                                    <circle cx="12" cy="5" r="1"></circle>
                                                    <circle cx="12" cy="19" r="1"></circle>
                                                </svg>
                                            </button>

                                            <div id="commentDropdown${status.index}" style="display: none; position: absolute; right: 0; top: 100%; background-color: white; min-width: 160px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); border-radius: 6px; z-index: 1000; border: 1px solid #e0e0e0; padding: 6px 0;">

                                                <!-- Report option for non-owners and admins -->
                                                <c:if test="${!isOwner || pageContext.request.isUserInRole('ADMIN')}">
                                                    <c:url var="reportJourneyCommentUrl" value='/reports/journey-responses/${response.id}/create'/>
                                                    <a href="${reportJourneyCommentUrl}"
                                                       style="color: #333; padding: 10px 14px; text-decoration: none; display: flex; align-items: center; gap: 10px; font-size: 13px;"
                                                       onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                            <path d="M12 9v4"></path>
                                                            <path d="M12 17h.01"></path>
                                                            <circle cx="12" cy="12" r="10"></circle>
                                                        </svg>
                                                        <span><spring:message code="comment.report" text="Report Comment" /></span>
                                                    </a>
                                                </c:if>

                                                <!-- Delete option for admins only -->
                                                <sec:authorize access="hasRole('ADMIN')">
                                                    <c:url var="deleteReplyUrl" value='/journeys/${journey.id}/reply/${response.id}/delete'/>
                                                    <a href="<c:out value='${deleteReplyUrl}'/>" style="color: #333; padding: 10px 14px; text-decoration: none; display: flex; align-items: center; gap: 10px; font-size: 13px;" onmouseover="this.style.backgroundColor='#fef2f2'; this.style.color='#dc2626'" onmouseout="this.style.backgroundColor='transparent'; this.style.color='#333'">
                                                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                            <path d="M3 6h18"></path>
                                                            <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                                        </svg>
                                                        <span><spring:message code="comment.delete" text="Delete Comment" /></span>
                                                    </a>
                                                </sec:authorize>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="response-body">
                                        <p class="response-message">
                                            <c:out value="${response.message}" />
                                        </p>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                        <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                            <jsp:param name="pageObjectTotalPages" value="${journeyResponsesPage.totalPages}" />
                            <jsp:param name="currentPage" value="${journeyResponsesPage.currentPage}" />
                            <jsp:param name="pageSize" value="${chatPageSize}" />
                            <jsp:param name="baseUrl" value="/journeys/${journey.id}?interestsPage=${interestPage.currentPage}&interestsSize=${interestPageSize}&eventsPage=${eventsPage.currentPage}&eventsSize=${eventsPageSize}" />
                        </jsp:include>
                    </div>

                    <div class="section-content">
                        <div class="reply-form-container">
                            <h3 class="reply-title">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                                <spring:message code="reply.message" text="Leave a comment" />
                            </h3>
                            <c:url var="replyUrl" value="/journeys/${journey.id}"/>
                            <form:form modelAttribute="replyJourneyForm" action="${replyUrl}" method="post" enctype="multipart/form-data" cssClass="reply-form">
                                <c:set var="messageLabel"><spring:message code="reply.message"/></c:set>
                                <c:set var="messageHint"><spring:message code="reply.message.hint"/></c:set>
                                <jsp:include page="../components/text-area.jsp">
                                    <jsp:param name="path" value="message" />
                                    <jsp:param name="label" value="${messageLabel}" />
                                    <jsp:param name="placeholder" value="${messageHint}" />
                                </jsp:include>

                                <div class="form-actions">
                                    <c:set var="submitButtonLabel"><spring:message code="reply.submit"/></c:set>
                                    <jsp:include page="../components/button.jsp">
                                        <jsp:param name="label" value="${submitButtonLabel}" />
                                        <jsp:param name="type" value="submit" />
                                    </jsp:include>
                                </div>
                            </form:form>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
</div>


<!-- Include Report Modal -->
<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>

<script>
    function toggleDropdown(dropdownId) {
        const dropdown = document.getElementById(dropdownId);
        dropdown.style.display = dropdown.style.display === 'none' ? 'block' : 'none';
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('[onclick*="toggleDropdown"]')) {
            document.querySelectorAll('[id$="-dropdown"]').forEach(dropdown => {
                dropdown.style.display = 'none';
            });
        }
    });
</script>
<script>
    function toggleComments() {
    const commentsList = document.getElementById('comments-list');
    const collapseIcon = document.getElementById('collapse-icon');
    const expandIcon = document.getElementById('expand-icon');

    if (commentsList.style.display === 'none') {
        commentsList.style.display = 'flex';
        collapseIcon.style.display = 'inline';
        expandIcon.style.display = 'none';
    } else {
        commentsList.style.display = 'none';
        collapseIcon.style.display = 'none';
        expandIcon.style.display = 'inline';
    }
}

function toggleEvents() {
    const eventsList = document.getElementById('events-list');
    const collapseIcon = document.getElementById('events-collapse-icon');
    const expandIcon = document.getElementById('events-expand-icon');

    if (eventsList.style.display === 'none') {
        eventsList.style.display = 'block';
        collapseIcon.style.display = 'inline';
        expandIcon.style.display = 'none';
    } else {
        eventsList.style.display = 'none';
        collapseIcon.style.display = 'none';
        expandIcon.style.display = 'inline';
    }
}


    function goBack(){
        const rutaAnterior = popFromNavigationStack()
        if (rutaAnterior) {
            window.location.href = rutaAnterior;
        } else {
            window.location.href = "<c:url value='/events'/>"
        }
    }

    function toggleActionMenu() {
        const dropdown = document.getElementById('actionDropdown');
        if (dropdown.style.display === 'none' || dropdown.style.display === '') {
            dropdown.style.display = 'block';
        } else {
            dropdown.style.display = 'none';
        }
    }

    function toggleCommentMenu(index) {
        const dropdown = document.getElementById('commentDropdown' + index);
        if (dropdown.style.display === 'none' || dropdown.style.display === '') {
            dropdown.style.display = 'block';
        } else {
            dropdown.style.display = 'none';
        }
    }

    // Close dropdowns when clicking outside
    document.addEventListener('click', function(event) {
        // Close main action dropdown
        const dropdown = document.getElementById('actionDropdown');
        const button = document.getElementById('actionMenuButton');

        if (dropdown && button && !button.contains(event.target) && !dropdown.contains(event.target)) {
            dropdown.style.display = 'none';
        }

        // Close comment dropdowns
        const commentButtons = document.querySelectorAll('[id^="commentMenuButton"]');
        const commentDropdowns = document.querySelectorAll('[id^="commentDropdown"]');

        commentButtons.forEach(function(commentButton, index) {
            const commentDropdown = document.getElementById('commentDropdown' + index);
            if (commentDropdown && commentButton && !commentButton.contains(event.target) && !commentDropdown.contains(event.target)) {
                commentDropdown.style.display = 'none';
            }
        });
    });
</script>

</body>
</html>