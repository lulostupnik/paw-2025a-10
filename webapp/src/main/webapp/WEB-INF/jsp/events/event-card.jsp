<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="event-card-wrapper">
    <a href="<c:url value="/events/${param.eventId}"/>" class="event-card-link">
        <div class="event-card">
            <!-- Image Container -->
            <div class="event-image-container">
                <c:if test="${not empty param.flyerImageId}">
                    <img src="<c:url value="/images/${param.flyerImageId}"/>"
                         alt="<spring:message code='event.flyer.alt'/>"
                         class="event-image profile-image">
                </c:if>
                <c:if test="${empty param.flyerImageId}">
                    <div class="event-image-placeholder">
                        <i class="fas fa-calendar-alt"></i>
                    </div>
                </c:if>

<%--                <!-- Favorite Badge -->--%>
<%--                <div class="favorite-badge">--%>
<%--                    <spring:message code="event.favorite.badge"/>--%>
<%--                </div>--%>

                <!-- Attend Button -->
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
            </div>

            <!-- Event Info -->
            <div class="event-info-container">
                <div class="event-header">
                    <div class="event-location">
                        <h3><c:out value="${param.eventCity}"/></h3>
                    </div>
                    <div class="event-rating">
                        <i class="fas fa-star"></i>
                        <span>5.0</span>
                    </div>
                </div>

                <!-- Format date -->
                <fmt:parseDate value="${param.eventDate}" pattern="yyyy-MM-dd" var="parsedDate" />
                <fmt:formatDate value="${parsedDate}" pattern="d" var="day" />
                <fmt:formatDate value="${parsedDate}" pattern="MM" var="month" />
                <fmt:formatDate value="${parsedDate}" pattern="yyyy" var="year" />

                <div class="event-name">
                    <p><spring:message code="event.date"/> <c:out value="${day}"/> <spring:message code="month.${month}"/> <c:out value="${year}"/></p>
                </div>

                <div class="event-description-container">
                    <p class="event-description"><c:out value="${param.eventDescription}"/></p>
                </div>
            </div>
        </div>
    </a>
</div>