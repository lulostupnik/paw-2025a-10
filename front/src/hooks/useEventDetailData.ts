import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { buildEventCreator, buildEventRatings, getEventById, isEventFuture } from "@/lib/api/events";
import { getCityByUrl } from "@/lib/api/journeys";
import type { EventCreator, EventDetail } from "@/types/event";

interface EventDetailParams {
    eventId?: string;
}

const parseIdFromUrl = (url?: string | null): number | null => {
    if (!url) {
        return null;
    }
    const match = url.match(/(\d+)\/?$/);
    return match ? Number(match[1]) : null;
};

// Splits the event detail into a fast core fetch (GET /events/{id}) and slower
// secondary lookups (creator, city, ratings). The page can render its main
// information as soon as the core resolves; each secondary block reports its own
// loading/error state so it can fill in independently.
export const useEventDetailData = ({ eventId }: EventDetailParams = {}) => {
    const queryClient = useQueryClient();

    const eventQuery = useQuery({
        queryKey: ["eventDetail", eventId],
        queryFn: ({ signal }) => {
            if (!eventId) {
                throw new Error("missing-event-id");
            }
            return getEventById(eventId, signal);
        },
        placeholderData: keepPreviousData,
        enabled: Boolean(eventId),
    });

    const event = eventQuery.data ?? null;
    const creatorUrl = event?.links?.creatorUrl ?? null;
    const cityUrl = event?.links?.cityUrl ?? null;

    const creatorQuery = useQuery({
        queryKey: ["eventCreator", eventId, creatorUrl],
        queryFn: ({ signal }) => buildEventCreator(event!, signal, queryClient),
        enabled: Boolean(event && creatorUrl),
        placeholderData: keepPreviousData,
    });

    const cityQuery = useQuery({
        queryKey: ["eventCity", eventId, cityUrl],
        queryFn: ({ signal }) => getCityByUrl(cityUrl, signal, queryClient),
        enabled: Boolean(cityUrl),
        placeholderData: keepPreviousData,
    });

    const ratingsQuery = useQuery({
        queryKey: ["eventRatings", eventId],
        queryFn: ({ signal }) => buildEventRatings(event!.id, signal, queryClient),
        enabled: Boolean(event),
        placeholderData: keepPreviousData,
    });

    // Creator id is derivable from its URL, so ownership-dependent controls can
    // resolve immediately without waiting for the creator profile fetch.
    const fallbackCreator: EventCreator = {
        id: parseIdFromUrl(creatorUrl) ?? 0,
        firstname: "",
        lastname: "",
        username: "",
        profilePictureUrl: null,
        university: null,
        career: null,
    };

    const ratings = ratingsQuery.data ?? [];
    const averageRating = event
        ? typeof event.rating === "number"
            ? event.rating
            : ratings.length > 0
              ? ratings.reduce((sum, rating) => sum + rating.rating, 0) / ratings.length
              : null
        : null;

    const data: EventDetail | null = event
        ? {
              id: event.id,
              title: event.title,
              description: event.description ?? "",
              city: { id: cityQuery.data?.id, name: cityQuery.data?.name ?? "" },
              date: event.date ?? "",
              time: event.time ?? null,
              address: event.address ?? null,
              flyerImageUrl: event.links?.flyerUrl ?? null,
              attendeesCount: typeof event.attendeesCount === "number" ? event.attendeesCount : 0,
              attendeesLimit: event.attendeesLimit ?? null,
              isFuture: isEventFuture(event.date, event.time),
              user: creatorQuery.data ?? fallbackCreator,
              comments: [],
              attendees: [],
              ratings,
              averageRating,
          }
        : null;

    return {
        data,
        isLoading: eventQuery.isLoading,
        isError: eventQuery.isError,
        isFetching: eventQuery.isFetching,
        creatorLoading: creatorQuery.isLoading,
        creatorError: creatorQuery.isError,
        creatorReady: Boolean(creatorQuery.data),
        cityLoading: cityQuery.isLoading,
        ratingsLoading: ratingsQuery.isLoading,
        ratingsError: ratingsQuery.isError,
    };
};
