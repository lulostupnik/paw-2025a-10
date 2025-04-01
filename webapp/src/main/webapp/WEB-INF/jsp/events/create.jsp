<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title><spring:message code="event.create.title"/></title>
  <!-- Include Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- Include Preline UI Kit CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body class="bg-gray-100">

<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
  <!-- Back Link -->
  <div class="mb-6">
    <a href="${pageContext.request.contextPath}/events" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
      <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="event.back"/>
    </a>
  </div>

  <div class="mx-auto max-w-2xl">
    <div class="text-center">
      <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
        <spring:message code="event.create.header"/>
      </h2>
    </div>

    <!-- Card -->
    <div class="mt-5 p-4 relative z-10 bg-white border border-gray-200 rounded-xl sm:mt-10 md:p-10">
      <form:form method="post" action="${pageContext.request.contextPath}/events" modelAttribute="createEventForm" enctype="multipart/form-data">

        <!-- Email Field -->
        <spring:message code="event.email.hint" var="emailHint"/>
        <div class="mb-4 sm:mb-8">
          <form:label path="email" class="block mb-2 text-sm font-medium">
            <spring:message code="event.email"/>
          </form:label>
          <form:input path="email" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${emailHint}" />
          <form:errors path="email" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- City Field -->
        <spring:message code="event.city.hint" var="cityHint"/>
        <div class="mb-4 sm:mb-8">
          <form:label path="city" class="block mb-2 text-sm font-medium">
            <spring:message code="event.city"/>
          </form:label>
          <form:input path="city" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${cityHint}" />
          <form:errors path="city" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Date Field -->
        <div class="mb-4 sm:mb-8">
          <form:label path="date" class="block mb-2 text-sm font-medium">
            <spring:message code="event.date"/>
          </form:label>
          <form:input path="date" type="date" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500" />
          <form:errors path="date" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Flyer Field -->
        <div class="mb-4 sm:mb-8">
          <form:label path="flyer" class="block mb-2 text-sm font-medium">
            <spring:message code="event.flyer"/>
          </form:label>
          <form:input path="flyer" type="file" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500" />
          <form:errors path="flyer" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Description Field -->
        <spring:message code="event.description.hint" var="descriptionHint"/>
        <div class="mb-4 sm:mb-8">
          <form:label path="description" class="block mb-2 text-sm font-medium">
            <spring:message code="event.description"/>
          </form:label>
          <form:textarea path="description" rows="4" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                         placeholder="${descriptionHint}" />
          <form:errors path="description" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Submit Button -->
        <spring:message code="event.create.button" var="submitButton"/>
        <div class="mt-6 grid">
          <button type="submit" class="w-full py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2">
              ${submitButton}
          </button>
        </div>
      </form:form>
    </div>
    <!-- End Card -->
  </div>
</div>

</body>
</html>
