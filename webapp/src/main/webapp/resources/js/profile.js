document.addEventListener('DOMContentLoaded', function() {
    // Get all event tabs and content
    const eventsTabs = document.querySelectorAll('.events-tab');
    const eventsTabContent = document.querySelectorAll('.events-tab-content');

    // Function to set active events sub-tab
    function setActiveEventsTab(tabSelector, contentId) {
        const tab = document.querySelector(tabSelector);
        if (tab) {
            // Remove active class from all tabs and content
            eventsTabs.forEach(t => t.classList.remove('active'));
            eventsTabContent.forEach(c => c.classList.remove('active'));

            // Add active class to selected tab and content
            tab.classList.add('active');

            const content = document.getElementById(contentId);
            if (content) {
                content.classList.add('active');
            }
        }
    }

    // Get URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const eventsTab = urlParams.get('eventsTab');

    // Set active events tab based on URL parameter
    if (eventsTab === 'attending') {
        setActiveEventsTab('.events-tab[data-events-tab="attending"]', 'attending-events');
    } else  if (eventsTab === 'finished') {
        setActiveEventsTab('.events-tab[data-events-tab="finished"]', 'finished-events');
    } else {
        // Default to created events tab
        setActiveEventsTab('.events-tab[data-events-tab="created"]', 'created-events');
    }

    // Add click event listeners to events sub-tabs
    eventsTabs.forEach(tab => {
        tab.addEventListener('click', function(e) {
            e.preventDefault();

            // Get the tab ID from data attribute
            const tabId = this.getAttribute('data-events-tab');

            // Remove active class from all tabs and content
            eventsTabs.forEach(t => t.classList.remove('active'));
            eventsTabContent.forEach(c => c.classList.remove('active'));

            // Add active class to clicked tab
            this.classList.add('active');

            // Show corresponding content
            const content = document.getElementById(tabId + '-events');
            if (content) {
                content.classList.add('active');
            }

            // Update URL with active tab without reloading the page
            const url = new URL(window.location);
            url.searchParams.set('eventsTab', tabId);
            window.history.pushState({}, '', url);
        });
    });

    // Handle browser back/forward navigation
    window.addEventListener('popstate', function() {
        const urlParams = new URLSearchParams(window.location.search);
        const eventsTab = urlParams.get('eventsTab');

        if (eventsTab === 'attending') {
            setActiveEventsTab('.events-tab[data-events-tab="attending"]', 'attending-events');
        } else {
            setActiveEventsTab('.events-tab[data-events-tab="created"]', 'created-events');
        }
    });
});