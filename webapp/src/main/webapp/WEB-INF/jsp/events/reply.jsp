<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <title><spring:message code="replyEvent.title"/></title>
  <!-- Include custom CSS -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>

<div class="container">
  <!-- Back Link -->
  <div class="mb-6">
    <button type="button" name="back" class="back-link" onClick="history.back()">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="event.back"/>
    </button>
  </div>

  <div class="mx-auto max-w-2xl">
    <div class="text-center mb-6">
      <h2 class="header">
        <spring:message code="replyEvent.title" />
      </h2>
      <p class="event-subtitle">
        <c:set var="eventDate"><c:out value="${event.date}"/></c:set>
        <spring:message code="replyEvent.subtitle" arguments="<${event.eventCity.name},${eventDate}" />
      </p>
    </div>

    <!-- Reply Form Card -->
    <div class="card">
<%--      <c:url var="replyUrl" value="/events/${event.id}/reply"/>--%>
      <c:url var="replyUrl" value="/events/${event.id}/reply">
        <c:param name="page" value="${param.page}" />
        <c:param name="size" value="${param.size}" />
        <c:param name="attendeesPage" value="${param.attendeesPage}" />
        <c:param name="attendeesSize" value="${param.attendeesSize}" />
      </c:url>
      <form:form modelAttribute="replyEventForm" action="${replyUrl}" method="post" enctype="multipart/form-data">

        <!-- Message Field -->
        <c:set var="messageLabel"><spring:message code="reply.message"/></c:set>
        <c:set var="messageHint"><spring:message code="reply.message.hint"/></c:set>
        <jsp:include page="../components/text-area.jsp">
          <jsp:param name="path" value="message" />
          <jsp:param name="label" value="${messageLabel}" />
          <jsp:param name="placeholder" value="${messageHint}" />
        </jsp:include>

        <!-- Submit Button -->
        <c:set var="submitButtonLabel"><spring:message code="reply.submit"/></c:set>
        <jsp:include page="../components/button.jsp">
          <jsp:param name="label" value="${submitButtonLabel}" />
          <jsp:param name="type" value="submit" />
        </jsp:include>
      </form:form>
    </div>
  </div>
</div>

</body>
</html>