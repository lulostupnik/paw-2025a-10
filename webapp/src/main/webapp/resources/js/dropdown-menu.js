// Add this JavaScript to make the action dropdown menu work
document.addEventListener('DOMContentLoaded', function() {
    const actionMenuButton = document.getElementById('actionMenuButton');
    const actionDropdown = document.getElementById('actionDropdown');

    if (actionMenuButton && actionDropdown) {
        // Toggle dropdown when button is clicked
        actionMenuButton.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            // Toggle the dropdown visibility
            if (actionDropdown.style.display === 'block') {
                actionDropdown.style.display = 'none';
            } else {
                actionDropdown.style.display = 'block';
            }
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function(e) {
            if (!actionMenuButton.contains(e.target) && !actionDropdown.contains(e.target)) {
                actionDropdown.style.display = 'none';
            }
        });

        // Close dropdown when pressing Escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                actionDropdown.style.display = 'none';
            }
        });
    }
});