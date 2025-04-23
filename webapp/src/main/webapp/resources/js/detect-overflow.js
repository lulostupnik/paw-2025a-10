// Add this script to detect if descriptions have overflow and need ellipsis
document.addEventListener("DOMContentLoaded", () => {
    const descriptions = document.querySelectorAll(".event-card-description")

    descriptions.forEach((description) => {
        // Check if the content overflows
        if (description.scrollHeight > description.clientHeight) {
            description.classList.add("has-overflow")
        }
    })
})