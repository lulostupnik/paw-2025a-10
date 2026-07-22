import { useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { setApiLocale } from "@/lib/api/client";
import { I18nContext, type I18nContextValue, type Locale, type TranslateFn } from "./context";
import en from "./messages/en.json";
import es from "./messages/es.json";

const catalogs: Record<Locale, Record<string, string>> = { en, es };
const fallbackLocale: Locale = "en";

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

const LOCALE_STORAGE_KEY = "gotogether.locale";

const detectInitialLocale = (): Locale => {
    if (typeof localStorage !== "undefined") {
        const stored = localStorage.getItem(LOCALE_STORAGE_KEY);
        if (isLocale(stored)) {
            return stored;
        }
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
}

export function I18nProvider({ children }: I18nProviderProps) {
    const [locale, setLocale] = useState<Locale>(detectInitialLocale);

    useEffect(() => {
        setApiLocale(locale);
        document.documentElement.lang = locale;
        if (typeof localStorage !== "undefined") {
            localStorage.setItem(LOCALE_STORAGE_KEY, locale);
        }
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
