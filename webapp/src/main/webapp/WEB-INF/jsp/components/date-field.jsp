<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:url value='/resources/css/date-field.css'/>" />

<%--
Parameters:
- path: Form path for the date input
- label: Label text
--%>

<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    ${param.label}
  </form:label>
  <form:input path="${param.path}" type="date" cssClass="form-input" />
  <form:errors path="${param.path}" cssClass="error-message" />
</div>