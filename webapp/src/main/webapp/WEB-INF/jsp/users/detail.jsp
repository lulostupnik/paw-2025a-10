<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
  <title><spring:message code="user.detail.title"/></title>
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
          <h1 class="journey-card-title"><c:out value="${user.firstname} ${user.lastname}"/></h1>
          <p class="journey-card-subtitle"><c:out value="${user.username}"/></p>
        </div>

        <div class="detail-content">
          <h2 class="section-title-landing"><spring:message code="user.detail.information"/></h2>

          <div class="features-grid">
            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.id"/></h3>
              <p class="feature-description"><c:out value="${user.id}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.email"/></h3>
              <p class="feature-description"><c:out value="${user.email}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.firstname"/></h3>
              <p class="feature-description"><c:out value="${user.firstname}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.lastname"/></h3>
              <p class="feature-description"><c:out value="${user.lastname}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.university"/></h3>
              <p class="feature-description"><c:out value="${user.university.name}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.career"/></h3>
              <p class="feature-description"><c:out value="${user.career.name}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.language"/></h3>
              <p class="feature-description"><c:out value="${user.locale}"/></p>
            </div>

            <div class="feature-card">
              <h3 class="feature-title"><spring:message code="user.detail.profilePicture"/></h3>
              <div class="profile-picture-container">
                <img src="<c:url value='/images/${user.profilePictureId}'/>" alt="<spring:message code="user.detail.profilePicture.alt"/>" class="profile-picture" />
              </div>
            </div>

          </div>
        </div>
        <div class="detail-actions">
          <a href="<c:url value='/dashboard/users'/>" class="btn-text">
            <spring:message code="users.back" text="Go Back"/>
          </a>
          <c:choose>
            <c:when test="${user.blocked}">
              <button type="button" class="cta-button primary" id="unblockUserBtn">
                <spring:message code="user.detail.unblock" text="Unblock User"/>
              </button>
            </c:when>
            <c:otherwise>
              <button type="button" class="cta-button delete-button" id="blockUserBtn">
                <spring:message code="user.detail.block" text="Block User"/>
              </button>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Block Confirmation Modal -->
<div id="blockModal" class="modal">
  <div class="modal-content">
    <div class="modal-header">
      <h2 id="blockModalTitle">
        <c:choose>
          <c:when test="${user.blocked}">
            <spring:message code="user.unblock.confirm.title"/>
          </c:when>
          <c:otherwise>
            <spring:message code="user.block.confirm.title"/>
          </c:otherwise>
        </c:choose>
      </h2>
      <button type="button" class="close-modal" aria-label="Close">&times;</button>
    </div>
    <div class="modal-body">
      <p id="blockModalMessage">
        <c:choose>
          <c:when test="${user.blocked}">
            <c:out value="${spring:message(code='user.unblock.confirm.message', arguments='${user.firstname} ${user.lastname}')}" />          </c:when>
          <c:otherwise>
            <c:out value="${spring:message(code='user.block.confirm.message', arguments='${user.firstname} ${user.lastname}')}" />          </c:otherwise>
        </c:choose>
      </p>
      <c:if test="${!user.blocked}">
        <p class="warning-text"><spring:message code="user.block.confirm.warning"/></p>
      </c:if>
    </div>
    <div class="modal-footer">
      <button type="button" class="cta-button secondary" id="cancelBlockBtn">
        <spring:message code="user.block.cancel"/>
      </button>
      <form action="<c:url value='/users/${user.id}/${user.blocked ? "unblock" : "block"}'/>" method="post" id="blockUserForm">
        <button type="submit" class="cta-button ${user.blocked ? 'primary' : 'delete-button'}">
          <c:choose>
            <c:when test="${user.blocked}">
              <spring:message code="user.unblock.confirm"/>
            </c:when>
            <c:otherwise>
              <spring:message code="user.block.confirm"/>
            </c:otherwise>
          </c:choose>
        </button>
      </form>
    </div>
  </div>
</div>

<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Modal functionality
    const modal = document.getElementById('blockModal');
    const blockBtn = document.getElementById('blockUserBtn');
    const unblockBtn = document.getElementById('unblockUserBtn');
    const cancelBtn = document.getElementById('cancelBlockBtn');
    const closeModal = document.querySelector('.close-modal');

    // Show modal when block or unblock button is clicked
    if (blockBtn) {
      blockBtn.addEventListener('click', function() {
        modal.style.display = 'flex';
      });
    }

    if (unblockBtn) {
      unblockBtn.addEventListener('click', function() {
        modal.style.display = 'flex';
      });
    }

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
