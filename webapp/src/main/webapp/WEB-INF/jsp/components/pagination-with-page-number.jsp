<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="<c:url value="/resources/css/components/pagination-with-page-number.css"/>" />

<%--
Expected Request Parameters:
- pageObjectTotalPages: total pages (required)
- currentPage: current page number (required)
- pageSize: number of items per page (required)
- baseUrl: base URL for pagination (required)
- paramName: name of page parameter (optional, defaults to 'page')
- sizeParamName: name of size parameter (optional, defaults to 'size')
--%>

<c:if test="${empty param.paramName}">
    <c:set var="paramName" value="page" />
</c:if>
<c:if test="${not empty param.paramName}">
    <c:set var="paramName" value="${param.paramName}" />
</c:if>

<c:if test="${empty param.sizeParamName}">
    <c:set var="sizeParamName" value="size" />
</c:if>
<c:if test="${not empty param.sizeParamName}">
    <c:set var="sizeParamName" value="${param.sizeParamName}" />
</c:if>

<%-- Convert string parameters to integers --%>
<c:set var="totalPages" value="${param.pageObjectTotalPages + 0}" />
<c:set var="currentPage" value="${param.currentPage + 0}" />
<c:set var="pageSize" value="${param.pageSize + 0}" />
<c:set var="baseUrl" value="${param.baseUrl}" />


<c:if test="${totalPages > 1}">
    <div class="pagination">
        <!-- Previous -->
        <c:if test="${currentPage > 1}">
            <c:url var="prevUrl" value="${baseUrl}">
                <!-- Add/update the page parameter -->
                <c:param name="${paramName}" value="${currentPage - 1}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${prevUrl}" class="page-link">&laquo; <spring:message code="pagination.prev"/></a>
        </c:if>

        <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />
        <c:set var="end" value="${currentPage + 2 > totalPages ? totalPages : currentPage + 2}" />

        <!-- First page + ellipsis -->
        <c:if test="${start > 1}">
            <c:url var="firstPageUrl" value="${baseUrl}">
                <!-- Add/update the page parameter -->
                <c:param name="${paramName}" value="1" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${firstPageUrl}" class="page-link">1</a>
            <span class="page-ellipsis">...</span>
        </c:if>

        <!-- Page numbers -->
        <c:forEach begin="${start}" end="${end}" var="pageNum">
            <c:url var="pageUrl" value="${baseUrl}">
                <!-- Add/update the page parameter -->
                <c:param name="${paramName}" value="${pageNum}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${pageUrl}" class="page-link ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>
        </c:forEach>

        <!-- Last page + ellipsis -->
        <c:if test="${end < totalPages}">
            <span class="page-ellipsis">...</span>
            <c:url var="lastPageUrl" value="${baseUrl}">
                <!-- Preserve all existing request parameters -->
                <c:forEach var="p" items="${param}">
                    <c:if test="${p.key != paramName && p.key != 'pageObjectTotalPages' && p.key != 'currentPage' && p.key != 'pageSize' && p.key != 'baseUrl' && p.key != 'paramName' && p.key != 'sizeParamName'}">
                        <c:param name="${p.key}" value="${p.value}" />
                    </c:if>
                </c:forEach>
                <!-- Add/update the page parameter -->
                <c:param name="${paramName}" value="${totalPages}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${lastPageUrl}" class="page-link">${totalPages}</a>
        </c:if>

        <c:if test="${currentPage < totalPages}">
            <c:url var="nextUrl" value="${baseUrl}">
                <c:param name="${paramName}" value="${currentPage + 1}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${nextUrl}" class="page-link"><spring:message code="pagination.next"/> &raquo;</a>
        </c:if>
    </div>
</c:if>