<%--&lt;%&ndash;<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>&ndash;%&gt;--%>
<%--&lt;%&ndash;<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>&ndash;%&gt;--%>
<%--&lt;%&ndash;<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>&ndash;%&gt;--%>

<%--&lt;%&ndash;<link rel="stylesheet" href="<c:url value="/resources/css/components/pagination-with-page-number.css"/>" />&ndash;%&gt;--%>

<%--&lt;%&ndash;&lt;%&ndash;&ndash;%&gt;--%>
<%--&lt;%&ndash;Parameters:&ndash;%&gt;--%>
<%--&lt;%&ndash;- pageObject: The page object containing pagination data (required)&ndash;%&gt;--%>
<%--&lt;%&ndash;- currentPage: The current page number (required)&ndash;%&gt;--%>
<%--&lt;%&ndash;- pageSize: Number of items per page (required)&ndash;%&gt;--%>
<%--&lt;%&ndash;- baseUrl: The base URL for pagination links (required)&ndash;%&gt;--%>
<%--&lt;%&ndash;- paramName: The parameter name for the page (default: "page")&ndash;%&gt;--%>
<%--&lt;%&ndash;- sizeParamName: The parameter name for the page size (default: "size")&ndash;%&gt;--%>
<%--&lt;%&ndash;&ndash;%&gt;&ndash;%&gt;--%>

<%--&lt;%&ndash;<c:if test="${empty paramName}">&ndash;%&gt;--%>
<%--&lt;%&ndash;    <c:set var="paramName" value="page" />&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>

<%--&lt;%&ndash;<c:if test="${empty sizeParamName}">&ndash;%&gt;--%>
<%--&lt;%&ndash;    <c:set var="sizeParamName" value="size" />&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>

<%--&lt;%&ndash;<c:if test="${pageObject.totalPages > 1}">&ndash;%&gt;--%>
<%--&lt;%&ndash;    <div class="pagination">&ndash;%&gt;--%>
<%--&lt;%&ndash;        <!-- Previous -->&ndash;%&gt;--%>
<%--&lt;%&ndash;        <c:if test="${currentPage > 1}">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:url var="prevUrl" value="${baseUrl}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${paramName}" value="${currentPage - 1}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${sizeParamName}" value="${pageSize}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:url>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${prevUrl}" class="page-link">&laquo; Prev</a>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </c:if>&ndash;%&gt;--%>

<%--&lt;%&ndash;        <!-- First page + ellipsis -->&ndash;%&gt;--%>
<%--&lt;%&ndash;        <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;        <c:set var="end" value="${currentPage + 2 > pageObject.totalPages ? pageObject.totalPages : currentPage + 2}" />&ndash;%&gt;--%>

<%--&lt;%&ndash;        <c:if test="${start > 1}">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:url var="firstPageUrl" value="${baseUrl}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${paramName}" value="1" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${sizeParamName}" value="${pageSize}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:url>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${firstPageUrl}" class="page-link">1</a>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <span class="page-ellipsis">...</span>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </c:if>&ndash;%&gt;--%>

<%--&lt;%&ndash;        <!-- Centered page numbers -->&ndash;%&gt;--%>
<%--&lt;%&ndash;        <c:forEach begin="${start}" end="${end}" var="pageNum">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:url var="pageUrl" value="${baseUrl}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${paramName}" value="${pageNum}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${sizeParamName}" value="${pageSize}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:url>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${pageUrl}" class="page-link ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </c:forEach>&ndash;%&gt;--%>

<%--&lt;%&ndash;        <!-- Last page + ellipsis -->&ndash;%&gt;--%>
<%--&lt;%&ndash;        <c:if test="${end < pageObject.totalPages}">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <span class="page-ellipsis">...</span>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:url var="lastPageUrl" value="${baseUrl}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${paramName}" value="${pageObject.totalPages}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${sizeParamName}" value="${pageSize}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:url>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${lastPageUrl}" class="page-link">${pageObject.totalPages}</a>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </c:if>&ndash;%&gt;--%>

