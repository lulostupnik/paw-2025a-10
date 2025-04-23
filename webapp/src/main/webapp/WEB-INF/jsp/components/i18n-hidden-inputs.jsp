<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<input type="hidden" id="i18n-interests-none" value="<spring:message code="interests.none.selected" text="No interests selected"/>" />
<input type="hidden" id="i18n-career-none" value="<spring:message code="career.none.selected" text="No career selected"/>" />
<input type="hidden" id="i18n-university-none" value="<spring:message code="university.none.selected" text="No university selected"/>" />
<input type="hidden" id="i18n-city-none" value="<spring:message code="city.none.selected" text="No city selected"/>" />
<input type="hidden" id="i18n-destination-none" value="<spring:message code="destination.none.selected" text="No destination selected"/>" />
<input type="hidden" id="i18n-items-none" value="<spring:message code="items.none.selected" text="No items selected"/>" />
<input type="hidden" id="i18n-university-none-error" value="<spring:message code="university.none.selected.error" text="Please select a university"/>" />
<input type="hidden" id="i18n-required-field" value="<spring:message code="dropdown.required.field" text="This field is required"/>" />
<input type="hidden" id="i18n-email-error" value="<spring:message code="valid.email.required" text="Please enter a valid email"/>" />
<input type="hidden" id="i18n-past-date-error" value="<spring:message code="date.not.past" text="Please, do not enter a date in the past"/>" />
<input type="hidden" id="i18n-valid-date-error" value="<spring:message code="end.date.before.start" text="End date must be after start date"/>" />
<%-- Hidden inputs for i18n messages used in JavaScript validation --%>
<input type="hidden" id="i18n-required-field" value="<spring:message code='validation.required' text='This field is required'/>" />
<input type="hidden" id="i18n-past-date-error" value="<spring:message code='validation.date.past' text='Date cannot be in the past'/>" />
<input type="hidden" id="i18n-positive-number-error" value="<spring:message code='validation.number.positive' text='Please enter a positive number'/>" />
<input type="hidden" id="i18n-city-required" value="<spring:message code='validation.city.required' text='Please select a city'/>" />
<input type="hidden" id="i18n-city-none" value="<spring:message code='validation.city.none' text='No city selected'/>" />
<input type="hidden" id="i18n-invalid-date-format" value="<spring:message code='validation.date.format' text='Please enter a valid date in YYYY-MM-DD format'/>" />
