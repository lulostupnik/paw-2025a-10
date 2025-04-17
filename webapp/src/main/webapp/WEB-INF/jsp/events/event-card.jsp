<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="event-card-wrapper">
    <a href="<c:url value="/events/${param.eventId}"/>" class="event-card-link">
        <div class="event-card">
            <!-- Image Container -->
            <div class="event-image-container">
                <c:if test="${not empty param.flyerImageId}">
                    <img src="<c:url value="/images/${param.flyerImageId}"/>"
                         alt="<spring:message code='event.flyer.alt'/>"
                         class="event-image">
                </c:if>
                <c:if test="${empty param.flyerImageId}">
                    <div class="event-image-placeholder">
                        <i class="fas fa-calendar-alt"></i>
                    </div>
                </c:if>

                <!-- Favorite Badge -->
                <div class="favorite-badge">
                    <spring:message code="event.favorite.badge"/>
                </div>

                <!-- Attend Button -->
                <button class="attend-button" data-event-id="${param.eventId}" aria-label="<spring:message code='event.attend'/>" onclick="toggleAttendance(event, ${param.eventId}, this)">
                    <i class="fas fa-calendar-check"></i>
                </button>
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