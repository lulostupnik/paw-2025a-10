<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%-- Pagination parameters --%>
<c:set var="currentPage" value="${param.currentPage != null ? param.currentPage : 1}" />
<c:set var="itemsPerPage" value="${param.itemsPerPage != null ? param.itemsPerPage : 10}" />
<c:set var="totalPages" value="${param.totalPages != null ? param.totalPages : 1}" />
<c:set var="pageUrl" value="${param.currentUrl}"/>

<div class="pagination-container">
  <div class="pagination-info">
    <spring:message code="pagination.page" /> <c:url value="${currentPage}"/> <spring:message code="pagination.of" /> ${totalPages}
  </div>
  <div class="pagination-controls">
    <%-- Previous button --%>
    <c:choose>
      <c:when test="${currentPage <= 1}">
        <span class="pagination-button prev-button disabled" aria-disabled="true">
          <i class="chevron-left-icon"></i>
          <spring:message code="pagination.previous"/>
        </span>
      </c:when>
      <c:otherwise>
        <c:url var="prevPageUrl" value="${pageUrl}">
          <c:param name="page" value="${currentPage - 1}" />
          <c:param name="pageSize" value="${itemsPerPage}" />
          <c:if test="${not empty param.search}">
            <c:param name="search" value="${param.search}" />
          </c:if>
        </c:url>
        <a href="${prevPageUrl}" class="pagination-button prev-button">
          <i class="chevron-left-icon"></i>
          <spring:message code="pagination.previous"/>
        </a>
      </c:otherwise>
    </c:choose>

    <%-- Next button --%>
    <c:choose>
      <c:when test="${currentPage >= totalPages}">
        <span class="pagination-button next-button disabled" aria-disabled="true">
          <spring:message code="pagination.next"/>
          <i class="chevron-right-icon"></i>
        </span>
      </c:when>
      <c:otherwise>
        <c:url var="nextPageUrl" value="${pageUrl}">
          <c:param name="page" value="${currentPage + 1}" />
          <c:param name="pageSize" value="${itemsPerPage}" />
          <c:if test="${not empty param.search}">
            <c:param name="search" value="${param.search}" />
          </c:if>
        </c:url>
        <a href="${nextPageUrl}" class="pagination-button next-button">
          <spring:message code="pagination.next"/>
          <i class="chevron-right-icon"></i>
        </a>
      </c:otherwise>
    </c:choose>
  </div>
</div>
