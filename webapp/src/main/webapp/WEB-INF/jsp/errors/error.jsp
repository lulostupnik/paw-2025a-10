<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- Set the locale based on the browser's language --%>
<fmt:setLocale value="${pageContext.request.locale}" />
<fmt:setBundle basename="i18n.messages" />

<%
    // Get the exception
    Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
    Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
    String errorType = "general";

    if (statusCode != null) {
        if (statusCode == 404) {
            errorType = "404";
        } else if (statusCode == 500) {
            errorType = "500";
        } else if (statusCode == 403) {
            errorType = "403";
        }
    } else if (throwable != null) {
        // Determine error type based on exception class
        if (throwable instanceof java.io.FileNotFoundException) {
            errorType = "404";
        } else if (throwable instanceof java.lang.SecurityException) {
            errorType = "403";
        } else {
            errorType = "500";
        }
    }

    request.setAttribute("errorType", errorType);
%>

<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="error.${errorType}.title" /> - <fmt:message key="header.brand" /></title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            darkMode: 'class',
            theme: {
                extend: {}
            }
        }
    </script>
</head>
<body class="flex flex-col min-h-screen bg-white dark:bg-gray-900">
<div class="max-w-3xl flex flex-col mx-auto min-h-screen">
    <!-- ========== HEADER ========== -->
    <header class="mb-auto flex justify-center z-50 w-full py-4">
        <nav class="px-4 sm:px-6 lg:px-8">
            <a class="flex-none text-xl font-semibold sm:text-3xl text-gray-900 dark:text-white" href="${pageContext.request.contextPath}/" aria-label="<fmt:message key="header.brand" />">
                <fmt:message key="header.brand" />
            </a>
        </nav>
    </header>
    <!-- ========== END HEADER ========== -->

    <!-- ========== MAIN CONTENT ========== -->
    <main id="content">
        <div class="text-center py-10 px-4 sm:px-6 lg:px-8">
            <h1 class="block text-7xl font-bold text-gray-800 sm:text-9xl dark:text-white">
                <fmt:message key="error.${errorType}.title" />
            </h1>
            <p class="mt-3 text-gray-600 dark:text-neutral-400">
                <fmt:message key="error.${errorType}.message" />
            </p>
            <p class="text-gray-600 dark:text-neutral-400">
                <fmt:message key="error.${errorType}.description" />
            </p>

            <%-- Display exception details in development mode --%>
            <c:if test="${not empty param.debug && not empty throwable}">
                <div class="mt-6 p-4 bg-gray-100 dark:bg-gray-800 rounded-lg text-left overflow-auto max-h-64">
                    <h3 class="font-bold text-red-600 dark:text-red-400">Exception Details:</h3>
                    <p class="text-sm text-gray-700 dark:text-gray-300">${throwable.message}</p>
                    <pre class="mt-2 text-xs text-gray-600 dark:text-gray-400 overflow-auto">
                            <c:forEach var="stackTraceElement" items="${throwable.stackTrace}">
                                ${stackTraceElement}
                            </c:forEach>
                        </pre>
                </div>
            </c:if>

            <div class="mt-5 flex flex-col justify-center items-center gap-2 sm:flex-row sm:gap-3">
                <a class="w-full sm:w-auto py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-hidden focus:bg-blue-700 disabled:opacity-50 disabled:pointer-events-none" href="${pageContext.request.contextPath}/">
                    <svg class="shrink-0 size-4" xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
                    <fmt:message key="button.back" />
                </a>
            </div>
        </div>
    </main>
    <!-- ========== END MAIN CONTENT ========== -->

    <!-- ========== FOOTER ========== -->
    <footer class="mt-auto text-center py-5">
        <div class="max-w-[85rem] mx-auto px-4 sm:px-6 lg:px-8">
            <p class="text-sm text-gray-500 dark:text-neutral-500">
                <fmt:message key="footer.copyright" /> <%= java.time.Year.now().getValue() %>
            </p>
        </div>
    </footer>
    <!-- ========== END FOOTER ========== -->
</div>
</body>
</html>