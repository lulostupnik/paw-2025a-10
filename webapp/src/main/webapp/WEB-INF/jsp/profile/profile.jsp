<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="profile.page.title"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/pages/profile.css"/>" />

  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>

<c:set var="pageSize" value="6" scope="request" />

<div class="layout-container">

  <div class="main-content">
    <jsp:include page="../components/navbar.jsp" />


    <div class="content-container">
      <div class="header-container">
        <h2 class="page-title"><spring:message code="profile.page.title"/></h2>
      </div>

      <c:if test="${not empty user}">

        <jsp:include page="./profile-header.jsp" />


          <jsp:include page="./profile-tabs.jsp" />


          <div class="profile-content">

            <c:set var="currentPath" value="${requestScope['javax.servlet.forward.servlet_path']}" />

            <c:choose>
              <c:when test="${currentPath eq '/profile/interests'}">
                <jsp:include page="./interests-tab.jsp" />
              </c:when>
              <c:when test="${currentPath eq '/profile/journey'}">
                <jsp:include page="journey-tab.jsp" />
              </c:when>
              <c:when test="${currentPath eq '/profile/events'}">
                <jsp:include page="./events-tab.jsp" />
              </c:when>
              <c:otherwise>

                <jsp:include page="./info-tab.jsp" />
              </c:otherwise>
            </c:choose>
          </div>
      </c:if>
    </div>
  </div>
</div>

<script src="<c:url value='/resources/js/profile.js'/>"></script>
</body>
</html>