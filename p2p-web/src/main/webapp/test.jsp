<%--
  Created by IntelliJ IDEA.
  User: 47009
  Date: 2018/3/24
  Time: 8:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <h3>第三方支付</h3>
    <%--<img src="${pageContext.request.contextPath}/loan/generateQRCode?out_trade_no=${rechargeNo}&rechargeMoney=${rechargeMoney}">
    --%><img src="loan/generateQRCode?out_trade_no=${rechargeNo}&rechargeMoney=${rechargeMoney}" />
</body>
</html>
