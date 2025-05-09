<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <title><spring:message code="event.page.title"/></title>
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
</head>
<body>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>
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
            <input type="text" name="search" class="search-input"
                   placeholder="<spring:message code='${searchPlaceholderCode}' />"
                   value="<c:out value="${param.search}"/>">
            <input type="hidden" name="page" value="1">
            <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 4}">

            <!-- Preserve sort parameters -->
            <c:if test="${not empty param.sort}">
              <input type="hidden" name="sort" value="<c:out value="${param.sort}"/>">
            </c:if>
            <c:if test="${not empty param.direction}">
              <input type="hidden" name="direction" value="<c:out value="${param.direction}"/>">
            </c:if>

            <!-- Preserve filter parameters - Fixed field names to match the filter form -->
            <c:if test="${not empty param.destination}">
              <input type="hidden" name="destination" value="<c:out value="${param.destination}"/>">
            </c:if>
            <c:if test="${not empty param.cityName}">
              <input type="hidden" name="cityName" value="<c:out value="${param.cityName}"/>">
            </c:if>
            <c:if test="${not empty param.startDate}">
              <input type="hidden" name="startDate" value="<c:out value="${param.startDate}"/>">
            </c:if>
            <c:if test="${not empty param.endDate}">
              <input type="hidden" name="endDate" value="<c:out value="${param.endDate}"/>">
            </c:if>
            <c:if test="${not empty param.interests}">
              <input type="hidden" name="interests" value="<c:out value="${param.interests}"/>">
            </c:if>
            <c:if test="${not empty param.interestName}">
              <input type="hidden" name="interestName" value="<c:out value="${param.interestName}"/>">
            </c:if>

            <!-- Preserve tab parameters -->
            <c:if test="${not empty param.isUpcoming}">
              <input type="hidden" name="isUpcoming" value="<c:out value="${param.isUpcoming}"/>">
            </c:if>
            <c:if test="${not empty param.attending}">
              <input type="hidden" name="attending" value="<c:out value="${param.attending}"/>">
            </c:if>
            <c:if test="${not empty param.isPast}">
              <input type="hidden" name="isPast" value="<c:out value="${param.isPast}"/>">
            </c:if>

            <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
              <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
            </button>
          </form>
          <button id="filterToggleBtn" class="btn-secondary btn-with-icon">
            <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="event.filter.toggle"/>" class="btn-icon filter-icon" />
            <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="event.filter.close"/>" class="btn-icon close-icon" style="display: none;" />
            <spring:message code="event.filter.toggle"/>
          </button>
          <div class="sort-dropdown">
            <button id="sortToggleBtn" class="btn-secondary btn-with-icon">
              <img src="<c:url value='/resources/icons/sort.svg'/>" alt="<spring:message code="event.sort.toggle"/>" class="btn-icon" />
              <spring:message code="event.sort.toggle"/>
            </button>
            <div id="sortDropdown" class="dropdown-content" style="display: none;">
              <!-- Updated sort links to use the correct parameter names and preserve tab parameters -->
              <a href="<c:url value="/events?sort=date&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.attending ? '&attending='.concat(param.attending) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'date' && param.direction == 'asc' ? 'active' : ''}">
                <spring:message code="event.sort.date.asc"/>
              </a>
              <a href="<c:url value="/events?sort=date&direction=desc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.attending ? '&attending='.concat(param.attending) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'date' && param.direction == 'desc' ? 'active' : ''}">
                <spring:message code="event.sort.date.desc"/>
              </a>
              <a href="<c:url value="/events?sort=attendees&direction=desc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.attending ? '&attending='.concat(param.attending) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'attendees' && param.direction == 'desc' ? 'active' : ''}">
                <spring:message code="event.sort.attendees"/>
              </a>
              <a href="<c:url value="/events?sort=city&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.attending ? '&attending='.concat(param.attending) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'city' && param.direction == 'asc' ? 'active' : ''}">
                <spring:message code="event.sort.city"/>
              </a>
              <a href="<c:url value="/events?sort=interest&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.attending ? '&attending='.concat(param.attending) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'interest' && param.direction == 'asc' ? 'active' : ''}">
                <spring:message code="event.sort.interest"/>
              </a>
            </div>
          </div>
          <a href="<c:url value="/events/create"/>" class="btn btn-primary btn-with-icon">
            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
            <spring:message code="event.create.button"/>
          </a>
        </div>
      </div>

      <!-- Event Tabs -->
      <div class="event-tabs">
        <ul class="tabs-list">
          <li class="tab-item ${empty param.isUpcoming && empty param.attending && empty param.isPast ? 'active' : ''}">
            <a href="<c:url value="/events?${not empty param.search ? 'search='.concat(param.search).concat('&') : ''}${not empty param.destination ? 'destination='.concat(param.destination).concat('&') : ''}${not empty param.cityName ? 'cityName='.concat(param.cityName).concat('&') : ''}${not empty param.startDate ? 'startDate='.concat(param.startDate).concat('&') : ''}${not empty param.endDate ? 'endDate='.concat(param.endDate).concat('&') : ''}${not empty param.interests ? 'interests='.concat(param.interests).concat('&') : ''}${not empty param.interestName ? 'interestName='.concat(param.interestName).concat('&') : ''}${not empty param.sort ? 'sort='.concat(param.sort).concat('&') : ''}${not empty param.direction ? 'direction='.concat(param.direction).concat('&') : ''}page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.all"/>
            </a>
          </li>
          <li class="tab-item ${not empty param.isUpcoming ? 'active' : ''}">
            <a href="<c:url value="/events?isUpcoming=true${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? 'interestName='.concat(param.interestName) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}${not empty param.direction ? '&direction='.concat(param.direction) : ''}&page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.upcoming"/>
            </a>
          </li>
          <c:if test="${ not empty user }">
          <li class="tab-item ${not empty param.attending ? 'active' : ''}">
            <a href="<c:url value="/events?attending=true${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? 'interestName='.concat(param.interestName) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}${not empty param.direction ? '&direction='.concat(param.direction) : ''}&page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.attending"/>
            </a>
          </li>
          </c:if>
          <li class="tab-item ${not empty param.isPast ? 'active' : ''}">
            <a href="<c:url value="/events?isPast=true${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.cityName ? '&cityName='.concat(param.cityName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? 'interestName='.concat(param.interestName) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}${not empty param.direction ? '&direction='.concat(param.direction) : ''}&page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.past"/>
            </a>
          </li>
        </ul>
      </div>

      <!-- Filter Section - Initially Hidden -->
      <div id="filterSection" class="filter-section hidden">
        <h3 class="filter-title">
          <spring:message code="event.filter.title"/>
        </h3>
        <c:set var="actionGet"><c:url value="/events"/></c:set>
        <form:form action="${actionGet}" method="GET"
                   modelAttribute="filterEventForm"
                   class="filter-form" id="eventFilterForm">

          <div class="filter-grid">
            <!-- City filter with autocomplete -->
            <div class="filter-item">
              <c:set var="cityLabel"><spring:message code="createJourney.city"/></c:set>
              <form:label for="citySearch" class="form-label" path="destination">${cityLabel}</form:label>
              <div class="autocomplete-wrapper">
                <c:set var="cityNamePlaceholder"><spring:message code="event.filter.city.placeholder"/></c:set>
                <form:input path="destination" type="text" id="citySearch" class="autocomplete-input"
                       placeholder="${cityNamePlaceholder}"
                       value="${param.cityName}" />
                <select id="city"  class="hidden-select"  style="display: none;">
                  <option value=""></option>
                  <c:forEach var="city" items="${cities}">
                    <option value="${city.id}" ${param.destination == city.id ? 'selected' : ''}><c:out value="${city.name}"/></option>
                  </c:forEach>
                </select>
                <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
                  <c:forEach var="city" items="${cities}">
                    <div class="autocomplete-item" data-value="${city.id}"><c:out value="${city.name}"/></div>
                  </c:forEach>
                </div>
                <div id="citySelectedContainer" class="selected-tags"></div>
              </div>
              <form:errors path="destination" cssClass="error-message" />
            </div>

            <div class="filter-item">
              <c:set var="afterDateFilter"><spring:message code="event.filter.afterDate"/></c:set>
              <jsp:include page="../components/date-field.jsp">
                <jsp:param name="path" value="startDate"/>
                <jsp:param name="label" value="${afterDateFilter}"/>
              </jsp:include>
            </div>

            <div class="filter-item">
              <c:set var="beforeDateFilter"><spring:message code="event.filter.beforeDate"/></c:set>
              <jsp:include page="../components/date-field.jsp">
                <jsp:param name="path" value="endDate"/>
                <jsp:param name="label" value="${beforeDateFilter}"/>
              </jsp:include>
              <form:errors path="" cssClass="error-message" />
            </div>

            <!-- Interest filter with autocomplete -->
            <div class="filter-item">
              <c:set var="interestsLabel"><spring:message code="event.filter.interest"/></c:set>
              <form:label for="interest-search" class="form-label" path="interests">${interestsLabel}</form:label>
              <div class="autocomplete-wrapper">
                <c:set var="interestNamePlaceholder"><spring:message code="event.filter.interest.placeholder"/></c:set>
                <form:input path="interests" type="text" id="interest-search" class="autocomplete-input"
                       placeholder="${interestNamePlaceholder}"
                       value="${param.interestName}" />
                <select  id="interest-select" class="hidden-select" style="display: none;">
                  <option value=""></option>
                  <c:forEach var="interest" items="${interests}">
                    <option value="${interest.id}" ${param.interests == interest.id ? 'selected' : ''}><c:out value="${interest.name}"/></option>
                  </c:forEach>
                </select>
                <div id="interest-dropdown" class="autocomplete-dropdown" style="display: none;">
                  <c:forEach var="interest" items="${interests}">
                    <div class="autocomplete-item" data-value="${interest.id}"><c:out value="${interest.name}"/></div>
                  </c:forEach>
                </div>
                <div id="interestSelectedContainer" class="selected-tags"></div>
              </div>
              <form:errors path="interests" cssClass="error-message" />
            </div>
          </div>

          <!-- Preserve search parameter -->
          <c:if test="${not empty param.search}">
            <input type="hidden" name="search" value="<c:out value="${param.search}"/>">
          </c:if>

          <!-- Preserve sort parameters -->
          <c:if test="${not empty param.sort}">
            <input type="hidden" name="sort" value="<c:out value="${param.sort}"/>">
          </c:if>
          <c:if test="${not empty param.direction}">
            <input type="hidden" name="direction" value="<c:out value="${param.direction}"/>">
          </c:if>

          <!-- Preserve tab parameters -->
          <c:if test="${not empty param.isUpcoming}">
            <form:hidden path="isUpcoming" value="${param.isUpcoming}" />
          </c:if>
          <c:if test="${not empty param.attending}">
            <form:hidden path="attending" value="${param.attending}" />
          </c:if>
          <c:if test="${not empty param.isPast}">
            <form:hidden path="isPast" value="${param.isPast}" />
          </c:if>

          <!-- Preserve pagination parameters -->
          <input type="hidden" name="page" value="1">
          <c:if test="${not empty param.pageSize}">
            <input type="hidden" name="pageSize" value="<c:out value="${param.pageSize}"/>">
          </c:if>

          <div class="filter-actions">
            <button type="button" id="resetFiltersBtn" class="btn-danger btn-with-icon">
              <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="event.filter.reset"/>" class="btn-icon" />
              <spring:message code="event.filter.reset"/>
            </button>
            <button type="submit" class="btn-secondary btn-with-icon">
              <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="event.filter.button"/>" class="btn-icon" />
              <spring:message code="event.filter.button"/>
            </button>
          </div>
        </form:form>
      </div>

      <!-- Events Grid -->
      <div class="events-container">
        <div class="events-grid">
          <c:if test="${empty eventsWithAttendance}">
            <div class="empty-state">
              <p class="empty-message"><spring:message code="event.no.events"/></p>
            </div>
          </c:if>
          <c:forEach items="${eventsWithAttendance}" var="event">
            <jsp:include page="event-card.jsp">
              <jsp:param name="eventId" value="${event.id}" />
              <jsp:param name="city" value="${event.eventCity.name}" />
              <jsp:param name="date" value="${event.date}" />
              <jsp:param name="username" value="${event.user.username}"/>
              <jsp:param name="description" value="${event.description}" />
              <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
              <jsp:param name="firstname" value="${event.user.firstname}" />
              <jsp:param name="lastname" value="${event.user.lastname}"/>
              <jsp:param name="title" value="${event.title}"/>
              <jsp:param name="isFull" value="${event.attendeesLimit.isPresent() && event.attendeesLimit.get() <= event.attendeesCount}"/>
            </jsp:include>
          </c:forEach>
        </div>
        <!-- Construct baseUrl with all query parameters -->
        <c:set var="paginationBaseUrl" value="/events?" />
        <c:if test="${not empty param.search}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}search=${param.search}&" />
        </c:if>
        <c:if test="${not empty param.destination}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}destination=${param.destination}&" />
        </c:if>
        <c:if test="${not empty param.destinationName}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}destinationName=${param.destinationName}&" />
        </c:if>
        <c:if test="${not empty param.startDate}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}startDate=${param.startDate}&" />
        </c:if>
        <c:if test="${not empty param.endDate}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}endDate=${param.endDate}&" />
        </c:if>
        <c:if test="${not empty param.interests}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}interests=${param.interests}&" />
        </c:if>
        <c:if test="${not empty param.interestName}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}interestName=${param.interestName}&" />
        </c:if>
        <c:if test="${not empty param.sort}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}sort=${param.sort}&" />
        </c:if>
        <c:if test="${not empty param.direction}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}direction=${param.direction}&" />
        </c:if>
        <c:if test="${not empty param.isMyDestination}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isMyDestination=${param.isMyDestination}&" />
        </c:if>
        <c:if test="${not empty param.isUpcoming}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isUpcoming=${param.isUpcoming}&" />
        </c:if>
        <c:if test="${not empty param.isPast}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isPast=${param.isPast}&" />
        </c:if>
<%--        <c:if test="${not empty param.pageSize}">--%>
<%--          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}pageSize=${param.pageSize}&" />--%>
<%--        </c:if>--%>

        <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
          <jsp:param name="pageObjectTotalPages" value="${eventsPage.totalPages}" />
          <jsp:param name="currentPage" value="${currentPage}" />
          <jsp:param name="pageSize" value="${pageSize}" />
          <jsp:param name="baseUrl" value="${paginationBaseUrl}" />
        </jsp:include>

      </div>
    </div>
  </div>
</div>
<script>
  window.apiBaseUrl = '<c:url value="/" />';
  window.eventBaseUrl = '<c:url value="/events"/>';
  window.closeImage = '<c:url value="/resources/icons/x.svg"/>';
  eventSelectedInterests = '<c:out value="${filterEventForm.interests}"/>';
  eventSelectedCity = '<c:out value="${filterEventForm.destination}"/>';
</script>
<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/event-list.js'/>"></script>

</body>
</html>