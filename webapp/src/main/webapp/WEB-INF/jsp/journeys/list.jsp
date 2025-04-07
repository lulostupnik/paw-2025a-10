<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="journey.page.title"/></title>
    <!-- Include Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {}
            }
        };
    </script>
    <!-- Include Preline UI Kit CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body class="bg-gray-100">

<div class="flex h-full min-h-screen">
    <!-- Include the sidebar component -->
    <jsp:include page="../components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="ml-64 flex-1">
        <div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
            <div class="mx-auto">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                        <spring:message code="journey.list.title"/>
                    </h2>
                    <div class="flex items-center gap-3">
                        <button id="filterToggleBtn" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
                            <svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                            </svg>
                            <spring:message code="journey.filter.toggle"/>
                        </button>
                        <a href="${pageContext.request.contextPath}/journeys/create" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
                            <spring:message code="journey.create.button"/>
                        </a>
                    </div>
                </div>

                <!-- Filter Section - Initially Hidden -->
                <div id="filterSection" class="bg-white p-4 rounded-xl border border-gray-200 mb-6 hidden transition-all duration-300">
                    <h3 class="text-lg font-semibold text-gray-800 mb-3">
                        <spring:message code="journey.filter.title" text="Filter Journeys"/>
                    </h3>
                    <form:form action="${pageContext.request.contextPath}/journeys/" method="GET" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div>
                            <label for="destination" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.destination"/>
                            </label>
                            <input type="text" id="destination" name="destination" class="py-2 px-3 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                   placeholder="<spring:message code="journey.filter.destination.placeholder"/>" value="<c:out value="${filterJourneyForm.destination}"/>" />
                        </div>

                        <div>
                            <label for="startDate" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.startDate"/>
                            </label>
                            <input type="date" id="startDate" name="startDate" class="py-2 px-3 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                   value="<c:out value="${filterJourneyForm.startDate}"/>"/>
                        </div>

                        <div>
                            <label for="endDate" class="block text-sm font-medium text-gray-700 mb-1">
                               <spring:message code="journey.filter.endDate"/>
                            </label>
                            <input type="date" id="endDate" name="endDate" class="py-2 px-3 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                   value="<c:out value="${filterJourneyForm.endDate}"/>"/>
                        </div>

                        <div>
                            <label for="interest" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.interest" />
                            </label>
                            <input type="text" id="interest" name="interest" class="py-2 px-3 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                   placeholder="<spring:message code="journey.filter.interest.placeholder" />" value="<c:out value=" ${filterJourneyForm.interest}"/>" />
                        </div>

                        <div class="md:col-span-2 lg:col-span-4 flex justify-end gap-2">
                            <a href="${pageContext.request.contextPath}/journeys" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-red-600 text-white hover:bg-red-700 transition-colors">
                                <svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                                </svg>
                                <spring:message code="journey.filter.reset" text="Reset Filters"/>
                            </a>
                            <button type="submit" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-700 text-white hover:bg-blue-800 shadow-md transition-colors">
                                <svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                </svg>
                                <spring:message code="journey.filter.button"/>
                            </button>
                        </div>
                    </form:form>
                </div>

                <!-- Journeys List with vertical scrolling -->
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-h-[600px] overflow-y-auto pb-6 pr-2">
                    <c:forEach var="journey" items="${journeys}">
                        <!-- Card -->
                        <div class="flex-shrink-0">
                            <div class="rounded-xl bg-white border border-gray-200 hover:border-blue-600 transition-all duration-200 h-full">
                                <div class="p-4 sm:p-6">
                                    <div class="flex items-center gap-x-4">
                                        <div>
                                            <h3 class="text-lg font-semibold text-gray-800">
                                                <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                            </h3>
                                            <p class="text-sm text-gray-500">
                                                <c:out value="${journey.destinationCity}" /> -
                                                <c:out value="${journey.destinationUniversity.name}" />
                                            </p>
                                            <p class="text-sm text-gray-500 mt-1">
                                                <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                            </p>
                                        </div>
                                    </div>
                                    <p class="mt-3 text-gray-600 line-clamp-3">
                                        <c:out value="${journey.description}" />
                                    </p>

                                    <!-- Action Buttons -->
                                    <div class="mt-4 flex gap-2">
                                        <a href="${pageContext.request.contextPath}/journeys/${journey.id}"
                                           class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
                                            <spring:message code="journey.view.details" text="View Details"/>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/journeys/${journey.id}/reply"
                                           class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
                                            <spring:message code="journey.reply.button"/>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>

                    <c:if test="${empty journeys}">
                        <div class="col-span-full text-center py-10">
                            <p class="text-gray-500"><spring:message code="journey.no.journeys"/></p>
                        </div>
                    </c:if>
                </div>
                <!-- End Journeys List -->
            </div>
        </div>
    </div>
</div>

<!-- Include Preline UI Kit JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.js"></script>

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
            this.classList.toggle('bg-gray-50');
            this.classList.toggle('bg-blue-50');
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