<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>--%>
<%--<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>--%>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>--%>
<%--<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>--%>
<%--<!DOCTYPE html>--%>
<%--<html lang="en">--%>
<%--<head>--%>
<%--    <meta charset="UTF-8">--%>
<%--    <meta name="viewport" content="width=device-width, initial-scale=1.0">--%>
<%--    <title><spring:message code="journey.detail.title"/></title>--%>
<%--    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />--%>
<%--</head>--%>

<%--<body>--%>
<%--<div class="layout-container">--%>
<%--    <!-- Include the sidebar component -->--%>
<%--    <jsp:include page="../components/sidebar.jsp" />--%>
<%--    <jsp:include page="../components/sidebar.jsp" />--%>

<%--    <!-- Main Content -->--%>
<%--    <div class="main-content">--%>
<%--        <div class="content-container">--%>
<%--            <!-- Journey Detail Header -->--%>
<%--            <div class="journey-detail-header">--%>
<%--                <div class="journey-user-info">--%>
<%--                    <div class="user-avatar">--%>
<%--                        <c:if test="${not empty journey.user.profilePictureId}">--%>
<%--                            <img src="<c:url value='/images/${journey.user.profilePictureId}'/>" alt="Profile" class="avatar-img">--%>
<%--                        </c:if>--%>
<%--                        <c:if test="${empty journey.user.profilePictureId}">--%>
<%--                            <div class="avatar-placeholder">--%>
<%--                                <c:out value="${fn:substring(journey.user.firstname, 0, 1)}${fn:substring(journey.user.lastname, 0, 1)}" />--%>
<%--&lt;%&ndash;                            @TODO CHECK&ndash;%&gt;--%>
<%--                            </div>--%>
<%--                        </c:if>--%>
<%--                    </div>--%>
<%--                    <div class="user-details">--%>
<%--                        <h1 class="journey-title">--%>
<%--                            <c:out value="${journey.user.firstname} ${journey.user.lastname}'s Journey" />--%>
<%--                        </h1>--%>
<%--                        <div class="journey-meta">--%>
<%--                            <div class="journey-destination">--%>
<%--                                <svg xmlns="http://www.w3.org/2000/svg" class="journey-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">--%>
<%--                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />--%>
<%--                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />--%>
<%--                                </svg>--%>
<%--&lt;%&ndash;                                @TODO CHANGE&ndash;%&gt;--%>
<%--                                <span class="destination-text">--%>
<%--                                    <c:out value="${journey.destinationUniversity.city}" /> ---%>
<%--                                    <c:out value="${journey.destinationUniversity.name}" />--%>
<%--                                </span>--%>
<%--                            </div>--%>
<%--                            <div class="journey-dates">--%>
<%--                                <svg xmlns="http://www.w3.org/2000/svg" class="journey-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">--%>
<%--                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />--%>
<%--                                </svg>--%>
<%--                                <span class="date-range">--%>
<%--                                    <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />--%>
<%--                                </span>--%>
<%--                            </div>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </div>--%>

<%--            <!-- Journey Description Section -->--%>
<%--            <section class="content-section">--%>
<%--                <div class="section-header">--%>
<%--                    <h2 class="section-title">--%>
<%--                        <spring:message code="journey.description" />--%>
<%--                    </h2>--%>
<%--                </div>--%>
<%--                <div class="journey-description-card">--%>
<%--                    <p class="journey-description-text">--%>
<%--                        <c:out value="${journey.description}" />--%>
<%--                    </p>--%>
<%--                </div>--%>
<%--            </section>--%>

<%--            <!-- Journey Responses Section -->--%>
<%--            <section class="content-section">--%>
<%--                <div class="section-header">--%>
<%--                    <h2 class="section-title">--%>
<%--                        <spring:message code="journey.detail.responses" />--%>
<%--                        <span class="response-count">(<c:out value="${fn:length(journeyResponses)}" />)</span>--%>
<%--                    </h2>--%>
<%--                </div>--%>

<%--                <!-- Responses List -->--%>
<%--                <div class="responses-list">--%>
<%--                    <c:if test="${empty journeyResponses}">--%>
<%--                        <div class="empty-state">--%>
<%--                            <div class="empty-icon">--%>
<%--                                <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">--%>
<%--                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z" />--%>
<%--                                </svg>--%>
<%--                            </div>--%>
<%--                            <p class="empty-message">--%>
<%--                                <spring:message code="journey.detail.no.responses" />--%>
<%--                            </p>--%>
<%--                        </div>--%>
<%--                    </c:if>--%>

