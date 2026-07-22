import { keepPreviousData, useQuery, useQueryClient } from "@tanstack/react-query";
import { apiErrorStatus } from "@/lib/api/client";
import { buildEventCreator, buildEventRatings, getEventById, isEventFuture } from "@/lib/api/events";
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

    const ratingsQuery = useQuery({
        queryKey: ["eventRatings", eventId],
        queryFn: ({ signal }) => buildEventRatings(event!.id, signal, queryClient),
        enabled: Boolean(event),
        placeholderData: keepPreviousData,
    });

    const fallbackCreator: EventCreator = {
        id: parseIdFromUrl(creatorUrl) ?? 0,
        firstname: "",
        lastname: "",
        username: "",
        profilePictureUrl: null,
        university: null,
        career: null,
    };

    const isNotFound = apiErrorStatus(eventQuery.error) === 404;

    const ratingsResult = ratingsQuery.data ?? { ratings: [], total: 0 };
    const ratings = ratingsResult.ratings;
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
              city: { id: parseIdFromUrl(cityUrl) ?? undefined, name: event.cityName ?? "" },
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
              ratingCount: ratingsResult.total,
              averageRating,
          }
        : null;

    return {
        data,
        isLoading: eventQuery.isLoading,
        isError: eventQuery.isError && !isNotFound,
        isNotFound,
        isFetching: eventQuery.isFetching,
        creatorLoading: creatorQuery.isLoading,
        creatorError: creatorQuery.isError,
        creatorReady: Boolean(creatorQuery.data),
        ratingsLoading: ratingsQuery.isLoading,
        ratingsError: ratingsQuery.isError,
    };
};
