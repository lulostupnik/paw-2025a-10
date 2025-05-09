/**
 * Single Option Autocomplete Component
 *
 * Provides autocomplete functionality specifically for single-select fields
 * Displays suggestions but allows free-form input without requiring selection
 */
let SingleOptionAutocomplete = (() => {
    /**
     * Initialize single option autocomplete component
     * @param {Object} options Configuration options
     */
    function init(options = {}) {
        // Default configuration
        const config = {
            selectId: "", // ID of the hidden select element that will hold the actual value
            searchId: "", // ID of the visible input field for searching
            dropdownId: "", // ID of the dropdown container
            apiEndpoint: null, // API endpoint for dynamic suggestions
            selectedValue: null, // Pre-selected value
            minChars: 2, // Minimum characters before triggering search
            debounceTime: 150, // Debounce should be between 150-200ms
            placeholder: "", // Placeholder text for the input field
            error: false, // Whether there's an error with this field
            onSelect: null, // Callback when an option is selected
            ...options,
        }

        console.log("Initializing SingleOptionAutocomplete with config:", config)

        // Get DOM elements
        const selectElement = document.getElementById(config.selectId)
        const searchInput = document.getElementById(config.searchId)
        const dropdownContainer = document.getElementById(config.dropdownId)

        // Validate required elements
        if (!selectElement) {
            console.error("SingleOptionAutocomplete: Select element not found:", config.selectId)
            return
        }
        if (!searchInput) {
            console.error("SingleOptionAutocomplete: Search input not found:", config.searchId)
            return
        }
        if (!dropdownContainer) {
            console.error("SingleOptionAutocomplete: Dropdown container not found:", config.dropdownId)
            return
        }

        // Ensure dropdown has proper styling for positioning and visibility
        dropdownContainer.style.display = "none"
        dropdownContainer.style.position = "absolute"
        dropdownContainer.style.zIndex = "9999"
        dropdownContainer.style.width = "100%"

        // Cache for API results to reduce requests
        const apiCache = {}

        // Debounce function for API requests
        let debounceTimeout = null

        // Initialize with any pre-selected value
        initializeSelectedValue()

        // If we have a selected value and an API endpoint, fetch options immediately
        // to ensure we have the proper display text
        if (config.selectedValue && config.apiEndpoint) {
            fetch(`${config.apiEndpoint}`)
                .then((response) => {
                    if (!response.ok) {
                        throw new Error("Network response was not ok")
                    }
                    return response.json()
                })
                .then((data) => {
                    // Cache the results
                    apiCache[""] = data

                    // Update dropdown with results
                    updateDropdownFromResults(data)
                })
                .catch((error) => {
                    console.error("Error fetching initial autocomplete data:", error)
                })
        }

        // Show dropdown on focus
        searchInput.addEventListener("focus", function () {
            console.log("Search input focused")
            positionDropdown()
            dropdownContainer.style.display = "block"
            search(this.value)
        })

        // Show dropdown on click (for better mobile experience)
        searchInput.addEventListener("click", (e) => {
            console.log("Search input clicked")
            e.stopPropagation() // Prevent immediate closing

            positionDropdown()
            dropdownContainer.style.display = "block"
            search(searchInput.value)
        })

        // Position dropdown relative to search input
        function positionDropdown() {
            const inputRect = searchInput.getBoundingClientRect()
            dropdownContainer.style.width = inputRect.width + "px"
            // No need to set top/left if using absolute positioning within a relative container
        }

        // Global click handler to close dropdown
        document.addEventListener("click", (e) => {
            // Only close if click is outside both the search input and dropdown
            if (!searchInput.contains(e.target) && !dropdownContainer.contains(e.target)) {
                console.log("Closing dropdown - clicked outside")
                dropdownContainer.style.display = "none"

                // Update the select element with the current input value
                updateSelectWithInputValue()
            }
        })

        // Filter options as user types
        searchInput.addEventListener("input", function () {
            console.log("Search input changed:", this.value)

            // Update the select element with the current input value
            updateSelectWithInputValue()

            dropdownContainer.style.display = "block"
            positionDropdown()
            search(this.value)
        })

        /**
         * Update the select element with the current input value
         */
        function updateSelectWithInputValue() {
            const inputValue = searchInput.value.trim()

            // Set the value directly to the select element
            selectElement.value = inputValue

            // Trigger change event on select element
            const event = new Event("change", { bubbles: true })
            selectElement.dispatchEvent(event)
        }

        /**
         * Show loading indicator in dropdown
         */
        function showLoadingIndicator() {
            // Clear existing content
            while (dropdownContainer.firstChild) {
                dropdownContainer.removeChild(dropdownContainer.firstChild)
            }

            // Add loading indicator
            const loadingItem = document.createElement("div")
            loadingItem.className = "autocomplete-item loading"
            loadingItem.textContent = "Loading..."
            dropdownContainer.appendChild(loadingItem)
        }

        /**
         * Hide loading indicator
         */
        function hideLoadingIndicator() {
            const loadingItem = dropdownContainer.querySelector(".loading")
            if (loadingItem) {
                loadingItem.remove()
            }
        }

        /**
         * Handle selection of an item
         */
        function handleItemSelection(value, text) {
            console.log("Handling item selection:", value, text)

            // Update the input field with the selected text
            searchInput.value = text

            // Ensure the option exists in the select element
            ensureOptionExists(value, text)

            // Update the select element
            selectElement.value = value

            // Trigger change event on select element
            const event = new Event("change", { bubbles: true })
            selectElement.dispatchEvent(event)

            // Call onSelect callback if provided
            if (typeof config.onSelect === "function") {
                config.onSelect(value, text)
            }

            // Hide dropdown after selection
            dropdownContainer.style.display = "none"
        }

        /**
         * Ensure an option with the given value exists in the select element
         */
        function ensureOptionExists(value, text) {
            // Check if option already exists
            let optionExists = false
            const options = selectElement.querySelectorAll("option")

            for (let i = 0; i < options.length; i++) {
                if (options[i].value === value) {
                    optionExists = true
                    break
                }
            }

            // If option doesn't exist, create it
            if (!optionExists) {
                const newOption = document.createElement("option")
                newOption.value = value
                newOption.textContent = text
                selectElement.appendChild(newOption)
            }
        }

        function updateDropdownFromResults(data) {
            console.log("Updating dropdown from results:", data)

            // Clear existing content
            while (dropdownContainer.firstChild) {
                dropdownContainer.removeChild(dropdownContainer.firstChild)
            }

            const noResultsTxt = document.getElementById("i18n-items-match-none")
                ? document.getElementById("i18n-items-match-none").value
                : "No matching items found"

            // If no results, show message
            if (!data || data.length === 0) {
                const noResults = document.createElement("div")
                noResults.className = "autocomplete-item no-results"
                noResults.textContent = noResultsTxt
                dropdownContainer.appendChild(noResults)
                return
            }

            // Add each result to dropdown and ensure it exists in the select element
            data.forEach((item) => {
                const itemValue = typeof item === "object" ? item.id || item.value || item.name : item
                const itemText = typeof item === "object" ? item.name || item.text || item.label : item

                // Ensure option exists in select element
                ensureOptionExists(itemValue, itemText)

                addDropdownItem(itemValue, itemText)
            })
        }

        /**
         * Add a single item to the dropdown
         */
        function addDropdownItem(value, text) {
            const option = document.createElement("div")
            option.className = "autocomplete-item"
            option.dataset.value = value
            option.textContent = text

            // Add click handler directly
            option.onclick = (e) => {
                e.preventDefault()
                e.stopPropagation()
                console.log("Option clicked:", value, text)
                handleItemSelection(value, text)
                return false
            }

            dropdownContainer.appendChild(option)
        }

        function initializeSelectedValue() {
            // Check if there's a pre-selected value
            if (config.selectedValue) {
                // Try to get the text from the select element
                const options = selectElement.querySelectorAll("option")
                let selectedText = config.selectedValue

                for (let i = 0; i < options.length; i++) {
                    if (options[i].value === config.selectedValue) {
                        selectedText = options[i].textContent.trim()
                        break
                    }
                }

                // Update the input field
                searchInput.value = selectedText

                // Update the select element
                selectElement.value = config.selectedValue
            }

            // If there's no pre-selected value but the select has a value
            else if (selectElement.value) {
                // Get the text from the selected option
                const selectedOption = selectElement.options[selectElement.selectedIndex]
                if (selectedOption) {
                    searchInput.value = selectedOption.textContent.trim()
                }
            }
        }

        /**
         * Filter dropdown options based on search text
         */
        function filterOptions(searchText) {
            console.log("Filtering options for:", searchText)

            const filter = searchText.toLowerCase()

            // Clear existing content
            while (dropdownContainer.firstChild) {
                dropdownContainer.removeChild(dropdownContainer.firstChild)
            }

            // Get all options from select element
            const options = selectElement.querySelectorAll("option")
            let visibleCount = 0

            // Filter and add matching options
            options.forEach((option) => {
                // Skip empty options
                if (!option.value) return

                const text = option.textContent.trim()
                const value = option.value

                // Check if option text matches filter
                if (text.toLowerCase().includes(filter)) {
                    addDropdownItem(value, text)
                    visibleCount++
                }
            })

            const noResultsTxt = document.getElementById("i18n-items-match-none")
                ? document.getElementById("i18n-items-match-none").value
                : "No matching items found"

            // Show "no results" message if needed
            if (visibleCount === 0) {
                const msg = document.createElement("div")
                msg.className = "autocomplete-item no-results"
                msg.textContent = noResultsTxt
                dropdownContainer.appendChild(msg)
            }
        }

        function search(value) {
            const searchText = value.trim()

            // If API endpoint is provided, fetch from API
            if (config.apiEndpoint) {
                // Clear any existing timeout
                if (debounceTimeout) {
                    clearTimeout(debounceTimeout)
                }

                // Set new timeout for debouncing
                debounceTimeout = setTimeout(() => {
                    // Check if we already have cached results
                    if (apiCache[searchText]) {
                        updateDropdownFromResults(apiCache[searchText])
                    } else {
                        // Show loading indicator
                        showLoadingIndicator()

                        // Fetch from API
                        fetch(`${config.apiEndpoint}?search=${encodeURIComponent(searchText)}`)
                            .then((response) => {
                                if (!response.ok) {
                                    throw new Error("Network response was not ok")
                                }
                                return response.json()
                            })
                            .then((data) => {
                                // Cache the results
                                apiCache[searchText] = data

                                // Update dropdown with results
                                updateDropdownFromResults(data)
                            })
                            .catch((error) => {
                                console.error("Error fetching autocomplete data:", error)
                                // Fall back to client-side filtering
                                hideLoadingIndicator()
                                filterOptions(searchText)
                            })
                    }
                }, config.debounceTime)
            } else {
                // Use client-side filtering when no API is provided
                filterOptions(searchText)
            }
        }

        // Public methods for this instance
        const publicMethods = {
            getValue: () => searchInput.value,
            setValue: (value) => {
                searchInput.value = value
                updateSelectWithInputValue()
            },
            clear: () => {
                searchInput.value = ""
                updateSelectWithInputValue()
            },
            refresh: () => {
                // Re-fetch options from API if available
                if (config.apiEndpoint) {
                    fetch(`${config.apiEndpoint}`)
                        .then((response) => {
                            if (!response.ok) {
                                throw new Error("Network response was not ok")
                            }
                            return response.json()
                        })
                        .then((data) => {
                            // Cache the results
                            apiCache[""] = data

                            // Update dropdown with results
                            updateDropdownFromResults(data)
                        })
                        .catch((error) => {
                            console.error("Error fetching autocomplete data:", error)
                        })
                }
            },
        }

        // Store instance methods on the element for future reference
        searchInput._singleOptionAutocomplete = publicMethods

        return publicMethods
    }

    // Public API
    return {
        init: init,
    }
})()

// Make available globally
window.SingleOptionAutocomplete = SingleOptionAutocomplete
