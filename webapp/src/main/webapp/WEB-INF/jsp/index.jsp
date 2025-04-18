<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="app.name"/> - <spring:message code="landing.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/landing.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<div class="landing-page">
    <!-- Navigation -->
    <header class="landing-header">
        <div class="container">
            <div class="landing-nav">
                <div class="landing-logo">
                    <a href="<c:url value='/'/>">
                        <svg xmlns="http://www.w3.org/2000/svg" class="logo-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <span class="logo-text"><spring:message code="app.name"/></span>
                    </a>
                </div>
                <div class="landing-menu">
                    <a href="<c:url value='/journeys'/>" class="menu-link"><spring:message code="nav.explore"/></a>
                    <a href="<c:url value='/events'/>" class="menu-link"><spring:message code="nav.events"/></a>
                    <a href="<c:url value='/about'/>" class="menu-link"><spring:message code="nav.about"/></a>
                </div>
                <div class="landing-auth">
                    <a href="<c:url value='/login'/>" class="btn-secondary"><spring:message code="auth.login"/></a>
                    <a href="<c:url value='/register'/>" class="btn-primary btn-signup"><spring:message code="auth.register"/></a>
                </div>
            </div>
        </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <div class="hero-content">
                <h1 class="hero-title"><spring:message code="landing.hero.title"/></h1>
                <p class="hero-subtitle"><spring:message code="landing.hero.subtitle"/></p>
                <div class="hero-cta">
                    <a href="<c:url value='/register'/>" class="btn-primary btn-large">
                        <spring:message code="landing.hero.cta"/>
                    </a>
                    <a href="<c:url value='/journeys'/>" class="btn-secondary btn-large">
                        <spring:message code="landing.hero.explore"/>
                    </a>
                </div>
            </div>
            <div class="hero-image">
                <img src="<c:url value='/resources/images/cityscape.jpeg'/>" alt="Students traveling" class="hero-img">
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title"><spring:message code="landing.features.title"/></h2>
                <p class="section-subtitle"><spring:message code="landing.features.subtitle"/></p>
            </div>
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" class="feature-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
                        </svg>
                    </div>
                    <h3 class="feature-title"><spring:message code="landing.feature1.title"/></h3>
                    <p class="feature-description"><spring:message code="landing.feature1.description"/></p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" class="feature-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
                        </svg>
                    </div>
                    <h3 class="feature-title"><spring:message code="landing.feature2.title"/></h3>
                    <p class="feature-description"><spring:message code="landing.feature2.description"/></p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" class="feature-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                    </div>
                    <h3 class="feature-title"><spring:message code="landing.feature3.title"/></h3>
                    <p class="feature-description"><spring:message code="landing.feature3.description"/></p>
                </div>
            </div>
        </div>
    </section>

    <!-- How It Works Section -->
    <section class="how-it-works-section">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title"><spring:message code="landing.how.title"/></h2>
                <p class="section-subtitle"><spring:message code="landing.how.subtitle"/></p>
            </div>
            <div class="steps-container">
                <div class="step-item">
                    <div class="step-number">1</div>
                    <div class="step-content">
                        <h3 class="step-title"><spring:message code="landing.step1.title"/></h3>
                        <p class="step-description"><spring:message code="landing.step1.description"/></p>
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">2</div>
                    <div class="step-content">
                        <h3 class="step-title"><spring:message code="landing.step2.title"/></h3>
                        <p class="step-description"><spring:message code="landing.step2.description"/></p>
                    </div>
                </div>
                <div class="step-item">
                    <div class="step-number">3</div>
                    <div class="step-content">
                        <h3 class="step-title"><spring:message code="landing.step3.title"/></h3>
                        <p class="step-description"><spring:message code="landing.step3.description"/></p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Featured Journeys Section -->
    <section class="featured-section">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title"><spring:message code="landing.featured.events.title" text="Featured Events"/></h2>
                <p class="section-subtitle"><spring:message code="landing.featured.events.subtitle" text="Discover exciting events happening around the world"/></p>
            </div>
            <div class="featured-events">
                <c:forEach items="${recommendedEvents}" var="event">
                    <div class="featured-event-card">
                        <div class="event-image-container">
                            <c:if test="${not empty event.flyerImageId}">
                                <img src="<c:url value="/images/${event.flyerImageId}"/>"
                                    alt="<spring:message code='event.flyer.alt'/>"
                                    class="event-image">
                            </c:if>
                            <c:if test="${empty event.flyerImageId}">
                                <div class="event-image-placeholder">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="placeholder-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>                                
                                </div>
                            </c:if>
                        </div>
                        <div class="event-card-content">
                            <div class="event-card-header">
                                <%--<h3 class="event-card-title">${event.title}</h3>--%>
                                <h3 class="event-card-title">Placeholder title</h3>
                                <p class="event-card-subtitle">${event.eventCity}</p>
                            </div>
                            <p class="event-card-description">${event.description}</p>
                            <div class="event-card-footer">
                                <div class="event-date">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                                    </svg>
                                    <span>${event.date}</span>
                                </div>
                                <div class="event-organizer">
                                    <svg xmlns="http://www.w3.org/2000/svg" class="event-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                    </svg>
                                    <span>${event.user.firstname} ${event.user.lastname}</span>
                                </div>
                                <a href="<c:url value='/events/1'/>" class="btn-text">
                                    <spring:message code="landing.event.view" text="View Details"/>
                                    <svg xmlns="http://www.w3.org/2000/svg" class="btn-icon-right" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3" />
                                    </svg>
                                </a>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            <div class="featured-cta">
                <a href="<c:url value='/events'/>" class="btn-primary btn-large">
                    <spring:message code="landing.featured.events.cta" text="Explore All Events"/>
                </a>
            </div>
        </div>
    </section>

    <!-- Testimonials Section -->
    <section class="testimonials-section">
        <div class="container">
            <div class="section-header">
                <h2 class="section-title"><spring:message code="landing.testimonials.title"/></h2>
                <p class="section-subtitle"><spring:message code="landing.testimonials.subtitle"/></p>
            </div>
            <div class="testimonials-container">
                <div class="testimonial-card">
                    <div class="testimonial-content">
                        <svg xmlns="http://www.w3.org/2000/svg" class="quote-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                        </svg>
                        <p class="testimonial-text"><spring:message code="landing.testimonial1.text"/></p>
                    </div>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">
                            <div class="avatar-placeholder">EM</div>
                        </div>
                        <div class="testimonial-info">
                            <h4 class="testimonial-name"><spring:message code="landing.testimonial1.name"/></h4>
                            <p class="testimonial-role"><spring:message code="landing.testimonial1.role"/></p>
                        </div>
                    </div>
                </div>
                <div class="testimonial-card">
                    <div class="testimonial-content">
                        <svg xmlns="http://www.w3.org/2000/svg" class="quote-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
                        </svg>
                        <p class="testimonial-text"><spring:message code="landing.testimonial2.text"/></p>
                    </div>
                    <div class="testimonial-author">
                        <div class="testimonial-avatar">
                            <div class="avatar-placeholder">RJ</div>
                        </div>
                        <div class="testimonial-info">
                            <h4 class="testimonial-name"><spring:message code="landing.testimonial2.name"/></h4>
                            <p class="testimonial-role"><spring:message code="landing.testimonial2.role"/></p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
        <div class="container">
            <div class="cta-container">
                <div class="cta-content">
                    <h2 class="cta-title"><spring:message code="landing.cta.title"/></h2>
                    <p class="cta-description"><spring:message code="landing.cta.description"/></p>
                </div>
                <div class="cta-buttons">
                    <a href="<c:url value='/register'/>" class="btn-primary btn-cta-signup">
                        <spring:message code="landing.cta.button"/>
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="landing-footer">
        <div class="container">
            <div class="footer-content">
                <div class="footer-logo">
                    <a href="<c:url value='/'/>">
                        <svg xmlns="http://www.w3.org/2000/svg" class="logo-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3.055 11H5a2 2 0 012 2v1a2 2 0 002 2 2 2 0 012 2v2.945M8 3.935V5.5A2.5 2.5 0 0010.5 8h.5a2 2 0 012 2 2 2 0 104 0 2 2 0 012-2h1.064M15 20.488V18a2 2 0 012-2h3.064M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                        </svg>
                        <span class="logo-text"><spring:message code="app.name"/></span>
                    </a>
                    <p class="footer-tagline"><spring:message code="landing.footer.tagline"/></p>
                </div>
                <div class="footer-links">
                    <div class="footer-column">
                        <h3 class="footer-heading"><spring:message code="landing.footer.explore"/></h3>
                        <ul class="footer-menu">
                            <li><a href="<c:url value='/journeys'/>"><spring:message code="nav.journeys"/></a></li>
                            <li><a href="<c:url value='/events'/>"><spring:message code="nav.events"/></a></li>
                            <li><a href="<c:url value='/universities'/>"><spring:message code="nav.universities"/></a></li>
                        </ul>
                    </div>
                    <div class="footer-column">
                        <h3 class="footer-heading"><spring:message code="landing.footer.company"/></h3>
                        <ul class="footer-menu">
                            <li><a href="<c:url value='/about'/>"><spring:message code="nav.about"/></a></li>
                            <li><a href="<c:url value='/contact'/>"><spring:message code="nav.contact"/></a></li>
                            <li><a href="<c:url value='/careers'/>"><spring:message code="nav.careers"/></a></li>
                        </ul>
                    </div>
                    <div class="footer-column">
                        <h3 class="footer-heading"><spring:message code="landing.footer.legal"/></h3>
                        <ul class="footer-menu">
                            <li><a href="<c:url value='/terms'/>"><spring:message code="nav.terms"/></a></li>
                            <li><a href="<c:url value='/privacy'/>"><spring:message code="nav.privacy"/></a></li>
                            <li><a href="<c:url value='/cookies'/>"><spring:message code="nav.cookies"/></a></li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="footer-bottom">
                <p class="copyright">&copy; <spring:message code="app.year"/> <spring:message code="app.name"/>. <spring:message code="landing.footer.copyright"/></p>
                <div class="social-links">
                    <a href="#" class="social-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="social-icon">
                            <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path>
                        </svg>
                    </a>
                    <a href="#" class="social-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="social-icon">
                            <path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"></path>
                        </svg>
                    </a>
                    <a href="#" class="social-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" stroke-linec height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="social-icon">
                            <rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect>
                            <path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path>
                            <line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line>
                        </svg>
                    </a>
                    <a href="#" class="social-link">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="social-icon">
                            <path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"></path>
                            <rect x="2" y="9" width="4" height="12"></rect>
                            <circle cx="4" cy="4" r="2"></circle>
                        </svg>
                    </a>
                </div>
            </div>
        </div>
    </footer>
</div>
</body>
</html>
