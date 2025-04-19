<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="event.create.title"/></title>
    <!-- Include CSS files -->
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancements.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="event.create.header"/></h1>
            <p class="auth-subtitle"><spring:message code="event.create.subtitle" text="Create a new event to share with others"/></p>
        </div>

        <c:url var="createEventUrl" value="/events/create"/>
        <form:form modelAttribute="createEventForm" action="${createEventUrl}" method="post" enctype="multipart/form-data" class="auth-form">
            <!-- City Field with Autocomplete -->
            <div class="form-group">
                <form:label path="city" cssClass="form-label required-field">
                    <spring:message code="event.city"/>
                </form:label>
                <div class="autocomplete-wrapper">
                    <form:select path="city" id="city" cssClass="form-select ${not empty errors.getFieldError('city') ? 'error' : ''}" required="true" style="display: none;">
                        <form:option value=""><spring:message code="createJourney.destinationCity.select"/></form:option>
                        <c:forEach var="item" items="${cities}">
                            <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <input type="text" id="citySearch" class="form-input" placeholder="<spring:message code="event.city.search" text="Type to search city..."/>" />
                    <div id="cityDropdown" class="dropdown-menu" style="display: none;">
                        <c:forEach var="item" items="${cities}">
                            <div class="dropdown-item" data-value="${item.name}">
                                <c:out value="${item.name}"/>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <form:errors path="city" cssClass="error-message" />
            </div>

            <!-- Date Field -->
            <div class="form-group">
                <form:label path="date" cssClass="form-label required-field">
                    <spring:message code="event.date"/>
                </form:label>
                <form:input path="date" type="date" cssClass="form-input ${not empty errors.getFieldError('date') ? 'error' : ''}" required="true" />
                <form:errors path="date" cssClass="error-message" />
            </div>

            <!-- Description Field -->
            <div class="form-group">
                <form:label path="description" cssClass="form-label required-field">
                    <spring:message code="event.description"/>
                </form:label>
                <c:set var="descriptionHint"><spring:message code="event.description.hint"/></c:set>
                <form:textarea path="description"
                               cssClass="form-textarea ${not empty errors.getFieldError('description') ? 'error' : ''}"
                               placeholder="${descriptionHint}"
                               required="true" />
                <form:errors path="description" cssClass="error-message" />
            </div>

            <!-- Enhanced file upload area for Flyer -->
            <div class="form-group">
                <form:label path="flyer" cssClass="form-label">
                    <spring:message code="event.flyer"/>
                </form:label>
                <div class="file-upload">
                    <label class="file-upload-label">
                        <svg xmlns="http://www.w3.org/2000/svg" class="file-upload-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <span class="file-upload-text">
                            <spring:message code="upload_picture.flyer"/>
                        </span>
                        <span class="file-upload-hint">
                            <spring:message code="upload_picture.hint" text="JPG or PNG, max 5MB"/>
                        </span>
                        <form:input path="flyer" type="file" cssClass="file-upload-input" accept="image/png, image/jpeg" />
                    </label>
                </div>
                <div id="filePreview" class="file-preview" style="display: none;">
                    <img id="previewImage" class="file-preview-image" src="#" alt="Preview" />
                    <span id="fileName" class="file-preview-name"></span>
                    <button type="button" id="removeFile" class="file-preview-remove" aria-label="Remove file">
                        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                            <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                        </svg>
                    </button>
                </div>
                <form:errors path="flyer" cssClass="error-message" />
            </div>

            <button type="submit" class="form-button">
                <spring:message code="event.create.button"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <a href="<c:url value='/events'/>" class="auth-link">
                <spring:message code="event.back" text="Back to events"/>
            </a>
        </div>
    </div>
</div>

<!-- JavaScript for autocomplete and file upload preview -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // Initialize city autocomplete
        initAutocomplete("city", "citySearch", "cityDropdown");

        // Initialize file upload preview
        initFileUploadPreview();

        /**
         * Initialize autocomplete for select fields
         */
        function initAutocomplete(selectId, searchId, dropdownId) {
            const selectField = document.getElementById(selectId);
            const searchInput = document.getElementById(searchId);
            const dropdown = document.getElementById(dropdownId);

            if (!selectField || !searchInput || !dropdown) return;

            const dropdownItems = dropdown.querySelectorAll(".dropdown-item");

            // Show dropdown on focus
            searchInput.addEventListener("focus", function() {
                dropdown.style.display = "block";
                filterDropdownItems(this.value.toLowerCase(), dropdownItems);
            });

            // Hide dropdown when clicking outside
            document.addEventListener("click", function(e) {
                if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
                    dropdown.style.display = "none";
                }
            });

            // Filter items as user types
            searchInput.addEventListener("input", function() {
                dropdown.style.display = "block";
                filterDropdownItems(this.value.toLowerCase(), dropdownItems);
            });

            // Handle item selection with visual feedback
            dropdownItems.forEach(item => {
                item.addEventListener("click", function() {
                    const value = this.dataset.value;
                    const text = this.textContent.trim();

                    // Update the select field
                    selectField.value = value;

                    // Update the search input
                    searchInput.value = text;

                    // Add highlight effect
                    searchInput.classList.add("highlight-selection");
                    setTimeout(() => {
                        searchInput.classList.remove("highlight-selection");
                    }, 1000);

                    // Hide dropdown
                    dropdown.style.display = "none";

                    // Trigger change event
                    const event = new Event("change");
                    selectField.dispatchEvent(event);
                });
            });

            // Initialize with selected value if any
            if (selectField.value) {
                const selectedOption = Array.from(selectField.options).find(option => option.value === selectField.value);
                if (selectedOption) {
                    searchInput.value = selectedOption.textContent;
                }
            }
        }

        /**
         * Filter dropdown items based on search text
         */
        function filterDropdownItems(searchText, items) {
            let visibleCount = 0;

            items.forEach(item => {
                const text = item.textContent.toLowerCase();
                const isVisible = text.includes(searchText);
                item.style.display = isVisible ? "block" : "none";
                if (isVisible) visibleCount++;
            });

            return visibleCount;
        }

        /**
         * Initialize file upload preview
         */
        function initFileUploadPreview() {
            const fileInput = document.querySelector('input[type="file"]');
            const filePreview = document.getElementById('filePreview');
            const previewImage = document.getElementById('previewImage');
            const fileName = document.getElementById('fileName');
            const removeFile = document.getElementById('removeFile');

            if (!fileInput || !filePreview || !previewImage || !fileName || !removeFile) return;

            fileInput.addEventListener('change', function() {
                if (this.files && this.files[0]) {
                    const file = this.files[0];

                    // Check file size (max 5MB)
                    if (file.size > 5 * 1024 * 1024) {
                        alert('File size exceeds 5MB limit');
                        this.value = '';
                        return;
                    }

                    // Update file name
                    fileName.textContent = file.name;

                    // Create preview image
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        previewImage.src = e.target.result;
                        filePreview.style.display = 'flex';
                    }
                    reader.readAsDataURL(file);
                }
            });

            // Remove file
            removeFile.addEventListener('click', function() {
                fileInput.value = '';
                filePreview.style.display = 'none';
                previewImage.src = '#';
                fileName.textContent = '';
            });
        }
    });
</script>
</body>
</html>
