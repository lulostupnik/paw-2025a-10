document.addEventListener('DOMContentLoaded', function() {
    // Initialize filter toggle
    const filterToggleBtn = document.getElementById('filterToggleBtn');
    const filterSection = document.getElementById('filterSection');
    const filterForm = document.getElementById('eventFilterForm');
    const filterIcon = filterToggleBtn.querySelector('.filter-icon');
    const closeIcon = filterToggleBtn.querySelector('.close-icon');

    // Check if there are any filter parameters in the URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('destination') || urlParams.has('startDate') ||
        urlParams.has('endDate') || urlParams.has('interests')) {
        // Show filter section if filters are applied
        filterSection.classList.remove('hidden');
        // Update icons
        filterIcon.style.display = 'none';
        closeIcon.style.display = 'inline';
    }

    // initializeSelectedValues()

    // Toggle filter section visibility
    filterToggleBtn.addEventListener('click', function() {
        filterSection.classList.toggle('hidden');

        // Toggle icons
        if (filterSection.classList.contains('hidden')) {
            filterIcon.style.display = 'inline';
            closeIcon.style.display = 'none';
        } else {
            filterIcon.style.display = 'none';
            closeIcon.style.display = 'inline';
        }

        // Optional: Animate the toggle button
        this.classList.toggle('active');
    });

    // Sort dropdown toggle
    const sortToggleBtn = document.getElementById('sortToggleBtn');
    const sortDropdown = document.getElementById('sortDropdown');

    sortToggleBtn.addEventListener('click', function(e) {
        e.stopPropagation();
        sortDropdown.style.display = sortDropdown.style.display === 'block' ? 'none' : 'block';
    });

    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (!sortToggleBtn.contains(e.target) && !sortDropdown.contains(e.target)) {
            sortDropdown.style.display = 'none';
        }
    });

    // Reset button functionality
    const resetFiltersBtn = document.getElementById('resetFiltersBtn');
    if (resetFiltersBtn) {
        resetFiltersBtn.addEventListener('click', function(e) {
            e.preventDefault(); // Prevent default button behavior

            // Build URL with only sort parameters preserved
            let resetUrl = window.eventBaseUrl;
            const sortParam = urlParams.get('sort');
            const directionParam = urlParams.get('direction');
            const searchParam = urlParams.get('search');
            const pageSizeParam = urlParams.get('pageSize');

            // Preserve tab parameters
            const isUpcomingParam = urlParams.get('isUpcoming');
            const attendingParam = urlParams.get('attending');
            const isPastParam = urlParams.get('isPast');

            // Start with question mark if we have parameters
            let hasParam = false;

            if (sortParam) {
                resetUrl += (hasParam ? '&' : '?') + 'sort=' + sortParam;
                hasParam = true;
            }

            if (directionParam) {
                resetUrl += (hasParam ? '&' : '?') + 'direction=' + directionParam;
                hasParam = true;
            }

            if (searchParam) {
                resetUrl += (hasParam ? '&' : '?') + 'search=' + searchParam;
                hasParam = true;
            }

            if (pageSizeParam) {
                resetUrl += (hasParam ? '&' : '?') + 'pageSize=' + pageSizeParam;
                hasParam = true;
            }

            // Add tab parameters if they exist
            if (isUpcomingParam) {
                resetUrl += (hasParam ? '&' : '?') + 'isUpcoming=' + isUpcomingParam;
                hasParam = true;
            }

            if (attendingParam) {
                resetUrl += (hasParam ? '&' : '?') + 'attending=' + attendingParam;
                hasParam = true;
            }

            if (isPastParam) {
                resetUrl += (hasParam ? '&' : '?') + 'isPast=' + isPastParam;
                hasParam = true;
            }

            // Always reset to page 1
            resetUrl += (hasParam ? '&' : '?') + 'page=1';

            // Navigate to the reset URL
            window.location.href = resetUrl;
        });
    }

    // Import ListAutocomplete (assuming it's a global or available through a module)
    // If it's a module, use: import ListAutocomplete from './list-autocomplete';
    // For this example, we'll assume it's a global.  If it's a module, adjust accordingly.
    const ListAutocomplete = window.SingleOptionAutocomplete // Example if it's a global

    // Add this code at the end of the DOMContentLoaded event listener, before the final console.log
    // Initialize ListAutocomplete for city (destination)
    if (document.getElementById("citySearch")) {
        console.log("Initializing city autocomplete with ListAutocomplete")

        const emptyMessage = document.getElementById("i18n-destination-none")
            ? document.getElementById("i18n-destination-none").value
            : "No destination selected"


        const cityAutocomplete = ListAutocomplete.init({
            selectId: "city",
            searchId: "citySearch",
            dropdownId: "cityDropdown",
            selectedContainerId: "citySelectedContainer",
            apiEndpoint: `${apiBaseUrl}cities`,
            selectedValue: eventSelectedCity,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Important: limit to single selection
        })
    }

    // Initialize ListAutocomplete for interest
    if (document.getElementById("interest-search")) {
        console.log("Initializing interest autocomplete with ListAutocomplete")
        const emptyMessage = document.getElementById("i18n-interesets-none")
            ? document.getElementById("i18n-interests-none").value
            : "No interests selected"

        const interestAutocomplete = ListAutocomplete.init({
            selectId: "interest-select",
            searchId: "interest-search",
            dropdownId: "interest-dropdown",
            selectedContainerId: "interestSelectedContainer",
            apiEndpoint: `${apiBaseUrl}interests`,
            selectedValue: eventSelectedInterests,
            minChars: 2,
            debounceTime: 300,
            emptyMessage: emptyMessage,
            multiSelect: false, // Important: limit to single selection
        })
    }
    // function initializeSelectedValues() {
    //     // City
    //     const citySelect = document.getElementById('city');
    //     const citySelectedContainer = document.getElementById('citySelectedContainer');
    //
    //     if (citySelect.value) {
    //         const selectedOption = Array.from(citySelect.options).find(opt => opt.selected);
    //         if (selectedOption) {
    //             const tag = document.createElement('div');
    //             tag.className = 'selected-tag';
    //             tag.innerHTML = selectedOption.textContent;
    //
    //             const removeBtn = document.createElement('button');
    //             removeBtn.type = 'button';
    //             removeBtn.className = 'tag-remove';
    //             removeBtn.innerHTML = `<img src="${apiBaseUrl}cities/>"`;
    //             removeBtn.addEventListener('click', function() {
    //                 selectedOption.selected = false;
    //                 tag.remove();
    //             });
    //
    //             tag.appendChild(removeBtn);
    //             citySelectedContainer.appendChild(tag);
    //         }
    //     }
    //
    //     // Interest
    //     const interestSelect = document.getElementById('interest-select');
    //     const interestSelectedContainer = document.getElementById('interestSelectedContainer');
    //
    //     if (interestSelect.value) {
    //         const selectedOption = Array.from(interestSelect.options).find(opt => opt.selected);
    //         if (selectedOption) {
    //             const tag = document.createElement('div');
    //             tag.className = 'selected-tag';
    //             tag.innerHTML = selectedOption.textContent;
    //
    //             const removeBtn = document.createElement('button');
    //             removeBtn.type = 'button';
    //             removeBtn.className = 'tag-remove';
    //             removeBtn.innerHTML = `<img src="${apiBaseUrl}cities/>"` ;
    //             removeBtn.addEventListener('click', function() {
    //                 selectedOption.selected = false;
    //                 tag.remove();
    //             });
    //
    //             tag.appendChild(removeBtn);
    //             interestSelectedContainer.appendChild(tag);
    //         }
    //     }
    // }
});