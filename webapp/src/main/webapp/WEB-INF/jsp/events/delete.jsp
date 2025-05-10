<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="event.delete"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<div class="layout-container">
    <div class="main-content">
        <div class="content-container">
            <!-- Back Navigation -->
            <div class="back-navigation">
                <a href="<c:url value='/events/${event.id}'/>" class="back-link">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                        <path d="M19 12H5"></path>
                        <path d="M12 19l-7-7 7-7"></path>
                    </svg>
                    <span><spring:message code="event.back" /></span>
                </a>
            </div>

            <!-- Delete Event Form -->
            <div class="content-card">
                <div class="card-header">
                    <h1 class="card-title"><spring:message code="event.delete" /></h1>
                </div>
                <div class="card-content">
                    <div class="journey-summary">
                        <h3><spring:message code="event.delete.summary" /></h3>
                        <ul class="summary-list">
                            <li><strong><spring:message code="event.name" />:</strong> <c:out value="${event.title}" /></li>
                            <li><strong><spring:message code="event.location" />:</strong> <c:out value="${event.eventCity.name}" /></li>
                            <li><strong><spring:message code="event.date" />:</strong> <c:out value="${event.date}" /></li>
                            <c:if test="${event.time.isPresent()}">
                                <li><strong><spring:message code="event.time" />:</strong> <c:out value="${event.time.get()}" /></li>
                            </c:if>
                            <li><strong><spring:message code="event.attendees" />:</strong> <c:out value="${event.attendeesCount}" /></li>
                            <li><strong><spring:message code="event.responses" />:</strong> <c:out value="${commentsCount}" /></li>
                        </ul>
                    </div>
                    <div class="warning-message">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path>
                            <line x1="12" y1="9" x2="12" y2="13"></line>
                            <line x1="12" y1="17" x2="12.01" y2="17"></line>
                        </svg>
                        <p><spring:message code="event.deleteWarning" /></p>
                    </div>

                    <c:url var="deleteUrl" value='/events/${event.id}/delete'/>
                    <form:form modelAttribute="deleteForm" action="${deleteUrl}" method="post" class="delete-form">
                        <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                        <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                        <jsp:include page="../components/text-area.jsp">
                            <jsp:param name="path" value="message" />
                            <jsp:param name="label" value="${messageLabel}" />
                            <jsp:param name="placeholder" value="${messagePlaceholder}" />
                        </jsp:include>

                        <div class="form-actions">
                            <a href="<c:url value='/events/${event.id}'/>" class="btn-secondary">
                                <spring:message code="event.cancel" />
                            </a>
                            <button type="submit" class="btn-danger">
                                <spring:message code="event.delete" />
                            </button>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>