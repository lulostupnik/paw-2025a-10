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
    const dropdownTemplate = document.getElementById("action-dropdown-template")
    let activeDropdown = null

    actionButtons.forEach((button) => {
        button.addEventListener("click", function (e) {
            e.stopPropagation()

            // Close any open dropdown
            if (activeDropdown) {
                activeDropdown.remove()
                activeDropdown = null
            }

            // Create new dropdown
            const dropdown = dropdownTemplate.cloneNode(true)
            dropdown.id = ""
            dropdown.style.display = "block"

            // Position dropdown
            const rect = this.getBoundingClientRect()
            dropdown.style.top = `${rect.bottom + window.scrollY}px`
            dropdown.style.left = `${rect.left + window.scrollX - 180 + rect.width}px`

            // Get the item ID
            const itemId = this.getAttribute("data-id")

            // Add event listeners to dropdown items
            const editItem = dropdown.querySelector(".edit-item")
            if (editItem) {
                editItem.addEventListener("click", () => {
                    handleEdit(itemId)
                })
            }

            const manageAttendeesItem = dropdown.querySelector(".manage-attendees-item")
            if (manageAttendeesItem) {
                manageAttendeesItem.addEventListener("click", () => {
                    handleManageAttendees(itemId)
                })
            }

            const deleteItem = dropdown.querySelector(".delete-item")
            if (deleteItem) {
                deleteItem.addEventListener("click", () => {
                    handleDelete(itemId)
                })
            }

            // Add dropdown to the page
            document.body.appendChild(dropdown)
            activeDropdown = dropdown
        })
    })

    // Close dropdown when clicking outside
    document.addEventListener("click", () => {
        if (activeDropdown) {
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
