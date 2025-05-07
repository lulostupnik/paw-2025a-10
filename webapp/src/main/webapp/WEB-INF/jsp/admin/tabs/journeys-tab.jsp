<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<link rel="stylesheet" href="<c:url value='/resources/css/dashboard.css'/>" />

<div class="tab-content active" id="journeys-tab">
  <c:set var="titleMessageCode" value="admin.manage.journeys" scope="request" />
  <c:set var="searchUrl" value="/dashboard/journeys" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.journeys" scope="request" />
  <c:set var="showAddButton" value="false" scope="request" />

  <div class="content-header">
    <h2><spring:message code="${titleMessageCode}" /></h2>
    <div class="action-bar">
      <div class="actions-container">
        <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
          <input type="text" name="search" class="search-input"
                 placeholder="<spring:message code='${searchPlaceholderCode}' />"
                 value="<c:out value="${param.search}"/>">          <input type="hidden" name="page" value="1">
          <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
          <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
            <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
          </button>        </form>
        <c:if test="${showAddButton}">
          <a href="<c:url value='${addButtonUrl}'/>" class="add-button">
            <i class="plus-icon"></i>
            <spring:message code="${addButtonTextCode}"/>
          </a>
        </c:if>
      </div>
    </div>
  </div>
  <div class="table-container">
    <table class="data-table">
      <thead>
      <tr>
        <th><spring:message code="admin.column.user" /></th>
        <th><spring:message code="admin.column.destination" /></th>
        <th><spring:message code="admin.column.university" /></th>
        <th><spring:message code="admin.column.start.date" /></th>
        <th><spring:message code="admin.column.end.date" /></th>
      </tr>
      </thead>
      <tbody>
      <c:set var="journeys" value="${pagedJourneys.content}" />
      <c:forEach items="${journeys}" var="journey">
        <tr class="clickable-row" data-href="<c:url value="../journeys/${journey.id}"/>" >
          <td><c:out value="${journey.user.username}"/></td>
          <td><c:out value="${journey.destinationUniversity.city}"/></td>
          <td><c:out value="${journey.destinationUniversity.name}"/></td>
          <td><c:out value="${journey.startDate}"/></td>
          <td><c:out value="${journey.endDate}"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty journeys}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>

<%--    <jsp:include page="../../components/pagination-controls.jsp">--%>
<%--      <jsp:param name="currentPage" value="${pagedJourneys.currentPage}" />--%>
<%--      <jsp:param name="itemsPerPage" value="10" />--%>
<%--      <jsp:param name="totalPages" value="${pagedJourneys.totalPages}" />--%>
<%--      <jsp:param name="search" value="${param.search}" />--%>
<%--      <jsp:param name="currentUrl" value="/dashboard/journeys" />--%>
<%--    </jsp:include>--%>
    <jsp:include page="../../components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${pagedJourneys.totalPages}" />
      <jsp:param name="currentPage" value="${pagedJourneys.currentPage}" />
      <jsp:param name="pageSize" value="10" />
      <jsp:param name="baseUrl" value="/dashboard/journeys?search=${param.search}" />
    </jsp:include>
  </div>
</div>