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

  <!-- Include CSS files -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css' />">
  <link rel="stylesheet" href="<c:url value='/resources/css/pages/dashboard.css' />">
</head>
<body>
<jsp:include page="../components/navbar.jsp" />
<div class="content-container">
  <header class="header">
    <h1 class="page-title"><spring:message code="admin.dashboard.heading" /></h1>
  </header>

  <div class="dashboard-tabs">
    <a href="<c:url value='/dashboard/journeys'/>" class="tab-button ${pagedJourneys != null ? 'active' : ''}">
      <spring:message code="admin.tab.journeys" />
    </a>
    <a href="<c:url value='/dashboard/users'/>" class="tab-button ${pagedUsers != null ? 'active' : ''}">
      <spring:message code="admin.tab.users" />
    </a>
    <a href="<c:url value='/dashboard/events'/>" class="tab-button ${pagedEvents != null ? 'active' : ''}">
      <spring:message code="admin.tab.events" />
    </a>
  </div>

  <!-- Journeys Tab Content -->
  <c:if test="${pagedJourneys != null}">
    <div class="tab-content active" id="journeys-tab">
      <div class="content-header">
        <h2><spring:message code="admin.manage.journeys" /></h2>
        <div class="action-bar">
          <div class="actions-container">
            <form action="<c:url value='/dashboard/journeys'/>" method="get" class="search-form">
              <input type="text" name="search" class="search-input" placeholder="<spring:message code='admin.search.journeys' />" value="${param.search}">
              <input type="hidden" name="page" value="1">
              <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
              <button type="submit" class="search-button"><spring:message code="admin.search.button" /></button>
            </form>
            <button class="filter-button"><i class="filter-icon"></i></button>
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
          <c:set var="journeys" value="${pagedJourneys.content}" />
          <c:forEach items="${journeys}" var="journey">
            <tr>
              <td><c:out value="${journey.id}"/></td>
              <td><c:out value="${journey.user.username}"/></td>
              <td><c:out value="${journey.destinationUniversity.city}"/></td>
              <td><c:out value="${journey.destinationUniversity.name}"/></td>
              <td><c:out value="${journey.startDate}"/></td>
              <td><c:out value="${journey.endDate}"/></td>
              <td>
                <button class="action-button" data-id="<c:out value="${journey.id}"/>">
                  <i class="more-icon"></i>
                </button>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <c:if test="${empty journeys}">
          <div class="no-results">
            <spring:message code="admin.no.results" />
          </div>
        </c:if>

        <jsp:include page="../components/pagination-controls.jsp">
          <jsp:param name="currentPage" value="${pagedJourneys.currentPage}" />
          <jsp:param name="itemsPerPage" value="10" />
          <jsp:param name="totalPages" value="200" />
          <jsp:param name="search" value="${param.search}" />
          <jsp:param name="currentUrl" value="/dashboard/journeys" />
        </jsp:include>
      </div>
    </div>
  </c:if>

  <!-- Users Tab Content -->
  <c:if test="${pagedUsers != null}">
    <div class="tab-content active" id="users-tab">
      <div class="content-header">
        <h2><spring:message code="admin.manage.users" /></h2>
        <div class="action-bar">
          <div class="actions-container">
            <form action="<c:url value='/dashboard/users'/>" method="get" class="search-form">
              <input type="text" name="search" class="search-input" placeholder="<spring:message code='admin.search.users' />" value="${param.search}">
              <input type="hidden" name="page" value="1">
              <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
              <button type="submit" class="search-button"><spring:message code="admin.search.button" /></button>
            </form>
            <button class="filter-button"><i class="filter-icon"></i></button>
            <a href="<c:url value="/users/create"/>" class="add-button">
              <i class="plus-icon"></i>
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
          <c:set var="users" value="${pagedUsers.content}" />
          <c:forEach items="${users}" var="user">
            <tr>
              <td><c:out value="${user.id}"/></td>
              <td><c:out value="${user.firstname}"/></td>
              <td><c:out value="${user.email}"/></td>
              <td><c:out value="${user.university}"/></td>
              <td>
                <button class="action-button" data-id="<c:out value="${user.id}"/>">
                  <i class="more-icon"></i>
                </button>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <c:if test="${empty users}">
          <div class="no-results">
            <spring:message code="admin.no.results" />
          </div>
        </c:if>

        <jsp:include page="../components/pagination-controls.jsp">
          <jsp:param name="currentPage" value="${pagedUsers.currentPage}" />
          <jsp:param name="itemsPerPage" value="10" />
          <jsp:param name="totalPages" value="200" />
          <jsp:param name="search" value="${param.search}" />
          <jsp:param name="currentUrl" value="/dashboard/users" />
        </jsp:include>
      </div>
    </div>
  </c:if>

  <!-- Events Tab Content -->
  <c:if test="${pagedEvents != null}">
    <div class="tab-content active" id="events-tab">
      <div class="content-header">
        <h2><spring:message code="admin.manage.events" /></h2>
        <div class="action-bar">
          <div class="actions-container">
            <form action="<c:url value='/dashboard/events'/>" method="get" class="search-form">
              <input type="text" name="search" class="search-input" placeholder="<spring:message code='admin.search.events' />" value="${param.search}">
              <input type="hidden" name="page" value="1">
              <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
              <button type="submit" class="search-button"><spring:message code="admin.search.button" /></button>
            </form>
            <button class="filter-button"><i class="filter-icon"></i></button>
            <a href="<c:url value="/events/create"/>" class="add-button">
              <i class="plus-icon"></i>
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
          <c:set var="events" value="${pagedEvents.content}" />
          <c:forEach items="${events}" var="event">
            <tr>
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
              <td>
                <button class="action-button" data-id="<c:out value="${event.id}"/>">
                  <i class="more-icon"></i>
                </button>
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

        <jsp:include page="../components/pagination-controls.jsp">
          <jsp:param name="currentPage" value="${pagedEvents.currentPage}" />
          <jsp:param name="itemsPerPage" value="10" />
          <jsp:param name="totalPages" value="200" />
          <jsp:param name="search" value="${param.search}" />
          <jsp:param name="currentUrl" value="/dashboard/events" />
        </jsp:include>
      </div>
    </div>
  </c:if>

  <!-- Action Dropdown Menu Template -->
  <div class="dropdown-menu-dashboard" id="action-dropdown-template" style="display: none;">
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
<script src="<c:url value='/resources/js/pagination.js' />"></script>
</body>
</html>
