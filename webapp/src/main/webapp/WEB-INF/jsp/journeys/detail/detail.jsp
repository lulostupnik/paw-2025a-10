<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="journey.detail.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/event-detail.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
  <script src="<c:url value='/resources/js/confirm-delete.js'/>"></script>
</head>

<body>
<c:set var="interestPageSize" value="8" scope="request" />
<c:set var="chatPageSize" value="4" scope="request" />
<c:set var="eventsPageSize" value="6" scope="request" />
<c:set var="tipsPageSize" value="10" scope="request" />

<div style="display: none;">
  <span id="i18n-journey.confirmDelete" data-message="<spring:message code='journey.confirmDelete' />"></span>
  <span id="i18n-journey.deleteWarning" data-message="<spring:message code='journey.deleteWarning' />"></span>
  <span id="i18n-journeyResponse.confirmDelete" data-message="<spring:message code='journeyResponse.confirmDelete' />"></span>
  <span id="i18n-journeyResponse.deleteWarning" data-message="<spring:message code='journeyResponse.deleteWarning' />"></span>
</div>

<div class="layout-container">
  <div class="main-content">
    <jsp:include page="../../components/navbar.jsp" />
    <div class="content-container">

      <div class="back-button-container">
        <c:if test="${isOwner}">
          <button onclick="goBack()" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
              <path d="M19 12H5"></path>
              <path d="M12 19l-7-7 7-7"></path>
            </svg>
            <span><spring:message code="journey.detail.back.to.profile" /></span>
          </button>
        </c:if>
        <c:if test="${not isOwner}">
          <button onclick="goBack()" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
              <path d="M19 12H5"></path>
              <path d="M12 19l-7-7 7-7"></path>
            </svg>
            <span><spring:message code="journey.detail.back.to.list" /></span>
          </button>
        </c:if>
      </div>

      <div class="event-detail-container">
        <div class="event-top-section">
          <div class="event-header">
            <c:set var="escapedFirstname"><c:out value="${journey.user.firstname}"/></c:set>
            <c:set var="escapedLastname"><c:out value="${journey.user.lastname}"/></c:set>
            <h1 class="event-title">
              <spring:message arguments="${escapedFirstname},${escapedLastname}" code="journey.detail.section.title" />
            </h1>

            <div class="action-controls">
              <div style="position: relative; display: inline-block;">
                <button onclick="toggleJourneyActionMenu()" class="btn-action btn-menu" id="journeyActionMenuButton" style="background: none; border: 1px solid #e0e0e0; border-radius: 6px; padding: 8px; cursor: pointer;">
                  <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="btn-icon">
                    <circle cx="12" cy="12" r="1"></circle>
                    <circle cx="12" cy="5" r="1"></circle>
                    <circle cx="12" cy="19" r="1"></circle>
                  </svg>
                </button>

                <div id="journeyActionDropdown" style="display: none; position: absolute; right: 0; top: 100%; background-color: white; min-width: 180px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); border-radius: 8px; z-index: 1000; border: 1px solid #e0e0e0; padding: 8px 0;">
                  <c:if test="${isOwner}">
                    <a href="<c:url value='/journeys/${journey.id}/update'/>" style="color: #333; padding: 12px 16px; text-decoration: none; display: flex; align-items: center; gap: 12px;" onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                      </svg>
                      <span><spring:message code="journey.edit" text="Edit Journey" /></span>
                    </a>
                  </c:if>

                  <c:if test="${isOwner || pageContext.request.isUserInRole('ADMIN')}">
                    <c:url var="deleteUrl" value='/journeys/${journey.id}/delete'/>
                    <a href="<c:out value='${deleteUrl}'/>" style="color: #333; padding: 12px 16px; text-decoration: none; display: flex; align-items: center; gap: 12px;" onmouseover="this.style.backgroundColor='#fef2f2'; this.style.color='#dc2626'" onmouseout="this.style.backgroundColor='transparent'; this.style.color='#333'">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M3 6h18"></path>
                        <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                        <line x1="10" y1="11" x2="10" y2="17"></line>
                        <line x1="14" y1="11" x2="14" y2="17"></line>
                      </svg>
                      <span><spring:message code="journey.delete" text="Delete Journey" /></span>
                    </a>
                  </c:if>

                  <c:if test="${!isOwner}">
                    <c:url var="reportJourneyUrl" value='/reports/journeys/${journey.id}/create'/>
                    <a href="${reportJourneyUrl}"
                       style="color: #333; padding: 12px 16px; text-decoration: none; display: flex; align-items: center; gap: 12px;"
                       onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M12 9v4"></path>
                        <path d="M12 17h.01"></path>
                        <circle cx="12" cy="12" r="10"></circle>
                      </svg>
                      <span><spring:message code="journey.report" text="Report Journey" /></span>
                    </a>
                  </c:if>
                </div>
              </div>
            </div>
          </div>

          <div class="event-meta">
            <div class="meta-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                <circle cx="12" cy="10" r="3"></circle>
              </svg>
              <span class="destination-text">
                                <c:out value="${journey.destinationUniversity.city}" /> -
                                <c:out value="${journey.destinationUniversity.name}" />
                            </span>
            </div>
            <div class="meta-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <fmt:parseDate value="${journey.startDate}" pattern="yyyy-MM-dd" var="parsedStartDate" />
              <fmt:parseDate value="${journey.endDate}" pattern="yyyy-MM-dd" var="parsedEndDate" />
              <fmt:formatDate value="${parsedStartDate}" pattern="MMMM d, yyyy" var="formattedStartDate" />
              <fmt:formatDate value="${parsedEndDate}" pattern="MMMM d, yyyy" var="formattedEndDate" />
              <span class="date-range">
                                <c:out value="${formattedStartDate}" /> → <c:out value="${formattedEndDate}" />
                            </span>
            </div>
          </div>

          <c:set var="creatorUser" value="${journey.user}" scope="request" />
          <c:set var="creatorShowName" value="false" scope="request" />
          <c:set var="isJourneyCreator" value="true" scope="request" />

          <jsp:include page="/WEB-INF/jsp/components/creator.jsp" />

          <div class="event-description">
            <h3 class="description-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
              </svg>
              <spring:message code="journey.description" />
            </h3>
            <div class="description-content">
              <p><c:out value="${journey.description}" /></p>
            </div>
          </div>
        </div>

        <div class="tabs-container">
          <div class="tabs-header">
            <button id="interests-tab" class="tab-btn" data-tab="interests">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
              </svg>
              <span><spring:message code="journey.detail.interests" text="Interests" /></span>
            </button>
            <button id="events-tab" class="tab-btn" data-tab="events">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <span><spring:message code="journey.detail.events" text="Events" /></span>
            </button>
            <button id="tips-tab" class="tab-btn" data-tab="tips">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
                <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
              </svg>
              <span><spring:message code="journey.detail.tips" text="Tips" /></span>
              <span class="count">(<c:out value="${tipsCount}" />)</span>
            </button>
            <button id="comments-tab" class="tab-btn" data-tab="comments">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
              </svg>
              <span><spring:message code="journey.detail.responses" /></span>
              <span class="count">(<c:out value="${commentsCount}" />)</span>
            </button>
          </div>

          <div class="tabs-content">
            <!-- Interests Tab Content -->
            <div id="interests-content" class="tab-content">
              <jsp:include page="interests-section.jsp" />
            </div>

            <!-- Events Tab Content -->
            <div id="events-content" class="tab-content">
              <jsp:include page="events-section.jsp" />
            </div>

            <!-- Tips Tab Content -->
            <div id="tips-content" class="tab-content">
              <jsp:include page="tips-section.jsp" />
            </div>

            <!-- Comments Tab Content -->
            <div id="comments-content" class="tab-content">
              <jsp:include page="comments-section.jsp" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>

