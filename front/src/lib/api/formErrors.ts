import { apiFieldErrors } from "@/lib/api/client";

export const mapApiFieldErrors = <TField extends string>(
    error: unknown,
    fieldMap: Partial<Record<string, TField>>
): Partial<Record<TField, string>> => {
    const mappedErrors: Partial<Record<TField, string>> = {};

    for (const [apiField, message] of Object.entries(apiFieldErrors(error))) {
        const formField = fieldMap[apiField];
        if (formField) {
            mappedErrors[formField] = message;
        }
    }

    return mappedErrors;
};
