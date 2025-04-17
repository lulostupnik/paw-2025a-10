<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><spring:message code="app.title" text="GoTogether - Connect Travelers Worldwide"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/landing.css'/>" />
</head>
<body>
<header>
    <div class="container">
        <div class="logo">
            <%--            <img src="logo.png" alt="GoTogether Logo">--%>
            <span><spring:message code="app.name" text="GoTogether"/></span>
        </div>
        <nav>
            <ul>
                <li><a href="#journeys"><spring:message code="nav.journeys" text="Journeys"/></a></li>
                <li><a href="#events"><spring:message code="nav.events" text="Events"/></a></li>
                <li><a href="#destinations"><spring:message code="nav.destinations" text="Destinations"/></a></li>
                <li><a href="#programs"><spring:message code="nav.programs" text="Exchange Programs"/></a></li>
                <li><a href="#resources"><spring:message code="nav.resources" text="Resources"/></a></li>
                <li><a href="#contact"><spring:message code="nav.contact" text="Contact"/></a></li>
            </ul>
        </nav>
        <div class="header-buttons">
            <c:set var="loginUrl"><c:url value="/login"/></c:set>
            <a href="${loginUrl}" class="cta-button secondary"><spring:message code="button.login" text="Log in"/></a>
            <form action="${pageContext.request.contextPath}/register" method="get">
                <button type="submit" class="cta-button primary"><spring:message code="button.getStarted" text="Get started"/></button>
            </form>
        </div>
    </div>
</header>

<main>
    <section class="hero">
        <div class="container">
            <div class="hero-content">
                <h1><spring:message code="hero.title.part1" />
                    <span class="highlight"><spring:message code="hero.title.highlight1"/></span>,<br>
                    <spring:message code="hero.title.part2"/>
                    <span class="highlight"><spring:message code="hero.title.highlight2"/></span>
                </h1>
                <p><spring:message code="hero.description" text="When you join GoTogether, you're doing something much bigger than just finding travel companions. You're transforming how students connect, explore, and experience new cities during their exchange programs."/></p>
                <div class="cta-buttons">
                    <button class="cta-button primary"><spring:message code="button.getStarted" text="Get started"/></button>
                    <button class="cta-button secondary"><spring:message code="button.howItWorks" text="How it works"/></button>
                </div>
            </div>
            <div class="hero-image">
                <img src="<c:url value="/resources/icons/cityscape.jpeg"/>" alt="<spring:message code="hero.image.alt" text="Students exploring a city together"/>">
            </div>
        </div>
    </section>

    <section class="features">
        <div class="container">
            <h2><spring:message code="features.title" text="Why choose GoTogether?"/></h2>
            <div class="feature-grid">
                <div class="feature-card">
                    <div class="feature-icon">
<%--                        <img src="icon-connect.png" alt="<spring:message code="features.connect.icon.alt" text="Connect icon"/>">--%>
                    </div>
                    <h3><spring:message code="features.connect.title" text="Connect Travelers"/></h3>
                    <p><spring:message code="features.connect.description" text="Build meaningful connections with other exchange students traveling to the same destination."/></p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
<%--                        <img src="icon-engage.png" alt="<spring:message code="features.engage.icon.alt" text="Engage icon"/>">--%>
                    </div>
                    <h3><spring:message code="features.engage.title" text="Create Journeys"/></h3>
                    <p><spring:message code="features.engage.description" text="Plan and organize journeys with fellow travelers to explore your new city together."/></p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
<%--                        <img src="icon-grow.png" alt="<spring:message code="features.grow.icon.alt" text="Grow icon"/>">--%>
                    </div>
                    <h3><spring:message code="features.grow.title" text="Join Events"/></h3>
                    <p><spring:message code="features.grow.description" text="Discover and participate in local events specifically designed for exchange students."/></p>
                </div>
            </div>
        </div>
    </section>

    <section class="testimonials">
        <div class="container">
            <h2><spring:message code="testimonials.title" text="What our travelers say"/></h2>
            <div class="testimonial-slider" id="testimonialSlider">
                <div class="testimonial">
                    <p><spring:message code="testimonials.1.quote" text="\"GoTogether transformed my exchange semester in Barcelona. I met amazing friends before even arriving and we explored the city together from day one.\""/></p>
                    <div class="testimonial-author">
<%--                        <img src="avatar1.png" alt="<spring:message code="testimonials.1.author.alt" text="Testimonial author"/>">--%>
                        <div>
                            <h4><spring:message code="testimonials.1.author.name" text="Sarah Johnson"/></h4>
                            <p><spring:message code="testimonials.1.author.role" text="Exchange Student, University of Barcelona"/></p>
                        </div>
                    </div>
                </div>
                <div class="testimonial">
                    <p><spring:message code="testimonials.2.quote" text="\"Finding other students going to Tokyo was incredibly easy with GoTogether. We planned weekend trips together and it made my exchange experience so much better.\""/></p>
                    <div class="testimonial-author">
<%--                        <img src="avatar2.png" alt="<spring:message code="testimonials.2.author.alt" text="Testimonial author"/>">--%>
                        <div>
                            <h4><spring:message code="testimonials.2.author.name" text="Michael Chen"/></h4>
                            <p><spring:message code="testimonials.2.author.role" text="Exchange Student, Waseda University"/></p>
                        </div>
                    </div>
                </div>
                <div class="testimonial">
                    <p><spring:message code="testimonials.3.quote" text="\"The events feature on GoTogether helped me discover local activities I would have never found on my own. I made friends from all over the world!\""/></p>
                    <div class="testimonial-author">
