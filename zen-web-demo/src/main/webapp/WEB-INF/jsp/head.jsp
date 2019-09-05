<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/jsp/include/taglib.jsp" %>
<meta charset="utf-8">
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<c:if test="${not empty param.title }"><title>${param.title }</title></c:if>
<link href="<spring:url value="/css/bootstrap.min.css" />" rel="stylesheet">
<link href="<spring:url value="/font-awesome/css/font-awesome.css" />" rel="stylesheet">
<link href="<spring:url value="/css/animate.css" />" rel="stylesheet">
<link href="<spring:url value="/css/style.css" />" rel="stylesheet">
