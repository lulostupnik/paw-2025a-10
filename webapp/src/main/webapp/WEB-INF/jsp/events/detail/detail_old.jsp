<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="event.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
    <link rel="stylesheet" href="<c:url value="/resources/css/event-detail.css"/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <script src="<c:url value='/resources/js/confirm-delete.js'/>"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Check if there's an active tab stored in session storage
            const activeTab = sessionStorage.getItem('activeTab') || 'details';

            // Set the active tab
            document.getElementById(activeTab + '-tab').classList.add('active');
            document.getElementById(activeTab + '-content').style.display = 'block';

            // Add event listeners to tab buttons
            document.querySelectorAll('.tab-btn').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    // Get the tab id
                    const tabId = this.getAttribute('data-tab');

                    // Store the active tab in session storage
                    sessionStorage.setItem('activeTab', tabId);

                    // Remove active class from all tabs
                    document.querySelectorAll('.tab-btn').forEach(function(tab) {
                        tab.classList.remove('active');
                    });

                    // Hide all tab contents
                    document.querySelectorAll('.tab-content').forEach(function(content) {
                        content.style.display = 'none';
                    });

                    // Add active class to clicked tab
                    this.classList.add('active');

                    // Show corresponding tab content
                    document.getElementById(tabId + '-content').style.display = 'block';

                    // Update the hidden field in the reply form
                    const activeTabInput = document.getElementById('active-tab-input');
                    if (activeTabInput) {
                        activeTabInput.value = tabId;
                    }
                });
            });

            // Set the active tab in the hidden field on page load
            const activeTabInput = document.getElementById('active-tab-input');
            if (activeTabInput) {
                activeTabInput.value = activeTab;
            }
        });

        function toggleSection(sectionId) {
            const section = document.getElementById(sectionId);
            section.classList.toggle('collapsed');

            const button = document.querySelector(`[data-toggle="${sectionId}"]`);
            const expandIcon = button.querySelector('.expand-icon');
            const collapseIcon = button.querySelector('.collapse-icon');

            if (section.classList.contains('collapsed')) {
                expandIcon.style.display = 'inline-block';
                collapseIcon.style.display = 'none';
            } else {
                expandIcon.style.display = 'none';
                collapseIcon.style.display = 'inline-block';
            }
        }
    </script>
</head>

<body>
<div style="display: none;">
    <!-- Event deletion messages -->
    <span id="i18n-event.confirmDelete" data-message="<spring:message code='event.confirmDelete' />"></span>
    <span id="i18n-event.deleteWarning" data-message="<spring:message code='event.deleteWarning' />"></span>

    <!-- Event response deletion messages -->
    <span id="i18n-eventResponse.confirmDelete" data-message="<spring:message code='eventResponse.confirmDelete' />"></span>
    <span id="i18n-eventResponse.deleteWarning" data-message="<spring:message code='eventResponse.deleteWarning' />"></span>
</div>

