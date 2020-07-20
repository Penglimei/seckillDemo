<%--
  Created by IntelliJ IDEA.
  User: penglimei
  Date: 2020-07-17
  Time: 18:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- 引入共用的jstl --%>
<%@include file="common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀列表页</title>
    <%-- 头部都是相同的，因此单独提取出来，放到head.jsp中 --%>
    <%@include file="common/head.jsp" %>
</head>
<body>
<%-- 页面显示部分 --%>
<div class="container">
    <div class="panel panel-default">

        <div class="panel-heading text-center">
            <h2>秒杀列表</h2>
        </div>

        <div class="panel-body">
            <table class="table table-hover">
                <thead>
                <tr>
                    <th>名称</th>
                    <th>库存</th>
                    <th>开始时间</th>
                    <th>结束时间</th>
                    <th>创建时间</th>
                    <th>详情页</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="sk" items="${pageInfo.list}">
                    <tr>
                        <td>${sk.name}</td>
                        <td>${sk.number}</td>
                        <td>
                            <fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </td>
                        <td>
                            <a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">查看</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
                <tr style="text-align: center">
                    <td colspan="5">
                        <a href="/seckill/list?curPage=${pageInfo.firstPage}">首页</a>
                        <c:if test="${pageInfo.hasPreviousPage}">
                            <a href="/seckill/list?curPage=${pageInfo.pageNum-1}">上一页</a>
                        </c:if>
                        <c:if test="${pageInfo.hasNextPage}">
                            <a href="/seckill/list?curPage=${pageInfo.pageNum+1}">下一页</a>
                        </c:if>

                        <a href="/seckill/list?curPage=${pageInfo.lastPage}">尾页</a>
                        当前${pageInfo.pageNum}页/共${pageInfo.pages}页/共${pageInfo.total}条记录
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
</body>
<!-- jQuery (Bootstrap 的 JavaScript 插件需要引入 jQuery) -->
<script src="https://code.jquery.com/jquery.js"></script>
<!-- 包括所有已编译的插件 -->
<script src="js/bootstrap.min.js"></script>
</html>