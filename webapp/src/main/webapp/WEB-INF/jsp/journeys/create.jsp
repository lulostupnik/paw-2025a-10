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
    <!-- Awesomplete CSS & JS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/awesomplete/1.1.5/awesomplete.min.css" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/awesomplete/1.1.5/awesomplete.min.js"></script>

</head>
<body class="bg-gray-100">

<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
    <!-- Back Link -->
    <div class="mb-6">
        <a href="${pageContext.request.contextPath}/journeys" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
            <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back" text="Back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <div class="text-center">
            <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                <spring:message code="createJourney.title"/>
            </h2>
        </div>

        <!-- Create JavaScript arrays for our autocomplete options -->
        <script>
            // City options
            var cityOptions = [
                <c:forEach var="city" items="${cities}" varStatus="status">
                "${city.name}"<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];

            var universityOptions = [
                <c:forEach var="university" items="${universities}" varStatus="status">
                "${university.name}"<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ];

        </script>

        <!-- Card -->
        <div class="mt-5 p-4 relative z-10 bg-white border border-gray-200 rounded-xl sm:mt-10 md:p-10">
            <c:url var="registerUrl" value="/journeys/create"/>
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
                <c:set var="cityIcon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="w-4 h-4" viewBox="0 0 16 16">
                        <path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/>
                    </svg>
                </c:set>

                <spring:message code="createJourney.destinationCity.hint" var="cityHint" text="Enter city name"/>

                <jsp:include page="/WEB-INF/jsp/components/autocomplete.jsp">
                    <jsp:param name="path" value="destinationCity" />
                    <jsp:param name="label" value="destinationCity" />
                    <jsp:param name="placeholder" value="${cityHint}" />
                    <jsp:param name="hint" value="Start typing to see matching cities" />
                    <jsp:param name="icon" value="${cityIcon}" />
                    <jsp:param name="listVar" value="cityOptions" />
                    <jsp:param name="required" value="true" />
                </jsp:include>

                <!-- Destination University Field - Also using autocomplete -->
                <c:set var="universityIcon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="w-4 h-4" viewBox="0 0 16 16">
                        <path d="M8.211 2.047a.5.5 0 0 0-.422 0l-7.5 3.5a.5.5 0 0 0 .025.917l7.5 3a.5.5 0 0 0 .372 0L14 7.14V13a1 1 0 0 0-1 1v2h3v-2a1 1 0 0 0-1-1V6.739l.686-.275a.5.5 0 0 0 .025-.917l-7.5-3.5Z"/>
                        <path d="M4.176 9.032a.5.5 0 0 0-.656.327l-.5 1.7a.5.5 0 0 0 .294.605l4.5 1.8a.5.5 0 0 0 .372 0l4.5-1.8a.5.5 0 0 0 .294-.605l-.5-1.7a.5.5 0 0 0-.656-.327L8 10.466 4.176 9.032Z"/>
                    </svg>
                </c:set>

                <spring:message code="createJourney.destinationUniversity.hint" var="universityHint"/>

                <jsp:include page="/WEB-INF/jsp/components/autocomplete.jsp">
                    <jsp:param name="path" value="destinationUniversity" />
                    <jsp:param name="label" value="destinationUniversity" />
                    <jsp:param name="placeholder" value="${universityHint}" />
                    <jsp:param name="hint" value="Start typing to see matching universities" />
                    <jsp:param name="icon" value="${universityIcon}" />
                    <jsp:param name="listVar" value="universityOptions" />
                    <jsp:param name="required" value="true" />
                </jsp:include>

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

<!-- Preline JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.js"></script>
</body>
</html>