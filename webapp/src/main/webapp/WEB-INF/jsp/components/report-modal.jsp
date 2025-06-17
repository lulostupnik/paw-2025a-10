<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%--
Expected URL parameters:
- actionUrl: The URL where the form should be submitted
- targetType: The type of content being reported (e.g., "post", "comment", "user")
- targetId: The ID of the content being reported

Example URL: /report?actionUrl=/api/reports&targetType=post&targetId=123
--%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="report.page.title" text="Report Content" /></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/event-detail.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>
<script>
  function goBack(){
    const rutaAnterior = popFromNavigationStack()
    if (rutaAnterior) {
      window.location.href = rutaAnterior;
    } else {
      window.location.href = "<c:url value='/explore'/>"
    }
  }
</script>
<body>
<div class="layout-container">
  <div class="main-content">
    <jsp:include page="../components/navbar.jsp"/>
    <div class="content-container">
  <!-- Page Header -->
      <div class="back-button-container">
        <c:if test="${not isEventOwner}">
          <button onclick="goBack()" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M19 12H5"></path>
              <path d="M12 19l-7-7 7-7"></path>
            </svg>
            <span><spring:message code="event.detail.back.to.list" /></span>
          </button>
        </c:if>
        <c:if test="${isEventOwner}">
          <button onclick="goBack()" class="back-link">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M19 12H5"></path>
              <path d="M12 19l-7-7 7-7"></path>
            </svg>
            <span><spring:message code="event.detail.back.to.profile" /></span>
          </button>
        </c:if>
      </div>
      <div class="event-detail-container event-top-section">
      <div class="page-header">
    <h1 class="page-title">
      <spring:message code="report.modal.title" text="Report Content" />
    </h1>
  </div>

  <!-- Error Messages Display -->
  <c:if test="${not empty reportFormErrors}">
    <div style="background-color: #fef2f2; border: 1px solid #fecaca; border-radius: 6px; padding: 12px; margin-bottom: 20px;">
      <div style="display: flex; align-items: center; margin-bottom: 8px;">
                    <span style="color: #dc2626; font-weight: 500; font-size: 14px;">
                        <spring:message code="report.errors.title" text="Please correct the following errors:" />
                    </span>
      </div>
      <ul style="margin: 0; padding-left: 20px; color: #dc2626; font-size: 13px;">
        <c:forEach items="${reportFormErrors}" var="error">
          <li style="margin-bottom: 4px;">
            <c:out value="${error.defaultMessage}" />
          </li>
        </c:forEach>
      </ul>
    </div>
  </c:if>

  <!-- Page Description -->
  <p class="page-description">
    <spring:message code="report.modal.description" text="Please provide details about why you're reporting this content. Your report will be reviewed by our moderation team." />
  </p>

  <!-- Report Form -->
        <c:set var="postUrl"><c:url value="${actionUrl}"/></c:set>
  <form:form modelAttribute="createReportForm" id="report-form" action="${postUrl}" method="post">

    <!-- Hidden fields to track what we're reporting -->
    <form:hidden path="reportType" id="report-target-type" value="${targetType}"/>
    <form:hidden path="targetId" id="report-target-id" value="${targetId}"/>

    <!-- Report Reason -->
    <div style="margin-bottom: 20px;">
      <label for="reason" style="display: block; margin-bottom: 8px; font-weight: 500; color: #374151;">
        <spring:message code="report.reason.label" text="Report Reason" />
        <span style="color: #dc2626;">*</span>
      </label>
      <form:select path="reason" id="reason"
              style="width: 100%; padding: 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; background-color: white; color: #374151;"
              onfocus="this.style.borderColor='#3b82f6'; this.style.outline='none'; this.style.boxShadow='0 0 0 3px rgba(59, 130, 246, 0.1)'"
              onblur="this.style.borderColor='#d1d5db'; this.style.boxShadow='none'">
        <form:option value="">
          <spring:message code="report.reason.placeholder" text="Select a reason" />
        </form:option>
        <form:option value="SPAM">
          <spring:message code="report.reason.spam" text="Spam or unwanted content" />
        </form:option>
        <form:option value="HARASSMENT" >
          <spring:message code="report.reason.harassment" text="Harassment or bullying" />
        </form:option>
        <form:option value="INAPPROPRIATE_CONTENT" >
          <spring:message code="report.reason.inappropriate" text="Inappropriate content" />
        </form:option>
        <form:option value="MISINFORMATION">
          <spring:message code="report.reason.misinformation" text="False or misleading information" />
        </form:option>
        <form:option value="HATE_SPEECH" >
          <spring:message code="report.reason.hate_speech" text="Hate speech or discrimination" />
        </form:option>
        <form:option value="VIOLENCE">
          <spring:message code="report.reason.violence" text="Violence or threats" />
        </form:option>
        <form:option value="OTHER">
          <spring:message code="report.reason.other" text="Other" />
        </form:option>
      </form:select>
      <form:errors path="reason" cssClass="error-message"/>
    </div>

    <!-- Additional Details -->
    <div style="margin-bottom: 24px;">
      <label for="description" style="display: block; margin-bottom: 8px; font-weight: 500; color: #374151;">
        <spring:message code="report.description.label" text="Additional Details" />
        <span style="color: #dc2626;">*</span>
      </label>
      <c:set var="placeholderDescription"><spring:message code='report.description.placeholder' text='Please provide any additional details that might help us understand the issue...' /></c:set>
      <form:textarea id="description" path="description" rows="4" maxlength="500" placeholder="${placeholderDescription}"
                style="width: 100%; padding: 12px; border: 1px solid #d1d5db; border-radius: 6px; resize: vertical; font-family: inherit; font-size: 14px; color: #374151; line-height: 1.5;"/>
      <form:errors path="description" cssClass="error-message" />
    </div>

    <!-- Form Actions -->
    <div style="display: flex; gap: 12px; justify-content: flex-end; flex-wrap: wrap;">
      <button type="button" onclick="goBack()"
              style="padding: 12px 24px; border: 1px solid #d1d5db; background: white; color: #374151; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500; transition: all 0.2s;"
              onmouseover="this.style.backgroundColor='#f9fafb'; this.style.borderColor='#9ca3af'"
              onmouseout="this.style.backgroundColor='white'; this.style.borderColor='#d1d5db'">
        <spring:message code="report.modal.cancel" text="Cancel" />
      </button>
      <button type="submit"
              style="padding: 12px 24px; background: #dc2626; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 14px; font-weight: 500; transition: all 0.2s;"
              onmouseover="this.style.backgroundColor='#b91c1c'"
              onmouseout="this.style.backgroundColor='#dc2626'">
        <spring:message code="report.modal.submit" text="Submit Report" />
      </button>
    </div>
  </form:form>
</div>
    </div>
</div>
</div>

<!-- JavaScript Functions -->
<script>
  function updateCharacterCount(textarea) {
    const currentLength = textarea.value.length;
    const maxLength = 500;
    const charCountElement = document.getElementById('char-count');

    charCountElement.textContent = currentLength + '/' + maxLength;

    // Change color if approaching limit
    if (currentLength > maxLength * 0.9) {
      charCountElement.style.color = '#dc2626';
    } else if (currentLength > maxLength * 0.7) {
      charCountElement.style.color = '#f59e0b';
    } else {
      charCountElement.style.color = '#6b7280';
    }
  }

  // Form submission handling
  document.getElementById('report-form').addEventListener('submit', function(e) {
    // Show loading state
    const submitButton = this.querySelector('button[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.textContent = '<spring:message code="report.submitting" text="Submitting..." />';
    submitButton.disabled = true;
  });

  // Initialize character count on page load
  document.addEventListener('DOMContentLoaded', function() {
    const descriptionField = document.getElementById('description');
    if (descriptionField.value) {
      updateCharacterCount(descriptionField);
    }
  });
</script>
</body>
</html>