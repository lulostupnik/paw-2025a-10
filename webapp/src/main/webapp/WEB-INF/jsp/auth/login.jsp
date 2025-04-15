<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
    <title><spring:message code="login.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/base.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-components.css'/>" />
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 400px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        .header {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 20px;
            text-align: center;
        }
        .form-group {
            margin-bottom: 20px;
        }
        .form-label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-control {
            width: 100%;
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .checkbox-container {
            margin-bottom: 20px;
        }
        .btn {
            background-color: #0066cc;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            width: 100%;
        }
        .btn:hover {
            background-color: #0052a3;
        }
        .card {
            padding: 20px;
            border: 1px solid #eee;
            border-radius: 5px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <spring:message code="login.title"/>
    </div>

    <div class="card">
        <c:url value="/login" var="loginUrl" />
        <form action="${loginUrl}" method="post" enctype="application/x-www-form-urlencoded">
            <!-- Username/Email Field -->
            <div class="form-group">
                <label for="j_username" class="form-label">
                    <spring:message code="createJourney.userEmail"/>
                </label>
                <input id="j_username" name="j_username" type="text"
                       placeholder="<spring:message code="createJourney.userEmail.hint"/>"
                       class="form-control" />
            </div>

            <!-- Password Field -->
            <div class="form-group">
                <label for="j_password" class="form-label">
                    <spring:message code="createJourney.password"/>
                </label>
                <input id="j_password" name="j_password" type="password"
                       placeholder="<spring:message code="createJourney.password.hint"/>"
                       class="form-control" />
            </div>

            <!-- Remember Me Checkbox -->
            <div class="checkbox-container">
                <label>
                    <input name="j_rememberme" type="checkbox"/>
                    <spring:message code="remember_me"/>
                </label>
            </div>

            <!-- Submit Button -->
            <div class="form-group">
                <input type="submit" value="<spring:message code="login.submit"/>" class="btn"/>
            </div>
        </form>
    </div>
</div>
</body>
</html>