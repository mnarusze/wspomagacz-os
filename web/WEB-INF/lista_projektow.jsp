<div id="main">
    Lista projektow   
    <sql:query var="projects" dataSource="jdbc/inzynierka">
        SELECT * FROM projects
    </sql:query>
    <c:set var="counter" value="1" scope="page" />
    <c:forEach var="project" items="${projects.rows}">
        <c:if test="${(counter % 2) == 0}">
        <div id="projectBox1">
        </c:if>
        <c:if test="${(counter % 2) == 1}">    
        <div id="projectBox2">
        </c:if>
            <a href="/Inzynierka/projekt?n=${project.proj_name}">${project.proj_name}</a>
        </div>
        <c:set var="counter" value="${counter+1}" />
    </c:forEach>
</div>