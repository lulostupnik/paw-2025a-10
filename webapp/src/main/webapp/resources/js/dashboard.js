document.addEventListener("DOMContentLoaded", () => {
    // Handle action buttons
    setupActionButtons()

    // Setup filter buttons
    setupFilterButtons()
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

            // Set the dropdown position
            const rect = button.getBoundingClientRect();
            const scrollTop = window.scrollY || document.documentElement.scrollTop;
            const scrollLeft = window.scrollX || document.documentElement.scrollLeft;

            // Calculate the position to align top-right of dropdown with bottom-right of button
            dropdown.style.top = `${rect.bottom + scrollTop}px`;
            dropdown.style.left = `${rect.left + scrollLeft}px`;

            // Add the dropdown to the DOM
            button.parentNode.insertBefore(dropdown, button.nextSibling)
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
    let isEventsTab = false

    if (document.getElementById("journeys-tab") && document.getElementById("journeys-tab").classList.contains("active")) {
        editUrl = `/journeys/edit/${itemId}`
        deleteUrl = `/journeys/${itemId}/delete`
    } else if (
        document.getElementById("users-tab") &&
        document.getElementById("users-tab").classList.contains("active")
    ) {
        editUrl = `/users/edit/${itemId}`
        deleteUrl = `/users/${itemId}/delete`
    } else if (
        document.getElementById("events-tab") &&
        document.getElementById("events-tab").classList.contains("active")
    ) {
        editUrl = `/events/edit/${itemId}`
        deleteUrl = `/events/${itemId}/delete`
        manageAttendeesUrl = `/events/${itemId}/attendees`
        isEventsTab = true
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

    // Set up manage attendees action (only for events)
    const manageAttendeesItem = dropdown.querySelector(".manage-attendees-item")
    if (manageAttendeesItem) {
        if (!isEventsTab) {
            manageAttendeesItem.style.display = "none"
        } else {
            manageAttendeesItem.addEventListener("click", () => {
                window.location.href = manageAttendeesUrl
            })
        }
    }
}

function setupFilterButtons() {
    // Setup filter buttons
    const filterButtons = document.querySelectorAll(".filter-button")
    filterButtons.forEach((button) => {
        button.addEventListener("click", () => {
            // In a real application, this would open a filter modal or dropdown
            alert("Filter functionality would be implemented here")
        })
    })
}
