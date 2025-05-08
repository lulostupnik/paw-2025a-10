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

<div class="layout-container">
    <!-- Main Content -->
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
                               value="<c:out value="${param.search}"/>">
                        <input type="hidden" name="page" value="1">
                        <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">

                        <!-- Preserve sort parameters -->
                        <c:if test="${not empty param.sort}">
                            <input type="hidden" name="sort" value="<c:out value="${param.sort}"/>">
                        </c:if>
                        <c:if test="${not empty param.direction}">
                            <input type="hidden" name="direction" value="<c:out value="${param.direction}"/>">
                        </c:if>

                        <!-- Preserve filter parameters -->
                        <c:if test="${not empty param.destination}">
                            <input type="hidden" name="destination" value="<c:out value="${param.destination}"/>">
                        </c:if>
                        <c:if test="${not empty param.destinationName}">
                            <input type="hidden" name="destinationName" value="<c:out value="${param.destinationName}"/>">
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
                        <c:if test="${not empty param.isMyDestination}">
                            <input type="hidden" name="isMyDestination" value="<c:out value="${param.isMyDestination}"/>">
                        </c:if>
                        <c:if test="${not empty param.isUpcoming}">
                            <input type="hidden" name="isUpcoming" value="<c:out value="${param.isUpcoming}"/>">
                        </c:if>
                        <c:if test="${not empty param.isPast}">
                            <input type="hidden" name="isPast" value="<c:out value="${param.isPast}"/>">
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
                            <a href="<c:url value="/journeys?sort=start_date&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isMyDestination ? '&isMyDestination='.concat(param.isMyDestination) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'startDate' && param.direction == 'asc' ? 'active' : ''}">
                                <spring:message code="journey.sort.startDate.asc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=start_date&direction=desc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isMyDestination ? '&isMyDestination='.concat(param.isMyDestination) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'startDate' && param.direction == 'desc' ? 'active' : ''}">
                                <spring:message code="journey.sort.startDate.desc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=end_date&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isMyDestination ? '&isMyDestination='.concat(param.isMyDestination) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'endDate' && param.direction == 'asc' ? 'active' : ''}">
                                <spring:message code="journey.sort.endDate.asc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=end_date&direction=desc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isMyDestination ? '&isMyDestination='.concat(param.isMyDestination) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'endDate' && param.direction == 'desc' ? 'active' : ''}">
                                <spring:message code="journey.sort.endDate.desc"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=city&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isMyDestination ? '&isMyDestination='.concat(param.isMyDestination) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'city' && param.direction == 'asc' ? 'active' : ''}">
                                <spring:message code="journey.sort.city"/>
                            </a>
                            <a href="<c:url value="/journeys?sort=interest&direction=asc${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.isMyDestination ? '&isMyDestination='.concat(param.isMyDestination) : ''}${not empty param.isUpcoming ? '&isUpcoming='.concat(param.isUpcoming) : ''}${not empty param.isPast ? '&isPast='.concat(param.isPast) : ''}${not empty param.page ? '&page='.concat(param.page) : ''}${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="${param.sort == 'interest' && param.direction == 'asc' ? 'active' : ''}">
                                <spring:message code="journey.sort.interest"/>
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

            <!-- Journey Tabs -->
            <div class="journey-tabs">
                <ul class="tabs-list">
                    <li class="tab-item ${empty param.isMyDestination && empty param.isUpcoming && empty param.isPast ? 'active' : ''}">
                        <a href="<c:url value="/journeys?${not empty param.search ? 'search='.concat(param.search).concat('&') : ''}${not empty param.destination ? 'destination='.concat(param.destination).concat('&') : ''}${not empty param.destinationName ? 'destinationName='.concat(param.destinationName).concat('&') : ''}${not empty param.startDate ? 'startDate='.concat(param.startDate).concat('&') : ''}${not empty param.endDate ? 'endDate='.concat(param.endDate).concat('&') : ''}${not empty param.interests ? 'interests='.concat(param.interests).concat('&') : ''}${not empty param.interestName ? 'interestName='.concat(param.interestName).concat('&') : ''}${not empty param.sort ? 'sort='.concat(param.sort).concat('&') : ''}${not empty param.direction ? 'direction='.concat(param.direction).concat('&') : ''}page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.all"/>
                        </a>
                    </li>
                    <li class="tab-item ${not empty param.isMyDestination ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isMyDestination=true${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}${not empty param.direction ? '&direction='.concat(param.direction) : ''}&page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.myDestination"/>
                        </a>
                    </li>
                    <li class="tab-item ${not empty param.isUpcoming ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isUpcoming=true${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}${not empty param.direction ? '&direction='.concat(param.direction) : ''}&page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.upcoming"/>
                        </a>
                    </li>
                    <li class="tab-item ${not empty param.isPast ? 'active' : ''}">
                        <a href="<c:url value="/journeys?isPast=true${not empty param.search ? '&search='.concat(param.search) : ''}${not empty param.destination ? '&destination='.concat(param.destination) : ''}${not empty param.destinationName ? '&destinationName='.concat(param.destinationName) : ''}${not empty param.startDate ? '&startDate='.concat(param.startDate) : ''}${not empty param.endDate ? '&endDate='.concat(param.endDate) : ''}${not empty param.interests ? '&interests='.concat(param.interests) : ''}${not empty param.interestName ? '&interestName='.concat(param.interestName) : ''}${not empty param.sort ? '&sort='.concat(param.sort) : ''}${not empty param.direction ? '&direction='.concat(param.direction) : ''}&page=1${not empty param.pageSize ? '&pageSize='.concat(param.pageSize) : ''}"/>" class="tab-link">
                            <spring:message code="journey.tabs.past"/>
                        </a>
                    </li>
                </ul>
            </div>

            <!-- Filter Section - Initially Hidden -->
            <div id="filterSection" class="filter-section hidden">
                <h3 class="filter-title">
                    <spring:message code="journey.filter.title"/>
                </h3>
                <c:set var="actionGet"><c:url value="/journeys"/></c:set>
                <form:form action="${actionGet}" method="GET"
                           modelAttribute="filterJourneyForm"
                           class="filter-form" id="journeyFilterForm">

                    <div class="filter-grid">
                        <!-- Destination filter with autocomplete -->
                        <div class="filter-item">
                            <c:set var="destinationLabel"><spring:message code="createJourney.destinationCity"/></c:set>
                            <form:label for="citySearch" class="form-label" path="destination">${destinationLabel}</form:label>
                            <div class="autocomplete-wrapper">
                                <input type="text" id="citySearch" class="autocomplete-input"
                                       placeholder="<spring:message code='journey.filter.destination.placeholder'/>"
                                       value="${param.destinationName}" />
                                <form:select id="city" name="destination" class="hidden-select" path="destination" style="display: none;">
                                    <option value=""></option>
                                    <c:forEach var="city" items="${cities}">
                                        <option value="${city.id}" ${param.destination == city.id ? 'selected' : ''}><c:out value="${city.name}"/></option>
                                    </c:forEach>
                                </form:select>
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

                        <!-- Interest filter with autocomplete -->
                        <div class="filter-item">
                            <c:set var="interestsLabel"><spring:message code="journey.filter.interest"/></c:set>
                            <form:label for="interest-search" class="form-label" path="interests">${interestsLabel}</form:label>
                            <div class="autocomplete-wrapper">
                                <input type="text" id="interest-search" class="autocomplete-input"
                                       placeholder="<spring:message code='journey.filter.interest.placeholder'/>"
                                       value="${param.interestName}" />
                                <form:select path="interests" id="interest-select" name="interests" class="hidden-select" style="display: none;">
                                    <option value=""></option>
                                    <c:forEach var="interest" items="${interests}">
                                        <option value="${interest.id}" ${param.interests == interest.id ? 'selected' : ''}><c:out value="${interest.name}"/></option>
                                    </c:forEach>
                                </form:select>
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
                    <c:if test="${not empty param.isMyDestination}">
                        <form:hidden path="isMyDestination" value="${param.isMyDestination}" />
                    </c:if>
                    <c:if test="${not empty param.isUpcoming}">
                        <form:hidden path="isUpcoming" value="${param.isUpcoming}" />
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
                <!-- Journeys List with grid layout -->
                <div class="events-grid">
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
                    <c:if test="${empty journeys.content}">
                        <div class="no-journeys">
                            <p class="no-journeys-message"><spring:message code="journey.no.journeys"/></p>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Construct baseUrl with all query parameters -->
            <c:set var="paginationBaseUrl" value="/journeys?" />
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
            <c:if test="${not empty param.pageSize}">
                <c:set var="paginationBaseUrl" value="${paginationBaseUrl}pageSize=${param.pageSize}&" />
            </c:if>

            <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                <jsp:param name="pageObjectTotalPages" value="${journeys.totalPages}" />
                <jsp:param name="currentPage" value="${currentPage}" />
                <jsp:param name="pageSize" value="${pageSize}" />
                <jsp:param name="baseUrl" value="${paginationBaseUrl}" />
            </jsp:include>
            <!-- End Journeys List -->
        </div>
    </div>
</div>

<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/journey-cards.js'/>"></script>

<!-- Custom JavaScript for the autocomplete functionality -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize filter toggle
        const filterToggleBtn = document.getElementById('filterToggleBtn');
        const filterSection = document.getElementById('filterSection');
        const filterForm = document.getElementById('journeyFilterForm');
        const filterIcon = filterToggleBtn.querySelector('.filter-icon');
        const closeIcon = filterToggleBtn.querySelector('.close-icon');

        // Check if there are any filter parameters in the URL
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('destination') || urlParams.has('startDate') ||
            urlParams.has('endDate') || urlParams.has('interests')) {
            // Show filter section if filters are applied
            filterSection.classList.remove('hidden');
            // Update icons
            filterIcon.style.display = 'none';
            closeIcon.style.display = 'inline';
        }

        // Toggle filter section visibility
        filterToggleBtn.addEventListener('click', function() {
            filterSection.classList.toggle('hidden');

            // Toggle icons
            if (filterSection.classList.contains('hidden')) {
                filterIcon.style.display = 'inline';
                closeIcon.style.display = 'none';
            } else {
                filterIcon.style.display = 'none';
                closeIcon.style.display = 'inline';
            }

            // Optional: Animate the toggle button
            this.classList.toggle('active');
        });

        // Sort dropdown toggle
        const sortToggleBtn = document.getElementById('sortToggleBtn');
        const sortDropdown = document.getElementById('sortDropdown');

        sortToggleBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            sortDropdown.style.display = sortDropdown.style.display === 'block' ? 'none' : 'block';
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!sortToggleBtn.contains(e.target) && !sortDropdown.contains(e.target)) {
                sortDropdown.style.display = 'none';
            }
        });

        // City Autocomplete
        initAutocomplete('citySearch', 'cityDropdown', 'city', 'citySelectedContainer', false);

        // Interest Autocomplete
        initAutocomplete('interest-search', 'interest-dropdown', 'interest-select', 'interestSelectedContainer', false);

        // Initialize with any pre-selected values
        initializeSelectedValues();

        // Reset button functionality
        const resetFiltersBtn = document.getElementById('resetFiltersBtn');
        if (resetFiltersBtn) {
            resetFiltersBtn.addEventListener('click', function(e) {
                e.preventDefault(); // Prevent default button behavior

                // Build URL with only sort parameters preserved
                let resetUrl = '<c:url value="/journeys"/>';
                const sortParam = urlParams.get('sort');
                const directionParam = urlParams.get('direction');
                const searchParam = urlParams.get('search');
                const pageSizeParam = urlParams.get('pageSize');

                // Preserve tab parameters
                const isMyDestinationParam = urlParams.get('isMyDestination');
                const isUpcomingParam = urlParams.get('isUpcoming');
                const isPastParam = urlParams.get('isPast');

                // Start with question mark if we have parameters
                let hasParam = false;

                if (sortParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'sort=' + sortParam;
                    hasParam = true;
                }

                if (directionParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'direction=' + directionParam;
                    hasParam = true;
                }

                if (searchParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'search=' + searchParam;
                    hasParam = true;
                }

                if (pageSizeParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'pageSize=' + pageSizeParam;
                    hasParam = true;
                }

                // Add tab parameters if they exist
                if (isMyDestinationParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'isMyDestination=' + isMyDestinationParam;
                    hasParam = true;
                }

                if (isUpcomingParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'isUpcoming=' + isUpcomingParam;
                    hasParam = true;
                }

                if (isPastParam) {
                    resetUrl += (hasParam ? '&' : '?') + 'isPast=' + isPastParam;
                    hasParam = true;
                }

                // Always reset to page 1
                resetUrl += (hasParam ? '&' : '?') + 'page=1';

                // Navigate to the reset URL
                window.location.href = resetUrl;
            });
        }

        // Function to initialize autocomplete
        function initAutocomplete(inputId, dropdownId, selectId, containerid, multiSelect) {
            const input = document.getElementById(inputId);
            const dropdown = document.getElementById(dropdownId);
            const select = document.getElementById(selectId);
            const selectedContainer = document.getElementById(containerid);
            const options = dropdown.querySelectorAll('.autocomplete-item');

            // Show dropdown on input focus
            input.addEventListener('focus', function() {
                dropdown.style.display = 'block';
                filterOptions(this.value);
            });

            // Show dropdown when clicking on input
            input.addEventListener('click', function(e) {
                e.stopPropagation();
                dropdown.style.display = 'block';
                filterOptions(this.value);
            });

            // Hide dropdown when clicking outside
            document.addEventListener('click', function(e) {
                if (!input.contains(e.target) && !dropdown.contains(e.target)) {
                    dropdown.style.display = 'none';
                }
            });

            // Filter options as user types
            input.addEventListener('input', function() {
                filterOptions(this.value);
                dropdown.style.display = 'block';
            });

            // Handle option selection
            options.forEach(option => {
                option.addEventListener('click', function() {
                    const value = this.dataset.value;
                    const text = this.textContent.trim();

                    // For single select, clear previous selection
                    if (!multiSelect) {
                        // Clear all options
                        Array.from(select.options).forEach(opt => {
                            opt.selected = false;
                        });

                        // Clear selected container
                        selectedContainer.innerHTML = '';
                    }

                    // Find and select the option
                    Array.from(select.options).forEach(opt => {
                        if (opt.value === value) {
                            opt.selected = true;
                        }
                    });

                    // Update input and selected container
                    input.value = '';

                    // Create selected tag
                    const tag = document.createElement('div');
                    tag.className = 'selected-tag';
                    tag.innerHTML = text;

                    // Add remove button for tag
                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.className = 'tag-remove';
                    removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>" width="12" height="12"/>';
                    removeBtn.addEventListener('click', function() {
                        // Deselect the option
                        Array.from(select.options).forEach(opt => {
                            if (opt.value === value) {
                                opt.selected = false;
                            }
                        });

                        // Remove the tag
                        tag.remove();
                    });

                    tag.appendChild(removeBtn);
                    selectedContainer.appendChild(tag);

                    // Hide dropdown
                    dropdown.style.display = 'none';
                });
            });

            // Filter dropdown options based on search text
            function filterOptions(searchText) {
                const filter = searchText.toLowerCase();
                let hasResults = false;

                options.forEach(option => {
                    const text = option.textContent.toLowerCase();
                    if (text.includes(filter)) {
                        option.style.display = '';
                        hasResults = true;
                    } else {
                        option.style.display = 'none';
                    }
                });


                const noResultsTxt = document.getElementById("i18n-results-match-none")
                    ? document.getElementById("i18n-results-match-none").value
                    : "No matching results found"

                // Show no results message if needed
                let noResultsMsg = dropdown.querySelector('.no-results');
                if (!hasResults) {
                    if (!noResultsMsg) {
                        noResultsMsg = document.createElement('div');
                        noResultsMsg.className = 'autocomplete-item no-results';
                        noResultsMsg.textContent = noResultsTxt;
                        dropdown.appendChild(noResultsMsg);
                    }
                    noResultsMsg.style.display = '';
                } else if (noResultsMsg) {
                    noResultsMsg.style.display = 'none';
                }
            }
        }

        // Initialize selected values from URL parameters
        function initializeSelectedValues() {
            // City
            const citySelect = document.getElementById('city');
            const citySelectedContainer = document.getElementById('citySelectedContainer');

            if (citySelect.value) {
                const selectedOption = Array.from(citySelect.options).find(opt => opt.selected);
                if (selectedOption) {
                    const tag = document.createElement('div');
                    tag.className = 'selected-tag';
                    tag.innerHTML = selectedOption.textContent;

                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.className = 'tag-remove';
                    removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
                    removeBtn.addEventListener('click', function() {
                        selectedOption.selected = false;
                        tag.remove();
                    });

                    tag.appendChild(removeBtn);
                    citySelectedContainer.appendChild(tag);
                }
            }

            // Interest
            const interestSelect = document.getElementById('interest-select');
            const interestSelectedContainer = document.getElementById('interestSelectedContainer');

            if (interestSelect.value) {
                const selectedOption = Array.from(interestSelect.options).find(opt => opt.selected);
                if (selectedOption) {
                    const tag = document.createElement('div');
                    tag.className = 'selected-tag';
                    tag.innerHTML = selectedOption.textContent;

                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.className = 'tag-remove';
                    removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
                    removeBtn.addEventListener('click', function() {
                        selectedOption.selected = false;
                        tag.remove();
                    });

                    tag.appendChild(removeBtn);
                    interestSelectedContainer.appendChild(tag);
                }
            }
        }
    });
