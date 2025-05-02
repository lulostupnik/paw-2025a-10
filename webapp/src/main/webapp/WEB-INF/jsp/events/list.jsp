<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:set var="eventsPage" value="${eventsPage}" />
<c:set var="currentPage" value="${currentPage}" />
<c:set var="pageSize" value="${pageSize}" />
<c:set var="baseUrl" value="/events" />


<html>
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
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
        <a href="<c:url value="/events/create"/>" class="btn btn-primary btn-with-icon">
          <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
          <spring:message code="event.create.button"/>
        </a>
      </div>

      <!-- Events Grid -->
      <div class="events-container">
        <div class="events-grid">
          <c:if test="${empty events and empty eventsWithAttendance}">
            <div class="empty-state">
              <p class="empty-message"><spring:message code="event.no.events"/></p>
            </div>
          </c:if>
          <c:if test="${empty eventsWithAttendance and not empty events}">
            <c:forEach items="${events}" var="event">
                <c:set var="attend" value="false" />
                <jsp:include page="event-card.jsp">
                  <jsp:param name="eventId" value="${event.id}" />
                  <jsp:param name="city" value="${event.eventCity.name}" />
                  <jsp:param name="date" value="${event.date}" />
                  <jsp:param name="username" value="${event.user.username}"/>
                  <jsp:param name="description" value="${event.description}" />
                  <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
                  <jsp:param name="attend" value="${attend}" />
                  <jsp:param name="firstname" value="${event.user.firstname}" />
                  <jsp:param name="lastname" value="${event.user.lastname}"/>
                  <jsp:param name="title" value="${event.title}"/>
                  <jsp:param name="isFull" value="${event.attendeesLimit.isPresent() && event.attendeesLimit.get() <= event.attendeesCount}"/>
                    <jsp:param name="isOwner" value="false" />
                </jsp:include>
            </c:forEach>
          </c:if>
          <c:if test="${not empty eventsWithAttendance and empty events}">
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
          </c:if>
        </div>
      </div>

<%--      <c:if test="${eventsPage.totalPages > 1}">--%>
<%--        <div class="pagination">--%>
<%--          <!-- Previous -->--%>
<%--          <c:if test="${currentPage > 1}">--%>
<%--            <c:url var="prevUrl" value="/events">--%>
<%--              <c:param name="page" value="${currentPage - 1}" />--%>
<%--              <c:param name="size" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${prevUrl}" class="page-link">&laquo; Prev</a>--%>
<%--          </c:if>--%>

<%--          <!-- First page + ellipsis -->--%>
<%--          <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />--%>
<%--          <c:set var="end" value="${currentPage + 2 > eventsPage.totalPages ? eventsPage.totalPages : currentPage + 2}" />--%>

<%--          <c:if test="${start > 1}">--%>
<%--            <c:url var="firstPageUrl" value="/events">--%>
<%--              <c:param name="page" value="1" />--%>
<%--              <c:param name="size" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${firstPageUrl}" class="page-link">1</a>--%>
<%--            <span class="page-ellipsis">...</span>--%>
<%--          </c:if>--%>

<%--          <!-- Centered page numbers -->--%>
<%--          <c:forEach begin="${start}" end="${end}" var="pageNum">--%>
<%--            <c:url var="pageUrl" value="/events">--%>
<%--              <c:param name="page" value="${pageNum}" />--%>
<%--              <c:param name="size" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${pageUrl}" class="page-link ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>--%>
<%--          </c:forEach>--%>

<%--          <!-- Last page + ellipsis -->--%>
<%--          <c:if test="${end < eventsPage.totalPages}">--%>
<%--            <span class="page-ellipsis">...</span>--%>
<%--            <c:url var="lastPageUrl" value="/events">--%>
<%--              <c:param name="page" value="${eventsPage.totalPages}" />--%>
<%--              <c:param name="size" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${lastPageUrl}" class="page-link">${eventsPage.totalPages}</a>--%>
<%--          </c:if>--%>

<%--          <!-- Next -->--%>
<%--          <c:if test="${currentPage < eventsPage.totalPages}">--%>
<%--            <c:url var="nextUrl" value="/events">--%>
<%--              <c:param name="page" value="${currentPage + 1}" />--%>
<%--              <c:param name="size" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${nextUrl}" class="page-link">Next &raquo;</a>--%>
<%--          </c:if>--%>
<%--        </div>--%>
<%--      </c:if>--%>

      <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp" />


    </div>
  </div>
</div>

</body>
</html>