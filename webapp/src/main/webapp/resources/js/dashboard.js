document.addEventListener("DOMContentLoaded", () => {
    // Tab switching functionality
    const tabButtons = document.querySelectorAll(".tab-button")
    const tabContents = document.querySelectorAll(".tab-content")

    // Set default active tab (first one)
    if (tabButtons.length > 0 && tabContents.length > 0) {
        tabButtons[0].classList.add("active")
        tabContents[0].classList.add("active")
    }

    tabButtons.forEach((button) => {
        button.addEventListener("click", function () {
            // Remove active class from all tabs
            tabButtons.forEach((btn) => btn.classList.remove("active"))
            tabContents.forEach((content) => content.classList.remove("active"))

            // Add active class to clicked tab
            this.classList.add("active")
            const tabId = this.getAttribute("data-tab")
            document.getElementById(`${tabId}-tab`).classList.add("active")
        })
    })

    // Action button dropdown functionality
    const actionButtons = document.querySelectorAll(".action-button")

    // Debug - Check if action buttons are found
    console.log("Action buttons found:", actionButtons.length)

    // Instead of using a template, create the dropdown directly
    function createDropdown(itemId) {
        const dropdown = document.createElement('div')
        dropdown.className = 'dropdown-menu'
        dropdown.style.display = 'block'
        dropdown.style.zIndex = '1000'

        dropdown.innerHTML = `
            <ul>
                <li class="dropdown-item edit-item">
                    <i class="edit-icon"></i> Edit
                </li>
                <li class="dropdown-item manage-attendees-item">
                    <i class="attendees-icon"></i> Manage Attendees
                </li>
                <li class="dropdown-item delete-item">
                    <i class="delete-icon"></i> Delete
                </li>
            </ul>
        `

        // Add event listeners
        dropdown.querySelector(".edit-item").addEventListener("click", () => {
            handleEdit(itemId)
        })

        dropdown.querySelector(".manage-attendees-item").addEventListener("click", () => {
            handleManageAttendees(itemId)
        })

        dropdown.querySelector(".delete-item").addEventListener("click", () => {
            handleDelete(itemId)
        })

        return dropdown
    }

    let activeDropdown = null

    actionButtons.forEach((button) => {
        button.addEventListener("click", function (e) {
            e.preventDefault()
            e.stopPropagation()

            console.log("Action button clicked")

            // Close any open dropdown
            if (activeDropdown) {
                activeDropdown.remove()
                activeDropdown = null
            }

            // Get the item ID
            const itemId = this.getAttribute("data-id")
            console.log("Item ID:", itemId)

            // Create new dropdown
            const dropdown = createDropdown(itemId)

            // Position dropdown
            const rect = this.getBoundingClientRect()
            dropdown.style.position = 'absolute'
            dropdown.style.top = `${rect.bottom + window.scrollY}px`
            dropdown.style.left = `${rect.left + window.scrollX - 180 + rect.width}px`

            console.log("Dropdown position:", dropdown.style.top, dropdown.style.left)

            // Add dropdown to the page
            document.body.appendChild(dropdown)
            activeDropdown = dropdown
        })
    })

    // Close dropdown when clicking outside
    document.addEventListener("click", (e) => {
        if (activeDropdown && !e.target.closest('.action-button')) {
            activeDropdown.remove()
            activeDropdown = null
        }
    })

    // Search functionality
    const searchInputs = document.querySelectorAll(".search-input")

    searchInputs.forEach((input) => {
        input.addEventListener("input", function () {
            const searchTerm = this.value.toLowerCase()
            const tabContent = this.closest(".tab-content")
            const tableRows = tabContent.querySelectorAll("tbody tr")

            tableRows.forEach((row) => {
                const text = row.textContent.toLowerCase()
                if (text.includes(searchTerm)) {
                    row.style.display = ""
                } else {
                    row.style.display = "none"
                }
            })
        })
    })

    // Add button functionality
    const addButtons = document.querySelectorAll(".add-button")

    addButtons.forEach((button) => {
        button.addEventListener("click", function () {
            const tabContent = this.closest(".tab-content")
            const tabId = tabContent.id.replace("-tab", "")

            switch (tabId) {
                case "journeys":
                    window.location.href = "journeys/create"
                    break
                case "users":
                    window.location.href = "users/create"
                    break
                case "events":
                    window.location.href = "events/create"
                    break
            }
        })
    })

    // Action handlers
    function handleEdit(id) {
        const activeTab = document.querySelector(".tab-content.active")
        const tabId = activeTab.id.replace("-tab", "")

        window.location.href = `${tabId}/edit/${id}`
    }

    function handleManageAttendees(id) {
        window.location.href = `events/attendees/${id}`
    }

    function handleDelete(id) {
        if (confirm(getTranslatedMessage("admin.confirm.delete"))) {
            const activeTab = document.querySelector(".tab-content.active")
            const tabId = activeTab.id.replace("-tab", "")

            // Send delete request
            fetch(`${tabId}/delete/${id}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "X-CSRF-TOKEN": getCSRFToken(),
                },
            })
                .then((response) => {
                    if (response.ok) {
                        // Reload the page to refresh the data
                        window.location.reload()
                    } else {
                        alert(getTranslatedMessage("admin.error.delete"))
                    }
                })
                .catch((error) => {
                    console.error("Error:", error)
                    alert(getTranslatedMessage("admin.error.general"))
                })
        }
    }

    // Helper function to get CSRF token
    function getCSRFToken() {
        const metaTag = document.querySelector('meta[name="_csrf"]')
        return metaTag ? metaTag.getAttribute("content") : ""
    }

    // Helper function to get translated messages
    function getTranslatedMessage(code) {
        // This is a simplified implementation
        // In a real application, you would load these from a server or include them in the page
        const messages = {
            "admin.confirm.delete": "Are you sure you want to delete this item?",
            "admin.error.delete": "Failed to delete the item.",
            "admin.error.general": "An error occurred. Please try again.",
        }

        return messages[code] || code
    }
})