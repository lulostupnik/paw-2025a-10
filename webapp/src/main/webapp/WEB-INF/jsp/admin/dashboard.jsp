<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="admin.dashboard.title" /></title>

  <!-- Include existing CSS files -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css' />">

  <!-- Dashboard specific CSS -->
  <link rel="stylesheet" href="<c:url value='/resources/css/pages/dashboard.css' />">
</head>
<body>
<jsp:include page="../components/navbar.jsp" />
<div class="content-container">
  <header class="header">
    <h1 class="page-title"><spring:message code="admin.dashboard.heading" /></h1>
    <div class="user-info">
      <span class="admin-badge"><spring:message code="admin.role" /></span>
    </div>
  </header>

  <div class="dashboard-tabs">
    <button class="tab-button" data-tab="journeys"><spring:message code="admin.tab.journeys" /></button>
    <button class="tab-button" data-tab="users"><spring:message code="admin.tab.users" /></button>
    <button class="tab-button" data-tab="events"><spring:message code="admin.tab.events" /></button>
  </div>

  <!-- Journeys Tab Content -->
  <div class="tab-content" id="journeys-tab">
    <div class="content-header">
      <h2><spring:message code="admin.manage.journeys" /></h2>
      <div class="action-bar">
        <div class="search-container">
          <input type="text" class="search-input" placeholder="<spring:message code='admin.search.journeys' />">
          <button class="filter-button"><i class="filter-icon"></i></button>
        </div>
        <a href="<c:url value="/journeys/create"/>" class="btn btn-primary btn-with-icon">
          <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
          <spring:message code="journey.create.button"/>
        </a>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th><spring:message code="admin.column.id" /></th>
          <th><spring:message code="admin.column.user" /></th>
          <th><spring:message code="admin.column.destination" /></th>
          <th><spring:message code="admin.column.university" /></th>
          <th><spring:message code="admin.column.start.date" /></th>
          <th><spring:message code="admin.column.end.date" /></th>
          <th><spring:message code="admin.column.actions" /></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${journeys}" var="journey">
          <tr>
            <td><c:out value="${journey.id}"/></td>
            <td><c:out value="${journey.user.firstname}"/></td>
            <td><c:out value="${journey.destinationUniversity.city}"/></td>
            <td><c:out value="${journey.destinationUniversity}"/></td>
            <td><fmt:parseDate value="${journey.startDate}" pattern="yyyy-MM-dd" /></td>
            <td><fmt:parseDate value="${journey.endDate}" pattern="yyyy-MM-dd" /></td>
            <td>
              <button class="action-button" data-id=<c:out value="${journey.id}"/> >
                <i class="more-icon"></i>
              </button>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Users Tab Content -->
  <div class="tab-content" id="users-tab">
    <div class="content-header">
      <h2><spring:message code="admin.manage.users" /></h2>
      <div class="action-bar">
        <div class="search-container">
          <input type="text" class="search-input" placeholder="<spring:message code='admin.search.users' />">
          <button class="filter-button"><i class="filter-icon"></i></button>
        </div>
        <a href="<c:url value="/users/create"/>" class="btn btn-primary btn-with-icon">
          <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="admin.add.user"/>" class="btn-icon" />
          <spring:message code="admin.add.user"/>
        </a>
      </div>
    </div>

    <div class="table-container">
      <table class="data-table">
        <thead>
        <tr>
          <th><spring:message code="admin.column.id" /></th>
          <th><spring:message code="admin.column.name" /></th>
          <th><spring:message code="admin.column.email" /></th>
          <th><spring:message code="admin.column.university" /></th>
          <th><spring:message code="admin.column.actions" /></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${users}" var="user">
          <tr>
            <td><c:out value="${user.id}"/></td>
            <td><c:out value="${user.firstname}"/></td>
            <td><c:out value="${user.email}"/></td>
            <td><c:out value="${user.university}"/></td>
            <td>
              <button class="action-button" data-id=<c:out value="${user.id}"/> >
                <i class="more-icon"></i>
              </button>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Events Tab Content -->
  <div class="tab-content" id="events-tab">
    <div class="content-header">
      <h2><spring:message code="admin.manage.events" /></h2>
      <div class="action-bar">
        <div class="search-container">
          <input type="text" class="search-input" placeholder="<spring:message code='admin.search.events' />">
          <button class="filter-button"><i class="filter-icon"></i></button>
        </div>
        <a href="<c:url value="/events/create"/>" class="btn btn-primary btn-with-icon">
          <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
          <spring:message code="event.create.button"/>
        </a>
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
          <th><spring:message code="admin.column.actions" /></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${events}" var="event">
          <tr>
            <td><c:out value="${event.id}"/></td>
            <td><c:out value="${event.title}"/></td>
            <td><c:out value="${event.user.username}"/></td>
            <td><c:out value="${event.eventCity}"/></td>
            <td><fmt:parseDate value="${event.date}" pattern="yyyy-MM-dd" /></td>
            <td>
              <c:choose>
                <c:when test="${event.attendeesLimit.isPresent() && event.attendeesLimit.get() != 0}">
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
            <td>
              <button class="action-button" data-id=<c:out value="${event.id}"/>>
                <i class="more-icon"></i>
              </button>
            </td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </div>
  </div>

  <!-- Action Dropdown Menu Template -->
  <div class="dropdown-menu" id="action-dropdown-template" style="display: none;">
    <ul>
      <li class="dropdown-item edit-item">
        <i class="edit-icon"></i> <spring:message code="admin.action.edit" />
      </li>
      <li class="dropdown-item manage-attendees-item">
        <i class="attendees-icon"></i> <spring:message code="admin.action.manage.attendees" />
      </li>
      <li class="dropdown-item delete-item">
        <i class="delete-icon"></i> <spring:message code="admin.action.delete" />
      </li>
    </ul>
  </div>
</div>

<!-- Include JavaScript files -->
<script src="<c:url value='/resources/js/dashboard.js' />"></script>
</body>
</html>
