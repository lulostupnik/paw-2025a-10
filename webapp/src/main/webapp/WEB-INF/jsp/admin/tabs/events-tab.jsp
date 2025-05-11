<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<link rel="stylesheet" href="<c:url value='/resources/css/dashboard.css'/>" />

<div class="tab-content active" id="events-tab">
  <c:set var="titleMessageCode" value="admin.manage.events" scope="request" />
  <c:set var="searchUrl" value="/dashboard/events" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.events" scope="request" />




  <div class="content-header">
    <h2><spring:message code="${titleMessageCode}" /></h2>
    <div class="action-bar">
      <div class="actions-container">
        <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
          <input type="text" name="search" class="search-input"
                 placeholder="<spring:message code='${searchPlaceholderCode}' />"
                 value="<c:out value="${param.search}"/>">          <input type="hidden" name="page" value="1">
          <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
          <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
            <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
          </button>
        </form>






      </div>
    </div>
  </div>
  <div class="table-container">
    <table class="data-table">
      <thead>
      <tr>
        <th><spring:message code="admin.column.title" /></th>
        <th><spring:message code="admin.column.organizer" /></th>
        <th><spring:message code="admin.column.location" /></th>
        <th><spring:message code="admin.column.date" /></th>
        <th><spring:message code="admin.column.attendees" /></th>
      </tr>
      </thead>
      <tbody>
      <c:set var="events" value="${pagedEvents.content}" />
      <c:forEach items="${events}" var="event">
        <tr class="clickable-row"  onclick="saveLink()" data-href="<c:url value="../events/${event.id}"/>" >
          <td><c:out value="${event.title}"/></td>
          <td><c:out value="${event.user.username}"/></td>
          <td><c:out value="${event.eventCity}"/></td>
          <td><c:out value="${event.date}"/></td>
          <td>
            <c:choose>
              <c:when test="${!empty event.attendeesLimit && event.attendeesLimit.isPresent() && event.attendeesLimit.get() > 0}">
                <div class="attendee-progress">
                  <span class="attendee-count"><c:out value="${event.attendeesCount}"/>/<c:out value="${event.attendeesLimit.get()}"/></span>
                  <div class="progress-bar">
                    <div class="progress-fill" style="width: <c:out value="${(event.attendeesCount * 100 / event.attendeesLimit.get())}"/>%"></div>
                  </div>
                </div>
              </c:when>
              <c:otherwise>
                <span class="unlimited-attendees"><spring:message code="admin.unlimited.attendees" /></span>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty events}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>

    <jsp:include page="../../components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${pagedEvents.totalPages}" />
      <jsp:param name="currentPage" value="${pagedEvents.currentPage}" />
      <jsp:param name="pageSize" value="10" />
      <jsp:param name="baseUrl" value="/dashboard/events?search=${param.search}" />
    </jsp:include>









  </div>
</div>
<script>
  function saveLink() {
    sessionStorage.setItem("rutaAnterior", window.location.href);
  }
</script>