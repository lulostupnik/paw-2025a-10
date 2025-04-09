<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
Parameters:
- path: Form path for the select
- label: Label text
- items: Collection of items to display (passed as request attribute)
- itemValue: Property to use for option value (default: "name")
- itemLabel: Property to use for option label (default: "name")
- defaultMessageCode: Message code for default option
--%>
<div class="form-group">
  <form:label path="${param.path}" cssClass="form-label">
    <c:out value="${param.label}" />
  </form:label>
  <form:select path="${param.path}" cssClass="form-select">
    <form:option value=""><spring:message code="${param.defaultMessageCode}"/></form:option>
    <c:forEach var="item" items="${requestScope[param.items]}">
        <c:set var="optionValue"><c:out value = "${item[empty param.itemValue ? 'name' : param.itemValue]}"/></c:set>
        <form:option value="optionValue"><c:out value="${item[empty param.itemLabel ? 'name' : param.itemLabel]}"/></form:option>
    </c:forEach>
  </form:select>
  <form:errors path="${param.path}" cssClass="error-message" />
</div>