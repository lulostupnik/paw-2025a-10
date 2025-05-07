<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
<link rel="stylesheet" href="<c:url value='/resources/css/detail.css'/>" />

<div class="tab-content active" id="users-tab">
  <c:set var="titleMessageCode" value="admin.manage.users" scope="request" />
  <c:set var="searchUrl" value="/dashboard/users" scope="request" />
  <c:set var="searchPlaceholderCode" value="admin.search.users" scope="request" />

  <div class="content-header">
    <h2><spring:message code="${titleMessageCode}" /></h2>
    <div class="action-bar">
      <div class="actions-container">
        <form action="<c:url value='${searchUrl}'/>" method="get" class="search-form">
          <input type="text" name="search" class="search-input"
                 placeholder="<spring:message code='${searchPlaceholderCode}' />"
                 value="<c:out value="${param.search}"/>">          <input type="hidden" name="page" value="1">
          <input type="hidden" name="pageSize" value="${param.pageSize != null ? param.pageSize : 10}">
          <button type="submit" class="btn-secondary" aria-label="<spring:message code="admin.search.button" />">
            <img src="<c:url value='/resources/icons/search.svg'/>" alt="<spring:message code="admin.search.button" />" class="search-icon" />
          </button>
        </form>
      </div>
    </div>
  </div>
  <div class="table-container">
    <table class="data-table">
      <thead>
      <tr>
        <th><spring:message code="admin.column.name" /></th>
        <th><spring:message code="admin.column.email" /></th>
        <th><spring:message code="admin.column.university" /></th>
        <th><spring:message code="admin.column.actions" /></th>
      </tr>
      </thead>
      <tbody>
      <c:set var="users" value="${pagedUsers.content}" />
      <c:forEach items="${users}" var="user">
        <tr class="clickable-row" data-href="<c:url value="../users/${user.id}"/>" >
          <td><c:out value="${user.firstname}"/></td>
          <td><c:out value="${user.email}"/></td>
          <td><c:out value="${user.university}"/></td>
          <td>
            <c:if test="${! user.blocked}">
              <button type="button" class="btn-danger btn-with-icon block-user-btn"
                      data-user-id="<c:out value="${user.id}"/>"
                      data-user-name="<c:out value="${user.firstname}"/>"
                      data-action="block">
                <img class="btn-icon" alt="<spring:message code="user.block"/>" src="<c:url value="/resources/icons/block.svg"/>"/>
              </button>
            </c:if>
            <c:if test="${user.blocked}">
              <button type="button" class="btn-primary btn-with-icon block-user-btn"
                      data-user-id="<c:out value="${user.id}"/>"
                      data-user-name="<c:out value="${user.firstname}"/>"
                      data-action="unblock">
                <img class="btn-icon" alt="<spring:message code="user.unblock"/>" src="<c:url value="/resources/icons/unblock.svg"/>"/>
              </button>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

    <c:if test="${empty users}">
      <div class="no-results">
        <spring:message code="admin.no.results" />
      </div>
    </c:if>
    <c:set var="search" ><c:out value="${param.search}"/></c:set>

    <jsp:include page="../../components/pagination-with-page-number.jsp">
      <jsp:param name="pageObjectTotalPages" value="${pagedUsers.totalPages}" />
      <jsp:param name="currentPage" value="${pagedUsers.currentPage}" />
      <jsp:param name="pageSize" value="10" />
      <jsp:param name="baseUrl" value="/dashboard/users?search=${search}" />
    </jsp:include>
  </div>

  <!-- Block/Unblock User Modal -->
  <div id="blockUserModal" class="modal">
    <div class="modal-content">
      <div class="modal-header">
        <h2 id="blockModalTitle"><spring:message code="user.block.confirm.title"/></h2>
        <button type="button" class="close-modal" aria-label="Close">&times;</button>
      </div>
      <div class="modal-body">
        <p id="blockModalMessage"></p>
        <p id="blockModalWarning" class="warning-text"><spring:message code="user.block.confirm.warning"/></p>
      </div>
      <div class="modal-footer">
        <button type="button" class="cta-button secondary" id="cancelBlockBtn">
          <spring:message code="user.block.cancel"/>
        </button>
        <form id="blockUserForm" method="post">
          <button type="submit" class="cta-button" id="confirmBlockBtn">
            <spring:message code="user.block.confirm"/>
          </button>
        </form>
      </div>
    </div>
  </div>
</div>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Get modal elements
    const modal = document.getElementById('blockUserModal');
    const modalTitle = document.getElementById('blockModalTitle');
    const modalMessage = document.getElementById('blockModalMessage');
    const modalWarning = document.getElementById('blockModalWarning');
    const blockForm = document.getElementById('blockUserForm');
    const confirmBtn = document.getElementById('confirmBlockBtn');
    const cancelBtn = document.getElementById('cancelBlockBtn');
    const closeBtn = document.querySelector('.close-modal');

    // Get all block/unblock buttons
    const blockBtns = document.querySelectorAll('.block-user-btn');

    // Add click event to all block/unblock buttons
    blockBtns.forEach(function(btn) {
      btn.addEventListener('click', function(e) {
        e.stopPropagation(); // Prevent row click

        const userId = this.getAttribute('data-user-id');
        const userName = this.getAttribute('data-user-name');
        const action = this.getAttribute('data-action');

        // Set modal content based on action
        if (action === 'block') {
          modalTitle.textContent = '<spring:message code="user.block.confirm.title"/>';
          modalMessage.textContent = '<spring:message code="user.block.confirm.message" arguments="' + userName + '"/>';
          confirmBtn.textContent = '<spring:message code="user.block.confirm"/>';
          confirmBtn.className = 'cta-button delete-button';
          modalWarning.style.display = 'block';
          blockForm.action = '<c:url value="/users/"/>' + userId + '/block/';
        } else {
          modalTitle.textContent = '<spring:message code="user.unblock.confirm.title"/>';
          modalMessage.textContent = '<spring:message code="user.unblock.confirm.message" arguments="' + userName + '"/>';
          confirmBtn.textContent = '<spring:message code="user.unblock.confirm"/>';
          confirmBtn.className = 'cta-button primary';
          modalWarning.style.display = 'none';
          blockForm.action = '<c:url value="/users/"/>' + userId + '/unblock/';
        }

        // Show modal
        modal.style.display = 'flex';
      });
    });

    // Close modal functions
    function closeModal() {
      modal.style.display = 'none';
    }

    cancelBtn.addEventListener('click', closeModal);
    closeBtn.addEventListener('click', closeModal);

    // Close modal when clicking outside
    window.addEventListener('click', function(e) {
      if (e.target === modal) {
        closeModal();
      }
    });

    // Make table rows clickable
    const rows = document.querySelectorAll('.clickable-row');
    rows.forEach(function(row) {
      row.addEventListener('click', function() {
        window.location.href = this.getAttribute('data-href');
      });
    });
  });
</script>