<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="event.create.title"/></title>
    <!-- Include CSS files -->
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/password-strength.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="event.create.header"/></h1>
            <p class="auth-subtitle"><spring:message code="event.create.subtitle" text="Create a new event to share with others"/></p>
        </div>

        <c:url var="createEventUrl" value="/events/create"/>
        <form:form modelAttribute="createEventForm" action="${createEventUrl}" method="post" enctype="multipart/form-data" class="auth-form">
            <!-- City Field with Enhanced Autocomplete -->
            <div class="form-group">
                <div class="form-group">
                    <form:label path="title" cssClass="form-label required-field">
                        <spring:message code="event.name"/>
                    </form:label>
                    <c:set var="title"><spring:message code="event.name.hint"/></c:set>
                    <form:input path="title" cssClass="form-input ${not empty errors.getFieldError('title') ? 'error' : ''}"
                                placeholder="${title}" required="true" />
                    <form:errors path="title" cssClass="error-message" />
                </div>

                <form:label path="city" cssClass="form-label required-field">
                    <spring:message code="event.city"/>
                </form:label>
                <div class="autocomplete-wrapper">
                    <form:select path="city" id="city" cssClass="form-select ${not empty errors.getFieldError('city') ? 'error' : ''}" required="true" style="display: none;">
                        <form:option value=""><spring:message code="createJourney.destinationCity.select"/></form:option>
                        <c:forEach var="item" items="${cities}">
                            <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <input type="text" id="citySearch" class="autocomplete-input" placeholder="<spring:message code="event.city.search" text="Type to search city..."/>" />
                    <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
                        <c:forEach var="item" items="${cities}">
                            <div class="autocomplete-item" data-value="${item.name}">
                                <c:out value="${item.name}"/>
                            </div>
                        </c:forEach>
                    </div>
                    <!-- Container for selected city tag -->
                    <div id="selectedCity" class="selected-tags"></div>
                </div>
                <form:errors path="city" cssClass="error-message" />
            </div>

            <!-- Date Field -->
            <div class="form-group">
                <form:label path="date" cssClass="form-label required-field">
                    <spring:message code="event.date"/>
                </form:label>
                <form:input path="date" type="date" cssClass="form-input ${not empty errors.getFieldError('date') ? 'error' : ''}" required="true" />
                <form:errors path="date" cssClass="error-message" />
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
                               required="true" />
                <form:errors path="description" cssClass="error-message" />
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
            </div>

            <button type="submit" class="form-button">
                <spring:message code="event.create.button"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <a href="<c:url value='/events'/>" class="auth-link">
                <spring:message code="event.back" text="Back to events"/>
            </a>
        </div>
    </div>
</div>

<!-- Include modularized JavaScript files -->
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/file-upload.js'/>"></script>
<script src="<c:url value='/resources/js/event-form.js'/>"></script>
</body>
</html>
