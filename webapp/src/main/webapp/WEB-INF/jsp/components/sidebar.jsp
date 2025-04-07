<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:url var="homeUrl" value="/" />
<c:url var="journeysUrl" value="/journeys" />
<c:url var="eventsUrl" value="/events" />
<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri'] != null
                          ? requestScope['javax.servlet.forward.request_uri']
                          : request.requestURI}" />

<!-- Sidebar -->
<div class="flex flex-col bg-white border-r border-gray-200 w-64 fixed h-full">
  <!-- Logo / Brand -->
  <div class="px-6 py-4 border-b border-gray-200">
    <h1 class="text-xl font-semibold text-gray-800">
      <spring:message code="app.name" />
    </h1>
  </div>

  <!-- Navigation -->
  <nav class="flex-1 overflow-auto py-4">
    <div class="px-3 space-y-1">
      <!-- Home -->
      <a href="<c:out value='${homeUrl}'/>"
         class="flex items-center px-3 py-2 text-sm font-medium rounded-md ${uri == homeUrl ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'}">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
        <spring:message code="nav.home" />
      </a>

      <!-- Journeys -->
      <a href="<c:out value='${journeysUrl}'/>"
         class="flex items-center px-3 py-2 text-sm font-medium rounded-md ${fn:startsWith(uri, journeysUrl) ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'}">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
        </svg>
        <spring:message code="nav.journeys" />
      </a>

      <!-- Events -->
      <a href="<c:out value='${eventsUrl}'/>"
         class="flex items-center px-3 py-2 text-sm font-medium rounded-md ${fn:startsWith(uri, eventsUrl) ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'}">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <spring:message code="nav.events"/>
      </a>
    </div>
  </nav>
</div>
