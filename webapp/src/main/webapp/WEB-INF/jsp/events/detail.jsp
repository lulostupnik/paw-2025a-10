<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title><spring:message code="event.detail.title"/></title>
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<div class="layout-container">
  <!-- Include the sidebar component -->
  <!-- Main Content -->
  <div class="main-content">
    <jsp:include page="../components/navbar.jsp" />
    <div class="content-container">
      <!-- Back Link -->
      <div class="back-link-container">
        <a href="<c:url value="/events/"/>" class="back-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
          </svg>
          <spring:message code="event.back"/>
        </a>
      </div>

      <div class="detail-page-container">
        <!-- Event Details Card -->
        <div class="detail-card">
          <!-- Header with event info and reply button -->
          <div class="detail-header">
            <div class="detail-header-content">
              <div class="event-info-primary">
                <h1 class="detail-title">
                  <svg xmlns="http://www.w3.org/2000/svg" class="detail-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                  <c:out value="${event.eventCity.name}"/>
                </h1>
                <div class="event-date-display">
                  <svg xmlns="http://www.w3.org/2000/svg" class="date-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                  <span class="date-text">
                    <fmt:formatDate value="${event.date}" pattern="EEEE, MMMM d, yyyy" />
                  </span>
                </div>
              </div>
            </div>
            <div class="detail-actions">
              <a href="<c:url value="/events/${event.id}/reply"/>" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" class="btn-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h10a8 8 0 018 8v2M3 10l6 6m-6-6l6-6" />
                </svg>
                <spring:message code="event.reply.button"/>
              </a>
            </div>
          </div>

          <!-- Event Flyer Image (if available) -->
          <c:if test="${not empty event.flyerImageId}">
            <div class="detail-section event-flyer-section">
              <img src="<c:url value='/images/${event.flyerImageId}'/>"
                   alt="<spring:message code='event.flyer.alt'/>"
                   class="event-flyer-image" />
            </div>
          </c:if>

          <!-- Description -->
          <div class="detail-section">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" class="section-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
              <spring:message code="event.description"/>
            </h2>
            <div class="description-content">
              <p class="event-description-text"><c:out value="${event.description}"/></p>
            </div>
          </div>

          <!-- Contact Info -->
          <div class="detail-section">
            <h2 class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" class="section-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207" />
              </svg>
              <spring:message code="event.contact"/>
            </h2>
            <div class="contact-info">
              <div class="user-contact">
                <div class="user-avatar small">
                  <c:if test="${not empty event.user.profilePictureId}">
                    <img src="<c:url value='/images/${event.user.profilePictureId}'/>" alt="Profile" class="avatar-img">
                  </c:if>
                  <c:if test="${empty event.user.profilePictureId}">
                    <div class="avatar-placeholder">
                        ${event.user.firstname.charAt(0)}${event.user.lastname.charAt(0)}
                    </div>
                  </c:if>
                </div>
                <div class="user-details">
                  <p class="user-name"><c:out value="${event.user.firstname} ${event.user.lastname}"/></p>
                  <p class="user-email">
                    <svg xmlns="http://www.w3.org/2000/svg" class="contact-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
                    </svg>
                    <c:out value="${event.user.email}"/>
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

</body>
</html>