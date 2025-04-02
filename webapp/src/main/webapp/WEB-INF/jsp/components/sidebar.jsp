<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Get the current URI to determine which nav item should be active --%>
<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<!-- Sidebar -->
<div class="flex flex-col bg-white border-r border-gray-200 w-64 fixed h-full">
  <!-- Logo / Brand -->
  <div class="px-6 py-4 border-b border-gray-200">
    <h1 class="text-xl font-semibold text-gray-800">
      <spring:message code="app.name" text="Exchange Platform"/>
    </h1>
    <p class="text-sm text-gray-500 mt-1">
      <spring:message code="app.welcome" text="Welcome"/> <c:out value="${user.email}" escapeXml="true"/>!
    </p>
  </div>

  <!-- Navigation -->
  <nav class="flex-1 overflow-auto py-4">
    <div class="px-3 space-y-1">
      <!-- Home -->
      <a href="${pageContext.request.contextPath}/"
         class="flex items-center px-3 py-2 text-sm font-medium rounded-md
                     ${fn:endsWith(currentUri, '/') || fn:endsWith(currentUri, '/dashboard') || fn:endsWith(currentUri, '/index.jsp') ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'}">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
        </svg>
        <spring:message code="nav.home" text="Home"/>
      </a>

      <!-- Journeys -->
      <a href="${pageContext.request.contextPath}/journeys"
         class="flex items-center px-3 py-2 text-sm font-medium rounded-md
                     ${fn:contains(currentUri, '/journey') ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'}">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
        </svg>
        <spring:message code="nav.journeys" text="Journeys"/>
      </a>

      <!-- Events -->
      <a href="${pageContext.request.contextPath}/events"
         class="flex items-center px-3 py-2 text-sm font-medium rounded-md
                     ${fn:contains(currentUri, '/events') ? 'bg-blue-50 text-blue-700' : 'text-gray-700 hover:bg-gray-100'}">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
        </svg>
        <spring:message code="nav.events" text="Events"/>
      </a>

      <!-- Uncomment when profile page is ready -->
      <%-- <a href="${pageContext.request.contextPath}/profile" class="flex items-center px-3 py-2 text-sm font-medium rounded-md text-gray-700 hover:bg-gray-100">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
          </svg>
          <spring:message code="nav.profile" text="Profile"/>
      </a> --%>
    </div>
  </nav>

  <!-- Footer - Uncomment when logout functionality is ready -->
  <%-- <div class="px-6 py-4 border-t border-gray-200">
      <a href="${pageContext.request.contextPath}/logout" class="flex items-center text-sm font-medium text-red-600 hover:text-red-700">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
          </svg>
          <spring:message code="nav.logout" text="Logout"/>
      </a>
  </div> --%>
</div>