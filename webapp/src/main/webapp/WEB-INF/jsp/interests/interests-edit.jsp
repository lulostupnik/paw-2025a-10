<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html lang="${pageContext.response.locale}">
<head>
  <title><spring:message code="editInterests.title" text="Edit Interests"/></title>
  <!-- Include custom CSS -->
  <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
  <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
  <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>
<jsp:include page="../components/i18n-hidden-inputs.jsp"/>

<div class="auth-container">
  <div class="auth-card">
    <div class="auth-header">
      <div class="auth-logo">
        <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" />
        </svg>
      </div>
      <h1 class="auth-title">
        <spring:message code="editInterests.title" text="Edit Interests"/>
      </h1>
      <p class="auth-subtitle">
        <spring:message code="editInterests.subtitle" text="Update your interests"/>
      </p>
    </div>

    <c:url var="formAction" value="/interests/edit"/>

    <form:form modelAttribute="editInterestsForm" action="${formAction}" method="post" class="auth-form" id="interestsForm" novalidate="true">
      <!-- Interests Field -->
      <div class="form-group">
        <form:label path="interests" cssClass="form-label required-field">
          <spring:message code="event.interest" text="Interests"/>
        </form:label>

        <!-- Hidden select that will hold the actual form data -->
        <form:select path="interests" multiple="true" id="interestsSelect" style="display: none;">
          <c:forEach var="interest" items="${interests}">
            <c:set var="isSelected" value="false" />
            <c:forEach var="userInterest" items="${userInterests}">
              <c:if test="${interest.id == userInterest.id}">
                <c:set var="isSelected" value="true" />
              </c:if>
            </c:forEach>
            <option value="${interest.id}" ${isSelected ? 'selected' : ''}><c:out value="${interest.name}"/></option>
          </c:forEach>
        </form:select>

        <!-- Custom UI for interests selection -->
        <div class="autocomplete-wrapper">
          <input type="text" id="interestSearch" class="autocomplete-input ${not empty errors.getFieldError('interests') ? 'error' : ''}"
                 placeholder="<spring:message code="event.interest.search" text="Search interests..."/>" />

          <div id="interestDropdown" class="autocomplete-dropdown" style="display: none;">
            <c:forEach var="interest" items="${interests}">
              <div class="autocomplete-item" data-id="${interest.id}" data-value="${interest.name}">
                <c:out value="${interest.name}"/>
              </div>
            </c:forEach>
          </div>

          <!-- Selected interests will appear here as tags -->
          <div id="selectedInterests" class="selected-tags required-selected-tags"></div>

          <form:errors path="interests" cssClass="error-message" />
        </div>
      </div>

      <button type="submit" class="form-button">
        <spring:message code="editInterests.submit" text="Update Interests"/>
      </button>
    </form:form>

    <div class="auth-footer">
      <a class="auth-link" href="<c:url value='/profile/interests'/>">
        <spring:message code="interests.back" text="Back to Interests"/>
      </a>
    </div>
  </div>
</div>

<!-- Include JavaScript files -->
<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script>
  document.addEventListener("DOMContentLoaded", () => {
    const interestsForm = document.getElementById("interestsForm");
    const interestsSelect = document.getElementById("interestsSelect");
    const interestSearch = document.getElementById("interestSearch");
    const interestDropdown = document.getElementById("interestDropdown");
    const selectedInterests = document.getElementById("selectedInterests");
    const interestItems = document.querySelectorAll("#interestDropdown .autocomplete-item");

    // Initialize with user's existing interests
    function initializeUserInterests() {
      // Clear any existing tags
      selectedInterests.innerHTML = '';

      // Get all selected options from the hidden select
      const selectedOptions = Array.from(interestsSelect.selectedOptions);

      // Create tags for each selected interest
      selectedOptions.forEach(option => {
        const id = option.value;
        const name = option.text;
        addInterestTag(id, name);
      });
    }

    // Add a tag for an interest
    function addInterestTag(id, name) {
      // Check if this interest is already selected
      if (document.querySelector(`.selected-tag[data-id="${id}"]`)) {
        return;
      }

      // Create the tag element
      const tag = document.createElement("div");
      tag.className = "selected-tag";
      tag.setAttribute("data-id", id);
      tag.setAttribute("data-value", name);

      // Create the tag content with proper escaping
      const tagText = document.createElement("span");
      tagText.className = "tag-text";
      tagText.textContent = name;

      const tagRemove = document.createElement("span");
      tagRemove.className = "tag-remove";
      tagRemove.textContent = "×";

      tag.appendChild(tagText);
      tag.appendChild(tagRemove);

      // Add click handler to remove tag
      tagRemove.addEventListener("click", function() {
        // Remove the tag
        tag.remove();

        // Deselect the option in the hidden select
        const option = Array.from(interestsSelect.options).find(opt => opt.value === id);
        if (option) {
          option.selected = false;
        }
      });

      // Add the tag to the container
      selectedInterests.appendChild(tag);

      // Select the option in the hidden select
      const option = Array.from(interestsSelect.options).find(opt => opt.value === id);
      if (option) {
        option.selected = true;
      }
    }

    // Show/hide dropdown when clicking on the search input
    interestSearch.addEventListener("focus", function() {
      interestDropdown.style.display = "block";
    });

    // Filter interests as user types
    interestSearch.addEventListener("input", function() {
      const searchTerm = this.value.toLowerCase();

      interestItems.forEach(item => {
        const itemValue = item.getAttribute("data-value").toLowerCase();
        if (itemValue.includes(searchTerm)) {
          item.style.display = "block";
        } else {
          item.style.display = "none";
        }
      });

      // Show dropdown if there are matching items
      const hasVisibleItems = Array.from(interestItems).some(item => item.style.display !== "none");
      interestDropdown.style.display = hasVisibleItems ? "block" : "none";
    });

    // Handle clicking outside the dropdown
    document.addEventListener("click", function(e) {
      if (!interestSearch.contains(e.target) && !interestDropdown.contains(e.target)) {
        interestDropdown.style.display = "none";
      }
    });

    // Handle selecting an interest from the dropdown
    interestItems.forEach(item => {
      item.addEventListener("click", function() {
        const id = this.getAttribute("data-id");
        const value = this.getAttribute("data-value");

        // Add the tag
        addInterestTag(id, value);

        // Clear the search input
        interestSearch.value = "";

        // Hide the dropdown
        interestDropdown.style.display = "none";
      });
    });

    // Validate form before submission
    interestsForm.addEventListener("submit", function(e) {
      // Check if at least one interest is selected
      if (selectedInterests.children.length === 0) {
        e.preventDefault();
        // Add error class to the input
        interestSearch.classList.add("error");
        // Show error message
        const errorMsg = document.createElement("div");
        errorMsg.className = "error-message";
        errorMsg.textContent = "<spring:message code='interests.required' text='Please select at least one interest'/>";

        // Remove any existing error message
        const existingError = selectedInterests.nextElementSibling;
        if (existingError && existingError.classList.contains("error-message")) {
          existingError.remove();
        }

        selectedInterests.after(errorMsg);
      }
    });

    // Initialize the form with user's existing interests
    initializeUserInterests();
  });
</script>

</body>
</html>