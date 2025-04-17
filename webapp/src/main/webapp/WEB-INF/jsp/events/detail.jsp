<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
  <title>Event Details</title>
  <!-- Include necessary CSS files -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/event-details.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>

<div class="container">
  <!-- Back Link - Moved to top left -->
  <div class="mb-6">
    <a href="<c:url value="/events/"/>" class="back-link">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="event.back"/>
    </a>
  </div>

  <div class="content-container">
    <!-- Event Details Component with Reply Button -->
    <jsp:include page="event-details-full.jsp">
      <jsp:param name="showReplyButton" value="true" />
    </jsp:include>
  </div>
</div>

</body>
</html>