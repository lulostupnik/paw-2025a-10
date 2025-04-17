<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/components/cards.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/pages/events.css"/>" />
</head>
<body>

<div class="layout-container">
  <!-- Include the sidebar component -->
  <jsp:include page="../components/sidebar.jsp" />

  <!-- Main Content -->
  <div class="main-content">
    <div class="content-container">
      <div class="header-container">
        <h2 class="page-title">
          <spring:message code="event.list.title"/>
        </h2>
        <a href="<c:url value="/events/create"/>" class="btn btn-primary">
          <spring:message code="event.create.button"/>
        </a>
      </div>

      <!-- Category Navigation (optional) -->
      <div class="category-nav">
        <div class="category-scroll">
          <a href="<c:url value="/events?type=all"/>" class="category-item ${empty param.type || param.type == 'all' ? 'active' : ''}">
            <div class="category-icon">
              <i class="fas fa-calendar-alt"></i>
            </div>
            <span><spring:message code="event.type.all"/></span>
          </a>
          <a href="<c:url value="/events?type=social"/>" class="category-item ${param.type == 'social' ? 'active' : ''}">
            <div class="category-icon">
              <i class="fas fa-users"></i>
            </div>
            <span><spring:message code="event.type.social"/></span>
          </a>
          <a href="<c:url value="/events?type=cultural"/>" class="category-item ${param.type == 'cultural' ? 'active' : ''}">
            <div class="category-icon">
              <i class="fas fa-landmark"></i>
            </div>
            <span><spring:message code="event.type.cultural"/></span>
          </a>
          <a href="<c:url value="/events?type=academic"/>" class="category-item ${param.type == 'academic' ? 'active' : ''}">
            <div class="category-icon">
              <i class="fas fa-graduation-cap"></i>
            </div>
            <span><spring:message code="event.type.academic"/></span>
          </a>
          <a href="<c:url value="/events?type=travel"/>" class="category-item ${param.type == 'travel' ? 'active' : ''}">
            <div class="category-icon">
              <i class="fas fa-plane"></i>
            </div>
            <span><spring:message code="event.type.travel"/></span>
          </a>
        </div>
      </div>

      <!-- Events Grid -->
      <div class="events-container">
        <div class="events-grid">
          <c:forEach items="${events}" var="event">
            <c:set var="attend" value="false" />
            <c:forEach items="${eventsAttended}" var="attendedEvent">
              <c:if test="${attendedEvent.id == event.id}">
                <c:set var="attend" value="true" />
              </c:if>
            </c:forEach>
            <jsp:include page="event-card.jsp">
              <jsp:param name="eventId" value="${event.id}" />
              <jsp:param name="eventCity" value="${event.eventCity.name}" />
              <jsp:param name="eventDate" value="${event.date}" />
              <jsp:param name="eventDescription" value="${event.description}" />
              <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
              <jsp:param name="attend" value="${attend}" />
            </jsp:include>
          </c:forEach>

          <c:if test="${empty events}">
            <div class="empty-state">
              <p class="empty-message"><spring:message code="event.no.events"/></p>
            </div>
          </c:if>
        </div>
      </div>
    </div>
  </div>
</div>

</body>
</html>