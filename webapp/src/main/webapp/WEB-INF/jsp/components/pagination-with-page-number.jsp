<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<link rel="stylesheet" href="<c:url value="/resources/css/components/pagination-with-page-number.css"/>" />

<c:if test="${eventsPage.totalPages > 1}">
    <div class="pagination">
        <!-- Previous -->
        <c:if test="${currentPage > 1}">
            <c:url var="prevUrl" value="/events">
                <c:param name="page" value="${currentPage - 1}" />
                <c:param name="size" value="${pageSize}" />
            </c:url>
            <a href="${prevUrl}" class="page-link">&laquo; Prev</a>
        </c:if>

        <!-- First page + ellipsis -->
        <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />
        <c:set var="end" value="${currentPage + 2 > eventsPage.totalPages ? eventsPage.totalPages : currentPage + 2}" />

        <c:if test="${start > 1}">
            <c:url var="firstPageUrl" value="/events">
                <c:param name="page" value="1" />
                <c:param name="size" value="${pageSize}" />
            </c:url>
            <a href="${firstPageUrl}" class="page-link">1</a>
            <span class="page-ellipsis">...</span>
        </c:if>

        <!-- Centered page numbers -->
        <c:forEach begin="${start}" end="${end}" var="pageNum">
            <c:url var="pageUrl" value="/events">
                <c:param name="page" value="${pageNum}" />
                <c:param name="size" value="${pageSize}" />
            </c:url>
            <a href="${pageUrl}" class="page-link ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>
        </c:forEach>

        <!-- Last page + ellipsis -->
        <c:if test="${end < eventsPage.totalPages}">
            <span class="page-ellipsis">...</span>
            <c:url var="lastPageUrl" value="/events">
                <c:param name="page" value="${eventsPage.totalPages}" />
                <c:param name="size" value="${pageSize}" />
            </c:url>
            <a href="${lastPageUrl}" class="page-link">${eventsPage.totalPages}</a>
        </c:if>

        <!-- Next -->
        <c:if test="${currentPage < eventsPage.totalPages}">
            <c:url var="nextUrl" value="/events">
                <c:param name="page" value="${currentPage + 1}" />
                <c:param name="size" value="${pageSize}" />
            </c:url>
            <a href="${nextUrl}" class="page-link">Next &raquo;</a>
        </c:if>
    </div>
</c:if>