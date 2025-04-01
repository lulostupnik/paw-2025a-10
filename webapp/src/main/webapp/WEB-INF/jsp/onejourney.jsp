<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        <a href="${pageContext.request.contextPath}/journey" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
            <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <!-- Journey Details Card -->
        <c:if test="${journey.present}">
            <div class="p-4 sm:p-6 lg:p-8 bg-white border border-gray-200 rounded-xl">
                <!-- User Info -->
                <div class="mb-6">
                    <h2 class="text-2xl font-bold text-gray-800">${journey.get().user.firstname} ${journey.get().user.lastname}</h2>
                    <p class="text-sm text-gray-500">
                        <spring:message code="journey.contact"/>: ${journey.get().user.email}
                    </p>
                </div>

                <!-- Destination Info -->
                <div class="mb-6">
                    <h3 class="text-lg font-semibold mb-2"><spring:message code="journey.destination"/></h3>
                    <div class="flex flex-wrap gap-2">
            <span class="inline-flex items-center gap-x-1.5 py-1.5 px-3 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
              <spring:message code="journey.city"/>: ${journey.get().destinationCity}
            </span>
                        <span class="inline-flex items-center gap-x-1.5 py-1.5 px-3 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
              <spring:message code="journey.university"/>: ${journey.get().destinationUniversity}
            </span>
                    </div>
                </div>

                <!-- Date Info -->
                <div class="mb-6">
                    <h3 class="text-lg font-semibold mb-2"><spring:message code="journey.dates"/></h3>
                    <div class="flex items-center gap-x-2">
            <span class="text-gray-700">
              <fmt:formatDate value="${journey.get().startDate}" pattern="MMMM d, yyyy" />
            </span>
                        <svg class="w-4 h-4 text-gray-400" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                            <path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h11.793l-3.147-3.146a.5.5 0 0 1 .708-.708l4 4a.5.5 0 0 1 0 .708l-4 4a.5.5 0 0 1-.708-.708L13.293 8.5H1.5A.5.5 0 0 1 1 8z"/>
                        </svg>
                        <span class="text-gray-700">
              <fmt:formatDate value="${journey.get().endDate}" pattern="MMMM d, yyyy" />
            </span>
                    </div>
                </div>

                <!-- Description -->
                <div class="mb-6">
                    <h3 class="text-lg font-semibold mb-2"><spring:message code="journey.description"/></h3>
                    <p class="text-gray-700">${journey.get().description}</p>
                </div>

                <!-- Reply Button -->
                <div class="mt-8">
                    <a href="${pageContext.request.contextPath}/journey/${journey.get().id}/reply"
                       class="py-3 px-4 inline-flex justify-center items-center gap-2 rounded-md border border-transparent font-semibold bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 transition-all text-sm">
                        <spring:message code="journey.reply.button"/>
                    </a>
                </div>
            </div>
        </c:if>

        <!-- If journey not found -->
        <c:if test="${!journey.present}">
            <div class="p-4 sm:p-6 lg:p-8 bg-white border border-gray-200 rounded-xl text-center">
                <div class="mb-6">
                    <svg class="w-12 h-12 text-gray-400 mx-auto" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                        <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                    </svg>
                    <h2 class="mt-4 text-2xl font-bold text-gray-800"><spring:message code="journey.not.found.title"/></h2>
                    <p class="mt-2 text-gray-600"><spring:message code="journey.not.found.message"/></p>
                </div>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>