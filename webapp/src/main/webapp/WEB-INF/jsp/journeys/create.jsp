<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title><spring:message code="createJourney.title"/></title>
    <!-- Include custom CSS -->
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/form-enhancment.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        /* Additional styles for autocomplete functionality */
        .autocomplete-wrapper {
            position: relative;
            width: 100%;
        }

        .dropdown-menu {
            position: absolute;
            top: 100%;
            left: 0;
            width: 100%;
            max-height: 200px;
            overflow-y: auto;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.15);
            z-index: 1000;
            margin-top: 2px;
        }

        .dropdown-item {
            padding: 10px 15px;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .dropdown-item:hover {
            background-color: #f5f5f5;
        }

        .highlight-selection {
            background-color: #e6f7ff;
            transition: background-color 0.3s;
        }
    </style>
</head>
<body>
<jsp:include page="../components/navbar.jsp"/>

<div class="auth-container">
    <div class="auth-card">
        <div class="auth-header">
            <div class="auth-logo">
                <svg xmlns="http://www.w3.org/2000/svg" class="auth-logo-img" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                </svg>
            </div>
            <h1 class="auth-title"><spring:message code="createJourney.title"/></h1>
            <p class="auth-subtitle"><spring:message code="createJourney.subtitle" text="Share your academic journey with others"/></p>
        </div>

        <c:url var="createJourneyUrl" value="/journeys/create"/>
        <form:form modelAttribute="createJourneyForm" action="${createJourneyUrl}" method="post" class="auth-form">
            <!-- Start Date Field -->
            <div class="form-group">
                <form:label path="startDate" cssClass="form-label required-field">
                    <spring:message code="createJourney.startDate"/>
                </form:label>
                <form:input path="startDate" type="date" cssClass="form-input ${not empty errors.getFieldError('startDate') ? 'error' : ''}" required="true" />
                <form:errors path="startDate" cssClass="error-message" />
            </div>

            <!-- End Date Field -->
            <div class="form-group">
                <form:label path="endDate" cssClass="form-label required-field">
                    <spring:message code="createJourney.endDate"/>
                </form:label>
                <form:input path="endDate" type="date" cssClass="form-input ${not empty errors.getFieldError('endDate') ? 'error' : ''}" required="true" />
                <form:errors path="endDate" cssClass="error-message" />
            </div>

            <!-- Destination University Field with Autocomplete -->
            <div class="form-group">
                <form:label path="destinationUniversity" cssClass="form-label required-field">
                    <spring:message code="createJourney.destinationUniversity"/>
                </form:label>
                <div class="autocomplete-wrapper">
                    <form:select path="destinationUniversity" id="universitySelect" cssClass="form-select" style="display: none;">
                        <form:option value=""><spring:message code="createJourney.destinationUniversity.select"/></form:option>
                        <c:forEach var="university" items="${universities}">
                            <form:option value="${university.id}">${university.name}</form:option>
                        </c:forEach>
                    </form:select>
                    <input type="text" id="universityInput" class="form-input" placeholder="<spring:message code="createJourney.destinationUniversity.search" text="Type to search university..."/>" />
                    <div id="universityDropdown" class="dropdown-menu" style="display: none;">
                        <c:forEach var="university" items="${universities}">
                            <div class="dropdown-item" data-value="${university.id}" data-text="${university.name}">
                                    ${university.name}
                            </div>
                        </c:forEach>
                    </div>
                </div>
                <form:errors path="destinationUniversity" cssClass="error-message" />
            </div>

            <!-- Description Field -->
            <div class="form-group">
                <form:label path="description" cssClass="form-label required-field">
                    <spring:message code="createJourney.description"/>
                </form:label>
                <form:textarea path="description" cssClass="form-textarea ${not empty errors.getFieldError('description') ? 'error' : ''}"
                               placeholder="<spring:message code="createJourney.description.hint"/>" required="true" />
                <form:errors path="description" cssClass="error-message" />
            </div>

            <button type="submit" class="form-button">
                <spring:message code="createJourney.submit"/>
            </button>
        </form:form>

        <div class="auth-footer">
            <a href="<c:url value='/journeys'/>" class="auth-link">
                <spring:message code="journey.back" text="Back to journeys"/>
            </a>
        </div>
    </div>
</div>

<!-- JavaScript for autocomplete -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
        // University autocomplete functionality
        const universitySelect = document.getElementById('universitySelect');
        const universityInput = document.getElementById('universityInput');
        const universityDropdown = document.getElementById('universityDropdown');
        const universityItems = universityDropdown ? universityDropdown.querySelectorAll('.dropdown-item') : [];

        // Initialize with selected value if any
        if (universitySelect && universitySelect.value) {
            for (const option of universitySelect.options) {
                if (option.value === universitySelect.value) {
                    if (universityInput) universityInput.value = option.textContent;
                    break;
                }
            }
        }

        // Show dropdown when input is focused
        if (universityInput && universityDropdown) {
            universityInput.addEventListener('focus', function() {
                universityDropdown.style.display = 'block';
                filterItems(universityInput.value.toLowerCase(), universityItems);
            });

            // Filter items as user types
            universityInput.addEventListener('input', function() {
                universityDropdown.style.display = 'block';
                filterItems(this.value.toLowerCase(), universityItems);
            });
        }

        // Handle item selection
        if (universityItems.length > 0) {
            universityItems.forEach(item => {
                item.addEventListener('click', function() {
                    const value = this.getAttribute('data-value');
                    const text = this.getAttribute('data-text');

                    if (universitySelect) universitySelect.value = value;
                    if (universityInput) {
                        universityInput.value = text;
                        // Visual feedback
                        universityInput.classList.add('highlight-selection');
                        setTimeout(() => {
                            universityInput.classList.remove('highlight-selection');
                        }, 800);
                    }

                    if (universityDropdown) universityDropdown.style.display = 'none';
                });
            });
        }

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (universityInput && universityDropdown && !universityInput.contains(e.target) && !universityDropdown.contains(e.target)) {
                universityDropdown.style.display = 'none';
            }
        });

        // Helper function to filter dropdown items
        function filterItems(searchText, items) {
            let hasVisibleItems = false;

            items.forEach(item => {
                const itemText = item.textContent.toLowerCase();
                if (itemText.includes(searchText)) {
                    item.style.display = 'block';
                    hasVisibleItems = true;
                } else {
                    item.style.display = 'none';
                }
            });

            return hasVisibleItems;
        }

        // Add console logging for debugging
        console.log('University Select:', universitySelect);
        console.log('University Input:', universityInput);
        console.log('University Dropdown:', universityDropdown);
        console.log('University Items:', universityItems.length);
    });
</script>
</body>
</html>
