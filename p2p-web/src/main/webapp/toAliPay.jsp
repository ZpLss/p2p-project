<%--
  Created by IntelliJ IDEA.
  User: 47009
  Date: 2018/3/20
  Time: 16:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>疯狂加载中...</title>
</head>
<body>
    <form action="http://localhost:9090/pay/api/alipay" method="post">

        <input type="hidden" name="out_trade_no" value="${rechargeNo}">
        <input type="hidden" name="total_amount" value="${rechargeMoney}">
        <input type="hidden" name="subject" value="${subject}">
        <input type="hidden" name="body" value="${body}">
    </form>
    <script>document.forms[0].submit();</script>
</body>
</html>
