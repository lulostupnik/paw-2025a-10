<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title><spring:message code="register.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/password-strength.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/landing.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <!-- You can add your logo here -->
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="register.title"/></h1>
            <p class="auth-subtitle"><spring:message code="register.subtitle" text="Create your account to get started"/></p>
        </div>

        <!-- Hidden internationalization messages for JavaScript -->
        <input type="hidden" id="i18n-password-very-weak" value="<spring:message code="password.strength.very-weak" text="Very Weak"/>" />
        <input type="hidden" id="i18n-password-weak" value="<spring:message code="password.strength.weak" text="Weak"/>" />
        <input type="hidden" id="i18n-password-medium" value="<spring:message code="password.strength.medium" text="Medium"/>" />
        <input type="hidden" id="i18n-password-strong" value="<spring:message code="password.strength.strong" text="Strong"/>" />
        <input type="hidden" id="i18n-password-very-strong" value="<spring:message code="password.strength.very-strong" text="Very Strong"/>" />
        <input type="hidden" id="i18n-password-strength" value="<spring:message code="password.strength.label" text="Password Strength"/>" />
        <input type="hidden" id="i18n-password-add-more" value="<spring:message code="password.strength.add-more" text="Add more complexity"/>" />
        <input type="hidden" id="i18n-password-good" value="<spring:message code="password.strength.good" text="Good password"/>" />
        <input type="hidden" id="i18n-password-great" value="<spring:message code="password.strength.great" text="Great password"/>" />
        <input type="hidden" id="i18n-password-req-length" value="<spring:message code="password.req.length" text="8+ characters"/>" />
        <input type="hidden" id="i18n-password-req-lowercase" value="<spring:message code="password.req.lowercase" text="Lowercase letter"/>" />
        <input type="hidden" id="i18n-password-req-uppercase" value="<spring:message code="password.req.uppercase" text="Uppercase letter"/>" />
        <input type="hidden" id="i18n-password-req-number" value="<spring:message code="password.req.number" text="Number"/>" />
        <input type="hidden" id="i18n-password-req-special" value="<spring:message code="password.req.special" text="Special character"/>" />
        <input type="hidden" id="i18n-password-match" value="<spring:message code="password.match" text="Passwords match"/>" />
        <input type="hidden" id="i18n-password-mismatch" value="<spring:message code="password.mismatch" text="Passwords do not match"/>" />
        <input type="hidden" id="i18n-password-show" value="<spring:message code="password.show" text="Show password"/>" />
        <input type="hidden" id="i18n-password-hide" value="<spring:message code="password.hide" text="Hide password"/>" />
        <input type="hidden" id="i18n-interests-none" value="<spring:message code="interests.none.selected" text="No interests selected"/>" />

        <c:url var="registerUrl" value="/register"/>
        <form:form modelAttribute="createUserForm" action="${registerUrl}" method="post" enctype="multipart/form-data" class="auth-form">
            <div class="auth-columns">
                <div class="auth-column">
                    <!-- Personal Information -->
                    <div class="form-group">
                        <form:label path="email" cssClass="form-label required-field">
                            <spring:message code="createJourney.userEmail"/>
                        </form:label>
                        <form:input path="email" type="email" cssClass="form-input ${not empty errors.getFieldError('email') ? 'error' : ''}"
                                    placeholder="example@email.com" required="true" />
                        <form:errors path="email" cssClass="error-message" />
                    </div>

                    <!-- Enhanced Password Field with Strength Meter -->
                    <div class="form-group">
                        <form:label path="password" cssClass="form-label required-field">
                            <spring:message code="createJourney.password"/>
                        </form:label>
                        <div class="password-field-container">
                            <form:password path="password" id="password"
                                           cssClass="form-input ${not empty errors.getFieldError('password') ? 'error' : ''}"
                                           placeholder="••••••••" required="true" />
                            <button type="button" id="togglePassword" class="password-toggle-button" aria-label="Show password">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="eyeIcon">
                                    <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                    <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                </svg>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="eyeSlashIcon" style="display: none;">
                                    <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                                    <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                                    <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                                </svg>
                            </button>
                        </div>
                        <form:errors path="password" cssClass="error-message" />

                        <!-- Single password strength meter -->
                        <div class="password-strength">
                            <div class="password-meter">
                                <div class="password-bar" id="passwordStrengthBar"></div>
                            </div>
                            <div class="password-feedback">
                                <div class="password-status">
                                    <span id="passwordStrengthLabel"></span>
                                </div>
                                <span class="password-message" id="passwordMessage"></span>
                            </div>

                            <!-- Password requirements -->
                            <div class="password-requirements">
                                <div class="requirement-item" id="req-length">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.length" text="8+ characters"/></span>
                                </div>
                                <div class="requirement-item" id="req-lowercase">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.lowercase" text="Lowercase letter"/></span>
                                </div>
                                <div class="requirement-item" id="req-uppercase">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.uppercase" text="Uppercase letter"/></span>
                                </div>
                                <div class="requirement-item" id="req-number">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.number" text="Number"/></span>
                                </div>
                                <div class="requirement-item" id="req-special">
                                    <svg class="requirement-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor" width="16" height="16">
                                        <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd" />
                                    </svg>
                                    <span><spring:message code="password.req.special" text="Special character"/></span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Password Confirmation Field -->
                    <div class="form-group">
                        <label for="confirmPassword" class="form-label required-field">
                            <spring:message code="register.confirmPassword" text="Confirm Password"/>
                        </label>
                        <div class="password-field-container">
                            <input type="password" id="confirmPassword" name="confirmPassword"
                                   class="form-input" placeholder="••••••••" required />
                            <button type="button" id="toggleConfirmPassword" class="password-toggle-button" aria-label="Show password">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="confirmEyeIcon">
                                    <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z"/>
                                    <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"/>
                                </svg>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16" id="confirmEyeSlashIcon" style="display: none;">
                                    <path d="M13.359 11.238C15.06 9.72 16 8 16 8s-3-5.5-8-5.5a7.028 7.028 0 0 0-2.79.588l.77.771A5.944 5.944 0 0 1 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.134 13.134 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755-.165.165-.337.328-.517.486l.708.709z"/>
                                    <path d="M11.297 9.176a3.5 3.5 0 0 0-4.474-4.474l.823.823a2.5 2.5 0 0 1 2.829 2.829l.822.822zm-2.943 1.299.822.822a3.5 3.5 0 0 1-4.474-4.474l.823.823a2.5 2.5 0 0 0 2.829 2.829z"/>
                                    <path d="M3.35 5.47c-.18.16-.353.322-.518.487A13.134 13.134 0 0 0 1.172 8l.195.288c.335.48.83 1.12 1.465 1.755C4.121 11.332 5.881 12.5 8 12.5c.716 0 1.39-.133 2.02-.36l.77.772A7.029 7.029 0 0 1 8 13.5C3 13.5 0 8 0 8s.939-1.721 2.641-3.238l.708.709zm10.296 8.884-12-12 .708-.708 12 12-.708.708z"/>
                                </svg>
                            </button>
                        </div>
                        <div id="passwordMatchMessage" class="password-match-message"></div>
                    </div>

                    <div class="form-group">
                        <form:label path="firstName" cssClass="form-label required-field">
                            <spring:message code="createJourney.firstName"/>
                        </form:label>
                        <c:set var="john"><spring:message code="john"/></c:set>
                        <form:input path="firstName" cssClass="form-input ${not empty errors.getFieldError('firstName') ? 'error' : ''}"
                                    placeholder="${john}" required="true" />
                        <form:errors path="firstName" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="lastName" cssClass="form-label required-field">
                            <spring:message code="createJourney.lastName"/>
                        </form:label>
                        <c:set var="doe"><spring:message code="doe"/></c:set>
                        <form:input path="lastName" cssClass="form-input ${not empty errors.getFieldError('lastName') ? 'error' : ''}"
                                    placeholder="${doe}" required="true" />
                        <form:errors path="lastName" cssClass="error-message" />
                    </div>
                </div>

                <div class="auth-column">
                    <div class="form-group">
                        <form:label path="username" cssClass="form-label required-field">
                            <spring:message code="createJourney.username"/>
                        </form:label>
                        <c:set var="johnUsername"><spring:message code="john.username"/></c:set>
                        <form:input path="username" cssClass="form-input ${not empty errors.getFieldError('username') ? 'error' : ''}"
                                    placeholder="${johnUsername}" required="true" />
                        <form:errors path="username" cssClass="error-message" />
                    </div>

                    <!-- Career field with enhanced autocomplete -->
                    <div class="form-group">
                        <form:label path="career" cssClass="form-label required-field">
                            <spring:message code="event.career"/>
                        </form:label>
                        <div class="autocomplete-wrapper">
                            <form:select path="career" id="career" cssClass="form-select ${not empty errors.getFieldError('career') ? 'error' : ''}" required="true" style="display: none;">
                                <form:option value=""><spring:message code="event.career.select"/></form:option>
                                <c:forEach var="item" items="${careers}">
                                    <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                                </c:forEach>
                            </form:select>
                            <input type="text" id="careerSearch" class="autocomplete-input" placeholder="<spring:message code="event.career.search" text="Type to search..."/>" />
                            <div id="careerDropdown" class="autocomplete-dropdown" style="display: none;">
                                <c:forEach var="item" items="${careers}">
                                    <div class="autocomplete-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                            <!-- Container for selected career tag -->
                            <div id="selectedCareer" class="selected-tags"></div>
                        </div>
                        <form:errors path="career" cssClass="error-message" />
                    </div>

                    <!-- Origin University field with enhanced autocomplete -->
                    <div class="form-group">
                        <form:label path="originUniversity" cssClass="form-label required-field">
                            <spring:message code="createJourney.originUniversity"/>
                        </form:label>
                        <div class="autocomplete-wrapper">
                            <form:select path="originUniversity" id="originUniversity" cssClass="form-select ${not empty errors.getFieldError('originUniversity') ? 'error' : ''}" required="true" style="display: none;">
                                <form:option value=""><spring:message code="createJourney.originUniversity.select"/></form:option>
                                <c:forEach var="item" items="${universities}">
                                    <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                                </c:forEach>
                            </form:select>
                            <input type="text" id="universitySearch" class="autocomplete-input" placeholder="<spring:message code="createJourney.originUniversity.search" text="Type to search..."/>" />
                            <div id="universityDropdown" class="autocomplete-dropdown" style="display: none;">
                                <c:forEach var="item" items="${universities}">
                                    <div class="autocomplete-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                            <!-- Container for selected university tag -->
                            <div id="selectedUniversity" class="selected-tags"></div>
                        </div>
                        <form:errors path="originUniversity" cssClass="error-message" />
                    </div>

                    <!-- Enhanced interests section with improved autocomplete and multi-select -->
                    <div class="form-group">
                        <form:label path="interests" cssClass="form-label">
                            <spring:message code="event.interest"/>
                        </form:label>

                        <!-- Hidden select that will hold the actual form data -->
                        <form:select path="interests" multiple="true" id="interestsSelect" style="display: none;">
                            <c:forEach var="item" items="${interests}">
                                <option value="${item.name}"><c:out value="${item.name}"/></option>
                            </c:forEach>
                        </form:select>

                        <!-- Custom UI for interests selection -->
                        <div class="autocomplete-wrapper">
                            <input type="text" id="interestSearch" class="autocomplete-input"
                                   placeholder="<spring:message code="event.interest.search" text="Search interests..."/>" />

                            <div id="interestDropdown" class="autocomplete-dropdown" style="display: none;">
                                <c:forEach var="item" items="${interests}">
                                    <div class="autocomplete-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <!-- Selected interests will appear here as tags -->
                        <div id="selectedInterests" class="selected-tags"></div>

                        <form:errors path="interests" cssClass="error-message" />
                    </div>

                    <!-- Enhanced file upload area -->
                    <div class="form-group">
                        <form:label path="profilePicture" cssClass="form-label">
                            <spring:message code="createJourney.profile_picture"/>
                        </form:label>
                        <div class="file-upload">
                            <label class="file-upload-label">
                                <svg xmlns="http://www.w3.org/2000/svg" class="file-upload-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                <span class="file-upload-text">
                                    <spring:message code="upload_picture.profile"/>
                                </span>
                                <span class="file-upload-hint">
                                    <spring:message code="upload_picture.hint" text="JPG or PNG, max 5MB"/>
                                </span>
                                <form:input path="profilePicture" type="file" cssClass="file-upload-input" accept="image/png, image/jpeg" />
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
                        <form:errors path="profilePicture" cssClass="error-message" />
                    </div>
                </div>
            </div>

            <button type="submit" class="form-button">
                <spring:message code="register.submit"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <spring:message code="register.have.account" text="Already have an account?"/>
            <a href="<c:url value='/login'/>" class="auth-link">
                <spring:message code="register.login" text="Sign in"/>
            </a>
        </div>
    </div>
</div>

<!-- Include modularized JavaScript files -->
<script src="<c:url value='/resources/js/components/password-strength.js'/>"></script>
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/file-upload.js'/>"></script>
<script src="<c:url value='/resources/js/register.js'/>"></script>

</body>
</html>
