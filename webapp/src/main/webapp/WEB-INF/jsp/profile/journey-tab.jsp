<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="profile-section active" id="journeys-section">
  <div class="section-actions">
    <c:if test="${empty userJourney}">
      <a href="<c:url value='/journeys/create'/>" class="btn-primary">
        <svg xmlns="http://www.w3.org/2000/svg" class="btn-icon" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        <spring:message code="journey.create.button"/>
      </a>
    </c:if>
  </div>

  <div class="cards-grid">
    <c:if test="${empty userJourney}">
      <div class="empty-state">
        <div class="empty-icon">
          <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 20l-5.447-2.724A1 1 0 013 16.382V5.618a1 1 0 011.447-.894L9 7m0 13l6-3m-6 3V7m6 10l4.553 2.276A1 1 0 0021 18.382V7.618a1 1 0 00-.553-.894L15 4m0 13V4m0 0L9 7" />
          </svg>
        </div>
        <p class="empty-message">
          <spring:message code="profile.no.journeys"/>
        </p>
        <a href="<c:url value='/journeys/create'/>" class="empty-action-btn">
          <spring:message code="journey.create.button" />
        </a>
      </div>
    </c:if>

    <c:if test="${not empty userJourney}">
      <jsp:include page="../journeys/journey-card.jsp">
        <jsp:param name="journeyId" value="${userJourney.id}" />
        <jsp:param name="city" value="${userJourney.destinationUniversity.city.name}" />
        <jsp:param name="startDate" value="${userJourney.startDate}" />
        <jsp:param name="endDate" value="${userJourney.endDate}" />
        <jsp:param name="description" value="${userJourney.description}" />
        <jsp:param name="profilePictureId" value="${userJourney.user.profilePictureId}" />
        <jsp:param name="userName" value="${userJourney.user.username}" />
        <jsp:param name="firstname" value="${userJourney.user.firstname}" />
        <jsp:param name="lastname" value="${userJourney.user.lastname}"/>
        <jsp:param name="country" value="${userJourney.destinationUniversity.city.country}"/>
        <jsp:param name="university" value="${userJourney.destinationUniversity.name}"/>
        <jsp:param name="isOwner" value="true"/>
      </jsp:include>
    </c:if>
  </div>
</div>