<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="journey.page.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
    <link rel="stylesheet" href="<c:url value='/resources/css/auth.css'/>" />
    <style>
        /* Autocomplete styling to match existing components */
        .autocomplete-wrapper {
            position: relative;
            width: 100%;
        }

        .autocomplete-input {
            display: block;
            width: 100%;
            padding: 0.75rem 1rem;
            font-size: 0.875rem;
            line-height: 1.5;
            color: #1f2937;
            background-color: #fff;
            background-clip: padding-box;
            border: 1px solid #e5e7eb;
            border-radius: 0.5rem;
            transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
            box-sizing: border-box;
        }

        .autocomplete-input:focus {
            border-color: #4f46e5;
            outline: 0;
            box-shadow: 0 0 0 0.2rem rgba(79, 70, 229, 0.25);
        }

        .autocomplete-dropdown {
            position: absolute;
            width: 100%;
            max-height: 200px;
            overflow-y: auto;
            background: white;
            border: 1px solid #e5e7eb;
            border-radius: 0.5rem;
            z-index: 1000;
            display: none;
            box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
            margin-top: 4px;
        }

        .autocomplete-item {
            padding: 0.75rem 1rem;
            cursor: pointer;
            font-size: 0.875rem;
            color: #1f2937;
            border-bottom: 1px solid #f3f4f6;
        }

        .autocomplete-item:last-child {
            border-bottom: none;
        }

        .autocomplete-item:hover {
            background-color: #f9fafb;
        }

        .selected-items-container {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
            margin-top: 8px;
        }

        .selected-tag {
            display: flex;
            align-items: center;
            background-color: #ede9fe;
            color: #4f46e5;
            border-radius: 0.375rem;
            padding: 0.25rem 0.75rem;
            font-size: 0.875rem;
            font-weight: 500;
        }

        .tag-remove {
            background: none;
            border: none;
            cursor: pointer;
            margin-left: 8px;
            padding: 0;
            display: flex;
            align-items: center;
            color: #4f46e5;
        }

        .hidden-select {
            display: none;
        }

        .empty-message {
            color: #6b7280;
            font-style: italic;
            font-size: 0.875rem;
            padding: 0.5rem 0;
        }

        .hidden {
            display: none !important;
        }

        /* Filter grid styling */
        .filter-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 1.5rem;
        }

        /* Button styling with icons */
        .btn-with-icon {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
        }

        .btn-icon {
            font-size: 0.875rem;
        }
        /* SVG icon styling for color inheritance */
        .btn-with-icon img.btn-icon {
            height: 1em;
            width: 1em;
            filter: invert(0%) sepia(0%) saturate(0%) hue-rotate(0deg) brightness(100%) contrast(100%);
        }

        /* Apply specific filters for different button types */
        .btn-primary img.btn-icon {
            /* For white icons on primary buttons */
            filter: brightness(0) invert(1);
        }

        .btn-secondary img.btn-icon {
            /* For darker icons on secondary buttons */
            filter: brightness(0) saturate(100%);
        }

        .btn-danger img.btn-icon {
            /* For white icons on danger buttons */
            filter: brightness(0) invert(1);
        }

        /* For tag remove buttons */
        .tag-remove img {
            height: 0.75em;
            width: 0.75em;
            filter: brightness(0) saturate(100%) invert(24%) sepia(90%) saturate(1960%) hue-rotate(235deg) brightness(97%) contrast(96%);
        }
    </style>
</head>
<body>

