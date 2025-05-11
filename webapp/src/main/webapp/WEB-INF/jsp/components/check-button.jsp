<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>



<div class="checkbox-container">
  <label class="checkbox-label">
    <form:checkbox path="${param.path}" cssClass="checkbox-input" />
    <span class="checkbox-custom"></span>
    <span class="checkbox-text"><c:out value="${param.label}" /></span>
  </label>
  <form:errors path="${param.path}" cssClass="error-message" element="p" />
</div>
