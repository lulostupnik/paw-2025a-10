<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Pagination parameters --%>
<c:set var="currentPage" value="${param.currentPage != null ? param.currentPage : 1}" />
<c:set var="itemsPerPage" value="${param.itemsPerPage != null ? param.itemsPerPage : 10}" />
<c:set var="view" value="${param.view != null ? param.view : 'events'}" />

<div class="pagination-container">
  <div class="pagination-controls">
    <%-- Previous button --%>
    <c:url var="prevPageUrl" value="/dashboard">
      <c:param name="page" value="${currentPage > 1 ? currentPage - 1 : 1}" />
      <c:param name="pageSize" value="${itemsPerPage}" />
        <c:param name="view" value="${view}" />
    </c:url>
    <a href="${prevPageUrl}"
       class="pagination-button prev-button ${currentPage <= 1 ? 'disabled' : ''}"
    ${currentPage <= 1 ? 'aria-disabled="true"' : ''}>
      <spring:message key="pagination.previous"/>
    </a>

    <span class="page-info"><spring:message key="pagination.title"/> <c:out value="${currentPage}"/> / <c:out value="${totalPages}"/></span>

    <%-- Next button --%>
    <c:url var="nextPageUrl" value="/dashboard">
      <c:param name="page" value="${currentPage + 1}" />
      <c:param name="pageSize" value="${itemsPerPage}" />
        <c:param name="view" value="${view}" />
    </c:url>
    <a href="${nextPageUrl}"
       class="pagination-button next-button">
      <spring:message key="pagination.next"/>
    </a>
  </div>
</div>
