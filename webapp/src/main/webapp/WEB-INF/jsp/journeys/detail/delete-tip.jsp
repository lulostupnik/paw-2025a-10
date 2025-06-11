<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title><spring:message code="journey.tip.delete.title" text="Delete Tip"/></title>
  <link rel="stylesheet" href="<c:url value="/resources/css/main.css"/>" />
  <link rel="stylesheet" href="<c:url value="/resources/css/event-detail.css"/>" />
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
  <script src="<c:url value='/resources/js/confirm-delete.js'/>"></script>
</head>

<body>
<div style="display: none;">
  <span id="i18n-tip.confirmDelete" data-message="<spring:message code='tip.confirmDelete' text='Are you sure you want to delete this tip?' />"></span>
  <span id="i18n-tip.deleteWarning" data-message="<spring:message code='tip.deleteWarning' text='This action cannot be undone. The tip will be permanently removed from your journey.' />"></span>
</div>

<div class="layout-container">
  <div class="main-content">
    <jsp:include page="../../components/navbar.jsp" />
    <div class="content-container">

      <div class="back-button-container">
        <button onclick="goBack()" class="back-link">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
            <path d="M19 12H5"></path>
            <path d="M12 19l-7-7 7-7"></path>
          </svg>
          <span><spring:message code="journey.tip.back.to.journey" text="Back to Journey" /></span>
        </button>
      </div>

      <div class="event-detail-container">
        <div class="event-top-section">
          <div class="event-header">
            <h1 class="event-title">
              <spring:message code="journey.tip.delete.title" text="Delete Tip" />
            </h1>
          </div>

          <div class="journey-info-card">
            <div class="journey-info-header">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="16" x2="12" y2="12"></line>
                <line x1="12" y1="8" x2="12.01" y2="8"></line>
              </svg>
              <span><spring:message code="journey.tip.from.journey" text="Tip from journey:" /></span>
            </div>
            <div class="journey-info-content">
              <h3 class="journey-info-title">
                <c:set var="escapedFirstname"><c:out value="${journey.user.firstname}"/></c:set>
                <c:set var="escapedLastname"><c:out value="${journey.user.lastname}"/></c:set>
                <spring:message arguments="${escapedFirstname},${escapedLastname}" code="journey.detail.section.title" />
              </h3>
              <div class="journey-info-meta">
                <div class="meta-item">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0118 0z"></path>
                    <circle cx="12" cy="10" r="3"></circle>
                  </svg>
                  <span class="destination-text">
                                        <c:out value="${journey.destinationUniversity.city}" /> -
                                        <c:out value="${journey.destinationUniversity.name}" />
                                    </span>
                </div>
              </div>
            </div>
          </div>


          <div class="tip-preview-container">
            <h3 class="tip-preview-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon">
                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <spring:message code="journey.tip.preview.title" text="Tip to be deleted:" />
            </h3>

            <div class="tip-preview-card">
              <div class="tip-preview-header">
                <div class="tip-preview-meta">
                  <h4 class="tip-preview-name">
                    <c:out value="${tip.title}" />
                  </h4>
                  <p class="tip-preview-date">
                    <fmt:parseDate value="${tip.dateTime}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDate" />
                    <fmt:formatDate value="${parsedDate}" pattern="MMMM d, yyyy 'at' HH:mm" var="formattedDate" />
                    <spring:message code="tip.created.on" text="Created on" /> <c:out value="${formattedDate}" />
                  </p>
                </div>
              </div>
              <div class="tip-preview-content">
                <div class="tip-preview-message">
                  <c:out value="${tip.content}" />
                </div>
              </div>
            </div>

            <div class="delete-form-container">
              <c:url var="deleteTipUrl" value="/journeys/tips/${tip.id}/delete"/>
              <form action="${deleteTipUrl}" method="post" class="delete-form" >
                <input type="hidden" name="_method" value="DELETE" />
                <div class="form-actions">
                  <c:url var="cancelUrl" value="/journeys/${journey.id}"/>
                  <a href="${cancelUrl}" class="btn-cancel">
                    <spring:message code="tip.cancel" text="Cancel" />
                  </a>
                  <button type="submit" class="btn-delete-confirm">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                      <path d="M3 6h18"></path>
                      <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"></path>
                    </svg>
                    <spring:message code="tip.delete.confirm" text="Delete Tip" />
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="<c:url value="/resources/js/components/navigation-stack.js"/>"></script>

