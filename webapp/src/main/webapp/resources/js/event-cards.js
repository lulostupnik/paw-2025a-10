document.addEventListener('DOMContentLoaded', function() {
    // Initialize attend buttons
    const attendButtons = document.querySelectorAll('.attend-button');
    attendButtons.forEach(button => {
        button.addEventListener('click', function(event) {
            event.preventDefault();
            event.stopPropagation();
            toggleAttendance(event, this.getAttribute('data-event-id'), this);
        });
    });
});

function toggleAttendance(event, eventId, button) {
    event.preventDefault(); // Prevent navigation
    button.classList.toggle('active');

    // Send AJAX request to update attendance status
    fetch('/api/events/attend', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            eventId: eventId,
            attending: button.classList.contains('active')
        })
    })
        .then(response => response.json())
        .then(data => {
            console.log('Attendance status updated:', data);
        })
        .catch(error => {
            console.error('Error updating attendance status:', error);
            // Revert UI change on error
            button.classList.toggle('active');
        });
}

// Make this function available globally
window.toggleAttendance = toggleAttendance;