<%--                        <img src="avatar3.png" alt="<spring:message code="testimonials.3.author.alt" text="Testimonial author"/>">--%>
                        <div>
                            <h4><spring:message code="testimonials.3.author.name" text="Emma Rodriguez"/></h4>
                            <p><spring:message code="testimonials.3.author.role" text="Exchange Student, Sciences Po Paris"/></p>
                        </div>
                    </div>
                </div>
            </div>
            <div class="slider-controls">
                <button id="prevBtn" aria-label="<spring:message code="testimonials.controls.previous" text="Previous testimonial"/>">←</button>
                <div class="slider-dots" id="sliderDots">
                    <span class="dot active"></span>
                    <span class="dot"></span>
                    <span class="dot"></span>
                </div>
                <button id="nextBtn" aria-label="<spring:message code="testimonials.controls.next" text="Next testimonial"/>">→</button>
            </div>
        </div>
    </section>

    <section class="cta-section">
        <div class="container">
            <h2><spring:message code="cta.title" text="Ready to transform your exchange experience?"/></h2>
            <p><spring:message code="cta.description" text="Join thousands of exchange students who are connecting and exploring new cities together."/></p>
            <div class="cta-buttons">
                <button class="cta-button primary"><spring:message code="cta.button.findJourneys" text="Find Journeys"/></button>
                <button class="cta-button secondary"><spring:message code="cta.button.howItWorks" text="How It Works"/></button>
            </div>
        </div>
    </section>
</main>

<footer>
    <div class="container">
        <div class="footer-grid">
            <div class="footer-col">
                <div class="logo">
<%--                    <img src="logo.png" alt="<spring:message code="footer.logo.alt" text="GoTogether Logo"/>">--%>
                    <span><spring:message code="app.name" text="GoTogether"/></span>
                </div>
                <p><spring:message code="footer.tagline" text="Connecting travelers, creating memories."/></p>
<%--                <div class="social-links">--%>
<%--                    <a href="#" aria-label="Facebook"><img src="facebook.png" alt="Facebook"></a>--%>
<%--                    <a href="#" aria-label="Twitter"><img src="twitter.png" alt="Twitter"></a>--%>
<%--                    <a href="#" aria-label="Instagram"><img src="instagram.png" alt="Instagram"></a>--%>
<%--                    <a href="#" aria-label="LinkedIn"><img src="linkedin.png" alt="LinkedIn"></a>--%>
<%--                </div>--%>
            </div>
            <div class="footer-col">
                <h3><spring:message code="footer.journeys.title" text="Journeys"/></h3>
                <ul>
                    <li><a href="#"><spring:message code="footer.journeys.create" text="Create Journey"/></a></li>
                    <li><a href="#"><spring:message code="footer.journeys.find" text="Find Journeys"/></a></li>
                    <li><a href="#"><spring:message code="footer.journeys.popular" text="Popular Routes"/></a></li>
                    <li><a href="#"><spring:message code="footer.journeys.tips" text="Travel Tips"/></a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h3><spring:message code="footer.programs.title" text="Exchange Programs"/></h3>
                <ul>
                    <li><a href="#"><spring:message code="footer.programs.erasmus" text="Erasmus+"/></a></li>
                    <li><a href="#"><spring:message code="footer.programs.semester" text="Semester Abroad"/></a></li>
                    <li><a href="#"><spring:message code="footer.programs.summer" text="Summer Schools"/></a></li>
                    <li><a href="#"><spring:message code="footer.programs.language" text="Language Courses"/></a></li>
                </ul>
            </div>
            <div class="footer-col">
                <h3><spring:message code="footer.resources.title" text="Resources"/></h3>
                <ul>
                    <li><a href="#"><spring:message code="footer.resources.blog" text="Travel Blog"/></a></li>
                    <li><a href="#"><spring:message code="footer.resources.stories" text="Student Stories"/></a></li>
                    <li><a href="#"><spring:message code="footer.resources.guides" text="City Guides"/></a></li>
                    <li><a href="#"><spring:message code="footer.resources.support" text="Support Center"/></a></li>
                </ul>
            </div>
        </div>
        <div class="footer-bottom">
            <p><spring:message code="footer.copyright" text="&copy; {0} GoTogether. All rights reserved." arguments="${currentYear}"/></p>
            <div class="footer-links">
                <a href="#"><spring:message code="footer.links.privacy" text="Privacy Policy"/></a>
                <a href="#"><spring:message code="footer.links.terms" text="Terms of Service"/></a>
                <a href="#"><spring:message code="footer.links.cookies" text="Cookie Policy"/></a>
            </div>
        </div>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/resources/js/landing.js"></script>
<%-- JSP Integration --%>
<%
    // Example JSP code for dynamic content
    String welcomeMessage = "Welcome to GoTogether!";
    String currentYear = java.time.Year.now().toString();
    request.setAttribute("currentYear", currentYear);
%>

<script>
    // Access JSP variables in JavaScript
    const welcomeMsg = "<%= welcomeMessage %>";
    const year = "<%= currentYear %>";

    // Update copyright year dynamically
    document.addEventListener('DOMContentLoaded', () => {
        console.log(welcomeMsg);
        // No need to update copyright year here as we're using spring:message with arguments
    });
</script>
</body>
</html>