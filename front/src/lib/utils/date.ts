const DATE_ONLY_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

export const parseApiDate = (value: string): Date => {
    if (DATE_ONLY_PATTERN.test(value)) {
        return new Date(Number(value.slice(0, 4)), Number(value.slice(5, 7)) - 1, Number(value.slice(8, 10)));
    }
    return new Date(value);
};

export const getTodayIsoDate = () => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, "0");
    const day = String(now.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
};
