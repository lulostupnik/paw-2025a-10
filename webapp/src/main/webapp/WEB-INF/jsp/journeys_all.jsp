<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <title><spring:message code="journey.page.title"/></title>
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

<div class="flex h-full min-h-screen">
    <!-- Include the sidebar component -->
    <jsp:include page="components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="ml-64 flex-1">
        <div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
            <div class="mx-auto">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                        <spring:message code="journey.list.title"/>
                    </h2>
                    <a href="${pageContext.request.contextPath}/journey" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
                        <spring:message code="journey.create.button"/>
                    </a>
                </div>

                <!-- Journeys List -->
                <div class="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    <c:forEach var="journey" items="${journeys}">
                        <!-- Make the entire journey card clickable -->
                        <a href="${pageContext.request.contextPath}/journey/${journey.id}" class="block group">
                            <div class="rounded-xl p-4 sm:p-6 bg-white border border-gray-200 hover:border-blue-600 transition-all duration-200 h-full">
                                <div class="flex items-center gap-x-4">
                                    <div>
                                        <h3 class="text-lg font-semibold text-gray-800">
                                            <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                        </h3>
                                        <p class="text-sm text-gray-500">
                                            <c:out value="${journey.destinationCity}" /> -
                                            <c:out value="${journey.destinationUniversity}" />
                                        </p>
                                        <p class="text-sm text-gray-500 mt-1">
                                            <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                        </p>
                                    </div>
                                </div>
                                <p class="mt-3 text-gray-600 line-clamp-3">
                                    <c:out value="${journey.description}" />
                                </p>
                                <div class="mt-4">
                                    <!-- This button is now separate from the card click -->
                                    <span onclick="event.stopPropagation(); window.location.href='${pageContext.request.contextPath}/journey/${journey.id}/reply';"
                                          class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-600 focus:ring-offset-2">
                                        <spring:message code="journey.reply.button"/>
                                    </span>
                                </div>
                            </div>
                        </a>
                    </c:forEach>

                    <c:if test="${empty journeys}">
                        <div class="col-span-full text-center py-10">
                            <p class="text-gray-500"><spring:message code="journey.no.journeys"/></p>
                        </div>
                    </c:if>
                </div>
                <!-- End Journeys List -->
            </div>
        </div>
    </div>
</div>

</body>
</html>