document.addEventListener("DOMContentLoaded", () => {
    // Add click event listeners to pagination buttons
    const paginationButtons = document.querySelectorAll(".pagination-button.disabled")

    paginationButtons.forEach((button) => {
        button.addEventListener("click", (e) => {
            e.preventDefault()
            return false
        })
    })
})
