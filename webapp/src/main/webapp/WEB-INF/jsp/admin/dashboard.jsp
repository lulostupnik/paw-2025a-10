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
        <div class="actions-container">
          <input type="text" class="search-input" placeholder="<spring:message code='admin.search.journeys' />">
          <button class="filter-button"><i class="filter-icon"></i></button>
          <a href="<c:url value="/journeys/create"/>" class="add-button">
            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
            <spring:message code="journey.create.button"/>
          </a>
        </div>
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
        <c:forEach items="${pagedJourneys}" var="pagedJourney">
          <tr>
            <td><c:out value="${pagedJourney.journey.id}"/></td>
            <td><c:out value="${pagedJourney.journey.user.firstname}"/></td>
            <td><c:out value="${pagedJourney.journey.destinationUniversity.city}"/></td>
            <td><c:out value="${pagedJourney.journey.destinationUniversity}"/></td>
            <td><fmt:parseDate value="${pagedJourney.journey.startDate}" pattern="yyyy-MM-dd" /></td>
            <td><fmt:parseDate value="${pagedJourney.journey.endDate}" pattern="yyyy-MM-dd" /></td>
            <td>
              <button class="action-button" data-id=<c:out value="${pagedJourney.journey.id}"/> >
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
        <div class="actions-container">
          <input type="text" class="search-input" placeholder="<spring:message code='admin.search.users' />">
          <button class="filter-button"><i class="filter-icon"></i></button>
          <a href="<c:url value="/users/create"/>" class="add-button">
          <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="admin.add.user"/>" class="btn-icon" />
          <spring:message code="admin.add.user"/>
          </a>
        </div>
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
        <c:forEach items="${pagedUsers}" var="pagedUser">
          <tr>
            <td><c:out value="${pagedUser.user.id}"/></td>
            <td><c:out value="${pagedUser.user.firstname}"/></td>
            <td><c:out value="${pagedUser.user.email}"/></td>
            <td><c:out value="${pagedUser.user.university}"/></td>
            <td>
              <button class="action-button" data-id=<c:out value="${pagedUser.user.id}"/> >
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
        <div class="actions-container">
          <input type="text" class="search-input" placeholder="<spring:message code='admin.search.events' />">
          <button class="filter-button"><i class="filter-icon"></i></button><a href="<c:url value="/events/create"/>" class="add-button">
          <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
          <spring:message code="event.create.button"/>
        </a>
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
          <th><spring:message code="admin.column.actions" /></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${pagedEvents}" var="pagedEvent">
          <tr>
            <td><c:out value="${pagedEvent.event.id}"/></td>
            <td><c:out value="${pagedEvent.event.title}"/></td>
            <td><c:out value="${pagedEvent.event.user.username}"/></td>
            <td><c:out value="${pagedEvent.event.eventCity}"/></td>
            <td><fmt:parseDate value="${pagedEvent.event.date}" pattern="yyyy-MM-dd" /></td>
            <td>
              <c:choose>
                <c:when test="${pagedEvent.event.attendeesLimit.isPresent() && pagedEvent.event.attendeesLimit.get() != 0}">
                  <div class="attendee-progress">
                    <span class="attendee-count"><c:out value="${pagedEvent.event.attendeesCount}"/>/<c:out value="${pagedEvent.event.attendeesLimit.get()}"/></span>
                    <div class="progress-bar">
                      <div class="progress-fill" style="width: <c:out value="${(pagedEvent.event.attendeesCount * 100 / pagedEvent.event.attendeesLimit.get())}"/>%"></div>
                    </div>
                  </div>
                </c:when>
                <c:otherwise>
                  <span class="unlimited-attendees"><spring:message code="admin.unlimited.attendees" /></span>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <button class="action-button" data-id=<c:out value="${pagedEvent.event.id}"/>>
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
