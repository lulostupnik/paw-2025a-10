<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<html>
<head>
    <link href="<c:url value='/css/main.css' />" rel="stylesheet"/> <!-- No entiendo porque, pero no me esta tomando el color rojo de main.css -->
</head>
<body>
<h2> <!-- style="color: red;" --> Hello <c:out value="${user.email}" escapeXml="true"/> ! </h2>
</body>
</html>
