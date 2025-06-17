<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="events-section">
  <!-- Events Sub-subtabs -->
  <div class="events-filter-subtabs">
    <button class="events-subtab active" data-events-subtab="created">
      <spring:message code="journey.events.created" text="Created"/>
    </button>
    <button class="events-subtab" data-events-subtab="attending">
      <spring:message code="journey.events.attending" text="Attending"/>
    </button>
  </div>

  <!-- Created Events subtab Content -->
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
                  <c:choose>
                    <c:when test="${not empty event.flyerImageId}">
                      <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event flyer" class="journey-event-image">
                    </c:when>
                    <c:otherwise>
                      <div class="journey-event-image-placeholder">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                          <line x1="16" y1="2" x2="16" y2="6"></line>
                          <line x1="8" y1="2" x2="8" y2="6"></line>
                          <line x1="3" y1="10" x2="21" y2="10"></line>
                        </svg>
                      </div>
                    </c:otherwise>
                  </c:choose>
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
            <jsp:param name="baseUrl" value="/journeys/${journey.id}?subtab=events&eventssubtab=created" />
            <jsp:param name="paramName" value="createdEventsPage" />
            <jsp:param name="sizeParamName" value="createdEventsSize" />
          </jsp:include>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>

  <!-- Attending Events subtab Content -->
  <div class="events-subtab-content" id="attending-events">
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
                  <c:choose>
                    <c:when test="${not empty event.flyerImageId}">
                      <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event flyer" class="journey-event-image">
                    </c:when>
                    <c:otherwise>
                      <div class="journey-event-image-placeholder">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
                          <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                          <circle cx="12" cy="10" r="3"></circle>
                        </svg>
                      </div>
                    </c:otherwise>
                  </c:choose>
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
            <jsp:param name="baseUrl" value="/journeys/${journey.id}?subtab=events&eventssubtab=attending" />
            <jsp:param name="paramName" value="attendingEventsPage" />
            <jsp:param name="sizeParamName" value="attendingEventsSize" />
          </jsp:include>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<style>
  .events-filter-subtabs {
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
    opacity: 0;
    transition: opacity 0.3s ease-in-out;
  }

  .events-subtab-content.active {
    display: block;
    opacity: 1;
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

  .journey-events-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .journey-event-card-link {
    text-decoration: none;
    color: inherit;
  }

  .journey-event-card {
    display: flex;
    align-items: center;
    padding: 16px;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    transition: all 0.2s ease;
    background: #fff;
  }

  .journey-event-card:hover {
    border-color: #007bff;
    box-shadow: 0 2px 8px rgba(0, 123, 255, 0.1);
    transform: translateY(-1px);
  }

  .journey-event-left {
    flex-shrink: 0;
    margin-right: 16px;
  }

  .journey-event-image {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 6px;
  }

  .journey-event-image-placeholder {
    width: 80px;
    height: 80px;
    background-color: #f8f9fa;
    border: 1px solid #e9ecef;
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #6c757d;
  }

  .journey-event-content {
    flex: 1;
    min-width: 0;
  }

  .journey-event-title {
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 8px 0;
    color: #212529;
  }

  .journey-event-meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 8px;
  }

  .journey-event-meta-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: #6c757d;
  }

  .journey-event-meta-item svg {
    flex-shrink: 0;
  }

  .journey-event-description {
    font-size: 14px;
    color: #6c757d;
    margin: 0;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .journey-event-arrow {
    flex-shrink: 0;
    margin-left: 16px;
    color: #6c757d;
  }

  @media (max-width: 768px) {
    .events-filter-subtabs {
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

    .journey-event-card {
      flex-direction: column;
      text-align: center;
    }

    .journey-event-left {
      margin-right: 0;
      margin-bottom: 12px;
    }

    .journey-event-meta {
      justify-content: center;
    }
  }
</style>

<script>
  (function() {
    'use strict';

    // Wait for DOM to be fully loaded
    function initializesubtabs() {
      console.log('Initializing events subtabs...'); // Debug log

      // Function to switch subtabs
      function switchsubtab(subtabName) {
        console.log('Switching to subtab:', subtabName); // Debug log

        // Remove active class from all subtab buttons
        const allsubtabs = document.querySelectorAll('.events-subtab');
        allsubtabs.forEach(function(subtab) {
          subtab.classList.remove('active');
        });

        // Hide all subtab content
        const allContent = document.querySelectorAll('.events-subtab-content');
        allContent.forEach(function(content) {
          content.classList.remove('active');
        });

        // Activate the selected subtab button
        const activesubtab = document.querySelector('[data-events-subtab="' + subtabName + '"]');
        if (activesubtab) {
          activesubtab.classList.add('active');
        }

        // Show the selected subtab content
        const activeContent = document.getElementById(subtabName + '-events');
        if (activeContent) {
          activeContent.classList.add('active');
        }
      }

      // Add click event listeners to all subtab buttons
      const subtabButtons = document.querySelectorAll('.events-subtab');
      subtabButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
          e.preventDefault();
          e.stopPropagation();

          const subtabName = this.getAttribute('data-events-subtab');
          if (subtabName) {
            switchsubtab(subtabName);
          }
        });
      });

      // Set initial active subtab to "created"

        // Leer el parámetro 'eventssubtab' de la URL
        const params = new URLSearchParams(window.location.search);
        const initialSubtab = params.get('eventssubtab') || 'created';
        switchsubtab(initialSubtab);


        console.log('Events subtabs initialized successfully'); // Debug log
    }

    // Initialize when DOM is ready
    if (document.readyState === 'loading') {
      document.addEventListener('DOMContentLoaded', initializesubtabs);
    } else {
      initializesubtabs();
    }
  })();
</script>
