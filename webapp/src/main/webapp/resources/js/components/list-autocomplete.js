/**
 * List Autocomplete Component
 *
 * Provides multi-select functionality with autocomplete
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
            ...options,
        }

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

        // Store selected values
        let selectedValues = []

        // Initialize with any pre-selected values
        initializeSelectedValues()
        updateSelectedTags()

        // Show dropdown on focus
        searchInput.addEventListener("focus", function () {
            dropdownContainer.style.display = "block"
            filterOptions(this.value)
        })

        // Hide dropdown when clicking outside
        document.addEventListener("click", (e) => {
            if (!searchInput.contains(e.target) && !dropdownContainer.contains(e.target)) {
                dropdownContainer.style.display = "none"
            }
        })

        // Filter options as user types
        searchInput.addEventListener("input", function () {
            dropdownContainer.style.display = "block"
            filterOptions(this.value)
        })

        // Handle option selection with visual feedback
        const dropdownOptions = dropdownContainer.querySelectorAll(".autocomplete-item")
        dropdownOptions.forEach((option) => {
            option.addEventListener("click", function () {
                const value = this.dataset.value
                const text = this.textContent.trim()

                // Check if already selected
                const exists = selectedValues.some((item) => item.value === value)

                if (!exists) {
                    // Add to selected values
                    selectedValues.push({ value, text })

                    // Update the hidden select
                    updateSelectElement()

                    // Highlight the selected option
                    this.classList.add("selected")
                    setTimeout(() => {
                        this.classList.remove("selected")
                    }, 500)

                    // Call onSelect callback if provided
                    if (typeof config.onSelect === "function") {
                        config.onSelect(value, text)
                    }
                }

                // Update the UI
                updateSelectedTags()
                searchInput.value = ""
                searchInput.focus() // Keep focus for adding more
            })
        })

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
        }

        /**
         * Update the select element based on selectedValues array
         */
        function updateSelectElement() {
            const options = selectElement.querySelectorAll("option")
            const selectedValueIds = selectedValues.map((item) => item.value)

            options.forEach((option) => {
                option.selected = selectedValueIds.includes(option.value)
            })
        }

        /**
         * Filter dropdown options based on search text
         */
        function filterOptions(searchText) {
            const filter = searchText.toLowerCase()
            let visibleCount = 0

            dropdownOptions.forEach((option) => {
                const text = option.textContent.toLowerCase()
                const isVisible = text.includes(filter)
                option.style.display = isVisible ? "block" : "none"
                if (isVisible) visibleCount++
            })

            // Show "no results" message if needed
            const noResultsMsg = document.getElementById("noInterestResults")
            if (visibleCount === 0) {
                if (!noResultsMsg) {
                    const msg = document.createElement("div")
                    msg.id = "noInterestResults"
                    msg.className = "autocomplete-item no-results"
                    msg.textContent = "No matching items found"
                    dropdownContainer.appendChild(msg)
                }
            } else if (noResultsMsg) {
                noResultsMsg.remove()
            }
        }

        /**
         * Update the selected tags UI
         */
        function updateSelectedTags() {
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
