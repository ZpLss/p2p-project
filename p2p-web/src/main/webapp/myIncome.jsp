<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>动力金融网 - 专业的互联网金融信息服务平台</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/cashbox-share.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fund-guanli.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/share.css"/>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/base.css"/>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" language="javascript" src="${pageContext.request.contextPath}/js/trafficStatistics.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	$("#overView").removeClass("on");
	$("#myAccount").addClass("on");
	$("#shouyijilu").addClass("on");
});
</script>
</head>

<body>
<!--页头start-->
<div id="header">
<jsp:include page="commons/header.jsp"/>
</div>
<!--页头end-->

<!-- 二级导航栏start -->
<jsp:include page="commons/subNav.jsp"/>
<!-- 二级导航栏end -->

<!--页中start-->
<div class="mainBox">
 <div class="homeWap">
  <div class="fund-guanli clearfix">
   <div class="left-nav">
	<jsp:include page="commons/leftNav.jsp"/>
   </div>
   <div class="right-body">
    <div class="right-wap">
     <div class="deal-data">
      <dl>
       <dt>
       <span class="deal-time">收益日期</span>
       <span class="deal-name">收益金额</span>
       <span class="deal-type" style="width:120px">投资产品</span>
       <span class="deal-money">投资金额(元)</span>
       <span class="deal-balance">收益状态</span>
       </dt>
		  <c:forEach items="${dataList}" var="incomeRecord" varStatus="index">
			  <dd>
				  <div class="deal-time"><fmt:formatDate value="${incomeRecord.incomeDate}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate> </div>
				  <div class="deal-name">${incomeRecord.bidMoney}</div>
				  <div class="deal-type" style="width:120px">${incomeRecord.loanInfo.productName}</div>
				  <div class="deal-money">${incomeRecord.bidMoney}</div>
				  <c:choose>
					  <%--0 :未返还  1:已返还--%>
					  <c:when test="${incomeRecord.incomeStatus eq 0}">
						  <div class="deal-balance">收益未返还</div>
					  </c:when>
					  <c:otherwise>
						  <div class="deal-balance">收益已返还</div>
					  </c:otherwise>
				  </c:choose>

			  </dd>
		  </c:forEach>

      	&nbsp;&nbsp;
		<div class="touzi_fenye" style="width:100%;text-align:center;">
		共${totalRows}条${totalPage}页　当前为第${currentPage}页
			<c:choose>
				<c:when test="${currentPage eq 1}">
					首页&nbsp;&nbsp;
					上一页
				</c:when>
				<c:otherwise>
					<a id="linkHomePage" href="${pageContext.request.contextPath}/loan/myIncome">首页</a>
					<a id="linkPreviousPage" href="${pageContext.request.contextPath}/loan/myIncome?currentPage=${currentPage-1}">上一页</a>
				</c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${currentPage eq totalPage}">
					下一页&nbsp;&nbsp;
					尾页
				</c:when>
				<c:otherwise>
					<a id="linkNextPage" href="${pageContext.request.contextPath}/loan/myIncome?currentPage=${currentPage+1}">下一页 </a>
					<a id="linkLastPage" href="${pageContext.request.contextPath}/loan/myIncome?currentPage=${totalPage}">尾页</a>
				</c:otherwise>
			</c:choose>
		</div>
      </dl>
     </div>
    </div>
   </div>
  </div>      
 </div>
</div>
<!--页中end-->   
<!--页脚start-->
<jsp:include page="commons/footer.jsp"/>
<!--页脚end-->
</body>
</html>