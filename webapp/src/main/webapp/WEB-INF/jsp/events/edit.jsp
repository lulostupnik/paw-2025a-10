<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="event.edit.title" text="Edit Event"/></title>
    <!-- Include CSS files -->
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/password-strength.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>
<script>
    // Form functionality
    document.addEventListener('DOMContentLoaded', function() {
        const attendeesLimit = document.getElementById('attendeesLimit');
        const noAttendeesLimit = document.getElementsByName('noAttendeesLimit').item(0);
        const attendeesLabel = document.getElementsByName("attendees-label").item(0);

        if (attendeesLimit && noAttendeesLimit) {
            noAttendeesLimit.addEventListener('change', function() {
                setAttendeesForm(attendeesLimit, noAttendeesLimit, attendeesLabel);
            });

            if (attendeesLimit.value == null || attendeesLimit.value == "" || attendeesLimit.value == "0") {
                attendeesLimit.setAttribute("disabled", "true");
                attendeesLimit.value = "";
                attendeesLimit.classList.add("form-disabled");
                attendeesLabel.classList.remove("required-field");
                noAttendeesLimit.checked = true;
            } else {
                attendeesLimit.removeAttribute("disabled");
                attendeesLimit.classList.remove("form-disabled");
                attendeesLabel.classList.add("required-field");
                noAttendeesLimit.checked = false;
            }
        }

        const timeInput = document.getElementById('time');
        const allDayEvent = document.getElementsByName('allDayEvent').item(0);
        const timeLabel = document.getElementsByName("time-label").item(0);
        if (timeInput && allDayEvent) {
            allDayEvent.addEventListener('change', function() {
                setTimeForm(timeInput, allDayEvent, timeLabel);
            });

            if (timeInput.value == null || timeInput.value == "") {
                timeInput.setAttribute("disabled", "true");
                timeInput.value = "";
                timeInput.classList.add("form-disabled");
                timeLabel.classList.remove("required-field");
                allDayEvent.checked = true;
            } else {
                timeInput.removeAttribute("disabled");
                timeInput.classList.remove("form-disabled");
                timeLabel.classList.add("required-field");
                allDayEvent.checked = false;
            }
        }
    });

    function setTimeForm(timeInput, allDayEvent, timeLabel) {
        if (allDayEvent.checked == true) {
            timeInput.setAttribute("disabled", "true");
            timeInput.value = "";
            timeInput.classList.add("form-disabled");
            timeLabel.classList.remove("required-field");
            allDayEvent.checked = true;
        } else {
            timeInput.removeAttribute("disabled");
            timeInput.value = "00:00";
            timeInput.classList.remove("form-disabled");
            timeLabel.classList.add("required-field");
            allDayEvent.checked = false;
        }
    }
    function setAttendeesForm(attendeesLimit, noAttendeesLimit, attendeesLabel) {
        if (noAttendeesLimit.checked == true) {
            attendeesLimit.setAttribute("disabled", "true");
            attendeesLimit.value = "";
            attendeesLimit.classList.add("form-disabled");
            attendeesLabel.classList.remove("required-field");
            noAttendeesLimit.checked = true;
        } else {
            attendeesLimit.removeAttribute("disabled");
            attendeesLimit.value = "0";
            attendeesLimit.classList.remove("form-disabled");
            attendeesLabel.classList.add("required-field");
            noAttendeesLimit.checked = false;
        }
    }
