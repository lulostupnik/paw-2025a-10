<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title>Upload Image</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body>
        <c:url var="uploadUrl" value="/images/upload"/>
        <form:form modelAttribute="uploadImageForm" action="${uploadUrl}" method="post" enctype="multipart/form-data">
            <div>
                <form:input type="file" path="image"/>
            </div>
            <button type="submit">
                    Submit
            </button>
        </form:form>
</body>
</html>