<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="sql" uri="http://java.sun.com/jsp/jstl/sql"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="stylesheet" type="text/css" href="css/inzynierka.css">
        <title>System wspomagania tworzenia projektów Open Source</title>
    </head>
    <body>       
        <div id="topElements">
            <div id="logoDiv">
                [logo]
            </div>
            <div id="searchDiv">
                [search]
            </div>
            <div id="loginDiv">
            <% if (request.getUserPrincipal()==null) {  %>
                <a href="/Inzynierka/logowanie">Zaloguj sie</a>
            <% } else { %>
                <a href="/Inzynierka/ustawienia">Ustawienia</a> <a href="/Inzynierka/wyloguj">Wyloguj sie</a>
            <% } %>                
            </div>
        </div>
        <div id="all">
            <div id="menu">
            <% if (request.getUserPrincipal()!=null) {  %>
                <a href="/Inzynierka/utworz_projekt">Utworz projekt</a>
            <% } %>
                <a href="/Inzynierka/lista_projektow">Lista projektow</a>
            </div>
            <% if (request.getUserPrincipal() != null && request.getUserPrincipal().getName().equals("Administrator")) { %>
            <div id="adminMenu">
                admin Menu
            </div>
            <% } %>