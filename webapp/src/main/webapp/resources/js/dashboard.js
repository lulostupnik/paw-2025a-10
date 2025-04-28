document.addEventListener("DOMContentLoaded", () => {
    // No need to handle tab switching with JavaScript anymore
    // as we're using server-side navigation with links

    // Handle action buttons
    setupActionButtons()
})

function setupActionButtons() {
    const actionButtons = document.querySelectorAll(".action-button")
    const dropdownTemplate = document.getElementById("action-dropdown-template")

    if (!dropdownTemplate) return

    let activeDropdown = null

    actionButtons.forEach((button) => {
        button.addEventListener("click", function (e) {
            e.stopPropagation()

            // Close any open dropdown
            if (activeDropdown) {
                activeDropdown.remove()
                if (activeDropdown === this.nextElementSibling) {
                    activeDropdown = null
                    return
                }
            }

            // Clone the dropdown template
            const dropdown = dropdownTemplate.cloneNode(true)
            dropdown.id = ""
            dropdown.style.display = "block"

            // Position the dropdown
            const rect = this.getBoundingClientRect()
            dropdown.style.position = "absolute"
            dropdown.style.top = `${rect.bottom}px`
            dropdown.style.left = `${rect.left}px`
            dropdown.style.zIndex = "1000"

            // Add the dropdown to the DOM
            this.parentNode.insertBefore(dropdown, this.nextSibling)

            // Store the active dropdown
            activeDropdown = dropdown

            // Set up the dropdown items
            const itemId = this.getAttribute("data-id")
            setupDropdownItems(dropdown, itemId)
        })
    })

    // Close dropdown when clicking outside
    document.addEventListener("click", () => {
        if (activeDropdown) {
            activeDropdown.remove()
            activeDropdown = null
        }
    })
}

function setupDropdownItems(dropdown, itemId) {
    // Determine which tab we're in
    let editUrl, deleteUrl, manageAttendeesUrl

    if (document.getElementById("journeys-tab") && document.getElementById("journeys-tab").classList.contains("active")) {
        editUrl = `/journeys/edit/${itemId}`
        deleteUrl = `/journeys/delete/${itemId}`
        // Hide manage attendees for journeys
        const manageAttendeesItem = dropdown.querySelector(".manage-attendees-item")
        if (manageAttendeesItem) manageAttendeesItem.style.display = "none"
    } else if (
        document.getElementById("users-tab") &&
        document.getElementById("users-tab").classList.contains("active")
    ) {
        editUrl = `/users/edit/${itemId}`
        deleteUrl = `/users/delete/${itemId}`
        // Hide manage attendees for users
        const manageAttendeesItem = dropdown.querySelector(".manage-attendees-item")
        if (manageAttendeesItem) manageAttendeesItem.style.display = "none"
    } else if (
        document.getElementById("events-tab") &&
        document.getElementById("events-tab").classList.contains("active")
    ) {
        editUrl = `/events/edit/${itemId}`
        deleteUrl = `/events/delete/${itemId}`
        manageAttendeesUrl = `/events/${itemId}/attendees`
    }

    // Set up edit action
    const editItem = dropdown.querySelector(".edit-item")
    if (editItem) {
        editItem.addEventListener("click", () => {
            window.location.href = editUrl
        })
    }

    // Set up delete action
    const deleteItem = dropdown.querySelector(".delete-item")
    if (deleteItem) {
        deleteItem.addEventListener("click", () => {
            if (confirm("Are you sure you want to delete this item?")) {
                window.location.href = deleteUrl
            }
        })
    }

    // Set up manage attendees action
    const manageAttendeesItem = dropdown.querySelector(".manage-attendees-item")
    if (manageAttendeesItem && manageAttendeesUrl) {
        manageAttendeesItem.addEventListener("click", () => {
            window.location.href = manageAttendeesUrl
        })
    }
}
