
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title>Responder al Viaje</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f8fa;
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 20px;
        }

        .reply-container {
            background-color: white;
            border-radius: 12px;
            box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
            width: 500px;
            padding: 20px;
            border: 1px solid #e1e8ed;
        }

        h2 {
            text-align: center;
        }

        textarea {
            width: 100%;
            height: 100px;
            margin-top: 10px;
            padding: 10px;
            border-radius: 8px;
            border: 1px solid #ccc;
        }

        .submit-button {
            background-color: #1da1f2;
            color: white;
            border: none;
            padding: 10px;
            width: 100%;
            border-radius: 20px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
        }

        .submit-button:hover {
            background-color: #0c85d0;
        }
    </style>
</head>
<body>

<div class="reply-container">
    <h2>Responder al Viaje</h2>
    <c:url var="registerUrl" value="/journey/${journey.id}/reply"/>
    <p>
        <c:out value="${journey.user.email}"/>
    </p>

    <form:form modelAttribute="replyJourneyForm" action="${registerUrl}" method="post">
        <div>
            <form:errors path="email" cssClass="error" element="p"/>
            <label>
                <spring:message code="createJourney.userEmail"/>
                <spring:message code="createJourney.userEmail.hint" var="emailHint"/>
                <form:input path="email" placeholder="${emailHint}"/>
            </label>

        </div>
        <div>
            <form:errors path="firstName" cssClass="error" element="p"/>
            <label>
                <spring:message code="createJourney.firstName"/>
                <spring:message code="createJourney.firstName.hint" var="nameHint"/>
                <form:input path="firstName" placeholder="${nameHint}"/>
            </label>
        </div>
        <div>
            <form:errors path="lastName" cssClass="error" element="p"/>
            <label>
                <spring:message code="createJourney.lastName"/>
                <spring:message code="createJourney.lastName.hint" var="lastNameHint"/>
                <form:input path="lastName" placeholder="${lastNameHint}"/>
            </label>
        </div>
        <div>
            <form:errors path="username" cssClass="error" element="p"/>
            <label>
                <spring:message code="createJourney.username"/>
                <spring:message code="createJourney.username.hint" var="usernameHint"/>
                <form:input path="username" placeholder="${usernameHint}"/>
            </label>
        <div>
            <form:errors path="career" cssClass="error" element="p"/>
            <label>
                <spring:message code="createJourney.career"/>
                <spring:message code="createJourney.career.hint" var="carrerHint"/>
                <form:input path="career" placeholder="${carrerHint}"/>
            </label>
        </div>
        <div>
            <form:errors path="originUniversity" cssClass="error" element="p"/>
            <label>
                <spring:message code="createJourney.originUniversity"/>
                <spring:message code="createJourney.originUniversity.hint" var="originUniversityHint"/>
                <form:input path="originUniversity" placeholder="${originUniversityHint}"/>
            </label>
        </div>
        <div>
            <form:errors path="message" cssClass="error" element="p"/>
            <label>
                <spring:message code="replyJourney.message"/>
                <spring:message code="replyJourney.message.hint" var="messageHint"/>
                <form:input path="message" placeholder="${messageHint}"/>
            </label>
        </div>
        <div>
            <spring:message code="replyJourney.reply" var="submit"/>
            <input type="submit" value="${submit}"/>
        </div>
    </form:form>
</div>

</body>
</html>
