<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <c:choose>
        <c:when test="${isUpdate}">
            <title><spring:message code="journey.tip.update.title" text="Update Tip"/></title>
        </c:when>
        <c:otherwise>
            <title><spring:message code="journey.tip.add" text="Add New Tip"/></title>
        </c:otherwise>
    </c:choose>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
    <link rel="stylesheet" href="<c:url value="/resources/css/event-detail.css"/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>

<body>
<div class="layout-container">
    <div class="main-content">
        <jsp:include page="../../components/navbar.jsp" />
        <div class="content-container">

            <div class="back-button-container">
                <button onclick="goBack()" class="back-link">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                        <path d="M19 12H5"></path>
                        <path d="M12 19l-7-7 7-7"></path>
                    </svg>
                    <span><spring:message code="journey.tip.back.to.journey" text="Back to Journey" /></span>
                </button>
            </div>

            <div class="event-detail-container">
                <div class="event-top-section">
                    <div class="event-header">
                        <h1 class="event-title">
                            <c:choose>
                                <c:when test="${isUpdate}">
                                    <spring:message code="journey.tip.update.title" text="Update Tip" />
                                </c:when>
                                <c:otherwise>
                                    <spring:message code="journey.tip.add" text="Share a New Tip" />
                                </c:otherwise>
                            </c:choose>
                        </h1>
                    </div>

                    <div class="journey-info-card">
                        <div class="journey-info-header">
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            <span>
                                <c:choose>
                                    <c:when test="${isUpdate}">
                                        <spring:message code="journey.tip.updating.for.journey" text="Updating tip for journey:" />
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="journey.tip.for.journey" text="Adding tip for journey:" />
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                        <div class="journey-info-content">
                            <h3 class="journey-info-title">
                                <c:set var="escapedFirstname"><c:out value="${journey.user.firstname}"/></c:set>
                                <c:set var="escapedLastname"><c:out value="${journey.user.lastname}"/></c:set>
                                <spring:message arguments="${escapedFirstname},${escapedLastname}" code="journey.detail.section.title" />
                            </h3>
                            <div class="journey-info-meta">
                                <div class="meta-item">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                        <circle cx="12" cy="10" r="3"></circle>
                                    </svg>
                                    <span class="destination-text">
                                        <c:out value="${journey.destinationUniversity.city}" /> -
                                        <c:out value="${journey.destinationUniversity.name}" />
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>

                    <c:if test="${isUpdate}">
                        <div class="tip-info-card">
                            <div class="tip-info-header">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                    <circle cx="12" cy="12" r="3"></circle>
                                </svg>
                                <span><spring:message code="journey.tip.editing" text="Editing tip:" /></span>
                            </div>
                            <div class="tip-info-content">
                                <h4 class="tip-info-title">
                                    <c:out value="${createTipForm.title}" />
                                </h4>
                            </div>
                        </div>
                    </c:if>

                    <div class="tip-form-container">
                        <h3 class="tip-form-title">
                            <c:choose>
                                <c:when test="${isUpdate}">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                    <spring:message code="journey.tip.update.form.title" text="Update Tip Details" />
                                </c:when>
                                <c:otherwise>
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                    <spring:message code="journey.tip.form.title" text="Tip Details" />
                                </c:otherwise>
                            </c:choose>
                        </h3>

                        <c:choose>
                            <c:when test="${isUpdate}">
                                <c:url var="formActionUrl" value="/journeys/tips/${tip.id}/update"/>
                            </c:when>
                            <c:otherwise>
                                <c:url var="formActionUrl" value="/journeys/${journey.id}/tips/create"/>
                            </c:otherwise>
                        </c:choose>
                        <form:form modelAttribute="createTipForm" action="${formActionUrl}" method="post" enctype="multipart/form-data" cssClass="tip-form">

                        <c:set var="titleLabel"><spring:message code="tip.title" text="Tip Title"/></c:set>
                        <c:set var="titleHint"><spring:message code="tip.title.hint" text="Give your tip a descriptive title"/></c:set>
                        <jsp:include page="../../components/text-field.jsp">
                            <jsp:param name="path" value="title" />
                            <jsp:param name="label" value="${titleLabel}" />
                            <jsp:param name="placeholder" value="${titleHint}" />
                            <jsp:param name="required" value="true" />
                        </jsp:include>

                        <c:set var="contentLabel"><spring:message code="tip.content" text="Tip Content"/></c:set>
                        <c:set var="contentHint"><spring:message code="tip.content.hint" text="Share your advice, insights, or helpful information"/></c:set>
                        <jsp:include page="../../components/text-area.jsp">
                            <jsp:param name="path" value="content" />
                            <jsp:param name="label" value="${contentLabel}" />
                            <jsp:param name="placeholder" value="${contentHint}" />
                            <jsp:param name="required" value="true" />
                            <jsp:param name="rows" value="6" />
                        </jsp:include>

                        <div class="form-actions">
                            <c:url var="cancelUrl" value="/journeys/${journey.id}"/>
                            <a href="${cancelUrl}" class="btn-cancel">
                                <spring:message code="tip.cancel" text="Cancel" />
                            </a>
                            <c:choose>
                                <c:when test="${isUpdate}">
                                    <c:set var="submitButtonLabel"><spring:message code="journey.tip.update.title" text="Update Tip"/></c:set>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="submitButtonLabel"><spring:message code="tip.submit" text="Share Tip"/></c:set>
                                </c:otherwise>
                            </c:choose>
                            <jsp:include page="../../components/button.jsp">
                                <jsp:param name="label" value="${submitButtonLabel}" />
                                <jsp:param name="type" value="submit" />
                            </jsp:include>
                        </div>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>

