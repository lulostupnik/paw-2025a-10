<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <title><spring:message code="replyEvent.title" text="Reply to Event"/></title>
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
  <!-- Back Link -->
  <div class="mb-6">
    <a href="${pageContext.request.contextPath}/events" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
      <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="event.back" text="Back to Events"/>
    </a>
  </div>

  <div class="mx-auto max-w-2xl">
    <div class="text-center mb-6">
      <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
        <spring:message code="replyEvent.title" text="Reply to Event"/>
      </h2>
      <p class="mt-2 text-gray-600">
        <spring:message code="replyEvent.subtitle" text="Responding to"/>: <c:out value="${event.eventCity.name}"/> - <c:out value="${event.date}"/>
      </p>
    </div>

    <!-- Event Details Card -->
    <div class="mb-8 p-4 sm:p-6 bg-white border border-gray-200 rounded-xl">
      <h3 class="text-lg font-semibold text-gray-800 mb-2">
        <spring:message code="event.details" text="Event Details"/>
      </h3>

      <!-- Event Image if available -->
      <c:if test="${not empty event.flyerImageId}">
        <img src="${pageContext.request.contextPath}/images/${event.flyerImageId}" alt="<spring:message code='event.flyer.alt'/>" class="w-full h-40 object-cover rounded-lg mb-4">
      </c:if>

      <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-4">
        <div>
          <p class="text-sm font-medium text-gray-500">
            <spring:message code="event.city" text="City"/>
          </p>
          <p class="text-base text-gray-800">${event.eventCity.name}</p>
        </div>
        <div>
          <p class="text-sm font-medium text-gray-500">
            <spring:message code="event.date" text="Date"/>
          </p>
          <p class="text-base text-gray-800">${event.date}</p>
        </div>
      </div>

      <div class="mb-2">
        <p class="text-sm font-medium text-gray-500">
          <spring:message code="event.description" text="Description"/>
        </p>
        <p class="text-base text-gray-800">${event.description}</p>
      </div>

      <div>
        <p class="text-sm font-medium text-gray-500">
          <spring:message code="event.organizer" text="Organizer"/>
        </p>
        <p class="text-base text-gray-800">${event.user.email}</p>
      </div>
    </div>

    <!-- Reply Form Card -->
    <div class="p-4 sm:p-6 lg:p-8 bg-white border border-gray-200 rounded-xl">
      <c:url var="replyUrl" value="/events/${event.id}/reply"/>
      <form:form modelAttribute="replyEventForm" action="${replyUrl}" method="post">

        <!-- Email Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="email" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.email" text="Your Email"/>
          </form:label>
          <spring:message code="replyEvent.email.hint" var="emailHint" text="Enter your email address"/>
          <form:input path="email" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${emailHint}"/>
          <form:errors path="email" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- First Name Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="firstName" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.firstName" text="First Name"/>
          </form:label>
          <spring:message code="replyEvent.firstName.hint" var="firstNameHint" text="Enter your first name"/>
          <form:input path="firstName" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${firstNameHint}"/>
          <form:errors path="firstName" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- Last Name Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="lastName" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.lastName" text="Last Name"/>
          </form:label>
          <spring:message code="replyEvent.lastName.hint" var="lastNameHint" text="Enter your last name"/>
          <form:input path="lastName" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${lastNameHint}"/>
          <form:errors path="lastName" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- Username Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="username" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.username" text="Username"/>
          </form:label>
          <spring:message code="replyEvent.username.hint" var="usernameHint" text="Enter your username"/>
          <form:input path="username" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${usernameHint}"/>
          <form:errors path="username" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- Origin University Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="originUniversity" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.originUniversity" text="Origin University"/>
          </form:label>
          <spring:message code="replyEvent.originUniversity.hint" var="originUniversityHint" text="Enter your university"/>
          <form:input path="originUniversity" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${originUniversityHint}"/>
          <form:errors path="originUniversity" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- Career Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="career" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.career" text="Career"/>
          </form:label>
          <spring:message code="replyEvent.career.hint" var="careerHint" text="Enter your career"/>
          <form:input path="career" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                      placeholder="${careerHint}"/>
          <form:errors path="career" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- Message Field -->
        <div class="mb-4 sm:mb-6">
          <form:label path="message" class="block mb-2 text-sm font-medium">
            <spring:message code="replyEvent.message" text="Your Message"/>
          </form:label>
          <spring:message code="replyEvent.message.hint" var="messageHint" text="Any questions or comments about the event?"/>
          <form:textarea path="message" rows="4" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                         placeholder="${messageHint}"/>
          <form:errors path="message" cssClass="text-red-500 text-sm mt-1" element="p"/>
        </div>

        <!-- Submit Button -->
        <div class="mt-6">
          <spring:message code="replyEvent.submit" var="submit" text="Submit Response"/>
          <button type="submit" class="w-full py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2">
              ${submit}
          </button>
        </div>
      </form:form>
    </div>
  </div>
</div>

<!-- Include Preline UI Kit JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.js"></script>
</body>
</html>