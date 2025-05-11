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
            function goBack(){
            const rutaAnterior = sessionStorage.getItem("rutaAnterior");
            if (rutaAnterior) {
            window.location.href = rutaAnterior;
        } else {
            window.location.href = "<c:url value='/journeys' />"
        }
        }
    </script>
</head>

<body>
<c:set var="interestPageSize" value="8" scope="request" />
<c:set var="chatPageSize" value="4" scope="request" />
<!-- Hidden elements to store i18n messages for JavaScript -->
<div style="display: none;">
    <!-- Journey deletion messages -->
    <span id="i18n-journey.confirmDelete" data-message="<spring:message code='journey.confirmDelete' />"></span>
    <span id="i18n-journey.deleteWarning" data-message="<spring:message code='journey.deleteWarning' />"></span>

    <!-- Journey response deletion messages -->
    <span id="i18n-journeyResponse.confirmDelete" data-message="<spring:message code='journeyResponse.confirmDelete' />"></span>
    <span id="i18n-journeyResponse.deleteWarning" data-message="<spring:message code='journeyResponse.deleteWarning' />"></span>
</div>

<div class="layout-container">
    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <!-- Back to Journeys Button -->
            <div class="back-navigation">
                <c:if test="${ isOwner}">
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

            <!-- Journey Detail Card -->
            <div class="content-card journey-detail-card">
                <!-- Journey Actions (Edit/Delete) -->
                <div class="journey-actions">
                    <c:if test="${isOwner || pageContext.request.isUserInRole('ADMIN')}">
                        <c:url var="deleteUrl" value='/journeys/${journey.id}/delete'/>

                        <c:if test="${isOwner}">
                            <a href="<c:url value='/journeys/${journey.id}/update'/>" class="btn-action btn-edit">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                                    <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                    <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                </svg>
                                <span class="btn-text"><spring:message code="journey.edit" text="Edit Journey" /></span>
                            </a>
                        </c:if>

                        <a href="${deleteUrl}" type="button" class="btn-action btn-danger" >
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                                <path d="M3 6h18"></path>
                                <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                <line x1="10" y1="11" x2="10" y2="17"></line>
                                <line x1="14" y1="11" x2="14" y2="17"></line>
                            </svg>
                            <span class="btn-text"><spring:message code="journey.delete" text="Delete Journey" /></span>
                        </a>
                    </c:if>
                </div>

                <!-- Journey Detail Header -->
                <div class="journey-detail-header">
                    <div class="journey-user-info">
                        <div class="user-avatar">
                            <c:if test="${not empty journey.user.profilePictureId}">
                                <img src="<c:url value='/images/${journey.user.profilePictureId}'/>" alt="Profile" class="avatar-img">
                            </c:if>
                            <c:if test="${empty journey.user.profilePictureId}">
                                <div class="avatar-placeholder">
                                    <c:out value="${fn:substring(journey.user.firstname, 0, 1)}${fn:substring(journey.user.lastname, 0, 1)}" />
                                </div>
                            </c:if>
                        </div>
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
                </div>

                <!-- Journey Description Section -->
                <section class="content-section">
                    <div class="section-content">
                        <div class="journey-description-card">
                            <p class="journey-description-text"><c:out value="${journey.description}" /></p>
                        </div>
                    </div>
                </section>

                <!-- User Interests Section -->
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
                                <c:forEach var="interest" items="${interestPage.content}">
                                    <div class="interest-tag">
                                        <c:out value="${interest}" />
                                    </div>
                                </c:forEach>
                            </div>
                            <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                                <jsp:param name="pageObjectTotalPages" value="${interestPage.totalPages}" />
                                <jsp:param name="currentPage" value="${interestPage.currentPage}" />
                                <jsp:param name="pageSize" value="${interestPageSize}" />
                                <jsp:param name="baseUrl" value="/journeys/${journey.id}?page=${journeyResponsesPage.currentPage}&size=${chatPageSize}" />
                                <jsp:param name="paramName" value="interestsPage" />
                                <jsp:param name="sizeParamName" value="interestsSize" />
                            </jsp:include>
                        </c:if>

                    </div>
                </section>

                <!-- Journey Responses Section -->
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

                    <!-- Responses List -->
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
                            <!-- Sort responses by date (newest first) -->
                            <c:set var="sortedResponses" value="${journeyResponsesPage.content}" />
                            <c:forEach var="response" items="${sortedResponses}">
                                <div class="chat-message">
                                    <!-- Delete Comment Button (Circle with Trash Icon) -->

                                    <div class="response-header">
                                        <div class="response-user">
                                            <div class="response-avatar">
                                                <div class="avatar-placeholder">
                                                    <c:out value="${fn:substring(response.username, 0, 1)}" />
                                                </div>
                                            </div>
                                            <div class="response-user-info">
                                                <h3 class="response-username">
                                                    <c:out value="${response.username}" />
                                                </h3>
                                                <p class="response-date">
                                                    <c:out value="${response.getFormattedDate()}" />
                                                </p>
                                            </div>
                                        </div>
                                        <sec:authorize access="hasRole('ADMIN')">
                                            <c:url var="deleteReplyUrl" value='/journeys/${journey.id}/reply/${response.id}/delete'/>

                                            <a type="button" class="delete-message-button" href="${deleteReplyUrl}" aria-label="Delete comment">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                    <path d="M3 6h18"></path>
                                                    <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                                </svg>
                                            </a>
                                        </sec:authorize>

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
                            <jsp:param name="baseUrl" value="/journeys/${journey.id}?interestsPage=${interestPage.currentPage}&interestsSize=${interestPageSize}" />
                        </jsp:include>
                    </div>

                    <!-- Leave a comment div -->
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
                                <!-- Message Field -->
                                <c:set var="messageLabel"><spring:message code="reply.message"/></c:set>
                                <c:set var="messageHint"><spring:message code="reply.message.hint"/></c:set>
                                <jsp:include page="../components/text-area.jsp">
                                    <jsp:param name="path" value="message" />
                                    <jsp:param name="label" value="${messageLabel}" />
                                    <jsp:param name="placeholder" value="${messageHint}" />
                                </jsp:include>

                                <!-- Submit Button -->
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
</body>
</html>
