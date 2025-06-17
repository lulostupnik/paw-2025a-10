<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
    <title><spring:message code="report.detail.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/detail.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="container">
    <div class="detail-container">
        <div class="featured-journey-card">
            <div class="journey-card-content">
                <div class="journey-card-header">
                    <h1 class="journey-card-title">
                        <spring:message code="report.detail.title.for"/> <c:out value="${report.reportedUser.username}"/>
                    </h1>
                    <p class="journey-card-subtitle">
                        <!-- Show content type badge -->
                        <c:choose>
                            <c:when test="${report.journey != null}">
                                <span class="content-type-badge journey-badge">
                                    <spring:message code="report.type.journey" text="Journey Report"/>
                                </span>
                            </c:when>
                            <c:when test="${report.event != null}">
                                <span class="content-type-badge event-badge">
                                    <spring:message code="report.type.event" text="Event Report"/>
                                </span>
                            </c:when>
                            <c:when test="${report.journeyResponse != null}">
                                <span class="content-type-badge comment-badge">
                                    <spring:message code="report.type.journey.comment" text="Journey Comment Report"/>
                                </span>
                            </c:when>
                            <c:when test="${report.eventResponse != null}">
                                <span class="content-type-badge comment-badge">
                                    <spring:message code="report.type.event.comment" text="Event Comment Report"/>
                                </span>
                            </c:when>
                        </c:choose>
                        - <c:out value="${report.reason}"/>
                    </p>
                </div>

                <div class="detail-content">
                    <h2 class="section-title-landing"><spring:message code="report.detail.information"/></h2>

                    <div class="features-grid">
                        <!-- Basic Report Information -->
                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.reportedUser"/></h3>
                            <p class="feature-description">
                                <strong><c:out value="${report.reportedUser.firstname}"/></strong>
                                (@<c:out value="${report.reportedUser.username}"/>)
                                <c:if test="${report.reportedUser.blocked}">
                                    <span class="user-status-badge blocked">
                                        <spring:message code="user.status.blocked" text="BLOCKED"/>
                                    </span>
                                </c:if>
                            </p>
                        </div>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.reportingUser"/></h3>
                            <p class="feature-description">
                                <strong><c:out value="${report.reportingUser.firstname}"/></strong>
                                (@<c:out value="${report.reportingUser.username}"/>)
                            </p>
                        </div>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.reason"/></h3>
                            <p class="feature-description">
                                <c:choose>
                                    <c:when test="${report.reason.name() == 'SPAM'}">
                                        <spring:message code="report.reason.spam" text="Spam or unwanted content"/>
                                    </c:when>
                                    <c:when test="${report.reason.name() == 'HARRASMENT'}">
                                        <spring:message code="report.reason.harassment" text="Harassment or bullying"/>
                                    </c:when>
                                    <c:when test="${report.reason.name() == 'INAPPROPRIATE_CONTENT'}">
                                        <spring:message code="report.reason.inappropriate" text="Inappropriate content"/>
                                    </c:when>
                                    <c:when test="${report.reason.name() == 'MISINFORMATION'}">
                                        <spring:message code="report.reason.misinformation" text="False or misleading information"/>
                                    </c:when>
                                    <c:when test="${report.reason.name() == 'HATE_SPEECH'}">
                                        <spring:message code="report.reason.hate_speech" text="Hate speech or discrimination"/>
                                    </c:when>
                                    <c:when test="${report.reason.name() == 'VIOLENCE'}">
                                        <spring:message code="report.reason.violence" text="Violence or threats"/>
                                    </c:when>
                                    <c:when test="${report.reason.name() == 'OTHER'}">
                                        <spring:message code="report.reason.other" text="Other"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:out value="${report.reason}"/>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <!-- Reported Content Details -->
                        <c:if test="${report.journey != null}">
                            <div class="feature-card reported-content-card">
                                <h3 class="feature-title">
                                    <spring:message code="report.detail.reported.journey" text="Reported Journey"/>
                                </h3>
                                <div class="reported-content">
                                    <p class="content-preview">
                                        <c:out value="${report.journey.description}" escapeXml="true"/>
                                    </p>
                                    <div class="content-meta">
                                        <c:if test="${report.journey.startDate != null}">
                                            <span>
                                                <spring:message code="journey.detail.startDate"/>:
                                                <c:out value="${report.journey.startDate}"/>
                                            </span>
                                        </c:if>
                                        <c:if test="${report.journey.endDate != null}">
                                            <span>
                                                <spring:message code="journey.detail.endDate"/>:
                                                <c:out value="${report.journey.endDate}"/>
                                            </span>
                                        </c:if>
                                    </div>
                                    <c:url var="journeyUrl" value='/journeys/${report.journey.id}'/>
                                    <c:choose>
                                        <c:when test="${report.journey.deleted}">
                                            <span class="deleted-badge">
                                                <spring:message code="report.content.deleted" text="Content Deleted"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${journeyUrl}" class="view-content-link" onclick="saveLink()" >
                                                <spring:message code="report.view.original.content" text="View Original Content"/> ↗
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${report.event != null}">
                            <div class="feature-card reported-content-card">
                                <h3 class="feature-title">
                                    <spring:message code="report.detail.reported.event" text="Reported Event"/>
                                </h3>
                                <div class="reported-content">
                                    <h4><c:out value="${report.event.title}"/></h4>
                                    <p class="content-preview">
                                        <c:out value="${report.event.description}" escapeXml="true"/>
                                    </p>
                                    <div class="content-meta">
                                        <c:if test="${report.event.date != null}">
                                            <span>
                                                <spring:message code="event.detail.date"/>:
                                                <c:out value="${report.event.date}"/>
                                            </span>
                                        </c:if>
                                        <span><spring:message code="event.detail.location"/>:
                                            <c:out value="${report.event.address}"/>
                                        </span>
                                    </div>
                                    <c:url var="eventUrl" value='/events/${report.event.id}'/>
                                    <c:choose>
                                        <c:when test="${report.event.deleted}">
                                            <span class="deleted-badge">
                                                <spring:message code="report.content.deleted" text="Content Deleted"/>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="<c:out value='${eventUrl}'/>" onclick="saveLink()" class="view-content-link">
                                                <spring:message code="report.view.original.content" text="View Original Content"/> ↗
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${report.journeyResponse != null}">
                            <div class="feature-card reported-content-card">
                                <h3 class="feature-title">
                                    <spring:message code="report.detail.reported.journey.comment" text="Reported Journey Comment"/>
                                </h3>
                                <div class="reported-content">
                                    <div class="comment-content">
                                        <p class="content-preview">
                                            "<c:out value="${report.journeyResponse.message}" escapeXml="true"/>"
                                        </p>
                                        <div class="content-meta">
                                            <c:if test="${report.journeyResponse.dateTime != null}">
                                                <fmt:parseDate value="${report.journeyResponse.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                                <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy HH:mm" var="formattedDate" />
                                                <span>
                                                    <spring:message code="comment.posted.on"/>:
                                                    <c:out value="${formattedDate}"/>
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="parent-content">
                                        <h5><spring:message code="report.detail.parent.journey" text="On Journey"/>:</h5>
                                        <p><c:out value="${report.journeyResponse.journey.user.username}"/></p>
                                        <c:url var="journeyUrl" value='/journeys/${report.journeyResponse.journey.id}'/>
                                        <c:choose>
                                            <c:when test="${report.journeyResponse.deleted}">
                                            <span class="deleted-badge">
                                                <spring:message code="report.content.deleted" text="Content Deleted"/>
                                            </span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="<c:out value='${journeyUrl}'/>" onclick="saveLink()" class="view-content-link" >
                                                    <spring:message code="report.view.original.content" text="View Original Content"/> ↗
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <c:if test="${report.eventResponse != null}">
                            <div class="feature-card reported-content-card">
                                <h3 class="feature-title">
                                    <spring:message code="report.detail.reported.event.comment" text="Reported Event Comment"/>
                                </h3>
                                <div class="reported-content">
                                    <div class="comment-content">
                                        <p class="content-preview">
                                            "<c:out value="${report.eventResponse.message}" escapeXml="true"/>"
                                        </p>
                                        <div class="content-meta">
                                            <c:if test="${report.eventResponse.dateTime != null}">
                                                <fmt:parseDate value="${report.eventResponse.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                                <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy HH:mm" var="formattedDate" />
                                                <span>
                                                    <spring:message code="comment.posted.on"/>:
                                                    <c:out value="${formattedDate}"/>
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="parent-content">
                                        <h5><spring:message code="report.detail.parent.event" text="On Event"/>:</h5>
                                        <p><c:out value="${report.eventResponse.event.title}"/></p>
                                        <c:url var="eventUrl" value='/events/${report.eventResponse.event.id}'/>
                                        <c:choose>
                                            <c:when test="${report.eventResponse.deleted}">
                                            <span class="deleted-badge">
                                                <spring:message code="report.content.deleted" text="Content Deleted"/>
                                            </span>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="<c:out value='${eventUrl}'/>" onclick="saveLink()" class="view-content-link">
                                                    <spring:message code="report.view.original.content" text="View Original Content"/> ↗
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.description"/></h3>
                            <p class="feature-description">
                                <c:choose>
                                    <c:when test="${not empty report.description}">
                                        <c:out value="${report.description}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <em><spring:message code="report.no.additional.details" text="No additional details provided"/></em>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>

                        <div class="feature-card">
                            <h3 class="feature-title"><spring:message code="report.detail.status"/></h3>
                            <div class="feature-description">
                                <c:choose>
                                    <c:when test="${report.status == 'PENDING'}">
                                        <span class="status-badge status-pending">
                                            <spring:message code="report.status.pending" text="Pending"/>
                                        </span>
                                    </c:when>
                                    <c:when test="${report.status == 'UNDER_REVIEW'}">
                                        <span class="status-badge status-under-review">
                                            <spring:message code="report.status.under_review" text="Under Review"/>
                                        </span>
                                    </c:when>
                                    <c:when test="${report.status == 'RESOLVED'}">
                                        <span class="status-badge status-resolved">
                                            <spring:message code="report.status.resolved" text="Resolved"/>
                                        </span>
                                    </c:when>
                                    <c:when test="${report.status == 'DISMISSED'}">
                                        <span class="status-badge status-dismissed">
                                            <spring:message code="report.status.dismissed" text="Dismissed"/>
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge">
                                            <c:out value="${report.status}"/>
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                    </div>
                </div>

                <!-- Admin Actions -->
                <div class="detail-actions">
                    <a href="<c:url value='/dashboard/reports'/>" class="btn-text">
                        <spring:message code="report.back" text="Back to Reports"/>
                    </a>

                    <div class="admin-actions">
                        <!-- Status Update Actions -->
                        <c:if test="${report.status == 'PENDING'}">
                            <form action="<c:url value='/reports/${report.id}/status'/>" method="post" style="display: inline;">
                                <input type="hidden" name="status" value="UNDER_REVIEW"/>
                                <button type="submit" class="cta-button btn-dele">
                                    <spring:message code="report.action.review" text="Start Review"/>
                                </button>
                            </form>
                        </c:if>

                        <c:if test="${report.status == 'UNDER_REVIEW'}">
                            <!-- Delete Content Button - dynamically sets the correct path based on content type -->
                            <c:choose>
                                <c:when test="${report.journey != null and report.journey.deleted == false}">
                                    <form action="<c:url value='/journeys/${report.journey.id}/delete'/>" method="get" style="display: inline;">
                                        <button type="submit" class="cta-button btn-dele" onclick="saveLink()">
                                            <spring:message code="report.action.delete_journey" text="Delete Journey"/>
                                        </button>
                                    </form>
                                </c:when>
                                <c:when test="${report.event != null and report.event.deleted == false}">
                                    <form action="<c:url value='/events/${report.event.id}/delete'/>" method="get" style="display: inline;">
                                        <button type="submit" class="cta-button btn-dele" onclick="saveLink()">
                                            <spring:message code="report.action.delete_event" text="Delete Event"/>
                                        </button>
                                    </form>
                                </c:when>
                                <c:when test="${report.journeyResponse != null and report.journeyResponse.deleted == false}">
                                    <form action="<c:url value='/journeys/reply/${report.journeyResponse.id}/delete'/>" method="get" style="display: inline;">
                                        <button type="submit" class="cta-button btn-dele" onclick="saveLink()">
                                            <spring:message code="report.action.delete_journey_comment" text="Delete Journey Comment"/>
                                        </button>
                                    </form>
                                </c:when>
                                <c:when test="${report.eventResponse != null and report.eventResponse.deleted == false }">
                                    <form action="<c:url value='/events/reply/${report.eventResponse.id}/delete'/>" method="get" style="display: inline;">
                                        <button type="submit" class="cta-button btn-dele" onclick="saveLink()">
                                            <spring:message code="report.action.delete_event_comment" text="Delete Event Comment"/>
                                        </button>
                                    </form>
                                </c:when>
                            </c:choose>
                        </c:if>

                        <!-- Block/Unblock User Action -->
                        <c:if test="${report.status == 'UNDER_REVIEW' || report.status == 'RESOLVED'}">
                            <button type="button" class="cta-button ${report.reportedUser.blocked ? 'btn-primary' : 'btn-danger'}" id="blockUserBtn">
                                <c:choose>
                                    <c:when test="${report.reportedUser.blocked}">
                                        <spring:message code="user.unblock" text="Unblock User"/>
                                    </c:when>
                                    <c:otherwise>
                                        <spring:message code="user.block" text="Block User"/>
                                    </c:otherwise>
                                </c:choose>
                            </button>
                        </c:if>
                        <c:if test="${report.status == 'UNDER_REVIEW'}">
                            <!-- Resolve with option to block user -->
                            <form action="<c:url value='/reports/${report.id}/status'/>" method="post" style="display: inline;">
                                <input type="hidden" name="status" value="RESOLVED"/>
                                <button type="submit" class="cta-button btn-tertiary" >
                                    <spring:message code="report.action.resolve" text="Mark as Resolved"/>
                                </button>
                            </form>
                            <c:if test="${!report.reportedUser.blocked
                            or (report.journey != null and !report.journey.deleted)
                            or (report.event != null and !report.event.deleted)
                            or (report.journeyResponse != null and !report.journeyResponse.deleted)
                            or (report.eventResponse != null and !report.eventResponse.deleted)}">
                                <form action="<c:url value='/reports/${report.id}/status'/>" method="post" style="display: inline;">
                                    <input type="hidden" name="status" value="DISMISSED"/>
                                    <button type="submit" class="cta-button btn-primary">
                                        <spring:message code="report.action.dismiss" text="Dismiss Report"/>
                                    </button>
                                </form>
                            </c:if>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<!-- Block User Modal -->
