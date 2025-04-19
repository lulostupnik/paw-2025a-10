<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="journey.page.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/journey-card-styles.css'/>" />
</head>
<body>

<div class="layout-container">
    <!-- Include the sidebar component -->
    <!-- Main Content -->
    <div class="main-content">
        <jsp:include page="../components/navbar.jsp" />
        <div class="content-container">
            <div class="header-container">
                <h2 class="page-title">
                    <spring:message code="journey.list.title"/>
                </h2>
                <div class="journeys-actions">
                    <button id="filterToggleBtn" class="btn-secondary">
                        <i class="fas fa-filter btn-icon"></i>
                        <spring:message code="journey.filter.toggle"/>
                    </button>
                    <a href="<c:url value="/journeys/create"/>" class="btn btn-primary">
                        <spring:message code="journey.create.button"/>
                    </a>
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
                           class="filter-form">

                    <div class="filter-grid">
                        <div class="filter-item">
                            <c:set var="destinationLabel"><spring:message code="createJourney.destinationUniversity"/></c:set>
                            <jsp:include page="../components/dropdown.jsp">
                                <jsp:param name="path" value="destination"/>
                                <jsp:param name="label" value="${destinationLabel}"/>
                                <jsp:param name="items" value="cities"/>
                                <jsp:param name="defaultMessageCode" value="journey.filter.destination.placeholder"/>
                            </jsp:include>
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
                        </div>

                        <!-- Interest Filter using dropdown instead of autocomplete -->
                        <div class="filter-item">
                            <c:set var="interestsLabel"><spring:message code="journey.filter.interest"/></c:set>
                            <jsp:include page="../components/dropdown.jsp">
                                <jsp:param name="path" value="interest"/>
                                <jsp:param name="label" value="${interestsLabel}"/>
                                <jsp:param name="items" value="interests"/>
                                <jsp:param name="defaultMessageCode" value="journey.filter.interest.placeholder"/>
                            </jsp:include>
                        </div>

                    </div>

                    <div class="filter-actions">
                        <a href="<c:url value="/journeys"/>" class="btn-danger">
                            <i class="fas fa-times btn-icon"></i>
                            <spring:message code="journey.filter.reset"/>
                        </a>
                        <button type="submit" class="btn-primary">
                            <i class="fas fa-filter btn-icon"></i>
                            <spring:message code="journey.filter.button"/>
                        </button>
                    </div>
                </form:form>
            </div>

            <div class="events-container">
                <!-- Journeys List with grid layout -->
                <div class="events-grid">
                    <c:forEach var="journey" items="${journeys}">
                        <jsp:include page="journey-card.jsp">
                            <jsp:param name="journeyId" value="${journey.id}" />
                            <jsp:param name="city" value="${journey.destinationUniversity.city.name}" />
                            <jsp:param name="startDate" value="${journey.startDate}" />
                            <jsp:param name="endDate" value="${journey.endDate}" />
                            <jsp:param name="description" value="${journey.description}" />
                            <jsp:param name="profilePictureId" value="${journey.user.profilePictureId}" />
                            <jsp:param name="userName" value="${journey.user.username}" />
                            <jsp:param name="university" value="${journey.destinationUniversity.name}" />
                            <jsp:param name="country" value="${journey.destinationUniversity.city.country}" />
                        </jsp:include>
                    </c:forEach>
                    <c:if test="${empty journeys}">
                        <div class="no-journeys">
                            <p class="no-journeys-message"><spring:message code="journey.no.journeys"/></p>
                        </div>
                    </c:if>
                </div>
            </div>
            <!-- End Journeys List -->
        </div>
    </div>
</div>

<!-- Filter Toggle JavaScript -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const filterToggleBtn = document.getElementById('filterToggleBtn');
        const filterSection = document.getElementById('filterSection');
        const filterForm = filterSection.querySelector('form');

        // Check if there are any filter parameters in the URL
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('destination') || urlParams.has('startDate') ||
            urlParams.has('endDate') || urlParams.has('interest')) {
            // Show filter section if filters are applied
            filterSection.classList.remove('hidden');
        }

        // Toggle filter section visibility
        filterToggleBtn.addEventListener('click', function() {
            filterSection.classList.toggle('hidden');

            // Optional: Animate the toggle button
            this.classList.toggle('active');
        });

        // Optional: Add event listener for reset button to clear form fields
        const resetButton = filterSection.querySelector('a[href*="/journeys"]');
        if (resetButton) {
            resetButton.addEventListener('click', function(e) {
                // Clear all form fields before navigating
                const inputs = filterForm.querySelectorAll('input');
                inputs.forEach(input => {
                    input.value = '';
                });

                // Let the default navigation happen
                // (The link will take the user to the base journeys URL)
            });
        }
    });
</script>
<script src="<c:url value='/resources/js/journey-cards.js'/>"></script>
</body>
</html>
