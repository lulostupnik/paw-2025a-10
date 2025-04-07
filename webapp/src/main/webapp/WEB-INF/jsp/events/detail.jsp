<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title></title>
  <!-- Include Tailwind CSS -->
  <script src="https://cdn.tailwindcss.com"></script>
  <!-- Include Preline UI Kit CSS -->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body class="bg-gray-100">

<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
  <!-- Back Link - Moved to top left -->
  <div class="mb-6">
    <a href="<c:url value="/events/"/>" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
      <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="event.back"/>
    </a>
  </div>

  <div class="mx-auto max-w-2xl">
    <!-- Event Details Card -->
      <div class="p-4 sm:p-6 lg:p-8 bg-white border border-gray-200 rounded-xl relative">
        <!-- Reply Button in Top Right -->
        <div class="absolute top-4 right-4">
          <a href="<c:url value=" ${pageContext.request.contextPath}/event/${event.id}/reply"/>"
             class="py-2 px-4 inline-flex justify-center items-center gap-2 rounded-md border border-transparent font-semibold bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2 transition-all text-sm">
            <spring:message code="event.reply.button"/>
          </a>
        </div>

        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800"><c:out value="${event.eventCity.name}"/></h2>
          <p class="text-sm text-gray-500">
            <fmt:formatDate value="${event.date}" pattern="MMMM d, yyyy" />
          </p>
        </div>

        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Description</h3>
          <p class="text-gray-700"><c:out value="${event.description}"/></p>
        </div>

        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Contact</h3>
          <p class="text-gray-700"><c:out value="${event.user.email}"/></p>
        </div>

        <div class="mb-6">
          <h3 class="text-lg font-semibold mb-2">Flyer Image</h3>
          <!-- Display the flyer image if available -->
          <c:if test="${not empty event.flyerImageId}">
            <img src="<c:url value="${pageContext.request.contextPath}/images/${event.flyerImageId}"/>" alt="Event Flyer" class="w-full h-auto rounded-lg" />
          </c:if>
        </div>
      </div>
  </div>
</div>

</body>
</html>
