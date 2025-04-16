<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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

    <div class="event-section">
        <h3 class="section-title">Flyer Image</h3>
        <!-- Display the flyer image if available -->
        <c:if test="${not empty event.flyerImageId}">
            <img src="<c:url value="${empty param.imagePathPrefix ? '' : param.imagePathPrefix}/images/${event.flyerImageId}"/>"
                 alt="Event Flyer" class="event-image" />
        </c:if>
    </div>
</div>
