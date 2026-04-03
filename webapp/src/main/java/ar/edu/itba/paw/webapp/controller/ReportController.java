package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.services.ReportService;
import ar.edu.itba.paw.models.Page;
import ar.edu.itba.paw.models.PageParams;
import ar.edu.itba.paw.models.Report;
import ar.edu.itba.paw.models.exceptions.ReportNotFoundException;
import ar.edu.itba.paw.webapp.auth.AuthUtils;
import ar.edu.itba.paw.webapp.CustomMediaType;
import ar.edu.itba.paw.webapp.dto.ReportDto;
import ar.edu.itba.paw.webapp.form.CreateReportForm;
import ar.edu.itba.paw.webapp.form.UpdateReportStatusForm;
import ar.edu.itba.paw.webapp.utils.PagingUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.ResponseBuilder;

import java.util.List;

@Path("reports")
@Component
public class ReportController {

    @Autowired
    ReportService reportService;

    @Context
    private UriInfo uriInfo;


    @GET
    @Produces(CustomMediaType.APPLICATION_REPORT_LIST)
    public Response listReports(
            @QueryParam("search") String search,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("size") @DefaultValue("8") int size
    ) {
        final Page<Report> reports = reportService.findAll(
                search,
                new PageParams(page, size)
        );

        final List<ReportDto> reportDtos = ReportDto.fromReportCollection(uriInfo, reports.getContent());
        final ResponseBuilder response = Response.ok(new GenericEntity<>(reportDtos) {});
        return PagingUtils.insertPaginationLinks(response, uriInfo, reports).build();
    }

    @GET
    @Path("/{id}")
    @Produces(CustomMediaType.APPLICATION_REPORT)
    public Response getReportById(@PathParam("id") final long id) {
        final Report report = reportService.findById(id).orElseThrow(() -> new ReportNotFoundException(id));
        return Response.ok(ReportDto.fromReport(uriInfo, report)).build();
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_REPORT)
    public Response createReport(@Valid final CreateReportForm form) {
        final Long userId = AuthUtils.getCurrentUserId();

        final Report report = reportService.createReport(form.getReportType(),userId,form.getTargetId(),form.getDescription(),form.getReason());

        return Response.created(UriUtils.getReportUri(uriInfo, report.getId()))
                .entity(ReportDto.fromReport(uriInfo, report))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(CustomMediaType.APPLICATION_REPORT)
    public Response updateStatus(@PathParam("id") final long id, @Valid final UpdateReportStatusForm form) {
        final Report report = reportService.updateReportStatus(id,form.getStatus());
        return Response.ok(ReportDto.fromReport(uriInfo, report)).build();
    }


    @DELETE
    @Path("/{id}")
    public Response deleteReport(
            @PathParam("id") final long id
    ) {
        reportService.deleteById(id);
        return Response.noContent().build();
    }





}