<div id="blockModal" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h2 id="blockModalTitle">
                <c:choose>
                    <c:when test="${report.reportedUser.blocked}">
                        <spring:message code="user.unblock.confirm.title" text="Unblock User"/>
                    </c:when>
                    <c:otherwise>
                        <spring:message code="user.block.confirm.title" text="Block User"/>
                    </c:otherwise>
                </c:choose>
            </h2>
            <button type="button" class="close-modal" aria-label="Close">&times;</button>
        </div>
        <div class="modal-body">
            <p id="blockModalMessage">
                <c:choose>
                    <c:when test="${report.reportedUser.blocked}">
                        <spring:message code="user.unblock.confirm.message" text="Are you sure you want to unblock {0} {1}?">
                            <spring:argument><c:out value="${report.reportedUser.firstname}"/></spring:argument>
                            <spring:argument><c:out value="${report.reportedUser.lastname}"/></spring:argument>
                        </spring:message>
                    </c:when>
                    <c:otherwise>
                        <spring:message code="user.block.confirm.message" text="Are you sure you want to block {0} {1}?">
                            <spring:argument><c:out value="${report.reportedUser.firstname}"/></spring:argument>
                            <spring:argument><c:out value="${report.reportedUser.lastname}"/></spring:argument>
                        </spring:message>
                    </c:otherwise>
                </c:choose>
            </p>
            <c:if test="${!report.reportedUser.blocked}">
                <p class="warning-text"><spring:message code="user.block.confirm.warning" text="This will prevent the user from accessing the platform."/></p>
            </c:if>
        </div>
        <div class="modal-footer">
            <button type="button" class="cta-button secondary" id="cancelBlockBtn">
                <spring:message code="user.block.cancel" text="Cancel"/>
            </button>
            <form action="<c:url value='/users/${report.reportedUser.id}/${report.reportedUser.blocked ? "unblock" : "block"}'/>" method="post" id="blockUserForm">
                <input type="hidden" name="userId" value="<c:out value="${report.reportedUser.id}"/>">
                <input type="hidden" name="returnUrl" value="<c:url value='/reports/${report.id}'/>">
                <button type="submit" class="cta-button ${report.reportedUser.blocked ? 'btn-primary' : 'btn-danger'}">
                    <c:choose>
                        <c:when test="${report.reportedUser.blocked}">
                            <spring:message code="user.unblock.confirm" text="Unblock User"/>
                        </c:when>
                        <c:otherwise>
                            <spring:message code="user.block.confirm" text="Block User"/>
                        </c:otherwise>
                    </c:choose>
                </button>
            </form>
        </div>
    </div>
