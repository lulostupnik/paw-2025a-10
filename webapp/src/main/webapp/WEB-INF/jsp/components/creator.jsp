<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%-- Expected params: user, title (optional), showName (optional) --%>

<c:set var="user" value="${creatorUser}" />
<c:set var="showName" value="${creatorShowName}" />


<a href="<c:url value='/profile/${user.id}/info' />" class="profile-card-link">
    <div class="event-creator ">
        <h3 class="creator-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <c:choose>
                <c:when test="${isJourneyCreator}">
                    <spring:message code="journey.host" text="Journey Creator" />

                </c:when>
                <c:otherwise>
                    <spring:message code="event.creator" text="Event Creator" />
                </c:otherwise>
            </c:choose>
        </h3>

        <div class="creator-profile">
            <div class="creator-avatar-container">
                <c:choose>
                    <c:when test="${user.profilePictureId > 0}">
                        <div class="creator-image-wrapper">
                            <img src="<c:url value='/images/${user.profilePictureId}'/>" alt="<spring:message code='event.creator.profile.image' text='Creator Profile'/>" class="creator-profile-img">
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="creator-avatar-placeholder">
                            <span><c:out value="${fn:substring(user.firstname, 0, 1)}${fn:substring(user.lastname, 0, 1)}" /></span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="creator-info">
                <c:if test="${empty showName or showName ne 'false'}">
                    <h4 class="creator-name"><c:out value="${user.firstname} ${user.lastname}" /></h4>
                </c:if>
                <p class="creator-username">@<c:out value="${user.username}" /></p>
                <div class="creator-details">
                    <c:if test="${not empty user.university}">
                        <div class="creator-detail">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                                <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                            </svg>
                            <span><c:out value="${user.university.name}" /></span>
                        </div>
                    </c:if>
                    <c:if test="${not empty user.career}">
                        <div class="creator-detail">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="2" y="7" width="20" height="14" rx="2" ry="2"></rect>
                                <path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"></path>
                            </svg>
                            <span><c:out value="${user.career.name}" /></span>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</a>
