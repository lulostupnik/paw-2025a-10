
<html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Journeys</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h2>Journeys</h2>
<table>
    <tr>
        <th>ID</th>
        <th>Usuario</th>
        <th>Ciudad Destino</th>
        <th>Fecha Inicio</th>
        <th>Fecha Fin</th>
        <th>Universidad Destino</th>
        <th>Descripción</th>
    </tr>
    <c:forEach var="journey" items="${journeys}">
        <tr>
            <td><c:out value="${journey.id}" /></td>
            <td><c:out value="${journey.user}" /></td>
            <td><c:out value="${journey.destinationCity}" /></td>
            <td><c:out value="${journey.startDate}" /></td>
            <td><c:out value="${journey.endDate}" /></td>
            <td><c:out value="${journey.destinationUniversity}" /></td>
            <td><c:out value="${journey.description}" /></td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
