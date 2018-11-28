<%--
  Created by IntelliJ IDEA.
  User: 47009
  Date: 2018/3/21
  Time: 9:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>疯狂加载toP2P</title>
</head>
<body>
    <form action="${p2p_pay_return_url}" method="post">
        <input type="hidden" name="signVerified" value="${signVerified}" />
        <c:forEach items="${params}" var="paramMap">
            <input type="hidden" name="${paramMap.key}" value="${paramMap.value}"/>
        </c:forEach>
    </form>

    <script>document.forms[0].submit();</script>

</body>
</html>































