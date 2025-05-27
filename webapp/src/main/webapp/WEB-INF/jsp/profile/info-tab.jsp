<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="profile-section active" id="info-section">
  <div class="profile-card">
    <h2 class="section-title"><spring:message code="profile.personal.info"/></h2>

    <div class="info-list">
      <c:if test="${isMine}">
      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.email"/></h3>
        <p class="info-value"><c:out value="${profileUser.email}"/></p>
      </div>
      </c:if>
      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.firstname"/></h3>
        <p class="info-value"><c:out value="${profileUser.firstname}"/></p>
      </div>

      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.lastname"/></h3>
        <p class="info-value"><c:out value="${profileUser.lastname}"/></p>
      </div>

      <c:if test="${not empty profileUser.university}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.home.university"/></h3>
          <p class="info-value"><c:out value="${profileUser.university.name}"/></p>
        </div>
      </c:if>

      <c:if test="${not empty profileUser.career}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.career"/></h3>
          <p class="info-value"><c:out value="${profileUser.career.name}"/></p>
        </div>
      </c:if>

    </div>
  </div>
</div>