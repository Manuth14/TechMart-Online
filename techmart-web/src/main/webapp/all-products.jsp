<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="lk.techmart.core.DTO.InventoryDTO" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TechMart - Live Multi-Warehouse Inventory</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 font-sans min-h-screen antialiased relative">

<div id="toast-container" class="fixed bottom-5 right-5 z-50 flex flex-col gap-3 max-w-md w-full px-4"></div>

<nav class="bg-white shadow-sm border-b border-slate-200">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-slate-800 tracking-tight">TechMart <span class="text-sm font-normal text-emerald-600">Enterprise</span></h1>
        <div class="flex items-center space-y-0 space-x-2">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-emerald-100 text-emerald-800">
                    <span class="h-2 w-2 rounded-full bg-emerald-500 animate-pulse mr-2"></span>
                    Live Sync Connected
                </span>
        </div>
    </div>
</nav>

<main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10">
    <div class="mb-8">
        <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Multi-Warehouse Stock</h2>
        <p class="mt-2 text-sm text-slate-600">Real-time inventory synchronization across automated fulfillment centers.</p>
    </div>

    <div class="mb-8 bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
        <h3 class="text-sm font-bold text-slate-700 uppercase tracking-wider mb-3 flex items-center">
            <span class="h-2 w-2 rounded-full bg-blue-500 animate-ping mr-2"></span>
            ActiveMQ Enterprise Notification Log
        </h3>
        <div class="max-h-40 overflow-y-auto border border-slate-100 bg-slate-50 rounded-xl p-4 text-xs font-mono text-slate-600 flex flex-col gap-2" id="notificationLog">
            <div class="text-slate-400 italic">Waiting for JMS messages...</div>
        </div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
        <%
            List<InventoryDTO> products = (List<InventoryDTO>) request.getAttribute("products");
            if (products != null && !products.isEmpty()) {
                for (InventoryDTO item : products) {
        %>
        <div class="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm transition-all duration-300 hover:shadow-md flex flex-col justify-between">
            <div>
                <div class="flex items-center justify-between mb-4">
                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-md text-xs font-medium bg-slate-100 text-slate-800">
                        📍 WH-01 (Main)
                    </span>
                </div>

                <h3 class="text-lg font-bold text-slate-800 mb-1">Item Code: <%= item.getProductId() %></h3>
                <p class="text-xs text-slate-400 mb-4">Managed via Pure DTO Pattern</p>

                <div class="bg-slate-50 border border-slate-100 rounded-xl p-4 mb-6">
                    <div class="flex justify-between items-center">
                        <span class="text-sm text-slate-500 font-medium">Available Stock:</span>
                        <span id="stock-<%= item.getProductId() %>" class="text-2xl font-black text-slate-800 transition-all duration-500 inline-block">
                            <%= item.getStockQuantity() %>
                        </span>
                    </div>
                </div>
            </div>

            <button onclick="instantCheckout('<%= item.getProductId() %>')"
                    class="w-full py-3 px-4 bg-slate-900 hover:bg-slate-800 text-white text-sm font-semibold rounded-xl shadow-sm transition-colors duration-200">
                Instant Order (Qty: 1)
            </button>
        </div>
        <%
            }
        } else {
        %>
        <div class="col-span-full bg-white rounded-2xl border border-dashed border-slate-300 p-12 text-center">
            <span class="text-4xl mb-4 block">📦</span>
            <h3 class="text-sm font-medium text-slate-900">No inventory loaded</h3>
            <p class="mt-1 text-sm text-slate-500">කරුණාකර ProductListServlet එක හරහා මේ පිටුවට පිවිසෙන්න.</p>
        </div>
        <%
            }
        %>
    </div>
</main>

