<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="replyJourney.title"/></title>
    <!-- Include Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {}
            }
        };
    </script>
    <!-- Include Preline UI Kit CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body class="bg-gray-100">

<div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
    <!-- Back Link -->
    <div class="mb-6">
        <a href="<c:url value="/journeys"/>" class="inline-flex items-center gap-x-1.5 text-sm text-blue-600 decoration-2 hover:underline">
            <svg class="w-3 h-3" xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                <path fill-rule="evenodd" d="M11.354 1.646a.5.5 0 0 1 0 .708L5.707 8l5.647 5.646a.5.5 0 0 1-.708.708l-6-6a.5.5 0 0 1 0-.708l6-6a.5.5 0 0 1 .708 0z"/>
            </svg>
            <spring:message code="journey.back"/>
        </a>
    </div>

    <div class="mx-auto max-w-2xl">
        <div class="text-center mb-6">
            <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                <spring:message code="replyJourney.title"/>
            </h2>
            <p class="mt-2 text-gray-600">
                <spring:message code="replyJourney.subtitle" arguments="${journey.user.email}"/>
            </p>
        </div>

        <!-- Reply Form Card -->
        <div class="p-4 sm:p-6 lg:p-8 bg-white border border-gray-200 rounded-xl">
            <c:url var="registerUrl" value="/journeys/${journey.id}/reply"/>
            <form:form modelAttribute="replyJourneyForm" action="${registerUrl}" method="post" enctype="multipart/form-data">

                <!-- Email Field -->
                <div class="mb-4 sm:mb-6">
                    <form:label path="email" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.userEmail"/>
                    </form:label>
                    <spring:message code="createJourney.userEmail.hint" var="emailHint"/>
                    <form:input path="email" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${emailHint}"/>
                    <form:errors path="email" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <div class="mb-4 sm:mb-8">
                    <form:label path="profilePicture" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.profile_picture" />
                    </form:label>
                    <div class="flex flex-col items-center p-5 border-2 border-gray-300 border-dashed rounded-lg bg-gray-50">
                        <svg class="w-8 h-8 mb-4 text-gray-500" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 20 16">
                            <path stroke="currentColor" stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 13h3a3 3 0 0 0 0-6h-.025A5.56 5.56 0 0 0 16 6.5 5.5 5.5 0 0 0 5.207 5.021C5.137 5.017 5.071 5 5 5a4 4 0 0 0 0 8h2.167M10 15V6m0 0L8 8m2-2 2 2"/>
                        </svg>
                        <p class="mb-2 text-sm text-gray-500 text-center"><spring:message code ="upload_picture.profile"/></p>
                        <form:input path="profilePicture" type="file" class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:font-medium file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100" accept="image/png, image/jpeg, application/pdf" />
                    </div>
                    <form:errors path="profilePicture" class="text-red-500 text-sm mt-1" />
                </div>

                <!-- First Name Field -->
                <div class="mb-4 sm:mb-6">
                    <form:label path="firstName" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.firstName"/>
                    </form:label>
                    <spring:message code="createJourney.firstName.hint" var="nameHint"/>
                    <form:input path="firstName" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${nameHint}"/>
                    <form:errors path="firstName" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Last Name Field -->
                <div class="mb-4 sm:mb-6">
                    <form:label path="lastName" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.lastName"/>
                    </form:label>
                    <spring:message code="createJourney.lastName.hint" var="lastNameHint"/>
                    <form:input path="lastName" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${lastNameHint}"/>
                    <form:errors path="lastName" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Username Field -->
                <div class="mb-4 sm:mb-6">
                    <form:label path="username" class="block mb-2 text-sm font-medium">
                        <spring:message code="createJourney.username"/>
                    </form:label>
                    <spring:message code="createJourney.username.hint" var="usernameHint"/>
                    <form:input path="username" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                placeholder="${usernameHint}"/>
                    <form:errors path="username" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Career Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="career" class="block mb-2 text-sm font-medium">
                        <spring:message code="event.career" />
                    </form:label>
                    <form:select path="career" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500">
                        <option value=""><spring:message code="event.career.select"/></option>
                        <c:forEach var="career" items="${careers}">
                            <option value="${career.name}"><c:out value=" ${career.name}"/></option>
                        </c:forEach>
                    </form:select>
                    <form:errors path="career" class="text-red-500 text-sm mt-1" />
                </div>

                <!-- Origin University Field -->
                <div class="mb-4 sm:mb-8">
                    <form:label path="originUniversity" class="block mb-2 text-sm font-medium">
                        <spring:message code="replyJourney.originUniversity"/>
                    </form:label>
                    <form:select path="originUniversity" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500">
                        <option value=""><spring:message code="createJourney.destinationUniversity.select"/></option>
                        <c:forEach var="university" items="${universities}">
                            <option value="${university.name}">$<c:out value="${university.name}"/></option>
                        </c:forEach>
                    </form:select>
                    <form:errors path="originUniversity" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>

                <!-- Message Field -->
                <div class="mb-4 sm:mb-6">
                    <form:label path="message" class="block mb-2 text-sm font-medium">
                        <spring:message code="replyJourney.message"/>
                    </form:label>
                    <spring:message code="replyJourney.message.hint" var="messageHint"/>
                    <form:textarea path="message" rows="4" class="py-2.5 sm:py-3 px-4 block w-full border border-gray-200 rounded-lg sm:text-sm focus:border-blue-500 focus:ring-blue-500"
                                   placeholder="${messageHint}"/>
                    <form:errors path="message" cssClass="text-red-500 text-sm mt-1" element="p"/>
                </div>



                <!-- Submit Button -->
                <div class="mt-6">
                    <spring:message code="replyJourney.reply" var="submit"/>
                    <button type="submit" class="w-full py-3 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2">
                            ${submit}
                    </button>
                </div>
            </form:form>
        </div>
    </div>
</div>

</body>
</html>