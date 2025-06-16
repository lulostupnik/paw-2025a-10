<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="stylesheet" href="<c:url value='/resources/css/ratings.css'/>" />

<div class="profile-section active" id="info-section">
  <div class="profile-card">
    <h2 class="section-title"><spring:message code="profile.personal.info"/></h2>

    <div class="info-list">
      <c:if test="${isMine}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.email"/></h3>
          <p class="info-value"><c:out value="${profileUser.email}"/></p>
        </div>
      </c:if>
      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.firstname"/></h3>
        <p class="info-value"><c:out value="${profileUser.firstname}"/></p>
      </div>

      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.lastname"/></h3>
        <p class="info-value"><c:out value="${profileUser.lastname}"/></p>
      </div>

      <c:if test="${not empty profileUser.university}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.home.university"/></h3>
          <p class="info-value"><c:out value="${profileUser.university.name}"/></p>
        </div>
      </c:if>

      <c:if test="${not empty profileUser.career}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.career"/></h3>
          <p class="info-value"><c:out value="${profileUser.career.name}"/></p>
        </div>
      </c:if>

    </div>
  </div>

  <!-- Rating Statistics Section -->
  <div class="profile-card">
    <h2 class="section-title">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
      </svg>
      <spring:message code="profile.rating.statistics"/>
    </h2>

    <div class="rating-statistics-container">
      <!-- Events Created Rating -->
      <div class="rating-stat-item">
        <div class="rating-stat-header">
          <h3 class="rating-stat-label">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
              <line x1="16" y1="2" x2="16" y2="6"></line>
              <line x1="8" y1="2" x2="8" y2="6"></line>
              <line x1="3" y1="10" x2="21" y2="10"></line>
            </svg>
            <spring:message code="profile.rating.events.created"/>
          </h3>
        </div>

        <c:choose>
          <c:when test="${not empty averageCreatedEventsRating && averageCreatedEventsRating > 0}">
            <div class="rating-display">
              <div class="rating-stars">
                <c:forEach var="i" begin="1" end="5">
                  <c:choose>
                    <c:when test="${averageCreatedEventsRating >= i}">
                      <svg class="star filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                      </svg>
                    </c:when>
                    <c:when test="${averageCreatedEventsRating >= (i - 0.5)}">
                      <svg class="star half-filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <defs>
                          <linearGradient id="created-half-${i}">
                            <stop offset="50%" stop-color="currentColor" stop-opacity="1"/>
                            <stop offset="50%" stop-color="transparent" stop-opacity="0"/>
                          </linearGradient>
                        </defs>
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill="url(#created-half-${i})"/>
                      </svg>
                    </c:when>
                    <c:otherwise>
                      <svg class="star empty" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9 27 8.91 8.26 12 2"></polygon>
                      </svg>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </div>
              <div class="rating-details">
                <span class="rating-number">
                  <fmt:formatNumber value="${averageCreatedEventsRating }" maxFractionDigits="1" minFractionDigits="1"/>
                </span>

              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="rating-empty-state">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
              <span class="empty-text">
                <spring:message code="profile.rating.no.created.events"/>
              </span>
            </div>
          </c:otherwise>
        </c:choose>
      </div>

      <!-- Events Attended Rating -->
      <div class="rating-stat-item">
        <div class="rating-stat-header">
          <h3 class="rating-stat-label">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            <spring:message code="profile.rating.events.attended"/>
          </h3>
        </div>

        <c:choose>
          <c:when test="${not empty averageAttendedEventsRating &&  averageAttendedEventsRating > 0}">
            <div class="rating-display">
              <div class="rating-stars">
                <c:forEach var="i" begin="1" end="5">
                  <c:choose>
                    <c:when test="${averageAttendedEventsRating >= i}">
                      <svg class="star filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                      </svg>
                    </c:when>
                    <c:when test="${averageAttendedEventsRating >= (i - 0.5)}">
                      <svg class="star half-filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <defs>
                          <linearGradient id="attended-half-${i}">
                            <stop offset="50%" stop-color="currentColor" stop-opacity="1"/>
                            <stop offset="50%" stop-color="transparent" stop-opacity="0"/>
                          </linearGradient>
                        </defs>
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill="url(#attended-half-${i})"/>
                      </svg>
                    </c:when>
                    <c:otherwise>
                      <svg class="star empty" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                      </svg>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </div>
              <div class="rating-details">
                <span class="rating-number">
                  <fmt:formatNumber value="${averageAttendedEventsRating}" maxFractionDigits="1" minFractionDigits="1"/>
                </span>

              </div>
            </div>
          </c:when>
          <c:otherwise>
            <div class="rating-empty-state">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
              </svg>
              <span class="empty-text">
                <spring:message code="profile.rating.no.attended.events"/>
              </span>
            </div>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</div>