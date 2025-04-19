<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="event-card-wrapper">
    <a href="<c:url value="/events/${param.eventId}"/>" class="event-card-link">
        <div class="featured-event-card">
            <div class="event-image-container">
                <c:if test="${not empty param.flyerImageId}">
                    <img src="<c:url value="/images/${param.flyerImageId}"/>"
                         alt="<spring:message code='event.flyer.alt'/>"
                         class="event-image profile-image">
                </c:if>
                <c:if test="${empty param.flyerImageId}">
                    <div class="image-placeholder">
                        <svg xmlns="http://www.w3.org/2000/svg" class="placeholder-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                    </div>
                </c:if>
                <%--                <!-- Favorite Badge -->--%>
                <%--                <div class="favorite-badge">--%>
                <%--                    <spring:message code="event.favorite.badge"/>--%>
                <%--                </div>--%>

                <!-- Attend Button -->
                <c:if test="${not empty username}">
                    <c:if test="${param.attend}">
                        <div class="attend-button-container">
                            <button type="button" class="attend-button attended" data-event-id="${param.eventId}" aria-label="<spring:message code='event.attend'/>">
                                <i class="fas fa-calendar-check"></i>
                            </button>
                        </div>
                    </c:if>
                    <c:if test="${not param.attend}">
                        <c:set var="postAttendanceUrl"><c:url value='/events/${param.eventId}/attend'/></c:set>
                        <form:form method="post" action="${postAttendanceUrl}" id="attendanceForm">
                            <input type="hidden" name="eventId" value="${param.eventId}"/>
                            <button type="submit" class="attend-button" data-event-id="${param.eventId}" aria-label="<spring:message code='event.attend'/>">
                                <i class="fas fa-calendar-check"></i>
                            </button>
                        </form:form>
                    </c:if>
                </c:if>
            </div>
            <div class="event-card-content">
                <div class="event-card-header">
                    <%--<h3 class="event-card-title">${event.title}</h3>--%>
                    <h3 class="event-card-title">${param.title}</h3>
                    <p class="event-card-subtitle">${param.city}</p>
                </div>
                <p class="event-card-description">${param.description}</p>
                <div class="event-card-footer">
                    <div class="event-date">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span>${param.date}</span>
                    </div>
                    <div class="event-organizer">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 016 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        <span class="organizer-name" title="${param.firstname} ${param.lastname}">${param.firstname} ${param.lastname}</span>
                    </div>
                </div>
            </div>
        </div>
    </a>
</div>