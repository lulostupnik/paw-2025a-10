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
                <spring:message code="profile.edit.picture.title" text="Edit Profile"/>
            </h1>
            <p class="auth-subtitle">
                <spring:message code="profile.edit.picture.subtitle" text="Update your profile information and picture"/>
            </p>
        </div>

        <c:url var="formAction" value="/profile/edit-picture"/>

        <form:form modelAttribute="editPictureForm" action="${formAction}" method="post"
                   class="auth-form profile-edit-form" enctype="multipart/form-data" novalidate="true">

            <!-- Profile Picture Section -->
                        <div class="profile-picture-section">
                            <div class="current-avatar-container">
                                <div class="current-avatar" id="currentAvatar">
                                    <c:choose>
                                        <c:when test="${not empty user.profilePictureId && user.profilePictureId > 0}">
                                            <img src="<c:url value='/images/${user.profilePictureId}'/>"
                                                 alt="<c:out value='${user.username}'/>"
                                                 class="avatar-image" id="avatarPreview" />
                                        </c:when>
                                        <c:otherwise>
                                            <div class="avatar-placeholder" id="avatarPlaceholder">
                                                <c:out value="${fn:substring(user.firstname, 0, 1).toUpperCase()}${fn:substring(user.lastname, 0, 1).toUpperCase()}"/>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="avatar-overlay">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
                                        <circle cx="12" cy="13" r="4"></circle>
                                    </svg>
                                </div>
                            </div>

                            <div class="form-group">
                                <form:label path="picture" cssClass="form-label">
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
                                        <form:input path="picture" id="eventFile" type="file" cssClass="file-upload-input" accept="image/png, image/jpeg" />
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
                                <form:errors path="picture" cssClass="error-message" />
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

<script src="<c:url value='/resources/js/profile-picture-edit.js'/>"></script>
<script src="<c:url value='/resources/js/components/file-upload.js'/>"></script>
</body>
</html>
