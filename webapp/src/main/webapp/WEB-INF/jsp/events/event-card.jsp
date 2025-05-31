<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script src="<c:url value='/resources/js/detect-overflow.js'/>"></script>
<c:set var="isOwner" value="${param.isOwner == 'true'}"/>
<div class="event-card-wrapper">


    <a href="<c:url value="/events/${param.eventId}"/>" onclick="saveLink()" class="event-card-link">
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

                <c:if test="${not empty user && isOwner == false}">
                    <div class="attend-button-container">
                        <c:if test="${ param.isFull}">
                            <div class="event-full-badge">
                                <spring:message code="event.full" />
                            </div>
                        </c:if>
                    </div>
                </c:if>

            </div>
            <div class="event-card-content">
                <div class="event-card-header">
                    <h3 class="event-card-title"><c:out value="${param.title}"/></h3>
                    <p class="event-card-subtitle"><c:out value="${param.city}"/></p>
                </div>
                <p class="event-card-description"><c:out value="${param.description}"/></p>
                <div class="event-card-footer">
                    <fmt:parseDate value="${param.date}" pattern="yyyy-MM-dd" var="parsedDate" />
                    <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy" var="formattedDate" />
                    <div class="event-date">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span><c:out value="${formattedDate}"/></span>
                    </div>
                    <div class="event-organizer">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 016 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        <span class="organizer-name" title="<c:out value="${param.username}"/>"><c:out value="${param.username}"/></span>
                    </div>
                </div>
            </div>
        </div>
    </a>
</div>


<script>
    // Prevent the event card link from triggering when clicking the attend button
    document.addEventListener('DOMContentLoaded', function() {
        const attendButtons = document.querySelectorAll('.attend-button');
        attendButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
            });
        });
    });

    // Add this function to your existing JavaScript
    function showFullEventMessage(event) {
        event.preventDefault();
        event.stopPropagation();

        const modal = document.getElementById('attendanceModal');
        const titleElement = document.getElementById('attendanceModalTitle');
        const messageElement = document.getElementById('attendanceModalMessage');
        const form = document.getElementById('attendanceForm');
        const confirmButton = document.getElementById('confirmAttendanceBtn');

        // Hide the form and confirm button
        form.style.display = 'none';

        // Set the modal content
        titleElement.textContent = '<spring:message code="event.full.title" />';
        messageElement.textContent = '<spring:message code="event.full.message" />';

        // Show the modal
        modal.classList.add('active');

        // Prevent scrolling on the body
        document.body.style.overflow = 'hidden';
    }

    function redirectToUpdate(eventId) {
        const baseUrl = '<c:url value="/" />';
        window.location.href = baseUrl + 'events/' + eventId + '/update';
    }

</script>
<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>
<script>
    function saveLink() {
        pushToNavigationStack(window.location.href);
    }
</script>