<script>
    // 🔗 1. WebSocket එකට සම්බන්ධ වීම සඳහා URL එක හැදීම සහ Connect වීම
    const wsUri = "ws://" + document.location.host + "${pageContext.request.contextPath}/inventory-sync";
    const websocket = new WebSocket(wsUri);

    // 🎯 2. පිටුව Refresh කරද්දී LocalStorage එකෙන් පරණ නොටිෆිකේෂන් ලෝඩ් කිරීම
    document.addEventListener("DOMContentLoaded", function() {
        const logBoard = document.getElementById("notificationLog");
        const savedNotifs = JSON.parse(localStorage.getItem("activemq_notifs")) || [];

        if (savedNotifs.length > 0 && logBoard) {
            logBoard.innerHTML = ""; // Waiting message එක අයින් කරනවා
            savedNotifs.forEach(msg => {
                const logItem = document.createElement("div");
                logItem.className = "bg-blue-50 border-l-4 border-blue-500 p-2 rounded shadow-sm text-blue-900";
                logItem.innerHTML = msg;
                logBoard.appendChild(logItem);
            });
        }
    });

    // 🎯 3. WebSocket මගින් එන Messages කියවීම (Stock සහ Notifications)
    websocket.onmessage = function(event) {
        const responseData = JSON.parse(event.data);

        // 👉 අවස්ථාව A: ActiveMQ එකෙන් ආපු NOTIFICATION එකක් නම්
        if (responseData.type === "NOTIFICATION") {
            const msg = responseData.message;
            showLiveToast(msg);

            const logBoard = document.getElementById("notificationLog");
            if (logBoard) {
                if (logBoard.innerText.includes("Waiting for JMS")) {
                    logBoard.innerHTML = "";
                }

                date = new Date().toLocaleTimeString();
                const htmlContent = date + msg;
                const logItem = document.createElement("div");
                logItem.className = "bg-blue-50 border-l-4 border-blue-500 p-2 rounded shadow-sm text-blue-900 animate-pulse";
                logItem.innerHTML = htmlContent;
                logBoard.insertBefore(logItem, logBoard.firstChild);

                // 💾 නොටිෆිකේෂන් එක LocalStorage එකට සේව් කිරීම (උපරිම 10ක්)
                let savedNotifs = JSON.parse(localStorage.getItem("activemq_notifs")) || [];
                savedNotifs.unshift(htmlContent);
                if(savedNotifs.length > 10) savedNotifs.pop();
                localStorage.setItem("activemq_notifs", JSON.stringify(savedNotifs));
            }
        }
        // 👉 අවස්ථාව B: සාමාන්‍ය ලයිව් STOCK UPDATE එකක් නම්
        else {
            const pid = responseData.productId;
            const stock = responseData.newStock;
            const stockElement = document.getElementById("stock-" + pid);
            if (stockElement) {
                stockElement.innerText = stock;
                stockElement.classList.add("text-rose-600", "scale-110");
                setTimeout(() => stockElement.classList.remove("text-rose-600", "scale-110"), 1000);
            }
        }
    };

    // 🎯 4. ලස්සන Tailwind Toast එකක් Screen එකේ පෙන්වන ෆන්ක්ෂන් එක
    function showLiveToast(message) {
        const container = document.getElementById("toast-container");
        const toast = document.createElement("div");
        toast.className = "bg-slate-900 text-white px-4 py-3 rounded-xl shadow-lg border border-slate-700 flex items-center gap-3 transition-all duration-300 transform translate-y-5 opacity-0";
        toast.innerHTML = `
            <span class="text-xl">🔔</span>
            <div class="text-xs">
                <p class="font-bold text-emerald-400">ActiveMQ Asynchronous Notification</p>
                <p class="text-slate-300 mt-0.5">${message}</p>
            </div>
        `;

        container.appendChild(toast);

        // Animation Trigger කිරීම
        setTimeout(() => {
            toast.classList.remove("translate-y-5", "opacity-0");
        }, 100);

        // තත්පර 4කින් මැකී යාම
        setTimeout(() => {
            toast.classList.add("opacity-0", "translate-y-2");
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }

    // 🎯 5. Button එක ක්ලික් කළාම Background එකෙන් Order එක Servlet එකට යැවීම (AJAX)
    function instantCheckout(productId) {
        const customerEmail = "kusal@gmail.com";
        const qty = 1;

        fetch("place-order?email=" + customerEmail + "&pid=" + productId + "&qty=" + qty)
            .then(response => {
                if(response.ok) {
                    console.log("Order request broadcasted for " + productId);
                } else {
                    alert("Order failed! Out of stock or invalid item.");
                }
            })
            .catch(error => console.error('Error handling sync:', error));
    }
</script>
</body>
</html>