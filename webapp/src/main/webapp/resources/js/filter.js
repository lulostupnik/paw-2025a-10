/**
 * Journey Filters Initialization
 *
 * Initializes and coordinates components for the journey filters
 */
document.addEventListener("DOMContentLoaded", () => {
    console.log("Initializing journey filters components...")

    // Initialize filter toggle functionality
    const filterToggleBtn = document.getElementById("filterToggleBtn")
    const filterSection = document.getElementById("filterSection")

    if (filterToggleBtn && filterSection) {
        console.log("Filter toggle and section elements found")

        // Add this right after the filterSection variable is defined
        if (filterSection) {
            console.log(
                "Initial filter section visibility:",
                filterSection.classList.contains("hidden") ? "hidden" : "visible",
            )
            // Force a check of the CSS applied
            const computedStyle = window.getComputedStyle(filterSection)
            console.log("Filter section computed display:", computedStyle.display)
        }

        // Check if there are any filter parameters in the URL
        const urlParams = new URLSearchParams(window.location.search)
        if (
            (urlParams.has("destination") && urlParams.get("destination")) ||
            (urlParams.has("startDate") && urlParams.get("startDate")) ||
            (urlParams.has("endDate") && urlParams.get("endDate")) ||
            (urlParams.has("interest") && urlParams.get("interest"))
        ) {
            // Show filter section if filters are applied
            console.log("Filter parameters found in URL, showing filter section")
            filterSection.classList.remove("hidden")
        }

        // Toggle filter section visibility
        filterToggleBtn.addEventListener("click", function () {
            console.log("Filter toggle button clicked")
            console.log("Filter section before toggle:", filterSection.classList.contains("hidden") ? "hidden" : "visible")
            filterSection.classList.toggle("hidden")
            console.log("Filter section after toggle:", filterSection.classList.contains("hidden") ? "hidden" : "visible")
            // Optional: Animate the toggle button
            this.classList.toggle("active")
        })
    } else {
        console.warn("Filter toggle button or filter section not found", {
            filterToggleBtn: !!filterToggleBtn,
            filterSection: !!filterSection,
        })
    }

    // Simple autocomplete implementation for city
    const cityDropdown = document.getElementById("cityDropdown")
    const citySearch = document.getElementById("citySearch")
    const citySelect = document.getElementById("city")

    if (cityDropdown && citySearch && citySelect) {
        console.log("City autocomplete elements found")

        // Make sure dropdown items have click handlers
        const cityItems = cityDropdown.querySelectorAll(".autocomplete-item")
        cityItems.forEach((item) => {
            item.addEventListener("click", function () {
                const value = this.dataset.value
                const text = this.textContent.trim()

                // Update the search input
                citySearch.value = text

                // Update the select element
                if (citySelect) {
                    citySelect.value = value
                }

                // Hide dropdown
                cityDropdown.style.display = "none"

                console.log("City selected:", value, text)
            })
        })

        // Show dropdown when clicking on search input
        citySearch.addEventListener("click", () => {
            cityDropdown.style.display = "block"
        })

        // Filter items as user types
        citySearch.addEventListener("input", function () {
            const filter = this.value.toLowerCase()
            cityDropdown.style.display = "block"

            const items = cityDropdown.querySelectorAll(".autocomplete-item")
            let hasMatches = false

            items.forEach((item) => {
                const text = item.textContent.toLowerCase()
                if (text.includes(filter)) {
                    item.style.display = "block"
                    hasMatches = true
                } else {
                    item.style.display = "none"
                }
            })

            // Show no results message if needed
            let noResults = cityDropdown.querySelector(".no-results")
            if (!hasMatches) {
                if (!noResults) {
                    noResults = document.createElement("div")
                    noResults.className = "autocomplete-item no-results"
                    noResults.textContent = "No matching cities found"
                    cityDropdown.appendChild(noResults)
                }
            } else if (noResults) {
                noResults.remove()
            }
        })

        // Close dropdown when clicking outside
        document.addEventListener("click", (e) => {
            if (!citySearch.contains(e.target) && !cityDropdown.contains(e.target)) {
                cityDropdown.style.display = "none"
            }
        })
    } else {
        console.warn("City autocomplete elements not found", {
            cityDropdown: !!cityDropdown,
            citySearch: !!citySearch,
            citySelect: !!citySelect,
        })
    }

    // Simple autocomplete implementation for interest
    const interestDropdown = document.getElementById("interest-dropdown")
    const interestSearch = document.getElementById("interest-search")
    const interestSelect = document.getElementById("interest-select")

    if (interestDropdown && interestSearch && interestSelect) {
        console.log("Interest autocomplete elements found")

        // Make sure dropdown items have click handlers
        const interestItems = interestDropdown.querySelectorAll(".autocomplete-item")
        interestItems.forEach((item) => {
            item.addEventListener("click", function () {
                const value = this.dataset.value
                const text = this.textContent.trim()

                // Update the search input
                interestSearch.value = text

                // Update the select element
                if (interestSelect) {
                    interestSelect.value = value
                }

                // Hide dropdown
                interestDropdown.style.display = "none"

                console.log("Interest selected:", value, text)
            })
        })

        // Show dropdown when clicking on search input
        interestSearch.addEventListener("click", () => {
            interestDropdown.style.display = "block"
        })

        // Filter items as user types
        interestSearch.addEventListener("input", function () {
            const filter = this.value.toLowerCase()
            interestDropdown.style.display = "block"

            const items = interestDropdown.querySelectorAll(".autocomplete-item")
            let hasMatches = false

            items.forEach((item) => {
                const text = item.textContent.toLowerCase()
                if (text.includes(filter)) {
                    item.style.display = "block"
                    hasMatches = true
                } else {
                    item.style.display = "none"
                }
            })

            // Show no results message if needed
            let noResults = interestDropdown.querySelector(".no-results")
            if (!hasMatches) {
                if (!noResults) {
                    noResults = document.createElement("div")
                    noResults.className = "autocomplete-item no-results"
                    noResults.textContent = "No matching interests found"
                    interestDropdown.appendChild(noResults)
                }
            } else if (noResults) {
                noResults.remove()
            }
        })

        // Close dropdown when clicking outside
        document.addEventListener("click", (e) => {
            if (!interestSearch.contains(e.target) && !interestDropdown.contains(e.target)) {
                interestDropdown.style.display = "none"
            }
        })
    } else {
        console.warn("Interest autocomplete elements not found", {
            interestDropdown: !!interestDropdown,
            interestSearch: !!interestSearch,
            interestSelect: !!interestSelect,
        })
    }

    // Initialize date range validation
    const startDateInput = document.getElementById("startDate")
    const endDateInput = document.getElementById("endDate")

    if (startDateInput && endDateInput) {
        console.log("Date range inputs found")

        startDateInput.addEventListener("change", function () {
            console.log("Start date changed:", this.value)
            if (endDateInput.value && new Date(this.value) > new Date(endDateInput.value)) {
                endDateInput.value = this.value
            }
            endDateInput.min = this.value
        })

        // Set initial min value for end date
        if (startDateInput.value) {
            endDateInput.min = startDateInput.value
        }
    } else {
        console.warn("Date range inputs not found", {
            startDateInput: !!startDateInput,
            endDateInput: !!endDateInput,
        })
    }

    // Import ListAutocomplete (assuming it's a global or available through a module)
    // If it's a module, use: import ListAutocomplete from './list-autocomplete';
    // For this example, we'll assume it's a global.  If it's a module, adjust accordingly.
    const ListAutocomplete = window.ListAutocomplete // Example if it's a global

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
            emptyMessage: emptyMessage,
            multiSelect: false, // Important: limit to single selection
            onSelect: (value, text) => {
                console.log("City selected:", value, text)
            },
        })

        // Pre-select city if it's in the URL parameters
        const urlParams = new URLSearchParams(window.location.search)
        if (urlParams.has("destination") && urlParams.get("destination")) {
            const citySelect = document.getElementById("city")
            const selectedOption = citySelect.querySelector(`option[value="${urlParams.get("destination")}"]`)
            if (selectedOption) {
                cityAutocomplete.addValue(selectedOption.value, selectedOption.textContent.trim())
            }
        }
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
            emptyMessage: emptyMessage,
            multiSelect: false, // Important: limit to single selection
            onSelect: (value, text) => {
                console.log("Interest selected:", value, text)
            },
        })

        // Pre-select interest if it's in the URL parameters
        const urlParams = new URLSearchParams(window.location.search)
        if (urlParams.has("interest") && urlParams.get("interest")) {
            const interestSelect = document.getElementById("interest-select")
            const selectedOption = interestSelect.querySelector(`option[value="${urlParams.get("interest")}"]`)
            if (selectedOption) {
                interestAutocomplete.addValue(selectedOption.value, selectedOption.textContent.trim())
            }
        }
    }

    console.log("Journey filters initialization complete")
})
