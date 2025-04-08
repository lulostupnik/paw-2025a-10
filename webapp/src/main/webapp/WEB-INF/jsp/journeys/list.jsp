<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
    <title><spring:message code="journey.page.title"/></title>
    <!-- Include Tailwind CSS -->
    <script src="https://cdn.tailwindcss.com"></script>
    <script>
        tailwind.config = {
            theme: {
                extend: {}
            }
        };
    </script>
    <!-- Include Preline UI Kit CSS -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.css" />
</head>
<body class="bg-gray-100">

<div class="flex h-full min-h-screen">
    <!-- Include the sidebar component -->
    <jsp:include page="../components/sidebar.jsp" />

    <!-- Main Content -->
    <div class="ml-64 flex-1">
        <div class="max-w-[85rem] px-4 py-10 sm:px-6 lg:px-8 lg:py-14 mx-auto">
            <div class="mx-auto">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-xl text-gray-800 font-bold sm:text-3xl">
                        <spring:message code="journey.list.title"/>
                    </h2>
                    <div class="flex items-center gap-3">
                        <button id="filterToggleBtn" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
                            <svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                            </svg>
                            <spring:message code="journey.filter.toggle"/>
                        </button>
                        <a href="<c:url value="/journeys/create"/>" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
                            <spring:message code="journey.create.button"/>
                        </a>
                    </div>
                </div>

                <!-- Filter Section - Initially Hidden -->
                <div id="filterSection" class="bg-white p-4 rounded-xl border border-gray-200 mb-6 hidden transition-all duration-300">
                    <h3 class="text-lg font-semibold text-gray-800 mb-3">
                        <spring:message code="journey.filter.title"/>
                    </h3>
                    <form:form action="${pageContext.request.contextPath}/journeys/" method="GET"
                               modelAttribute="filterJourneyForm"
                               class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
                        <div>
                            <form:label path="destination" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.destination"/>
                            </form:label>
                            <form:select path="destination" class="py-2.5 px-4 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
                                <option value=""><spring:message code="journey.filter.destination.placeholder"/></option>
                                <c:forEach var="city" items="${cities}">
                                    <option value="${city.name}" ${filterJourneyForm.destination eq city.name ? 'selected' : ''}>${city.name}</option>
                                </c:forEach>
                            </form:select>
                        </div>

                        <div>
                            <label for="startDate" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.startDate"/>
                            </label>
                            <input type="date" id="startDate" name="startDate" class="py-2 px-3 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                   value="<c:out value="${filterJourneyForm.startDate}"/>"/>
                        </div>

                        <div>
                            <label for="endDate" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.endDate"/>
                            </label>
                            <input type="date" id="endDate" name="endDate" class="py-2 px-3 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                   value="<c:out value="${filterJourneyForm.endDate}"/>"/>
                        </div>

                        <!-- Enhanced Interest Filter with Search -->
                        <div>
                            <form:label path="interest" class="block text-sm font-medium text-gray-700 mb-1">
                                <spring:message code="journey.filter.interest"/>
                            </form:label>

                            <!-- Hidden select that will hold the actual form data -->
                            <form:select path="interest" id="interestSelect" style="display: none;">
                                <option value=""><spring:message code="journey.filter.interest.placeholder"/></option>
                                <form:options items="${interests}" itemValue="name" itemLabel="name"/>
                            </form:select>

                            <!-- Custom UI for interest selection -->
                            <div class="relative">
                                <!-- Search input -->
                                <input type="text" id="interestSearch"
                                       class="py-2.5 px-4 block w-full border border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500"
                                       placeholder="<spring:message code="journey.filter.interest.placeholder"/>" />

                                <!-- Dropdown for search results -->
                                <div id="interestDropdown" class="hidden absolute z-10 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-y-auto">
                                    <ul class="py-1 text-sm">
                                        <c:forEach var="interest" items="${interests}">
                                            <li class="interest-option px-4 py-2 hover:bg-gray-100 cursor-pointer" data-value="${interest.name}">
                                                <c:out value="${interest.name}"/>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </div>
                            </div>

                            <!-- Selected interest will appear here -->
                            <div id="selectedInterest" class="mt-2"></div>
                        </div>

                        <div class="md:col-span-2 lg:col-span-4 flex justify-end gap-2">
                            <a href="<c:url value="/journeys"/>" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-red-600 text-white hover:bg-red-700 transition-colors">
                                <svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                                </svg>
                                <spring:message code="journey.filter.reset"/>
                            </a>
                            <button type="submit" class="py-2 px-4 inline-flex justify-center items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-700 text-white hover:bg-blue-800 shadow-md transition-colors">
                                <svg class="w-4 h-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
                                </svg>
                                <spring:message code="journey.filter.button"/>
                            </button>
                        </div>
                    </form:form>
                </div>
                <!-- Journeys List with vertical scrolling -->
                <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-h-[600px] overflow-y-auto pb-6 pr-2">
                    <c:forEach var="journey" items="${journeys}">
                        <!-- Card -->
                        <div class="flex-shrink-0">
                            <div class="rounded-xl bg-white border border-gray-200 hover:border-blue-600 transition-all duration-200 h-full flex flex-col">
                                <!-- Card Content -->
                                <div class="p-4 sm:p-6 flex-grow">
                                    <div class="flex items-center gap-x-4">
                                        <div>
                                            <h3 class="text-lg font-semibold text-gray-800">
                                                <c:out value="${journey.user.firstname} ${journey.user.lastname}" />
                                            </h3>
                                            <p class="text-sm text-gray-500">
                                                <c:out value="${journey.destinationCity}" /> -
                                                <c:out value="${journey.destinationUniversity.name}" />
                                            </p>
                                            <p class="text-sm text-gray-500 mt-1">
                                                <c:out value="${journey.startDate}" /> → <c:out value="${journey.endDate}" />
                                            </p>
                                        </div>
                                    </div>
                                    <p class="mt-3 text-gray-600 line-clamp-3">
                                        <c:out value="${journey.description}" />
                                    </p>
                                </div>

                                <!-- Action Buttons - Fixed at bottom -->
                                <div class="p-4 sm:px-6 sm:pb-6 mt-auto">
                                    <div class="flex gap-2">
                                        <a href="<c:url value="/journeys/${journey.id}"/>"
                                           class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
                                            <spring:message code="journey.view.details"/>
                                        </a>
                                        <a href="<c:url value="/journeys/${journey.id}/reply"/>"
                                           class="py-2 px-3 flex-1 inline-flex justify-center items-center text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
                                            <spring:message code="journey.reply.button"/>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <c:if test="${empty journeys}">
                    <div class="col-span-full text-center py-10">
                        <p class="text-gray-500"><spring:message code="journey.no.journeys"/></p>
                    </div>
                </c:if>
            </div>
            <!-- End Journeys List -->
        </div>
    </div>