<%--&lt;%&ndash;        <!-- Next -->&ndash;%&gt;--%>
<%--&lt;%&ndash;        <c:if test="${currentPage < pageObject.totalPages}">&ndash;%&gt;--%>
<%--&lt;%&ndash;            <c:url var="nextUrl" value="${baseUrl}">&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${paramName}" value="${currentPage + 1}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;                <c:param name="${sizeParamName}" value="${pageSize}" />&ndash;%&gt;--%>
<%--&lt;%&ndash;            </c:url>&ndash;%&gt;--%>
<%--&lt;%&ndash;            <a href="${nextUrl}" class="page-link">Next &raquo;</a>&ndash;%&gt;--%>
<%--&lt;%&ndash;        </c:if>&ndash;%&gt;--%>
<%--&lt;%&ndash;    </div>&ndash;%&gt;--%>
<%--&lt;%&ndash;</c:if>&ndash;%&gt;--%>

<%--<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>--%>
<%--<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>--%>
<%--<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>--%>

<%--<link rel="stylesheet" href="<c:url value="/resources/css/components/pagination-with-page-number.css"/>" />--%>

<%--&lt;%&ndash;--%>
<%--Expected Request Parameters:--%>
<%--- pageObjectTotalPages: total pages (required)--%>
<%--- currentPage: current page number (required)--%>
<%--- pageSize: number of items per page (required)--%>
<%--- baseUrl: base URL for pagination (required)--%>
<%--- paramName: name of page parameter (optional, defaults to 'page')--%>
<%--- sizeParamName: name of size parameter (optional, defaults to 'size')--%>
<%--&ndash;%&gt;--%>

<%--<c:if test="${empty param.paramName}">--%>
<%--    <c:set var="paramName" value="page" />--%>
<%--</c:if>--%>
<%--<c:if test="${not empty param.paramName}">--%>
<%--    <c:set var="paramName" value="${param.paramName}" />--%>
<%--</c:if>--%>

<%--<c:if test="${empty param.sizeParamName}">--%>
<%--    <c:set var="sizeParamName" value="size" />--%>
<%--</c:if>--%>
<%--<c:if test="${not empty param.sizeParamName}">--%>
<%--    <c:set var="sizeParamName" value="${param.sizeParamName}" />--%>
<%--</c:if>--%>

<%--<c:set var="totalPages" value="${param.pageObjectTotalPages}" />--%>
<%--<c:set var="currentPage" value="${param.currentPage}" />--%>
<%--<c:set var="pageSize" value="${param.pageSize}" />--%>
<%--<c:set var="baseUrl" value="${param.baseUrl}" />--%>

<%--<c:if test="${totalPages > 1}">--%>
<%--    <div class="pagination">--%>
<%--        <!-- Previous -->--%>
<%--        <c:if test="${currentPage > 1}">--%>
<%--            <c:url var="prevUrl" value="${baseUrl}">--%>
<%--                <c:param name="${paramName}" value="${currentPage - 1}" />--%>
<%--                <c:param name="${sizeParamName}" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${prevUrl}" class="page-link">&laquo; Prev</a>--%>
<%--        </c:if>--%>

<%--        <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />--%>
<%--        <c:set var="end" value="${currentPage + 2 > totalPages ? totalPages : currentPage + 2}" />--%>

<%--        <!-- First page + ellipsis -->--%>
<%--        <c:if test="${start > 1}">--%>
<%--            <c:url var="firstPageUrl" value="${baseUrl}">--%>
<%--                <c:param name="${paramName}" value="1" />--%>
<%--                <c:param name="${sizeParamName}" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${firstPageUrl}" class="page-link">1</a>--%>
<%--            <span class="page-ellipsis">...</span>--%>
<%--        </c:if>--%>

<%--        <!-- Page numbers -->--%>
<%--        <c:forEach begin="${start}" end="${end}" var="pageNum">--%>
<%--            <c:url var="pageUrl" value="${baseUrl}">--%>
<%--                <c:param name="${paramName}" value="${pageNum}" />--%>
<%--                <c:param name="${sizeParamName}" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${pageUrl}" class="page-link ${pageNum == currentPage ? 'active' : ''}">${pageNum}</a>--%>
<%--        </c:forEach>--%>

