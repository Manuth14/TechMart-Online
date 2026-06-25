<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Status</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 font-sans min-h-screen flex items-center justify-center antialiased">

<div class="w-full max-w-md p-4 text-center">

    <%-- 🎯 සර්ව්ලට් එකෙන් සාර්ථකයි කියලා එව්වොත් මේ කෑල්ල පේනවා --%>
    <% if (request.getAttribute("success") != null && (Boolean) request.getAttribute("success")) { %>

    <div class="bg-white rounded-2xl p-8 shadow-xl border border-emerald-100 transform transition duration-500 hover:scale-105">
        <div class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-emerald-100 mb-6">
            <span class="text-3xl">🎉</span>
        </div>

        <h2 class="text-2xl font-bold text-emerald-600 mb-3">Order Placed Successfully!</h2>
        <p class="text-sm text-slate-500 mb-6">Your order via Pure DTO Pattern was processed.</p>

        <div class="bg-slate-50 rounded-xl p-4 text-left space-y-2 mb-6 border border-slate-100">
            <p class="text-sm text-slate-600"><span class="font-semibold text-slate-800">Order ID:</span> <%= request.getAttribute("orderId") %></p>
            <p class="text-sm text-slate-600"><span class="font-semibold text-slate-800">Product ID:</span> <%= request.getAttribute("productId") %></p>
            <p class="text-sm text-slate-600"><span class="font-semibold text-slate-800">Quantity:</span> <%= request.getAttribute("qty") %></p>
        </div>

        <a href="index.jsp" class="inline-block w-full py-3 px-4 bg-emerald-600 hover:bg-emerald-700 text-white font-medium rounded-xl shadow-md shadow-emerald-200 transition-colors duration-200">
            Back to Shop
        </a>
    </div>

    <%-- 🎯 සර්ව්ලට් එකෙන් ෆේල් කියලා එව්වොත් මේ කෑල්ල පේනවා --%>
    <% } else { %>

    <div class="bg-white rounded-2xl p-8 shadow-xl border border-rose-100 transform transition duration-500 hover:scale-105">
        <div class="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-rose-100 mb-6">
            <span class="text-3xl">❌</span>
        </div>

        <h2 class="text-2xl font-bold text-rose-600 mb-3">Order Failed!</h2>
        <p class="text-sm text-slate-500 mb-6">කරුණාකර ස්ටොක් ප්‍රමාණය හෝ Product ID එක නිවැරදිදැයි පරීක්ෂා කරන්න.</p>

        <% if (request.getAttribute("error") != null) { %>
        <div class="bg-rose-50 rounded-xl p-3 text-left mb-6 border border-rose-100">
            <p class="text-xs font-mono text-rose-700 break-all"><strong>Error:</strong> <%= request.getAttribute("error") %></p>
        </div>
        <% } %>

        <a href="index.jsp" class="inline-block w-full py-3 px-4 bg-rose-600 hover:bg-rose-700 text-white font-medium rounded-xl shadow-md shadow-rose-200 transition-colors duration-200">
            Try Again
        </a>
    </div>

    <% } %>

</div>

</body>
</html>