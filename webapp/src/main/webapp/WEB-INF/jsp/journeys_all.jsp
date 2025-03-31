
<html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title>Journeys</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f8fa;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        .journey-card {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
            width: 500px;
            padding: 15px;
            margin-bottom: 15px;
            border: 1px solid #e1e8ed;
        }

        .journey-header {
            font-weight: bold;
            color: #14171a;
        }

        .journey-meta {
            color: #657786;
            font-size: 14px;
        }

        .journey-description {
            margin-top: 10px;
            font-size: 15px;
        }

        .reply-button {
            background-color: #1da1f2;
            color: white;
            border: none;
            padding: 8px 12px;
            border-radius: 20px;
            font-size: 14px;
            cursor: pointer;
            margin-top: 10px;
        }

        .reply-button:hover {
            background-color: #0c85d0;
        }
    </style>
</head>
<body>

<h2>Journeys</h2>

<c:forEach var="journey" items="${journeys}">
    <div class="journey-card">
        <div class="journey-header">
            <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
        </div>
        <div class="journey-meta">
            <c:out value="${journey.destinationCity}" /> -
            <c:out value="${journey.destinationUniversity}" />
        </div>
        <div class="journey-meta">
            <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
        </div>
        <div class="journey-description">
            <c:out value="${journey.description}" />
        </div>
        <button class="reply-button">Responder</button>
    </div>
</c:forEach>

</body>
</html>
