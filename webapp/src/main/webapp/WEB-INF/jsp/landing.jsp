<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CommunityConnect - Create Digital Communities</title>
    <link rel="stylesheet" href="<c:url value='/resources/css/landing.css'/>" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
</head>
<body>
<header>
    <div class="container">
        <div class="logo">
<%--            <img src="logo.png" alt="CommunityConnect Logo">--%>
            <span>GoTogether</span>
        </div>
        <nav>
            <ul>
                <li><a href="#solutions">Solutions</a></li>
                <li><a href="#working-with-us">Working With Us</a></li>
                <li><a href="#destinations">Destinations</a></li>
                <li><a href="#programs">Programs</a></li>
                <li><a href="#resources">Resources</a></li>
                <li><a href="#contact">Contact</a></li>
            </ul>
        </nav>
        <button class="cta-button primary">Get started</button>
    </div>
</header>

<main>
    <section class="hero">
        <div class="container">
            <div class="hero-content">
                <h1>Create digital <span class="highlight">communities</span>,<br>connect <span class="highlight">like-minded</span> explorers</h1>
                <p>When you deliver exceptional digital experiences for community members, you're doing something much, much bigger. You're transforming how they connect, engage, and interact with the world around them.</p>
                <div class="cta-buttons">
                    <button class="cta-button primary">Get started</button>
                    <button class="cta-button secondary">How it works</button>
                </div>
            </div>
            <div class="hero-image">
                <img src="<c:url value="/resources/icons/cityscape.jpeg"/>" alt="Digital community illustration">
            </div>
        </div>
    </section>

    <section class="features">
        <div class="container">
            <h2>Why choose CommunityConnect?</h2>
            <div class="feature-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="icon-connect.png" alt="Connect icon">
                    </div>
                    <h3>Connect Members</h3>
                    <p>Build meaningful connections between community members with our powerful platform.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="icon-engage.png" alt="Engage icon">
                    </div>
                    <h3>Engage Participants</h3>
                    <p>Create engaging experiences that keep your community active and thriving.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <img src="icon-grow.png" alt="Grow icon">
                    </div>
                    <h3>Grow Together</h3>
                    <p>Scale your community with tools designed for sustainable growth and engagement.</p>
                </div>
            </div>
        </div>
    </section>

    <section class="testimonials">
        <div class="container">
            <h2>What our communities say</h2>
            <div class="testimonial-slider" id="testimonialSlider">
                <div class="testimonial">
                    <p>"CommunityConnect transformed how our members interact. We've seen engagement increase by 200% in just three months."</p>
                    <div class="testimonial-author">
                        <img src="avatar1.png" alt="Testimonial author">
                        <div>
                            <h4>Sarah Johnson</h4>
                            <p>Community Manager, TechHub</p>
                        </div>
                    </div>
                </div>
                <div class="testimonial">
                    <p>"The platform made it incredibly easy to connect students for our international exchange program. The experience has been seamless."</p>
                    <div class="testimonial-author">
                        <img src="avatar2.png" alt="Testimonial author">
                        <div>
                            <h4>Michael Chen</h4>
                            <p>Program Director, Global Education</p>
                        </div>
                    </div>
                </div>
                <div class="testimonial">
                    <p>"We've been able to create digital experiences that truly resonate with our residents. The results have exceeded our expectations."</p>
                    <div class="testimonial-author">
                        <img src="avatar3.png" alt="Testimonial author">
                        <div>
                            <h4>Emma Rodriguez</h4>
                            <p>Neighborhood Association Lead</p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="slider-controls">
                <button id="prevBtn" aria-label="Previous testimonial">←</button>
                <div class="slider-dots" id="sliderDots">
                    <span class="dot active"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                </div>
                <button id="nextBtn" aria-label="Next testimonial">→</button>
            </div>
        </div>
    </section>

    <section class="cta-section">
        <div class="container">
            <h2>Ready to transform your community?</h2>
            <p>Join thousands of community leaders who are creating exceptional digital experiences.</p>
            <div class="cta-buttons">
                <button class="cta-button primary">Find Programs</button>
                <button class="cta-button secondary">How It Works</button>
            </div>
        </div>
    </section>
</main>

<footer>
    <div class="container">
        <div class="footer-grid">
            <div class="footer-col">
                <div class="logo">
                    <img src="logo.png" alt="CommunityConnect Logo">
                    <span>CommunityConnect</span>
                </div>
                <p>Creating digital communities that thrive.</p>
                <div class="social-links">
                    <a href="#" aria-label="Facebook"><img src="facebook.png" alt="Facebook"></a>
                    <a href="#" aria-label="Twitter"><img src="twitter.png" alt="Twitter"></a>
                    <a href="#" aria-label="Instagram"><img src="instagram.png" alt="Instagram"></a>
                    <a href="#" aria-label="LinkedIn"><img src="linkedin.png" alt="LinkedIn"></a>
                </div>
            </div>
            <div class="footer-col">
                <h3>Solutions</h3>
                <ul>
                    <li><a href="#">Community Platform</a></li>
                    <li><a href="#">Engagement Tools</a></li>
                    <li><a href="#">Analytics Dashboard</a></li>
                    <li><a href="#">Mobile App</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h3>Programs</h3>
                <ul>
                    <li><a href="#">Student Exchange</a></li>
                    <li><a href="#">Community Events</a></li>
                    <li><a href="#">Digital Workshops</a></li>
                    <li><a href="#">Leadership Training</a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h3>Resources</h3>
                <ul>
                    <li><a href="#">Blog</a></li>
                    <li><a href="#">Case Studies</a></li>
                    <li><a href="#">Webinars</a></li>
                    <li><a href="#">Support Center</a></li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom">
            <p>&copy; 2025 CommunityConnect. All rights reserved.</p>
            <div class="footer-links">
                <a href="#">Privacy Policy</a>
                <a href="#">Terms of Service</a>
                <a href="#">Cookie Policy</a>
            </div>
        </div>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/resources/js/landing.js"></script>
<%-- JSP Integration --%>
<%
    // Example JSP code for dynamic content
    String welcomeMessage = "Welcome to CommunityConnect!";
    String currentYear = java.time.Year.now().toString();
%>

<script>
    // Access JSP variables in JavaScript
    const welcomeMsg = "<%= welcomeMessage %>";
    const year = "<%= currentYear %>";

    // Update copyright year dynamically
    document.addEventListener('DOMContentLoaded', () => {
        console.log(welcomeMsg);
        document.querySelector('.footer-bottom p').innerHTML = `&copy; ${year} CommunityConnect. All rights reserved.`;
    });
</script>
</body>
</html>