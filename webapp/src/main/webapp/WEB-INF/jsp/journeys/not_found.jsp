<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html lang="${pageContext.response.locale}">
<head>
  <title><spring:message code="journey.detail.title"/></title>
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<div class="container">

  <div class="mb-6">
    <button type="button" name="back" class="back-link" onClick="history.back()">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
        <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
      </svg>
      <spring:message code="journey.back"/>
    </button>
  </div>

  <!-- If journey not found -->
  <div class="card">
    <div class="mb-6">
      <h2><spring:message code="journey.not.found.title"/></h2>
      <p><spring:message code="journey.not.found.message"/></p>
    </div>
  </div>
</div>
</body>
</html>
