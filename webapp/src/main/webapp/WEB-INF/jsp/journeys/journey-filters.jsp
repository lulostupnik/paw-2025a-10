<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<link rel="stylesheet" href="<c:url value='/resources/css/components/journey-filters.css'/>" />

<div class="filters-container">
  <button id="filter-toggle" class="filter-toggle-btn">
    <svg xmlns="http://www.w3.org/2000/svg" class="filter-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
    </svg>
    <span><spring:message code="journey.filter.button"/></span>
    <span class="filter-count" id="filter-count">0</span>
  </button>

  <div id="filters-panel" class="filters-panel">
    <div class="filters-header">
      <h3 class="filters-title"><spring:message code="journey.filter.title"/></h3>
      <button id="filters-close" class="filters-close">
        <svg xmlns="http://www.w3.org/2000/svg" class="filters-close-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </button>
    </div>

    <form id="filter-form" action="<c:url value='/journeys'/>" method="get" class="filters-form">
      <div class="filters-grid">

        <div class="filter-group">
          <label for="destination" class="filter-label"><spring:message code="journey.filter.destination"/></label>
          <div class="filter-input-container">
            <svg xmlns="http://www.w3.org/2000/svg" class="filter-input-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <input type="text" id="destination" name="destination" class="filter-input" placeholder="<spring:message code='journey.filter.destination.placeholder'/>" value="${param.destination}">
          </div>
        </div>


        <div class="filter-group">
          <label for="startDate" class="filter-label"><spring:message code="journey.filter.startDate"/></label>
          <div class="filter-date-range">
            <div class="filter-input-container">
              <svg xmlns="http://www.w3.org/2000/svg" class="filter-input-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <input type="date" id="startDate" name="startDate" class="filter-input" value="${param.startDate}">
            </div>
            <span class="date-separator">-</span>
            <div class="filter-input-container">
              <svg xmlns="http://www.w3.org/2000/svg" class="filter-input-icon" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              <input type="date" id="endDate" name="endDate" class="filter-input" value="${param.endDate}">
            </div>
          </div>
        </div>
      </div>
    </form>
  </div>
</div>
