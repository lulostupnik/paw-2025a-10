<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="report.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/detail.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="container">
    <div class="detail-container">
        <div class="featured-journey-card">
            <div class="journey-card-content">
                <div class="journey-card-header">
                    <h1 class="journey-card-title"><c:out value="${report.reportedUser.username}"/></h1>
                    <p class="journey-card-subtitle"><c:out value="${report.reason}"/></p>
                </div>

                <div class="detail-content">
                    <h2 class="section-title-landing"><spring:message code="report.detail.information"/></h2>

                    <div class="features-grid">

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.name"/></h3>
                            <p class="feature-description"><c:out value="${report.reportedUser.name}"/></p>
                        </div>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.reportingUser"/></h3>
                            <p class="feature-description"><c:out value="${report.reportingUser.name}"/></p>
                        </div>
                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.reason"/></h3>
                            <p class="feature-description"><c:out value="${report.reason}"/></p>
                        </div>

                        <c:if test="${report.journey != null}">
<%--                        path al journey me dio paja--%>
                        </c:if>
                        <c:if test="${report.event != null}">
<%--                          path al eveneto --%>
                        </c:if>
                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.description"/></h3>
                            <p class="feature-description"><c:out value="${report.description}"/></p>
                        </div>
                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.status"/></h3>
                            <p class="feature-description"><c:out value="${report.status}"/></p>
                        </div>
                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.date"/></h3>
                            <p class="feature-description">
                                <fmt:formatDate value="${report.createdAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                            </p>
                        </div>


                    </div>
                </div>

                <div class="detail-actions">
                    <a href="<c:url value='/dashboard/reports'/>" class="btn-text">
                        <spring:message code="report.back" text="Back to universities"/>
                    </a>
                    <div class="hero-cta">
                        <button type="button" class="cta-button delete-button" id="deletereportBtn">
                            <spring:message code="report.detail.delete"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<div id="deleteModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2><spring:message code="report.delete.confirm.title"/></h2>
            <button type="button" class="close-modal" aria-label="Close">&times;</button>
        </div>
        <div class="modal-body">
            <p><spring:message code="report.delete.confirm.message"/></p>
            <p class="warning-text"><spring:message code="report.delete.confirm.warning"/></p>
        </div>
        <div class="modal-footer">
            <button type="button" class="cta-button secondary" id="cancelDeleteBtn">
                <spring:message code="report.delete.cancel"/>
            </button>
            <form action="<c:url value='/reports/${report.id}/delete'/>" method="post" id="deletereportForm">
                <button type="submit" class="cta-button delete-button">
                    <spring:message code="report.delete.confirm"/>
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Modal functionality
        const modal = document.getElementById('deleteModal');
        const deleteBtn = document.getElementById('deletereportBtn');
        const cancelBtn = document.getElementById('cancelDeleteBtn');
        const closeModal = document.querySelector('.close-modal');

        deleteBtn.addEventListener('click', function() {
            modal.style.display = 'flex';
        });

        function hideModal() {
            modal.style.display = 'none';
        }

        cancelBtn.addEventListener('click', hideModal);
        closeModal.addEventListener('click', hideModal);

        window.addEventListener('click', function(event) {
            if (event.target === modal) {
                hideModal();
            }
        });
    });
</script>

</body>
</html>
