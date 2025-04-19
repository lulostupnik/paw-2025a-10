/**
 * Autocomplete Component
 *
 * Enhances select fields with autocomplete functionality
 */
let Autocomplete = (() => {
    /**
     * Initialize autocomplete component
     * @param {Object} options Configuration options
     */
    function init(options = {}) {
        // Default configuration
        const config = {
            selectId: "",
            searchId: "",
            dropdownId: "",
            onSelect: null,
            ...options,
        }

        // Get DOM elements
        const selectField = document.getElementById(config.selectId)
        const searchInput = document.getElementById(config.searchId)
        const dropdown = document.getElementById(config.dropdownId)

        // Validate required elements
        if (!selectField) {
            console.error("Autocomplete: Select field not found:", config.selectId)
            return null
        }
        if (!searchInput) {
            console.error("Autocomplete: Search input not found:", config.searchId)
            return null
        }
        if (!dropdown) {
            console.error("Autocomplete: Dropdown not found:", config.dropdownId)
            return null
        }

        console.log("Autocomplete: All elements found, initializing for", config.selectId)

        // Get dropdown items
        const dropdownItems = dropdown.querySelectorAll(".dropdown-item")
        console.log("Autocomplete: Found", dropdownItems.length, "dropdown items")

        // Initialize with selected value if any
        if (selectField.value) {
            console.log("Autocomplete: Initializing with selected value:", selectField.value)
            const selectedOption = Array.from(selectField.options).find((option) => option.value === selectField.value)
            if (selectedOption) {
                searchInput.value = selectedOption.textContent
            }
        }

        // Show dropdown on focus
        searchInput.addEventListener("focus", function () {
            console.log("Autocomplete: Input focused")
            dropdown.style.display = "block"
            filterDropdownItems(this.value.toLowerCase())
        })

        // Hide dropdown when clicking outside
        document.addEventListener("click", (e) => {
            if (!searchInput.contains(e.target) && !dropdown.contains(e.target)) {
                console.log("Autocomplete: Clicked outside, hiding dropdown")
                dropdown.style.display = "none"
            }
        })

        // Filter items as user types
        searchInput.addEventListener("input", function () {
            console.log("Autocomplete: Input changed to:", this.value)
            dropdown.style.display = "block"
            filterDropdownItems(this.value.toLowerCase())
        })

        // Handle item selection with visual feedback
        dropdownItems.forEach((item) => {
            item.addEventListener("click", function () {
                const value = this.getAttribute("data-value")
                const text = this.textContent.trim()

                console.log("Autocomplete: Item selected:", text, "with value:", value)

                // Update the select field
                selectField.value = value

                // Update the search input
                searchInput.value = text

                // Add highlight effect
                searchInput.classList.add("highlight-selection")
                setTimeout(() => {
                    searchInput.classList.remove("highlight-selection")
                }, 1000)

                // Hide dropdown
                dropdown.style.display = "none"

                // Trigger change event
                const event = new Event("change", { bubbles: true })
                selectField.dispatchEvent(event)

                // Call onSelect callback if provided
                if (typeof config.onSelect === "function") {
                    config.onSelect(value, text)
                }
            })
        })

        /**
         * Filter dropdown items based on search text
         */
        function filterDropdownItems(searchText) {
            let visibleCount = 0

            dropdownItems.forEach((item) => {
                const text = item.textContent.toLowerCase()
                const isVisible = text.includes(searchText)
                item.style.display = isVisible ? "block" : "none"
                if (isVisible) visibleCount++
            })

            console.log("Autocomplete: Filtered items, visible count:", visibleCount)

            // Show "no results" message if needed
            let noResultsMsg = dropdown.querySelector(".no-results")
            if (visibleCount === 0) {
                if (!noResultsMsg) {
                    noResultsMsg = document.createElement("div")
                    noResultsMsg.className = "dropdown-item no-results"
                    noResultsMsg.textContent = "No results found"
                    dropdown.appendChild(noResultsMsg)
                    console.log("Autocomplete: Added 'no results' message")
                }
            } else if (noResultsMsg) {
                noResultsMsg.remove()
                console.log("Autocomplete: Removed 'no results' message")
            }

            return visibleCount
        }

        // Public methods for this instance
        const publicMethods = {
            setValue: (value) => {
                console.log("Autocomplete: Setting value programmatically:", value)
                if (!value) {
                    selectField.value = ""
                    searchInput.value = ""
                    return
                }

                selectField.value = value
                const selectedOption = Array.from(selectField.options).find((option) => option.value === value)
                if (selectedOption) {
                    searchInput.value = selectedOption.textContent
                }
            },

            getValue: () => selectField.value,

            getText: () => searchInput.value,

            clear: () => {
                console.log("Autocomplete: Clearing value")
                selectField.value = ""
                searchInput.value = ""
            },

            showDropdown: () => {
                dropdown.style.display = "block"
                filterDropdownItems(searchInput.value.toLowerCase())
            },

            hideDropdown: () => {
                dropdown.style.display = "none"
            },
        }

        // Store instance methods on the element for future reference
        searchInput._autocomplete = publicMethods

        console.log("Autocomplete: Component initialized successfully for", config.selectId)
        return publicMethods
    }

    // Public API
    return {
        init: init,
    }
})()

// Make available globally
window.Autocomplete = Autocomplete
