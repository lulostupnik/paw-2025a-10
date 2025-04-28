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
          <c:set var="journeys" value="${pagedJourneys.content}" />
          <c:forEach items="${journeys}" var="pagedJourney">
            <tr>
              <td><c:out value="${pagedJourney.id}"/></td>
              <td><c:out value="${pagedJourney.user.username}"/></td>
              <td><c:out value="${pagedJourney.destinationUniversity.city}"/></td>
              <td><c:out value="${pagedJourney.destinationUniversity}"/></td>
              <td><fmt:formatDate value="${pagedJourney.startDate}" pattern="yyyy-MM-dd" /></td>
              <td><fmt:formatDate value="${pagedJourney.endDate}" pattern="yyyy-MM-dd" /></td>
              <td>
                <button class="action-button" data-id="<c:out value="${pagedJourney.id}"/>">
                  <i class="more-icon"></i>
                </button>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <c:set var="currentPage" value="${param.page != null ? param.page : 1}" />
        <c:set var="itemsPerPage" value="${param.pageSize != null ? param.pageSize : 10}" />
        <c:set var="currentUrl" value="${pageContext.request.contextPath}/dashboard/journeys" />
        <jsp:include page="../components/pagination-controls.jsp">
          <jsp:param name="currentPage" value="${currentPage}" />
          <jsp:param name="itemsPerPage" value="${itemsPerPage}" />
          <jsp:param name="totalPages" value="${pagedJourneys.totalPages}" />
          <jsp:param name="currentUrl" value="${currentUrl}" />
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
          <c:set var="users" value="${pagedUsers.content}" />
          <c:forEach items="${users}" var="pagedUser">
            <tr>
              <td><c:out value="${pagedUser.id}"/></td>
              <td><c:out value="${pagedUser.firstname}"/></td>
              <td><c:out value="${pagedUser.email}"/></td>
              <td><c:out value="${pagedUser.university}"/></td>
              <td>
                <button class="action-button" data-id="<c:out value="${pagedUser.id}"/>">
                  <i class="more-icon"></i>
                </button>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <c:set var="currentPage" value="${param.page != null ? param.page : 1}" />
        <c:set var="itemsPerPage" value="${param.pageSize != null ? param.pageSize : 10}" />
        <c:set var="currentUrl" value="${pageContext.request.contextPath}/dashboard/users" />
        <jsp:include page="../components/pagination-controls.jsp">
          <jsp:param name="currentPage" value="${currentPage}" />
          <jsp:param name="itemsPerPage" value="${itemsPerPage}" />
          <jsp:param name="totalPages" value="${pagedUsers.totalPages}" />
          <jsp:param name="currentUrl" value="${currentUrl}" />
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
            <input type="text" class="search-input" placeholder="<spring:message code='admin.search.events' />">
            <button class="filter-button"><i class="filter-icon"></i></button>
            <a href="<c:url value="/events/create"/>" class="add-button">
              <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="event.create.button"/>" class="btn-icon" />
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
          <c:forEach items="${events}" var="pagedEvent">
            <tr>
              <td><c:out value="${pagedEvent.id}"/></td>
              <td><c:out value="${pagedEvent.title}"/></td>
              <td><c:out value="${pagedEvent.user.username}"/></td>
              <td><c:out value="${pagedEvent.eventCity}"/></td>
              <td><fmt:formatDate value="${pagedEvent.date}" pattern="yyyy-MM-dd" /></td>
              <td>
                <c:choose>
                  <c:when test="${pagedEvent.attendeesLimit.isPresent() && pagedEvent.attendeesLimit.get() != 0}">
                    <div class="attendee-progress">
                      <span class="attendee-count"><c:out value="${pagedEvent.attendeesCount}"/>/<c:out value="${pagedEvent.attendeesLimit.get()}"/></span>
                      <div class="progress-bar">
                        <div class="progress-fill" style="width: <c:out value="${(pagedEvent.attendeesCount * 100 / pagedEvent.attendeesLimit.get())}"/>%"></div>
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <span class="unlimited-attendees"><spring:message code="admin.unlimited.attendees" /></span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td>
                <button class="action-button" data-id="<c:out value="${pagedEvent.id}"/>">
                  <i class="more-icon"></i>
                </button>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>

        <c:set var="currentPage" value="${param.page != null ? param.page : 1}" />
        <c:set var="itemsPerPage" value="${param.pageSize != null ? param.pageSize : 10}" />
        <c:set var="currentUrl" value="${pageContext.request.contextPath}/dashboard/events" />
        <jsp:include page="../components/pagination-controls.jsp">
          <jsp:param name="currentPage" value="${currentPage}" />
          <jsp:param name="itemsPerPage" value="${itemsPerPage}" />
          <jsp:param name="totalPages" value="${pagedEvents.totalPages}" />
          <jsp:param name="currentUrl" value="${currentUrl}" />
        </jsp:include>
      </div>
    </div>
  </c:if>

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
