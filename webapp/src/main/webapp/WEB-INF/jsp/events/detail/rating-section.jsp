<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<link rel="stylesheet" href="<c:url value='/resources/css/ratings.css'/>" />

<c:if test="${empty param.showToggle}">
  <c:set var="showToggle" value="true" />
</c:if>
<c:if test="${not empty param.showToggle}">
  <c:set var="showToggle" value="${param.showToggle}" />
</c:if>

<div class="content-section">
  <div class="section-header">
    <h2 class="section-title">
      <svg xmlns="http://www.w3.org/2000/svg" width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
      </svg>
      <spring:message code="event.rating" />
      <c:if test="${averageRating.isPresent()}">
        <span class="count">(<c:out value="${ratingCount}" /> <spring:message code="event.rating.reviews" />)</span>
      </c:if>
    </h2>
    <c:if test="${showToggle eq 'true'}">
      <button class="toggle-button" data-toggle="rating-section" onclick="toggleSection('rating-section')">
                <span class="collapse-icon">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="18 15 12 9 6 15"></polyline>
                    </svg>
                </span>
        <span class="expand-icon" style="display: none;">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <polyline points="6 9 12 15 18 9"></polyline>
                    </svg>
                </span>
      </button>
    </c:if>
  </div>

  <div id="rating-section" class="rating-content">
    <!-- Overall Rating Display -->
    <c:if test="${ averageRating.isPresent()}">
      <div class="rating-summary">
        <div class="average-rating">
          <span class="rating-number"><c:out value="${averageRating.get()}" /></span>
          <div class="rating-stars-display">
            <c:forEach var="i" begin="1" end="5">
              <c:choose>
                <c:when test="${averageRating.get() >= i}">
                  <svg class="star filled" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                  </svg>
                </c:when>
                <c:when test="${averageRating.get() >= (i - 0.5)}">
                  <svg class="star half-filled" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <defs>
                      <linearGradient id="half-fill-${i}">
                        <stop offset="50%" stop-color="currentColor" stop-opacity="1"/>
                        <stop offset="50%" stop-color="transparent" stop-opacity="0"/>
                      </linearGradient>
                    </defs>
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill="url(#half-fill-${i})"/>
                  </svg>
                </c:when>
                <c:otherwise>
                  <svg class="star empty" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                  </svg>
                </c:otherwise>
              </c:choose>
            </c:forEach>
          </div>
          <span class="rating-text">
                        <spring:message code="event.rating.outOf" arguments="5" />
                    </span>
        </div>
      </div>
    </c:if>

    <c:if test="${ averageRating.isEmpty()}">
      <div class="rating-summary empty-state">
        <div class="empty-icon">
          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
          </svg>
        </div>
        <p class="empty-message">
          <spring:message code="event.rating.noRatings" />
        </p>
        </div>
    </c:if>


    <!-- User Rating Form (only for authenticated users who attended the event) -->
    <c:if test="${not empty user and attend and empty userRating}">
      <c:if test="${ ! event.isFuture}">
        <div class="user-rating-form">
          <h3 class="rating-form-title">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
            <spring:message code="event.rating.add" />
          </h3>

          <c:url var="ratingUrl" value="/events/${event.id}/rating"/>
          <form:form modelAttribute="eventRatingForm" action="${ratingUrl}" method="post" cssClass="rating-form">
            <div class="rating-input-container">
              <label class="rating-label">
                <spring:message code="event.rating.yourRating" />
              </label>
              <div class="star-rating-input" data-rating="${not empty userRating ? userRating.get() : 0}">
                <c:forEach var="i" begin="1" end="5">
                  <div class="star-input-group">
                    <!-- Full star -->
                    <input type="radio" name="rating" value="${i}" id="star-${i}"
                      ${not empty userRating && userRating.get() == i ? 'checked' : ''} />
                    <label for="star-${i}" class="star-label full-star" data-value="${i}">
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" stroke="currentColor" stroke-width="2">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                      </svg>
                    </label>

                    <!-- Half star (except for the first star) -->
                    <c:if test="${i > 1}">
                      <input type="radio" name="rating" value="${i - 0.5}" id="star-${i - 0.5}"
                        ${not empty userRating && userRating.get() == (i - 0.5) ? 'checked' : ''} />
                      <label for="star-${i - 0.5}" class="star-label half-star" data-value="${i - 0.5}">
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                          <defs>
                            <linearGradient id="half-input-${i}">
                              <stop offset="50%" stop-color="currentColor" stop-opacity="1"/>
                              <stop offset="50%" stop-color="transparent" stop-opacity="0"/>
                            </linearGradient>
                          </defs>
                          <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill="url(#half-input-${i})"/>
                        </svg>
                      </label>
                    </c:if>
                  </div>
                </c:forEach>
              </div>
              <div class="rating-value-display">
                                <span id="current-rating-value">
                                    <c:choose>
                                      <c:when test="${ not empty userRating}">
                                        <c:out value="${userRating.get()}" />
                                      </c:when>
                                      <c:otherwise>0</c:otherwise>
                                    </c:choose>
                                </span>
                <span class="rating-max">/ 5</span>
              </div>
            </div>

            <div class="form-actions">
              <c:set var="submitButtonLabel">
                    <spring:message code="event.rating.submit"/>
              </c:set>
              <jsp:include page="../../components/button.jsp">
                <jsp:param name="label" value="${submitButtonLabel}" />
                <jsp:param name="type" value="submit" />
              </jsp:include>
            </div>
          </form:form>
        </div>
      </c:if>
    </c:if>

