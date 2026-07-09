import { createContext, useContext, useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { setApiLocale } from "@/lib/api/client";
import en from "./messages/en.json";
import es from "./messages/es.json";

export type Locale = "en" | "es";

type TranslateOptions = {
    defaultValue?: string;
    values?: Record<string | number, string | number>;
};

type TranslateFn = (key: string, options?: TranslateOptions) => string;

interface I18nContextValue {
    locale: Locale;
    availableLocales: Locale[];
    setLocale: (locale: Locale) => void;
    t: TranslateFn;
}

const catalogs: Record<Locale, Record<string, string>> = { en, es };
const fallbackLocale: Locale = "en";

const I18nContext = createContext<I18nContextValue | undefined>(undefined);

const isLocale = (value: string | null): value is Locale => value === "en" || value === "es";

const formatTemplate = (template: string, values?: Record<string | number, string | number>) => {
    if (!values) {
        return template;
    }

    return Object.entries(values).reduce((acc, [token, replacement]) => {
        const matcher = new RegExp(`\\{${token}\\}`, "g");
        return acc.replace(matcher, String(replacement));
    }, template);
};

const detectInitialLocale = (preferred?: Locale): Locale => {
    if (preferred && isLocale(preferred)) {
        return preferred;
    }

    if (typeof navigator !== "undefined") {
        const language = navigator.language?.slice(0, 2).toLowerCase();
        if (isLocale(language)) {
            return language;
        }
    }

    return fallbackLocale;
};

interface I18nProviderProps {
    children: ReactNode;
    initialLocale?: Locale;
}

export function I18nProvider({ children, initialLocale }: I18nProviderProps) {
    const [locale, setLocale] = useState<Locale>(() => detectInitialLocale(initialLocale));

    useEffect(() => {
        if (initialLocale && initialLocale !== locale) {
            setLocale(initialLocale);
        }
    }, [initialLocale, locale]);

    useEffect(() => {
        setApiLocale(locale);
    }, [locale]);

    const value = useMemo<I18nContextValue>(() => {
        const dictionary = catalogs[locale];
        const fallbackDictionary = catalogs[fallbackLocale];

        const translate: TranslateFn = (key, options) => {
            const template = dictionary[key] ?? fallbackDictionary[key] ?? options?.defaultValue ?? key;
            return formatTemplate(template, options?.values);
        };

        return {
            locale,
            availableLocales: Object.keys(catalogs) as Locale[],
            setLocale,
            t: translate,
        };
    }, [locale]);

    return <I18nContext.Provider value={value}>{children}</I18nContext.Provider>;
}

export function useI18n() {
    const context = useContext(I18nContext);

    if (!context) {
        throw new Error("useI18n must be used within an I18nProvider");
    }

    return context;
}

export function useTranslate(): TranslateFn {
    return useI18n().t;
}
