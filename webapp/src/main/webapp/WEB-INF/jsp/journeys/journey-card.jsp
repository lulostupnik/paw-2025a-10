<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sprng" uri="http://java.sun.com/jsp/jstl/fmt" %>
<script src="<c:url value='/resources/js/detect-overflow.js'/>"></script>

<div class="event-card-wrapper">
    <a href="<c:url value="/journeys/${param.journeyId}"/>" class="event-card-link">
        <div class="featured-event-card">
            <!-- Image Container with improved aspect ratio for profile pictures -->
            <div class="event-image-container">
                <c:if test="${not empty param.profilePictureId}">
                    <img src="<c:url value="/images/${param.profilePictureId}"/>"
                         alt="<spring:message code='journey.profile.alt'/>"
                         class="event-image profile-image">
                </c:if>
                <c:if test="${empty param.profilePictureId}">
                    <div class="image-placeholder">
                        <i class="fas fa-user"></i>
                    </div>
                </c:if>
            </div>

            <!-- Journey Info with improved layout -->
            <div class="event-card-content">
                <div class="event-card-header">
                    <div class="event-location">
                        <h3><spring:message code="journey.destinationCityAndCountry" arguments="${param.city},${param.country}"/></h3>
                    </div>
                    <p class="event-card-subtitle mt-2">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                        </svg>
                        <span class="university-name" title="<c:out value="${param.university}"/>"><c:out value="${param.university}"/></span>
                    </p>
                    <div class="event-organizer">
                        <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 016 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                        </svg>
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

                <div class="card-dates">
                    <svg xmlns="http://www.w3.org/2000/svg" class="card-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <span class="card-date-range">
                        <c:out value="${param.startDate}" /> → <c:out value="${param.endDate}" />
                    </span>
                </div>
                <p class="event-card-description"><c:out value="${param.description}"/></p>
            </div>
        </div>
    </a>
</div>
