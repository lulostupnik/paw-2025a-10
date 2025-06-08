<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="profile.edit.title" text="Edit Profile"/></title>

    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/profile-edit.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>

<div class="auth-container">
    <div class="auth-card profile-edit-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
            </div>
            <h1 class="auth-title">
                <spring:message code="profile.edit.title" text="Edit Profile"/>
            </h1>
            <p class="auth-subtitle">
                <spring:message code="profile.edit.subtitle" text="Update your profile information and picture"/>
            </p>
        </div>

        <c:url var="formAction" value="/profile/edit"/>

        <form:form modelAttribute="editUserForm" action="${formAction}" method="post"
                   class="auth-form profile-edit-form" enctype="multipart/form-data" novalidate="true">

            <form:hidden path="userId" value="${user.id}" />
            <!-- Personal Information Section -->
            <div class="form-section">
                <h3 class="section-title">
                    <spring:message code="profile.edit.personal.info" text="Personal Information"/>
                </h3>

                <div class="form-row">
                    <div class="form-group">
                        <form:label path="firstName" cssClass="form-label required-field">
                            <spring:message code="profile.firstname" text="First Name"/>
                        </form:label>
                        <form:input path="firstName" id="firstName"
                                   cssClass="form-input required ${not empty errors.getFieldError('firstName') ? 'error' : ''}"
                                   placeholder="" />
                        <form:errors path="firstName" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="lastName" cssClass="form-label required-field">
                            <spring:message code="profile.lastname" text="Last Name"/>
                        </form:label>
                        <form:input path="lastName" id="lastName"
                                   cssClass="form-input required ${not empty errors.getFieldError('lastName') ? 'error' : ''}"
                                   placeholder="" />
                        <form:errors path="lastName" cssClass="error-message" />
                    </div>
                </div>

                <div class="form-group">
                    <form:label path="username" cssClass="form-label required-field">
                        <spring:message code="profile.username" text="Username"/>
                    </form:label>
                    <form:input path="username" id="username"
                               cssClass="form-input required ${not empty errors.getFieldError('username') ? 'error' : ''}"
                               placeholder="" />
                    <form:errors path="username" cssClass="error-message" />
                </div>
            </div>

            <!-- Academic Information Section -->
            <div class="form-section">
                <h3 class="section-title">
                    <spring:message code="profile.edit.academic.info" text="Academic Information"/>
                </h3>

                <div class="form-group">
                    <form:label path="originUniversity" cssClass="form-label">
                        <spring:message code="university.detail.title" text="University"/>
                    </form:label>
                    <div class="autocomplete-wrapper">
                        <select  id="originUniversity" class="form-select ${not empty errors.getFieldError('originUniversity') ? 'error' : ''}" style="display: none;">
                            <option value=""><spring:message code="createJourney.originUniversity.select"/></option>
                            <c:forEach var="item" items="${universities}">
                                <option value="<c:out value="${item.name}"/>"><c:out value="${item.name}"/></option>
                            </c:forEach>
                        </select>
                        <c:set var="searchUni"><spring:message code="createJourney.originUniversity.search" text="Type to search..."/></c:set>
                        <form:input path="originUniversity" type="text" id="universitySearch" class="autocomplete-input ${not empty errors.getFieldError('originUniversity') ? 'error' : ''}" placeholder="${searchUni}" />
                        <div id="universityDropdown" class="autocomplete-dropdown" style="display: none;">
                            <c:forEach var="item" items="${universities}">
                                <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                                    <c:out value="${item.name}"/>
                                </div>
                            </c:forEach>
                        </div>

                        <div id="selectedUniversity" class="selected-tags required-selected-tags"></div>
                    </div>

                    <form:errors path="originUniversity" cssClass="error-message" />
                </div>

                <div class="form-group">
                    <form:label path="career" cssClass="form-label">
                        <spring:message code="profile.career" text="Career"/>
                    </form:label>
                    <div class="autocomplete-wrapper">
                        <select id="career" class="form-select ${not empty errors.getFieldError('career') ? 'error' : ''}" style="display: none;">
                            <option value=""><spring:message code="event.career.select"/></option>
                            <c:forEach var="item" items="${careers}">
                                <option value="<c:out value="${item.name}"/>"><c:out value="${item.name}"/></option>
                            </c:forEach>
                        </select>
                        <c:set var="searchPlaceholder"><spring:message code="event.career.search" text="Type to search..."/></c:set>
                        <form:input type="text" path="career" id="careerSearch" class="autocomplete-input ${not empty errors.getFieldError('career') ? 'error' : ''}" placeholder="${searchPlaceholder}" />
                        <div id="careerDropdown" class="autocomplete-dropdown" style="display: none;">
                            <c:forEach var="item" items="${careers}">
                                <div class="autocomplete-item" data-value="<c:out value="${item.name}"/>">
                                    <c:out value="${item.name}"/>
                                </div>
                            </c:forEach>
                        </div>

                        <div id="selectedCareer" class="selected-tags required-selected-tags"></div>
                    </div>
                    <form:errors path="career" cssClass="error-message" />
                </div>
            </div>

            <div class="form-actions">
                <button type="submit" class="form-button">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"></path>
                        <polyline points="17,21 17,13 7,13 7,21"></polyline>
                        <polyline points="7,3 7,8 15,8"></polyline>
                    </svg>
                    <spring:message code="profile.save.changes" text="Save Changes"/>
                </button>
            </div>
        </form:form>

        <div class="auth-footer">
            <a href="<c:url value='/profile/${user.id}/info'/>" class="auth-link">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 12H5"></path>
                    <path d="M12 19l-7-7 7-7"></path>
                </svg>
                <spring:message code="profile.back.to.profile" text="Back to Profile"/>
            </a>
        </div>
    </div>
</div>

<script src="<c:url value='/resources/js/profile-edit.js'/>"></script>
<script>
    function htmlDecode(input) {
        const doc = new DOMParser().parseFromString(input, "text/html");
        return doc.documentElement.textContent;
    }
    window.apiBaseUrl = '<c:url value="/" />';
    selectedCareer = htmlDecode('<c:out value="${editUserForm.career}" />');
    selectedUniversity = htmlDecode('<c:out value="${editUserForm.originUniversity}" />');
</script>
<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
</body>
</html>
