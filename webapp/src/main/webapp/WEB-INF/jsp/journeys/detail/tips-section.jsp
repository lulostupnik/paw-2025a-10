<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--ESTO VIENE DE V0 NO IMAGINO QUE SEA AS(--%>
<div class="tips-section">
  <!-- Add Tip Form (Only for journey owner) -->
  <c:if test="${isOwner}">
    <div class="add-tip-container">
      <button class="btn-add-tip" onclick="toggleTipForm()">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="16"></line>
          <line x1="8" y1="12" x2="16" y2="12"></line>
        </svg>
        <span><spring:message code="journey.tips.add" text="Add a Tip"/></span>
      </button>

      <div class="tip-form-container" id="tipFormContainer" style="display: none;">
        <div class="tip-form-card">
          <h3 class="tip-form-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
              <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
            </svg>
            <spring:message code="journey.tips.share" text="Share a Tip"/>
          </h3>

          <form:form action="${pageContext.request.contextPath}/journeys/${journey.id}/tips"
                     method="post"
                     modelAttribute="tipForm">

            <div class="form-group">
              <label for="tipTitle" class="form-label">
                <spring:message code="journey.tips.title" text="Tip Title"/>
              </label>
              <form:input path="title"
                          id="tipTitle"
                          class="form-control"
                          placeholder="Enter a descriptive title for your tip"/>
              <form:errors path="title" cssClass="error-message"/>
            </div>

            <div class="form-group">
              <label for="tipCategory" class="form-label">
                <spring:message code="journey.tips.category" text="Category"/>
              </label>
              <form:select path="category" id="tipCategory" class="form-control">
                <form:option value="" label="Select a category"/>
                <form:option value="ACCOMMODATION" label="Accommodation"/>
                <form:option value="TRANSPORTATION" label="Transportation"/>
                <form:option value="FOOD" label="Food & Dining"/>
                <form:option value="ACTIVITIES" label="Activities"/>
                <form:option value="BUDGET" label="Budget"/>
                <form:option value="SAFETY" label="Safety"/>
                <form:option value="CULTURE" label="Culture"/>
                <form:option value="OTHER" label="Other"/>
              </form:select>
              <form:errors path="category" cssClass="error-message"/>
            </div>

            <div class="form-group">
              <label for="tipContent" class="form-label">
                <spring:message code="journey.tips.content" text="Tip Content"/>
              </label>
              <form:textarea path="content"
                             id="tipContent"
                             class="form-control tip-textarea"
                             rows="4"
                             placeholder="Share your helpful tip or advice..."/>
              <form:errors path="content" cssClass="error-message"/>
            </div>

            <div class="form-actions">
              <button type="submit" class="btn btn-primary">
                <spring:message code="journey.tips.post" text="Post Tip"/>
              </button>
              <button type="button" class="btn btn-secondary" onclick="cancelTipForm()">
                <spring:message code="journey.tips.cancel" text="Cancel"/>
              </button>
            </div>
          </form:form>
        </div>
      </div>
    </div>
  </c:if>

  <!-- Tips List -->
  <div class="tips-content">
    <c:choose>
      <c:when test="${empty tipsPage.content}">
        <div class="empty-state">
          <div class="empty-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-img">
              <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
              <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
            </svg>
          </div>
          <p class="empty-message">
            <spring:message code="journey.tips.no.tips" text="No tips shared yet" />
          </p>
          <c:if test="${isOwner}">
            <p class="empty-suggestion">
              <spring:message code="journey.tips.first.tip" text="Be the first to share a helpful tip about your journey!" />
            </p>
          </c:if>
        </div>
      </c:when>
      <c:otherwise>
        <div class="tips-container">
          <c:forEach var="tip" items="${tipsPage.content}">
            <div class="tip-card">
              <div class="tip-header">
                <div class="tip-category">
                  <c:choose>
                    <c:when test="${tip.category == 'ACCOMMODATION'}">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                        <polyline points="9,22 9,12 15,12 15,22"></polyline>
                      </svg>
                    </c:when>
                    <c:when test="${tip.category == 'TRANSPORTATION'}">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="12" cy="12" r="2"></circle>
                        <path d="M12 1v6m0 6v6"></path>
                        <path d="m15.5 3.5-3 3-3-3"></path>
                        <path d="m15.5 20.5-3-3-3 3"></path>
                      </svg>
                    </c:when>
                    <c:when test="${tip.category == 'FOOD'}">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M18 8h1a4 4 0 0 1 0 8h-1"></path>
                        <path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path>
                        <line x1="6" y1="1" x2="6" y2="4"></line>
                        <line x1="10" y1="1" x2="10" y2="4"></line>
                        <line x1="14" y1="1" x2="14" y2="4"></line>
                      </svg>
                    </c:when>
                    <c:when test="${tip.category == 'BUDGET'}">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="12" y1="1" x2="12" y2="23"></line>
                        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                      </svg>
                    </c:when>
                    <c:otherwise>
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M9 11H5a2 2 0 0 0-2 2v3c0 1.1.9 2 2 2h4l3 3V8l-3 3z"></path>
                        <path d="M22 4H12a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h9l1 1V6a2 2 0 0 0-2-2z"></path>
                      </svg>
                    </c:otherwise>
                  </c:choose>
                  <span class="category-text">
                    <spring:message code="journey.tips.category.${tip.category}" text="${tip.category}"/>
                  </span>
                </div>

                <div class="tip-meta">
                  <fmt:parseDate value="${tip.createdDate}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedTipDate" />
                  <fmt:formatDate value="${parsedTipDate}" pattern="MMM d, yyyy" var="formattedTipDate" />
                  <span class="tip-date">
                    <c:out value="${formattedTipDate}" />
                  </span>

                  <c:if test="${isOwner || pageContext.request.isUserInRole('ADMIN')}">
                    <div class="tip-actions">
                      <button class="tip-action-btn" onclick="toggleTipMenu(${tip.id})">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                          <circle cx="12" cy="12" r="1"></circle>
                          <circle cx="12" cy="5" r="1"></circle>
                          <circle cx="12" cy="19" r="1"></circle>
                        </svg>
                      </button>
                      <div class="tip-menu" id="tipMenu${tip.id}" style="display: none;">
                        <a href="<c:url value='/journeys/${journey.id}/tips/${tip.id}/delete'/>" class="tip-menu-item delete">
                          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <path d="M3 6h18"></path>
                            <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                          </svg>
                          <spring:message code="journey.tips.delete" text="Delete"/>
                        </a>
                      </div>
                    </div>
                  </c:if>
                </div>
              </div>

              <div class="tip-content">
                <h3 class="tip-title"><c:out value="${tip.title}" /></h3>
                <p class="tip-text"><c:out value="${tip.content}" /></p>
              </div>

              <div class="tip-footer">
                <div class="tip-author">
                  <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                    <circle cx="12" cy="7" r="4"></circle>
                  </svg>
                  <span><c:out value="${tip.author.firstname}" /> <c:out value="${tip.author.lastname}" /></span>
                </div>
              </div>
            </div>
          </c:forEach>
        </div>

        <!-- Pagination -->
        <c:if test="${tipsPage.totalPages > 1}">
          <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
            <jsp:param name="pageObjectTotalPages" value="${tipsPage.totalPages}" />
            <jsp:param name="currentPage" value="${tipsPage.currentPage}" />
            <jsp:param name="pageSize" value="${tipsPageSize}" />
            <jsp:param name="baseUrl" value="/journeys/${journey.id}?tab=tips" />
            <jsp:param name="paramName" value="tipsPage" />
            <jsp:param name="sizeParamName" value="tipsSize" />
          </jsp:include>
        </c:if>
      </c:otherwise>
    </c:choose>
  </div>
</div>

<style>
  /* Tips Section Styles */
  .tips-section {
    margin-top: 16px;
  }

  .add-tip-container {
    margin-bottom: 24px;
  }

  .btn-add-tip {
    background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
    color: white;
    border: none;
    padding: 12px 20px;
    border-radius: 8px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: all 0.2s ease;
    box-shadow: 0 2px 4px rgba(0, 123, 255, 0.2);
  }

  .btn-add-tip:hover {
    background: linear-gradient(135deg, #0056b3 0%, #004085 100%);
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
  }

  .tip-form-container {
    margin-top: 16px;
    animation: slideDown 0.3s ease-out;
  }

  @keyframes slideDown {
    from {
      opacity: 0;
      transform: translateY(-10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .tip-form-card {
    background: #fff;
    border: 1px solid #e9ecef;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }

  .tip-form-title {
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 0 0 20px 0;
    font-size: 18px;
    font-weight: 600;
    color: #212529;
  }

  .form-group {
    margin-bottom: 20px;
  }

  .form-label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
    color: #495057;
    font-size: 14px;
  }

  .form-control {
    width: 100%;
    padding: 12px;
    border: 1px solid #ced4da;
    border-radius: 6px;
    font-size: 14px;
    transition: border-color 0.2s ease;
    box-sizing: border-box;
  }

  .form-control:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
  }

  .tip-textarea {
    resize: vertical;
    min-height: 100px;
  }

  .form-actions {
    display: flex;
    gap: 12px;
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #e9ecef;
  }

  .btn {
    padding: 10px 20px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.2s ease;
  }

  .btn-primary {
    background-color: #28a745;
    color: white;
  }

  .btn-primary:hover {
    background-color: #218838;
  }

  .btn-secondary {
    background-color: #6c757d;
    color: white;
  }

  .btn-secondary:hover {
    background-color: #5a6268;
  }

  .error-message {
    color: #dc3545;
    font-size: 12px;
    margin-top: 4px;
    display: block;
  }

  /* Tips List Styles */
  .tips-container {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .tip-card {
    background: #fff;
    border: 1px solid #e9ecef;
    border-radius: 12px;
    padding: 20px;
    transition: all 0.2s ease;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  }

  .tip-card:hover {
    border-color: #007bff;
    box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
    transform: translateY(-1px);
  }

  .tip-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }

  .tip-category {
    display: flex;
    align-items: center;
    gap: 8px;
    background: #f8f9fa;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 500;
    color: #495057;
  }

  .tip-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 12px;
    color: #6c757d;
  }

  .tip-actions {
    position: relative;
  }

  .tip-action-btn {
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;
    border-radius: 4px;
    color: #6c757d;
    transition: all 0.2s ease;
  }

  .tip-action-btn:hover {
    background-color: #f8f9fa;
    color: #495057;
  }

  .tip-menu {
    position: absolute;
    right: 0;
    top: 100%;
    background: white;
    border: 1px solid #e9ecef;
    border-radius: 6px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    z-index: 1000;
    min-width: 120px;
  }

  .tip-menu-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    text-decoration: none;
    color: #495057;
    font-size: 12px;
    transition: background-color 0.2s ease;
  }

  .tip-menu-item:hover {
    background-color: #f8f9fa;
  }

  .tip-menu-item.delete:hover {
    background-color: #fef2f2;
    color: #dc2626;
  }

  .tip-content {
    margin-bottom: 16px;
  }

  .tip-title {
    font-size: 16px;
    font-weight: 600;
    color: #212529;
    margin: 0 0 8px 0;
  }

  .tip-text {
    font-size: 14px;
    color: #495057;
    line-height: 1.5;
    margin: 0;
  }

  .tip-footer {
    padding-top: 12px;
    border-top: 1px solid #f1f3f4;
  }

  .tip-author {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #6c757d;
  }

  /* Empty State */
  .empty-state {
    text-align: center;
    padding: 48px 24px;
    color: #6c757d;
  }

  .empty-icon {
    margin-bottom: 16px;
  }

  .empty-icon-img {
    color: #dee2e6;
  }

  .empty-message {
    font-size: 16px;
    margin: 0 0 8px 0;
  }

  .empty-suggestion {
    font-size: 14px;
    margin: 0;
    color: #868e96;
  }

  /* Responsive Design */
  @media (max-width: 768px) {
    .tip-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }

    .tip-meta {
      align-self: flex-end;
    }

    .form-actions {
      flex-direction: column;
    }

    .btn {
      width: 100%;
    }
  }
</style>

<script>
  function toggleTipForm() {
    const container = document.getElementById('tipFormContainer');
    if (container.style.display === 'none') {
      container.style.display = 'block';
    } else {
      container.style.display = 'none';
    }
  }

  function cancelTipForm() {
    document.getElementById('tipFormContainer').style.display = 'none';
    // Clear form fields
    document.getElementById('tipTitle').value = '';
    document.getElementById('tipCategory').value = '';
    document.getElementById('tipContent').value = '';
  }

  function toggleTipMenu(tipId) {
    const menu = document.getElementById('tipMenu' + tipId);
    // Close all other menus
    document.querySelectorAll('.tip-menu').forEach(m => {
      if (m.id !== 'tipMenu' + tipId) {
        m.style.display = 'none';
      }
    });

    if (menu.style.display === 'none') {
      menu.style.display = 'block';
    } else {
      menu.style.display = 'none';
    }
  }

  // Close menus when clicking outside
  document.addEventListener('click', function(event) {
    if (!event.target.closest('.tip-actions')) {
      document.querySelectorAll('.tip-menu').forEach(menu => {
        menu.style.display = 'none';
      });
    }
  });
</script>

