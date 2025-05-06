<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="tab-content active" id="users-tab">
  <c:set var="titleMessageCode" value="admin.manage.users" scope="request" />
  <c:set var="searchUrl" value="/dashboard/users" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.users" scope="request" />
<%--  <c:set var="showAddButton" value="true" scope="request" />--%>
<%--  <c:set var="addButtonUrl" value="/users/create" scope="request" />--%>
<%--  <c:set var="addButtonTextCode" value="admin.add.user" scope="request" />--%>

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
<%--        <c:if test="${showAddButton}">--%>
<%--          <a href="<c:url value='${addButtonUrl}'/>" class="add-button">--%>
<%--            <i class="plus-icon"></i>--%>
<%--            <spring:message code="${addButtonTextCode}"/>--%>
<%--          </a>--%>
<%--        </c:if>--%>
      </div>
    </div>
  </div>
  <div class="table-container">
    <table class="data-table">
      <thead>
      <tr>
        <th><spring:message code="admin.column.id" /></th>
        <th><spring:message code="admin.column.name" /></th>
        <th><spring:message code="admin.column.email" /></th>
        <th><spring:message code="admin.column.university" /></th>
      </tr>
      </thead>
      <tbody>
      <c:set var="users" value="${pagedUsers.content}" />
      <c:forEach items="${users}" var="user">
        <tr class="clickable-row" data-href="<c:url value="../users/${user.id}"/>" >
          <td><c:out value="${user.id}"/></td>
          <td><c:out value="${user.firstname}"/></td>
          <td><c:out value="${user.email}"/></td>
          <td><c:out value="${user.university}"/></td>
          <c:set var="blockUrl" value="/users/${user.id}/block/"/>
            <c:set var="unblockUrl" value="/users/${user.id}/unblock/"/>
          <td>
            <c:if test="${! user.blocked}">
              <form action="<c:url value='${blockUrl}'/>"  method="post">
                <button type="submit" class="btn-danger btn-with-icon">
                  <img class="btn-icon" alt="<spring:message code="user.block"/>" src="<c:url value="/resources/icons/block.svg"/>"/>
                </button>
              </form>
            </c:if>
            <c:if test="${user.blocked}">
              <form action="<c:url value='${unblockUrl}'/>"  method="post">
                <button type="submit" class="btn-primary btn-with-icon">
                  <img class="btn-icon" alt="<spring:message code="user.block"/>" src="<c:url value="/resources/icons/block.svg"/>"/>
                </button>
              </form>
            </c:if>
          </td>

        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty users}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>

<%--    <jsp:include page="../../components/pagination-controls.jsp">--%>
<%--      <jsp:param name="currentPage" value="${pagedUsers.currentPage}" />--%>
<%--      <jsp:param name="itemsPerPage" value="10" />--%>
<%--      <jsp:param name="totalPages" value="${pagedUsers.totalPages}" />--%>
<%--      <jsp:param name="search" value="${param.search}" />--%>
<%--      <jsp:param name="currentUrl" value="/dashboard/users" />--%>
<%--    </jsp:include>--%>

    <jsp:include page="../../components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${pagedUsers.totalPages}" />
      <jsp:param name="currentPage" value="${pagedUsers.currentPage}" />
      <jsp:param name="pageSize" value="10" />
      <jsp:param name="baseUrl" value="/dashboard/users?search=${param.search}" />
    </jsp:include>
  </div>
</div>