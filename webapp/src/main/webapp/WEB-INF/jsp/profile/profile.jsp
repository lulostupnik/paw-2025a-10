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
      <c:if test="${user.id != profileUser.id}">
      <div class="back-button-container">
          <button onclick="goBack()" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M19 12H5"></path>
              <path d="M12 19l-7-7 7-7"></path>
            </svg>
            <span><spring:message code="event.detail.back.to.list" /></span>
          </button>
        </div>
        </c:if>
      <div class="header-container">
        <h2 class="page-title"><spring:message code="profile.page.title"/></h2>
      </div>

      <c:if test="${not empty profileUser}">

        <jsp:include page="./profile-header.jsp" />



      <div class="profile-tabs-container">
      <jsp:include page="./profile-tabs.jsp" />


          <div class="profile-content">

            <c:set var="currentPath" value="${requestScope['javax.servlet.forward.servlet_path']}" />



            <c:choose>
              <c:when test="${isInterestsTab}">
                <jsp:include page="./interests-tab.jsp" />
              </c:when>
              <c:when test="${isEventTab}">
                <jsp:include page="./events-tab.jsp" />
              </c:when>
              <c:when test="${isInfoTab}">
                <jsp:include page="./info-tab.jsp" />
              </c:when>
              <c:otherwise/>
            </c:choose>

          </div>
      </div>
      </c:if>
    </div>
  </div>
</div>

<script src="<c:url value='/resources/js/profile.js'/>"></script>
<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>
<script>
  function goBack(){
    const rutaAnterior = popFromNavigationStack()
    if (rutaAnterior) {
      window.location.href = rutaAnterior;
    } else {
      window.location.href = "<c:url value='/events'/>"
    }
  }
</script>
</body>
</html>