<script>
    function goBack(){
        const rutaAnterior = popFromNavigationStack()
        if (rutaAnterior) {
            window.location.href = rutaAnterior;
        } else {
            window.location.href = "<c:url value='/journeys/${journey.id}'/>"
        }
    }
</script>

<style>
    .journey-info-card {
        background-color: #f9fafb;
        border-radius: 0.75rem;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        border-left: 4px solid #4f46e5;
    }

    .journey-info-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        color: #6b7280;
        margin-bottom: 1rem;
    }

    .journey-info-content {
        margin-left: 1.5rem;
    }

    .journey-info-title {
        font-size: 1.25rem;
        font-weight: 600;
        color: #111827;
        margin: 0 0 0.75rem;
    }

    .journey-info-meta {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        color: #4b5563;
        font-size: 0.875rem;
    }

    .tip-info-card {
        background-color: #fff7ed;
        border-radius: 0.75rem;
        padding: 1.5rem;
        margin-bottom: 2rem;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        border-left: 4px solid #f59e0b;
    }

    .tip-info-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 0.875rem;
        color: #92400e;
        margin-bottom: 1rem;
    }

    .tip-info-content {
        margin-left: 1.5rem;
    }

    .tip-info-title {
        font-size: 1.125rem;
        font-weight: 600;
        color: #111827;
        margin: 0 0 0.5rem;
    }

    .tip-info-date {
        font-size: 0.875rem;
        color: #6b7280;
        margin: 0;
    }

    .tip-form-container {
        background-color: #ffffff;
        border-radius: 0.75rem;
        padding: 2rem;
        border: 1px solid #e5e7eb;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    }

    .tip-form-title {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 1.5rem;
        font-weight: 600;
        color: #111827;
        margin: 0 0 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 1px solid #e5e7eb;
    }

    .tip-form {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
    }

    .form-actions {
        display: flex;
        justify-content: flex-end;
        gap: 1rem;
        margin-top: 2rem;
        padding-top: 1.5rem;
        border-top: 1px solid #e5e7eb;
    }

    .btn-cancel {
        display: inline-flex;
        align-items: center;
        padding: 0.75rem 1.5rem;
        border-radius: 0.5rem;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s ease;
        background-color: #f3f4f6;
        color: #4b5563;
        border: 1px solid #d1d5db;
    }

    .btn-cancel:hover {
        background-color: #e5e7eb;
        color: #374151;
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    @media (max-width: 768px) {
        .tip-form-container {
            padding: 1.5rem;
        }

        .form-actions {
            flex-direction: column-reverse;
        }

        .btn-cancel {
            width: 100%;
            justify-content: center;
        }
    }
</style>

</body>
</html>