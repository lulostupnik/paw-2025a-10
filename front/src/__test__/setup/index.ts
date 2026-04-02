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
    ]),
    ...createCatalogHandlers("cities", [
        { id: 1, name: "Buenos Aires", country: "Argentina", links: { selfUrl: "http://localhost/webapp/api/cities/1" } },
        { id: 2, name: "Boston", country: "USA", links: { selfUrl: "http://localhost/webapp/api/cities/2" } },
    ]),
    ...createCatalogHandlers("careers", [
        { id: 1, name: "Computer Science", links: { selfUrl: "http://localhost/webapp/api/careers/1" } },
        { id: 2, name: "Engineering", links: { selfUrl: "http://localhost/webapp/api/careers/2" } },
    ]),
    ...createCatalogHandlers("interests", [
        { id: 1, name: "Travel", links: { selfUrl: "http://localhost/webapp/api/interests/1" } },
        { id: 2, name: "Music", links: { selfUrl: "http://localhost/webapp/api/interests/2" } },
        { id: 3, name: "Sports", links: { selfUrl: "http://localhost/webapp/api/interests/3" } },
    ]),
];
