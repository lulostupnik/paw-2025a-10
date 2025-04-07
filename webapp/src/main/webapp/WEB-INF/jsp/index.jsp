<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="app.title"/></title>
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
            <div class="mx-auto mb-10">
                <h1 class="text-2xl font-bold text-gray-900 sm:text-3xl mb-2">
                    <spring:message code="dashboard.welcome"/>
                </h1>
                <p class="text-gray-600 max-w-3xl">
                    <spring:message code="dashboard.intro"/>
                </p>
            </div>

            <!-- Content Grid -->
            <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
                <!-- Newest Journeys -->
                <div class="bg-white rounded-xl border border-gray-200 shadow-sm">
                    <div class="p-5 border-b border-gray-200">
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">
                                <spring:message code="dashboard.newest.journeys"/>
                            </h2>
                            <a href="<c:url value='/journeys'/>" class="text-sm font-medium text-blue-600 hover:text-blue-700">
                                <spring:message code="dashboard.view.all" />
                            </a>
                        </div>
                        <p class="text-sm text-gray-500 mt-1">
                            <spring:message code="dashboard.newest.journeys.desc"/>
                        </p>
                    </div>

                    <!-- Journey List -->
                    <div class="divide-y divide-gray-200">
                        <c:if test="${empty journeys}">
                            <div class="p-5 text-center">
                                <p class="text-gray-500">
                                    <spring:message code="dashboard.no.journeys"/>
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty journeys}">
                            <c:forEach var="journey" items="${journeys}" varStatus="status">
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
                                        <a href="<c:url value='/journeys/${journey.id}'/>" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-700">
                                            <spring:message code="dashboard.details"/>
                                            <svg class="ml-1 w-4 h-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                            </svg>
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>

                <!-- Newest Events -->
                <div class="bg-white rounded-xl border border-gray-200 shadow-sm">
                    <div class="p-5 border-b border-gray-200">
                        <div class="flex justify-between items-center">
                            <h2 class="text-lg font-semibold text-gray-800">
                                <spring:message code="dashboard.newest.events"/>
                            </h2>
                            <a href="<c:url value='/events'/>" class="text-sm font-medium text-blue-600 hover:text-blue-700">
                                <spring:message code="dashboard.view.all"/>
                            </a>
                        </div>
                        <p class="text-sm text-gray-500 mt-1">
                            <spring:message code="dashboard.newest.events.desc" />
                        </p>
                    </div>

                    <!-- Events List -->
                    <div class="divide-y divide-gray-200">
                        <c:if test="${empty events}">
                            <div class="p-5 text-center">
                                <p class="text-gray-500">
                                    <spring:message code="dashboard.no.events"/>
                                </p>
                            </div>
                        </c:if>

                        <c:if test="${not empty events}">
                            <c:forEach var="event" items="${events}" varStatus="status">
                                <div class="p-5 hover:bg-gray-50">
                                    <div class="flex justify-between">
                                        <div>
                                            <h3 class="text-base font-semibold text-gray-800">
                                                <c:out value="${event.eventCity}" />
                                            </h3>
                                            <p class="text-sm text-gray-600 mt-1">
                                                 <c:out value="${event.date}" />
                                            </p>
                                            <p class="text-sm text-gray-500 mt-1 line-clamp-2">
                                                <c:out value="${event.description}" />
                                            </p>
                                        </div>
                                        <a href="<c:url value='/events/${event.id}'/>" class="inline-flex items-center text-sm font-medium text-blue-600 hover:text-blue-700">
                                            <spring:message code="dashboard.details"/>
                                            <svg class="ml-1 w-4 h-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor">
                                                <path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd" />
                                            </svg>
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Include Preline JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.js"></script>
</body>
</html>