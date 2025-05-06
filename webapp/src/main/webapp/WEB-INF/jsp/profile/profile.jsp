<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><c:out value="${pageContext.request.contextPath}"/><spring:message code="profile.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/pages/profile.css"/>" />

  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>

<c:set var="pageSize" value="4" scope="request" />

<div class="layout-container">
  <!-- Main Content -->
  <div class="main-content">
    <jsp:include page="../components/navbar.jsp" />

    <!-- Page Title -->
    <div class="content-container">
      <div class="header-container">
        <h2 class="page-title"><spring:message code="profile.page.title"/></h2>
      </div>
    </div>

    <c:if test="${not empty user}">
      <!-- Profile Header Section -->
      <jsp:include page="./profile-header.jsp" />

      <!-- Main Content Section -->
      <div class="content-container">
        <!-- Profile Navigation Tabs -->
        <jsp:include page="./profile-tabs.jsp" />

        <!-- Profile Content Sections -->
        <div class="profile-content">
          <!-- Include the appropriate tab content based on the current URL -->
          <c:set var="currentPath" value="${requestScope['javax.servlet.forward.servlet_path']}" />

          <c:choose>
            <c:when test="${currentPath eq '/profile/interests'}">
              <jsp:include page="./interests-tab.jsp" />
            </c:when>
            <c:when test="${currentPath eq '/profile/journeys'}">
              <jsp:include page="./journeys-tab.jsp" />
            </c:when>
            <c:when test="${currentPath eq '/profile/events'}">
              <jsp:include page="./events-tab.jsp" />
            </c:when>
            <c:otherwise>
              <!-- Default to info tab -->
              <jsp:include page="./info-tab.jsp" />
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </c:if>
  </div>
</div>

<script src="<c:url value='/resources/js/profile.js'/>"></script>
</body>
</html>