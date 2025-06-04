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
                  <c:out value="${fn:substring(profileUser.firstname, 0, 1).toUpperCase()}${fn:substring(user.lastname, 0, 1).toUpperCase()}"/>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
      <div class="profile-info">
        <h1 class="profile-name"><c:out value="${profileUser.firstname} ${profileUser.lastname}"/></h1>
        <p class="profile-username">@<c:out value="${profileUser.username}"/></p>
      </div>
      <c:if test="${isMine}">
        <button class="btn-primary"
                onclick="window.location.href='<c:url value='/profile/changePassword' />'">
          <spring:message code="profile.edit.password" text="Change password"/>
        </button>
      </c:if>


    </div>