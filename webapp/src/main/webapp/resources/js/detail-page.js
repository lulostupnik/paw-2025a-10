document.addEventListener('DOMContentLoaded', function() {
    // You could add additional functionality here, such as:
    // - Map initialization if using a real map
    // - AJAX calls for related data
    // - Tabs for different sections of information

    // Example: Toggle sections
    const sectionTitles = document.querySelectorAll('.section-title');

    sectionTitles.forEach(title => {
        title.addEventListener('click', function() {
            const content = this.nextElementSibling;
            const isVisible = content.style.display !== 'none';

            if (isVisible) {
                content.style.display = 'none';
                this.classList.add('collapsed');
            } else {
                content.style.display = 'block';
                this.classList.remove('collapsed');
            }
        });
    });
});