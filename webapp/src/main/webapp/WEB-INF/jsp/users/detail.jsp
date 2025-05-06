<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title><spring:message code="user.detail.title"/></title>
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/detail.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="container">
  <div class="detail-container">
    <div class="featured-journey-card">
      <div class="journey-card-content">
        <div class="journey-card-header">
          <h1 class="journey-card-title"><c:out value="${user.firstname} ${user.lastname}"/></h1>
          <p class="journey-card-subtitle"><c:out value="${user.username}"/></p>
        </div>

        <div class="detail-content">
          <h2 class="section-title-landing"><spring:message code="user.detail.information"/></h2>

          <div class="features-grid">
            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.id"/></h3>
              <p class="feature-description"><c:out value="${user.id}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.email"/></h3>
              <p class="feature-description"><c:out value="${user.email}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.firstname"/></h3>
              <p class="feature-description"><c:out value="${user.firstname}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.lastname"/></h3>
              <p class="feature-description"><c:out value="${user.lastname}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.university"/></h3>
              <p class="feature-description"><c:out value="${user.university.name}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.career"/></h3>
              <p class="feature-description"><c:out value="${user.career.name}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.language"/></h3>
              <p class="feature-description"><c:out value="${user.locale}"/></p>
            </div>

              <div class="feature-card">
                <h3 class="feature-title"><spring:message code="user.detail.profilePicture"/></h3>
                <div class="profile-picture-container">
                  <img src="<c:url value='/images/${user.profilePictureId}'/>" alt="<spring:message code="user.detail.profilePicture.alt"/>" class="profile-picture" />
                </div>
              </div>

          </div>
        </div>
        <div class="detail-actions">
          <a href="<c:url value='/dashboard/users'/>" class="btn-text">
            <spring:message code="users.back" text="Go Back"/>
          </a>
          <c:choose>
            <c:when test="${user.blocked}">
              <form action="<c:url value='/users/${user.id}/unblock'/>" method="post" style="display: inline;">
                <button type="submit" class="btn-primary">
                  <spring:message code="user.detail.unblock" text="Unblock User"/>
                </button>
              </form>
            </c:when>
            <c:otherwise>
              <form action="<c:url value='/users/${user.id}/block'/>" method="post" style="display: inline;">
                <button type="submit" class="btn-danger">
                  <spring:message code="user.detail.block" text="Block User"/>
                </button>
              </form>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>