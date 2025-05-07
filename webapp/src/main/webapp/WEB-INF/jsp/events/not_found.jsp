<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="event.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<div class="container">

    <button type="button" name="back" class="back-link" onClick="history.back()">
        <svg class="icon" style="width: 0.75rem; height: 0.75rem;" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 16 16">
            <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
        </svg>
        <spring:message code="event.back"/>
    </button>

    <!-- Event not found -->
    <div class="card">
<%--        <svg class="icon" xmlns="http://www.w3.org/2000/svg" fill="currentColor" viewBox="0 0 16 16">--%>
<%--            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>--%>
<%--            <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>--%>
<%--        </svg>--%>
        <h2 class="title"><spring:message code="event.not.found.title"/></h2>
        <p class="message"><spring:message code="event.not.found.message"/></p>
    </div>
</div>
</body>
</html>
