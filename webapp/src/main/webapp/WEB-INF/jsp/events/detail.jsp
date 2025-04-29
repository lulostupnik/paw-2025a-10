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
    <title><spring:message code="event.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
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

        function toggleAttendees() {
            const attendeesList = document.getElementById('attendees-list');
            const attendeesCollapseIcon = document.getElementById('attendees-collapse-icon');
            const attendeesExpandIcon = document.getElementById('attendees-expand-icon');

            if (attendeesList.style.display === 'none') {
                attendeesList.style.display = 'grid';
                attendeesCollapseIcon.style.display = 'inline';
                attendeesExpandIcon.style.display = 'none';
            } else {
                attendeesList.style.display = 'none';
                attendeesCollapseIcon.style.display = 'none';
                attendeesExpandIcon.style.display = 'inline';
            }
        }
    </script>
</head>

<body>
<div style="display: none;">
    <!-- Event deletion messages -->
    <span id="i18n-event.confirmDelete" data-message="<spring:message code='event.confirmDelete' />"></span>
    <span id="i18n-event.deleteWarning" data-message="<spring:message code='event.deleteWarning' />"></span>

    <!-- Event response deletion messages -->
    <span id="i18n-eventResponse.confirmDelete" data-message="<spring:message code='eventResponse.confirmDelete' />"></span>
    <span id="i18n-eventResponse.deleteWarning" data-message="<spring:message code='eventResponse.deleteWarning' />"></span>

  </div>
<div class="layout-container">
    <!-- Include the sidebar component -->
    <jsp:include page="../components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <!-- Back to Events Button -->

            <div class="back-navigation">
                <c:if test="${not isEventOwner}">
                    <a href="<c:url value='/events' />" class="back-link">
                        <img src="<c:url value='/resources/icons/back.svg'/>" alt="Back" class="icon" />
                        <spring:message code="event.detail.back.to.list" />
                    </a>
                </c:if>
                <c:if test="${isEventOwner}">
                    <a href="<c:url value='/profile' />" class="back-link">
                        <img src="<c:url value='/resources/icons/back.svg'/>" alt="Back" class="icon" />
                        <spring:message code="event.detail.back.to.profile" />
                    </a>
                </c:if>
            </div>


            <!-- Event Detail Card -->
            <div class="content-card event-detail-card">
                <!-- Event Flyer Banner -->

                <!-- Event Detail Header -->
                <div class="event-detail-header${isFull ? ' is-full' : ''}">
                    <div class="event-info">
                        <div class="event-title-section">
                            <h1 class="event-title">
                                <c:out value="${event.title}" />

                                <%--                                <c:if test="${isFull}">--%>
                                <%--                                    <span class="event-full-tag">FULL</span>--%>
                                <%--                                </c:if>--%>
                            </h1>
                            <div class="event-meta">
                                <div class="event-location">
                                    <img src="<c:url value='/resources/icons/location.svg'/>" alt="Location" class="icon" />
                                    <span class="location-text">
                                        <c:out value="${event.eventCity.name}" />
                                    </span>
                                </div>
                                <div class="event-date">
                                    <img src="<c:url value='/resources/icons/calendar_black.svg'/>" alt="Calendar" class="icon" />
                                    <span class="date-text">
                                         <c:out value="${event.date}" />
