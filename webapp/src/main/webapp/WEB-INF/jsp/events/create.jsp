<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
  <title>Create New Event</title>
  <!-- Include Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- Include Preline UI Kit CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body class="bg-gray-100">

<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
  <div class="mx-auto max-w-2xl">
    <div class="text-center">
      <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
        Create a New Event
      </h2>
    </div>

    <!-- Card -->
    <div class="mt-5 p-4 relative z-10 bg-white border border-gray-200 rounded-xl sm:mt-10 md:p-10">
      <form:form method="post" action="${pageContext.request.contextPath}/events" modelAttribute="createEventForm">

        <!-- Email Field -->
        <div class="mb-4 sm:mb-8">
          <form:label path="email" class="block mb-2 text-sm font-medium">Email address</form:label>
          <form:input path="email" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500" placeholder="Enter your email" />
          <form:errors path="email" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- City Field -->
        <div class="mb-4 sm:mb-8">
          <form:label path="city" class="block mb-2 text-sm font-medium">City</form:label>
          <form:input path="city" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500" placeholder="Enter event city" />
          <form:errors path="city" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Date Field -->
        <div class="mb-4 sm:mb-8">
          <form:label path="date" class="block mb-2 text-sm font-medium">Event Date</form:label>
          <form:input path="date" type="date" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500" />
          <form:errors path="date" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Description Field -->
        <div class="mb-4 sm:mb-8">
          <form:label path="description" class="block mb-2 text-sm font-medium">Description</form:label>
          <form:textarea path="description" rows="4" class="py-2.5 sm:py-3 px-4 block w-full border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500" placeholder="Describe your event" />
          <form:errors path="description" class="text-red-500 text-sm mt-1" />
        </div>

        <!-- Submit Button -->
        <div class="mt-6 grid">
          <button type="submit" class="w-full py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2">
            Create Event
          </button>
        </div>
      </form:form>
    </div>
    <!-- End Card -->
  </div>
</div>

</body>
</html>