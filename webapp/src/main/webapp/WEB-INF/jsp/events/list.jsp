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
<c:set var="startDate"><c:out value="${param.startDate}"/></c:set>
<c:set var="endDate"><c:out value="${param.endDate}"/></c:set>
<c:set var="search"><c:out value="${param.search}"/></c:set>
<c:set var="destination"><c:out value="${param.destination}"/></c:set>
<c:set var="cityName"><c:out value="${param.cityName}"/></c:set>
<c:set var="escapedInterests"><c:out value="${param.interests}"/></c:set>
<c:set var="pageSize"><c:out value="${param.pageSize}"/></c:set>
<c:set var="escapedSort"><c:out value="${param.sort}"/></c:set>
<c:set var="escapedDirection"><c:out value="${param.direction}"/></c:set>
<c:set var="escapedInterest"><c:out value="${param.interestName}"/></c:set>
<c:set var="escapedUpcoming"><c:out value="${param.isUpcoming}"/></c:set>
<c:set var="escapedAttending"><c:out value="${param.attending}"/></c:set>
<c:set var="escapedPast"><c:out value="${param.isPast}"/></c:set>
<c:set var="escapedDestination"><c:out value="${param.isMyDestination}"/></c:set>

<c:choose>
  <c:when test="${escapedSort == 'rating' and (escapedUpcoming eq 'true' or escapedAttending eq 'true')}">
    <c:set var="actualSort" value="date" />
  </c:when>
  <c:otherwise>
    <c:set var="actualSort" value="${escapedSort}" />
  </c:otherwise>
</c:choose>

