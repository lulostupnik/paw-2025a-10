<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    <c:out value="${param.label}" />
  </form:label>
  <div class="file-upload-area">
    <svg class="file-upload-icon" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16" width="24" height="24">
      <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
    </svg>
    <p class="file-upload-text">
      <spring:message code="${param.messageCode}"/>
    </p>
    <form:input path="${param.path}" type="file" cssClass="file-input"
                accept="${empty param.accept ? 'image/png, image/jpeg' : param.accept}" />
  </div>
  <form:errors path="${param.path}" cssClass="error-message" />
</div>