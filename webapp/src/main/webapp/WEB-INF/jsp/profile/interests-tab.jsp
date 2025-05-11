<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="profile-section active" id="interests-section">
    <div class="profile-card">
        <h2 class="section-title"><spring:message code="profile.home.interest"/></h2>
        <div class="info-list">
            <c:if test="${not empty interests.content}">
                <c:forEach items="${interests.content}" var="interest">
                    <div class="info-item">
                        <p class="info-value"><c:out value="${interest.name}"/></p>
                    </div>
                </c:forEach>
                <jsp:include page="/WEB-INF/jsp/components/pagination-with-page-number.jsp">
                    <jsp:param name="pageObjectTotalPages" value="${interests.totalPages}" />
                    <jsp:param name="currentPage" value="${interests.currentPage}" />
                    <jsp:param name="pageSize" value="4" />
                    <jsp:param name="baseUrl" value="/profile/interests" />
                </jsp:include>


                <div class="action-buttons">
                    <a href="<c:url value='/interests/edit'/>" class="btn-primary">
                        <spring:message code="profile.edit.interests"/>
                    </a>
                </div>
            </c:if>
            <c:if test="${empty interests.content}">
                <div class="empty-state">
                    <div class="empty-icon">
                        <svg xmlns="http://www.w3.org/2000/svg" class="empty-svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                        </svg>
                    </div>
                    <p class="empty-message">
                        <spring:message code="profile.no.interests"/>
                    </p>


                    <div class="action-buttons">
                        <a href="<c:url value='/interests/edit'/>" class="btn-primary">
                            <spring:message code="profile.add.interests"/>
                        </a>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
</div>