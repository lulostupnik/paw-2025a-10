<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="btn-container">
    <button type="${empty param.type ? 'submit' : param.type}"
            class="btn-primary ${param.additionalClasses}">
        <c:out value="${param.label}" />
    </button>
</div>