</div>
<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>
<script>
    function saveLink() {
        pushToNavigationStack(window.location.href);
    }
</script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Resolve Modal functionality
        const resolveModal = document.getElementById('resolveModal');
        const resolveBtn = document.getElementById('resolveReportBtn');
        const cancelResolveBtn = document.getElementById('cancelResolveBtn');
        const confirmResolveBtn = document.getElementById('confirmResolveBtn');

        // Block Modal functionality
        const blockModal = document.getElementById('blockModal');
        const blockBtn = document.getElementById('blockUserBtn');
        const cancelBlockBtn = document.getElementById('cancelBlockBtn');

        // Close modal buttons
        const closeModals = document.querySelectorAll('.close-modal');

        // Open resolve modal
        if (resolveBtn) {
            resolveBtn.addEventListener('click', function() {
                resolveModal.style.display = 'flex';
            });
        }

        // Open block modal
        if (blockBtn) {
            blockBtn.addEventListener('click', function() {
                blockModal.style.display = 'flex';
            });
        }

        // Confirm resolve action
        if (confirmResolveBtn) {
            confirmResolveBtn.addEventListener('click', function() {
                const selectedAction = document.querySelector('input[name="resolveAction"]:checked').value;

                if (selectedAction === 'resolve_and_block') {
                    // First block the user, then resolve the report
                    const blockForm = document.createElement('form');
                    blockForm.method = 'post';
                    blockForm.action = '<c:url value="/users/${report.reportedUser.id}/block"/>';

                    const userIdInput = document.createElement('input');
                    userIdInput.type = 'hidden';
                    userIdInput.name = 'userId';
                    userIdInput.value = '${report.reportedUser.id}';

                    const returnUrlInput = document.createElement('input');
                    returnUrlInput.type = 'hidden';
                    returnUrlInput.name = 'returnUrl';
                    returnUrlInput.value = '<c:url value="/reports/${report.id}"/>';

                    const resolveInput = document.createElement('input');
                    resolveInput.type = 'hidden';
                    resolveInput.name = 'resolveReport';
                    resolveInput.value = '${report.id}';

                    blockForm.appendChild(userIdInput);
                    blockForm.appendChild(returnUrlInput);
                    blockForm.appendChild(resolveInput);

                    document.body.appendChild(blockForm);
                    blockForm.submit();
                } else {
                    // Just resolve the report
                    const resolveForm = document.createElement('form');
                    resolveForm.method = 'post';
                    resolveForm.action = '<c:url value="/reports/${report.id}/status"/>';

                    const statusInput = document.createElement('input');
                    statusInput.type = 'hidden';
                    statusInput.name = 'status';
                    statusInput.value = 'RESOLVED';

                    resolveForm.appendChild(statusInput);
                    document.body.appendChild(resolveForm);
                    resolveForm.submit();
                }
            });
        }

        // Close modal functions
        function hideModal(modal) {
            modal.style.display = 'none';
        }

        // Cancel buttons
        if (cancelResolveBtn) {
            cancelResolveBtn.addEventListener('click', () => hideModal(resolveModal));
        }
        if (cancelBlockBtn) {
            cancelBlockBtn.addEventListener('click', () => hideModal(blockModal));
        }

        // Close modal X buttons
        closeModals.forEach(closeBtn => {
            closeBtn.addEventListener('click', function() {
                const modal = this.closest('.modal');
                hideModal(modal);
            });
        });

        // Click outside to close
        window.addEventListener('click', function(event) {
            if (event.target.classList.contains('modal')) {
                hideModal(event.target);
            }
        });
    });
