<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${empty interestPage.content}">
  <div class="empty-state">
    <div class="empty-icon">
      <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
      </svg>
    </div>
    <p class="empty-message">
      <spring:message code="journey.detail.no.interests" text="No interests to display" />
    </p>
  </div>
</c:if>
<c:if test="${not empty interestPage.content}">
  <div class="interests-container">
    <c:forEach var="userInterest" items="${interestPage.content}">
      <div class="interest-tag">
        <c:out value="${userInterest.interest}" />
      </div>
    </c:forEach>
  </div>
  <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
    <jsp:param name="pageObjectTotalPages" value="${interestPage.totalPages}" />
    <jsp:param name="currentPage" value="${interestPage.currentPage}" />
    <jsp:param name="pageSize" value="${interestPageSize}" />
    <jsp:param name="baseUrl" value="/journeys/${journey.id}?page=${journeyResponsesPage.currentPage}&size=${chatPageSize}&eventsPage=${eventsPage.currentPage}&eventsSize=${eventsPageSize}" />
    <jsp:param name="paramName" value="interestsPage" />
    <jsp:param name="sizeParamName" value="interestsSize" />
  </jsp:include>
</c:if>
