<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="delete-modal" class="attendance-modal">
  <div class="attendance-modal-content">
    <div class="attendance-modal-header">
      <h3 class="attendance-modal-title"><spring:message code="event.confirmDelete"/></h3>
      <button type="button" class="attendance-modal-close" onclick="closeDeleteModal()">
        <svg xmlns="http://www.w3.org/2000/svg" class="modal-close-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>
    <div class="attendance-modal-body">
      <p>${param.warning}</p>
    </div>
    <div class="attendance-modal-footer">
      <button type="button" class="btn-secondary" onclick="closeDeleteModal()">
        <spring:message code="event.confirm.delete.reject" />
      </button>
      <button type="submit" class="btn-danger">
        <spring:message code="event.confirm.delete.accept" />
      </button
    </div>
  </div>
</div>
<script src="<c:url value='/resources/js/confirm-delete.js'/>"></script>