<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="journey.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
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
    <!-- Include the sidebar component -->
    <!-- Main Content -->
    <div class="main-content">
        <%--        <jsp:include page="../components/navbar.jsp" />--%>
        <div class="content-container">
            <!-- Back to Journeys Button -->

            <div class="back-navigation">
                <a href="<c:url value='/journeys' />" class="back-link">
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
                            <c:set var="escapedFirstname"><c:out value="${journey.user.firstname}"/></c:set>
                            <c:set var="escapedLastname"><c:out value="${journey.user.lastname}"/></c:set>
                            <h1 class="journey-title">
                                <spring:message arguments="${escapedFirstname},${escapedLastname}" code="journey.detail.section.title" />
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
                    <%--                    <div class="section-header">--%>
                    <%--                        <h2 class="section-title">--%>
                    <%--                            <img src="<c:url value='/resources/icons/description.svg'/>" alt="Description" class="icon" />--%>
                    <%--                            <spring:message code="journey.description" />--%>
                    <%--                        </h2>--%>
                    <%--                    </div>--%>
                    <div class="section-content">
                        <div class="journey-description-card">
                            <p class="journey-description-text">
                                <c:out value="${journey.description}" />
                            </p>
                        </div>
                    </div>
                        <sec:authorize access="hasRole('ADMIN')">
                            <c:url var="deleteUrl" value='/journeys/${journey.id}/delete'/>
                            <form:form modelAttribute="deleteForm" id="delete-journey-form" action="${deleteUrl}" method="post" style="display: none;">
                                <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                                <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                                <jsp:include page="../components/text-area.jsp">
                                    <jsp:param name="path" value="message" />
                                    <jsp:param name="label" value="${messageLabel}" />
                                    <jsp:param name="placeholder" value="${messagePlaceholder}" />
                                </jsp:include>
                            </form:form>

                            <button type="button" class="btn-attendance btn-danger" onclick="openDeleteModal('delete-journey-form', 'journey')">
                                <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code='event.delete'/>" class="btn-icon" />
                                <span class="btn-text"><spring:message code="event.delete" text="Delete" /></span>
                            </button>
                        </sec:authorize>
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
                                    <img src="<c:url value='/resources/icons/no_comment.svg'/>" alt="No Comments" class="empty-icon-img" />
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
                                <div class="response-card flex flex-row justify-between items-center">
                                    <div class="flex flex-col">
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
                                    <sec:authorize access="hasRole('ADMIN')">
                                        <div class="flex">
                                            <c:url var="deleteReplyUrl" value='/journey-replies/${response.id}/delete'/>
                                            <form:form modelAttribute="deleteReplyForm" id="delete-journey-response-form-${response.id}" action="${deleteReplyUrl}" method="post" style="display: none;">
                                                <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                                                <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                                                <jsp:include page="../components/text-area.jsp">
                                                    <jsp:param name="path" value="message" />
                                                    <jsp:param name="label" value="${messageLabel}" />
                                                    <jsp:param name="placeholder" value="${messagePlaceholder}" />
                                                </jsp:include>
                                            </form:form>

                                            <button type="button" class="btn-attendance btn-danger" onclick="openDeleteModal('delete-journey-response-form-${response.id}', 'journeyResponse')">
                                                <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code='event.delete'/>" class="btn-icon" />
                                                <span class="btn-text"><spring:message code="event.delete" text="Delete" /></span>
                                            </button>
                                        </div>
                                    </sec:authorize>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>


                    <%--                    Leave a comment div--%>
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
<c:set var="warning"><spring:message code="event.deleteWarning"/></c:set>
<jsp:include page="../components/delete-modal.jsp">
    <jsp:param name="warning" value="${warning}"/>
</jsp:include>
<!-- Add this before the closing body tag -->
<c:if test="${deleteFormHasErrors}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Open the modal with the form that has errors
            openDeleteModal('${deleteFormId}', '${deleteFormType}');
        });
    </script>
</c:if>
</body>
</html>
