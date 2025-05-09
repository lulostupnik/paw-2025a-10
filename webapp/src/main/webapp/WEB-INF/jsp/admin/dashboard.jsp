<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="admin.dashboard.title" /></title>

  <!-- Include CSS files -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css' />">
  <link rel="stylesheet" href="<c:url value='/resources/css/pages/dashboard.css' />">
</head>
<body>
<jsp:include page="../components/navbar.jsp" />
<div class="main-content">
  <div class="content-container">
    <header class="header">
      <h1 class="page-title"><spring:message code="admin.dashboard.heading" /></h1>
    </header>

    <div class="dashboard-tabs">
      <a href="<c:url value='/dashboard/journeys'/>" class="tab-button ${pagedJourneys != null ? 'active' : ''}">
        <spring:message code="admin.tab.journeys" />
      </a>
      <a href="<c:url value='/dashboard/users'/>" class="tab-button ${pagedUsers != null ? 'active' : ''}">
        <spring:message code="admin.tab.users" />
      </a>
      <a href="<c:url value='/dashboard/events'/>" class="tab-button ${pagedEvents != null ? 'active' : ''}">
        <spring:message code="admin.tab.events" />
      </a>
      <a href="<c:url value='/dashboard/universities'/>" class="tab-button ${pagedUniversities != null ? 'active' : ''}">
        <spring:message code="admin.tab.universities" />
      </a>
      <a href="<c:url value='/dashboard/interests'/>" class="tab-button ${pagedInterests != null ? 'active' : ''}">
        <spring:message code="admin.tab.interests" />
      </a>
      <a href="<c:url value='/dashboard/cities'/>" class="tab-button ${pagedCities != null ? 'active' : ''}">
        <spring:message code="admin.tab.cities" />
      </a>
      <a href="<c:url value='/dashboard/careers'/>" class="tab-button ${pagedCareers != null ? 'active' : ''}">
        <spring:message code="admin.tab.careers" />
      </a>
    </div>

    <!-- Include the appropriate tab content based on which tab is active -->
    <c:if test="${pagedJourneys != null}">
      <jsp:include page="tabs/journeys-tab.jsp" />
    </c:if>

    <c:if test="${pagedUsers != null}">
      <jsp:include page="tabs/users-tab.jsp" />
    </c:if>

    <c:if test="${pagedEvents != null}">
      <jsp:include page="tabs/events-tab.jsp" />
    </c:if>

    <c:if test="${pagedUniversities != null}">
      <jsp:include page="tabs/universities-tab.jsp" />
    </c:if>

    <c:if test="${pagedInterests != null}">
      <jsp:include page="tabs/interests-tab.jsp" />
    </c:if>

    <c:if test="${pagedCities != null}">
      <jsp:include page="tabs/cities-tab.jsp" />
    </c:if>

    <c:if test="${pagedCareers != null}">
      <jsp:include page="tabs/career-tab.jsp" />
    </c:if>
  </div>
</div>
<!-- Include JavaScript files -->
<script src="<c:url value='/resources/js/dashboard.js' />"></script>
<script src="<c:url value='/resources/js/components/pagination.js' />"></script>
<script>
  document.addEventListener("DOMContentLoaded", function () {
    const rows = document.querySelectorAll(".clickable-row");
    rows.forEach(row => {
      row.addEventListener("click", () => {
        window.location.href = row.getAttribute("data-href");
      });
    });
  });
</script>

</body>
</html>