<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title><spring:message code="createJourney.title"/></title>
    <!-- Include custom CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/base.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-components.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/autocomplete.css'/>" />

</head>
<body>

<div class="container">
    <!-- Back Link -->
    <div class="mb-6">
        <a href="<c:url value="/journeys"/>" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <div class="text-center">
            <h2 class="header">
                <spring:message code="createJourney.title"/>
            </h2>
        </div>

        <!-- Card -->
        <div class="card">
            <c:url var="registerUrl" value="/journeys/create"/>
            <form:form modelAttribute="createJourneyForm" action="${registerUrl}" method="post" enctype="multipart/form-data">

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

                <!-- Email Field -->
                <c:set var="emailLabel"><spring:message code="createJourney.userEmail"/></c:set>
                <c:set var="emailHint"><spring:message code="createJourney.userEmail.hint"/></c:set>
                <jsp:include page="../components/text-field.jsp">
                    <jsp:param name="path" value="email" />
                    <jsp:param name="label" value="${emailLabel}" />
                    <jsp:param name="placeholder" value="${emailHint}" />
                    <jsp:param name="type" value="email" />
                </jsp:include>

                <!-- Start Date Field -->
                <c:set var="startDateLabel"><spring:message code="createJourney.startDate"/></c:set>
                <jsp:include page="../components/date-field.jsp">
                    <jsp:param name="path" value="startDate" />
                    <jsp:param name="label" value="${startDateLabel}" />
                    <jsp:param name="isStartDate" value="true" />
                </jsp:include>

                <!-- End Date Field -->
                <c:set var="endDateLabel"><spring:message code="createJourney.endDate"/></c:set>
                <jsp:include page="../components/date-field.jsp">
                    <jsp:param name="path" value="endDate" />
                    <jsp:param name="label" value="${endDateLabel}" />
                </jsp:include>

<%--                <!-- Destination City Field -->--%>
<%--                <c:set var="cityLabel"><spring:message code="createJourney.destinationCity"/></c:set>--%>
<%--                <c:set target="${requestScope}" property="cityItems" value="${cities}" />--%>
<%--                <jsp:include page="../components/dropdown.jsp">--%>
<%--                    <jsp:param name="path" value="destinationCity" />--%>
<%--                    <jsp:param name="label" value="${cityLabel}" />--%>
<%--                    <jsp:param name="items" value="cityItems" />--%>
<%--                    <jsp:param name="defaultMessageCode" value="createJourney.destinationCity.select" />--%>
<%--                </jsp:include>--%>

                <!-- Destination University Field -->
                <c:set var="universityLabel"><spring:message code="createJourney.destinationUniversity"/></c:set>
                <c:set target="${requestScope}" property="universityItems" value="${universities}" />
                <jsp:include page="../components/dropdown.jsp">
                    <jsp:param name="path" value="destinationUniversity" />
                    <jsp:param name="label" value="${universityLabel}" />
                    <jsp:param name="items" value="universityItems" />
                    <jsp:param name="defaultMessageCode" value="createJourney.destinationUniversity.select" />
                </jsp:include>

                <c:set var="universityLabel"><spring:message code="createJourney.originUniversity"/></c:set>
                <c:set target="${requestScope}" property="universityItems" value="${universities}" />
                <jsp:include page="../components/dropdown.jsp">
                    <jsp:param name="path" value="originUniversity" />
                    <jsp:param name="label" value="${universityLabel}" />
                    <jsp:param name="items" value="universityItems" />
                    <jsp:param name="defaultMessageCode" value="createJourney.originUniversity.select" />
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

                <!-- Description Field -->
                <c:set var="descriptionLabel"><spring:message code="createJourney.description"/></c:set>
                <c:set var="descriptionHint"><spring:message code="createJourney.description.hint"/></c:set>
                <jsp:include page="../components/text-area.jsp">
                    <jsp:param name="path" value="description" />
                    <jsp:param name="label" value="${descriptionLabel}" />
                    <jsp:param name="placeholder" value="${descriptionHint}" />
                </jsp:include>

                <!-- Profile picture Upload Field -->
                <c:set var="profilePicLabel"><spring:message code="createJourney.profile_picture"/></c:set>
                <jsp:include page="../components/image-upload.jsp">
                    <jsp:param name="path" value="profilePicture" />
                    <jsp:param name="label" value="${profilePicLabel}" />
                    <jsp:param name="messageCode" value="upload_picture.profile" />
                </jsp:include>

                <!-- Submit Button -->
                <c:set var="submitButtonLabel"><spring:message code="createJourney.submit"/></c:set>
                <jsp:include page="../components/button.jsp">
                    <jsp:param name="label" value="${submitButtonLabel}" />
                    <jsp:param name="type" value="submit" />
                </jsp:include>
            </form:form>
        </div>
        <!-- End Card -->
    </div>
</div>

</body>
</html>