<div class="layout-container">
    <!-- Include the sidebar component -->
    <jsp:include page="../../components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="content-container">
            <!-- Back Button -->
            <div class="back-button-container">
                <c:if test="${not isEventOwner}">
                    <a href="<c:url value='/events' />" class="back-button">
                        <!-- Back arrow SVG -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M19 12H5"></path>
                            <path d="M12 19l-7-7 7-7"></path>
                        </svg>
                        <span><spring:message code="event.detail.back.to.list" /></span>
                    </a>
                </c:if>
                <c:if test="${isEventOwner}">
                    <a href="<c:url value='/profile' />" class="back-button">
                        <!-- Back arrow SVG -->
                        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M19 12H5"></path>
                            <path d="M12 19l-7-7 7-7"></path>
                        </svg>
                        <span><spring:message code="event.detail.back.to.profile" /></span>
                    </a>
                </c:if>
            </div>

            <!-- Event Detail Container -->
            <div class="event-detail-container">
                <!-- Top Section -->
                <div class="event-top-section">
                    <!-- Event Header -->
                    <div class="event-header">
                        <h1 class="event-title"><c:out value="${event.title}" /></h1>

                        <!-- Action Controls - FIXED POSITIONING -->
                        <div class="action-controls">
                            <!-- Attendance Control for non-event owners -->
                            <c:if test="${not isEventOwner and not empty user}">
                                <c:choose>
                                    <c:when test="${attend}">
                                        <div class="attendance-status">
                                            <div class="attending-detail-badge">
                                                <!-- Check icon SVG -->
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                    <polyline points="20 6 9 17 4 12"></polyline>
                                                </svg>
                                                <span><spring:message code="event.attending" text="Attending" /></span>
                                            </div>
                                            <form action="<c:url value='/events/${event.id}/dont-attend'/>" method="post" class="cancel-attendance">
                                                <button type="submit" class="cancel-button" aria-label="<spring:message code='event.cancel.attendance' text='Cancel Attendance'/>">
                                                    <!-- X icon SVG -->
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                        <line x1="18" y1="6" x2="6" y2="18"></line>
                                                        <line x1="6" y1="6" x2="18" y2="18"></line>
                                                    </svg>
                                                </button>
                                            </form>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${not isFull}">
                                            <form action="<c:url value='/events/${event.id}/attend'/>" method="post">
                                                <button type="submit" class="attend-detail-button">
                                                    <!-- Calendar plus icon SVG -->
                                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                        <path d="M21 14V6a2 2 0 00-2-2H5a2 2 0 00-2 2v14a2 2 0 002 2h8"></path>
                                                        <line x1="16" y1="2" x2="16" y2="6"></line>
                                                        <line x1="8" y1="2" x2="8" y2="6"></line>
                                                        <line x1="3" y1="10" x2="21" y2="10"></line>
                                                        <line x1="19" y1="15" x2="19" y2="21"></line>
                                                        <line x1="16" y1="18" x2="22" y2="18"></line>
                                                    </svg>
                                                    <span><spring:message code="event.attend" text="Attend" /></span>
                                                </button>
                                            </form>
                                        </c:if>
                                        <c:if test="${isFull}">
                                            <div class="event-full">
                                                <!-- Alert icon SVG -->
                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                    <circle cx="12" cy="12" r="10"></circle>
                                                    <line x1="12" y1="8" x2="12" y2="12"></line>
                                                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                                                </svg>
                                                <span><spring:message code="event.full" text="Event full" /></span>
                                            </div>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>

                            <sec:authorize access="hasRole('ADMIN')">
                                <c:url var="deleteUrl" value='/events/${event.id}/delete'/>
                                <form:form modelAttribute="deleteForm" id="delete-event-form" action="${deleteUrl}" method="post" style="display: none;">
                                    <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                                    <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                                    <jsp:include page="../../components/text-area.jsp">
                                        <jsp:param name="path" value="message" />
                                        <jsp:param name="label" value="${messageLabel}" />
                                        <jsp:param name="placeholder" value="${messagePlaceholder}" />
                                    </jsp:include>
                                </form:form>

                                <button type="button" class="delete-button" onclick="openDeleteModal('delete-event-form', 'event')">
                                    <!-- Delete icon SVG -->
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M3 6h18"></path>
                                        <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                        <line x1="10" y1="11" x2="10" y2="17"></line>
                                        <line x1="14" y1="11" x2="14" y2="17"></line>
                                    </svg>
                                    <span><spring:message code="event.delete" text="Delete" /></span>
                                </button>
                            </sec:authorize>
                        </div>
                    </div>

                    <!-- Event Meta Info -->
                    <div class="event-meta">
                        <div class="meta-item">
                            <!-- Location icon SVG -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                                <circle cx="12" cy="10" r="3"></circle>
                            </svg>
                            <span><c:out value="${event.eventCity.name}" /></span>
                        </div>
                        <div class="meta-item">
                            <!-- Calendar icon SVG -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                                <line x1="16" y1="2" x2="16" y2="6"></line>
                                <line x1="8" y1="2" x2="8" y2="6"></line>
                                <line x1="3" y1="10" x2="21" y2="10"></line>
                            </svg>
                            <span><c:out value="${event.date}" /></span>
                        </div>
                        <div class="meta-item">
                            <!-- Clock icon SVG -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"></circle>
                                <polyline points="12 6 12 12 16 14"></polyline>
                            </svg>
                            <span>
                                    <c:if test="${event.time.isPresent()}">
                                        <c:out value="${event.time.get()}" />
                                    </c:if>
                                    <c:if test="${event.time.isEmpty()}">
                                        <spring:message code="event.allDayEvent"/>
                                    </c:if>
                                </span>
                        </div>
                        <c:if test="${not empty event.address}">
                            <div class="meta-item">
                                <!-- Map icon SVG -->
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <polygon points="1 6 1 22 8 18 16 22 23 18 23 2 16 6 8 2 1 6"></polygon>
                                    <line x1="8" y1="2" x2="8" y2="18"></line>
                                    <line x1="16" y1="6" x2="16" y2="22"></line>
                                </svg>
                                <span><c:out value="${event.address}" /></span>
                            </div>
                        </c:if>
                    </div>

                    <!-- Event Flyer -->
                    <div class="event-flyer">
                        <c:if test="${not empty event.flyerImageId}">
                            <img src="<c:url value='/images/${event.flyerImageId}'/>" alt="Event Flyer" class="flyer-image">
                        </c:if>
                        <c:if test="${empty event.flyerImageId}">
                            <div class="flyer-placeholder">
                                <!-- Image icon SVG -->
                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                                    <circle cx="8.5" cy="8.5" r="1.5"></circle>
                                    <polyline points="21 15 16 10 5 21"></polyline>
                                </svg>
                                <p><spring:message code="event.no.flyer" /></p>
                            </div>
                        </c:if>
                    </div>

                    <!-- Event Description -->
                    <div class="event-description">
                        <h3 class="description-title">
                            <!-- Info icon SVG -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="16" x2="12" y2="12"></line>
                                <line x1="12" y1="8" x2="12.01" y2="8"></line>
                            </svg>
                            <spring:message code="event.description" />
                        </h3>
                        <div class="description-content">
                            <p><c:out value="${event.description}" /></p>
                        </div>
                    </div>
                </div>

                <!-- Tab Navigation -->
                <div class="tabs-container">
                    <div class="tabs-header">
                        <button id="details-tab" class="tab-btn" data-tab="details">
                            <!-- Users icon SVG -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                <circle cx="9" cy="7" r="4"></circle>
                                <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                            </svg>
                            <span><spring:message code="event.details" text="Details" /></span>
                        </button>
                        <button id="chat-tab" class="tab-btn" data-tab="chat">
                            <!-- Message icon SVG -->
                            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                            </svg>
                            <span><spring:message code="event.chat" text="Chat" /></span>
                        </button>
                    </div>

                    <!-- Tab Contents -->
                    <div class="tabs-content">
                        <!-- Details Tab Content -->
                        <div id="details-content" class="tab-content">
                            <!-- Attendees Section -->
                            <div class="content-section">
                                <div class="section-header">
                                    <h2 class="section-title">
                                        <!-- Users icon SVG -->
                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                                            <circle cx="9" cy="7" r="4"></circle>
                                            <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                                            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                                        </svg>
                                        <spring:message code="event.attendees" />
                                        <c:if test="${event.attendeesLimit.isPresent()}">
                                            <span class="count">(<c:out value="${event.attendeesCount}" /> / <c:out value="${event.attendeesLimit.get()}" />)</span>
                                        </c:if>
                                        <c:if test="${event.attendeesLimit.isEmpty()}">
                                            <span class="count">(<c:out value="${event.attendeesCount}" /> / <spring:message code="event.noAttendeesLimit"/>)</span>
                                        </c:if>
                                    </h2>
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
                                </div>

                                <!-- Attendees List  -->
                                <c:if test="${isEventOwner}">
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
                                                        <img src="<c:url value='/images/${attendee.profilePictureId}'/>" alt="Profile" class="avatar-img">
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
                                        <jsp:param name="pageSize" value="4" />
                                        <jsp:param name="baseUrl" value="/events/${event.id}" />
                                        <jsp:param name="paramName" value="attendeesPage" />
                                        <jsp:param name="sizeParamName" value="attendeesSize" />
                                    </jsp:include>
                                </c:if>
                            </div>
                        </div>

                        <!-- Chat Tab Content -->
                        <div id="chat-content" class="tab-content">
                            <div class="content-section">
                                <div class="section-header">
                                    <h2 class="section-title">
                                        <!-- Message icon SVG -->
                                        <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                        </svg>
                                        <spring:message code="event.responses" />
                                        <span class="count">(<c:out value="${commentsCount}" />)</span>
                                    </h2>
                                    <button class="toggle-button" data-toggle="chat-list" onclick="toggleSection('chat-list')">
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
                                </div>

                                <!-- Chat Messages -->
                                <div id="chat-list" class="chat-list">
                                    <c:if test="${empty eventResponsesPage.content}">
                                        <div class="empty-state">
                                            <div class="empty-icon">
                                                <!-- Message icon SVG -->
                                                <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                                                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                                                </svg>
                                            </div>
                                            <p class="empty-message">
                                                <spring:message code="event.no.responses" />
                                            </p>
                                        </div>
                                    </c:if>

                                    <c:if test="${not empty eventResponsesPage.content}">
                                        <c:forEach var="response" items="${eventResponsesPage.content}">
                                            <div class="chat-message">
                                                <div class="message-header">
                                                    <div class="message-user">
                                                        <div class="message-avatar">
                                                            <div class="avatar-placeholder">
                                                                <c:out value="${fn:substring(response.username, 0, 1)}" />
                                                            </div>
                                                        </div>
                                                        <div class="message-user-info">
                                                            <h3 class="message-username">
                                                                <c:out value="${response.username}" />
                                                            </h3>
                                                            <p class="message-date">
                                                                <c:out value="${response.formattedDate}" />
                                                            </p>
                                                        </div>
                                                    </div>

                                                    <sec:authorize access="hasRole('ADMIN')">
                                                        <div class="message-actions">
                                                            <c:url var="deleteReplyUrl" value='/event-replies/${response.id}/delete'/>
                                                            <form:form modelAttribute="deleteReplyForm" id="delete-event-response-form-${response.id}" action="${deleteReplyUrl}" method="post" style="display: none;">
                                                                <c:set var="messageLabel"><spring:message code="delete.reason.label"/></c:set>
                                                                <c:set var="messagePlaceholder"><spring:message code="delete.reason.placeholder"/></c:set>
                                                                <jsp:include page="../../components/text-area.jsp">
                                                                    <jsp:param name="path" value="message" />
                                                                    <jsp:param name="label" value="${messageLabel}" />
                                                                    <jsp:param name="placeholder" value="${messagePlaceholder}" />
                                                                </jsp:include>
                                                            </form:form>

                                                            <button type="button" class="delete-message-button" onclick="openDeleteModal('delete-event-response-form-${response.id}', 'eventResponse')">
                                                                <!-- Trash icon SVG -->
                                                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                                                    <polyline points="3 6 5 6 21 6"></polyline>
                                                                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                                                </svg>
                                                            </button>
                                                        </div>
                                                    </sec:authorize>
                                                </div>
                                                <div class="message-content">
                                                    <p class="message-text">
                                                        <c:out value="${response.message}" />
                                                    </p>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:if>
                                    <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                                        <jsp:param name="pageObjectTotalPages" value="${eventResponsesPage.totalPages}" />
                                        <jsp:param name="currentPage" value="${eventResponsesPage.currentPage}" />
                                        <jsp:param name="pageSize" value="4" />
                                        <jsp:param name="baseUrl" value="/events/${id}" />
                                    </jsp:include>


                                </div>


                                <!-- Reply Form -->
                                <div class="reply-container">
                                    <h3 class="reply-title">
                                        <!-- Edit icon SVG -->
                                        <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                        </svg>
                                        <spring:message code="reply.message" text="Leave a comment" />
                                    </h3>
                                    <c:url var="replyUrl" value="/events/${event.id}/reply"/>
                                    <form:form modelAttribute="replyEventForm" action="${replyUrl}" method="post" enctype="multipart/form-data" cssClass="reply-form">
                                        <!-- Hidden field to track active tab -->
                                        <input type="hidden" name="activeTab" id="active-tab-input" value="chat" />

                                        <!-- Message Field -->
                                        <c:set var="messageLabel"><spring:message code="reply.message"/></c:set>
                                        <c:set var="messageHint"><spring:message code="reply.message.hint"/></c:set>
                                        <jsp:include page="../../components/text-area.jsp">
                                            <jsp:param name="path" value="message" />
                                            <jsp:param name="label" value="${messageLabel}" />
                                            <jsp:param name="placeholder" value="${messageHint}" />
                                        </jsp:include>

                                        <!-- Submit Button -->
                                        <div class="form-actions">
                                            <c:set var="submitButtonLabel"><spring:message code="reply.submit"/></c:set>
                                            <jsp:include page="../../components/button.jsp">
                                                <jsp:param name="label" value="${submitButtonLabel}" />
                                                <jsp:param name="type" value="submit" />
                                            </jsp:include>
                                        </div>
                                    </form:form>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<c:set var="warning"><spring:message code="event.deleteWarning"/></c:set>
<jsp:include page="../../components/delete-modal.jsp">
    <jsp:param name="warning" value="${warning}"/>
</jsp:include>

<!-- Add this before the closing body tag -->
<c:if test="${deleteFormHasErrors}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Open the modal with the form that has errors
            openDeleteModal('${deleteFormId}', '${deleteFormType}');
        });
    </script>
</c:if>

<!-- Handle active tab after form submission -->
<c:if test="${not empty param.activeTab}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Set the active tab based on the parameter
            const tabId = "${param.activeTab}";
            sessionStorage.setItem('activeTab', tabId);

            // Trigger a click on the tab
            document.getElementById(tabId + '-tab').click();
        });
    </script>
</c:if>
</body>
</html>