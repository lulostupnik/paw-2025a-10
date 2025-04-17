<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--
Parameters:
- event: The event object to display
- showReplyButton: Whether to show the reply button (default: false)
- imagePathPrefix: Optional prefix for the image path (default: empty)
--%>

<div class="event-card">
    <!-- Reply Button in Top Right - Only shown if explicitly requested -->
    <c:if test="${param.showReplyButton == 'true'}">
        <div class="reply-button-container">
            <a href="<c:url value="/events/${event.id}/reply"/>" class="reply-button">
                <spring:message code="event.reply.button"/>
            </a>
        </div>
    </c:if>

    <div class="event-header">
        <h2 class="event-title"><c:out value="${event.eventCity.name}"/></h2>
        <p class="event-date">
            <fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy" />
        </p>
    </div>

    <div class="event-section">
        <h3 class="section-title">Description</h3>
        <p class="section-content"><c:out value="${event.description}"/></p>
    </div>

    <div class="event-section">
        <h3 class="section-title">Contact</h3>
        <p class="section-content"><c:out value="${event.user.email}"/></p>
    </div>

    <!-- New Attendance Button Section -->
    <div class="event-section attendance-section">
        <h3 class="section-title">Will you attend?</h3>
        <c:if test="${not attend }">
            <c:set var="postAttendanceUrl"><c:url value='/events/${event.id}/attend'/></c:set>
            <form:form method="post" action="${postAttendanceUrl}" id="attendanceForm">
                <input type="hidden" name="eventId" value="${event.id}"/>
            <button id="attendButton" type="submit" class="attend-button" data-event-id="${event.id}">
                <span class="button-icon">✓</span>
                <span class="button-text">I will attend this event</span>
            </button>
            </form:form>
        </c:if>
        <c:if test="${attend}">
        <p id="attendanceConfirmation" class="attendance-confirmation">
            You're attending this event! We look forward to seeing you.
        </p>
        </c:if>
    </div>

    <!-- New Participants List Section -->
    <div class="event-section participants-section">
        <h3 class="section-title">Participants</h3>
        <div class="participants-list-container">
            <c:choose>
                <c:when test="${not empty attendees}">
                    <ul class="participants-list">
                        <c:forEach var="participant" items="${attendees}">
                            <li class="participant-item">
                                <div class="participant-avatar">
                                    <c:choose>
                                        <c:when test="${not empty participant.profilePictureId}">
                                            <img class="" src="<c:url value="${empty param.imagePathPrefix ? '' : param.imagePathPrefix}/images/${participant.profilePictureId}"/>" alt=""/>
                                        </c:when>
                                        <c:otherwise>
<%--                                            <div class="avatar-placeholder">${fn:substring(participant.name, 0, 1)}</div>--%>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="participant-info">
                                    <span class="participant-name"><c:out value="${participant.username}"/></span>
                                    <c:if test="${not empty participant.university.city.name}">
                                        <span class="participant-title"><c:out value="${participant.university.city.name}"/></span>
                                    </c:if>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p class="no-participants">No participants have registered yet. Be the first to attend!</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="event-section">
        <h3 class="section-title">Flyer Image</h3>
        <!-- Display the flyer image if available -->
        <c:if test="${not empty event.flyerImageId}">
            <img src="<c:url value="${empty param.imagePathPrefix ? '' : param.imagePathPrefix}/images/${event.flyerImageId}"/>"
                 alt="Event Flyer" class="event-image" />
        </c:if>
    </div>

    <div class="event-section comments-section">
        <h3 class="section-title">Comments</h3>
        <c:choose>
            <c:when test="${not empty eventResponses}">
                <ul class="comments-list">
                    <c:forEach var="response" items="${eventResponses}">
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
