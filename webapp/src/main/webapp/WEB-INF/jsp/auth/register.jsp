<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%--
  Created by IntelliJ IDEA.
  User: nicol
  Date: 4/12/2025
  Time: 5:01 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <title><spring:message code="register.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/base.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-components.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/autocomplete.css'/>" />
</head>
<body>
<div class="container">
    <!-- Back Link -->
    <div class="mb-6">
        <a href="<c:url value="/"/>" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="register.back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <div class="text-center">
            <h2 class="header">
                <spring:message code="register.title"/>
            </h2>
        </div>

        <!-- Card -->
        <div class="card">
            <c:url var="registerUrl" value="/register"/>
            <form:form modelAttribute="createUserForm" action="${registerUrl}" method="post" enctype="multipart/form-data">

                <!-- Email Field -->
                <c:set var="emailLabel"><spring:message code="createJourney.userEmail"/></c:set>
                <c:set var="emailHint"><spring:message code="createJourney.userEmail.hint"/></c:set>
                <jsp:include page="../components/text-field.jsp">
                    <jsp:param name="path" value="email" />
                    <jsp:param name="label" value="${emailLabel}" />
                    <jsp:param name="placeholder" value="${emailHint}" />
                    <jsp:param name="type" value="email" />
                </jsp:include>

                <!-- Password Field -->
                <c:set var="passwordLabel"><spring:message code="createJourney.password"/></c:set>
                <c:set var="passwordHint"><spring:message code="createJourney.password.hint"/></c:set>
                <jsp:include page="../components/text-field.jsp">
                    <jsp:param name="path" value="password" />
                    <jsp:param name="label" value="${passwordLabel}" />
                    <jsp:param name="placeholder" value="${passwordHint}" />
                    <jsp:param name="type" value="password" />
                </jsp:include>

                <!-- First Name Field -->
                <c:set var="firstNameLabel"><spring:message code="createJourney.firstName"/></c:set>
                <c:set var="firstNameHint"><spring:message code="createJourney.firstName.hint"/></c:set>
                <jsp:include page="../components/text-field.jsp">
                    <jsp:param name="path" value="firstName" />
                    <jsp:param name="label" value="${firstNameLabel}" />
                    <jsp:param name="placeholder" value="${firstNameHint}" />
                </jsp:include>

                <!-- Last Name Field -->
                <c:set var="lastNameLabel"><spring:message code="createJourney.lastName"/></c:set>
                <c:set var="lastNameHint"><spring:message code="createJourney.lastName.hint"/></c:set>
                <jsp:include page="../components/text-field.jsp">
                    <jsp:param name="path" value="lastName" />
                    <jsp:param name="label" value="${lastNameLabel}" />
                    <jsp:param name="placeholder" value="${lastNameHint}" />
                </jsp:include>

                <!-- Career Field -->
                <c:set var="careerLabel"><spring:message code="event.career"/></c:set>
                <c:set target="${requestScope}" property="careerItems" value="${careers}" />
                <jsp:include page="../components/dropdown.jsp">
                    <jsp:param name="path" value="career" />
                    <jsp:param name="label" value="${careerLabel}" />
                    <jsp:param name="items" value="careerItems" />
                    <jsp:param name="defaultMessageCode" value="event.career.select" />
                </jsp:include>

                <!-- Username Field -->
                <c:set var="usernameLabel"><spring:message code="createJourney.username"/></c:set>
                <c:set var="usernameHint"><spring:message code="createJourney.username.hint"/></c:set>
                <jsp:include page="../components/text-field.jsp">
                    <jsp:param name="path" value="username" />
                    <jsp:param name="label" value="${usernameLabel}" />
                    <jsp:param name="placeholder" value="${usernameHint}" />
                </jsp:include>

                <!--Origin University Field -->
                <c:set var="universityLabel"><spring:message code="createJourney.originUniversity"/></c:set>
                <c:set target="${requestScope}" property="universityItems" value="${universities}" />
                <jsp:include page="../components/dropdown.jsp">
                    <jsp:param name="path" value="originUniversity" />
                    <jsp:param name="label" value="${universityLabel}" />
                    <jsp:param name="items" value="universityItems" />
                    <jsp:param name="defaultMessageCode" value="createJourney.originUniversity.select" />
                </jsp:include>

                <!-- Interests Field -->
                <c:set var="interestsLabel"><spring:message code="event.interest"/></c:set>
                <c:set var="interestsSearchPlaceholder"><spring:message code="event.interest.search"/></c:set>
                <c:set var="interestsHelpText"><spring:message code="event.interest.select"/></c:set>
                <c:set target="${requestScope}" property="interestItems" value="${interests}" />
                <jsp:include page="../components/autocomplete.jsp">
                    <jsp:param name="path" value="interests" />
                    <jsp:param name="label" value="${interestsLabel}" />
                    <jsp:param name="items" value="interestItems" />
                    <jsp:param name="searchPlaceholder" value="${interestsSearchPlaceholder}" />
                    <jsp:param name="helpText" value="${interestsHelpText}" />
                </jsp:include>

                <!-- Profile picture Upload Field -->
                <c:set var="profilePicLabel"><spring:message code="createJourney.profile_picture"/></c:set>
                <jsp:include page="../components/image-upload.jsp">
                    <jsp:param name="path" value="profilePicture" />
                    <jsp:param name="label" value="${profilePicLabel}" />
                    <jsp:param name="messageCode" value="upload_picture.profile" />
                </jsp:include>

                <!-- Submit Button -->
                <c:set var="submitButtonLabel"><spring:message code="register.submit"/></c:set>
                <jsp:include page="../components/button.jsp">
                    <jsp:param name="label" value="${submitButtonLabel}" />
                    <jsp:param name="type" value="submit" />
                </jsp:include>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>
