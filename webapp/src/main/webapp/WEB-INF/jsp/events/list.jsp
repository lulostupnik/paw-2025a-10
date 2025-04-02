<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title><spring:message code="event.page.title"/></title>
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

<div class="flex h-full min-h-screen">
  <!-- Include the sidebar component -->
  <jsp:include page="../components/sidebar.jsp" />

  <!-- Main Content -->
  <div class="ml-64 flex-1">
    <div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
      <div class="mx-auto">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
            <spring:message code="event.list.title"/>
          </h2>
          <a href="${pageContext.request.contextPath}/events/create" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
            <spring:message code="event.create.button"/>
          </a>
        </div>

        <!-- Events List with horizontal scrolling -->
        <div class="overflow-x-auto pb-6">
          <div class="inline-flex gap-6 min-w-full">
            <c:forEach items="${events}" var="event">
              <!-- Card with buttons below -->
              <div class="w-80 flex-shrink-0">
                <div class="rounded-xl bg-white border border-gray-200 hover:border-blue-600 transition-all duration-200 h-full">
                  <div class="p-4 sm:p-6">
                    <!-- Image -->
                    <c:if test="${not empty event.flyerImageId}">
                      <img src="${pageContext.request.contextPath}/images/${event.flyerImageId}" alt="<spring:message code='event.flyer.alt'/>" class="w-full h-40 object-cover rounded-lg">
                    </c:if>

                    <div class="flex items-center gap-x-4 mt-3">
                      <div>
                        <h3 class="text-lg font-semibold text-gray-800">
                          <spring:message code="event.city"/>: ${event.eventCity.name}
                        </h3>
                        <p class="text-sm text-gray-500">
                          <spring:message code="event.date"/>: ${event.date}
                        </p>
                      </div>
                    </div>
                    <p class="mt-3 text-gray-600 line-clamp-3">
                      <spring:message code="event.description"/>: ${event.description}
                    </p>

                    <!-- Action Buttons -->
                    <div class="mt-4 flex gap-2">
                      <a href="${pageContext.request.contextPath}/events/${event.id}"
                         class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
                        <spring:message code="event.view.details" text="View Details"/>
                      </a>
                      <a href="${pageContext.request.contextPath}/events/${event.id}/reply"
                         class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
                        <spring:message code="event.reply" text="Reply"/>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
            </c:forEach>

            <c:if test="${empty events}">
              <div class="col-span-full text-center py-10">
                <p class="text-gray-500"><spring:message code="event.no.events"/></p>
              </div>
            </c:if>
          </div>
        </div>
        <!-- End Events List -->

        <!-- Scroll indicator -->
        <div class="mt-4 flex justify-center gap-1">
          <span class="block w-2 h-2 rounded-full bg-blue-600"></span>
          <span class="block w-2 h-2 rounded-full bg-gray-300"></span>
          <span class="block w-2 h-2 rounded-full bg-gray-300"></span>
        </div>
      </div>
    </div>
  </div>
</div>

</body>
</html>