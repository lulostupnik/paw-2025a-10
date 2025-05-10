<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%--
Parameters:
- chatListId: ID for the chat list container (required)
- showToggle: Whether to show the toggle button (default: true)
--%>

<c:if test="${empty param.showToggle}">
    <c:set var="showToggle" value="true" />
</c:if>
<c:if test="${not empty param.showToggle}">
    <c:set var="showToggle" value="${param.showToggle}" />
</c:if>

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
        <c:if test="${showToggle eq 'true'}">
            <button class="toggle-button" data-toggle="${param.chatListId}" onclick="toggleSection('${param.chatListId}')">
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

    <!-- Chat Messages -->
    <div id="${param.chatListId}" class="chat-list">
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
                                <c:url var="deleteReplyUrl" value='/events/${event.id}/reply/${response.id}/delete'/>

                                <a type="button" class="delete-message-button" href="${deleteReplyUrl}">
                                    <!-- Trash icon SVG -->
                                    <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <polyline points="3 6 5 6 21 6"></polyline>
                                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                                    </svg>
                                </a>
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
            <jsp:param name="pageSize" value="${chatPageSize}" />
            <jsp:param name="baseUrl" value="/events/${event.id}" />
        </jsp:include>

    </div>


    <c:set var="activeTab" value="${isEventOwner ? 'chat' : ''}" />

    <div class="reply-container">
        <h3 class="reply-title">
            <!-- Edit icon SVG -->
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            <spring:message code="reply.message" text="Leave a comment" />
        </h3>
        <c:url var="replyUrl" value="/events/${event.id}"/>
        <form:form modelAttribute="replyEventForm" action="${replyUrl}" method="post" enctype="multipart/form-data" cssClass="reply-form">
            <!-- Hidden field to track active tab -->
            <c:if test="${not empty param.activeTab}">
                <input type="hidden" name="activeTab" id="active-tab-input" value="${param.activeTab}" />
            </c:if>

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