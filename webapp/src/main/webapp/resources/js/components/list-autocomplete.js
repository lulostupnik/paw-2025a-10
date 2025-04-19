/**
 * List Autocomplete Component
 *
 * Provides multi-select or single-select functionality with autocomplete
 */
let ListAutocomplete = (() => {
    /**
     * Initialize list autocomplete component
     * @param {Object} options Configuration options
     */
    function init(options = {}) {
        // Default configuration
        const config = {
            selectId: "",
            searchId: "",
            dropdownId: "",
            selectedContainerId: "",
            emptyMessage: "No items selected",
            onSelect: null,
            onRemove: null,
            multiSelect: true, // New parameter to control single/multi select behavior
            apiEndpoint: null, // API endpoint for dynamic suggestions
            minChars: 2, // Minimum characters before triggering search
            debounceTime: 300, // Debounce time for API requests
            ...options,
        }

        console.log("Initializing ListAutocomplete with config:", config)

        // Get DOM elements
        const selectElement = document.getElementById(config.selectId)
        const searchInput = document.getElementById(config.searchId)
        const dropdownContainer = document.getElementById(config.dropdownId)
        const selectedContainer = document.getElementById(config.selectedContainerId)

        // Validate required elements
        if (!selectElement) {
            console.error("ListAutocomplete: Select element not found:", config.selectId)
            return
        }
        if (!searchInput) {
            console.error("ListAutocomplete: Search input not found:", config.searchId)
            return
        }
        if (!dropdownContainer) {
            console.error("ListAutocomplete: Dropdown container not found:", config.dropdownId)
            return
        }
        if (!selectedContainer) {
            console.error("ListAutocomplete: Selected container not found:", config.selectedContainerId)
            return
        }

        // Ensure dropdown has proper styling for positioning and visibility
        dropdownContainer.style.display = "none"
        dropdownContainer.style.position = "absolute"
        dropdownContainer.style.zIndex = "9999"
        dropdownContainer.style.width = "100%"

        // Store selected values
        let selectedValues = []

        // Cache for API results to reduce requests
        const apiCache = {}

        // Debounce function for API requests
        let debounceTimeout = null

        // Initialize with any pre-selected values
        initializeSelectedValues()
        updateSelectedTags()

        // Show dropdown on focus
        searchInput.addEventListener("focus", function () {
            console.log("Search input focused")
            positionDropdown()
            dropdownContainer.style.display = "block"
            filterOptions(this.value)
        })

        // Show dropdown on click (for better mobile experience)
        searchInput.addEventListener("click", function (e) {
            console.log("Search input clicked")
            e.stopPropagation() // Prevent immediate closing
            positionDropdown()
            dropdownContainer.style.display = "block"
            filterOptions(this.value)
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
            }
        })

        // Filter options as user types
        searchInput.addEventListener("input", function () {
            console.log("Search input changed:", this.value)
            dropdownContainer.style.display = "block"
            positionDropdown()

            const searchText = this.value.trim()

            // If API endpoint is provided and we have enough characters, fetch from API
            if (config.apiEndpoint && searchText.length >= config.minChars) {
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
                        fetch(`${config.apiEndpoint}?q=${encodeURIComponent(searchText)}`)
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
                // Use client-side filtering for short queries or when no API is provided
                filterOptions(searchText)
            }
        })

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

            // Check if already selected
            const exists = selectedValues.some((item) => item.value === value)

            if (!exists) {
                // For single select, clear existing selections first
                if (!config.multiSelect && selectedValues.length > 0) {
                    selectedValues = []
                }

                // Add to selected values
                selectedValues.push({ value, text })

                // Update the hidden select
                updateSelectElement()

                // Call onSelect callback if provided
                if (typeof config.onSelect === "function") {
                    config.onSelect(value, text)
                }
            }

            // Update the UI
            updateSelectedTags()
            searchInput.value = ""

            // Hide dropdown after selection
            dropdownContainer.style.display = "none"

            console.log("Selection complete, current values:", selectedValues)
        }

        /**
         * Update dropdown with results from API
         */
        function updateDropdownFromResults(data) {
            console.log("Updating dropdown from results:", data)

            // Clear existing content
            while (dropdownContainer.firstChild) {
                dropdownContainer.removeChild(dropdownContainer.firstChild)
            }

            // If no results, show message
            if (!data || data.length === 0) {
                const noResults = document.createElement("div")
                noResults.className = "autocomplete-item no-results"
                noResults.textContent = "No matching items found"
                dropdownContainer.appendChild(noResults)
                return
            }

            // Add each result to dropdown
            data.forEach((item) => {
                addDropdownItem(
                    typeof item === "object" ? item.id || item.value || item.name : item,
                    typeof item === "object" ? item.name || item.text || item.label : item,
                )
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

        /**
         * Initialize selected values from the select element
         */
        function initializeSelectedValues() {
            const options = selectElement.querySelectorAll("option")

            options.forEach((option) => {
                if (option.selected) {
                    selectedValues.push({
                        value: option.value,
                        text: option.textContent.trim(),
                    })
                }
            })

            console.log("Initialized selected values:", selectedValues)
        }

        /**
         * Update the select element based on selectedValues array
         */
        function updateSelectElement() {
            // For single select, just set the value
            if (!config.multiSelect) {
                if (selectedValues.length > 0) {
                    selectElement.value = selectedValues[0].value
                } else {
                    selectElement.value = ""
                }
                return
            }

            // For multi-select, update all options
            const options = selectElement.querySelectorAll("option")
            const selectedValueIds = selectedValues.map((item) => item.value)

            options.forEach((option) => {
                option.selected = selectedValueIds.includes(option.value)
            })

            // Trigger change event on select element
            const event = new Event("change", { bubbles: true })
            selectElement.dispatchEvent(event)
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

            // Show "no results" message if needed
            if (visibleCount === 0) {
                const msg = document.createElement("div")
                msg.className = "autocomplete-item no-results"
                msg.textContent = "No matching items found"
                dropdownContainer.appendChild(msg)
            }
        }

        /**
         * Update the selected tags UI
         */
        function updateSelectedTags() {
            console.log("Updating selected tags UI")

            // Clear existing tags
            selectedContainer.innerHTML = ""

            if (selectedValues.length === 0) {
                const emptyState = document.createElement("div")
                emptyState.className = "empty-interests"
                emptyState.textContent = config.emptyMessage
                selectedContainer.appendChild(emptyState)
                return
            }

            // Create tags for each selected value
            selectedValues.forEach((item) => {
                // Create tag container
                const tag = document.createElement("div")
                tag.className = "selected-tag"

                // Add text node
                const textNode = document.createTextNode(item.text)
                tag.appendChild(textNode)

                // Create remove button
                const removeBtn = document.createElement("button")
                removeBtn.type = "button"
                removeBtn.className = "tag-remove"
                removeBtn.dataset.value = item.value
                removeBtn.setAttribute("aria-label", `Remove ${item.text}`)

                // Create SVG for the remove button
                const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg")
                svg.setAttribute("width", "12")
                svg.setAttribute("height", "12")
                svg.setAttribute("fill", "currentColor")
                svg.setAttribute("viewBox", "0 0 16 16")

                const path = document.createElementNS("http://www.w3.org/2000/svg", "path")
                path.setAttribute(
                    "d",
                    "M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708z",
                )

                svg.appendChild(path)
                removeBtn.appendChild(svg)

                // Add remove button functionality
                removeBtn.addEventListener("click", function (e) {
                    e.preventDefault()
                    e.stopPropagation()
                    const valueToRemove = this.dataset.value
                    selectedValues = selectedValues.filter((item) => item.value !== valueToRemove)
                    updateSelectElement()
                    updateSelectedTags()
                    searchInput.focus() // Return focus to search input

                    // Call onRemove callback if provided
                    if (typeof config.onRemove === "function") {
                        config.onRemove(valueToRemove)
                    }
                })

                // Add button to tag
                tag.appendChild(removeBtn)

                // Add tag to container
                selectedContainer.appendChild(tag)
            })
        }

        // Public methods for this instance
        const publicMethods = {
            getSelectedValues: () => [...selectedValues],
            getSelectedIds: () => selectedValues.map((item) => item.value),
            addValue: (value, text) => {
                // Check if already exists
                if (selectedValues.some((item) => item.value === value)) {
                    return false
                }

                // For single select, clear existing selections first
                if (!config.multiSelect && selectedValues.length > 0) {
                    selectedValues = []
                }

                // Add to selected values
                selectedValues.push({ value, text })
                updateSelectElement()
                updateSelectedTags()
                return true
            },
            removeValue: (value) => {
                const initialLength = selectedValues.length
                selectedValues = selectedValues.filter((item) => item.value !== value)

                if (initialLength !== selectedValues.length) {
                    updateSelectElement()
                    updateSelectedTags()
                    return true
                }

                return false
            },
            clearAll: () => {
                selectedValues = []
                updateSelectElement()
                updateSelectedTags()
            },
            isSingleSelect: () => !config.multiSelect,
            getSelectedValue: () => (selectedValues.length > 0 ? selectedValues[0].value : null),
            getSelectedText: () => (selectedValues.length > 0 ? selectedValues[0].text : null),
        }

        // Store instance methods on the element for future reference
        searchInput._listAutocomplete = publicMethods

        return publicMethods
    }

    // Public API
    return {
        init: init,
    }
})()

// Make available globally
window.ListAutocomplete = ListAutocomplete
