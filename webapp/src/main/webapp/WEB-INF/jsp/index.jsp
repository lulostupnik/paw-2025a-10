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
<body class="bg-gray-50">
<div class="flex h-full min-h-screen">
    <!-- Sidebar -->
    <div class="flex flex-col bg-white border-r border-gray-200 w-64 fixed h-full">
        <!-- Logo / Brand -->
        <div class="px-6 py-4 border-b border-gray-200">
            <h1 class="text-xl font-semibold text-gray-800">
                <spring:message code="app.name" text="Exchange Platform"/>
            </h1>
            <p class="text-sm text-gray-500 mt-1">
                <spring:message code="app.welcome" text="Welcome"/> <c:out value="${user.email}" escapeXml="true"/>!
            </p>
        </div>

        <!-- Navigation -->
        <nav class="flex-1 overflow-auto py-4">
            <div class="px-3 space-y-1">
                <!-- Home -->
                <a href="${pageContext.request.contextPath}/" class="flex items-center px-3 py-2 text-sm font-medium rounded-md bg-blue-50 text-blue-700">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
                    </svg>
                    <spring:message code="nav.home" text="Home"/>
                </a>

                <!-- Journeys -->
                <a href="${pageContext.request.contextPath}/journeys" class="flex items-center px-3 py-2 text-sm font-medium rounded-md text-gray-700 hover:bg-gray-100">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
                    </svg>
                    <spring:message code="nav.journeys" text="Journeys"/>
                </a>

                <!-- Events -->
                <a href="${pageContext.request.contextPath}/events" class="flex items-center px-3 py-2 text-sm font-medium rounded-md text-gray-700 hover:bg-gray-100">
                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <spring:message code="nav.events" text="Events"/>
                </a>

                <!-- Profile -->
<%--                <a href="${pageContext.request.contextPath}/profile" class="flex items-center px-3 py-2 text-sm font-medium rounded-md text-gray-700 hover:bg-gray-100">--%>
<%--                    <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">--%>
<%--                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />--%>
<%--                    </svg>--%>
<%--                    <spring:message code="nav.profile" text="Profile"/>--%>
<%--                </a>--%>
            </div>
        </nav>

        <!-- Footer -->
<%--        <div class="px-6 py-4 border-t border-gray-200">--%>
<%--            <a href="${pageContext.request.contextPath}/logout" class="flex items-center text-sm font-medium text-red-600 hover:text-red-700">--%>
<%--                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">--%>
<%--                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />--%>
<%--                </svg>--%>
<%--                <spring:message code="nav.logout" text="Logout"/>--%>
<%--            </a>--%>
<%--        </div>--%>
    </div>

    <!-- Main Content -->
    <div class="ml-64 flex-1 p-6">
        <div class="max-w-7xl mx-auto">
            <!-- Welcome Header -->
            <div class="mb-8">
                <h1 class="text-2xl font-bold text-gray-900 sm:text-3xl">
                    <spring:message code="dashboard.welcome" text="Welcome to the Exchange Platform"/>
                </h1>
                <p class="mt-2 text-lg text-gray-600">
                    <spring:message code="dashboard.subtitle" text="Discover journeys and events from students around the world"/>
                </p>
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
                                    <a href="${pageContext.request.contextPath}/journey/${journey.id}" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-700">
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
                            <a href="${pageContext.request.contextPath}/events" class="text-sm font-medium text-blue-600 hover:text-blue-700">
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
                                    <a href="${pageContext.request.contextPath}/events/${event.id}" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-700">
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

