<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="app.title" text="Student Exchange Platform"/></title>
    <!-- Include Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <!-- Include Preline UI Kit CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>

<body class="bg-gray-100">
<div class="flex h-full min-h-screen">
    <!-- Include the sidebar component -->
    <jsp:include page="components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="ml-64 flex-1">
        <div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
            <!-- Welcome Header -->
            <div class="mx-auto mb-6"> <!-- Changed to mb-6 to match the second file -->
                <h1 class="text-2xl font-bold text-gray-900 sm:text-3xl">
                    <spring:message code="dashboard.welcome" text="Welcome to the Exchange Platform"/>
                </h1>
            </div>

            <!-- Content Grid -->
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <!-- Recommended Journeys -->
                <div class="bg-white rounded-xl border border-gray-200 shadow-sm">
                    <div class="p-5 border-b border-gray-200">
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">
                                <spring:message code="dashboard.recommended.journeys" text="Recommended Journeys"/>
                            </h2>
                            <a href="${pageContext.request.contextPath}/journeys" class="text-sm font-medium text-blue-600 hover:text-blue-700">
                                <spring:message code="dashboard.view.all" text="View All"/>
                            </a>
                        </div>
                    </div>

                    <!-- Journey List -->
                    <div class="divide-y divide-gray-200">
                        <c:forEach var="journey" items="${recommendedJourneys}" varStatus="status">
                            <div class="p-5 hover:bg-gray-50">
                                <div class="flex justify-between">
                                    <div>
                                        <h3 class="text-base font-semibold text-gray-800">
                                            <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                        </h3>
                                        <p class="text-sm text-gray-600 mt-1">
                                            <c:out value="${journey.destinationCity}" /> -
                                            <c:out value="${journey.destinationUniversity}" />
                                        </p>
                                        <p class="text-sm text-gray-500 mt-1">
                                            <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                        </p>
                                    </div>
                                    <a href="<c:url /journey/${journey.id}"/>" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-700">
                                        <spring:message code="dashboard.details" text="Details"/>
                                        <svg class="ml-1 w-4 h-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                        </svg>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty recommendedJourneys}">
                            <div class="p-5 text-center">
                                <p class="text-gray-500">
                                    <spring:message code="dashboard.no.journeys" text="No recommended journeys available"/>
                                </p>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Recommended Events -->
                <div class="bg-white rounded-xl border border-gray-200 shadow-sm">
                    <div class="p-5 border-b border-gray-200">
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">
                                <spring:message code="dashboard.recommended.events" text="Recommended Events"/>
                            </h2>
                            <a href="<c:url/events"/>" class="text-sm font-medium text-blue-600 hover:text-blue-700">
                                <spring:message code="dashboard.view.all" text="View All"/>
                            </a>
                        </div>
                    </div>

                    <!-- Events List -->
                    <div class="divide-y divide-gray-200">
                        <c:forEach var="event" items="${recommendedEvents}" varStatus="status">
                            <div class="p-5 hover:bg-gray-50">
                                <div class="flex justify-between">
                                    <div>
                                        <h3 class="text-base font-semibold text-gray-800">
                                            <c:out value="${event.eventCity.name}" />
                                        </h3>
                                        <p class="text-sm text-gray-600 mt-1">
                                            <c:out value="${event.date}" />
                                        </p>
                                        <p class="text-sm text-gray-500 mt-1 line-clamp-2">
                                            <c:out value="${event.description}" />
                                        </p>
                                    </div>
                                    <a href="<c:url>/events/${event.id}"/>" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-700">
                                        <spring:message code="dashboard.details" text="Details"/>
                                        <svg class="ml-1 w-4 h-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                            <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                        </svg>
                                    </a>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty recommendedEvents}">
                            <div class="p-5 text-center">
                                <p class="text-gray-500">
                                    <spring:message code="dashboard.no.events" text="No recommended events available"/>
                                </p>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>