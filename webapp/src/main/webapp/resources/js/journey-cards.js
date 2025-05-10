document.addEventListener('DOMContentLoaded', function() {
    // Initialize join buttons
    const joinButtons = document.querySelectorAll('.join-button');
    joinButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            toggleJoin(event, this.getAttribute('data-journey-id'), this);
        });
    });
});

function toggleJoin(event, journeyId, button) {
    event.preventDefault(); // Prevent navigation
    button.classList.toggle('active');

    // Send AJAX request to update join status
    fetch('/api/journeys/join', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            journeyId: journeyId,
            joining: button.classList.contains('active')
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log('Join status updated:', data);
        })
        .catch(error => {
            console.error('Error updating join status:', error);
            // Revert UI change on error
            button.classList.toggle('active');
        });
}

// Make this function available globally
window.toggleJoin = toggleJoin;