<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="profile-header">
  <div class="profile-avatar-container">
    <div class="profiles-avatar">
      <c:choose>
        <c:when test="${not empty profileUser.profilePictureId && profileUser.profilePictureId > 0}">
          <img src="<c:url value='/images/${profileUser.profilePictureId}'/>" alt="<c:out value="${profileUser.username}"/>" class="avatar-image" />
        </c:when>
        <c:otherwise>
          <div class="avatar-placeholder">
            <c:out value="${fn:substring(profileUser.firstname, 0, 1).toUpperCase()}${fn:substring(profileUser.lastname, 0, 1).toUpperCase()}"/>
          </div>
        </c:otherwise>
      </c:choose>

      <!-- Profile Picture Edit Overlay (only visible on own profile) -->
      <c:if test="${isMine}">
        <div class="avatar-edit-overlay" onclick="window.location.href='<c:url value='/profile/edit-picture' />'">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
            <circle cx="12" cy="13" r="4"></circle>
          </svg>
        </div>
      </c:if>
    </div>

    <!-- Profile Picture Edit Button (mobile-friendly alternative) -->
    <c:if test="${isMine}">
      <button class="btn-picture-edit" onclick="window.location.href='<c:url value='/profile/edit-picture' />'">
        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
          <circle cx="12" cy="13" r="4"></circle>
        </svg>
        <span class="btn-text"><spring:message code="profile.edit.picture.button" text="Edit Photo"/></span>
      </button>
    </c:if>
  </div>

  <div class="profile-info">
    <h1 class="profile-name"><c:out value="${profileUser.firstname} ${profileUser.lastname}"/></h1>
    <p class="profile-username">@<c:out value="${profileUser.username}"/></p>
  </div>

  <c:if test="${isMine}">
    <div class="profile-actions">
      <div class="action-buttons-group">
        <!-- Edit Profile Information Button -->
        <button class="btn-primary btn-edit-info" onclick="window.location.href='<c:url value='/profile/edit' />'">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
          </svg>
          <span class="btn-text"><spring:message code="profile.edit" text="Edit Profile"/></span>
        </button>

        <!-- Change Password Button -->
        <button class="btn-secondary btn-change-password" onclick="window.location.href='<c:url value='/profile/changePassword' />'">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
            <circle cx="12" cy="16" r="1"></circle>
            <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
          </svg>
          <span class="btn-text"><spring:message code="profile.edit.password" text="Change Password"/></span>
        </button>
      </div>

      <!-- Quick Actions Dropdown (for smaller screens) -->
      <div class="profile-actions-dropdown">
        <button class="btn-dropdown-toggle" id="profileActionsToggle">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="1"></circle>
            <circle cx="12" cy="5" r="1"></circle>
            <circle cx="12" cy="19" r="1"></circle>
          </svg>
        </button>

        <div class="dropdown-menu" id="profileActionsMenu">
          <a href="<c:url value='/profile/edit-picture' />" class="dropdown-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path>
              <circle cx="12" cy="13" r="4"></circle>
            </svg>
            <spring:message code="profile.edit.picture.button" text="Edit Photo"/>
          </a>

          <a href="<c:url value='/profile/edit' />" class="dropdown-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
              <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            <spring:message code="profile.edit.button" text="Edit Profile"/>
          </a>

          <a href="<c:url value='/profile/changePassword' />" class="dropdown-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
              <circle cx="12" cy="16" r="1"></circle>
              <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
            </svg>
            <spring:message code="profile.edit.password" text="Change Password"/>
          </a>
        </div>
      </div>
    </div>
  </c:if>
</div>
