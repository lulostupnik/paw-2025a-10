<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="profile-section active" id="events-section">
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
    <button class="events-tab" data-events-tab="finished">
      <spring:message code="profile.events.finished"/>
    </button>
  </div>


  <div class="events-tab-content active" id="created-events">
    <div class="cards-grid">
      <c:if test="${empty events.content}">
        <div class="empty-state">
          <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>

          <p class="empty-message">
            <spring:message code="profile.no.created.events"/>
          </p>
          <c:if test="${isMine}">
            <a href="<c:url value='/events/create'/>" class="empty-action-btn">
              <spring:message code="event.create.button"/>
            </a>
          </c:if>


        </div>
      </c:if>

      <c:if test="${not empty events.content}">
        <c:forEach items="${events.content}" var="event">
          <jsp:include page="../events/event-card.jsp">
            <jsp:param name="username" value="${event.user.username}"/>
            <jsp:param name="eventId" value="${event.id}" />
            <jsp:param name="city" value="${event.city.name}" />
            <jsp:param name="date" value="${event.date}" />
            <jsp:param name="description" value="${event.description}" />
            <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
            <jsp:param name="attend" value="false" />
            <jsp:param name="firstname" value="${event.user.firstname}" />
            <jsp:param name="lastname" value="${event.user.lastname}"/>
            <jsp:param name="title" value="${event.title}"/>
            <jsp:param name="isFull" value="${event.attendeesLimit != null && event.attendeesLimit <= event.attendeesCount}"/>
            <jsp:param name="isOwner" value="true" />
          </jsp:include>
        </c:forEach>
      </c:if>
    </div>

    <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${events.totalPages}" />
      <jsp:param name="currentPage" value="${currentPageUserEvents}" />
      <jsp:param name="pageSize" value="${pageSize}" />
      <jsp:param name="baseUrl" value="/profile/${profileUser.id}/events?attendingPage=${currentPageUserAttending}&size=${pageSize}&finishedPage=${currentPageFinished}" />
    </jsp:include>
  </div>

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
          <jsp:include page="../events/event-card.jsp">
            <jsp:param name="username" value="${event.user.username}"/>
            <jsp:param name="eventId" value="${event.id}" />
            <jsp:param name="city" value="${event.city.name}" />
            <jsp:param name="date" value="${event.date}" />
            <jsp:param name="description" value="${event.description}" />
            <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
            <jsp:param name="attend" value="true" />
            <jsp:param name="firstname" value="${event.user.firstname}" />
            <jsp:param name="lastname" value="${event.user.lastname}"/>
            <jsp:param name="title" value="${event.title}"/>
            <jsp:param name="isOwner" value="false" />
            <jsp:param name="isFull" value="${event.attendeesLimit != null && event.attendeesLimit <= event.attendeesCount}"/>
          </jsp:include>
        </c:forEach>
      </c:if>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${userAttendingEvents.totalPages}" />
      <jsp:param name="currentPage" value="${currentPageUserAttending}" />
      <jsp:param name="pageSize" value="${pageSize}" />
      <jsp:param name="baseUrl" value="/profile/${profileUser.id}/events?page=${currentPageUserEvents}&size=${pageSize}&eventsTab=attending&finishedPage=${currentPageFinished}" />
      <jsp:param name="paramName" value="attendingPage" />
    </jsp:include>
  </div>
  <div class="events-tab-content" id="finished-events">
    <div class="cards-grid">
      <c:if test="${empty finishedEvents.content}">
        <div class="empty-state">
          <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          </div>
          <p class="empty-message">
            <spring:message code="profile.no.finished.events"/>
          </p>
          <a href="<c:url value='/events'/>" class="empty-action-btn">
            <spring:message code="profile.explore.events"/>
          </a>
        </div>
      </c:if>

      <c:if test="${not empty finishedEvents.content}">
        <c:forEach items="${finishedEvents.content}" var="event">
          <jsp:include page="../events/event-card.jsp">
            <jsp:param name="username" value="${event.user.username}"/>
            <jsp:param name="eventId" value="${event.id}" />
            <jsp:param name="city" value="${event.city.name}" />
            <jsp:param name="date" value="${event.date}" />
            <jsp:param name="description" value="${event.description}" />
            <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
            <jsp:param name="attend" value="false" />
            <jsp:param name="firstname" value="${event.user.firstname}" />
            <jsp:param name="lastname" value="${event.user.lastname}"/>
            <jsp:param name="title" value="${event.title}"/>
            <jsp:param name="isOwner" value="false" />
            <jsp:param name="isFull" value="${event.attendeesLimit != null && event.attendeesLimit <= event.attendeesCount}"/>
          </jsp:include>
        </c:forEach>
        </c:if>
    </div>
    <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${finishedEvents.totalPages}" />
      <jsp:param name="currentPage" value="${currentPageFinished}" />
      <jsp:param name="pageSize" value="${pageSize}" />
      <jsp:param name="baseUrl" value="/profile/${profileUser.id}/events?page=${currentPageUserEvents}&size=${pageSize}&attendingPage=${currentPageUserAttending}&eventsTab=finished" />
      <jsp:param name="paramName" value="finishedPage" />
    </jsp:include>

</div>
</div>