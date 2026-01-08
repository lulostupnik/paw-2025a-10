import Button from "../../../shared/components/ui/Button";




export default function LandingPage() {
    return (
        <div className="page-shell">
            <section className="hero">
                <div className="hero__content">
                    <p className="eyebrow">GoTogether</p>
                    <h1>Planificá viajes y eventos con la estética clásica del portal JSP.</h1>
                    <p className="lead">
                        Construimos una base visual consistente para que la migración a React sea más simple. Conservamos la paleta
                        de colores, la tipografía y la jerarquía que ya conocen tus usuaries, pero con componentes reutilizables y listos
                        para usar.
                    </p>
                    <div className="hero__actions">
                        <Button size="lg">Explorar eventos</Button>
                        <Button size="lg" variant="secondary">
                            Crear viaje
                        </Button>
                        <Button size="sm" variant="ghost">
                            Ver cómo funciona
                        </Button>
                    </div>
                </div>
            </section>
        </div>
    );
}
