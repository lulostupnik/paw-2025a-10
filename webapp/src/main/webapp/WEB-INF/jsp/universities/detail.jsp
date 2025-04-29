<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title><spring:message code="university.detail.title"/></title>
  <!-- Include custom CSS -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/university-detail.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="detail-container">
  <div class="detail-card">
    <div class="detail-header">
      <div class="detail-logo">
        <svg xmlns="http://www.w3.org/2000/svg" class="detail-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
        </svg>
      </div>
      <h1 class="detail-title"><c:out value="${university.name}"/></h1>
      <p class="detail-subtitle"><c:out value="${university.abbreviation}"/></p>
    </div>

    <div class="detail-content">
      <div class="detail-section">
        <h2 class="section-title"><spring:message code="university.detail.information"/></h2>

        <div class="detail-row">
          <div class="detail-label"><spring:message code="university.detail.id"/></div>
          <div class="detail-value"><c:out value="${university.id}"/></div>
        </div>

        <div class="detail-row">
          <div class="detail-label"><spring:message code="university.detail.name"/></div>
          <div class="detail-value"><c:out value="${university.name}"/></div>
        </div>

        <div class="detail-row">
          <div class="detail-label"><spring:message code="university.detail.abbreviation"/></div>
          <div class="detail-value"><c:out value="${university.abbreviation}"/></div>
        </div>

        <div class="detail-row">
          <div class="detail-label"><spring:message code="university.detail.city"/></div>
          <div class="detail-value"><c:out value="${university.city.name}"/></div>
        </div>
      </div>

    </div>

    <div class="detail-actions">
      <a href="<c:url value='/universities/${university.id}/edit'/>" class="action-button edit-button">
        <svg xmlns="http://www.w3.org/2000/svg" class="action-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
        </svg>
        <spring:message code="university.detail.edit"/>
      </a>

      <button type="button" class="action-button delete-button" id="deleteUniversityBtn">
        <svg xmlns="http://www.w3.org/2000/svg" class="action-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
        </svg>
        <spring:message code="university.detail.delete"/>
      </button>
    </div>

    <div class="detail-footer">
      <a href="<c:url value='/dashboard/universities'/>" class="detail-link">
        <svg xmlns="http://www.w3.org/2000/svg" class="detail-link-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        <spring:message code="university.back" text="Back to universities"/>
      </a>
    </div>
  </div>
</div>

<!-- Delete Confirmation Modal -->
<div id="deleteModal" class="modal" style="display: none;">
  <div class="modal-content">
    <div class="modal-header">
      <h2><spring:message code="university.delete.confirm.title"/></h2>
      <span class="close-modal">&times;</span>
    </div>
    <div class="modal-body">
      <p><spring:message code="university.delete.confirm.message"/></p>
      <p class="warning-text"><spring:message code="university.delete.confirm.warning"/></p>
    </div>
    <div class="modal-footer">
      <button type="button" class="modal-button cancel-button" id="cancelDeleteBtn">
        <spring:message code="university.delete.cancel"/>
      </button>
      <form action="<c:url value='/universities/${university.id}/delete'/>" method="post" id="deleteUniversityForm">
        <input type="hidden" name="_method" value="DELETE">
        <button type="submit" class="modal-button confirm-button">
          <spring:message code="university.delete.confirm"/>
        </button>
      </form>
    </div>
  </div>
</div>

<!-- Include JavaScript files -->
<script src="<c:url value='/resources/js/detail-page.js'/>"></script>
<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Modal functionality
    const modal = document.getElementById('deleteModal');
    const deleteBtn = document.getElementById('deleteUniversityBtn');
    const cancelBtn = document.getElementById('cancelDeleteBtn');
    const closeModal = document.querySelector('.close-modal');

    deleteBtn.addEventListener('click', function() {
      modal.style.display = 'block';
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