<%--    <!-- Rating List -->--%>
<%--    <c:if test="${not empty eventRatings}">--%>
<%--      <div class="ratings-list">--%>
<%--        <h3 class="ratings-list-title">--%>
<%--          <spring:message code="event.rating.all" />--%>
<%--        </h3>--%>
<%--        <c:forEach var="rating" items="${eventRatings}">--%>
<%--          <div class="rating-item">--%>
<%--            <div class="rating-header">--%>
<%--              <div class="rating-user">--%>
<%--                <div class="rating-avatar">--%>
<%--                  <c:if test="${not empty rating.user.profilePictureId}">--%>
<%--                    <img src="<c:url value='/images/${rating.user.profilePictureId}'/>" alt="Profile" class="rating-avatar-img">--%>
<%--                  </c:if>--%>
<%--                  <c:if test="${empty rating.user.profilePictureId}">--%>
<%--                    <div class="avatar-placeholder">--%>
<%--                      <c:out value="${fn:substring(rating.user.firstname, 0, 1)}${fn:substring(rating.user.lastname, 0, 1)}" />--%>
<%--                    </div>--%>
<%--                  </c:if>--%>
<%--                </div>--%>
<%--                <div class="rating-user-info">--%>
<%--                  <h4 class="rating-username">--%>
<%--                    <c:out value="${rating.user.firstname} ${rating.user.lastname}" />--%>
<%--                  </h4>--%>
<%--                  <p class="rating-date">--%>
<%--                    <c:out value="${rating.formattedDate}" />--%>
<%--                  </p>--%>
<%--                </div>--%>
<%--              </div>--%>
<%--              <div class="rating-stars">--%>
<%--                <c:forEach var="i" begin="1" end="5">--%>
<%--                  <c:choose>--%>
<%--                    <c:when test="${rating.rating >= i}">--%>
<%--                      <svg class="star filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">--%>
<%--                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>--%>
<%--                      </svg>--%>
<%--                    </c:when>--%>
<%--                    <c:when test="${rating.rating >= (i - 0.5)}">--%>
<%--                      <svg class="star half-filled" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">--%>
<%--                        <defs>--%>
<%--                          <linearGradient id="rating-half-${rating.id}-${i}">--%>
<%--                            <stop offset="50%" stop-color="currentColor" stop-opacity="1"/>--%>
<%--                            <stop offset="50%" stop-color="transparent" stop-opacity="0"/>--%>
<%--                          </linearGradient>--%>
<%--                        </defs>--%>
<%--                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" fill="url(#rating-half-${rating.id}-${i})"/>--%>
<%--                      </svg>--%>
<%--                    </c:when>--%>
<%--                    <c:otherwise>--%>
<%--                      <svg class="star empty" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">--%>
<%--                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>--%>
<%--                      </svg>--%>
<%--                    </c:otherwise>--%>
<%--                  </c:choose>--%>
<%--                </c:forEach>--%>
<%--                <span class="rating-value">(<c:out value="${rating.rating}" />)</span>--%>
<%--              </div>--%>
<%--            </div>--%>
<%--          </div>--%>
<%--        </c:forEach>--%>
<%--      </div>--%>
<%--    </c:if>--%>

<%--    <c:if test="${empty eventRatings}">--%>
<%--      <div class="empty-state">--%>
<%--        <div class="empty-icon">--%>
<%--          <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round">--%>
<%--            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>--%>
<%--          </svg>--%>
<%--        </div>--%>
<%--        <p class="empty-message">--%>
<%--          <spring:message code="event.rating.noRatings" />--%>
<%--        </p>--%>
<%--      </div>--%>
<%--    </c:if>--%>
  </div>
</div>


<script>
  document.addEventListener('DOMContentLoaded', function() {
    // Star rating interaction
    const starInputs = document.querySelectorAll('.star-rating-input input[type="radio"]');
    const starLabels = document.querySelectorAll('.star-rating-input .star-label');
    const ratingValueDisplay = document.getElementById('current-rating-value');

    starInputs.forEach(function(input) {
      input.addEventListener('change', function() {
        const value = parseFloat(this.value);
        if (ratingValueDisplay) {
          ratingValueDisplay.textContent = value;
        }

        // Update visual state
        starLabels.forEach(function(label) {
          const labelValue = parseFloat(label.getAttribute('data-value'));
          if (labelValue <= value) {
            label.classList.add('active');
          } else {
            label.classList.remove('active');
          }
        });
      });
    });

    // Hover effects for star rating
    starLabels.forEach(function(label) {
      label.addEventListener('mouseenter', function() {
        const value = parseFloat(this.getAttribute('data-value'));
        starLabels.forEach(function(l) {
          const lValue = parseFloat(l.getAttribute('data-value'));
          if (lValue <= value) {
            l.style.color = '#fbbf24';
          } else {
            l.style.color = '#e5e7eb';
          }
        });
      });
    });

    const starRatingContainer = document.querySelector('.star-rating-input');
    if (starRatingContainer) {
      starRatingContainer.addEventListener('mouseleave', function() {
        const checkedInput = document.querySelector('.star-rating-input input[type="radio"]:checked');
        if (checkedInput) {
          const checkedValue = parseFloat(checkedInput.value);
          starLabels.forEach(function(label) {
            const labelValue = parseFloat(label.getAttribute('data-value'));
            if (labelValue <= checkedValue) {
              label.style.color = '#fbbf24';
            } else {
              label.style.color = '#e5e7eb';
            }
          });
        } else {
          starLabels.forEach(function(label) {
            label.style.color = '#e5e7eb';
          });
        }
      });
    }
  });
</script>