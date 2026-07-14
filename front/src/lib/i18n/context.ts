import { createContext, useContext } from "react";

export type Locale = "en" | "es";

export type TranslateOptions = {
    defaultValue?: string;
    values?: Record<string | number, string | number>;
};

export type TranslateFn = (key: string, options?: TranslateOptions) => string;

export interface I18nContextValue {
    locale: Locale;
    availableLocales: Locale[];
    setLocale: (locale: Locale) => void;
    t: TranslateFn;
}

// Native display name for each locale. When adding a language, register its
// catalog in the provider and its label here so the selector picks it up
// automatically.
export const localeLabels: Record<Locale, string> = { en: "English", es: "Español" };

export const I18nContext = createContext<I18nContextValue | undefined>(undefined);

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
