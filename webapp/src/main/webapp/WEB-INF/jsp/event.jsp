<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
--FIXME: NOT YET TESTED
<html>
<head>
  <title>Create Event</title>
  <style>
    .error {
      color: red;
    }
    .form-group {
      margin-bottom: 15px;
    }
  </style>
</head>
<body>
<h1>Create New Event</h1>

<c:if test="${not empty error}">
  <div class="error">${error}</div>
</c:if>

<form:form modelAttribute="createEventForm" action="${pageContext.request.contextPath}/events" method="post" enctype="multipart/form-data">
  <div class="form-group">
    <form:label path="city">City:</form:label>
    <form:input path="city" />
    <form:errors path="city" cssClass="error" />
  </div>

  <div class="form-group">
    <form:label path="date">Date:</form:label>
    <form:input path="date" type="date" />
    <form:errors path="date" cssClass="error" />
  </div>

  <div class="form-group">
    <form:label path="description">Description:</form:label>
    <form:textarea path="description" rows="5" />
    <form:errors path="description" cssClass="error" />
  </div>

  <div class="form-group">
    <form:label path="flyer">Event Flyer:</form:label>
    <input type="file" name="flyer" />
  </div>

  <div class="form-group">
    <input type="submit" value="Create Event" />
  </div>
</form:form>
</body>
</html>