document.addEventListener('DOMContentLoaded', function() {
    // Testimonial Slider
    const testimonialSlider = document.getElementById('testimonialSlider');
    const testimonials = testimonialSlider.querySelectorAll('.testimonial');
    const dots = document.querySelectorAll('.dot');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    let currentIndex = 0;

    // Hide all testimonials except the first one
    testimonials.forEach((testimonial, index) => {
        if (index !== 0) {
            testimonial.style.display = 'none';
        }
    });

    // Function to show testimonial at specific index
    function showTestimonial(index) {
        // Hide all testimonials
        testimonials.forEach(testimonial => {
            testimonial.style.display = 'none';
        });

        // Remove active class from all dots
        dots.forEach(dot => {
            dot.classList.remove('active');
        });

        // Show the testimonial at the specified index
        testimonials[index].style.display = 'block';

        // Add active class to the corresponding dot
        dots[index].classList.add('active');

        // Update current index
        currentIndex = index;
    }

    // Event listeners for dots
    dots.forEach((dot, index) => {
        dot.addEventListener('click', () => {
            showTestimonial(index);
        });
    });

    // Event listeners for prev/next buttons
    prevBtn.addEventListener('click', () => {
        let newIndex = currentIndex - 1;
        if (newIndex < 0) {
            newIndex = testimonials.length - 1;
        }
        showTestimonial(newIndex);
    });

    nextBtn.addEventListener('click', () => {
        let newIndex = currentIndex + 1;
        if (newIndex >= testimonials.length) {
            newIndex = 0;
        }
        showTestimonial(newIndex);
    });

    // Auto-rotate testimonials every 5 seconds
    setInterval(() => {
        let newIndex = currentIndex + 1;
        if (newIndex >= testimonials.length) {
            newIndex = 0;
        }
        showTestimonial(newIndex);
    }, 5000);

    // Mobile Menu Toggle
    const mobileMenuToggle = document.createElement('button');
    mobileMenuToggle.classList.add('mobile-menu-toggle');
    mobileMenuToggle.innerHTML = '☰';
    mobileMenuToggle.setAttribute('aria-label', 'Toggle navigation menu');

    const header = document.querySelector('header .container');
    const nav = document.querySelector('nav');

    // Only add mobile menu if screen width is below 992px
    if (window.innerWidth < 992) {
        header.insertBefore(mobileMenuToggle, document.querySelector('.cta-button.primary'));

        mobileMenuToggle.addEventListener('click', () => {
            nav.style.display = nav.style.display === 'flex' ? 'none' : 'flex';
        });
    }

    // Smooth scroll for navigation links
    const navLinks = document.querySelectorAll('nav a');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            const targetId = link.getAttribute('href');

            // Only apply smooth scroll for hash links
            if (targetId.startsWith('#')) {
                e.preventDefault();

                const targetElement = document.querySelector(targetId);

                if (targetElement) {
                    window.scrollTo({
                        top: targetElement.offsetTop - 100,
                        behavior: 'smooth'
                    });

                    // Close mobile menu if open
                    if (window.innerWidth < 992) {
                        nav.style.display = 'none';
                    }
                }
            }
        });
    });

    // Animate elements when they come into view
    const animateOnScroll = () => {
        const elements = document.querySelectorAll('.feature-card, .testimonial, .cta-section');

        elements.forEach(element => {
            const elementPosition = element.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;

            if (elementPosition < windowHeight - 100) {
                element.style.opacity = '1';
                element.style.transform = 'translateY(0)';
            }
        });
    };

    // Set initial styles for animation
    const elementsToAnimate = document.querySelectorAll('.feature-card, .testimonial, .cta-section');
    elementsToAnimate.forEach(element => {
        element.style.opacity = '0';
        element.style.transform = 'translateY(20px)';
        element.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    });

    // Run animation on scroll
    window.addEventListener('scroll', animateOnScroll);

    // Run once on page load
    animateOnScroll();

    // JSP integration example - this would be populated by JSP variables
    // This is just a placeholder for demonstration
    function updateDynamicContent() {
        // This function would normally use JSP variables
        // For demo purposes, we're using hardcoded values
        const currentDate = new Date();
        const formattedDate = currentDate.toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        // Create a dynamic element to display the date
        const dateElement = document.createElement('div');
        dateElement.classList.add('current-date');
        dateElement.textContent = `Today is ${formattedDate}`;
        dateElement.style.textAlign = 'center';
        dateElement.style.padding = '0.5rem';
        dateElement.style.backgroundColor = 'rgba(59, 130, 246, 0.1)';
        dateElement.style.borderRadius = 'var(--border-radius)';
        dateElement.style.marginTop = '2rem';

        // Add it to the hero section
        const heroContent = document.querySelector('.hero-content');
        heroContent.appendChild(dateElement);
    }

    // Call the function to update dynamic content
    updateDynamicContent();
});