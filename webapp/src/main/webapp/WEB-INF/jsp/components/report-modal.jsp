<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- Report Modal -->
<div id="report-modal" style="display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0, 0, 0, 0.5); z-index: 1000; justify-content: center; align-items: center;">
  <div style="background: white; border-radius: 12px; padding: 24px; width: 90%; max-width: 500px; max-height: 90vh; overflow-y: auto; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);">

    <!-- Modal Header -->
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
      <h2 style="margin: 0; font-size: 20px; font-weight: 600; color: #111;">
        <spring:message code="report.modal.title" text="Report Content" />
      </h2>
      <button type="button" onclick="closeReportModal()"
              style="background: none; border: none; font-size: 24px; cursor: pointer; color: #666; padding: 0; width: 32px; height: 32px; display: flex; align-items: center; justify-content: center; border-radius: 6px;"
              onmouseover="this.style.backgroundColor='#f5f5f5'" onmouseout="this.style.backgroundColor='transparent'">
        ×
      </button>
    </div>

    <!-- Modal Description -->
    <p style="margin: 0 0 24px 0; color: #666; line-height: 1.5;">
      <spring:message code="report.modal.description" text="Please provide details about why you're reporting this content. Your report will be reviewed by our moderation team." />
    </p>

    <!-- Report Form -->
    <form id="report-form" action="" method="post">

      <!-- Hidden fields to track what we're reporting -->
      <input type="hidden" id="report-target-type" name="targetType" value="">
      <input type="hidden" id="report-target-id" name="targetId" value="">

      <!-- Report Reason -->
      <div style="margin-bottom: 20px;">
        <label for="reason" style="display: block; margin-bottom: 8px; font-weight: 500; color: #374151;">
          <spring:message code="report.reason.label" text="Report Reason" />
          <span style="color: #dc2626;">*</span>
        </label>
        <select id="reason" name="reason" required
                style="width: 100%; padding: 12px; border: 1px solid #d1d5db; border-radius: 6px; font-size: 14px; background-color: white; color: #374151;"
                onfocus="this.style.borderColor='#3b82f6'; this.style.outline='none'; this.style.boxShadow='0 0 0 3px rgba(59, 130, 246, 0.1)'"
                onblur="this.style.borderColor='#d1d5db'; this.style.boxShadow='none'">
          <option value="">
            <spring:message code="report.reason.placeholder" text="Select a reason" />
          </option>
          <option value="spam">
            <spring:message code="report.reason.spam" text="Spam or unwanted content" />
          </option>
          <option value="harassment">
            <spring:message code="report.reason.harassment" text="Harassment or bullying" />
          </option>
          <option value="inappropriate">
            <spring:message code="report.reason.inappropriate" text="Inappropriate content" />
          </option>
          <option value="misinformation">
            <spring:message code="report.reason.misinformation" text="False or misleading information" />
          </option>
          <option value="hate_speech">
            <spring:message code="report.reason.hate_speech" text="Hate speech or discrimination" />
          </option>
          <option value="violence">
            <spring:message code="report.reason.violence" text="Violence or threats" />
          </option>
          <option value="other">
            <spring:message code="report.reason.other" text="Other" />
          </option>
        </select>
      </div>

      <!-- Additional Details -->
      <div style="margin-bottom: 24px;">
        <label for="description" style="display: block; margin-bottom: 8px; font-weight: 500; color: #374151;">
          <spring:message code="report.description.label" text="Additional Details" />
          <span style="color: #6b7280; font-weight: normal; font-size: 14px;">
                        (<spring:message code="form.optional" text="optional" />)
                    </span>
        </label>
        <textarea id="description" name="description" rows="4" maxlength="500"
                  placeholder="<spring:message code='report.description.placeholder' text='Please provide any additional details that might help us understand the issue...' />"
                  style="width: 100%; padding: 12px; border: 1px solid #d1d5db; border-radius: 6px; resize: vertical; font-family: inherit; font-size: 14px; color: #374151; line-height: 1.5;"
                  onfocus="this.style.borderColor='#3b82f6'; this.style.outline='none'; this.style.boxShadow='0 0 0 3px rgba(59, 130, 246, 0.1)'"
                  onblur="this.style.borderColor='#d1d5db'; this.style.boxShadow='none'"
                  oninput="updateCharacterCount(this)"></textarea>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 6px;">
          <div style="font-size: 12px; color: #6b7280;">
            <spring:message code="report.description.help" text="Maximum 500 characters" />
          </div>
          <div id="char-count" style="font-size: 12px; color: #6b7280;">
            0/500
          </div>
        </div>
      </div>

      <!-- Form Actions -->
      <div style="display: flex; gap: 12px; justify-content: flex-end;">
        <button type="button" onclick="closeReportModal()"
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
    </form>
  </div>
</div>

<!-- Modal JavaScript Functions -->
<script>
  function openReportModal(targetType, targetId, actionUrl) {
    // Set the form action URL
    document.getElementById('report-form').action = actionUrl;

    // Set hidden fields for context
    document.getElementById('report-target-type').value = targetType;
    document.getElementById('report-target-id').value = targetId;

    // Reset form
    document.getElementById('report-form').reset();

    // Reset character count
    document.getElementById('char-count').textContent = '0/500';

    // Show modal
    document.getElementById('report-modal').style.display = 'flex';

    // Focus on reason select after a short delay
    setTimeout(() => {
      document.getElementById('reason').focus();
    }, 100);

    // Prevent body scroll when modal is open
    document.body.style.overflow = 'hidden';
  }

  function closeReportModal() {
    // Hide modal
    document.getElementById('report-modal').style.display = 'none';

    // Restore body scroll
    document.body.style.overflow = 'auto';

    // Reset form
    document.getElementById('report-form').reset();
    document.getElementById('char-count').textContent = '0/500';
  }

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

  // Close modal when clicking outside of it
  document.getElementById('report-modal').addEventListener('click', function(e) {
    if (e.target === this) {
      closeReportModal();
    }
  });

  // Close modal with Escape key
  document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && document.getElementById('report-modal').style.display === 'flex') {
      closeReportModal();
    }
  });

  // Form submission handling
  document.getElementById('report-form').addEventListener('submit', function(e) {
    const reason = document.getElementById('reason').value;
    if (!reason) {
      e.preventDefault();
      alert('<spring:message code="report.reason.required" text="Please select a reason for the report" />');
      document.getElementById('reason').focus();
      return false;
    }

    // Show loading state
    const submitButton = this.querySelector('button[type="submit"]');
    const originalText = submitButton.textContent;
    submitButton.textContent = '<spring:message code="report.submitting" text="Submitting..." />';
    submitButton.disabled = true;

    // Note: The form will submit normally, this is just for user feedback
    // You might want to handle this with AJAX instead
  });
</script>