<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
  <title>Events</title>
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
  <div class="mx-auto">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
        All Events
      </h2>
      <a href="${pageContext.request.contextPath}/events/create" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
        Create Event
      </a>
    </div>

    <!-- Events List -->
    <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <c:forEach items="${events}" var="event">
        <a href="${pageContext.request.contextPath}/events/${event.id}" class="group block rounded-xl p-4 sm:p-6 bg-white border border-gray-200 hover:border-blue-600">
          <!-- Image -->
          <c:if test="${not empty event.flyerImageId}">
            <img src="${pageContext.request.contextPath}/images/${event.flyerImageId}" alt="Event Flyer" class="w-full h-40 object-cover rounded-lg">
          </c:if>

          <div class="flex items-center gap-x-4 mt-3">
            <div>
              <h3 class="text-lg font-semibold text-gray-800">${event.eventCity.name}</h3>
              <p class="text-sm text-gray-500">${event.date}</p>
            </div>
          </div>
          <p class="mt-3 text-gray-600 line-clamp-3">${event.description}</p>
        </a>
      </c:forEach>

      <c:if test="${empty events}">
        <div class="col-span-full text-center py-10">
          <p class="text-gray-500">No events found. Be the first to create one!</p>
        </div>
      </c:if>
    </div>
    <!-- End Events List -->
  </div>
</div>

</body>
</html>