<%--                    <c:if test="${not empty journeyResponses}">--%>
<%--                        <c:forEach var="response" items="${journeyResponses}">--%>
<%--                            <div class="response-card">--%>
<%--                                <div class="response-header">--%>
<%--                                    <div class="response-user">--%>
<%--                                        <div class="response-avatar">--%>
<%--                                            <!-- This would need user data that might not be in the response object -->--%>
<%--                                            <div class="avatar-placeholder">--%>
<%--                                                <c:out value="${fn:substring(response.username, 0, 1)}" />--%>
<%--                                            </div>--%>
<%--                                        </div>--%>
<%--                                        <div class="response-user-info">--%>
<%--                                            <h3 class="response-username">--%>
<%--                                                <c:out value="${response.username}" />--%>
<%--                                            </h3>--%>
<%--                                            <p class="response-date">--%>
<%--                                                <c:out value="${response.getFormattedDate()}" />--%>
<%--                                            </p>--%>
<%--                                        </div>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                                <div class="response-body">--%>
<%--                                    <p class="response-message">--%>
<%--                                        <c:out value="${response.message}" />--%>
<%--                                    </p>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                        </c:forEach>--%>
<%--                    </c:if>--%>
<%--                </div>--%>
<%--            </section>--%>

<%--            <!-- Reply Form Section -->--%>


<%--            <div class="card">--%>
<%--                <c:url var="registerUrl" value="/journeys/${journey.id}/reply"/>--%>
<%--                <form:form modelAttribute="replyJourneyForm" action="${registerUrl}" method="post" enctype="multipart/form-data">--%>

<%--                    <!-- Message Field -->--%>
<%--                    <c:set var="messageLabel"><spring:message code="reply.message"/></c:set>--%>
<%--                    <c:set var="messageHint"><spring:message code="reply.message.hint"/></c:set>--%>
<%--                    <jsp:include page="../components/text-area.jsp">--%>
<%--                        <jsp:param name="path" value="message" />--%>
<%--                        <jsp:param name="label" value="${messageLabel}" />--%>
<%--                        <jsp:param name="placeholder" value="${messageHint}" />--%>
<%--                    </jsp:include>--%>

<%--                    <!-- Submit Button -->--%>
<%--                    <c:set var="submitButtonLabel"><spring:message code="reply.submit"/></c:set>--%>
<%--                    <jsp:include page="../components/button.jsp">--%>
<%--                        <jsp:param name="label" value="${submitButtonLabel}" />--%>
<%--                        <jsp:param name="type" value="submit" />--%>
<%--                    </jsp:include>--%>
<%--                </form:form>--%>
<%--            </div>--%>

<%--&lt;%&ndash;            <section class="content-section">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <div class="section-header">&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <h2 class="section-title">&ndash;%&gt;--%>
<%--&lt;%&ndash;                        <spring:message code="journey.detail.leave.reply" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                    </h2>&ndash;%&gt;--%>
<%--&lt;%&ndash;                </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                <div class="reply-form-container">&ndash;%&gt;--%>
<%--&lt;%&ndash;                    <form:form modelAttribute="replyJourneyForm" method="post" action="${pageContext.request.contextPath}/journeys/${journey.id}/reply" enctype="multipart/form-data" cssClass="reply-form">&ndash;%&gt;--%>
<%--&lt;%&ndash;                        <div class="form-group">&ndash;%&gt;--%>
<%--&lt;%&ndash;                            <label for="message" class="form-label">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <spring:message code="journey.detail.reply.message" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                            </label>&ndash;%&gt;--%>
<%--&lt;%&ndash;                            <form:textarea path="message" id="message" rows="4" cssClass="form-textarea" required="true" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                            <form:errors path="message" cssClass="form-error" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                        </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                        <div class="form-actions">&ndash;%&gt;--%>
<%--&lt;%&ndash;                            <button type="submit" class="submit-btn">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <spring:message code="journey.detail.submit.reply" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                                <svg xmlns="http://www.w3.org/2000/svg" class="submit-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">&ndash;%&gt;--%>
<%--&lt;%&ndash;                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 5l7 7-7 7M5 5l7 7-7 7" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                                </svg>&ndash;%&gt;--%>
<%--&lt;%&ndash;                            </button>&ndash;%&gt;--%>
<%--&lt;%&ndash;                        </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;                    </form:form>&ndash;%&gt;--%>
<%--&lt;%&ndash;                </div>&ndash;%&gt;--%>
<%--            </section>--%>

