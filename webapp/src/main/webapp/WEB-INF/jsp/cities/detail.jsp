<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title><spring:message code="interest.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/university-detail.css'/>" />
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
                    <h1 class="journey-card-title"><c:out value="${city.name}"/></h1>
                </div>

                <div class="detail-content">
                    <h2 class="section-title-landing"><spring:message code="interest.detail.information"/></h2>

                    <div class="features-grid">
                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="interest.detail.id"/></h3>
                            <p class="feature-description"><c:out value="${city.id}"/></p>
                        </div>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="interest.detail.name"/></h3>
                            <p class="feature-description"><c:out value="${city.name}"/></p>
                        </div>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="city.detail.country"/></h3>
                            <p class="feature-description"><c:out value="${city.country}"/></p>
                        </div>
                    </div>
                </div>

                <div class="detail-actions">
                    <a href="<c:url value='/dashboard/cities'/>" class="btn-text">
                        <spring:message code="back" text="Back"/>
                    </a>
                    <div class="hero-cta">
                        <a href="<c:url value='/cities/${city.id}/edit'/>" class="cta-button primary">
                            <spring:message code="interest.detail.edit"/>
                        </a>
                        <button type="button" class="cta-button delete-button" id="deleteCityBtn">
                            <spring:message code="interests.detail.delete"/>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2><spring:message code="interest.delete.confirm.title"/></h2>
            <button type="button" class="close-modal" aria-label="Close">&times;</button>
        </div>
        <div class="modal-body">
            <p><spring:message code="interest.delete.confirm.message"/></p>
            <p class="warning-text"><spring:message code="interest.delete.confirm.warning"/></p>
        </div>
        <div class="modal-footer">
            <button type="button" class="cta-button secondary" id="cancelDeleteBtn">
                <spring:message code="interest.delete.cancel"/>
            </button>
            <form action="<c:url value='/cities/${city.id}/delete'/>" method="post" id="deleteCityForm">
                <button type="submit" class="cta-button delete-button">
                    <spring:message code="interest.delete.confirm"/>
                </button>
            </form>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Modal functionality
        const modal = document.getElementById('deleteModal');
        const deleteBtn = document.getElementById('deleteCityBtn');
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
