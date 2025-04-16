<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<html>
<head>
    <title><spring:message code="login.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="login-container">
    <div class="login-header">
        <h1 class="login-title"><spring:message code="login.title"/></h1>
    </div>

    <div class="login-card">
        <c:if test="${param.error != null}">
            <div class="error-alert">
                <p><spring:message code="login.error" text="Invalid username or password"/></p>
            </div>
        </c:if>

        <c:url value="/login" var="loginUrl" />
        <form action="${loginUrl}" method="post" enctype="application/x-www-form-urlencoded">
            <!-- Username Field -->
            <div class="form-group-log">
                <label for="j_username" class="form-label-log">
                    <spring:message code="createJourney.userEmail"/>
                </label>
                <input id="j_username"
                       name="j_username"
                       type="text"
                       placeholder="<spring:message code="createJourney.userEmail.hint"/>"
                       class="form-control-log" />
            </div>

            <!-- Password Field -->
            <div class="form-group-log">
                <label for="j_password" class="form-label-log">
                    <spring:message code="createJourney.password"/>
                </label>
                <input id="j_password"
                       name="j_password"
                       type="password"
                       placeholder="<spring:message code="createJourney.password.hint"/>"
                       class="form-control-log" />
            </div>

            <!-- Remember Me Checkbox -->
            <div class="checkbox-container">
                <label class="checkbox-label">
                    <input name="j_rememberme"
                           type="checkbox"
                           class="checkbox-input" />
                    <span><spring:message code="remember_me"/></span>
                </label>
            </div>

            <!-- Submit Button -->
            <div class="form-group-log">
                <button type="submit" class="btn-primary">
                    <spring:message code="login.submit"/>
                </button>
            </div>
        </form>

<%--        <!-- Optional: Add forgot password and sign up links -->--%>
<%--        <div class="login-footer">--%>
<%--            <a href="<c:url value='/forgot-password'/>" class="login-link">--%>
<%--                <spring:message code="login.forgot_password" text="Forgot password?"/>--%>
<%--            </a>--%>
<%--            &nbsp;|&nbsp;--%>
<%--            <a href="<c:url value='/register'/>" class="login-link">--%>
<%--                <spring:message code="login.signup" text="Sign up"/>--%>
<%--            </a>--%>
<%--        </div>--%>
    </div>
</div>
</body>
</html>
