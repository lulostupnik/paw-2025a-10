<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<html>
<head>
    <title><spring:message code="app.name"/> - <spring:message code="landing.title"/></title>
    <link rel="stylesheet" href="<c:url value='/resources/css/main.css'/>" />
    <link rel="stylesheet" href="<c:url value='/resources/css/landing.css'/>" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/svg+xml" href="<c:url value='/resources/images/favicon.svg'/>" />
    <link rel="alternate icon" href="<c:url value='/resources/images/favicon.ico'/>" type="image/x-icon" />
</head>
<body>
<div class="landing-page">
    <!-- Navigation -->
    <jsp:include page="components/navbar.jsp"/>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <div class="hero-content">
                <h1 class="hero-title"><spring:message code="landing.hero.title"/></h1>
                <p class="hero-subtitle"><spring:message code="landing.hero.subtitle"/></p>
                <div class="hero-cta">
                    <a href="<c:url value='/register'/>" class="btn-primary">
                        <spring:message code="landing.hero.cta"/>
                    </a>
                    <a href="<c:url value='/journeys'/>" class="btn-explore-journey">
                        <spring:message code="landing.hero.explore"/>
                    </a>
                </div>
            </div>
            <div class="hero-image">
                <img src="<c:url value='/resources/images/cityscape.jpeg'/>" alt="<spring:message code="landing.hero.image.alt"/>" class="hero-img">
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features-section-landing">
        <div class="container">
            <div class="section-header-landing ">
                <h2 class="section-title-landing" ><spring:message code="landing.features.title"/></h2>
                <p class="section-subtitle-landing "><spring:message code="landing.features.subtitle"/></p>
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
    <section class="featured-section ">
        <div class="container">
            <div class="section-header-landing ">
                <h2 class="section-title-landing "><spring:message code="landing.featured.events.title"/></h2>
                <p class="section-subtitle-landing "><spring:message code="landing.featured.events.subtitle"/></p>
            </div>
            <div class="featured-events">
                <c:if test="${empty recommendedEvents}">
                    <div class="empty-state">
                        <div class="empty-icon">
                            <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                            </svg>
                        </div>
                        <p class="empty-message">
                            <spring:message code="dashboard.no.events"/>
                        </p>
                        <a href="<c:url value='/events/create'/>" class="empty-action-btn">
                            <spring:message code="dashboard.create.event"/>
                        </a>
                    </div>
                </c:if>

                <c:if test="${not empty recommendedEvents}">
                    <c:forEach items="${recommendedEvents}" var="event">
                        <c:set var="attend" value="false" />
                        <c:forEach items="${eventsAttended}" var="attendedEvent">
                            <c:if test="${attendedEvent.id == event.id}">
                                <c:set var="attend" value="true" />
                            </c:if>
                        </c:forEach>
                        <jsp:include page="events/event-card.jsp">
                            <jsp:param name="eventId" value="${event.id}" />
                            <jsp:param name="city" value="${event.eventCity.name}" />
                            <jsp:param name="date" value="${event.date}" />
                            <jsp:param name="description" value="${event.description}" />
                            <jsp:param name="flyerImageId" value="${event.flyerImageId}" />
                            <jsp:param name="attend" value="${attend}" />
                            <jsp:param name="firstname" value="${event.user.firstname}" />
                            <jsp:param name="lastname" value="${event.user.lastname}"/>
                            <jsp:param name="title" value="${event.title}"/>
                        </jsp:include>
                    </c:forEach>
                </c:if>
                <div class="featured-cta">
                    <a href="<c:url value='/events'/>" class="btn-primary">
                        <spring:message code="landing.featured.events.cta"/>
                    </a>
                </div>
            </div>
        </div>
    </section>

    <!-- Testimonials Section -->
    <section class="testimonials-section">
        <div class="container">
            <div class="section-header-landing ">
                <h2 class="section-title-landing "><spring:message code="landing.testimonials.title"/></h2>
                <p class="section-subtitle-landing "><spring:message code="landing.testimonials.subtitle"/></p>
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
                    <a href="<c:url value='/register'/>" class="btn-outline-signup">
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
            </div>
            <div class="footer-bottom">
                <p class="copyright">&copy; <spring:message code="app.year"/> <spring:message code="app.name"/>. <spring:message code="landing.footer.copyright"/></p>
            </div>
        </div>
    </footer>
</div>
</body>
</html>