<script>
  function goBack(){
    const rutaAnterior = popFromNavigationStack()
    if (rutaAnterior) {
      window.location.href = rutaAnterior;
    } else {
      window.location.href = "<c:url value='/events'/>"
    }
  }

  function toggleJourneyActionMenu() {
    const dropdown = document.getElementById('journeyActionDropdown');
    if (dropdown.style.display === 'none' || dropdown.style.display === '') {
      dropdown.style.display = 'block';
    } else {
      dropdown.style.display = 'none';
    }
  }

  // Close dropdowns when clicking outside
  document.addEventListener('click', function(event) {
    // Close journey action dropdown
    const journeyDropdown = document.getElementById('journeyActionDropdown');
    const journeyButton = document.getElementById('journeyActionMenuButton');

    if (journeyDropdown && journeyButton && !journeyButton.contains(event.target) && !journeyDropdown.contains(event.target)) {
      journeyDropdown.style.display = 'none';
    }
  });

  // Main tab functionality
  document.addEventListener('DOMContentLoaded', function() {
    // Check if there's an active tab stored in session storage
    const activeTab = sessionStorage.getItem('activeJourneyTab') || 'interests';

    // Only activate if the tab exists
    const activeTabBtn = document.getElementById(activeTab + '-tab');
    const activeTabContent = document.getElementById(activeTab + '-content');

    if (activeTabBtn && activeTabContent) {
      activeTabBtn.classList.add('active');
      activeTabContent.style.display = 'block';
    } else {
      // Fallback if the saved tab doesn't exist
      const defaultBtn = document.getElementById('interests-tab');
      const defaultContent = document.getElementById('interests-content');

      if (defaultBtn && defaultContent) {
        defaultBtn.classList.add('active');
        defaultContent.style.display = 'block';
        sessionStorage.setItem('activeJourneyTab', 'interests');
      }
    }

    // Add event listeners to tab buttons
    document.querySelectorAll('.tab-btn').forEach(function(btn) {
      btn.addEventListener('click', function() {
        // Get the tab id
        const tabId = this.getAttribute('data-tab');

        // Store the active tab in session storage
        sessionStorage.setItem('activeJourneyTab', tabId);

        // Remove active class from all tabs
        document.querySelectorAll('.tab-btn').forEach(function(tab) {
          tab.classList.remove('active');
        });

        // Hide all tab contents
        document.querySelectorAll('.tab-content').forEach(function(content) {
          content.style.display = 'none';
        });

        // Add active class to clicked tab
        this.classList.add('active');

        // Show corresponding tab content
        document.getElementById(tabId + '-content').style.display = 'block';
      });
    });
  });
</script>

</body>
</html>