<%--        <!-- Last page + ellipsis -->--%>
<%--        <c:if test="${end < totalPages}">--%>
<%--            <span class="page-ellipsis">...</span>--%>
<%--            <c:url var="lastPageUrl" value="${baseUrl}">--%>
<%--                <c:param name="${paramName}" value="${totalPages}" />--%>
<%--                <c:param name="${sizeParamName}" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${lastPageUrl}" class="page-link">${totalPages}</a>--%>
<%--        </c:if>--%>

<%--        <!-- Next -->--%>
<%--        <c:if test="${currentPage < totalPages}">--%>
<%--            <c:url var="nextUrl" value="${baseUrl}">--%>
<%--                <c:param name="${paramName}" value="${currentPage + 1}" />--%>
<%--                <c:param name="${sizeParamName}" value="${pageSize}" />--%>
<%--            </c:url>--%>
<%--            <a href="${nextUrl}" class="page-link">Next &raquo;</a>--%>
<%--        </c:if>--%>
<%--    </div>--%>
<%--</c:if>--%>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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

<c:set var="totalPages" value="${param.pageObjectTotalPages}" />
<c:set var="currentPage" value="${param.currentPage}" />
<c:set var="pageSize" value="${param.pageSize}" />
<c:set var="baseUrl" value="${param.baseUrl}" />

<c:if test="${totalPages > 1}">
    <div class="pagination">
        <!-- Previous -->
        <c:if test="${currentPage > 1}">
            <c:url var="prevUrl" value="${baseUrl}">
                <!-- Preserve all existing request parameters -->
                <c:forEach var="p" items="${param}">
                    <c:if test="${p.key != paramName && p.key != 'pageObjectTotalPages' && p.key != 'currentPage' && p.key != 'pageSize' && p.key != 'baseUrl' && p.key != 'paramName' && p.key != 'sizeParamName'}">
                        <c:param name="${p.key}" value="${p.value}" />
                    </c:if>
                </c:forEach>
                <!-- Add/update the page parameter -->
                <c:param name="${paramName}" value="${currentPage - 1}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${prevUrl}" class="page-link">&laquo; Prev</a>
        </c:if>

        <c:set var="start" value="${currentPage - 2 < 1 ? 1 : currentPage - 2}" />
        <c:set var="end" value="${currentPage + 2 > totalPages ? totalPages : currentPage + 2}" />

        <!-- First page + ellipsis -->
        <c:if test="${start > 1}">
            <c:url var="firstPageUrl" value="${baseUrl}">
                <!-- Preserve all existing request parameters -->
                <c:forEach var="p" items="${param}">
                    <c:if test="${p.key != paramName && p.key != 'pageObjectTotalPages' && p.key != 'currentPage' && p.key != 'pageSize' && p.key != 'baseUrl' && p.key != 'paramName' && p.key != 'sizeParamName'}">
                        <c:param name="${p.key}" value="${p.value}" />
                    </c:if>
                </c:forEach>
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
                <!-- Preserve all existing request parameters -->
                <c:forEach var="p" items="${param}">
                    <c:if test="${p.key != paramName && p.key != 'pageObjectTotalPages' && p.key != 'currentPage' && p.key != 'pageSize' && p.key != 'baseUrl' && p.key != 'paramName' && p.key != 'sizeParamName'}">
                        <c:param name="${p.key}" value="${p.value}" />
                    </c:if>
                </c:forEach>
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

        <!-- Next -->
        <c:if test="${currentPage < totalPages}">
            <c:url var="nextUrl" value="${baseUrl}">
                <!-- Preserve all existing request parameters -->
                <c:forEach var="p" items="${param}">
                    <c:if test="${p.key != paramName && p.key != 'pageObjectTotalPages' && p.key != 'currentPage' && p.key != 'pageSize' && p.key != 'baseUrl' && p.key != 'paramName' && p.key != 'sizeParamName'}">
                        <c:param name="${p.key}" value="${p.value}" />
                    </c:if>
                </c:forEach>
                <!-- Add/update the page parameter -->
                <c:param name="${paramName}" value="${currentPage + 1}" />
                <c:param name="${sizeParamName}" value="${pageSize}" />
            </c:url>
            <a href="${nextUrl}" class="page-link">Next &raquo;</a>
        </c:if>
    </div>
</c:if>