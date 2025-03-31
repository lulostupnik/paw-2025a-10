<%--
  Created by IntelliJ IDEA.
  User: fer
  Date: 27/3/25
  Time: 15:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<html>
<head>
    <title>Register a new journey</title>
</head>
<body>
  <h2><spring:message code="createJourney.title"/></h2>
  <c:url var="registerUrl" value="/journey/"/>
  <form:form modelAttribute="createJourneyForm" action="${registerUrl}" method="post">
      <div>
          <form:errors path="email" cssClass="error" element="p"/>
          <label>
              <spring:message code="createJourney.userEmail"/>
              <spring:message code="createJourney.userEmail.hint" var="emailHint"/>
              <form:input path="email" placeholder="${emailHint}"/>
          </label>

      </div>            
      <div>
          <form:errors path="startDate" cssClass="error" element="p"/>
          <label>
              <spring:message code="createJourney.startDate"/>
              <spring:message code="createJourney.startDate.hint" var="startDateHint"/>
              <form:input type="date" path="startDate" placeholder="${startDateHint}"/>
          </label>
      </div>
      <div>
        <form:errors path="endDate" cssClass="error" element="p"/>
        <label>
            <spring:message code="createJourney.endDate"/>
            <spring:message code="createJourney.endDate.hint" var="endDateHint"/>
            <form:input type="date" path="endDate" placeholder="${endDateHint}"/>
        </label>
      </div>      
      <div>
        <form:errors path="destinationCity" cssClass="error" element="p"/>
        <label>
            <spring:message code="createJourney.destinationCity"/>
            <spring:message code="createJourney.destinationCity.hint" var="destinationCityHint"/>
            <form:input path="destinationCity" placeholder="${destinationCityHint}"/>
        </label>
      </div>
      <div>
        <form:errors path="destinationUniversity" cssClass="error" element="p"/>
        <label>
            <spring:message code="createJourney.destinationUniversity"/>
            <spring:message code="createJourney.destinationUniversity.hint" var="destinationUniversityHint"/>
            <form:input path="destinationUniversity" placeholder="${destinationUniversityHint}"/>
        </label>
      </div>
      <div>
        <form:errors path="description" cssClass="error" element="p"/>
        <label>
            <spring:message code="createJourney.description"/>
            <spring:message code="createJourney.description.hint" var="descriptionHint"/>
            <form:input path="description" placeholder="${descriptionHint}"/>
        </label>
      </div>
      <div>
        <spring:message code="createJourney.submit" var="submit"/>
        <input type="submit" value="${submit}"/>
      </div>
  </form:form>
</body>
</html>
