<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="journey.page.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
</head>
<body>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>
<c:set var="searchUrl" value="/journeys" scope="request" />
<c:set var="searchPlaceholderCode" value="journeys.search.journey" scope="request" />
<c:set var="escapedInterest"><c:out value="${param.interests}"/></c:set>
<c:set var="escapedCity"><c:out value="${param.city}"/></c:set>
<c:set var="escapedStartDate"><c:out value="${param.startDate}"/></c:set>
<c:set var="escapedEndDate"><c:out value="${param.endDate}"/></c:set>
    <c:set var="escapedSearch"><c:out value="${param.search}"/></c:set>
    <c:set var="isMyDestination"><c:out value="${param.isMyDestination}"/></c:set>
    <c:set var="isUpcoming"><c:out value="${param.isUpcoming}"/></c:set>
    <c:set var="isOngoing"><c:out value="${param.isOngoing}"/></c:set>
    <c:set var="isPast"><c:out value="${param.isPast}"/></c:set>
    <c:set var="direction"><c:out value="${param.direction}"/></c:set>
    <c:set var="sort"><c:out value="${param.sort}"/></c:set>
    <c:set var="pageSize"><c:out value="${param.pageSize}"/></c:set>

<div class="layout-container">

    <div class="main-content">
        <jsp:include page="../components/navbar.jsp" />
        <div class="content-container">
            <div class="header-container">
                <h2 class="page-title">
                    <spring:message code="journey.list.title"/>
                </h2>
                <div class="journeys-actions">
                    <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
                        <input type="text" name="search" class="search-input"
                               placeholder="<spring:message code='${searchPlaceholderCode}' />"
                               value="${escapedSearch}"/>
                        <input type="hidden" name="page" value="1">
                        <input type="hidden" name="pageSize" value="${pageSize != null ? pageSize : 10}">


                        <c:if test="${not empty sort}">
                            <input type="hidden" name="sort" value="<c:out value="${sort}"/>">
                        </c:if>
                        <c:if test="${not empty direction}">
                            <input type="hidden" name="direction" value="<c:out value="${direction}"/>">
                        </c:if>


                        <c:if test="${not empty escapedCity}">
                            <input type="hidden" name="destination" value="<c:out value="${escapedCity}"/>">
                        </c:if>
                        <c:if test="${not empty escapedStartDate}">
                            <input type="hidden" name="startDate" value="<c:out value="${escapedStartDate}"/>">
                        </c:if>
                        <c:if test="${not empty escapedEndDate}">
                            <input type="hidden" name="endDate" value="<c:out value="${escapedEndDate}"/>">
                        </c:if>
                        <c:if test="${not empty escapedInterest}">
                            <input type="hidden" name="interests" value="<c:out value="${escapedInterest}"/>">
                        </c:if>


                        <c:if test="${not empty isMyDestination}">
                            <input type="hidden" name="isMyDestination" value="<c:out value="${isMyDestination}"/>">
                        </c:if>
                        <c:if test="${not empty isUpcoming}">
                            <input type="hidden" name="isUpcoming" value="<c:out value="${isUpcoming}"/>">
                        </c:if>
                        <c:if test="${not empty isPast}">
                            <input type="hidden" name="isPast" value="<c:out value="${isPast}"/>">
                        </c:if>
                        <c:if test="${not empty isOngoing}">
                            <input type="hidden" name="isOngoing" value="<c:out value="${isOngoing}"/>">
                        </c:if>

                        <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
                            <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
                        </button>
                    </form>
                    <button id="filterToggleBtn" class="btn-secondary btn-with-icon">
                        <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="journey.filter.toggle"/>" class="btn-icon filter-icon" />
                        <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="journey.filter.close"/>" class="btn-icon close-icon" style="display: none;" />
                        <spring:message code="journey.filter.toggle"/>
                    </button>
                    <div class="sort-dropdown">
                        <button id="sortToggleBtn" class="btn-secondary btn-with-icon">
                            <img src="<c:url value='/resources/icons/sort.svg'/>" alt="<spring:message code="journey.sort.toggle"/>" class="btn-icon" />
                            <spring:message code="journey.sort.toggle"/>
                        </button>
                        <div id="sortDropdown" class="dropdown-content" style="display: none;">
                            <a href="<c:url value="/journeys?sort=start_date&direction=asc${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty isMyDestination ? '&isMyDestination='.concat(isMyDestination) : ''}${not empty isUpcoming ? '&isUpcoming='.concat(isUpcoming) : ''}${not empty isPast ? '&isPast='.concat(isPast) : ''}${not empty isOngoing ? '&isOngoing='.concat(isOngoing) : ''}"/>"
                               class="${
  (empty sort or
   (sort != 'start_date' and sort != 'end_date') or
   (sort == 'start_date' and (empty direction or direction != 'desc'))
  ) ? 'active' : ''}">

                            <spring:message code="journey.sort.startDate.asc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=start_date&direction=desc${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty isMyDestination ? '&isMyDestination='.concat(isMyDestination) : ''}${not empty isUpcoming ? '&isUpcoming='.concat(isUpcoming) : ''}${not empty isPast ? '&isPast='.concat(isPast) : ''}${not empty isOngoing ? '&isOngoing='.concat(isOngoing) : ''}"/>" class="${sort == 'start_date' && direction == 'desc' ? 'active' : ''}">
                                <spring:message code="journey.sort.startDate.desc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=end_date&direction=asc${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty isMyDestination ? '&isMyDestination='.concat(isMyDestination) : ''}${not empty isUpcoming ? '&isUpcoming='.concat(isUpcoming) : ''}${not empty isPast ? '&isPast='.concat(isPast) : ''}${not empty isOngoing ? '&isOngoing='.concat(isOngoing) : ''}"/>"
                               class="${sort == 'end_date' and (empty direction or direction != 'desc') ? 'active' : ''}">
                                <spring:message code="journey.sort.endDate.asc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=end_date&direction=desc${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty isMyDestination ? '&isMyDestination='.concat(isMyDestination) : ''}${not empty isUpcoming ? '&isUpcoming='.concat(isUpcoming) : ''}${not empty isPast ? '&isPast='.concat(isPast) : ''}${not empty isOngoing ? '&isOngoing='.concat(isOngoing) : ''}"/>" class="${sort == 'end_date' && direction == 'desc' ? 'active' : ''}">
                                <spring:message code="journey.sort.endDate.desc"/>
                            </a>

                        </div>
                    </div>
                    <c:if test="${hasJourney == false}">
                        <a href="<c:url value="/journeys/create"/>" class="btn btn-primary btn-with-icon">
                            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
                            <spring:message code="journey.create.button"/>
                        </a>
                    </c:if>
                </div>
            </div>


            <div class="journey-tabs">
                <ul class="tabs-list">
                    <li class="tab-item ${empty isMyDestination && empty isUpcoming && empty isPast && empty isOngoing ? 'active' : ''}">
                        <a href="<c:url value="/journeys?${not empty escapedSearch ? 'search='.concat(escapedSearch).concat('&') : ''}${not empty escapedCity ? 'destination='.concat(escapedCity).concat('&') : ''}${not empty escapedCityName ? 'destinationName='.concat(escapedCityName).concat('&') : ''}${not empty escapedStartDate ? 'startDate='.concat(escapedStartDate).concat('&') : ''}${not empty escapedEndDate ? 'endDate='.concat(escapedEndDate).concat('&') : ''}${not empty escapedInterest ? 'interests='.concat(escapedInterest).concat('&') : ''}${not empty sort ? 'sort='.concat(sort).concat('&') : ''}${not empty direction ? 'direction='.concat(direction).concat('&') : ''}page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.all"/>
                        </a>
                    </li>
                    <c:if test="${hasJourney}">
                    <li class="tab-item ${not empty isMyDestination ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isMyDestination=true${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty sort ? '&sort='.concat(sort) : ''}${not empty direction ? '&direction='.concat(direction) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.myDestination"/>
                        </a>
                    </li>
                    </c:if>
                    <li class="tab-item ${not empty isOngoing ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isOngoing=true${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty sort ? '&sort='.concat(sort) : ''}${not empty direction ? '&direction='.concat(direction) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.ongoing"/>
                        </a>
                    </li>
                    <li class="tab-item ${not empty isUpcoming ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isUpcoming=true${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty sort ? '&sort='.concat(sort) : ''}${not empty direction ? '&direction='.concat(direction) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.upcoming"/>
                        </a>
                    </li>
                    <li class="tab-item ${not empty isPast ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isPast=true${not empty escapedSearch ? '&search='.concat(escapedSearch) : ''}${not empty escapedCity ? '&destination='.concat(escapedCity) : ''}${not empty escapedCityName ? '&destinationName='.concat(escapedCityName) : ''}${not empty escapedStartDate ? '&startDate='.concat(escapedStartDate) : ''}${not empty escapedEndDate ? '&endDate='.concat(escapedEndDate) : ''}${not empty escapedInterest ? '&interests='.concat(escapedInterest) : ''}${not empty sort ? '&sort='.concat(sort) : ''}${not empty direction ? '&direction='.concat(direction) : ''}&page=1${not empty pageSize ? '&pageSize='.concat(pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.past"/>
                        </a>
                    </li>
                </ul>
            </div>


            <div id="filterSection" class="filter-section hidden">
                <h3 class="filter-title">
                    <spring:message code="journey.filter.title"/>
                </h3>
                <c:set var="actionGet"><c:url value="/journeys"/></c:set>
                <form:form action="${actionGet}" method="GET"
                           modelAttribute="filterJourneyForm"
                           class="filter-form" id="journeyFilterForm">

                    <div class="filter-grid">

                        <div class="filter-item">
                            <c:set var="destinationLabel"><spring:message code="createJourney.destinationCity"/></c:set>
                            <form:label for="citySearch" class="form-label" path="destination">${destinationLabel}</form:label>
                            <div class="autocomplete-wrapper">
                                <c:set var="citySearch"><spring:message code='journey.filter.destination.placeholder'/></c:set>
                                <form:input path="destination" type="text" id="citySearch" class="autocomplete-input"
                                       placeholder="${citySearch}"
                                       value="${escapedCity}" />
                                <select id="city" class="hidden-select" style="display: none;">
                                    <option value=""></option>
                                    <c:forEach var="city" items="${cities}">
                                        <option value="<c:out value="${city.name}"/>" ${escapedCity == city.id ? 'selected' : ''}><c:out value="${city.name}"/></option>
                                    </c:forEach>
                                </select>
                                <div id="cityDropdown" class="autocomplete-dropdown" style="display: none;">
                                    <c:forEach var="city" items="${cities}">
                                        <div class="autocomplete-item" data-value="<c:out value="${city.name}"/>"><c:out value="${city.name}"/></div>
                                    </c:forEach>
                                </div>
                                <div id="citySelectedContainer" class="selected-tags"></div>
                            </div>
                            <form:errors path="destination" cssClass="error-message" />
                        </div>

                        <div class="filter-item">
                            <c:set var="startDateFilter"><spring:message code="journey.filter.startDate"/></c:set>
                            <jsp:include page="../components/date-field.jsp">
                                <jsp:param name="path" value="startDate"/>
                                <jsp:param name="label" value="${startDateFilter}"/>
                            </jsp:include>
                        </div>

                        <div class="filter-item">
                            <c:set var="endDateFilter"><spring:message code="journey.filter.endDate"/></c:set>
                            <jsp:include page="../components/date-field.jsp">
                                <jsp:param name="path" value="endDate"/>
                                <jsp:param name="label" value="${endDateFilter}"/>
                            </jsp:include>
                            <form:errors path="" cssClass="error-message" />
                        </div>


                        <div class="filter-item">
                            <c:set var="interestsLabel"><spring:message code="journey.filter.interest"/></c:set>
                            <form:label for="interest-search" class="form-label" path="interests">${interestsLabel}</form:label>
                            <div class="autocomplete-wrapper">
                                <select  id="interest-select" class="hidden-select" style="display: none;">
                                    <option value=""></option>
                                    <c:forEach var="interest" items="${interests}">
                                        <option value="<c:out value=" ${interest.name}"/>"><c:out value="${interest.name}"/></option>
                                    </c:forEach>
                                </select>
                                <c:set var="interestSearch"><spring:message code='journey.filter.interest.placeholder'/></c:set>
                                <form:input path="interests" type="text" id="interest-search" class="autocomplete-input"
                                       placeholder="${interestSearch}"
                                       value="${escapedInterest}" />
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


                    <c:if test="${not empty escapedSearch}">
                        <input type="hidden" name="search" value="${escapedSearch}"/>
                    </c:if>


                    <c:if test="${not empty sort}">
                        <input type="hidden" name="sort" value="<c:out value="${sort}"/>">
                    </c:if>
                    <c:if test="${not empty direction}">
                        <input type="hidden" name="direction" value="<c:out value="${direction}"/>">
                    </c:if>


                    <c:if test="${not empty isMyDestination}">
                        <form:hidden path="isMyDestination" value="${isMyDestination}" />
                    </c:if>
                    <c:if test="${not empty isUpcoming}">
                        <form:hidden path="isUpcoming" value="${isUpcoming}" />
                    </c:if>
                    <c:if test="${not empty isOngoing}">
                        <form:hidden path="isOngoing" value="${isOngoing}" />
                    </c:if>
                    <c:if test="${not empty isPast}">
                        <form:hidden path="isPast" value="${isPast}" />
                    </c:if>


                    <input type="hidden" name="page" value="1">
                    <c:if test="${not empty pageSize}">
                        <input type="hidden" name="pageSize" value="<c:out value="${pageSize}"/>">
                    </c:if>

                    <div class="filter-actions">
                        <button type="button" id="resetFiltersBtn" class="btn-danger btn-with-icon">
                            <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="journey.filter.reset"/>" class="btn-icon" />
                            <spring:message code="journey.filter.reset"/>
                        </button>
                        <button type="submit" class="btn-secondary btn-with-icon">
                            <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="journey.filter.button"/>" class="btn-icon" />
                            <spring:message code="journey.filter.button"/>
                        </button>
                    </div>
                </form:form>
            </div>

            <div class="events-container">

                <div class="events-grid">
                    <c:if test="${empty journeys.content}">
                        <div class="empty-state">
                            <p class="empty-message"><spring:message code="journey.no.journeys"/></p>
                        </div>
                    </c:if>
                    <c:forEach var="journey" items="${journeys.content}">
                        <jsp:include page="journey-card.jsp">
                            <jsp:param name="journeyId" value="${journey.id}" />
                            <jsp:param name="city" value="${journey.destinationUniversity.city.name}" />
                            <jsp:param name="startDate" value="${journey.startDate}" />
                            <jsp:param name="endDate" value="${journey.endDate}" />
                            <jsp:param name="description" value="${journey.description}" />
                            <jsp:param name="profilePictureId" value="${journey.user.profilePictureId}" />
                            <jsp:param name="userName" value="${journey.user.username}" />
                            <jsp:param name="firstname" value="${journey.user.firstname}" />
                            <jsp:param name="lastname" value="${journey.user.lastname}"/>
                            <jsp:param name="country" value="${journey.destinationUniversity.city.country}"/>
                            <jsp:param name="university" value="${journey.destinationUniversity.name}"/>
                            <jsp:param name="isOwner" value="false"/>
                        </jsp:include>
                    </c:forEach>
                </div>
            </div>


            <c:set var="paginationBaseUrl" value="/journeys?" />
            <c:if test="${not empty escapedSearch}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}search=${escapedSearch}&" />
            </c:if>
            <c:if test="${not empty escapedCity}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}destination=${escapedCity}&" />
            </c:if>
            <c:if test="${not empty escapedStartDate}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}startDate=${escapedStartDate}&" />
            </c:if>
            <c:if test="${not empty escapedEndDate}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}endDate=${escapedEndDate}&" />
            </c:if>
            <c:if test="${not empty escapedInterest}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}interests=${escapedInterest}&" />
            </c:if>
            <c:if test="${not empty sort}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}sort=${sort}&" />
            </c:if>
            <c:if test="${not empty direction}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}direction=${direction}&" />
            </c:if>
            <c:if test="${not empty isMyDestination}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isMyDestination=${isMyDestination}&" />
            </c:if>
            <c:if test="${not empty isUpcoming}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isUpcoming=${isUpcoming}&" />
            </c:if>
            <c:if test="${not empty isOngoing}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isOngoing=${isOngoing}&" />
            </c:if>
            <c:if test="${not empty isPast}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}isPast=${isPast}&" />
            </c:if>
            <c:if test="${not empty pageSize}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}pageSize=${pageSize}&" />
            </c:if>

            <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                <jsp:param name="pageObjectTotalPages" value="${journeys.totalPages}" />
                <jsp:param name="currentPage" value="${currentPage}" />
                <jsp:param name="pageSize" value="${pageSize}" />
                <jsp:param name="baseUrl" value="${paginationBaseUrl}" />
            </jsp:include>

        </div>
    </div>
</div>
<script>
    function htmlDecode(input) {
        const doc = new DOMParser().parseFromString(input, "text/html");
        return doc.documentElement.textContent;
    }

    journeySelectedInterests = htmlDecode('<c:out value="${filterJourneyForm.interests}"/>');
    window.apiBaseUrl = '<c:url value="/" />';
    window.journeyBaseUrl = '<c:url value="/journeys"/>';
    window.closeImage = '<c:url value="/resources/icons/x.svg"/>';
    journeySelectedCity = htmlDecode('<c:out value="${filterJourneyForm.destination}"/>');
</script>
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/journeys/journey-cards.js'/>"></script>
<script src="<c:url value='/resources/js/journeys/filter.js'/>"></script>


</body>
</html>
