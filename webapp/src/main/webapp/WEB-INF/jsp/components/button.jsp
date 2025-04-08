<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="<c:url value='/resources/css/button.css'/>" />

<div class="btn-container">
    <button type="${empty param.type ? 'submit' : param.type}"
            class="btn-primary ${param.additionalClasses}">
        ${param.label}
    </button>
</div>