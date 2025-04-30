<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title><spring:message code="university.detail.title"/></title>
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
          <h1 class="journey-card-title"><c:out value="${university.get().name}"/></h1>
          <p class="journey-card-subtitle"><c:out value="${university.get().abbreviation}"/></p>
        </div>

        <div class="detail-content">
          <h2 class="section-title-landing"><spring:message code="university.detail.information"/></h2>

          <div class="features-grid">
            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="university.detail.id"/></h3>
              <p class="feature-description"><c:out value="${university.get().id}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="university.detail.name"/></h3>
              <p class="feature-description"><c:out value="${university.get().name}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="university.detail.abbreviation"/></h3>
              <p class="feature-description"><c:out value="${university.get().abbreviation}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="university.detail.city"/></h3>
              <p class="feature-description"><c:out value="${university.get().city.name}"/></p>
            </div>
          </div>
        </div>

        <div class="detail-actions">
          <a href="<c:url value='/dashboard/universities'/>" class="btn-text">
            <spring:message code="university.back" text="Back to universities"/>
          </a>
          <div class="hero-cta">
            <a href="<c:url value='/universities/${university.get().id}/edit'/>" class="cta-button primary">
              <spring:message code="university.detail.edit"/>
            </a>
            <button type="button" class="cta-button delete-button" id="deleteUniversityBtn">
              <spring:message code="university.detail.delete"/>
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
      <h2><spring:message code="university.delete.confirm.title"/></h2>
      <button type="button" class="close-modal" aria-label="Close">&times;</button>
    </div>
    <div class="modal-body">
      <p><spring:message code="university.delete.confirm.message"/></p>
      <p class="warning-text"><spring:message code="university.delete.confirm.warning"/></p>
    </div>
    <div class="modal-footer">
      <button type="button" class="cta-button secondary" id="cancelDeleteBtn">
        <spring:message code="university.delete.cancel"/>
      </button>
      <form action="<c:url value='/universities/${university.get().id}/delete'/>" method="post" id="deleteUniversityForm">
        <input type="hidden" name="_method" value="DELETE">
        <button type="submit" class="cta-button delete-button">
          <spring:message code="university.delete.confirm"/>
        </button>
      </form>
    </div>
  </div>
</div>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Modal functionality
    const modal = document.getElementById('deleteModal');
    const deleteBtn = document.getElementById('deleteUniversityBtn');
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
