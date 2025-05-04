<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${pageContext.request.contextPath}"/><spring:message code="profile.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/pages/profile.css"/>" />

  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>

<c:set var="pageSize" value="4" scope="request" />

<div class="layout-container">
  <!-- Main Content -->
  <div class="main-content">
    <jsp:include page="./components/navbar.jsp" />

    <c:if test="${not empty user}">
      <c:set var="userObj" value="${user}" />

      <!-- Profile Header Section -->
      <div class="profile-header-wrapper">
        <div class="profile-header-bg"></div>
        <div class="content-container">
          <div class="profile-header">
            <div class="profile-avatar-container">
              <div class="profiles-avatar">
                <c:choose>
                  <c:when test="${not empty userObj.profilePictureId && userObj.profilePictureId > 0}">
                    <img src="<c:url value='/images/${userObj.profilePictureId}'/>" alt="${userObj.username}" class="avatar-image" />
                  </c:when>
                  <c:otherwise>
                    <div class="avatar-placeholder">
                        ${fn:substring(userObj.firstname, 0, 1).toUpperCase()}${fn:substring(userObj.lastname, 0, 1).toUpperCase()}
                    </div>
                  </c:otherwise>
                </c:choose>
              </div>
            </div>
            <div class="profile-info">
              <h1 class="profile-name"><c:out value="${userObj.firstname} ${userObj.lastname}"/></h1>
              <p class="profile-username">@<c:out value="${userObj.username}"/></p>
            </div>
              <%--            <div class="profile-actions">--%>
              <%--              <a href="<c:url value='/profile/edit'/>" class="btn-edit-profile">--%>
              <%--                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">--%>
              <%--                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>--%>
              <%--                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>--%>
              <%--                </svg>--%>
              <%--                <spring:message code="profile.edit"/>--%>
              <%--              </a>--%>
              <%--            </div>--%>
          </div>
        </div>
      </div>

      <!-- Main Content Section -->
      <div class="content-container">
        <!-- Profile Navigation Tabs -->
        <div class="profile-tabs-container">
          <div class="profile-tabs">
            <button class="profile-tab active" data-tab="info">
              <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
              </svg>
              <span class="tab-text"><spring:message code="profile.tab.info"/></span>
            </button>
            <button class="profile-tab" data-tab="journeys">
              <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
              </svg>
              <span class="tab-text"><spring:message code="profile.tab.journeys"/></span>
            </button>
            <button class="profile-tab" data-tab="events">
              <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                <line x1="16" y1="2" x2="16" y2="6"></line>
                <line x1="8" y1="2" x2="8" y2="6"></line>
                <line x1="3" y1="10" x2="21" y2="10"></line>
              </svg>
              <span class="tab-text"><spring:message code="profile.tab.events"/></span>
            </button>
          </div>
        </div>

        <!-- Profile Content Sections -->
        <div class="profile-content">
          <!-- Personal Information Tab -->
          <div class="profile-section active" id="info-section">
            <div class="profile-card">
              <h2 class="section-title"><spring:message code="profile.personal.info"/></h2>

              <div class="info-list">
                <div class="info-item">
                  <h3 class="info-label"><spring:message code="profile.email"/></h3>
                  <p class="info-value"><c:out value="${userObj.email}"/></p>
                </div>

                <div class="info-item">
                  <h3 class="info-label"><spring:message code="profile.firstname"/></h3>
                  <p class="info-value"><c:out value="${userObj.firstname}"/></p>
                </div>

                <div class="info-item">
                  <h3 class="info-label"><spring:message code="profile.lastname"/></h3>
                  <p class="info-value"><c:out value="${userObj.lastname}"/></p>
                </div>

                <c:if test="${not empty userObj.university}">
                  <div class="info-item">
                    <h3 class="info-label"><spring:message code="profile.home.university"/></h3>
                    <p class="info-value"><c:out value="${userObj.university.name}"/></p>
                  </div>
                </c:if>

                <c:if test="${not empty userObj.career}">
                  <div class="info-item">
                    <h3 class="info-label"><spring:message code="profile.career"/></h3>
                    <p class="info-value"><c:out value="${userObj.career.name}"/></p>
                  </div>
                </c:if>

                <c:if test="${not empty userObj.locale}">
                  <div class="info-item">
                    <h3 class="info-label"><spring:message code="profile.language"/></h3>
                    <p class="info-value"><c:out value="${userObj.locale.displayLanguage}"/></p>
                  </div>
                </c:if>
              </div>
            </div>
          </div>

          <!-- User Journeys Tab -->
          <div class="profile-section" id="journeys-section">
            <div class="section-actions">
              <c:if test="${empty userJourneys}">
                <a href="<c:url value='/journeys/create'/>" class="btn-primary">
                  <svg xmlns="http://www.w3.org/2000/svg" class="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="12" y1="5" x2="12" y2="19"></line>
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                  </svg>
                  <spring:message code="journey.create.button"/>
                </a>
              </c:if>
            </div>

            <div class="cards-grid">
              <c:if test="${empty userJourneys}">
                <div class="empty-state">
                  <div class="empty-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                    </svg>
                  </div>
                  <p class="empty-message">
                    <spring:message code="profile.no.journeys"/>
                  </p>
                  <c:if test="${empty userJourneys}">
                    <a href="<c:url value='/journeys/create'/>" class="empty-action-btn">
                      <spring:message code="journey.create.button" />
                    </a>
                  </c:if>
                </div>
              </c:if>

              <c:if test="${not empty userJourneys}">
                <c:forEach var="journey" items="${userJourneys}" varStatus="status">
                  <jsp:include page="./journeys/journey-card.jsp">
                    <jsp:param name="journeyId" value="${journey.id}" />
                    <jsp:param name="city" value="${journey.destinationUniversity.city.name}" />
                    <jsp:param name="startDate" value="${journey.startDate}" />
                    <jsp:param name="endDate" value="${journey.endDate}" />
                    <jsp:param name="description" value="${journey.description}" />
                    <jsp:param name="profilePictureId" value="${journey.user.profilePictureId}" />
                    <jsp:param name="userName" value="${journey.user.username}" />
                    <jsp:param name="firstname" value="${journey.user.firstname}" />
                    <jsp:param name="lastname" value="${journey.user.lastname}"/>
                    <jsp:param name="country" value="${journey.destinationUniversity.city.country}"/>
                    <jsp:param name="university" value="${journey.destinationUniversity.name}"/>
                    <jsp:param name="isOwner" value="true"/>
                  </jsp:include>
                </c:forEach>
              </c:if>
            </div>
          </div>

          <!-- User Events Tab -->
          <div class="profile-section" id="events-section">
            <div class="section-actions">
              <a href="<c:url value='/events/create'/>" class="btn-primary">
                <svg xmlns="http://www.w3.org/2000/svg" class="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="12" y1="5" x2="12" y2="19"></line>
                  <line x1="5" y1="12" x2="19" y2="12"></line>
                </svg>
                <spring:message code="event.create.button"/>
              </a>
            </div>

            <div class="events-filter-tabs">
              <button class="events-tab active" data-events-tab="created">
                <spring:message code="profile.events.created"/>
              </button>
              <button class="events-tab" data-events-tab="attending">
                <spring:message code="profile.events.attending"/>
              </button>
            </div>

            <!-- Created Events -->
            <div class="events-tab-content active" id="created-events">
              <div class="cards-grid">
                <c:if test="${empty userEvents.content}">
                  <div class="empty-state">
                    <div class="empty-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                    <p class="empty-message">
                      <spring:message code="profile.no.created.events"/>
                    </p>
                    <a href="<c:url value='/events/create'/>" class="empty-action-btn">
                      <spring:message code="event.create.button"/>
                    </a>
                  </div>
                </c:if>

                <c:if test="${not empty userEvents.content}">
                  <c:forEach items="${userEvents.content}" var="event">
                    <jsp:include page="./events/event-card.jsp">
                      <jsp:param name="username" value="${event.user.username}"/>
                      <jsp:param name="eventId" value="${event.id}" />
                      <jsp:param name="city" value="${event.eventCity.name}" />
                      <jsp:param name="date" value="${event.date}" />
                      <jsp:param name="description" value="${event.description}" />
                      <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
                      <jsp:param name="attend" value="false" />
                      <jsp:param name="firstname" value="${event.user.firstname}" />
                      <jsp:param name="lastname" value="${event.user.lastname}"/>
                      <jsp:param name="title" value="${event.title}"/>
                      <jsp:param name="isFull" value="${event.attendeesLimit.isPresent() && event.attendeesLimit.get() <= event.attendeesCount}"/>
                      <jsp:param name="isOwner" value="true" />
                    </jsp:include>
                  </c:forEach>
                </c:if>
              </div>

              <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                <jsp:param name="pageObjectTotalPages" value="${userEvents.totalPages}" />
                <jsp:param name="currentPage" value="${currentPageUserEvents}" />
                <jsp:param name="pageSize" value="${pageSize}" />
                <jsp:param name="baseUrl" value="/profile?attendingPage=${currentPageUserAttending}&size=${pageSize}&activeTab=eventsCreated" />
              </jsp:include>
            </div>

            <!-- Attending Events -->
            <div class="events-tab-content" id="attending-events">
              <div class="cards-grid">
                <c:if test="${empty userAttendingEvents.content}">
                  <div class="empty-state">
                    <div class="empty-icon">
                      <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                      </svg>
                    </div>
                    <p class="empty-message">
                      <spring:message code="profile.no.attending.events"/>
                    </p>
                    <a href="<c:url value='/events'/>" class="empty-action-btn">
                      <spring:message code="profile.explore.events"/>
                    </a>
                  </div>
                </c:if>

                <c:if test="${not empty userAttendingEvents.content}">
                  <c:forEach items="${userAttendingEvents.content}" var="event">
                    <jsp:include page="./events/event-card.jsp">
                      <jsp:param name="username" value="${event.user.username}"/>
                      <jsp:param name="eventId" value="${event.id}" />
                      <jsp:param name="city" value="${event.eventCity.name}" />
                      <jsp:param name="date" value="${event.date}" />
                      <jsp:param name="description" value="${event.description}" />
                      <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
                      <jsp:param name="attend" value="true" />
                      <jsp:param name="firstname" value="${event.user.firstname}" />
                      <jsp:param name="lastname" value="${event.user.lastname}"/>
                      <jsp:param name="title" value="${event.title}"/>
                      <jsp:param name="isOwner" value="false" />
                      <jsp:param name="isFull" value="${event.attendeesLimit.isPresent() && event.attendeesLimit.get() <= event.attendeesCount}"/>
                    </jsp:include>
                  </c:forEach>
                </c:if>
              </div>
              <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                <jsp:param name="pageObjectTotalPages" value="${userAttendingEvents.totalPages}" />
                <jsp:param name="currentPage" value="${currentPageUserAttending}" />
                <jsp:param name="pageSize" value="${pageSize}" />
                <jsp:param name="baseUrl" value="/profile?page=${currentPageUserEvents}&size=${pageSize}&activeTab=events_attending" />
                <jsp:param name="paramName" value="attendingPage" />
              </jsp:include>
            </div>
          </div>
        </div>
      </div>

      <%--      <c:if test="${!user.present}">--%>
      <%--        <div class="content-container">--%>
      <%--          <div class="error-container">--%>
      <%--            <h2><spring:message code="profile.not.found"/></h2>--%>
      <%--            <p><spring:message code="profile.not.found.message"/></p>--%>
      <%--            <a href="<c:url value='/'/>" class="btn-primary">--%>
      <%--              <spring:message code="go.home"/>--%>
      <%--            </a>--%>
      <%--          </div>--%>
      <%--        </div>--%>
      <%--      </c:if>--%>
    </c:if>
  </div>
