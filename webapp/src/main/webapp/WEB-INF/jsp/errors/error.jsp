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

<link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />

<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><fmt:message key="error.${errorType}.title" /></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/error-styles.css"/>"/>
    <script>
        // Check for dark mode preference
        if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            document.documentElement.classList.add('dark-mode');
        }
    </script>
</head>
<body>
<div class="container">
    <main id="content">
        <div class="error-content">
            <h1 class="error-title">
                <fmt:message key="error.${errorType}.title" />
            </h1>
            <p class="error-message">
                <fmt:message key="error.${errorType}.message" />
            </p>
            <p class="error-description">
                <fmt:message key="error.${errorType}.description" />
            </p>

            <%-- Display exception details in development mode --%>
            <c:if test="${not empty param.debug && not empty throwable}">
                <div class="exception-details">
                    <h3 class="exception-title">Exception Details:</h3>
                    <p class="exception-message">${throwable.message}</p>
                    <pre class="exception-stack-trace">
                        <c:forEach var="stackTraceElement" items="${throwable.stackTrace}">
                            ${stackTraceElement}
                        </c:forEach>
                    </pre>
                </div>
            </c:if>

            <div class="button-container">
                <a class="back-button" href="<c:url value="/home"/>">
                    <svg class="back-icon" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="m15 18-6-6 6-6"/></svg>
                    <fmt:message key="button.back" />
                </a>
            </div>
        </div>
    </main>

    <footer>
        <div class="footer-content">
            <p class="copyright-text">
                <fmt:message key="footer.copyright" /> <%= java.time.Year.now().getValue() %>
            </p>
        </div>
    </footer>
</div>
</body>
</html>