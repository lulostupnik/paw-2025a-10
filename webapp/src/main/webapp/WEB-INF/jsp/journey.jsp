<%--
  Created by IntelliJ IDEA.
  User: fer
  Date: 27/3/25
  Time: 15:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title><spring:message code="createJourney.title"/></title>
    <!-- Include Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
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
            <spring:message code="createJourney.back" text="Back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <div class="text-center">
            <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                <spring:message code="createJourney.title"/>
            </h2>
        </div>

        <!-- Card -->
        <div class="mt-5 p-4 relative z-10 bg-white border border-gray-200 rounded-xl sm:mt-10 md:p-10">
            <c:url var="registerUrl" value="/journey/"/>
            <form:form modelAttribute="createJourneyForm" action="${registerUrl}" method="post">

                <!-- Email Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="email" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.userEmail"/>
                    </form:label>
                    <spring:message code="createJourney.userEmail.hint" var="emailHint"/>
                    <form:input path="email" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${emailHint}"/>
                    <form:errors path="email" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Start Date Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="startDate" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.startDate"/>
                    </form:label>
                    <spring:message code="createJourney.startDate.hint" var="startDateHint"/>
                    <form:input type="date" path="startDate" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${startDateHint}"/>
                    <form:errors path="startDate" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- End Date Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="endDate" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.endDate"/>
                    </form:label>
                    <spring:message code="createJourney.endDate.hint" var="endDateHint"/>
                    <form:input type="date" path="endDate" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${endDateHint}"/>
                    <form:errors path="endDate" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Destination City Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="destinationCity" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.destinationCity"/>
                    </form:label>
                    <spring:message code="createJourney.destinationCity.hint" var="destinationCityHint"/>
                    <form:input path="destinationCity" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${destinationCityHint}"/>
                    <form:errors path="destinationCity" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Destination University Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="destinationUniversity" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.destinationUniversity"/>
                    </form:label>
                    <spring:message code="createJourney.destinationUniversity.hint" var="destinationUniversityHint"/>
                    <form:input path="destinationUniversity" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${destinationUniversityHint}"/>
                    <form:errors path="destinationUniversity" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Description Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="description" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.description"/>
                    </form:label>
                    <spring:message code="createJourney.description.hint" var="descriptionHint"/>
                    <form:textarea path="description" rows="4" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                   placeholder="${descriptionHint}"/>
                    <form:errors path="description" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Submit Button -->
                <div class="mt-6 grid">
                    <spring:message code="createJourney.submit" var="submit"/>
                    <button type="submit" class="w-full py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2">
                            ${submit}
                    </button>
                </div>
            </form:form>
        </div>
        <!-- End Card -->
    </div>
</div>

<!-- Include Preline JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.js"></script>
</body>
</html>