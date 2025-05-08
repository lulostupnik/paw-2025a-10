<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="journey.page.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
</head>
<body>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>

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
                    <button id="filterToggleBtn" class="btn-secondary btn-with-icon">
                        <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="journey.filter.toggle"/>" class="btn-icon filter-icon" />
                        <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="journey.filter.close"/>" class="btn-icon close-icon" style="display: none;" />
                        <spring:message code="journey.filter.toggle"/>
                    </button>
                    <c:if test="${hasJourney == false}">
                        <a href="<c:url value="/journeys/create"/>" class="btn btn-primary btn-with-icon">
                            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
                            <spring:message code="journey.create.button"/>
                        </a>
                    </c:if>
                </div>
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
                                <c:set var="citySearch"><spring:message code='journey.filter.destination.placeholder'/></c:set>
                                <form:input path="destination" type="text" id="citySearch" class="autocomplete-input"
                                       placeholder="${citySearch}"
                                       value="${param.destinationName}" />
                                <select id="city" name="destination" class="hidden-select" style="display: none;">
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
                                <select  id="interest-select" name="interest" class="hidden-select" style="display: none;">
                                    <option value=""></option>
                                    <c:forEach var="interest" items="${interests}">
                                        <option value="${interest.id}"><c:out value="${interest.name}"/></option>
                                    </c:forEach>
                                </select>
                                <c:set var="interestSearch"><spring:message code='journey.filter.interest.placeholder'/></c:set>
                                <form:input path="interests" type="text" id="interest-search" class="autocomplete-input"
                                       placeholder="${interestSearch}"
                                       value="${param.interestName}" />
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

            <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                <jsp:param name="pageObjectTotalPages" value="${journeys.totalPages}" />
                <jsp:param name="currentPage" value="${currentPage}" />
                <jsp:param name="pageSize" value="${pageSize}" />
                <jsp:param name="baseUrl" value="/journeys" />
            </jsp:include>
            <!-- End Journeys List -->
        </div>
    </div>
</div>
<script>
    window.apiBaseUrl = '<c:url value="/" />';
    window.journeyBaseUrl = '<c:url value="/journeys" />';
    window.closeImage = '<c:url value="/resources/icons/x.svg"/>';
    journeySelectedInterests = '<c:out value="${filterJourneyForm.interests}"/>';
    journeySelectedCity = '<c:out value="${filterJourneyForm.destination}"/>';
</script>
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/components/single-option-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/journey-cards.js'/>"></script>
<script src="<c:url value='/resources/js/filter.js'/>"></script>

<!-- Custom JavaScript for the autocomplete functionality -->
</body>
</html>
