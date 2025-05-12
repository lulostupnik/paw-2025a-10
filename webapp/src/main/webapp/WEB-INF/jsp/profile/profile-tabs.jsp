<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script>
  function saveLink() {
    sessionStorage.setItem("rutaAnterior", window.location.href);
  }
</script>
<div class="profile-tabs-container">
  <div class="profile-tabs">
    <a href="<c:url value='/profile/info'/>" class="profile-tab ${requestScope['javax.servlet.forward.servlet_path'] eq '/profile/info' || requestScope['javax.servlet.forward.servlet_path'] eq '/profile' ? 'active' : ''}">
      <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="12" r="10"></circle>
        <line x1="12" y1="16" x2="12" y2="12"></line>
        <line x1="12" y1="8" x2="12.01" y2="8"></line>
      </svg>
      <span class="tab-text"><spring:message code="profile.tab.info"/></span>
    </a>
    <a href="<c:url value='/profile/interests'/>" class="profile-tab ${requestScope['javax.servlet.forward.servlet_path'] eq '/profile/interests' ? 'active' : ''}">
      <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
      </svg>
      <span class="tab-text"><spring:message code="profile.tab.interests"/></span>
    </a>

    <a href="<c:url value='/profile/events'/>" class="profile-tab ${requestScope['javax.servlet.forward.servlet_path'] eq '/profile/events' ? 'active' : ''}">
      <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
        <line x1="16" y1="2" x2="16" y2="6"></line>
        <line x1="8" y1="2" x2="8" y2="6"></line>
        <line x1="3" y1="10" x2="21" y2="10"></line>
      </svg>
      <span class="tab-text"><spring:message code="profile.tab.events"/></span>
    </a>

      <c:if test="${not empty userJourney}">
        <a href="<c:url value='/journeys/${userJourney.id}'/>" onclick="saveLink()" class="btn-primary profile-tabs-right ${requestScope['javax.servlet.forward.servlet_path'] eq '/profile/journey' ? 'active' : ''}">
          <svg xmlns="http://www.w3.org/2000/svg" class="tab-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
          </svg>
          <span class="tab-text"><spring:message code="profile.tab.journeys"/></span>
        </a>
      </c:if>
      <c:if test="${empty userJourney}">
        <a href="<c:url value='/journeys/create'/>" class="btn-primary profile-tabs-right ${requestScope['javax.servlet.forward.servlet_path'] eq '/profile/journey' ? 'active' : ''}">
          <svg xmlns="http://www.w3.org/2000/svg" class="button-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7"></path>
          </svg>
          <span><spring:message code="profile.create.journey" text=" Create Journey"/></span>
        </a>
      </c:if>


  </div>
</div>