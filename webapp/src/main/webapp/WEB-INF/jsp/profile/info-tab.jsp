<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="profile-section active" id="info-section">
  <div class="profile-card">
    <h2 class="section-title"><spring:message code="profile.personal.info"/></h2>

    <div class="info-list">
      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.email"/></h3>
        <p class="info-value"><c:out value="${user.email}"/></p>
      </div>

      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.firstname"/></h3>
        <p class="info-value"><c:out value="${user.firstname}"/></p>
      </div>

      <div class="info-item">
        <h3 class="info-label"><spring:message code="profile.lastname"/></h3>
        <p class="info-value"><c:out value="${user.lastname}"/></p>
      </div>

      <c:if test="${not empty user.university}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.home.university"/></h3>
          <p class="info-value"><c:out value="${user.university.name}"/></p>
        </div>
      </c:if>

      <c:if test="${not empty user.career}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.career"/></h3>
          <p class="info-value"><c:out value="${user.career.name}"/></p>
        </div>
      </c:if>

      <c:if test="${not empty user.locale}">
        <div class="info-item">
          <h3 class="info-label"><spring:message code="profile.language"/></h3>
          <p class="info-value"><c:out value="${user.locale.displayLanguage}"/></p>
        </div>
      </c:if>
    </div>
  </div>
</div>