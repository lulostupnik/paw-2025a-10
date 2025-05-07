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
</head>
<c:set var="attendeesPageSize" value="4" scope="request" />
<c:set var="chatPageSize" value="4" scope="request" />
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
                                                <button type="submit" class="btn-primary btn-with-icon">
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

                <!-- Different layouts for event owner vs non-owner -->
                <c:choose>
                    <c:when test="${isEventOwner}">
                        <!-- Tab Navigation for event owners -->
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
                                    <jsp:include page="attendees-section.jsp">
                                        <jsp:param name="showList" value="true" />
                                        <jsp:param name="showToggle" value="false" />
                                    </jsp:include>
                                </div>

                                <!-- Chat Tab Content -->
                                <div id="chat-content" class="tab-content">
                                    <jsp:include page="chat-section.jsp">
                                        <jsp:param name="chatListId" value="chat-list" />
                                        <jsp:param name="showToggle" value="true" />
                                    </jsp:include>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <!-- Simplified layout for non-owners -->
                        <div class="non-owner-content">
                            <!-- Attendees Count Section -->
                            <jsp:include page="attendees-section.jsp">
                                <jsp:param name="showList" value="false" />
                                <jsp:param name="showToggle" value="false" />
                            </jsp:include>

                            <!-- Chat Section -->
                            <jsp:include page="chat-section.jsp">
                                <jsp:param name="chatListId" value="chat-list-non-owner" />
                                <jsp:param name="showToggle" value="true" />
                            </jsp:include>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</div>

<c:set var="warning"><spring:message code="event.deleteWarning"/></c:set>
<jsp:include page="../../components/delete-modal.jsp">
    <jsp:param name="warning" value="${warning}"/>
</jsp:include>

<!-- JavaScript for the page -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Only initialize tabs if they exist (for event owners)
        if (document.querySelector('.tabs-header')) {
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
        }

        // Initialize all toggle sections
        document.querySelectorAll('.toggle-button').forEach(function(button) {
            const sectionId = button.getAttribute('data-toggle');
            const section = document.getElementById(sectionId);
            const expandIcon = button.querySelector('.expand-icon');
            const collapseIcon = button.querySelector('.collapse-icon');

            // Set initial state
            if (section && section.classList.contains('collapsed')) {
                expandIcon.style.display = 'inline-block';
                collapseIcon.style.display = 'none';
            } else if (section) {
                expandIcon.style.display = 'none';
                collapseIcon.style.display = 'inline-block';
            }
        });
    });

    function toggleSection(sectionId) {
        const section = document.getElementById(sectionId);
        if (!section) return;

        section.classList.toggle('collapsed');

        const button = document.querySelector(`[data-toggle="${sectionId}"]`);
        if (!button) return;

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
            const tabElement = document.getElementById(tabId + '-tab');
            if (tabElement) {
                tabElement.click();
            }
        });
    </script>
</c:if>
</body>
</html>