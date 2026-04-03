import { authHandlers } from "../mocks/AuthApiMock";
import { eventsHandlers } from "../mocks/EventsApiMock";
import { journeysHandlers } from "../mocks/JourneysApiMock";
import { usersHandlers } from "../mocks/UsersApiMock";
import { reportsHandlers } from "../mocks/ReportsApiMock";
import { createCatalogHandlers } from "../mocks/factories";

export const handlers = [
    ...authHandlers,
    ...eventsHandlers,
    ...journeysHandlers,
    ...usersHandlers,
    ...reportsHandlers,
    ...createCatalogHandlers("universities", [
        { id: 1, name: "MIT", abbreviation: "MIT", links: { cityUrl: "http://localhost/webapp/api/cities/1", selfUrl: "http://localhost/webapp/api/universities/1" } },
        { id: 2, name: "Stanford University", abbreviation: "SU", links: { cityUrl: "http://localhost/webapp/api/cities/2", selfUrl: "http://localhost/webapp/api/universities/2" } },
    ], { list: "application/vnd.gotogether.university-list.v1+json", single: "application/vnd.gotogether.university.v1+json" }),
    ...createCatalogHandlers("cities", [
        { id: 1, name: "Buenos Aires", country: "Argentina", links: { selfUrl: "http://localhost/webapp/api/cities/1" } },
        { id: 2, name: "Boston", country: "USA", links: { selfUrl: "http://localhost/webapp/api/cities/2" } },
    ], { list: "application/vnd.gotogether.city-list.v1+json", single: "application/vnd.gotogether.city.v1+json" }),
    ...createCatalogHandlers("careers", [
        { id: 1, name: "Computer Science", links: { selfUrl: "http://localhost/webapp/api/careers/1" } },
        { id: 2, name: "Engineering", links: { selfUrl: "http://localhost/webapp/api/careers/2" } },
    ], { list: "application/vnd.gotogether.career-list.v1+json", single: "application/vnd.gotogether.career.v1+json" }),
    ...createCatalogHandlers("interests", [
        { id: 1, name: "Travel", links: { selfUrl: "http://localhost/webapp/api/interests/1" } },
        { id: 2, name: "Music", links: { selfUrl: "http://localhost/webapp/api/interests/2" } },
        { id: 3, name: "Sports", links: { selfUrl: "http://localhost/webapp/api/interests/3" } },
    ], { list: "application/vnd.gotogether.interest-list.v1+json", single: "application/vnd.gotogether.interest.v1+json" }),
];
