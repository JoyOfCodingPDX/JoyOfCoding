#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
<%@ page contentType="text/plain;charset=UTF-8" language="java" isErrorPage="true" %>
<%out.println( request.getAttribute("javax.servlet.error.message") );%>