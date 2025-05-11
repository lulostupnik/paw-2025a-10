<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<c:set var="escapedPlaceholder">
  <c:out value="${param.placeholder}" />
</c:set>
<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    <c:out value="${param.label}" />
  </form:label>
  <form:textarea path="${param.path}" rows="${empty param.rows ? '4' : param.rows}"
                 cssClass="form-input" placeholder="${escapedPlaceholder}" style="resize: vertical;" />
  <form:errors path="${param.path}" cssClass="error-message" />
</div>
