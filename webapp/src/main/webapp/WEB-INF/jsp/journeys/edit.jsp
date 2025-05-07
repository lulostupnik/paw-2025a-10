<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title><spring:message code="journey.edit.title"/></title>
    <!-- Include custom CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="journey.edit.title"/></h1>
            <p class="auth-subtitle"><spring:message code="journey.edit.subtitle" text="Update your academic journey details"/></p>
        </div>

        <c:url var="updateJourneyUrl" value="/journeys/${journeyId}/update"/>
        <form:form modelAttribute="createJourneyForm" action="${updateJourneyUrl}" method="post" class="auth-form" id="journeyForm" novalidate="true">
            <!-- Start Date Field -->
            <div class="form-group">
                <form:label path="startDate" cssClass="form-label required-field">
                    <spring:message code="createJourney.startDate"/>
                </form:label>
                <form:input path="startDate" id="startDate" type="date" cssClass="form-input ${not empty errors.getFieldError('startDate') ? 'error' : ''}" />
                <form:errors path="startDate" cssClass="error-message" />
            </div>

            <!-- End Date Field -->
            <div class="form-group">
                <form:label path="endDate" cssClass="form-label required-field">
                    <spring:message code="createJourney.endDate"/>
                </form:label>
                <form:input path="endDate" id="endDate" type="date" cssClass="form-input ${not empty errors.getFieldError('endDate') ? 'error' : ''}" />
                <form:errors path="endDate" cssClass="error-message" />
            </div>

            <!-- Destination University Field with Enhanced Autocomplete -->
            <div class="form-group">
                <form:label path="destinationUniversity" cssClass="form-label required-field">
                    <spring:message code="createJourney.destinationUniversity"/>
                </form:label>
                <div class="autocomplete-wrapper">
                    <form:select path="destinationUniversity" id="destinationUniversity" cssClass="form-select ${not empty errors.getFieldError('destinationUniversity') ? 'error' : ''}" style="display: none;">
                        <form:option value=""><spring:message code="createJourney.destinationUniversity.select"/></form:option>
                        <c:forEach var="item" items="${universities}">
                            <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <input type="text" id="universitySearch" class="form-input autocomplete-input" placeholder="<spring:message code="createJourney.destinationUniversity.search" text="Type to search university..."/>" />
                    <div id="universityDropdown" class="autocomplete-dropdown" style="display: none;">
                        <c:forEach var="item" items="${universities}">
                            <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                                <c:out value="${item.name}"/>
                            </div>
                        </c:forEach>
                    </div>
                    <!-- Container for selected universities -->
                    <div id="selectedUniversities" class="selected-tags"></div>
                </div>
                <form:errors path="destinationUniversity" cssClass="error-message" />
            </div>

            <!-- Description Field -->
            <div class="form-group">
                <form:label path="description" cssClass="form-label required-field">
                    <spring:message code="createJourney.description"/>
                </form:label>

                <c:set var="descriptionHint"><spring:message code="createJourney.description.hint"/></c:set>
                <form:textarea path="description"
                               cssClass="form-textarea ${not empty errors.getFieldError('description') ? 'error' : ''}"
                               placeholder="${descriptionHint}"
                />
                <form:errors path="description" cssClass="error-message" />
            </div>

            <button type="submit" class="form-button">
                <spring:message code="journey.edit.submit"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <a href="<c:url value='/profile/info' />" class="auth-link">
                <spring:message code="journey.back" text="Back to journeys"/>
            </a>
        </div>

    </div>
</div>

<!-- Include modularized JavaScript files -->
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/date-validation.js'/>"></script>
<script src="<c:url value='/resources/js/journey-form.js'/>"></script>

</body>
</html>