<div class="layout-container">
    <!-- Main Content -->
    <div class="main-content">
        <jsp:include page="../components/navbar.jsp" />
        <div class="content-container">
            <div class="header-container">
                <h2 class="page-title">
                    <spring:message code="journey.list.title"/>
                </h2>
                <div class="journeys-actions">
                    <button id="filterToggleBtn" class="btn-secondary btn-with-icon">
                        <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="journey.filter.toggle"/>" class="btn-icon" />
                        <spring:message code="journey.filter.toggle"/>
                    </button>
                    <c:if test="${hasJourney == false}">
                        <a href="<c:url value="/journeys/create"/>" class="btn btn-primary btn-with-icon">
                            <img src="<c:url value='/resources/icons/plus.svg'/>" alt="<spring:message code="journey.create.button"/>" class="btn-icon" />
                            <spring:message code="journey.create.button"/>
                        </a>
                    </c:if>
                </div>
            </div>

            <!-- Filter Section - Initially Hidden -->
            <div id="filterSection" class="filter-section hidden">
                <h3 class="filter-title">
                    <spring:message code="journey.filter.title"/>
                </h3>
                <c:set var="actionGet"><c:url value="/journeys"/></c:set>
                <form:form action="${actionGet}" method="GET"
                           modelAttribute="filterJourneyForm"
                           class="filter-form" id="journeyFilterForm">

                    <div class="filter-grid">
                        <!-- Destination filter with autocomplete -->
                        <div class="filter-item">
                            <c:set var="destinationLabel"><spring:message code="createJourney.destinationUniversity"/></c:set>
                            <form:label for="citySearch" class="form-label" path="destination">${destinationLabel}</form:label>
                            <div class="autocomplete-wrapper">
                                <input type="text" id="citySearch" class="autocomplete-input"
                                       placeholder="<spring:message code='journey.filter.destination.placeholder'/>"
                                       value="${param.destinationName}" />
                                <form:select id="city" name="destination" class="hidden-select" path="destination">
                                    <option value=""></option>
                                    <c:forEach var="city" items="${cities}">
                                        <option value="${city.id}" ${param.destination == city.id ? 'selected' : ''}><c:out value="${city.name}"/></option>
                                    </c:forEach>
                                </form:select>
                                <div id="cityDropdown" class="autocomplete-dropdown">
                                    <c:forEach var="city" items="${cities}">
                                        <div class="autocomplete-item" data-value="${city.id}"><c:out value="${city.name}"/></div>
                                    </c:forEach>
                                </div>
                                <div id="citySelectedContainer" class="selected-items-container"></div>
                            </div>
                            <form:errors path="destination" cssClass="error-message" />
                        </div>

                        <div class="filter-item">
                            <c:set var="startDateFilter"><spring:message code="journey.filter.startDate"/></c:set>
                            <jsp:include page="../components/date-field.jsp">
                                <jsp:param name="path" value="startDate"/>
                                <jsp:param name="label" value="${startDateFilter}"/>
                            </jsp:include>
                        </div>

                        <div class="filter-item">
                            <c:set var="endDateFilter"><spring:message code="journey.filter.endDate"/></c:set>
                            <jsp:include page="../components/date-field.jsp">
                                <jsp:param name="path" value="endDate"/>
                                <jsp:param name="label" value="${endDateFilter}"/>
                            </jsp:include>
                            <form:errors path="" cssClass="error-message" />
                        </div>

                        <!-- Interest filter with autocomplete -->
                        <div class="filter-item">
                            <c:set var="interestsLabel"><spring:message code="journey.filter.interest"/></c:set>
                            <form:label for="interest-search" class="form-label" path="interests">${interestsLabel}</form:label>
                            <div class="autocomplete-wrapper">
                                <input type="text" id="interest-search" class="autocomplete-input"
                                       placeholder="<spring:message code='journey.filter.interest.placeholder'/>"
                                       value="${param.interestName}" />
                                <form:select path="interests" id="interest-select" name="interest" class="hidden-select">
                                    <option value=""></option>
                                    <c:forEach var="interest" items="${interests}">
                                        <option value="${interest.id}" ${param.interest == interest.id ? 'selected' : ''}><c:out value="${interest.name}"/></option>
                                    </c:forEach>
                                </form:select>
                                <div id="interest-dropdown" class="autocomplete-dropdown">
                                    <c:forEach var="interest" items="${interests}">
                                        <div class="autocomplete-item" data-value="${interest.id}"><c:out value="${interest.name}"/></div>
                                    </c:forEach>
                                </div>
                                <div id="interestSelectedContainer" class="selected-items-container"></div>
                            </div>
                            <form:errors path="interests" cssClass="error-message" />
                        </div>

                    </div>

                    <div class="filter-actions">
                        <button type="button" id="resetFiltersBtn" class="btn-danger btn-with-icon">
                            <img src="<c:url value='/resources/icons/x.svg'/>" alt="<spring:message code="journey.filter.reset"/>" class="btn-icon" />
                            <spring:message code="journey.filter.reset"/>
                        </button>
                        <button type="submit" class="btn-secondary btn-with-icon">
                            <img src="<c:url value='/resources/icons/filter.svg'/>" alt="<spring:message code="journey.filter.button"/>" class="btn-icon" />
                            <spring:message code="journey.filter.button"/>
                        </button>
                    </div>
                </form:form>
            </div>

            <div class="events-container">
                <!-- Journeys List with grid layout -->
                <div class="events-grid">
                    <c:forEach var="journey" items="${journeys}">
                        <jsp:include page="journey-card.jsp">
                            <jsp:param name="journeyId" value="${journey.id}" />
                            <jsp:param name="city" value="${journey.destinationUniversity.city.name}" />
                            <jsp:param name="startDate" value="${journey.startDate}" />
                            <jsp:param name="endDate" value="${journey.endDate}" />
                            <jsp:param name="description" value="${journey.description}" />
                            <jsp:param name="profilePictureId" value="${journey.user.profilePictureId}" />
                            <jsp:param name="userName" value="${journey.user.username}" />
                            <jsp:param name="firstname" value="${journey.user.firstname}" />
                            <jsp:param name="lastname" value="${journey.user.lastname}"/>
                            <jsp:param name="country" value="${journey.destinationUniversity.city.country}"/>
                            <jsp:param name="university" value="${journey.destinationUniversity.name}"/>
                        </jsp:include>
                    </c:forEach>
                    <c:if test="${empty journeys}">
                        <div class="no-journeys">
                            <p class="no-journeys-message"><spring:message code="journey.no.journeys"/></p>
                        </div>
                    </c:if>
                </div>
            </div>
            <!-- End Journeys List -->
        </div>
    </div>
