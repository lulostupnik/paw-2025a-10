/**
 * Initialize pagination functionality
 */
document.addEventListener("DOMContentLoaded", () => {
    // Add click event listeners to pagination buttons
    const paginationButtons = document.querySelectorAll(".pagination-button")

    paginationButtons.forEach((button) => {
        button.addEventListener("click", (e) => {
            if (button.classList.contains("disabled")) {
                e.preventDefault()
                return false
            }
        })
    })
})