<div class="layout-container">

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
                   value="${search}"/>
            <input type="hidden" name="page" value="1">
            <input type="hidden" name="pageSize" value="${pageSize != null ? pageSize : 4}">

            <c:if test="${not empty actualSort}">
              <input type="hidden" name="sort" value="<c:out value="${actualSort}"/>">
            </c:if>

            <c:if test="${not empty escapedDirection}">
              <input type="hidden" name="direction" value="<c:out value="${escapedDirection}"/>">
            </c:if>


            <c:if test="${not empty destination}">
              <input type="hidden" name="destination" value="<c:out value="${destination}"/>">
            </c:if>
            <c:if test="${not empty cityName}">
              <input type="hidden" name="cityName" value="<c:out value="${cityName}"/>">
            </c:if>
            <c:if test="${not empty startDate}">
              <input type="hidden" name="startDate" value="<c:out value="${startDate}"/>">
            </c:if>
            <c:if test="${not empty endDate}">
              <input type="hidden" name="endDate" value="<c:out value="${endDate}"/>">
            </c:if>
            <c:if test="${not empty escapedInterests}">
              <input type="hidden" name="interests" value="<c:out value="${escapedInterests}"/>">
            </c:if>
            <c:if test="${not empty interestName}">
              <input type="hidden" name="interestName" value="<c:out value="${interestName}"/>">
            </c:if>


            <c:if test="${not empty escapedUpcoming}">
              <input type="hidden" name="isUpcoming" value="<c:out value="${escapedUpcoming}"/>">
            </c:if>
            <c:if test="${not empty escapedAttending}">
              <input type="hidden" name="attending" value="<c:out value="${escapedAttending}"/>">
            </c:if>
            <c:if test="${not empty escapedPast}">
              <input type="hidden" name="isPast" value="<c:out value="${escapedPast}"/>">
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
              <a href="<c:url value="/events?sort=date&direction=asc${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? '&interestName='.concat(interestName) : ''}${not empty escapedUpcoming ? '&isUpcoming='.concat(escapedUpcoming) : ''}${not empty escapedAttending ? '&attending='.concat(escapedAttending) : ''}${not empty escapedPast ? '&isPast='.concat(escapedPast) : ''}"/>"
                 class="${empty actualSort or (
          actualSort != 'date' and
          actualSort != 'attendees'
          and ( (escapedUpcoming != true and escapedAttending != true) and actualSort != 'rating' )
        ) or (actualSort == 'date' and (empty escapedDirection or escapedDirection != "desc")) ? 'active' : ''}">
                <spring:message code="event.sort.date.asc"/>
              </a>
              <a href="<c:url value="/events?sort=date&direction=desc${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? '&interestName='.concat(interestName) : ''}${not empty escapedUpcoming ? '&isUpcoming='.concat(escapedUpcoming) : ''}${not empty escapedAttending ? '&attending='.concat(escapedAttending) : ''}${not empty escapedPast ? '&isPast='.concat(escapedPast) : ''}"/>"
                 class="${actualSort == 'date' && escapedDirection == 'desc' ? 'active' : ''}">
                <spring:message code="event.sort.date.desc"/>
              </a>

              <a href="<c:url value="/events?sort=attendees&direction=asc${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? '&interestName='.concat(interestName) : ''}${not empty escapedUpcoming ? '&isUpcoming='.concat(escapedUpcoming) : ''}${not empty escapedAttending ? '&attending='.concat(escapedAttending) : ''}${not empty escapedPast ? '&isPast='.concat(escapedPast) : ''}"/>"
                 class="${actualSort == 'attendees' and (empty escapedDirection or escapedDirection != 'desc') ? 'active' : ''}">
                <spring:message code="event.sort.attendees.asc"/>
              </a>

              <a href="<c:url value="/events?sort=attendees&direction=desc${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? '&interestName='.concat(interestName) : ''}${not empty escapedUpcoming ? '&isUpcoming='.concat(escapedUpcoming) : ''}${not empty escapedAttending ? '&attending='.concat(escapedAttending) : ''}${not empty escapedPast ? '&isPast='.concat(escapedPast) : ''}"/>"
                 class="${actualSort == 'attendees' && escapedDirection == 'desc' ? 'active' : ''}">
                <spring:message code="event.sort.attendees"/>
              </a>


              <c:if test="${escapedUpcoming ne 'true' and escapedAttending ne 'true'}">
                <a href="<c:url value="/events?sort=rating&direction=asc${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? '&interestName='.concat(interestName) : ''}${not empty escapedUpcoming ? '&isUpcoming='.concat(escapedUpcoming) : ''}${not empty escapedAttending ? '&attending='.concat(escapedAttending) : ''}${not empty escapedPast ? '&isPast='.concat(escapedPast) : ''}"/>"
                   class="${actualSort == 'rating' and (empty escapedDirection or escapedDirection != 'desc') ? 'active' : ''}">
                  <spring:message code="event.sort.rating.asc"/>
                </a>

                <a href="<c:url value="/events?sort=rating&direction=desc${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? '&interestName='.concat(interestName) : ''}${not empty escapedUpcoming ? '&isUpcoming='.concat(escapedUpcoming) : ''}${not empty escapedAttending ? '&attending='.concat(escapedAttending) : ''}${not empty escapedPast ? '&isPast='.concat(escapedPast) : ''}"/>"
                   class="${actualSort == 'rating' && escapedDirection == 'desc' ? 'active' : ''}">
                  <spring:message code="event.sort.rating"/>
                </a>
              </c:if>


            </div>

          </div>
          <a href="<c:url value="/events/create"/>" class="btn btn-primary btn-with-icon">
            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
            <spring:message code="event.create.button"/>
          </a>
        </div>
      </div>


      <div class="event-tabs">
        <ul class="tabs-list">
          <li class="tab-item ${empty escapedUpcoming && empty escapedAttending && empty escapedPast ? 'active' : ''}">
            <a href="<c:url value="/events?${not empty search ? 'search='.concat(search).concat('&') : ''}${not empty destination ? 'destination='.concat(destination).concat('&') : ''}${not empty cityName ? 'cityName='.concat(cityName).concat('&') : ''}${not empty startDate ? 'startDate='.concat(startDate).concat('&') : ''}${not empty endDate ? 'endDate='.concat(endDate).concat('&') : ''}${not empty escapedInterests ? 'interests='.concat(escapedInterests).concat('&') : ''}${not empty interestName ? 'interestName='.concat(interestName).concat('&') : ''}${not empty actualSort ? 'sort='.concat(actualSort).concat('&') : ''}${not empty escapedDirection ? 'direction='.concat(escapedDirection).concat('&') : ''}page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.all"/>
            </a>
          </li>
          <li class="tab-item ${not empty escapedUpcoming ? 'active' : ''}">
            <a href="<c:url value="/events?isUpcoming=true${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? 'interestName='.concat(interestName) : ''}${actualSort == 'rating' ? '&sort=date' : (not empty actualSort ? '&sort='.concat(actualSort) : '')}${not empty escapedDirection ? '&direction='.concat(escapedDirection) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.upcoming"/>
            </a>
          </li>
          <c:if test="${ not empty user }">
          <li class="tab-item ${not empty escapedAttending ? 'active' : ''}">
            <a href="<c:url value="/events?attending=true${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? 'interestName='.concat(interestName) : ''}${actualSort == 'rating' ? '&sort=date' : (not empty actualSort ? '&sort='.concat(actualSort) : '')}${not empty escapedDirection ? '&direction='.concat(escapedDirection) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.attending"/>
            </a>
          </li>
          </c:if>
          <li class="tab-item ${not empty escapedPast ? 'active' : ''}">
            <a href="<c:url value="/events?isPast=true${not empty search ? '&search='.concat(search) : ''}${not empty destination ? '&destination='.concat(destination) : ''}${not empty cityName ? '&cityName='.concat(cityName) : ''}${not empty startDate ? '&startDate='.concat(startDate) : ''}${not empty endDate ? '&endDate='.concat(endDate) : ''}${not empty escapedInterests ? '&interests='.concat(escapedInterests) : ''}${not empty interestName ? 'interestName='.concat(interestName) : ''}${not empty actualSort ? '&sort='.concat(actualSort) : ''}${not empty escapedDirection ? '&direction='.concat(escapedDirection) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
              <spring:message code="event.tabs.past"/>
            </a>
          </li>
        </ul>
      </div>


      <div id="filterSection" class="filter-section hidden">
        <h3 class="filter-title">
          <spring:message code="event.filter.title"/>
        </h3>
        <c:set var="actionGet"><c:url value="/events"/></c:set>
        <form:form action="${actionGet}" method="GET"
                   modelAttribute="filterEventForm"
                   class="filter-form" id="eventFilterForm">

          <div class="filter-grid">

            <div class="filter-item">
              <c:set var="cityLabel"><spring:message code="createJourney.city"/></c:set>
              <form:label for="citySearch" class="form-label" path="destination">${cityLabel}</form:label>
              <div class="autocomplete-wrapper">
                <c:set var="cityNamePlaceholder"><spring:message code="event.filter.city.placeholder"/></c:set>
                <form:input path="destination" type="text" id="citySearch" class="autocomplete-input"
                       placeholder="${cityNamePlaceholder}"
                       value="${cityName}" />
                <select id="city"  class="hidden-select"  style="display: none;">
                  <option value=""></option>
                  <c:forEach var="city" items="${cities}">
                    <option value="${city.id}" ${destination == city.id ? 'selected' : ''}><c:out value="${city.name}"/></option>
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


            <div class="filter-item">
              <c:set var="interestsLabel"><spring:message code="event.filter.interest"/></c:set>
              <form:label for="interest-search" class="form-label" path="interests">${interestsLabel}</form:label>
              <div class="autocomplete-wrapper">
                <c:set var="interestNamePlaceholder"><spring:message code="event.filter.interest.placeholder"/></c:set>
                <form:input path="interests" type="text" id="interest-search" class="autocomplete-input"
                       placeholder="${interestNamePlaceholder}"
                       value="${interestName}" />
                <select  id="interest-select" class="hidden-select" style="display: none;">
                  <option value=""></option>
                  <c:forEach var="interest" items="${interests}">
                    <option value="${interest.id}" ${escapedInterests == interest.id ? 'selected' : ''}><c:out value="${interest.name}"/></option>
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


          <c:if test="${not empty search}">
            <input type="hidden" name="search" value="${search}"/>
          </c:if>


          <c:if test="${not empty actualSort}">
            <input type="hidden" name="sort" value="<c:out value="${actualSort}"/>">
          </c:if>
          <c:if test="${not empty escapedDirection}">
            <input type="hidden" name="direction" value="<c:out value="${escapedDirection}"/>">
          </c:if>


          <c:if test="${not empty escapedUpcoming}">
            <form:hidden path="isUpcoming" value="${escapedUpcoming}" />
          </c:if>
          <c:if test="${not empty escapedAttending}">
            <form:hidden path="attending" value="${escapedAttending}" />
          </c:if>
          <c:if test="${not empty escapedPast}">
            <form:hidden path="isPast" value="${escapedPast}" />
          </c:if>


          <input type="hidden" name="page" value="1">
          <c:if test="${not empty pageSize}">
            <input type="hidden" name="pageSize" value="<c:out value="${pageSize}"/>">
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
      <c:set var="eventsWithAttendance" value="${eventsPage.content}"/>

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
              <jsp:param name="city" value="${event.city.name}" />
              <jsp:param name="date" value="${event.date}" />
              <jsp:param name="username" value="${event.user.username}"/>
              <jsp:param name="description" value="${event.description}" />
              <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
              <jsp:param name="firstname" value="${event.user.firstname}" />
              <jsp:param name="lastname" value="${event.user.lastname}"/>
              <jsp:param name="title" value="${event.title}"/>
              <jsp:param name="isFull" value="${event.attendeesLimit != null && event.attendeesLimit <= event.attendeesCount}"/>
            </jsp:include>
          </c:forEach>
        </div>

        <c:set var="paginationBaseUrl" value="/events?" />
        <c:if test="${not empty search}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}search=${search}&" />
        </c:if>
        <c:if test="${not empty destination}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}destination=${destination}&" />
        </c:if>
        <c:if test="${not empty destinationName}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}destinationName=${destinationName}&" />
        </c:if>
        <c:if test="${not empty startDate}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}startDate=${startDate}&" />
        </c:if>
        <c:if test="${not empty endDate}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}endDate=${endDate}&" />
        </c:if>
        <c:if test="${not empty escapedInterests}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}interests=${escapedInterests}&" />
        </c:if>
        <c:if test="${not empty interestName}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}interestName=${interestName}&" />
        </c:if>
        <c:if test="${not empty actualSort}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}sort=${actualSort}&" />
        </c:if>
        <c:if test="${not empty escapedDirection}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}direction=${escapedDirection}&" />
        </c:if>
        <c:if test="${not empty escapedDestination}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isMyDestination=${escapedDestination}&" />
        </c:if>
        <c:if test="${not empty escapedUpcoming}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isUpcoming=${escapedUpcoming}&" />
        </c:if>
        <c:if test="${not empty escapedPast}">
          <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isPast=${escapedPast}&" />
        </c:if>
          <c:if test="${not empty escapedAttending}">
              <c:set var="paginationBaseUrl" value="${paginationBaseUrl}attending=${escapedAttending}&" />
          </c:if>





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
  function htmlDecode(input) {
    const doc = new DOMParser().parseFromString(input, "text/html");
    return doc.documentElement.textContent;
  }
  window.apiBaseUrl = '<c:url value="/" />';
  window.eventBaseUrl = '<c:url value="/events"/>';
  window.closeImage = '<c:url value="/resources/icons/x.svg"/>';
  eventSelectedInterests = htmlDecode('<c:out value="${filterEventForm.interests}"/>');
  eventSelectedCity = htmlDecode('<c:out value="${filterEventForm.destination}"/>');
</script>

<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/events/event-list.js'/>"></script>

</body>
</html>