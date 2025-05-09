<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%--
Parameters:
- showList: Whether to show the full attendees list (default: true)
- showToggle: Whether to show the toggle button (default: true)
--%>

<c:if test="${empty param.showList}">
    <c:set var="showList" value="true" />
</c:if>
<c:if test="${not empty param.showList}">
    <c:set var="showList" value="${param.showList}" />
</c:if>

<c:if test="${empty param.showToggle}">
    <c:set var="showToggle" value="true" />
</c:if>
<c:if test="${not empty param.showToggle}">
    <c:set var="showToggle" value="${param.showToggle}" />
</c:if>

<div class="content-section">
    <div class="section-header">
        <div class="attendees-header">
            <h2 class="section-title">
                <!-- Users icon SVG -->
                <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                    <circle cx="9" cy="7" r="4"></circle>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                </svg>
                <spring:message code="event.data" />

            </h2>
            <c:if test="${showToggle eq 'true' and showList eq 'true'}">
                <button class="toggle-button" data-toggle="attendees-list" onclick="toggleSection('attendees-list')">
                    <span class="collapse-icon">
                        <!-- Chevron up icon SVG -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="18 15 12 9 6 15"></polyline>
                        </svg>
                    </span>
                    <span class="expand-icon" style="display: none;">
                        <!-- Chevron down icon SVG -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <polyline points="6 9 12 15 18 9"></polyline>
                        </svg>
                    </span>
                </button>
            </c:if>
        </div>
    </div>

    <div class="data-statistics-container">
        <div class="data-section">
            <div class="statistics-list">
                <div class="stat-item">
                    <span class="stat-label"><spring:message code="event.stats.createdEvents" /></span>
                    <span class="stat-value"><c:out value="${createdEventsCount}" /></span>
                </div>
                <div class="stat-item">
                    <span class="stat-label"><spring:message code="event.stats.attendedEvents" /></span>
                    <span class="stat-value"><c:out value="${attendedEventsCount}" /></span>
                </div>
                <c:if test="${not empty topAttendeeCountry}">
                    <div class="stat-item">
                        <span class="stat-label"><spring:message code="event.stats.topCountry" /></span>
                        <span class="stat-value"><c:out value="${topAttendeeCountry}" /> (<c:out value="${topAttendeeCountryCount}" />)</span>
                    </div>
                </c:if>
                <div class="stat-item">
                    <span class="stat-label"><spring:message code="event.stats.totalParticipants" /></span>
                    <c:if test="${event.attendeesLimit.isPresent()}">
                        <span class="stat-value">(<c:out value="${event.attendeesCount}" /> / <c:out value="${event.attendeesLimit.get()}" />)</span>
                    </c:if>
                    <c:if test="${event.attendeesLimit.isEmpty()}">
                        <span class="stat-value">(<c:out value="${event.attendeesCount}" /> / <spring:message code="event.noAttendeesLimit"/>)</span>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Attendees List - Only shown if showList is true -->
    <c:if test="${showList eq 'true'}">
        <div id="attendees-list" class="attendees-grid">
            <c:if test="${empty attendeesPage.content}">
                <div class="empty-state">
                    <div class="empty-icon">
                        <!-- Users icon SVG -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                            <circle cx="9" cy="7" r="4"></circle>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                        </svg>
                    </div>
                    <p class="empty-message">
                        <spring:message code="event.no.attendees" />
                    </p>
                </div>
            </c:if>

            <c:if test="${not empty attendeesPage.content}">
                <c:forEach var="attendee" items="${attendeesPage.content}">
                    <div class="attendee-card">
                        <div class="attendee-avatar">
                            <c:if test="${not empty attendee.profilePictureId}">
                                <img src="<c:url value='/images/${attendee.profilePictureId}'/>" alt="Profile" class="detail-avatar-img">
                            </c:if>
                            <c:if test="${empty attendee.profilePictureId}">
                                <div class="avatar-placeholder">
                                    <c:out value="${fn:substring(attendee.firstname, 0, 1)}${fn:substring(attendee.lastname, 0, 1)}" />
                                </div>
                            </c:if>
                        </div>
                        <div class="attendee-info">
                            <h3 class="attendee-name">
                                <c:out value="${attendee.firstname} ${attendee.lastname}" />
                            </h3>
                            <p class="attendee-email">
                                <c:out value="${attendee.email}" />
                            </p>
                        </div>
                    </div>
                </c:forEach>
            </c:if>
        </div>

        <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
            <jsp:param name="pageObjectTotalPages" value="${attendeesPage.totalPages}" />
            <jsp:param name="currentPage" value="${attendeesPage.currentPage}" />
            <jsp:param name="pageSize" value="${attendeesPageSize}" />
            <jsp:param name="baseUrl" value="/events/${event.id}?page=${eventResponsesPage.currentPage}&size=${chatPageSize}" />
            <jsp:param name="paramName" value="attendeesPage" />
            <jsp:param name="sizeParamName" value="attendeesSize" />
        </jsp:include>
    </c:if>
</div>
