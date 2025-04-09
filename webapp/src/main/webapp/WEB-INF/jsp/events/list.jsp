<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/event-list.css"/>" />
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

      <!-- Events List with vertical scrolling -->
      <div class="events-container">
        <div class="events-grid">
          <c:forEach items="${events}" var="event">
            <jsp:include page="event-card.jsp">
              <jsp:param name="eventId" value="${event.id}" />
              <jsp:param name="eventCity" value="${event.eventCity.name}" />
              <jsp:param name="eventDate" value="${event.date}" />
              <jsp:param name="eventDescription" value="${event.description}" />
              <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
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