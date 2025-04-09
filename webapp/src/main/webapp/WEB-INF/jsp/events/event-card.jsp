<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="event-card">
    <!-- Card Content -->
    <div class="event-card-content">
        <!-- Image -->
        <c:if test="${not empty param.flyerImageId}">
            <img src="<c:url value="/images/${param.flyerImageId}"/>"
                 alt="<spring:message code='event.flyer.alt'/>"
                 class="event-image">
        </c:if>

        <div class="event-info">
            <div>
                <h3 class="event-title">
                    <spring:message code="event.city"/><c:out value="${param.eventCity}"/>
                </h3>
                <p class="event-date">
                    <spring:message code="event.date"/><c:out value="${param.eventDate}"/>
                </p>
            </div>
        </div>
        <p class="event-description">
            <spring:message code="event.description"/><c:out value="${param.eventDescription}"/>
        </p>
    </div>

    <!-- Action Buttons -->
    <div class="event-actions">
        <div class="event-buttons">
            <a href="<c:url value="/events/${param.eventId}"/>" class="btn btn-secondary">
                <spring:message code="event.view.details"/>
            </a>
            <a href="<c:url value="/events/${param.eventId}/reply"/>" class="btn btn-primary">
                <spring:message code="event.reply"/>
            </a>
        </div>
    </div>
</div>