</div>
</div>

<!-- Include Preline UI Kit JS -->
<script src="https://cdn.jsdelivr.net/npm/preline/dist/preline.min.js"></script>

<!-- Filter Toggle JavaScript -->
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const filterToggleBtn = document.getElementById('filterToggleBtn');
        const filterSection = document.getElementById('filterSection');
        const filterForm = filterSection.querySelector('form');

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
            this.classList.toggle('bg-gray-50');
            this.classList.toggle('bg-blue-50');
        });

        // Optional: Add event listener for reset button to clear form fields
        const resetButton = filterSection.querySelector('a[href*="/journeys"]');
        if (resetButton) {
            resetButton.addEventListener('click', function(e) {
                // Clear all form fields before navigating
                const inputs = filterForm.querySelectorAll('input');
                inputs.forEach(input => {
                    input.value = '';
                });

                // Let the default navigation happen
                // (The link will take the user to the base journeys URL)
            });
        }

        // Interest Searchable Dropdown
        const interestSelect = document.getElementById('interestSelect');
        const interestSearch = document.getElementById('interestSearch');
        const interestDropdown = document.getElementById('interestDropdown');
        const selectedInterest = document.getElementById('selectedInterest');
        const interestOptions = document.querySelectorAll('.interest-option');

        // Initialize with any pre-selected value
        initializeSelectedInterest();

        // Show dropdown when focusing on search input
        interestSearch.addEventListener('focus', function() {
            interestDropdown.classList.remove('hidden');
            filterOptions(this.value);
        });

        // Hide dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!interestSearch.contains(e.target) && !interestDropdown.contains(e.target)) {
                interestDropdown.classList.add('hidden');
            }
        });

        // Filter options as user types
        interestSearch.addEventListener('input', function() {
            filterOptions(this.value);
            interestDropdown.classList.remove('hidden');
        });

        // Handle option selection
        interestOptions.forEach(option => {
            option.addEventListener('click', function() {
                const value = this.dataset.value;
                const text = this.textContent.trim();

                // Set the value in the hidden select
                setSelectedInterest(value, text);

                // Update the UI
                updateSelectedInterestTag();

                // Clear search input and hide dropdown
                interestSearch.value = '';
                interestDropdown.classList.add('hidden');
            });
        });

        // Filter dropdown options based on search text
        function filterOptions(searchText) {
            const filter = searchText.toLowerCase();
            let hasVisibleOptions = false;

            interestOptions.forEach(option => {
                const text = option.textContent.toLowerCase();
                if (text.includes(filter)) {
                    option.style.display = '';
                    hasVisibleOptions = true;
                } else {
                    option.style.display = 'none';
                }

                // Highlight selected option
                const value = option.dataset.value;
                const isSelected = interestSelect.value === value;

                if (isSelected) {
                    option.classList.add('bg-blue-50');
                } else {
                    option.classList.remove('bg-blue-50');
                }
            });

            // If no options match the search, show a message
            if (!hasVisibleOptions && searchText.length > 0) {
                // Check if "no results" message already exists
                let noResultsEl = interestDropdown.querySelector('.no-results');
                if (!noResultsEl) {
                    noResultsEl = document.createElement('li');
                    noResultsEl.className = 'no-results px-4 py-2 text-gray-500';
                    noResultsEl.textContent = 'No matching interests found';
                    interestDropdown.querySelector('ul').appendChild(noResultsEl);
                }
                noResultsEl.style.display = '';
            } else {
                // Hide "no results" message if it exists
                const noResultsEl = interestDropdown.querySelector('.no-results');
                if (noResultsEl) {
                    noResultsEl.style.display = 'none';
                }
            }
        }

        // Initialize selected interest from the select element
        function initializeSelectedInterest() {
            const selectedOption = interestSelect.options[interestSelect.selectedIndex];
            if (selectedOption && selectedOption.value) {
                // Set the search input value to the selected option text
                interestSearch.value = selectedOption.text;

                // Update the selected interest tag
                updateSelectedInterestTag();
            }
        }

        // Set the selected interest
        function setSelectedInterest(value, text) {
            // Find the option in the select element
            for (let i = 0; i < interestSelect.options.length; i++) {
                if (interestSelect.options[i].value === value) {
                    interestSelect.selectedIndex = i;
                    break;
                }
            }
        }

        // Update the selected interest tag UI
        function updateSelectedInterestTag() {
            // Clear existing tag
            selectedInterest.innerHTML = '';

            // Get the selected option
            const selectedOption = interestSelect.options[interestSelect.selectedIndex];

            // If an option is selected, create a tag
            if (selectedOption && selectedOption.value) {
                const tag = document.createElement('div');
                tag.className = 'inline-flex items-center bg-blue-100 text-blue-800 text-xs font-medium px-2.5 py-1 rounded';

                // Add text
                const text = document.createTextNode(selectedOption.text);
                tag.appendChild(text);

                // Create remove button
                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.className = 'ml-1.5 text-blue-800 hover:text-blue-900';
                removeBtn.setAttribute('aria-label', `Remove ${selectedOption.text}`);

                // Create SVG for the remove button
                const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
                svg.setAttribute('width', '12');
                svg.setAttribute('height', '12');
                svg.setAttribute('fill', 'currentColor');
                svg.setAttribute('viewBox', '0 0 16 16');

                const path = document.createElementNS('http://www.w3.org/2000/svg', 'path');
                path.setAttribute('d', 'M8 8.707l3.646 3.647a.5.5 0 0 0 .708-.708L8.707 8l3.647-3.646a.5.5 0 0 0-.708-.708L8 7.293 4.354 3.646a.5.5 0 1 0-.708.708L7.293 8l-3.647 3.646a.5.5 0 0 0 .708.708L8 8.707z');

                svg.appendChild(path);
                removeBtn.appendChild(svg);

                // Add remove button functionality
                removeBtn.addEventListener('click', function(e) {
                    e.preventDefault(); // Prevent form submission

                    // Reset the select element
                    interestSelect.selectedIndex = 0;

                    // Clear the search input
                    interestSearch.value = '';

                    // Update the UI
                    updateSelectedInterestTag();
                });

                // Add button to tag
                tag.appendChild(removeBtn);

                // Add tag to container
                selectedInterest.appendChild(tag);
            }
        }
    });
</script>
</body>
</html>