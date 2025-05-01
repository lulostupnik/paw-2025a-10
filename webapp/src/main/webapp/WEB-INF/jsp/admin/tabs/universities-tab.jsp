<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="tab-content active" id="universities-tab">
  <c:set var="titleMessageCode" value="admin.manage.university" scope="request" />
  <c:set var="searchUrl" value="/dashboard/universities" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.university" scope="request" />
  <c:set var="showAddButton" value="true" scope="request" />
  <c:set var="addButtonUrl" value="/universities/create" scope="request" />
  <c:set var="addButtonTextCode" value="university.create.button" scope="request" />

  <div class="content-header">
    <h2><spring:message code="${titleMessageCode}" /></h2>
    <div class="action-bar">
      <div class="actions-container">
        <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
          <input type="text" name="search" class="search-input" placeholder="<spring:message code='${searchPlaceholderCode}' />" value="${param.search}">
          <input type="hidden" name="page" value="1">
          <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
          <button type="submit" class="search-button"><spring:message code="admin.search.button" /></button>
        </form>
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
        <th><spring:message code="admin.column.id" /></th>
        <th><spring:message code="admin.column.name" /></th>
        <th><spring:message code="admin.column.abbreviation" /></th>
        <th><spring:message code="admin.column.location" /></th>
      </tr>
      </thead>
      <tbody>
      <c:set var="universities" value="${pagedUniversities.content}" />
      <c:forEach items="${universities}" var="uni">
        <tr class="clickable-row" data-href="<c:url value="../universities/${uni.id}"/>" >
          <td><c:out value="${uni.id}"/></td>
          <td><c:out value="${uni.name}"/></td>
          <td><c:out value="${uni.abbreviation}"/></td>
          <td><c:out value="${uni.city}"/></td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty universities}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>

    <jsp:include page="../../components/pagination-controls.jsp">
      <jsp:param name="currentPage" value="${pagedUniversities.currentPage}" />
      <jsp:param name="itemsPerPage" value="10" />
      <jsp:param name="totalPages" value="${pagedUniversities.totalPages}" />
      <jsp:param name="search" value="${param.search}" />
      <jsp:param name="currentUrl" value="/dashboard/universities" />
    </jsp:include>
  </div>
</div>