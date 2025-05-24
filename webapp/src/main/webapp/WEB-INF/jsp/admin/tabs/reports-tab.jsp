<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<link rel="stylesheet" href="<c:url value='/resources/css/dashboard.css'/>" />

<script>
  function saveLink() {
    sessionStorage.setItem("rutaAnterior", window.location.href);
  }
</script>
<div class="tab-content active" id="reports-tab">
  <c:set var="titleMessageCode" value="admin.manage.reports" scope="request" />
  <c:set var="searchUrl" value="/dashboard/reports" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.reports" scope="request" />
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
        <th><spring:message code="admin.column.reportedUser" /></th>
        <th><spring:message code="admin.column.reportingUser"/></th>
        <th><spring:message code="admin.column.description" /></th>
        <th><spring:message code="admin.column.reason" /></th>
        <th><spring:message code="admin.column.status" /></th>
      </tr>
      </thead>
      <tbody>
      <c:set var="reports" value="${pagedReports.content}" />
      <c:forEach items="${reports}" var="report">
        <tr class="clickable-row" onclick="saveLink()" data-href="<c:url value="../reports/${report.id}"/>" >
          <td><c:out value="${report.reportedUser.username}"/></td>
          <td><c:out value="${report.reportingUser.username}"/></td>
          <td><c:out value="${report.description}"/></td>
          <td><c:out value="${report.reason}"/></td>
          <td><c:out value="${report.status}"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty reports}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>








    <jsp:include page="../../components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${pagedReports.totalPages}" />
      <jsp:param name="currentPage" value="${pagedReports.currentPage}" />
      <jsp:param name="pageSize" value="10" />
      <jsp:param name="baseUrl" value="/dashboard/reports?search=${param.search}" />
    </jsp:include>
  </div>
</div>
