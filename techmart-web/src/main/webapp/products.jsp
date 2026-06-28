<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="lk.techmart.core.DTO.InventoryDTO" %>
<%@ page import="lk.techmart.core.DTO.CartItemDTO" %>
<%
    // 1. Servlet එකෙන් එවපු Products ලැයිස්තුව කියවීම
    List<InventoryDTO> products = (List<InventoryDTO>) request.getAttribute("products");

    // 2. Auth සෙෂන් එකෙන් එවපු යූසර්ගේ නම කියවීම
    String userName = (String) request.getAttribute("loggedInUserName");

    // 3. Stateful Bean එකෙන් ආපු Cart Items ලැයිස්තුව කියවීම
    List<CartItemDTO> cartItems = (List<CartItemDTO>) request.getAttribute("userCartItems");

    // 4. Grand Total එක ගණන් හදාගැනීම
    double grandTotal = 0.0;
    if (cartItems != null) {
        for (CartItemDTO item : cartItems) {
            grandTotal += item.getTotalLinePrice();
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TechMart - Products & Stateful Cart</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 font-sans min-h-screen antialiased">

<%-- 🎯 NAVIGATION BAR (Personalized User Session) --%>
<nav class="bg-white shadow-sm border-b border-slate-200 sticky top-0 z-40">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
        <h1 class="text-xl font-bold text-slate-800 tracking-tight">TechMart <span class="text-sm font-normal text-emerald-600">Enterprise</span></h1>

        <div class="flex items-center space-x-4">
            <%-- 👤 ලොග් වෙලා ඉන්න යූසර්ගේ නම පෙන්වීම --%>
            <span class="text-xs font-medium text-slate-700 bg-slate-100 px-3 py-1.5 rounded-xl border border-slate-200">
                👤 User: <span class="text-emerald-600 font-bold"><%= (userName != null) ? userName : "Guest" %></span>
            </span>
        </div>
    </div>
</nav>

<%-- 🎯 MAIN LAYOUT (Grid - වමේ බඩු 20, දකුණේ Stateful Cart එක) --%>
<main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-10 grid grid-cols-1 lg:grid-cols-4 gap-8">

    <%-- LEFT 3 COLUMNS: Products List --%>
    <div class="lg:col-span-3 space-y-6">
        <div>
            <h2 class="text-3xl font-extrabold text-slate-900 tracking-tight">Available Products</h2>
            <p class="mt-2 text-sm text-slate-600">Select items to add to your stateful shopping session.</p>
        </div>

        <%-- Products Grid (බඩු 20 ලූප් වෙන තැන) --%>
        <div class="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-6">
            <%
                if (products != null && !products.isEmpty()) {
                    for (InventoryDTO item : products) {
            %>
            <div class="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm transition-all duration-300 hover:shadow-md flex flex-col justify-between">
                <div>
                    <div class="flex items-center justify-between mb-3">
                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-md text-xs font-medium bg-slate-100 text-slate-800">
                            📦 In Stock
                        </span>
                        <span class="text-sm font-black text-slate-900">$<%= item.getUnitPrice() %></span>
                    </div>

                    <h3 class="text-base font-bold text-slate-800 mb-1 leading-snug h-12 overflow-hidden"><%= item.getProduct_name() %></h3>
                    <p class="text-[11px] font-mono text-slate-400 mb-4">Code: <%= item.getProductId() %></p>

                    <div class="bg-slate-50 border border-slate-100 rounded-xl p-3 mb-4">
                        <div class="flex justify-between items-center">
                            <span class="text-xs text-slate-500 font-medium">Available Stock:</span>
                            <span class="text-sm font-bold text-slate-800">
                                <%= item.getStockQuantity() %>
                            </span>
                        </div>
                    </div>
                </div>

                <div>
                    <button onclick="addToCart('<%= item.getProductId() %>', '<%= item.getUnitPrice() %>')"
                            class="w-full block text-center py-2.5 px-4 bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold rounded-xl shadow-sm transition-colors">
                        🛒 Add to Stateful Cart
                    </button>
                </div>
            </div>
            <%
                }
            } else {
            %>
            <div class="col-span-full bg-white rounded-2xl border border-dashed border-slate-300 p-12 text-center">
                <span class="text-4xl mb-4 block">📦</span>
                <h3 class="text-sm font-medium text-slate-900">No products available</h3>
                <p class="mt-1 text-sm text-slate-500">කරුණාකර ProductListServlet එක හරහා මේ පිටුවට පිවිසෙන්න.</p>
            </div>
            <%
                }
            %>
        </div>
    </div>

    <%-- RIGHT 1 COLUMN: STATEFUL SHOPPING CART SIDEBAR --%>
    <div class="lg:col-span-1">
        <div class="bg-white border border-slate-200 rounded-2xl p-5 shadow-sm sticky top-24">
            <div class="flex justify-between items-center border-b border-slate-100 pb-3 mb-4">
                <h3 class="font-bold text-slate-900 flex items-center gap-2 text-sm">
                    <span>🛒</span> Stateful Cart
                </h3>
                <a href="${pageContext.request.contextPath}/cart-action?action=clear" class="text-[11px] text-rose-500 hover:underline font-medium">Clear All</a>
            </div>

            <%-- 🎯 Cart Items List එක Render කිරීම --%>
            <%
                if (cartItems != null && !cartItems.isEmpty()) {
            %>
            <div class="space-y-2 max-h-80 overflow-y-auto pr-1 flex flex-col">
                <%
                    for (CartItemDTO cartItem : cartItems) {
                %>
                <div class="bg-slate-50 border border-slate-200 rounded-xl p-3 flex justify-between items-center text-xs">
                    <div>
                        <p class="font-bold text-slate-800"><%= cartItem.getProductId() %></p>
                        <p class="text-slate-400 mt-0.5">Qty: <span class="font-semibold text-slate-700"><%= cartItem.getQuantity() %></span></p>
                    </div>
                    <div class="text-right flex items-center gap-2">
                        <span class="font-bold text-slate-900">$<%= cartItem.getTotalLinePrice() %></span>
                        <a href="${pageContext.request.contextPath}/cart-action?action=remove&pid=<%= cartItem.getProductId() %>"
                           class="text-rose-500 hover:text-rose-700 font-bold text-xs ml-1">✕</a>
                    </div>
                </div>
                <%
                    }
                %>
            </div>

            <%-- Grand Total Display --%>
            <div class="border-t border-slate-100 pt-4 mt-4 space-y-3">
                <div class="flex justify-between items-center text-xs">
                    <span class="font-medium text-slate-500">Grand Total:</span>
                    <span class="text-lg font-black text-slate-900">$<%= grandTotal %></span>
                </div>
                <%-- Mock Checkout Route --%>
                <a href="${pageContext.request.contextPath}/cart-action?action=checkout"
                   class="w-full block text-center py-2.5 px-4 bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold rounded-xl shadow-md transition-colors">
                    Proceed to Checkout
                </a>
            </div>
            <%
            } else {
            %>
            <div class="py-12 text-center text-slate-400 text-xs">
                <span class="text-2xl block mb-2">🛒</span>
                Your cart is empty.
            </div>
            <%
                }
            %>
        </div>
    </div>
</main>

<script>
    function addToCart(productId, price) {
        // 📡 පේජ් එක රිෆ්‍රෙෂ් වෙන්නේ නැතුව බැක්ග්‍රවුන්ඩ් එකෙන් සර්ව්ලට් එකට රික්වෙස්ට් එක යවනවා
        fetch("${pageContext.request.contextPath}/cart-action?action=add&pid=" + productId + "&price=" + price)
            .then(response => {
                if (response.ok) {
                    // ✅ සාර්ථක නම්, පේජ් එක රිෆ්‍රෙෂ් කරන්නේ නැතුව කාර්ට් සයිඩ්බාර් එක විතරක් අප්ඩේට් කරන්න
                    // දැනට ලේසිම ක්‍රමය විදිහට සයිඩ්බාර් එකේ ඩේටා ටික විතරක් ලෝඩ් කරගන්න මෙතනින් පේජ් එක reload කරනවා වෙනුවට
                    // මුළු පිටුවම reload නොවී, location.reload() එකක් දැම්මත් UI එක සැනින් මාරු වෙනවා.
                    // වඩාත්ම නිවැරදි ක්‍රමය තමයි මුළු පේජ් එකම රිෆ්‍රෙෂ් නොකර සයිඩ්බාර් එකේ HTML එක විතරක් අප්ඩේට් කරන එක:
                    location.reload(); // මේකෙන් මුළු බ්‍රවුසර් එකම සුදු වෙලා ලෝඩ් වෙන්නේ නැහැ, සැනින් අප්ඩේට් වෙනවා
                } else {
                    alert("❌ Cannot add to cart! Out of stock.");
                }
            })
            .catch(error => console.error('Error adding to cart:', error));
    }
</script>

</body>
</html>