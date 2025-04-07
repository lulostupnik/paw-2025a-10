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
          <a href="<c:url value="/events/create"/>" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
            <spring:message code="event.create.button"/>
          </a>
        </div>

        <!-- Events List with vertical scrolling -->
        <div class="max-h-[600px] overflow-y-auto pb-6 pr-2">
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            <c:forEach items="${events}" var="event">
              <!-- Card with buttons below -->
              <div class="flex-shrink-0">
                <div class="rounded-xl bg-white border border-gray-200 hover:border-blue-600 transition-all duration-200 h-full">
                  <div class="p-4 sm:p-6">
                    <!-- Image -->
                    <c:if test="${not empty event.flyerImageId}">
                      <img src="<c:url value=" ${pageContext.request.contextPath}/images/${event.flyerImageId}"/>" alt="<spring:message code='event.flyer.alt'/>" class="w-full h-40 object-cover rounded-lg">
                    </c:if>

                    <div class="flex items-center gap-x-4 mt-3">
                      <div>
                        <h3 class="text-lg font-semibold text-gray-800">
                          <spring:message code="event.city"/><c:url value=" ${event.eventCity.name}"/>
                        </h3>
                        <p class="text-sm text-gray-500">
                          <spring:message code="event.date"/> <c:url value=" ${event.date}"/>
                        </p>
                      </div>
                    </div>
                    <p class="mt-3 text-gray-600 line-clamp-3">
                      <spring:message code="event.description"/> <c:url value=" ${event.description}"/>
                    </p>

                    <!-- Action Buttons -->
                    <div class="mt-4 flex gap-2">
                      <a href="<c:url value="/events/${event.id}"/>"
                         class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
                        <spring:message code="event.view.details" text="View Details"/>
                      </a>
                      <a href="<c:url value="/events/${event.id}/reply"/>"
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

        <!-- Scroll indicator - removed horizontal dots since we're now using vertical scroll -->
      </div>
    </div>
  </div>
</div>

<!-- Include Preline UI Kit JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.js"></script>

</body>
</html>