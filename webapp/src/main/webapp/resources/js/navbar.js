document.addEventListener('DOMContentLoaded', function() {
    // Mobile menu toggle
    const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
    const mobileNav = document.getElementById('mobile-nav');
    const mobileNavOverlay = document.getElementById('mobile-nav-overlay');
    const mobileNavClose = document.getElementById('mobile-nav-close');

    // Profile dropdown toggle
    const profileDropdownToggle = document.getElementById('profile-dropdown-toggle');
    const profileDropdownMenu = document.getElementById('profile-dropdown-menu');

    // Toggle mobile menu
    if (mobileMenuToggle) {
        mobileMenuToggle.addEventListener('click', function() {
            mobileNav.classList.add('mobile-nav-open');
            mobileNavOverlay.classList.add('mobile-nav-overlay-active');
            document.body.classList.add('mobile-nav-active');
        });
    }

    // Close mobile menu
    if (mobileNavClose) {
        mobileNavClose.addEventListener('click', function() {
            mobileNav.classList.remove('mobile-nav-open');
            mobileNavOverlay.classList.remove('mobile-nav-overlay-active');
            document.body.classList.remove('mobile-nav-active');
        });
    }

    // Close mobile menu when clicking overlay
    if (mobileNavOverlay) {
        mobileNavOverlay.addEventListener('click', function() {
            mobileNav.classList.remove('mobile-nav-open');
            mobileNavOverlay.classList.remove('mobile-nav-overlay-active');
            document.body.classList.remove('mobile-nav-active');
        });
    }

    // Toggle profile dropdown
    if (profileDropdownToggle && profileDropdownMenu) {
        profileDropdownToggle.addEventListener('click', function(e) {
            e.stopPropagation();
            profileDropdownMenu.classList.toggle('dropdown-menu-active');
        });

        // Close dropdown when clicking outside
        document.addEventListener('click', function() {
            if (profileDropdownMenu.classList.contains('dropdown-menu-active')) {
                profileDropdownMenu.classList.remove('dropdown-menu-active');
            }
        });

        // Prevent dropdown from closing when clicking inside
        profileDropdownMenu.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    }

    // Handle window resize
    window.addEventListener('resize', function() {
        if (window.innerWidth > 1024) {
            if (mobileNav) {
                mobileNav.classList.remove('mobile-nav-open');
            }
            if (mobileNavOverlay) {
                mobileNavOverlay.classList.remove('mobile-nav-overlay-active');
            }
            document.body.classList.remove('mobile-nav-active');
        }
    });
});