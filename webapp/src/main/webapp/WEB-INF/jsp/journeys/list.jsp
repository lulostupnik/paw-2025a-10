<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="journey.page.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/journey-list.css'/>" />
</head>
<body>

<div class="page-container">
    <!-- Include the sidebar component -->
    <jsp:include page="../components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <div class="content-wrapper">
                <div class="header">
                    <h2 class="page-title">
                        <spring:message code="journey.list.title"/>
                    </h2>
                    <div class="header-actions">
                        <button id="filterToggleBtn" class="btn-secondary">
                            <svg class="btn-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                            </svg>
                            <spring:message code="journey.filter.toggle"/>
                        </button>
                        <a href="<c:url value="/journeys/create"/>" class="btn-primary">
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

                            <!-- Interest Filter using the autocomplete component -->
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
                                <svg class="btn-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                                </svg>
                                <spring:message code="journey.filter.reset"/>
                            </a>
                            <button type="submit" class="btn-primary">
                                <svg class="btn-icon" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                </svg>
                                <spring:message code="journey.filter.button"/>
                            </button>
                        </div>
                    </form:form>
                </div>

                <!-- Journeys List with vertical scrolling -->
                <div class="journey-grid">
                    <c:forEach var="journey" items="${journeys}">
                        <!-- Card -->
                        <div class="journey-card">
                            <!-- Card Content -->
                            <div class="journey-card-content">
                                <div class="journey-user-info">
                                    <div>
                                        <h3 class="journey-user-name">
                                            <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                        </h3>
                                        <p class="journey-destination">
                                            <c:out value="${journey.destinationUniversity.city}" /> -
                                            <c:out value="${journey.destinationUniversity.name}" />
                                        </p>
                                        <p class="journey-dates">
                                            <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                        </p>
                                    </div>
                                </div>
                                <p class="journey-description">
                                    <c:out value="${journey.description}" />
                                </p>
                            </div>

                            <!-- Action Buttons - Fixed at bottom -->
                            <div class="journey-card-actions">
                                <div class="journey-buttons">
                                    <a href="<c:url value="/journeys/${journey.id}"/>"
                                       class="btn-secondary btn-card">
                                        <spring:message code="journey.view.details"/>
                                    </a>
                                    <a href="<c:url value="/journeys/${journey.id}/reply"/>"
                                       class="btn-primary btn-card">
                                        <spring:message code="journey.reply.button"/>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <c:if test="${empty journeys}">
                    <div class="no-journeys">
                        <p><spring:message code="journey.no.journeys"/></p>
                    </div>
                </c:if>
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
</body>
</html>