<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
Parameters:
- path: Form path for the date input
- label: Label text
--%>

<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    <c:out value="${param.label}" />
  </form:label>
  <form:input path="${param.path}" type="date" cssClass="form-input" />
  <form:errors path="${param.path}" cssClass="error-message" />
  <c:if test="${param.isStartDate}">
    <form:errors path="" cssClass="error-message"/>
  </c:if>
</div>