</script>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="event.edit.header" text="Edit Event"/></h1>
            <p class="auth-subtitle"><spring:message code="event.edit.subtitle" text="Update your event details"/></p>
        </div>

        <c:url var="updateEventUrl" value="/events/${eventId}/update"/>
        <form:form modelAttribute="editEventForm" action="${updateEventUrl}" method="post" enctype="multipart/form-data" class="auth-form" novalidate="true">

            <form:input type="hidden" value="${eventId}" path="id"/>
            <!-- Title Field -->
            <div class="form-group">
                <form:label path="title" cssClass="form-label required-field">
                    <spring:message code="event.name"/>
                </form:label>
                <c:set var="title"><spring:message code="event.name.hint"/></c:set>
                <form:input path="title" cssClass="form-input ${not empty errors.getFieldError('title') ? 'error' : ''}"
                            placeholder="${title}" />
                <form:errors path="title" cssClass="error-message" />
            </div>

            <!-- City Field with Enhanced Autocomplete -->
            <div class="form-group">
                <form:label path="city" cssClass="form-label required-field">
                    <spring:message code="event.city"/>
                </form:label>
                <div class="autocomplete-wrapper">
                    <select  id="city" class="form-select ${not empty errors.getFieldError('city') ? 'error' : ''}" style="display: none;">
                        <option value=""><spring:message code="createJourney.destinationCity.select"/></option>
                        <c:forEach var="item" items="${cities}">
                            <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                        </c:forEach>
                    </select>
                    <c:set var="citySearch"><spring:message code="event.city.search"/></c:set>
                    <form:input path="city" type="text" id="citySearch" class="autocomplete-input" placeholder="${citySearch}" />
                    <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
                        <c:forEach var="item" items="${cities}">
                            <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                                <c:out value="${item.name}"/>
                            </div>
                        </c:forEach>
                    </div>
                    <!-- Container for selected city tag -->
                    <div id="selectedCity" class="selected-tags"></div>
                </div>
                <form:errors path="city" cssClass="error-message" />
            </div>

            <div class="form-row">
                <!-- Date Field -->
                <div class="form-group" style="width: 50%;">
                    <form:label path="date" cssClass="form-label required-field">
                        <spring:message code="event.date"/>
                    </form:label>
                    <form:input path="date" type="date" id="date" cssClass="form-input ${not empty errors.getFieldError('date') ? 'error' : ''}" />
                    <form:errors path="date" cssClass="error-message" />
                </div>

                <div class="form-row" style="width: 50%;">
                    <!-- Time Field -->
                    <div class="form-group">
                        <form:label path="time" name="time-label" cssClass="form-label required-field">
                            <spring:message code="event.time"/>
                        </form:label>
                        <form:input path="time" type="time" cssClass="form-input ${not empty errors.getFieldError('time') ? 'error' : ''}" />
                        <form:errors path="time" cssClass="error-message" />
                    </div>
                    <div class="checkbox-container">
                        <label class="checkbox-label">
                            <input type="checkbox" name="allDayEvent"
                                   class="checkbox-custom" />
                        </label>
                        <span><spring:message code="event.allDayEvent"/></span>
                    </div>
                </div>
            </div>

            <!-- Description Field -->
            <div class="form-group">
                <form:label path="description" cssClass="form-label required-field">
                    <spring:message code="event.description"/>
                </form:label>
                <c:set var="descriptionHint"><spring:message code="event.description.hint"/></c:set>
                <form:textarea path="description"
                               cssClass="form-textarea ${not empty errors.getFieldError('description') ? 'error' : ''}"
                               placeholder="${descriptionHint}"
                />
                <form:errors path="description" cssClass="error-message" />
            </div>

            <!-- Address Field -->
            <div class="form-group">
                <form:label path="address" cssClass="form-label">
                    <spring:message code="event.address"/>
                </form:label>
                <c:set var="addressHint"><spring:message code="event.address.hint"/></c:set>
                <form:input path="address" cssClass="form-input ${not empty errors.getFieldError('address') ? 'error' : ''}"
                            placeholder="${addressHint}" />
                <form:errors path="address" cssClass="error-message" />
            </div>

            <!-- Attendees limit Field -->
            <div class="form-row">
                <div class="form-group">
                    <form:label path="attendeesLimit" name="attendees-label" cssClass="form-label required-field">
                        <spring:message code="event.attendeesLimit"/>
                    </form:label>
                    <form:input path="attendeesLimit" cssClass="form-input ${not empty errors.getFieldError('attendeesLimit') ? 'error' : ''}"/>
                    <form:errors path="attendeesLimit" cssClass="error-message" />
                    <form:errors path="" cssClass="error-message" />
                </div>
                <div class="checkbox-container">
                    <label class="checkbox-label">
                        <input type="checkbox" name="noAttendeesLimit"
                               class="checkbox-custom"/>
                    </label>
                    <span><spring:message code="event.noAttendeesLimit"/></span>
                </div>
            </div>

            <!-- Enhanced file upload area for Flyer -->
            <div class="form-group">
                <form:label path="flyer" cssClass="form-label">
                    <spring:message code="event.flyer"/>
                </form:label>
                <div class="file-upload">
                    <label class="file-upload-label">
                        <svg xmlns="http://www.w3.org/2000/svg" class="file-upload-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span class="file-upload-text">
                            <spring:message code="upload_picture.flyer"/>
                        </span>
                        <span class="file-upload-hint">
                            <spring:message code="upload_picture.hint" text="JPG or PNG, max 5MB"/>
                        </span>
                        <form:input path="flyer" id="eventFile" type="file" cssClass="file-upload-input" accept="image/png, image/jpeg" />
                    </label>
                </div>
                <div id="filePreview" class="file-preview" style="display: none;">
                    <img id="previewImage" class="file-preview-image" src="#" alt="Preview" />
                    <span id="fileName" class="file-preview-name"></span>
                    <button type="button" id="removeFile" class="file-preview-remove" aria-label="Remove file">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                            <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                        </svg>
                    </button>
                </div>
                <form:errors path="flyer" cssClass="error-message" />
                <p class="form-hint"><spring:message code="event.flyer.edit.hint" text="Leave empty to keep current flyer"/></p>
            </div>

            <button type="submit" class="form-button">
                <spring:message code="event.update.button" text="Update Event"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <a  class="auth-link" href="<c:url value='/events/${eventId}'/>" class="btn-text">
                <spring:message code="event.back" text="Back to events"/>
            </a>
        </div>
    </div>
</div>

<!-- Include modularized JavaScript files -->
<script>
    window.apiBaseUrl = '<c:url value="/" />';
    eventSelectedCity = '<c:out value="${createEventForm.city}"/>';
</script>
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/file-upload.js'/>"></script>
<script src="<c:url value='/resources/js/components/date-validation.js'/>"></script>
<script src="<c:url value='/resources/js/event-form.js'/>"></script>
</body>
</html>