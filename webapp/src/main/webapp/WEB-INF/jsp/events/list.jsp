<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
</head>
<body>
<c:set var="searchUrl" value="/events" scope="request" />
<c:set var="searchPlaceholderCode" value="events.search.event" scope="request" />

<div class="layout-container">
  <!-- Main Content -->
  <div class="main-content">
    <jsp:include page="../components/navbar.jsp" />
    <div class="content-container">
      <div class="header-container">
        <h2 class="page-title">
          <spring:message code="event.list.title"/>
        </h2>
        <div class="journeys-actions">
            <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
              <input type="text" name="search" class="search-input" placeholder="<spring:message code='${searchPlaceholderCode}' />" value="${param.search}">
              <input type="hidden" name="page" value="1">
              <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
              <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
                <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
              </button>
            </form>
            <a href="<c:url value="/events/create"/>" class="btn btn-primary btn-with-icon">
              <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
              <spring:message code="event.create.button"/>
            </a>
        </div>

      </div>

      <!-- Events Grid -->
      <div class="events-container">
        <div class="events-grid">
          <c:if test="${empty eventsWithAttendance}">
            <div class="empty-state">
              <p class="empty-message"><spring:message code="event.no.events"/></p>
            </div>
          </c:if>
          <c:forEach items="${eventsWithAttendance}" var="eventAttendance">
            <jsp:include page="event-card.jsp">
              <jsp:param name="eventId" value="${eventAttendance.event.id}" />
              <jsp:param name="city" value="${eventAttendance.event.eventCity.name}" />
              <jsp:param name="date" value="${eventAttendance.event.date}" />
              <jsp:param name="username" value="${eventAttendance.event.user.username}"/>
              <jsp:param name="description" value="${eventAttendance.event.description}" />
              <jsp:param name="flyerImageId" value="${eventAttendance.event.flyerImageId}" />
              <jsp:param name="attend" value="${eventAttendance.attending}" />
              <jsp:param name="firstname" value="${eventAttendance.event.user.firstname}" />
              <jsp:param name="lastname" value="${eventAttendance.event.user.lastname}"/>
              <jsp:param name="title" value="${eventAttendance.event.title}"/>
              <jsp:param name="isFull" value="${eventAttendance.event.attendeesLimit.isPresent() && eventAttendance.event.attendeesLimit.get() <= eventAttendance.event.attendeesCount}"/>
            </jsp:include>
          </c:forEach>
        </div>
        <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
          <jsp:param name="pageObjectTotalPages" value="${eventsPage.totalPages}" />
          <jsp:param name="currentPage" value="${currentPage}" />
          <jsp:param name="pageSize" value="${pageSize}" />
          <jsp:param name="baseUrl" value="/events" />
        </jsp:include>

      </div>



    </div>
  </div>
</div>

</body>
</html>