</div>


<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Get URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const activeTab = urlParams.get('activeTab');

    // Profile tabs functionality
    const profileTabs = document.querySelectorAll('.profile-tab');
    const profileSections = document.querySelectorAll('.profile-section');

    // Set active tab from URL parameter if available
    if (activeTab) {
      // Handle main tabs
      if (activeTab === 'info' || activeTab === 'journeys' || activeTab === 'events') {
        profileTabs.forEach(t => t.classList.remove('active'));
        profileSections.forEach(section => section.classList.remove('active'));

        const tabToActivate = document.querySelector(`.profile-tab[data-tab="${activeTab}"]`);
        if (tabToActivate) {
          tabToActivate.classList.add('active');
          document.getElementById(activeTab + '-section').classList.add('active');
        }
      }

      // Handle events sub-tabs
      const eventsTabs = document.querySelectorAll('.events-tab');
      const eventsTabContent = document.querySelectorAll('.events-tab-content');

      if (activeTab === 'eventsCreated') {
        // First activate the events main tab if not already active
        if (!document.querySelector('.profile-tab[data-tab="events"]').classList.contains('active')) {
          profileTabs.forEach(t => t.classList.remove('active'));
          profileSections.forEach(section => section.classList.remove('active'));

          document.querySelector('.profile-tab[data-tab="events"]').classList.add('active');
          document.getElementById('events-section').classList.add('active');
        }

        // Then activate the created events sub-tab
        eventsTabs.forEach(t => t.classList.remove('active'));
        eventsTabContent.forEach(content => content.classList.remove('active'));

        document.querySelector('.events-tab[data-events-tab="created"]').classList.add('active');
        document.getElementById('created-events').classList.add('active');
      } else if (activeTab === 'events_attending') {
        // First activate the events main tab if not already active
        if (!document.querySelector('.profile-tab[data-tab="events"]').classList.contains('active')) {
          profileTabs.forEach(t => t.classList.remove('active'));
          profileSections.forEach(section => section.classList.remove('active'));

          document.querySelector('.profile-tab[data-tab="events"]').classList.add('active');
          document.getElementById('events-section').classList.add('active');
        }

        // Then activate the attending events sub-tab
        eventsTabs.forEach(t => t.classList.remove('active'));
        eventsTabContent.forEach(content => content.classList.remove('active'));

        document.querySelector('.events-tab[data-events-tab="attending"]').classList.add('active');
        document.getElementById('attending-events').classList.add('active');
      }
    }

    // Profile tabs click handler
    profileTabs.forEach(tab => {
      tab.addEventListener('click', function() {
        // Remove active class from all tabs
        profileTabs.forEach(t => t.classList.remove('active'));

        // Add active class to clicked tab
        this.classList.add('active');

        // Hide all sections
        profileSections.forEach(section => section.classList.remove('active'));

        // Show the corresponding section
        const tabId = this.getAttribute('data-tab');
        document.getElementById(tabId + '-section').classList.add('active');

        // Update URL with active tab without reloading the page
        const url = new URL(window.location);
        url.searchParams.set('activeTab', tabId);
        window.history.pushState({}, '', url);
      });
    });

    // Events sub-tabs click handler
    const eventsTabs = document.querySelectorAll('.events-tab');
    const eventsTabContent = document.querySelectorAll('.events-tab-content');

    eventsTabs.forEach(tab => {
      tab.addEventListener('click', function() {
        // Remove active class from all tabs
        eventsTabs.forEach(t => t.classList.remove('active'));

        // Add active class to clicked tab
        this.classList.add('active');

        // Hide all content sections
        eventsTabContent.forEach(content => content.classList.remove('active'));

        // Show the corresponding content
        const tabId = this.getAttribute('data-events-tab');
        document.getElementById(tabId + '-events').classList.add('active');

        // Update URL with active tab without reloading the page
        const url = new URL(window.location);
        // Set the appropriate activeTab value based on which events tab is clicked
        if (tabId === 'created') {
          url.searchParams.set('activeTab', 'eventsCreated');
        } else if (tabId === 'attending') {
          url.searchParams.set('activeTab', 'events_attending');
        }
        window.history.pushState({}, '', url);
      });
    });
  });
</script>

</body>
</html>