<%--            <!-- Back to Journeys Button -->--%>
<%--            <div class="back-navigation">--%>
<%--                <a href="<c:url value='/journeys'/>" class="back-link">--%>
<%--                    <svg xmlns="http://www.w3.org/2000/svg" class="back-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">--%>
<%--                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />--%>
<%--                    </svg>--%>
<%--                    <spring:message code="journey.detail.back.to.list" />--%>
<%--                </a>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>
<%--</div>--%>
<%--</body>--%>
<%--</html>--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="journey.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
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
    </script>
</head>

<body>
<div class="layout-container">
    <!-- Include the sidebar component -->
    <jsp:include page="../components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <!-- Back to Journeys Button -->
            <div class="back-navigation">
                <a href="<c:url value='/journeys'/>" class="back-link">
                    <img src="<c:url value='/resources/icons/back.svg'/>" alt="Back" class="icon" />
                    <spring:message code="journey.detail.back.to.list" />
                </a>
            </div>

            <!-- Journey Detail Card -->
            <div class="content-card journey-detail-card">
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
                            <h1 class="journey-title">
                                <c:out value="${journey.user.firstname} ${journey.user.lastname}'s Journey" />
                            </h1>
                            <div class="journey-meta">
                                <div class="journey-destination">
                                    <img src="<c:url value='/resources/icons/location.svg'/>" alt="Location" class="icon" />
                                    <span class="destination-text">
                                        <c:out value="${journey.destinationUniversity.city}" /> -
                                        <c:out value="${journey.destinationUniversity.name}" />
                                    </span>
                                </div>
                                <div class="journey-dates">
                                    <img src="<c:url value='/resources/icons/calendar_black.svg'/>" alt="Calendar" class="icon" />
                                    <span class="date-range">
                                        <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Journey Description Section -->
                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <img src="<c:url value='/resources/icons/description.svg'/>" alt="Description" class="icon" />
                            <spring:message code="journey.description" />
                        </h2>
                    </div>
                    <div class="section-content">
                        <div class="journey-description-card">
                            <p class="journey-description-text">
                                <c:out value="${journey.description}" />
                            </p>
                        </div>
                    </div>
                </section>

                <!-- Journey Responses Section -->
                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <img src="<c:url value='/resources/icons/comments.svg'/>" alt="Comments" class="icon" />
                            <spring:message code="journey.detail.responses" />
                            <span class="response-count">(<c:out value="${fn:length(journeyResponses)}" />)</span>
                        </h2>
                        <button onclick="toggleComments()" class="toggle-comments-btn" aria-label="Toggle comments">
                            <span id="collapse-icon">
                                <img src="<c:url value='/resources/icons/collapse.svg'/>" alt="Collapse" class="icon" />
                            </span>
                            <span id="expand-icon" style="display: none;">
                                <img src="<c:url value='/resources/icons/expand.svg'/>" alt="Expand" class="icon" />
                            </span>
                        </button>
                    </div>

                    <!-- Responses List -->
                    <div id="comments-list" class="section-content responses-list">
                        <c:if test="${empty journeyResponses}">
                            <div class="empty-state">
                                <div class="empty-icon">
<%--                                    @TODO CHECK ICON--%>
                                    <img src="<c:url value='/resources/icons/comments.svg'/>" alt="No Comments" class="empty-icon-img" />
                                </div>
                                <p class="empty-message">
                                    <spring:message code="journey.detail.no.responses" />
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty journeyResponses}">
                            <!-- Sort responses by date (newest first) -->
                            <c:set var="sortedResponses" value="${journeyResponses}" />
                            <c:forEach var="response" items="${sortedResponses}">
                                <div class="response-card">
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
                                    </div>
                                    <div class="response-body">
                                        <p class="response-message">
                                            <c:out value="${response.message}" />
                                        </p>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </section>

                <!-- Reply Form Section -->
                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
<%--                            <img src="<c:url value='/resources/icons/reply.svg'/>" alt="Reply" class="icon" />--%>
                            <spring:message code="journey.detail.leave.reply" />
                        </h2>
                    </div>
                    <div class="section-content">
                        <div class="reply-form-container">
                            <c:url var="replyUrl" value="/journeys/${journey.id}/reply"/>
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