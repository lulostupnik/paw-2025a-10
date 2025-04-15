<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="replyJourney.title"/></title>
    <!-- Include custom CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />

</head>
<body>

<div class="container">
    <!-- Back Link -->
    <div class="mb-6">
        <a href="<c:url value="/journeys/"/>" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back"/>
        </a>
    </div>
        
    <div class="mx-auto max-w-2xl">
        <div class="text-center mb-6">
            <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                <spring:message code="replyJourney.title"/>
            </h2>
            <p class="mt-2 text-gray-600">
                <spring:message code="replyJourney.subtitle" arguments="${journey.user.email}"/>
            </p>
        </div>

        <!-- Reply Form Card -->
        <div class="card">
            <c:url var="registerUrl" value="/journeys/${journey.id}/reply"/>
            <form:form modelAttribute="replyJourneyForm" action="${registerUrl}" method="post" enctype="multipart/form-data">

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