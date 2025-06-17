<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="content-section">
    <div class="section-header">
        <h2 class="section-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <spring:message code="journey.detail.comments" text="Comments" />
            <span class="count">(<c:out value="${journeyResponsesPage.totalElements}" />)</span>
        </h2>
    </div>

    <div id="journey-chat-list" class="chat-list">
        <c:if test="${empty journeyResponsesPage.content}">
            <div class="empty-state">
                <div class="empty-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                    </svg>
                </div>
                <p class="empty-message">
                    <spring:message code="journey.detail.no.responses" />
                </p>
            </div>
        </c:if>

        <c:if test="${not empty journeyResponsesPage.content}">
            <c:set var="sortedResponses" value="${journeyResponsesPage.content}" />
            <c:forEach var="response" items="${sortedResponses}" varStatus="status">
                <div class="chat-message">
                    <div class="message-header">
                        <div class="message-user">
                            <div class="message-avatar">
                                <div class="avatar-placeholder">
                                    <c:out value="${fn:substring(response.user.username, 0, 1)}" />
                                </div>
                            </div>
                            <div class="message-user-info">
                                <h3 class="message-username">
                                    <c:out value="${response.user.username}" />
                                </h3>
                                <p class="message-date">
                                    <fmt:parseDate value="${response.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                                    <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy HH:mm" var="formattedDate" />
                                    <c:out value="${formattedDate}" />
                                </p>
                            </div>
                        </div>

                        <!-- Comment Actions Dropdown -->
                        <div style="position: relative; display: inline-block;">
                            <button onclick="toggleCommentMenu(${status.index})" class="btn-action" id="commentMenuButton${status.index}" style="background: none; border: 1px solid #e0e0e0; border-radius: 4px; padding: 6px; cursor: pointer;" aria-label="Comment actions">
                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="1"></circle>
                                    <circle cx="12" cy="5" r="1"></circle>
                                    <circle cx="12" cy="19" r="1"></circle>
                                </svg>
                            </button>

                            <div id="commentDropdown${status.index}" style="display: none; position: absolute; right: 0; top: 100%; background-color: white; min-width: 160px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); border-radius: 6px; z-index: 1000; border: 1px solid #e0e0e0; padding: 6px 0;">

                                <!-- Report option for everyone except the owner -->
                                <c:if test="${!isOwner || pageContext.request.isUserInRole('ADMIN')}">
                                    <c:url var="reportJourneyCommentUrl" value='/reports/journey-responses/${response.id}/create'/>
                                    <a href="${reportJourneyCommentUrl}" onclick="saveLink()"
                                       style="color: #333; padding: 10px 14px; text-decoration: none; display: flex; align-items: center; gap: 10px; font-size: 13px;"
                                       onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M12 9v4"></path>
                                            <path d="M12 17h.01"></path>
                                            <circle cx="12" cy="12" r="10"></circle>
                                        </svg>
                                        <span><spring:message code="comment.report" text="Report Comment" /></span>
                                    </a>
                                </c:if>

                                <!-- Delete option for admins only -->
                                <sec:authorize access="hasRole('ADMIN')">
                                    <c:url var="deleteReplyUrl" value='/journeys/reply/${response.id}/delete'/>
                                    <a href="<c:out value='${deleteReplyUrl}'/>" style="color: #333; padding: 10px 14px; text-decoration: none; display: flex; align-items: center; gap: 10px; font-size: 13px;" onmouseover="this.style.backgroundColor='#fef2f2'; this.style.color='#dc2626'" onmouseout="this.style.backgroundColor='transparent'; this.style.color='#333'">
                                        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                            <path d="M3 6h18"></path>
                                            <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                        </svg>
                                        <span><spring:message code="comment.delete" text="Delete Comment" /></span>
                                    </a>
                                </sec:authorize>
                            </div>
                        </div>
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
            <jsp:param name="pageObjectTotalPages" value="${journeyResponsesPage.totalPages}" />
            <jsp:param name="currentPage" value="${journeyResponsesPage.currentPage}" />
            <jsp:param name="pageSize" value="${chatPageSize}" />
            <jsp:param name="baseUrl" value="/journeys/${journey.id}?interestsPage=${interestPage.currentPage}&interestsSize=${interestPageSize}&eventsPage=${eventsPage.currentPage}&eventsSize=${eventsPageSize}" />
        </jsp:include>
    </div>

    <div class="reply-container">
        <h3 class="reply-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
            </svg>
            <spring:message code="reply.message" text="Leave a comment" />
        </h3>
        <c:url var="replyUrl" value="/journeys/${journey.id}"/>
        <form:form modelAttribute="replyJourneyForm" action="${replyUrl}" method="post" enctype="multipart/form-data" cssClass="reply-form">

            <c:set var="messageLabel"><spring:message code="reply.message"/></c:set>
            <c:set var="messageHint"><spring:message code="reply.message.hint"/></c:set>
            <jsp:include page="../../components/text-area.jsp">
                <jsp:param name="path" value="message" />
                <jsp:param name="label" value="${messageLabel}" />
                <jsp:param name="placeholder" value="${messageHint}" />
            </jsp:include>

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

<script>
    function toggleSection(sectionId) {
        const section = document.getElementById(sectionId);
        const button = document.querySelector(`[data-toggle="${sectionId}"]`);
        const collapseIcon = button.querySelector('.collapse-icon');
        const expandIcon = button.querySelector('.expand-icon');

        if (section.classList.contains('collapsed')) {
            section.classList.remove('collapsed');
            collapseIcon.style.display = 'block';
            expandIcon.style.display = 'none';
        } else {
            section.classList.add('collapsed');
            collapseIcon.style.display = 'none';
            expandIcon.style.display = 'block';
        }
    }

    function toggleCommentMenu(index) {
        const dropdown = document.getElementById('commentDropdown' + index);
        if (dropdown.style.display === 'none' || dropdown.style.display === '') {
            dropdown.style.display = 'block';
        } else {
            dropdown.style.display = 'none';
        }
    }

    // Close comment dropdowns when clicking outside
    document.addEventListener('click', function(event) {
        const commentButtons = document.querySelectorAll('[id^="commentMenuButton"]');
        const commentDropdowns = document.querySelectorAll('[id^="commentDropdown"]');

        commentButtons.forEach(function(commentButton, index) {
            const commentDropdown = document.getElementById('commentDropdown' + index);
            if (commentDropdown && commentButton && !commentButton.contains(event.target) && !commentDropdown.contains(event.target)) {
                commentDropdown.style.display = 'none';
            }
        });
    });
</script>