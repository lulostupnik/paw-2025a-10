<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!-- Filters Component -->
<div class="bg-white rounded-xl border border-gray-200 mb-6 overflow-hidden">
  <div class="p-4 border-b border-gray-200">
    <div class="flex items-center gap-2">
      <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="text-gray-500">
        <polygon points="22 3 2 3 10 12.46 10 19 14 21 14 12.46 22 3"></polygon>
      </svg>
      <h3 class="font-medium text-gray-800"><spring:message code="journey.filters.title" text="Filters"/></h3>
    </div>
  </div>

  <div class="p-4">
    <form action="${pageContext.request.contextPath}/journeys" method="get">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <!-- Date Range Filter - Fixed -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect width="18" height="18" x="3" y="4" rx="2" ry="2"></rect>
              <line x1="16" x2="16" y1="2" y2="6"></line>
              <line x1="8" x2="8" y1="2" y2="6"></line>
              <line x1="3" x2="21" y1="10" y2="10"></line>
            </svg>
            <spring:message code="journey.filters.date" text="Date"/>
          </label>
          <div class="grid grid-cols-2 gap-2">
            <div>
              <label for="startDate" class="block text-xs text-gray-500 mb-1">
                <spring:message code="journey.filters.from" text="From"/>
              </label>
              <input type="date" id="startDate" name="startDate" class="py-2 px-3 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
            <div>
              <label for="endDate" class="block text-xs text-gray-500 mb-1">
                <spring:message code="journey.filters.to" text="To"/>
              </label>
              <input type="date" id="endDate" name="endDate" class="py-2 px-3 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
            </div>
          </div>
        </div>

        <!-- Destination Filters -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"></path>
              <circle cx="12" cy="10" r="3"></circle>
            </svg>
            <spring:message code="journey.filters.destination" text="Destination"/>
          </label>
          <div class="grid grid-cols-2 gap-2">
            <input type="text" name="destinationCity" placeholder="<spring:message code="journey.filters.city" text="City"/>" class="py-2 px-3 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
            <input type="text" name="destinationUniversity" placeholder="<spring:message code="journey.filters.university" text="University"/>" class="py-2 px-3 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
          </div>
        </div>

        <!-- Origin Filters -->
        <div class="space-y-2">
          <label class="block text-sm font-medium text-gray-700 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
              <polyline points="9 22 9 12 15 12 15 22"></polyline>
            </svg>
            <spring:message code="journey.filters.origin" text="Origin"/>
          </label>
          <div class="grid grid-cols-2 gap-2">
            <input type="text" name="originCity" placeholder="<spring:message code="journey.filters.city" text="City"/>" class="py-2 px-3 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
            <input type="text" name="originUniversity" placeholder="<spring:message code="journey.filters.university" text="University"/>" class="py-2 px-3 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
          </div>
        </div>

        <!-- Degree Filter -->
        <div class="space-y-2">
          <label for="degree" class="block text-sm font-medium text-gray-700 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M22 10v6M2 10l10-5 10 5-10 5z"></path>
              <path d="M6 12v5c3 3 9 3 12 0v-5"></path>
            </svg>
            <spring:message code="journey.filters.degree" text="Degree"/>
          </label>
          <select id="degree" name="degree" class="py-2 px-3 pe-9 block w-full border-gray-200 rounded-lg text-sm focus:border-blue-500 focus:ring-blue-500">
            <option value=""><spring:message code="journey.filters.select.degree" text="Select degree"/></option>
            <option value="bachelor"><spring:message code="journey.filters.bachelor" text="Bachelor's"/></option>
            <option value="master"><spring:message code="journey.filters.master" text="Master's"/></option>
            <option value="phd"><spring:message code="journey.filters.phd" text="PhD"/></option>
            <option value="exchange"><spring:message code="journey.filters.exchange" text="Exchange Program"/></option>
          </select>
        </div>

        <!-- Interests Filter -->
        <div class="space-y-2 md:col-span-2">
          <label class="block text-sm font-medium text-gray-700 flex items-center gap-1">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="11" cy="11" r="8"></circle>
              <path d="m21 21-4.3-4.3"></path>
            </svg>
            <spring:message code="journey.filters.interests" text="Interests"/>
          </label>
          <div class="grid grid-cols-2 sm:grid-cols-3 gap-2">
            <c:forEach var="interest" items="${interests}">
              <div class="flex items-center">
                <input type="checkbox" id="interest-${interest.id}" name="interests" value="${interest.id}" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-${interest.id}" class="ml-2 text-sm text-gray-600">${interest.name}</label>
              </div>
            </c:forEach>

            <!-- Fallback if no interests are available -->
            <c:if test="${empty interests}">
              <div class="flex items-center">
                <input type="checkbox" id="interest-1" name="interests" value="1" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-1" class="ml-2 text-sm text-gray-600">Academic</label>
              </div>
              <div class="flex items-center">
                <input type="checkbox" id="interest-2" name="interests" value="2" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-2" class="ml-2 text-sm text-gray-600">Cultural</label>
              </div>
              <div class="flex items-center">
                <input type="checkbox" id="interest-3" name="interests" value="3" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-3" class="ml-2 text-sm text-gray-600">Sports</label>
              </div>
              <div class="flex items-center">
                <input type="checkbox" id="interest-4" name="interests" value="4" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-4" class="ml-2 text-sm text-gray-600">Technology</label>
              </div>
              <div class="flex items-center">
                <input type="checkbox" id="interest-5" name="interests" value="5" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-5" class="ml-2 text-sm text-gray-600">Arts</label>
              </div>
              <div class="flex items-center">
                <input type="checkbox" id="interest-6" name="interests" value="6" class="h-4 w-4 border-gray-200 rounded text-blue-600 focus:ring-blue-500">
                <label for="interest-6" class="ml-2 text-sm text-gray-600">Business</label>
              </div>
            </c:if>
          </div>
        </div>
      </div>

      <div class="flex justify-end gap-2 mt-4">
        <button type="reset" class="py-2 px-3 inline-flex items-center gap-x-2 text-sm font-medium rounded-lg border border-gray-200 bg-white text-gray-800 shadow-sm hover:bg-gray-50">
          <spring:message code="journey.filters.reset" text="Reset"/>
        </button>
        <button type="submit" class="py-2 px-3 inline-flex items-center gap-x-2 text-sm font-medium rounded-lg border border-transparent bg-blue-600 text-white hover:bg-blue-700">
          <spring:message code="journey.filters.apply" text="Apply"/>
        </button>
      </div>
    </form>
  </div>
</div>
<!-- End Filters Component -->