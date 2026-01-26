import { useNavigate } from "react-router-dom";
import Button from "@/components/ui/Button";
import EventCard from "@/components/cards/EventCard";
import EmptyState from "@/components/EmptyState";
import LoginRequiredModal from "@/components/LoginRequiredModal";
import { useEvents } from "@/hooks/useEvents";
import { useAuthGate } from "@/hooks/useAuthGate";
import { useI18n } from "@/lib/i18n";
import heroImage from "@/assets/cityscape.jpeg";

const GlobeIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path strokeLinecap="round" strokeLinejoin="round" d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
    </svg>
);

const UsersIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path
            strokeLinecap="round"
            strokeLinejoin="round"
            d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"
        />
    </svg>
);

const CalendarIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path strokeLinecap="round" strokeLinejoin="round" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
    </svg>
);

const QuoteIcon = () => (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth={2} aria-hidden="true">
        <path strokeLinecap="round" strokeLinejoin="round" d="M7 8h10M7 12h4m1 8l-4-4H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-3l-4 4z" />
    </svg>
);

const FEATURES = [
    { titleKey: "landing.feature1.title", descriptionKey: "landing.feature1.description", Icon: GlobeIcon },
    { titleKey: "landing.feature2.title", descriptionKey: "landing.feature2.description", Icon: UsersIcon },
    { titleKey: "landing.feature3.title", descriptionKey: "landing.feature3.description", Icon: CalendarIcon },
] as const;

const STEPS = [
    { titleKey: "landing.step1.title", descriptionKey: "landing.step1.description" },
    { titleKey: "landing.step2.title", descriptionKey: "landing.step2.description" },
    { titleKey: "landing.step3.title", descriptionKey: "landing.step3.description" },
] as const;

const TESTIMONIALS = [
    { nameKey: "landing.testimonial1.name", roleKey: "landing.testimonial1.role", textKey: "landing.testimonial1.text" },
    { nameKey: "landing.testimonial2.name", roleKey: "landing.testimonial2.role", textKey: "landing.testimonial2.text" },
] as const;

const getInitials = (value: string) =>
    value
        .split(" ")
        .filter(Boolean)
        .slice(0, 2)
        .map((chunk) => chunk[0])
        .join("")
        .toUpperCase();

