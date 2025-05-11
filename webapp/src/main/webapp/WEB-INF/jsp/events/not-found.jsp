<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="event.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<div class="container">

    <a type="button" class="back-link" href="<c:url value="/events"/>">
        <svg class="icon" style="width: 0.75rem; height: 0.75rem;" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        <spring:message code="event.back"/>
    </a>


    <div class="card">




        <h2 class="title"><spring:message code="event.not.found.title"/></h2>
        <p class="message"><spring:message code="event.not.found.message"/></p>
    </div>
</div>
</body>
</html>
