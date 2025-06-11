<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty tipsPage.content}">
    <div class="empty-state">
        <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
                <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
                <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
            </svg>
        </div>
        <p class="empty-message">
            <spring:message code="journey.detail.no.tips" text="No tips have been shared for this journey yet" />
        </p>

        <c:if test="${isOwner}">
            <p class="empty-message">
                <spring:message code="journey.detail.add.first.tip" text="Share your first tip to help others on their journey" />
            </p>
        </c:if>
    </div>
</c:if>

<c:if test="${not empty tipsPage.content}">
    <div class="tips-container">
        <c:forEach var="tip" items="${tipsPage.content}" varStatus="status">
            <div class="tip-card">
                <div class="tip-header">
                    <div class="tip-meta">
                        <h3 class="tip-title">
                            <c:out value="${tip.title}" />
                        </h3>
                        <p class="tip-date">
                            <fmt:parseDate value="${tip.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                            <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy" var="formattedDate" />
                            <c:out value="${formattedDate}" />
                        </p>
                    </div>

                    <c:if test="${isOwner}">
                        <div style="position: relative; display: inline-block;">
                            <button onclick="toggleTipMenu(${status.index})" class="btn-action" id="tipMenuButton${status.index}" style="background: none; border: 1px solid #e0e0e0; border-radius: 4px; padding: 6px; cursor: pointer;" aria-label="Tip actions">
                                <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="1"></circle>
                                    <circle cx="12" cy="5" r="1"></circle>
                                    <circle cx="12" cy="19" r="1"></circle>
                                </svg>
                            </button>

                            <div id="tipDropdown${status.index}" style="display: none; position: absolute; right: 0; top: 100%; background-color: white; min-width: 160px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2); border-radius: 6px; z-index: 1000; border: 1px solid #e0e0e0; padding: 6px 0;">
                                <c:url var="editTipUrl" value='/journeys/tips/${tip.id}/update'/>
                                <a href="${editTipUrl}" style="color: #333; padding: 10px 14px; text-decoration: none; display: flex; align-items: center; gap: 10px; font-size: 13px;" onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                                        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                                    </svg>
                                    <span><spring:message code="tip.edit" text="Edit Tip" /></span>
                                </a>

                                <c:url var="deleteTipUrl" value='/journeys/tips/${tip.id}/delete'/>
                                <a href="${deleteTipUrl}" style="color: #333; padding: 10px 14px; text-decoration: none; display: flex; align-items: center; gap: 10px; font-size: 13px;" onmouseover="this.style.backgroundColor='#fef2f2'; this.style.color='#dc2626'" onmouseout="this.style.backgroundColor='transparent'; this.style.color='#333'">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                        <path d="M3 6h18"></path>
                                        <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                                    </svg>
                                    <span><spring:message code="tip.delete" text="Delete Tip" /></span>
                                </a>
                            </div>
                        </div>
                    </c:if>
                </div>
                <div class="tip-content">
                    <p class="tip-message">
                        <c:out value="${tip.content}" />
                    </p>
                </div>
            </div>
        </c:forEach>
    </div>

    <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
        <jsp:param name="pageObjectTotalPages" value="${tipsPage.totalPages}" />
        <jsp:param name="currentPage" value="${tipsPage.currentPage}" />
        <jsp:param name="pageSize" value="${tipsPageSize}" />
        <jsp:param name="baseUrl" value="/journeys/${journey.id}?interestsPage=${interestPage.currentPage}&interestsSize=${interestPageSize}&eventsPage=${eventsPage.currentPage}&eventsSize=${eventsPageSize}&chatPage=${journeyResponsesPage.currentPage}&chatSize=${chatPageSize}" />
        <jsp:param name="paramName" value="tipsPage" />
        <jsp:param name="sizeParamName" value="tipsSize" />
    </jsp:include>
</c:if>

<c:if test="${isOwner}">
    <div class="add-tip-button-container">
        <c:url var="addTipFormUrl" value="/journeys/${journey.id}/tips/create"/>
        <a href="${addTipFormUrl}" class="add-tip-button">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                <path d="M12 5v14"></path>
                <path d="M5 12h14"></path>
            </svg>
            <span><spring:message code="journey.tip.add" text="Share a New Tip" /></span>
        </a>
    </div>
</c:if>

<style>
    .tips-container {
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        margin-bottom: 2rem;
    }

    .tip-card {
        background-color: #f9fafb;
        border-radius: 0.75rem;
        padding: 1.5rem;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
        border-left: 3px solid #4f46e5;
        position: relative;
    }

    .tip-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 1rem;
    }

    .tip-meta {
        flex-grow: 1;
    }

    .tip-title {
        font-size: 1.25rem;
        font-weight: 600;
        margin: 0 0 0.5rem;
        color: #111827;
    }

    .tip-date {
        font-size: 0.875rem;
        color: #6b7280;
        margin: 0;
    }

    .tip-content {
        color: #4b5563;
    }

    .tip-message {
        margin: 0;
        line-height: 1.6;
        white-space: pre-line;
    }

    .add-tip-button-container {
        display: flex;
        justify-content: center;
        margin-top: 2rem;
        padding-top: 1.5rem;
        border-top: 1px solid #e5e7eb;
    }

    .add-tip-button {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        background: linear-gradient(135deg, #4f46e5, #6366f1);
        color: white;
        text-decoration: none;
        padding: 0.75rem 1.5rem;
        border-radius: 0.5rem;
        font-weight: 500;
        transition: all 0.3s ease;
        box-shadow: 0 2px 5px rgba(79, 70, 229, 0.3);
    }

    .add-tip-button:hover {
        background: linear-gradient(135deg, #4338ca, #4f46e5);
        transform: translateY(-2px);
        box-shadow: 0 4px 8px rgba(79, 70, 229, 0.4);
        color: white;
    }

    .add-tip-button:active {
        transform: translateY(0);
        box-shadow: 0 2px 4px rgba(79, 70, 229, 0.3);
    }

    @media (max-width: 768px) {
        .tip-card {
            padding: 1.25rem;
        }

        .tip-title {
            font-size: 1.125rem;
        }

        .add-tip-button {
            width: 100%;
            justify-content: center;
        }
    }
</style>

<script>
    function toggleTipMenu(index) {
        const dropdown = document.getElementById('tipDropdown' + index);
        if (dropdown.style.display === 'none' || dropdown.style.display === '') {
            dropdown.style.display = 'block';
        } else {
            dropdown.style.display = 'none';
        }
    }

    // Close tip dropdowns when clicking outside
    document.addEventListener('click', function(event) {
        const tipButtons = document.querySelectorAll('[id^="tipMenuButton"]');
        const tipDropdowns = document.querySelectorAll('[id^="tipDropdown"]');

        tipButtons.forEach(function(tipButton, index) {
            const tipDropdown = document.getElementById('tipDropdown' + index);
            if (tipDropdown && tipButton && !tipButton.contains(event.target) && !tipDropdown.contains(event.target)) {
                tipDropdown.style.display = 'none';
            }
        });
    });
</script>