</div>

<script src="<c:url value='/resources/js/components/list-autocomplete.js'/>"></script>
<script src="<c:url value='/resources/js/journey-cards.js'/>"></script>

<!-- Custom JavaScript for the autocomplete functionality -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Initialize filter toggle
        const filterToggleBtn = document.getElementById('filterToggleBtn');
        const filterSection = document.getElementById('filterSection');
        const filterForm = document.getElementById('journeyFilterForm');

        // Check if there are any filter parameters in the URL
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('destination') || urlParams.has('startDate') ||
            urlParams.has('endDate') || urlParams.has('interest')) {
            // Show filter section if filters are applied
            filterSection.classList.remove('hidden');
        }

        // Toggle filter section visibility
        filterToggleBtn.addEventListener('click', function() {
            filterSection.classList.toggle('hidden');
            // Optional: Animate the toggle button
            this.classList.toggle('active');
        });

        // City Autocomplete
        initAutocomplete('citySearch', 'cityDropdown', 'city', 'citySelectedContainer', false);

        // Interest Autocomplete
        initAutocomplete('interest-search', 'interest-dropdown', 'interest-select', 'interestSelectedContainer', false);

        // Initialize with any pre-selected values
        initializeSelectedValues();

        // Reset button functionality
        const resetFiltersBtn = document.getElementById('resetFiltersBtn');
        if (resetFiltersBtn) {
            resetFiltersBtn.addEventListener('click', function(e) {
                e.preventDefault(); // Prevent default button behavior

                // Clear all form inputs
                const inputs = filterForm.querySelectorAll('input');
                inputs.forEach(input => {
                    input.value = '';
                });

                // Clear all select elements
                const selects = filterForm.querySelectorAll('select');
                selects.forEach(select => {
                    Array.from(select.options).forEach(option => {
                        option.selected = false;
                    });
                    // Select the first empty option if it exists
                    if (select.options.length > 0 && select.options[0].value === '') {
                        select.options[0].selected = true;
                    }
                });

                // Clear all selected tags
                const selectedContainers = filterForm.querySelectorAll('.selected-items-container');
                selectedContainers.forEach(container => {
                    container.innerHTML = '';
                });

                // Navigate to the base journeys URL
                window.location.href = '<c:url value="/journeys"/>';
            });
        }

        // Function to initialize autocomplete
        function initAutocomplete(inputId, dropdownId, selectId, containerid, multiSelect) {
            const input = document.getElementById(inputId);
            const dropdown = document.getElementById(dropdownId);
            const select = document.getElementById(selectId);
            const selectedContainer = document.getElementById(containerid);
            const options = dropdown.querySelectorAll('.autocomplete-item');

            // Show dropdown on input focus
            input.addEventListener('focus', function() {
                dropdown.style.display = 'block';
                filterOptions(this.value);
            });

            // Hide dropdown when clicking outside
            document.addEventListener('click', function(e) {
                if (!input.contains(e.target) && !dropdown.contains(e.target)) {
                    dropdown.style.display = 'none';
                }
            });

            // Filter options as user types
            input.addEventListener('input', function() {
                filterOptions(this.value);
                dropdown.style.display = 'block';
            });

            // Handle option selection
            options.forEach(option => {
                option.addEventListener('click', function() {
                    const value = this.dataset.value;
                    const text = this.textContent.trim();

                    // For single select, clear previous selection
                    if (!multiSelect) {
                        // Clear all options
                        Array.from(select.options).forEach(opt => {
                            opt.selected = false;
                        });

                        // Clear selected container
                        selectedContainer.innerHTML = '';
                    }

                    // Find and select the option
                    Array.from(select.options).forEach(opt => {
                        if (opt.value === value) {
                            opt.selected = true;
                        }
                    });

                    // Update input and selected container
                    input.value = '';

                    // Create selected tag
                    const tag = document.createElement('div');
                    tag.className = 'selected-tag';
                    tag.innerHTML = text;

                    // Add remove button for tag
                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.className = 'tag-remove';
                    removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
                    removeBtn.addEventListener('click', function() {
                        // Deselect the option
                        Array.from(select.options).forEach(opt => {
                            if (opt.value === value) {
                                opt.selected = false;
                            }
                        });

                        // Remove the tag
                        tag.remove();
                    });

                    tag.appendChild(removeBtn);
                    selectedContainer.appendChild(tag);

                    // Hide dropdown
                    dropdown.style.display = 'none';
                });
            });

            // Filter dropdown options based on search text
            function filterOptions(searchText) {
                const filter = searchText.toLowerCase();
                let hasResults = false;

                options.forEach(option => {
                    const text = option.textContent.toLowerCase();
                    if (text.includes(filter)) {
                        option.style.display = '';
                        hasResults = true;
                    } else {
                        option.style.display = 'none';
                    }
                });

                // Show no results message if needed
                let noResultsMsg = dropdown.querySelector('.empty-message');
                if (!hasResults) {
                    if (!noResultsMsg) {
                        noResultsMsg = document.createElement('div');
                        noResultsMsg.className = 'empty-message';
                        noResultsMsg.textContent = 'No results found';
                        dropdown.appendChild(noResultsMsg);
                    }
                    noResultsMsg.style.display = '';
                } else if (noResultsMsg) {
                    noResultsMsg.style.display = 'none';
                }
            }
        }

        // Initialize selected values from URL parameters
        function initializeSelectedValues() {
            // City
            const citySelect = document.getElementById('city');
            const citySelectedContainer = document.getElementById('citySelectedContainer');

            if (citySelect.value) {
                const selectedOption = Array.from(citySelect.options).find(opt => opt.selected);
                if (selectedOption) {
                    const tag = document.createElement('div');
                    tag.className = 'selected-tag';
                    tag.innerHTML = selectedOption.textContent;

                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.className = 'tag-remove';
                    removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
                    removeBtn.addEventListener('click', function() {
                        selectedOption.selected = false;
                        tag.remove();
                    });

                    tag.appendChild(removeBtn);
                    citySelectedContainer.appendChild(tag);
                }
            }

            // Interest
            const interestSelect = document.getElementById('interest-select');
            const interestSelectedContainer = document.getElementById('interestSelectedContainer');

            if (interestSelect.value) {
                const selectedOption = Array.from(interestSelect.options).find(opt => opt.selected);
                if (selectedOption) {
                    const tag = document.createElement('div');
                    tag.className = 'selected-tag';
                    tag.innerHTML = selectedOption.textContent;

                    const removeBtn = document.createElement('button');
                    removeBtn.type = 'button';
                    removeBtn.className = 'tag-remove';
                    removeBtn.innerHTML = '<img src="<c:url value='/resources/icons/x.svg'/>"/>';
                    removeBtn.addEventListener('click', function() {
                        selectedOption.selected = false;
                        tag.remove();
                    });

                    tag.appendChild(removeBtn);
                    interestSelectedContainer.appendChild(tag);
                }
            }
        }
    });
</script>
</body>
</html>
