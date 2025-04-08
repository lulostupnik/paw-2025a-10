<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <title><spring:message code="replyEvent.title"/></title>
  <!-- Include custom CSS -->
  <link rel="stylesheet" href="<c:url value='/resources/css/base.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/form-components.css'/>" />
  <style>
    /* Inline styles to ensure proper sizing */
    *, *::before, *::after {
      box-sizing: border-box;
    }

    /* Fix for SVG icons */
    svg {
      max-width: 100%;
      max-height: 100%;
    }
  </style>
</head>
<body>

<div class="container">
  <!-- Back Link -->
  <div class="mb-6">
    <a href="<c:url value="/events/"/>" class="back-link">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="event.back"/>
    </a>
  </div>

  <div class="mx-auto max-w-2xl">
    <div class="text-center mb-6">
      <h2 class="header">
        <spring:message code="replyEvent.title" />
      </h2>
      <p class="event-subtitle">
        <spring:message code="replyEvent.subtitle" arguments="${event.eventCity.name},${event.date}" />
      </p>
    </div>

    <!-- Reply Form Card -->
    <div class="card">
      <c:url var="replyUrl" value="/events/${event.id}/reply"/>
      <form:form modelAttribute="replyEventForm" action="${replyUrl}" method="post" enctype="multipart/form-data">

        <!-- Profile Picture Upload Field -->
        <c:set var="profilePicLabel"><spring:message code="createJourney.profile_picture" /></c:set>
        <jsp:include page="../components/image-upload.jsp">
          <jsp:param name="path" value="profilePicture" />
          <jsp:param name="label" value="${profilePicLabel}" />
          <jsp:param name="messageCode" value="upload_picture.profile" />
        </jsp:include>

        <!-- Email Field -->
        <c:set var="emailLabel"><spring:message code="replyEvent.email"/></c:set>
        <c:set var="emailHint"><spring:message code="replyEvent.email.hint"/></c:set>
        <jsp:include page="../components/text-field.jsp">
          <jsp:param name="path" value="email" />
          <jsp:param name="label" value="${emailLabel}" />
          <jsp:param name="placeholder" value="${emailHint}" />
          <jsp:param name="type" value="email" />
        </jsp:include>

        <!-- First Name Field -->
        <c:set var="firstNameLabel"><spring:message code="replyEvent.firstName"/></c:set>
        <c:set var="firstNameHint"><spring:message code="replyEvent.firstName.hint"/></c:set>
        <jsp:include page="../components/text-field.jsp">
          <jsp:param name="path" value="firstName" />
          <jsp:param name="label" value="${firstNameLabel}" />
          <jsp:param name="placeholder" value="${firstNameHint}" />
        </jsp:include>

        <!-- Last Name Field -->
        <c:set var="lastNameLabel"><spring:message code="replyEvent.lastName"/></c:set>
        <c:set var="lastNameHint"><spring:message code="replyEvent.lastName.hint"/></c:set>
        <jsp:include page="../components/text-field.jsp">
          <jsp:param name="path" value="lastName" />
          <jsp:param name="label" value="${lastNameLabel}" />
          <jsp:param name="placeholder" value="${lastNameHint}" />
        </jsp:include>

        <!-- Username Field -->
        <c:set var="usernameLabel"><spring:message code="replyEvent.username"/></c:set>
        <c:set var="usernameHint"><spring:message code="replyEvent.username.hint"/></c:set>
        <jsp:include page="../components/text-field.jsp">
          <jsp:param name="path" value="username" />
          <jsp:param name="label" value="${usernameLabel}" />
          <jsp:param name="placeholder" value="${usernameHint}" />
        </jsp:include>

        <!-- Origin University Field -->
        <c:set var="universityLabel"><spring:message code="event.university"/></c:set>
        <c:set target="${requestScope}" property="universityItems" value="${universities}" />
        <jsp:include page="../components/dropdown.jsp">
          <jsp:param name="path" value="originUniversity" />
          <jsp:param name="label" value="${universityLabel}" />
          <jsp:param name="items" value="universityItems" />
          <jsp:param name="defaultMessageCode" value="createJourney.destinationUniversity.select" />
        </jsp:include>

        <!-- Career Field -->
        <c:set var="careerLabel"><spring:message code="event.career"/></c:set>
        <c:set target="${requestScope}" property="careerItems" value="${careers}" />
        <jsp:include page="../components/dropdown.jsp">
          <jsp:param name="path" value="career" />
          <jsp:param name="label" value="${careerLabel}" />
          <jsp:param name="items" value="careerItems" />
          <jsp:param name="defaultMessageCode" value="event.career.select" />
        </jsp:include>

        <!-- Message Field -->
        <c:set var="messageLabel"><spring:message code="replyEvent.message"/></c:set>
        <c:set var="messageHint"><spring:message code="replyEvent.message.hint"/></c:set>
        <jsp:include page="../components/text-area.jsp">
          <jsp:param name="path" value="message" />
          <jsp:param name="label" value="${messageLabel}" />
          <jsp:param name="placeholder" value="${messageHint}" />
        </jsp:include>

        <!-- Submit Button -->
        <c:set var="submitButtonLabel"><spring:message code="replyEvent.submit"/></c:set>
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