<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="event-card-wrapper">
    <a href="<c:url value="/journeys/${param.journeyId}"/>" class="event-card-link">
        <div class="event-card">
            <!-- Image Container with improved aspect ratio for profile pictures -->
            <div class="event-image-container">
                <c:if test="${not empty param.profilePictureId}">
                    <img src="<c:url value="/images/${param.profilePictureId}"/>"
                         alt="<spring:message code='journey.profile.alt'/>"
                         class="event-image profile-image">
                </c:if>
                <c:if test="${empty param.profilePictureId}">
                    <div class="event-image-placeholder">
                        <i class="fas fa-user"></i>
                    </div>
                </c:if>
            </div>

            <!-- Journey Info with improved layout -->
            <div class="event-info-container">
                <div class="event-header">
                    <div class="event-location">
                        <h3><c:out value="${param.city}"/></h3>
                    </div>
                    <div class="event-rating">
                        <span class="user-name"><c:out value="${param.userName}"/></span>
                    </div>
                </div>

                <!-- Format dates -->
                <fmt:parseDate value="${param.startDate}" pattern="yyyy-MM-dd" var="parsedStartDate" />
                <fmt:parseDate value="${param.endDate}" pattern="yyyy-MM-dd" var="parsedEndDate" />
                <fmt:formatDate value="${parsedStartDate}" pattern="d" var="startDay" />
                <fmt:formatDate value="${parsedStartDate}" pattern="MM" var="startMonth" />
                <fmt:formatDate value="${parsedStartDate}" pattern="yyyy" var="startYear" />
                <fmt:formatDate value="${parsedEndDate}" pattern="d" var="endDay" />
                <fmt:formatDate value="${parsedEndDate}" pattern="MM" var="endMonth" />
                <fmt:formatDate value="${parsedEndDate}" pattern="yyyy" var="endYear" />

                <div class="event-name">
                    <p class="date-range">
                        <spring:message code="journey.dates"/>
                        <c:out value="${startDay}"/> <spring:message code="month.${startMonth}"/>
                        <spring:message code="journey.to"/>
                        <c:out value="${endDay}"/> <spring:message code="month.${endMonth}"/>
                        <c:out value="${endYear}"/>
                    </p>
                </div>

                <div class="event-description-container">
                    <p class="event-description"><c:out value="${param.description}"/></p>
                </div>
            </div>
        </div>
    </a>
</div>
