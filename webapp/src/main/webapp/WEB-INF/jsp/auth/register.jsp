<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%--
  Created by IntelliJ IDEA.
  User: nicol
  Date: 4/12/2025
  Time: 5:01 PM
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <title><spring:message code="register.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="auth-container">
    <a href="<c:url value="/"/>" class="back-link">
        <svg xmlns="http://www.w3.org/2000/svg" class="back-link-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18" />
        </svg>
        <spring:message code="register.back"/>
    </a>

    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <!-- You can add your logo here -->
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="register.title"/></h1>
            <p class="auth-subtitle"><spring:message code="register.subtitle" text="Create your account to get started"/></p>
        </div>

        <c:url var="registerUrl" value="/register"/>
        <form:form modelAttribute="createUserForm" action="${registerUrl}" method="post" enctype="multipart/form-data" class="auth-form">
            <div class="auth-columns">
                <div class="auth-column">
                    <!-- Personal Information -->
                    <div class="form-group">
                        <form:label path="email" cssClass="form-label required-field">
                            <spring:message code="createJourney.userEmail"/>
                        </form:label>
                        <form:input path="email" type="email" cssClass="form-input ${not empty errors.getFieldError('email') ? 'error' : ''}"
                                    placeholder="example@email.com" required="true" />
                        <form:errors path="email" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="password" cssClass="form-label required-field">
                            <spring:message code="createJourney.password"/>
                        </form:label>
                        <form:password path="password" cssClass="form-input ${not empty errors.getFieldError('password') ? 'error' : ''}"
                                       placeholder="••••••••" required="true" />
                        <form:errors path="password" cssClass="error-message" />
                        <div class="helper-text">
                            <spring:message code="register.password.requirements" text="Password must be at least 8 characters"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <form:label path="firstName" cssClass="form-label required-field">
                            <spring:message code="createJourney.firstName"/>
                        </form:label>
                        <form:input path="firstName" cssClass="form-input ${not empty errors.getFieldError('firstName') ? 'error' : ''}"
                                    placeholder="John" required="true" />
                        <form:errors path="firstName" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="lastName" cssClass="form-label required-field">
                            <spring:message code="createJourney.lastName"/>
                        </form:label>
                        <form:input path="lastName" cssClass="form-input ${not empty errors.getFieldError('lastName') ? 'error' : ''}"
                                    placeholder="Doe" required="true" />
                        <form:errors path="lastName" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="username" cssClass="form-label required-field">
                            <spring:message code="createJourney.username"/>
                        </form:label>
                        <form:input path="username" cssClass="form-input ${not empty errors.getFieldError('username') ? 'error' : ''}"
                                    placeholder="johndoe" required="true" />
                        <form:errors path="username" cssClass="error-message" />
                    </div>
                </div>

                <div class="auth-column">
                    <!-- Academic Information -->
                    <div class="form-group">
                        <form:label path="career" cssClass="form-label required-field">
                            <spring:message code="event.career"/>
                        </form:label>
                        <form:select path="career" cssClass="form-select ${not empty errors.getFieldError('career') ? 'error' : ''}" required="true">
                            <form:option value=""><spring:message code="event.career.select"/></form:option>
                            <c:forEach var="item" items="${careers}">
                                <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="career" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="originUniversity" cssClass="form-label required-field">
                            <spring:message code="createJourney.originUniversity"/>
                        </form:label>
                        <form:select path="originUniversity" cssClass="form-select ${not empty errors.getFieldError('originUniversity') ? 'error' : ''}" required="true">
                            <form:option value=""><spring:message code="createJourney.originUniversity.select"/></form:option>
                            <c:forEach var="item" items="${universities}">
                                <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                            </c:forEach>
                        </form:select>
                        <form:errors path="originUniversity" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="interests" cssClass="form-label">
                            <spring:message code="event.interest"/>
                        </form:label>

                        <!-- Hidden select that will hold the actual form data -->
                        <form:select path="interests" multiple="true" id="interestsSelect" style="display: none;">
                            <c:forEach var="item" items="${interests}">
                                <option value="${item.name}"><c:out value="${item.name}"/></option>
                            </c:forEach>
                        </form:select>

                        <!-- Custom UI for interests selection -->
                        <div class="autocomplete-container">
                            <input type="text" id="interestSearch" class="autocomplete-input"
                                   placeholder="<spring:message code="event.interest.search"/>" />

                            <div id="interestDropdown" class="autocomplete-dropdown" style="display: none;">
                                <c:forEach var="item" items="${interests}">
                                    <div class="autocomplete-item" data-value="${item.name}">
                                        <c:out value="${item.name}"/>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <!-- Selected interests will appear here as tags -->
                        <div id="selectedInterests" class="selected-tags"></div>

                        <div class="helper-text">
                            <spring:message code="event.interest.select"/>
                        </div>

                        <form:errors path="interests" cssClass="error-message" />
                    </div>

                    <div class="form-group">
                        <form:label path="profilePicture" cssClass="form-label">
                            <spring:message code="createJourney.profile_picture"/>
                        </form:label>
                        <div class="file-upload">
                            <label class="file-upload-label">
                                <svg xmlns="http://www.w3.org/2000/svg" class="file-upload-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                </svg>
                                <span class="file-upload-text">
                                    <spring:message code="upload_picture.profile"/>
                                </span>
                                <form:input path="profilePicture" type="file" cssClass="file-upload-input" accept="image/png, image/jpeg" />
                            </label>
                        </div>
                        <div id="filePreview" class="file-preview" style="display: none;">
                            <img id="previewImage" class="file-preview-image" src="#" alt="Preview" />
                            <span id="fileName" class="file-preview-name"></span>
                            <button type="button" id="removeFile" class="file-preview-remove">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                    <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z"/>
                                </svg>
                            </button>
                        </div>
                        <form:errors path="profilePicture" cssClass="error-message" />
                    </div>
                </div>
            </div>

            <button type="submit" class="auth-button">
                <spring:message code="register.submit"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <spring:message code="register.have.account" text="Already have an account?"/>
            <a href="<c:url value='/login'/>" class="auth-link">
                <spring:message code="register.login" text="Sign in"/>
            </a>
        </div>
    </div>
