<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="tab-content active" id="events-tab">
  <c:set var="titleMessageCode" value="admin.manage.events" scope="request" />
  <c:set var="searchUrl" value="/dashboard/events" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.events" scope="request" />
<%--  <c:set var="showAddButton" value="true" scope="request" />--%>
<%--  <c:set var="addButtonUrl" value="/events/create" scope="request" />--%>
<%--  <c:set var="addButtonTextCode" value="event.create.button" scope="request" />--%>

  <div class="content-header">
    <h2><spring:message code="${titleMessageCode}" /></h2>
    <div class="action-bar">
      <div class="actions-container">
        <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
          <input type="text" name="search" class="search-input" placeholder="<spring:message code='${searchPlaceholderCode}' />" value="${param.search}">
          <input type="hidden" name="page" value="1">
          <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
          <button type="submit" class="search-button"><spring:message code="admin.search.button" /></button>
        </form>
<%--        <c:if test="${showAddButton}">--%>
<%--          <a href="<c:url value='${addButtonUrl}'/>" class="add-button">--%>
<%--            <i class="plus-icon"></i>--%>
<%--            <spring:message code="${addButtonTextCode}"/>--%>
<%--          </a>--%>
<%--        </c:if>--%>
      </div>
    </div>
  </div>
  <div class="table-container">
    <table class="data-table">
      <thead>
      <tr>
        <th><spring:message code="admin.column.id" /></th>
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
        <tr class="clickable-row" data-href="<c:url value="../events/${event.id}"/>" >
          <td><c:out value="${event.id}"/></td>
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

    <jsp:include page="../../components/pagination-controls.jsp">
      <jsp:param name="currentPage" value="${pagedEvents.currentPage}" />
      <jsp:param name="itemsPerPage" value="10" />
      <jsp:param name="totalPages" value="200" />
      <jsp:param name="search" value="${param.search}" />
      <jsp:param name="currentUrl" value="/dashboard/events" />
    </jsp:include>
  </div>
</div>