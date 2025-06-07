<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty eventsPage.content}">
  <div class="empty-state">
    <div class="empty-icon">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
        <line x1="16" y1="2" x2="16" y2="6"></line>
        <line x1="8" y1="2" x2="8" y2="6"></line>
        <line x1="3" y1="10" x2="21" y2="10"></line>
      </svg>
    </div>
    <p class="empty-message">
      <spring:message code="journey.detail.no.events" text="No events during this journey" />
    </p>
  </div>
</c:if>

<c:if test="${not empty eventsPage.content}">
  <div class="journey-events-container">
    <c:forEach var="event" items="${eventsPage.content}">
      <a href="<c:url value='/events/${event.id}'/>" class="journey-event-card-link">
        <div class="journey-event-card">
          <div class="journey-event-left">
            <c:if test="${not empty event.flyerImageId}">
              <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event flyer" class="journey-event-image">
            </c:if>
            <c:if test="${empty event.flyerImageId}">
              <div class="journey-event-image-placeholder">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
              </div>
            </c:if>
          </div>
          <div class="journey-event-content">
            <h3 class="journey-event-title"><c:out value="${event.title}" /></h3>
            <div class="journey-event-meta">
              <div class="journey-event-meta-item">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
                <span><c:out value="${event.city.name}" /></span>
              </div>
              <div class="journey-event-meta-item">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                <fmt:parseDate value="${event.date}" pattern="yyyy-MM-dd" var="parsedEventDate" />
                <fmt:formatDate value="${parsedEventDate}" pattern="MMM d, yyyy" var="formattedEventDate" />
                <span><c:out value="${formattedEventDate}" /></span>
              </div>
              <c:if test="${not empty event.time}">
                <div class="journey-event-meta-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                  <span><c:out value="${event.time}" /></span>
                </div>
              </c:if>
            </div>
            <p class="journey-event-description"><c:out value="${event.description}" /></p>
          </div>
          <div class="journey-event-arrow">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polyline points="9 18 15 12 9 6"></polyline>
            </svg>
          </div>
        </div>
      </a>
    </c:forEach>
  </div>
  <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
    <jsp:param name="pageObjectTotalPages" value="${eventsPage.totalPages}" />
    <jsp:param name="currentPage" value="${eventsPage.currentPage}" />
    <jsp:param name="pageSize" value="${eventsPageSize}" />
    <jsp:param name="baseUrl" value="/journeys/${journey.id}?page=${journeyResponsesPage.currentPage}&size=${chatPageSize}&interestsPage=${interestPage.currentPage}&interestsSize=${interestPageSize}" />
    <jsp:param name="paramName" value="eventsPage" />
    <jsp:param name="sizeParamName" value="eventsSize" />
  </jsp:include>
</c:if>
