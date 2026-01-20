package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.Journey;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.enums.SortDirection;
import ar.edu.itba.paw.models.enums.SortFieldJourney;
import ar.edu.itba.paw.models.exceptions.ReportNotFoundException;
import ar.edu.itba.paw.webapp.auth.AccessHelper;
import ar.edu.itba.paw.webapp.dto.JourneyDto;
import ar.edu.itba.paw.webapp.dto.ReportDto;
import ar.edu.itba.paw.webapp.form.CreateJourneyForm;
import ar.edu.itba.paw.webapp.form.CreateReportForm;
import ar.edu.itba.paw.webapp.form.UpdateJourneyForm;
import ar.edu.itba.paw.webapp.form.UpdateReportStatusForm;
import ar.edu.itba.paw.webapp.utils.DateUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Path("reports")
@Component
public class ReportController {

    @Autowired
    ReportService reportService;

    @Autowired
    private AccessHelper accessHelper;

    @Context
    private UriInfo uriInfo;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listReports(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("8") int size
    ) {
        final List<Report> reports = reportService.findAll(
                search,
                new PageParams(page + 1, size)
        ).getContent();

        final List<ReportDto> reportDtos = ReportDto.fromReportCollection(uriInfo, reports);
        return Response.ok(new GenericEntity<>(reportDtos) {}).build();

    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReportById(@PathParam("id") final long id) {
        final Report report = reportService.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        return Response.ok(ReportDto.fromReport(uriInfo, report)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReport(@Valid final CreateReportForm form) {
        final Long userId = accessHelper.getCurrentUserId();

        final Report report = reportService.createReport(form.getReportType(),userId,form.getTargetId(),form.getDescription(),form.getReason());

        return Response.created(UriUtils.getReportUri(uriInfo, report.getId()))
                .entity(ReportDto.fromReport(uriInfo, report))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStatus(@PathParam("id") final long id, @Valid final UpdateReportStatusForm form) {
        final Report report = reportService.updateReportStatus(id,form.getStatus());
        return Response.ok(ReportDto.fromReport(uriInfo, report)).build();
    }


    @DELETE
    @Path("/{id}/") //TODO: xq tiene / al final?
    public Response deleteReport(
            @PathParam("id") final long id
    ) {
        reportService.deleteById(id);
        return Response.noContent().build();
    }





}