<script>
  function goBack(){
    const rutaAnterior = popFromNavigationStack()
    if (rutaAnterior) {
      window.location.href = rutaAnterior;
    } else {
      window.location.href = "<c:url value='/journeys/${journey.id}'/>"
    }
  }
</script>

<style>
  .journey-info-card {
    background-color: #f9fafb;
    border-radius: 0.75rem;
    padding: 1.5rem;
    margin-bottom: 2rem;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    border-left: 4px solid #4f46e5;
  }

  .journey-info-header {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 0.875rem;
    color: #6b7280;
    margin-bottom: 1rem;
  }

  .journey-info-content {
    margin-left: 1.5rem;
  }

  .journey-info-title {
    font-size: 1.25rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 0.75rem;
  }

  .journey-info-meta {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    color: #4b5563;
    font-size: 0.875rem;
  }

  .warning-message {
    display: flex;
    align-items: flex-start;
    gap: 1rem;
    background-color: #fee2e2;
    border-left: 4px solid #ef4444;
    padding: 1.25rem;
    border-radius: 0.5rem;
    margin-bottom: 2rem;
  }

  .warning-message svg {
    flex-shrink: 0;
    color: #ef4444;
    margin-top: 0.125rem;
  }

  .warning-message p {
    margin: 0 0 0.5rem;
    color: #7f1d1d;
    line-height: 1.5;
  }

  .warning-message p:last-child {
    margin-bottom: 0;
  }

  .tip-preview-container {
    background-color: #ffffff;
    border-radius: 0.75rem;
    padding: 2rem;
    border: 1px solid #e5e7eb;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  }

  .tip-preview-title {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    font-size: 1.5rem;
    font-weight: 600;
    color: #111827;
    margin: 0 0 1.5rem;
    padding-bottom: 1rem;
    border-bottom: 1px solid #e5e7eb;
  }

  .tip-preview-card {
    background-color: #f9fafb;
    border-radius: 0.75rem;
    padding: 1.5rem;
    margin-bottom: 2rem;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    border-left: 3px solid #ef4444;
  }

  .tip-preview-header {
    margin-bottom: 1rem;
  }

  .tip-preview-meta {
    flex-grow: 1;
  }

  .tip-preview-name {
    font-size: 1.25rem;
    font-weight: 600;
    margin: 0 0 0.5rem;
    color: #111827;
  }

  .tip-preview-date {
    font-size: 0.875rem;
    color: #6b7280;
    margin: 0;
  }

  .tip-preview-content {
    color: #4b5563;
  }

  .tip-preview-message {
    margin: 0;
    line-height: 1.6;
    white-space: pre-line;
    background-color: #ffffff;
    padding: 1rem;
    border-radius: 0.5rem;
    border: 1px solid #e5e7eb;
  }

  .delete-form-container {
    border-top: 1px solid #e5e7eb;
    padding-top: 1.5rem;
  }

  .delete-form {
    width: 100%;
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
  }

  .btn-cancel {
    display: inline-flex;
    align-items: center;
    padding: 0.75rem 1.5rem;
    border-radius: 0.5rem;
    font-weight: 500;
    text-decoration: none;
    transition: all 0.2s ease;
    background-color: #f3f4f6;
    color: #4b5563;
    border: 1px solid #d1d5db;
  }

  .btn-cancel:hover {
    background-color: #e5e7eb;
    color: #374151;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }

  .btn-delete-confirm {
    display: inline-flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.75rem 1.5rem;
    border-radius: 0.5rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    background-color: #ef4444;
    color: white;
    border: none;
    box-shadow: 0 2px 4px rgba(239, 68, 68, 0.3);
  }

  .btn-delete-confirm:hover {
    background-color: #dc2626;
    transform: translateY(-1px);
    box-shadow: 0 4px 6px rgba(220, 38, 38, 0.4);
  }

  .btn-delete-confirm:active {
    transform: translateY(0);
    box-shadow: 0 2px 4px rgba(220, 38, 38, 0.3);
  }

  @media (max-width: 768px) {
    .tip-preview-container {
      padding: 1.5rem;
    }

    .tip-preview-card {
      padding: 1.25rem;
    }

    .form-actions {
      flex-direction: column-reverse;
    }

    .btn-cancel,
    .btn-delete-confirm {
      width: 100%;
      justify-content: center;
    }

    .warning-message {
      padding: 1rem;
    }

    .warning-message {
      flex-direction: column;
      text-align: center;
    }

    .warning-message svg {
      align-self: center;
      margin-top: 0;
      margin-bottom: 0.5rem;
    }
  }
</style>

</body>
</html>