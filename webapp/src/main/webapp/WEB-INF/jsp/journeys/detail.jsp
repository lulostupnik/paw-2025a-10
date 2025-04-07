<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="journey.detail.title"/></title>
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

<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
    <!-- Back Link -->
    <div class="mb-6">
        <a href="${pageContext.request.contextPath}/journeys" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
            <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <!-- Journey Details Card -->
            <div class="p-4 sm:p-6 lg:p-8 bg-white border border-gray-200 rounded-xl">
                <!-- User Info -->
                <div class="mb-6">
                    <h2 class="text-2xl font-bold text-gray-800"><c:out value="${journey.user.firstname} ${journey.user.lastname}"/></h2>
                    <p class="text-sm text-gray-500">
                        <spring:message code="journey.contact"/><c:out value="${journey.user.email}"/>
                    </p>
                </div>

                <!-- Destination Info -->
                <div class="mb-6">
                    <h3 class="text-lg font-semibold mb-2"><spring:message code="journey.destination"/></h3>
                    <div class="flex flex-wrap gap-2">
                        <span class="inline-flex items-center gap-x-1.5 py-1.5 px-3 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                            <spring:message code="journey.city"/><c:out value=" ${journey.destinationCity}"/>
                        </span>
                        <span class="inline-flex items-center gap-x-1.5 py-1.5 px-3 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                            <spring:message code="journey.university"/> <c:out value=" ${journey.destinationUniversity.name}"/>
                        </span>
                    </div>
                </div>

                <!-- Date Info -->
                <div class="mb-6">
                    <h3 class="text-lg font-semibold mb-2"><spring:message code="journey.dates"/></h3>
                    <div class="flex items-center gap-x-2">
                        <p class="text-gray-500 text-base"><c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" /></p>
                    </div>
                </div>

                <!-- Description -->
                <div class="mb-6">
                    <h3 class="text-lg font-semibold mb-2"><spring:message code="journey.description"/></h3>
                    <p class="text-gray-700"><c:out value="${journey.description}"/></p>
                </div>

                <!-- Reply Button -->
                <div class="mt-8">
                    <a href="<c:url value ="/journeys/${journey.id}/reply"/>"
                       class="py-3 px-4 inline-flex justify-center items-center gap-2 rounded-md border border-transparent font-semibold bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 transition-all text-sm">
                        <spring:message code="journey.reply.button"/>
                    </a>
                </div>
            </div>


    </div>
</div>

</body>
</html>