export default function LandingPage() {
    const { t } = useI18n();
    const navigate = useNavigate();
    const currentYear = new Date().getFullYear();
    const {
        events: featuredEvents,
        loading: featuredEventsLoading,
        error: featuredEventsError,
    } = useEvents({ size: 3 });
    const gate = useAuthGate();

    const handleCreateEvent = () => {
        gate.runOrPrompt(() => navigate("/events/create"));
    };

    return (
        <div className="page-shell landing-page">
            <section className="hero hero--landing">
                <div className="hero__content">
                    <p className="eyebrow">{t("landing.title")}</p>
                    <h1>{t("landing.hero.title")}</h1>
                    <p className="lead">{t("landing.hero.subtitle")}</p>
                    <div className="hero__actions">
                        <Button size="lg" onClick={() => navigate("/register")}>
                            {t("landing.hero.cta")}
                        </Button>
                        <Button size="lg" variant="secondary" onClick={() => navigate("/journeys")}>
                            {t("landing.hero.explore")}
                        </Button>
                    </div>
                </div>
                <div className="hero__media">
                    <img src={heroImage} alt={t("landing.hero.image.alt")} />
                </div>
            </section>

            <section className="section">
                <div className="section__header">
                    <h2>{t("landing.features.title")}</h2>
                    <p className="section__subtitle">{t("landing.features.subtitle")}</p>
                </div>
                <div className="feature-grid">
                    {FEATURES.map(({ titleKey, descriptionKey, Icon }) => (
                        <article key={titleKey} className="feature-card">
                            <span className="feature-card__icon">
                                <Icon />
                            </span>
                            <h3>{t(titleKey)}</h3>
                            <p>{t(descriptionKey)}</p>
                        </article>
                    ))}
                </div>
            </section>

            <section className="section section--muted">
                <div className="section__header">
                    <h2>{t("landing.how.title")}</h2>
                    <p className="section__subtitle">{t("landing.how.subtitle")}</p>
                </div>
                <div className="steps-grid">
                    {STEPS.map((step, index) => (
                        <article key={step.titleKey} className="step-card">
                            <div className="step-card__number">0{index + 1}</div>
                            <h3>{t(step.titleKey)}</h3>
                            <p>{t(step.descriptionKey)}</p>
                        </article>
                    ))}
                </div>
            </section>

            <section className="section">
                <div className="section__header section__header--split">
                    <div>
                        <h2>{t("landing.featured.events.title")}</h2>
                        <p className="section__subtitle">{t("landing.featured.events.subtitle")}</p>
                    </div>
                    <Button variant="outline" onClick={() => navigate("/events")} size="sm">
                        {t("landing.featured.events.cta")}
                    </Button>
                </div>
                <div className="section__panel">
                    {featuredEventsLoading && <p className="section__helper">{t("landing.featured.events.loading")}</p>}
                    {!featuredEventsLoading && featuredEventsError && (
                        <EmptyState
                            title={t("landing.featured.events.error")}
                            action={
                                <Button variant="primary" size="sm" onClick={handleCreateEvent}>
                                    {t("events.cta.button")}
                                </Button>
                            }
                        />
                    )}
                    {!featuredEventsLoading && !featuredEventsError && featuredEvents.content.length === 0 && (
                        <EmptyState
                            title={t("events.list.empty")}
                            description={t("events.list.empty.description") || undefined}
                            action={
                                <Button variant="primary" size="sm" onClick={handleCreateEvent}>
                                    {t("events.cta.button")}
                                </Button>
                            }
                        />
                    )}
                    {!featuredEventsError && featuredEvents.content.length > 0 && (
                        <div className="listing-grid listing-grid--compact">
                            {featuredEvents.content.map((event) => (
                                <EventCard
                                    key={event.id}
                                    event={event}
                                    actionSlot={
                                        <Button variant="ghost" size="sm" onClick={() => navigate(`/events/${event.id}`)}>
                                            {t("landing.event.view")}
                                        </Button>
                                    }
                                />
                            ))}
                        </div>
                    )}
                </div>
            </section>

            <section className="section">
                <div className="section__header">
                    <h2>{t("landing.testimonials.title")}</h2>
                    <p className="section__subtitle">{t("landing.testimonials.subtitle")}</p>
                </div>
                <div className="testimonial-grid">
                    {TESTIMONIALS.map((testimonial) => {
                        const name = t(testimonial.nameKey);
                        return (
                            <article key={testimonial.nameKey} className="testimonial card">
                                <div className="testimonial__quote">
                                    <span className="testimonial__icon">
                                        <QuoteIcon />
                                    </span>
                                    <p>{t(testimonial.textKey)}</p>
                                </div>
                                <div className="testimonial__author">
                                    <div className="avatar" aria-hidden="true">
                                        {getInitials(name)}
                                    </div>
                                    <div>
                                        <p className="testimonial__name">{name}</p>
                                        <p className="testimonial__role">{t(testimonial.roleKey)}</p>
                                    </div>
                                </div>
                            </article>
                        );
                    })}
                </div>
            </section>

            <section className="section">
                <div className="cta-panel card">
                    <div>
                        <h2>{t("landing.cta.title")}</h2>
                        <p>{t("landing.cta.description")}</p>
                    </div>
                    <Button size="lg" onClick={() => navigate("/register")}>
                        {t("landing.cta.button")}
                    </Button>
                </div>
            </section>

            <footer className="landing-footer">
                <div className="landing-footer__brand">
                    <span className="landing-footer__logo">{t("app.name")}</span>
                    <p>{t("landing.footer.tagline")}</p>
                </div>
                <p className="landing-footer__copy">
                    &copy; {currentYear} {t("app.name")}. {t("landing.footer.copyright")}
                </p>
            </footer>
            <LoginRequiredModal open={gate.open} onClose={gate.close} nextPath="/events/create" />
        </div>
    );
}