</div>

<script>
    // Interests selection functionality
    document.addEventListener('DOMContentLoaded', function() {
        const interestsSelect = document.getElementById('interestsSelect');
        const interestSearch = document.getElementById('interestSearch');
        const interestDropdown = document.getElementById('interestDropdown');
        const selectedInterests = document.getElementById('selectedInterests');
        const interestOptions = document.querySelectorAll('.autocomplete-item');

        // Store selected values
        let selectedValues = [];

        // Initialize with any pre-selected values (for edit forms)
        initializeSelectedValues();
        updateSelectedTags();

        // Toggle dropdown on input focus
        interestSearch.addEventListener('focus', function() {
            interestDropdown.style.display = 'block';
            filterOptions(this.value);
        });

        // Hide dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!interestSearch.contains(e.target) && !interestDropdown.contains(e.target)) {
                interestDropdown.style.display = 'none';
            }
        });

        // Filter options as user types
        interestSearch.addEventListener('input', function() {
            filterOptions(this.value);
            interestDropdown.style.display = 'block';
        });

        // Handle option selection
        interestOptions.forEach(option => {
            option.addEventListener('click', function() {
                const value = this.dataset.value;
                const text = this.textContent.trim();

                // Check if already selected
                const exists = selectedValues.some(item => item.value === value);

                // Toggle selection
                if (!exists) {
                    // Add to selected values
                    selectedValues.push({ value: value, text: text });

                    // Update the hidden select
                    updateSelectElement();
                }

                // Update the UI
                updateSelectedTags();
                interestSearch.value = '';
                interestDropdown.style.display = 'none';
            });
        });

        // Filter dropdown options based on search text
        function filterOptions(searchText) {
            const filter = searchText.toLowerCase();
            interestOptions.forEach(option => {
                const text = option.textContent.toLowerCase();
                if (text.includes(filter)) {
                    option.style.display = '';
                } else {
                    option.style.display = 'none';
                }
            });
        }

        // Initialize selected values from the select element
        function initializeSelectedValues() {
            // Get all option elements
            const options = interestsSelect.querySelectorAll('option');

            // Check which ones are selected
            options.forEach(option => {
                if (option.selected) {
                    selectedValues.push({
                        value: option.value,
                        text: option.textContent.trim()
                    });
                }
            });
        }

        // Update the select element based on selectedValues array
        function updateSelectElement() {
            const options = interestsSelect.querySelectorAll('option');
            const selectedValueIds = selectedValues.map(item => item.value);

            options.forEach(option => {
                option.selected = selectedValueIds.includes(option.value);
            });
        }

        // Update the selected tags UI
        function updateSelectedTags() {
            // Clear existing tags
            selectedInterests.innerHTML = '';

            // Create tags for each selected value
            selectedValues.forEach(item => {
                // Create tag container
                const tag = document.createElement('div');
                tag.className = 'selected-tag';

                // Add text node
                const textNode = document.createTextNode(item.text);
                tag.appendChild(textNode);

                // Create remove button
                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.className = 'tag-remove';
                removeBtn.dataset.value = item.value;

                // Create SVG for the remove button
                const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
                svg.setAttribute('width', '12');
                svg.setAttribute('height', '12');
                svg.setAttribute('fill', 'currentColor');
                svg.setAttribute('viewBox', '0 0 16 16');

                const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
                path.setAttribute('d', 'M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z');

                svg.appendChild(path);
                removeBtn.appendChild(svg);

                // Add remove button functionality
                removeBtn.addEventListener('click', function() {
                    const valueToRemove = this.dataset.value;
                    selectedValues = selectedValues.filter(item => item.value !== valueToRemove);
                    updateSelectElement();
                    updateSelectedTags();
                });

                // Add button to tag
                tag.appendChild(removeBtn);

                // Add tag to container
                selectedInterests.appendChild(tag);
            });
        }
    });

    // File upload preview
    document.addEventListener('DOMContentLoaded', function() {
        const fileInput = document.querySelector('input[type="file"]');
        const filePreview = document.getElementById('filePreview');
        const previewImage = document.getElementById('previewImage');
        const fileName = document.getElementById('fileName');
        const removeFile = document.getElementById('removeFile');

        fileInput.addEventListener('change', function() {
            if (this.files && this.files[0]) {
                const file = this.files[0];

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
    });

    // Form validation
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('.auth-form');

        form.addEventListener('submit', function(e) {
            let isValid = true;

            // Get all required inputs
            const requiredInputs = form.querySelectorAll('input[required], select[required]');

            requiredInputs.forEach(input => {
                if (!input.value.trim()) {
                    showError(input, '<spring:message code="register.field.required" text="This field is required"/>');
                    isValid = false;
                } else {
                    clearError(input);
                }
            });

            // Email validation
            const emailInput = form.querySelector('input[type="email"]');
            if (emailInput && emailInput.value.trim() && !isValidEmail(emailInput.value.trim())) {
                showError(emailInput, '<spring:message code="register.email.invalid" text="Please enter a valid email address"/>');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
                // Scroll to the first error
                const firstError = form.querySelector('.error-message');
                if (firstError) {
                    firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }
        });

        function showError(input, message) {
            const formGroup = input.closest('.form-group');
            let errorElement = formGroup.querySelector('.error-message');

            input.classList.add('error');

            if (!errorElement) {
                errorElement = document.createElement('div');
                errorElement.className = 'error-message';
                errorElement.textContent = message;
                formGroup.appendChild(errorElement);
            } else {
                errorElement.textContent = message;
            }
        }

        function clearError(input) {
            const formGroup = input.closest('.form-group');
            const errorElement = formGroup.querySelector('.error-message:not(.field-error)');

            input.classList.remove('error');

            if (errorElement) {
                errorElement.remove();
            }
        }

        function isValidEmail(email) {
            const re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return re.test(email);
        }

        // Add input event listeners for real-time validation
        const inputs = form.querySelectorAll('input, select');
        inputs.forEach(input => {
            input.addEventListener('input', function() {
                if (this.hasAttribute('required') && this.value.trim()) {
                    clearError(this);
                }

                if (this.type === 'email' && this.value.trim() && isValidEmail(this.value.trim())) {
                    clearError(this);
                }
            });
        });
    });
</script>
</body>
</html>