</script>

<style>
    /* Sort dropdown styles */
    .sort-dropdown {
        position: relative;
        display: inline-block;
    }

    .dropdown-content {
        position: absolute;
        right: 0;
        background-color: white;
        min-width: 200px;
        box-shadow: 0 8px 16px rgba(0,0,0,0.1);
        z-index: 1;
        border-radius: 0.5rem;
        overflow: hidden;
    }

    .dropdown-content a {
        color: #333;
        padding: 12px 16px;
        text-decoration: none;
        display: block;
        transition: background-color 0.2s;
    }

    .dropdown-content a:hover {
        background-color: #f1f1f1;
    }

    .dropdown-content a.active {
        background-color: #e5e7eb;
        font-weight: 500;
    }

    /* Journey Tabs Styles */
    .journey-tabs {
        margin: 1.5rem 0;
    }

    .tabs-list {
        display: flex;
        list-style: none;
        padding: 0;
        margin: 0;
        border-bottom: 1px solid #e5e7eb;
        overflow-x: auto;
    }

    .tab-item {
        margin-right: 0.5rem;
    }

    .tab-link {
        display: block;
        padding: 0.75rem 1.25rem;
        color: #6b7280;
        text-decoration: none;
        border-bottom: 2px solid transparent;
        transition: all 0.2s ease;
        white-space: nowrap;
    }

    .tab-item.active .tab-link {
        color: #4361ee;
        border-bottom-color: #4361ee;
        font-weight: 500;
    }

    .tab-link:hover {
        color: #4361ee;
    }

    @media (max-width: 640px) {
        .tabs-list {
            padding-bottom: 0.5rem;
        }

        .tab-link {
            padding: 0.5rem 1rem;
            font-size: 0.875rem;
        }
    }
</style>
</body>
</html>
