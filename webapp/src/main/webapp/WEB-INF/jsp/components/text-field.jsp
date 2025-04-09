<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
Parameters:
- path: Form path for the input
- label: Label text
- placeholder: Placeholder text (optional)
- type: Input type (default: "text")
--%>

<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    ${param.label}
  </form:label>
  <form:input path="${param.path}" type="${empty param.type ? 'text' : param.type}"
              cssClass="form-input" placeholder="${param.placeholder}" />
  <form:errors path="${param.path}" cssClass="error-message" element="p" />
</div>