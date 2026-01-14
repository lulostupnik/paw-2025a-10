export interface ProfileEvent {
    id: number;
    title: string;
    description?: string | null;
    date: string;
    city: { name: string };
    flyerImageUrl?: string | null;
    attendeesLimit?: number | null;
    attendeesCount?: number | null;
    isFull?: boolean;
    user: {
        id: number;
        username: string;
        firstname: string;
        lastname: string;
    };
}
