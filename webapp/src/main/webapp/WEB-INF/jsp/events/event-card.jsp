<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<c:set var="isOwner" value="${param.isOwner == 'true'}"/>
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

                <!-- Attend Button -->
                <c:if test="${not empty username && isOwner == false}">
                    <div class="attend-button-container">
                        <button type="button"
                                class="attend-button ${param.attend ? 'attended' : ''}"
                                data-event-id="<c:out value="${param.eventId}"/>"
                                data-event-title="<c:out value="${param.title}"/>"
                                data-is-attending="${param.attend}"
                                onclick="openAttendanceModal(event, this)"
                                aria-label="<spring:message code='event.attend'/>">
                            <c:if test="${param.attend}">
                                <img src="<c:url value='/resources/icons/check.svg'/>" alt="<spring:message code='event.attending'/>" class="btn-icon" />
                            </c:if>
                            <c:if test="${not param.attend}">
                                <img src="<c:url value='/resources/icons/calendar-plus.svg'/>" alt="<spring:message code='event.attend'/>" class="btn-icon" />
                            </c:if>
                        </button>
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
                    <div class="event-date">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span><c:out value="${param.date}"/></span>
                    </div>
                    <div class="event-organizer">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 016 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
                        <span class="organizer-name" title="<c:out value="${param.firstname} ${param.lastname}"/>"><c:out value="${param.firstname} ${param.lastname}"/></span>
                    </div>
                </div>
            </div>
        </div>
    </a>
</div>

<!-- Attendance Modal -->
<c:if test="${isOwner == false}">
    <div id="attendanceModal" class="attendance-modal">
        <div class="attendance-modal-content">
            <div class="attendance-modal-header">
                <h3 id="attendanceModalTitle" class="attendance-modal-title"></h3>
                <button type="button" class="attendance-modal-close" onclick="closeAttendanceModal()">
                    <svg xmlns="http://www.w3.org/2000/svg" class="modal-close-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </div>
            <div class="attendance-modal-body">
                <p id="attendanceModalMessage"></p>
            </div>
            <div class="attendance-modal-footer">
                <button type="button" class="btn-secondary" onclick="closeAttendanceModal()">
                    <spring:message code="event.cancel" />
                </button>
                <form id="attendanceForm" method="post" action="">
                    <input type="hidden" name="eventId" id="eventIdInput" value="" />
                    <button type="submit" id="confirmAttendanceBtn" class="btn-primary">
                        <spring:message code="event.confirm" />
                    </button>
                </form>
            </div>
        </div>
    </div>
</c:if>

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

    function openAttendanceModal(event, button) {
        event.preventDefault();
        event.stopPropagation();

        const eventId = button.getAttribute('data-event-id');
        const eventTitle = button.getAttribute('data-event-title');
        const isAttending = button.getAttribute('data-is-attending') === 'true';

        const modal = document.getElementById('attendanceModal');
        const titleElement = document.getElementById('attendanceModalTitle');
        const messageElement = document.getElementById('attendanceModalMessage');
        const form = document.getElementById('attendanceForm');
        const eventIdInput = document.getElementById('eventIdInput');
        const confirmButton = document.getElementById('confirmAttendanceBtn');

        // Set the event ID in the form
        eventIdInput.value = eventId;

        // Set the form action based on attendance status
        if (isAttending) {
            form.action = '<c:url value="/events/"/>' + eventId + '/dont-attend';
            titleElement.textContent = '<spring:message code="event.cancel.attendance" />';
            messageElement.textContent = '<spring:message code="event.unattend.message" arguments="' + eventTitle + '" />';
            confirmButton.classList.remove('btn-primary');
            confirmButton.classList.add('btn-danger');
            confirmButton.textContent = '<spring:message code="event.unattend.confirm" />';
        } else {
            form.action = '<c:url value="/events/"/>' + eventId + '/attend';
            titleElement.textContent = '<spring:message code="event.attend.title" />';
            messageElement.textContent = '<spring:message code="event.attend.message" arguments="' + eventTitle + '" />';
            confirmButton.classList.remove('btn-danger');
            confirmButton.classList.add('btn-primary');
            confirmButton.textContent = '<spring:message code="event.attend.confirm" />';
        }

        // Show the modal
        modal.classList.add('active');

        // Prevent scrolling on the body
        document.body.style.overflow = 'hidden';
    }

    function closeAttendanceModal() {
        const modal = document.getElementById('attendanceModal');
        modal.classList.remove('active');

        // Re-enable scrolling on the body
        document.body.style.overflow = '';
    }
</script>