</script>

<style>
    .content-type-badge {
        display: inline-block;
        padding: 0.25rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.75rem;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.025em;
    }

    .journey-badge {
        background-color: #e0f2fe;
        color: #0369a1;
    }

    .event-badge {
        background-color: #f3e8ff;
        color: #7c3aed;
    }

    .comment-badge {
        background-color: #fef3c7;
        color: #92400e;
    }

    .user-status-badge {
        display: inline-block;
        padding: 0.125rem 0.5rem;
        border-radius: 9999px;
        font-size: 0.625rem;
        font-weight: 700;
        text-transform: uppercase;
        margin-left: 0.5rem;
    }

    .user-status-badge.blocked {
        background-color: #fecaca;
        color: #991b1b;
    }

    .reported-content-card {
        border-left: 4px solid #dc2626;
        background-color: #fef2f2;
    }

    .reported-content {
        margin-top: 0.5rem;
    }

    .content-preview {
        background-color: white;
        padding: 1rem;
        border-radius: 0.5rem;
        border: 1px solid #e5e7eb;
        font-style: italic;
        margin: 0.5rem 0;
    }

    .content-meta {
        display: flex;
        flex-direction: column;
        gap: 0.25rem;
        font-size: 0.875rem;
        color: #6b7280;
        margin: 0.5rem 0;
    }

    .parent-content {
        margin-top: 1rem;
        padding-top: 1rem;
        border-top: 1px solid #e5e7eb;
    }

    .parent-content h5 {
        margin: 0 0 0.5rem 0;
        font-size: 0.875rem;
        color: #374151;
    }

    .view-content-link {
        display: inline-block;
        margin-top: 0.5rem;
        color: #2563eb;
        text-decoration: none;
        font-size: 0.875rem;
        font-weight: 500;
    }

    .view-content-link:hover {
        text-decoration: underline;
    }

    .admin-actions {
        display: flex;
        gap: 0.75rem;
        align-items: center;
    }

    .admin-actions form {
        margin: 0;
    }

    .modal {
        display: none;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 1000;
        justify-content: center;
        align-items: center;
    }

    .modal-content {
        background: white;
        border-radius: 12px;
        padding: 24px;
        width: 90%;
        max-width: 500px;
        max-height: 90vh;
        overflow-y: auto;
        box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    }

    .modal-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
    }

    .modal-header h2 {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: #111;
    }
    .btn-tertiary:hover {
        background-color: #e3e4e6;
        color: #111;
    }

    .close-modal {
        background: none;
        border: none;
        font-size: 24px;
        cursor: pointer;
        color: #666;
        padding: 0;
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 6px;
    }

    .close-modal:hover {
        background-color: #f5f5f5;
    }

    .modal-body {
        margin-bottom: 24px;
    }

    .modal-footer {
        display: flex;
        gap: 12px;
        justify-content: flex-end;
    }

    .resolve-options {
        margin-top: 16px;
    }

    .resolve-option {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;
        cursor: pointer;
    }

    .resolve-option input[type="radio"] {
        margin: 0;
    }

    .warning-text {
        color: #dc2626;
        font-weight: 500;
        margin-top: 12px;
    }

    .status-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.375rem;
        padding: 0.375rem 0.75rem;
        border-radius: 9999px;
        font-size: 0.75rem;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.025em;
    }

    .status-pending {
        background-color: #fef3c7;
        color: #92400e;
    }

    .status-under-review {
        background-color: #dbeafe;
        color: #1e40af;
    }
    btn-del{
        background-color: #fde68a;
        color: #92400e;
    }

    .status-resolved {
        background-color: #d1fae5;
        color: #065f46;
    }

    .status-dismissed {
        background-color: #f3f4f6;
        color: #374151;
    }
</style>

</body>
</html>
