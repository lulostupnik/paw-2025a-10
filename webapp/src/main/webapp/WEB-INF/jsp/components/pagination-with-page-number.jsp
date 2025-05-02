<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<link rel="stylesheet" href="<c:url value="/resources/css/components/pagination-with-page-number.css"/>" />

<%--
Parameters:
- pageObject: The page object containing pagination data (required)
- currentPage: The current page number (required)
- pageSize: Number of items per page (required)
- baseUrl: The base URL for pagination links (required)
- paramName: The parameter name for the page (default: "page")
- sizeParamName: The parameter name for the page size (default: "size")
--%>

<c:if test="${empty paramName}">
    <c:set var="paramName" value="page" />
</c:if>

<c:if test="${empty sizeParamName}">
    <c:set var="sizeParamName" value="size" />
</c:if>

<c:if test="${pageObject.totalPages > 1}">
    <div class="pagination">
        <!-- Previous -->
        <c:if test="${currentPage > 1}">
            <c:url var="prevUrl" value="${baseUrl}">
                <c:param name="${paramName}" value="${currentPage - 1}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${prevUrl}" class="page-link">&laquo; Prev</a>
        </c:if>

        <!-- First page + ellipsis -->
        <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />
        <c:set var="end" value="${currentPage + 2 > pageObject.totalPages ? pageObject.totalPages : currentPage + 2}" />

        <c:if test="${start > 1}">
            <c:url var="firstPageUrl" value="${baseUrl}">
                <c:param name="${paramName}" value="1" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${firstPageUrl}" class="page-link">1</a>
            <span class="page-ellipsis">...</span>
        </c:if>

        <!-- Centered page numbers -->
        <c:forEach begin="${start}" end="${end}" var="pageNum">
            <c:url var="pageUrl" value="${baseUrl}">
                <c:param name="${paramName}" value="${pageNum}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${pageUrl}" class="page-link ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>
        </c:forEach>

        <!-- Last page + ellipsis -->
        <c:if test="${end < pageObject.totalPages}">
            <span class="page-ellipsis">...</span>
            <c:url var="lastPageUrl" value="${baseUrl}">
                <c:param name="${paramName}" value="${pageObject.totalPages}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${lastPageUrl}" class="page-link">${pageObject.totalPages}</a>
        </c:if>

        <!-- Next -->
        <c:if test="${currentPage < pageObject.totalPages}">
            <c:url var="nextUrl" value="${baseUrl}">
                <c:param name="${paramName}" value="${currentPage + 1}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${nextUrl}" class="page-link">Next &raquo;</a>
        </c:if>
    </div>
</c:if>