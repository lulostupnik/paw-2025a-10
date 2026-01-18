package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.Size;

public class PatchTipForm {

    @Size
    private String title;

    @Size(max = 2047)
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
