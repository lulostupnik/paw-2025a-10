<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%--Falta el backend igual--%>
<div class="events-section">
  <!-- Events Sub-tabs -->
  <div class="events-filter-tabs">
    <button class="events-subtab active" data-events-tab="created" onclick="switchEventsSubTab(event, 'created')">
      <spring:message code="journey.events.created" text="Created"/>
    </button>
    <button class="events-subtab" data-events-tab="attending" onclick="switchEventsSubTab(event, 'attending')">
      <spring:message code="journey.events.attending" text="Attending"/>
    </button>
  </div>

  <!-- Created Events Tab Content -->
  <div class="events-subtab-content active" id="created-events">
    <c:choose>
      <c:when test="${empty createdEventsPage.content}">
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
            <spring:message code="journey.events.no.created" text="No events created during this journey" />
          </p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="journey-events-container">
          <c:forEach var="event" items="${createdEventsPage.content}">
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

        <c:if test="${createdEventsPage.totalPages > 1}">
          <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
            <jsp:param name="pageObjectTotalPages" value="${createdEventsPage.totalPages}" />
            <jsp:param name="currentPage" value="${createdEventsPage.currentPage}" />
            <jsp:param name="pageSize" value="${createdEventsPageSize}" />
            <jsp:param name="baseUrl" value="/journeys/${journey.id}?tab=events&eventsTab=created" />
            <jsp:param name="paramName" value="createdEventsPage" />
            <jsp:param name="sizeParamName" value="createdEventsSize" />
          </jsp:include>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>

  <!-- Attending Events Tab Content -->
  <div class="events-subtab-content" id="attending-events" style="display: none;">
    <c:choose>
      <c:when test="${empty attendingEventsPage.content}">
        <div class="empty-state">
          <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
              <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
          </div>
          <p class="empty-message">
            <spring:message code="journey.events.no.attending" text="No events attended during this journey" />
          </p>
        </div>
      </c:when>
      <c:otherwise>
        <div class="journey-events-container">
          <c:forEach var="event" items="${attendingEventsPage.content}">
            <a href="<c:url value='/events/${event.id}'/>" class="journey-event-card-link">
              <div class="journey-event-card">
                <div class="journey-event-left">
                  <c:if test="${not empty event.flyerImageId}">
                    <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event flyer" class="journey-event-image">
                  </c:if>
                  <c:if test="${empty event.flyerImageId}">
                    <div class="journey-event-image-placeholder">
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                        <circle cx="12" cy="10" r="3"></circle>
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

        <c:if test="${attendingEventsPage.totalPages > 1}">
          <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
            <jsp:param name="pageObjectTotalPages" value="${attendingEventsPage.totalPages}" />
            <jsp:param name="currentPage" value="${attendingEventsPage.currentPage}" />
            <jsp:param name="pageSize" value="${attendingEventsPageSize}" />
            <jsp:param name="baseUrl" value="/journeys/${journey.id}?tab=events&eventsTab=attending" />
            <jsp:param name="paramName" value="attendingEventsPage" />
            <jsp:param name="sizeParamName" value="attendingEventsSize" />
          </jsp:include>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<style>
  /* Events Sub-tab Styles - Scoped to avoid conflicts */
  .events-filter-tabs {
    display: flex;
    border-bottom: 2px solid #e9ecef;
    margin-bottom: 24px;
    gap: 0;
  }

  .events-subtab {
    background: none;
    border: none;
    padding: 12px 24px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    color: #6c757d;
    border-bottom: 3px solid transparent;
    transition: all 0.2s ease;
    position: relative;
    flex: 1;
    text-align: center;
  }

  .events-subtab:hover {
    color: #495057;
    background-color: #f8f9fa;
  }

  .events-subtab.active {
    color: #007bff;
    border-bottom-color: #007bff;
    background-color: #fff;
  }

  .events-subtab-content {
    display: none;
    animation: fadeIn 0.3s ease-in-out;
  }

  .events-subtab-content.active {
    display: block;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  /* Empty State Styles */
  .empty-state {
    text-align: center;
    padding: 48px 24px;
    color: #6c757d;
  }

  .empty-icon {
    margin-bottom: 16px;
  }

  .empty-icon-img {
    color: #dee2e6;
  }

  .empty-message {
    font-size: 16px;
    margin: 0;
  }

  /* Responsive Design */
  @media (max-width: 768px) {
    .events-filter-tabs {
      flex-direction: column;
    }

    .events-subtab {
      text-align: left;
      border-bottom: 1px solid #e9ecef;
      border-right: 3px solid transparent;
    }

    .events-subtab.active {
      border-bottom-color: #e9ecef;
      border-right-color: #007bff;
    }
  }
</style>

<script>
  // Events sub-tab functionality - scoped to avoid conflicts with main tabs
  function switchEventsSubTab(event, tabName) {
    // Prevent event bubbling to avoid interfering with main tabs
    event.stopPropagation();

    // Remove active class from all events sub-tabs
    const tabs = document.querySelectorAll('.events-subtab');
    tabs.forEach(tab => tab.classList.remove('active'));

    // Hide all events sub-tab content
    const contents = document.querySelectorAll('.events-subtab-content');
    contents.forEach(content => {
      content.classList.remove('active');
      content.style.display = 'none';
    });

    // Add active class to clicked tab
    const activeTab = document.querySelector(`[data-events-tab="${tabName}"]`);
    if (activeTab) {
      activeTab.classList.add('active');
    }

    // Show corresponding content
    const activeContent = document.getElementById(`${tabName}-events`);
    if (activeContent) {
      activeContent.classList.add('active');
      activeContent.style.display = 'block';
    }

    // Store the active events sub-tab
    sessionStorage.setItem('activeEventsSubTab', tabName);
  }

  // Initialize events sub-tab on page load
  document.addEventListener('DOMContentLoaded', function() {
    // Only initialize if we're on the events tab
    const eventsTabContent = document.getElementById('events-content');
    if (eventsTabContent && eventsTabContent.style.display !== 'none') {
      const savedEventsTab = sessionStorage.getItem('activeEventsSubTab') || 'created';

      // Find the button and trigger click without event propagation
      const targetButton = document.querySelector(`[data-events-tab="${savedEventsTab}"]`);
      if (targetButton) {
        // Create a fake event object
        const fakeEvent = { stopPropagation: function() {} };
        switchEventsSubTab(fakeEvent, savedEventsTab);
      }
    }
  });
</script>
