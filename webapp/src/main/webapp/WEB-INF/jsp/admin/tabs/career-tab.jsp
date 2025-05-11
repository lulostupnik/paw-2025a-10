<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<link rel="stylesheet" href="<c:url value='/resources/css/dashboard.css'/>" />

<div class="tab-content active" id="careers-tab">
  <c:set var="titleMessageCode" value="admin.manage.careers" scope="request" />
  <c:set var="searchUrl" value="/dashboard/careers" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.careers" scope="request" />
  <c:set var="showAddButton" value="true" scope="request" />
  <c:set var="addButtonUrl" value="/careers/create" scope="request" />
  <c:set var="addButtonTextCode" value="careers.create.button" scope="request" />

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
          </button>
        </form>
        <c:if test="${showAddButton}">
          <a href="<c:url value='${addButtonUrl}'/>" class="btn btn-primary btn-with-icon">
            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="${addButtonTextCode}"/>" class="btn-icon" />
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
        <th><spring:message code="admin.column.name" /></th>

      </tr>
      </thead>
      <tbody>
      <c:set var="careers" value="${pagedCareers.content}" />
      <c:forEach items="${careers}" var="career">
        <tr class="clickable-row" data-href="<c:url value="../careers/${career.id}"/>" >
          <td><c:out value="${career.name}"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty careers}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>


    <jsp:include page="../../components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${pagedCareers.totalPages}" />
      <jsp:param name="currentPage" value="${pagedCareers.currentPage}" />
      <jsp:param name="pageSize" value="10" />
      <jsp:param name="baseUrl" value="/dashboard/careers?search=${param.search}" />
    </jsp:include>










  </div>
</div>
