<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="journey.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/journey-detail.css"/>" />
</head>
<body>

<div class="container">
    <!-- Back Link -->
    <div class="back-link">
        <a href="<c:url value="/journeys"/>" class="back-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back"/>
        </a>
    </div>

    <div class="content-wrapper">
        <!-- Journey Details Card -->
        <div class="journey-card">
            <!-- User Info -->
            <div class="user-info">
                <h2 class="user-name"><c:out value="${journey.user.firstname} ${journey.user.lastname}"/></h2>
                <p class="user-email">
                    <spring:message code="journey.contact"/><c:out value="${journey.user.email}"/>
                </p>
            </div>

            <!-- Destination Info -->
            <div class="destination-info">
                <h3 class="section-title"><spring:message code="journey.destination"/></h3>
                <div class="tags">
                    <span class="tag tag-blue">
                        <spring:message code="journey.city"/><c:out value=" ${journey.destinationUniversity.city}"/>
                    </span>
                    <span class="tag tag-gray">
                        <spring:message code="journey.university"/> <c:out value=" ${journey.destinationUniversity.name}"/>
                    </span>
                </div>
            </div>

            <!-- Date Info -->
            <div class="date-info">
                <h3 class="section-title"><spring:message code="journey.dates"/></h3>
                <div class="date-range">
                    <p><c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" /></p>
                </div>
            </div>

            <!-- Description -->
            <div class="description">
                <h3 class="section-title"><spring:message code="journey.description"/></h3>
                <p><c:out value="${journey.description}"/></p>
            </div>

            <!-- Reply Button -->
            <div class="button-container">
                <a href="<c:url value ="/journeys/${journey.id}/reply"/>" class="reply-button">
                    <spring:message code="journey.reply.button"/>
                </a>
            </div>
        </div>
    </div>
</div>

</body>
</html>