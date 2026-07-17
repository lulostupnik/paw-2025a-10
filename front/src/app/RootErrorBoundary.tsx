import { Component, type ReactNode } from "react";
import RootErrorFallback from "@/pages/errors/RootErrorFallback";

interface RootErrorBoundaryProps {
    children: ReactNode;
}

interface RootErrorBoundaryState {
    hasError: boolean;
}

export class RootErrorBoundary extends Component<RootErrorBoundaryProps, RootErrorBoundaryState> {
    state: RootErrorBoundaryState = { hasError: false };

    static getDerivedStateFromError(): RootErrorBoundaryState {
        return { hasError: true };
    }

    componentDidCatch(error: unknown) {
        console.error("Unhandled error outside the router", error);
    }

    render() {
        return this.state.hasError ? <RootErrorFallback /> : this.props.children;
    }
}
