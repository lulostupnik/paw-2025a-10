package ar.edu.itba.paw.webapp.form;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ReplyForm {

        @Size(min = 2, max = 2047)
        @NotNull
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "{message: \"" + message + "\"}";
        }

}
