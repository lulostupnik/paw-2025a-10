<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>

<div class="layout-container">
  <!-- Include the sidebar component -->

  <!-- Main Content -->
  <div class="main-content">
    <jsp:include page="../components/navbar.jsp" />
    <div class="content-container">
      <div class="header-container">
        <h2 class="page-title">
          <spring:message code="event.list.title"/>
        </h2>
        <a href="<c:url value="/events/create"/>" class="btn btn-primary">
          <spring:message code="event.create.button"/>
        </a>
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
              <jsp:param name="city" value="${event.eventCity.name}" />
              <jsp:param name="date" value="${event.date}" />
              <jsp:param name="description" value="${event.description}" />
              <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
              <jsp:param name="attend" value="${attend}" />
              <jsp:param name="firstname" value="${event.user.firstname}" />
              <jsp:param name="lastname" value="${event.user.lastname}"/>
              <jsp:param name="title" value="${event.title}"/>
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