<%--                                        <fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy" />--%>
                                    </span>
                                </div>
                                <div class="event-date">
                                    <img src="<c:url value='/resources/icons/time.svg'/>" alt="Time clock" class="icon" />
                                    <span class="date-text">
                                        <c:if test="${event.time.isPresent()}">
                                            <c:out value="${event.time.get()}" />
                                        </c:if>
                                        <c:if test="${event.time.isEmpty()}">
                                            <span><spring:message code="event.allDayEvent"/></span>
                                        </c:if>
                                    </span>
                                </div>
                            </div>
                            <c:if test="${not empty event.address}">
                                <div class="event-meta">
                                    <div class="event-location">
                                        <img src="<c:url value='/resources/icons/map.svg'/>" style="filter: saturate(0) brightness(10%)" alt="Map" class="icon" />
                                        <span class="location-text">
                                            <c:out value="${event.address}" />
                                        </span>
                                    </div>
                                </div>
                            </c:if>

                        <!-- Attend Button Section -->
                            <c:if test="${not isEventOwner}">
                                <div class="attendance-control">
                            <c:choose>
                                <c:when test="${not empty username and attend}">
                                    <div class="attendance-status-container">
                                        <div class="btn-attendance btn-attending">
                                            <img src="<c:url value='/resources/icons/check.svg'/>" alt="<spring:message code='event.attending'/>" class="btn-icon" />
                                            <span class="btn-text"><spring:message code="event.attending" text="Attending" /></span>
                                        </div>
                                        <form action="<c:url value='/events/${event.id}/dont-attend'/>" method="post" class="cancel-attendance-form">
                                            <button type="submit" class="btn-cancel-attendance" aria-label="<spring:message code='event.cancel.attendance' text='Cancel Attendance'/>">
                                                <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code='event.cancel.attendance' text='Cancel Attendance'/>" class="btn-icon" />
                                            </button>
                                        </form>
                                    </div>
                                </c:when>

                                <c:when test="${not empty username and not attend and not isFull}">
                                    <form action="<c:url value='/events/${event.id}/attend'/>" method="post">
                                        <button type="submit" class="btn-attendance btn-attend">
                                            <img src="<c:url value='/resources/icons/calendar-plus.svg'/>" alt="<spring:message code='event.attend'/>" class="btn-icon" />
                                            <span class="btn-text"><spring:message code="event.attend" text="Attend" /></span>
                                        </button>
                                    </form>
                                </c:when>

                                <c:when test="${not empty username and not attend and isFull}">
                                    <div class="event-full-status">
                                        <img src="<c:url value='/resources/icons/alert-circle.svg'/>" alt="Alert" class="icon" />
                                        <span><spring:message code="event.full" text="Event full" /></span>
                                    </div>
                                </c:when>
                            </c:choose>
                        </div>
                            </c:if>
                        </div>
                    </div>
                </div>
                <div class="event-flyer-container">
                    <c:if test="${not empty event.flyerImageId}">
                        <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event Flyer" class="event-flyer-img">
                    </c:if>
                    <c:if test="${empty event.flyerImageId}">
                        <div class="event-flyer-placeholder">
                            <p class="event-placeholder-text"><spring:message code="event.no.flyer" /></p>
                        </div>
                    </c:if>
                </div>


                <!-- Event Description Section -->
                <section class="content-section">
                    <%--                    <div class="section-header">--%>
                    <%--                        <h2 class="section-title">--%>
                    <%--                            <img src="<c:url value='/resources/icons/description.svg'/>" alt="Description" class="icon" />--%>
                    <%--                            <spring:message code="event.description" />--%>
                    <%--                        </h2>--%>
                    <%--                    </div>--%>
                    <div class="section-content">
                        <div class="event-description-card">
                            <p class="event-description-text">
                                <c:out value="${event.description}" />
                            </p>
                        </div>
                    </div>
                        <!-- Replace the existing delete button with this -->
                        <sec:authorize access="hasRole('ADMIN')">
                            <c:url var="deleteUrl" value='/events/${event.id}/delete'/>
                            <form:form modelAttribute="deleteForm" id="delete-event-form" action="${deleteUrl}" method="post" style="display: none;">
                                <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                                <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                                <jsp:include page="../components/text-area.jsp">
                                    <jsp:param name="path" value="message" />
                                    <jsp:param name="label" value="${messageLabel}" />
                                    <jsp:param name="placeholder" value="${messagePlaceholder}" />
                                </jsp:include>
                            </form:form>

                            <button type="button" class="btn-attendance btn-danger" onclick="openDeleteModal('delete-event-form', 'event')">
                                <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code='event.delete'/>" class="btn-icon" />
                                <span class="btn-text"><spring:message code="event.delete" text="Delete" /></span>
                            </button>
                        </sec:authorize>
                </section>

                <!-- Event Attendees Section -->
                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <img src="<c:url value='/resources/icons/users.svg'/>" alt="Attendees" class="icon" />
                            <spring:message code="event.attendees" />
                            <c:if test="${event.attendeesLimit.isPresent()}">
                                <%--                                @TODO change to more efficient--%>
                                <span class="attendees-count">(<c:out value="${event.attendeesCount}" /> / <c:out value="${event.attendeesLimit.get()}" />)</span>
                            </c:if>
                            <c:if test="${event.attendeesLimit.isEmpty()}">
                                <span class="attendees-count">(<c:out value="${event.attendeesCount}" /> / <spring:message code="event.noAttendeesLimit"/>)</span>
                            </c:if>
                        </h2>
                        <c:if test="${isEventOwner}">
                            <button onclick="toggleAttendees()" class="toggle-attendees-btn" aria-label="Toggle attendees">
                                <span id="attendees-collapse-icon">
                                    <img src="<c:url value='/resources/icons/collapse.svg'/>" alt="Collapse" class="icon" />
                                </span>
                                <span id="attendees-expand-icon" style="display: none;">
                                    <img src="<c:url value='/resources/icons/expand.svg'/>" alt="Expand" class="icon" />
                                </span>
                            </button>
                        </c:if>
                    </div>

                    <!-- Attendees List -->
                    <c:if test="${isEventOwner}">
                        <div id="attendees-list" class="section-content attendees-grid">
                            <c:if test="${empty attendees}">
                                <div class="empty-state">
                                    <div class="empty-icon">
                                        <img src="<c:url value='/resources/icons/users-empty.svg'/>" alt="No Attendees" class="empty-icon-img" />
                                    </div>
                                    <p class="empty-message">
                                        <spring:message code="event.no.attendees" />
                                    </p>
                                </div>
                            </c:if>

                            <c:if test="${not empty attendees}">
                                <c:forEach var="attendee" items="${attendees}">
                                    <div class="attendee-card">
                                        <div class="attendee-avatar">
                                            <c:if test="${not empty attendee.profilePictureId}">
                                                <img src="<c:url value='/images/${attendee.profilePictureId}'/>" alt="Profile" class="avatar-img">
                                            </c:if>
                                            <c:if test="${empty attendee.profilePictureId}">
                                                <div class="avatar-placeholder">
                                                    <c:out value="${fn:substring(attendee.firstname, 0, 1)}${fn:substring(attendee.lastname, 0, 1)}" />
                                                </div>
                                            </c:if>
                                        </div>
                                        <div class="attendee-info">
                                            <h3 class="attendee-name">
                                                <c:out value="${attendee.firstname} ${attendee.lastname}" />
                                            </h3>
                                            <p class="attendee-email">
                                                <c:out value="${attendee.email}" />
                                            </p>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </div>
                    </c:if>
                </section>

                <!-- Event Responses Section -->
                <section class="content-section">
                    <div class="section-header">
                        <h2 class="section-title">
                            <img src="<c:url value='/resources/icons/comments.svg'/>" alt="Comments" class="icon" />
                            <spring:message code="event.responses" />
                            <span class="response-count">(<c:out value="${fn:length(eventResponses)}" />)</span>
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
                        <c:if test="${empty eventResponses}">
                            <div class="empty-state">
                                <div class="empty-icon">
                                    <img src="<c:url value='/resources/icons/no_comment.svg'/>" alt="No Comments" class="empty-icon-img" />
                                </div>
                                <p class="empty-message">
                                    <spring:message code="event.no.responses" />
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty eventResponses}">
                            <!-- Sort responses by date (newest first) -->
                            <c:set var="sortedResponses" value="${eventResponses}" />
                            <c:forEach var="response" items="${sortedResponses}">
                                <div class="response-card flex flex-row justify-between items-center">
                                    <div class="flex flex-col">
                                        <div class="response-header">
                                            <div class="response-user">
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
                                                        <c:out value="${response.formattedDate}" />
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
                                            <c:url var="deleteReplyUrl" value='/event-replies/${response.id}/delete'/>
                                            <form:form modelAttribute="deleteReplyForm" id="delete-event-response-form-${response.id}" action="${deleteReplyUrl}" method="post" style="display: none;">
                                                <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                                                <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                                                <jsp:include page="../components/text-area.jsp">
                                                    <jsp:param name="path" value="message" />
                                                    <jsp:param name="label" value="${messageLabel}" />
                                                    <jsp:param name="placeholder" value="${messagePlaceholder}" />
                                                </jsp:include>
                                            </form:form>

                                            <button type="button" class="btn-attendance btn-danger" onclick="openDeleteModal('delete-event-response-form-${response.id}', 'eventResponse')">
                                                <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code='event.delete'/>" class="btn-icon" />
                                                <span class="btn-text"><spring:message code="event.delete" text="Delete" /></span>
                                            </button>
                                        </div>
                                    </sec:authorize>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                    <%--Leave a comment section--%>

                    <div class="section-content">
                        <div class="reply-form-container">
                            <c:url var="replyUrl" value="/events/${event.id}/reply"/>
                            <form:form modelAttribute="replyEventForm" action="${replyUrl}" method="post" enctype="multipart/form-data" cssClass="reply-form">
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
