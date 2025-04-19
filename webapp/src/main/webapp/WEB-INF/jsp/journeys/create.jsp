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
                    <form:select path="destinationUniversity" id="destinationUniversity" cssClass="form-select ${not empty errors.getFieldError('destinationUniversity') ? 'error' : ''}" required="true" style="display: none;">
                        <form:option value=""><spring:message code="createJourney.destinationUniversity.select"/></form:option>
                        <c:forEach var="item" items="${universities}">
                            <form:option value="${item.name}"><c:out value="${item.name}"/></form:option>
                        </c:forEach>
                    </form:select>
                    <input type="text" id="universitySearch" class="form-input" placeholder="<spring:message code="createJourney.destinationUniversity.search" text="Type to search university..."/>" />
                    <div id="universityDropdown" class="dropdown-menu" style="display: none;">
                        <c:forEach var="item" items="${universities}">
                            <div class="dropdown-item" data-value="${item.name}">
                                <c:out value="${item.name}"/>
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

                <c:set var="descriptionHint"><spring:message code="createJourney.description.hint"/></c:set>
                <form:textarea path="description"
                               cssClass="form-textarea ${not empty errors.getFieldError('description') ? 'error' : ''}"
                               placeholder="${descriptionHint}"
                               required="true" />
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
        // Initialize university autocomplete
        initAutocomplete("destinationUniversity", "universitySearch", "universityDropdown");

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
    });
</script>
</body>
</html>
