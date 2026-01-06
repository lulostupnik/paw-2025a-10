package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.models.Tip;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TipDto {

    private long id;
    private String title;
    private String content;
    private LocalDateTime dateTime;

    private URI selfUrl;
    private URI journeyUrl;

    public static TipDto fromTip(final UriInfo uriInfo, final Tip tip) {
        final TipDto dto = new TipDto();
        dto.id = tip.getId();
        dto.title = tip.getTitle();
        dto.content = tip.getContent();
        dto.dateTime = tip.getDateTime();

        dto.journeyUrl = UriUtils.getJourneyUri(uriInfo, tip.getJourney().getId());
        dto.selfUrl = UriUtils.getJourneyTipUri(uriInfo, tip.getJourney().getId(), tip.getId());

        return dto;
    }

    public static List<TipDto> fromTipCollection(final UriInfo uriInfo, final Collection<Tip> tips) {
        return tips.stream().map(tip -> fromTip(uriInfo, tip)).toList();
    }

    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getDateTime() { return dateTime; }
    public URI getSelfUrl() { return selfUrl; }
    public URI getJourneyUrl() { return journeyUrl; }
}
