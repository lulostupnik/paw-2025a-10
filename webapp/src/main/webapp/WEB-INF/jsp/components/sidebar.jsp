<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link href="<c:url value='/resources/css/sidebar.css' />" rel="stylesheet"/>

<c:url var="homeUrl" value="/" />
<c:url var="journeysUrl" value="/journeys" />
<c:url var="eventsUrl" value="/events" />
<c:set var="uri" value="${requestScope['javax.servlet.forward.request_uri'] != null
                          ? requestScope['javax.servlet.forward.request_uri']
                          : request.requestURI}" />

<!-- Sidebar -->
<div class="sidebar">
  <!-- Logo / Brand -->
  <div class="sidebar-brand">
    <h1 class="sidebar-title">
      <spring:message code="app.name" />
    </h1>
  </div>

  <!-- Navigation -->
  <nav class="sidebar-nav">
    <div class="sidebar-nav-container">
      <!-- Home -->
      <a href="<c:out value='${homeUrl}'/>"
         class="sidebar-nav-item ${uri == homeUrl ? 'active' : ''}">
        <img src="<c:url value='/resources/icons/estate.svg'/>" alt="" class="sidebar-icon" />
        <spring:message code="nav.home" />
      </a>

      <!-- Journeys -->
      <a href="<c:out value='${journeysUrl}'/>"
         class="sidebar-nav-item ${fn:startsWith(uri, journeysUrl) ? 'active' : ''}">
        <img src="<c:url value='/resources/icons/map.svg'/>" alt="" class="sidebar-icon" />
        <spring:message code="nav.journeys" />
      </a>

      <!-- Events -->
      <a href="<c:out value='${eventsUrl}'/>"
         class="sidebar-nav-item ${fn:startsWith(uri, eventsUrl) ? 'active' : ''}">
        <img src="<c:url value='/resources/icons/calender.svg'/>" alt="" class="sidebar-icon" />

        <spring:message code="nav.events"/>
      </a>
    </div>
  </nav>
</div>