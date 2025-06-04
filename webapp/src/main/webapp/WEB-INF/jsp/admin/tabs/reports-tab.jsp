<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<link rel="stylesheet" href="<c:url value='/resources/css/dashboard.css'/>" />

<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>
<script>
  function saveLink() {
    pushToNavigationStack(window.location.href);
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
        <c:set var="reportReason"> <c:choose>
          <c:when test="${report.reason.name() == 'SPAM'}">
            <spring:message code="report.reason.spam" text="Spam or unwanted content"/>
          </c:when>
          <c:when test="${report.reason.name() == 'HARRASMENT'}">
            <spring:message code="report.reason.harassment" text="Harassment or bullying"/>
          </c:when>
          <c:when test="${report.reason.name() == 'INAPPROPRIATE_CONTENT'}">
            <spring:message code="report.reason.inappropriate" text="Inappropriate content"/>
          </c:when>
          <c:when test="${report.reason.name() == 'MISINFORMATION'}">
            <spring:message code="report.reason.misinformation" text="False or misleading information"/>
          </c:when>
          <c:when test="${report.reason.name() == 'HATE_SPEECH'}">
            <spring:message code="report.reason.hate_speech" text="Hate speech or discrimination"/>
          </c:when>
          <c:when test="${report.reason.name() == 'VIOLENCE'}">
            <spring:message code="report.reason.violence" text="Violence or threats"/>
          </c:when>
          <c:when test="${report.reason.name() == 'OTHER'}">
            <spring:message code="report.reason.other" text="Other"/>
          </c:when>
          <c:otherwise>
            <c:out value="${report.reason}"/>
          </c:otherwise>
        </c:choose></c:set>
        <tr class="clickable-row" onclick="saveLink()" data-href="<c:url value="../reports/${report.id}"/>" >
          <td><c:out value="${report.reportedUser.username}"/></td>
          <td><c:out value="${report.reportingUser.username}"/></td>
          <td><c:out value="${report.description}"/></td>
          <td><c:out value="${reportReason}"/></td>
          <!-- Replace the existing status column in your table -->
          <!-- Alternative design showing progress -->
          <td>
            <div style="display: flex; flex-direction: column; gap: 0.25rem;">
              <c:choose>
                <c:when test="${report.status == 'PENDING'}">
                <span class="status-badge status-pending">
                    <spring:message code="report.status.pending" text="Pending" />
                </span>
                  <div style="width: 100%; height: 2px; background-color: #f3f4f6; border-radius: 1px;">
                    <div style="width: 25%; height: 100%; background-color: #f59e0b; border-radius: 1px;"></div>
                  </div>
                </c:when>
                <c:when test="${report.status == 'UNDER_REVIEW'}">
                <span class="status-badge status-under-review">
                    <spring:message code="report.status.under_review" text="Under Review" />
                </span>
                  <div style="width: 100%; height: 2px; background-color: #f3f4f6; border-radius: 1px;">
                    <div style="width: 60%; height: 100%; background-color: #3b82f6; border-radius: 1px;"></div>
                  </div>
                </c:when>
                <c:when test="${report.status == 'RESOLVED'}">
                <span class="status-badge status-resolved">
                    <spring:message code="report.status.resolved" text="Resolved" />
                </span>
                  <div style="width: 100%; height: 2px; background-color: #10b981; border-radius: 1px;">
                    <div style="width: 100%; height: 100%; background-color: #10b981; border-radius: 1px;"></div>
                  </div>
                </c:when>
                <c:when test="${report.status == 'DISMISSED'}">
                <span class="status-badge status-dismissed">
                    <spring:message code="report.status.dismissed" text="Dismissed" />
                </span>
                  <div style="width: 100%; height: 2px; background-color: #6b7280; border-radius: 1px;">
                    <div style="width: 100%; height: 100%; background-color: #6b7280; border-radius: 1px;"></div>
                  </div>
                </c:when>
              </c:choose>
            </div>
          </td>      </tr>
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
