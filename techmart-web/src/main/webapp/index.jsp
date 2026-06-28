<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TechMart Enterprise - Login</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-slate-50 font-sans min-h-screen flex flex-col justify-center py-12 sm:px-6 lg:px-8 antialiased">

<div class="sm:mx-auto w-full max-w-md">
    <h1 class="text-center text-3xl font-extrabold text-slate-900 tracking-tight">
        TechMart <span class="text-emerald-600 font-normal text-xl">Enterprise</span>
    </h1>
    <p class="mt-2 text-center text-sm text-slate-600">
        Secure Session & Stateful Cart Modernization Portal
    </p>
</div>

<div class="mt-8 sm:mx-auto w-full max-w-md">
    <div class="bg-white py-8 px-4 shadow-sm border border-slate-200 sm:rounded-2xl sm:px-10">

        <%-- 🎯 මොකක් හරි වැරැද්දක් වුණොත් Error Message එකක් පෙන්වන්න --%>
        <% if (request.getParameter("error") != null) { %>
        <div class="mb-4 bg-rose-50 border-l-4 border-rose-500 p-3 rounded text-rose-900 text-xs font-medium animate-shake">
            ⚠️ <%= request.getParameter("error") %>
        </div>
        <% } %>

        <form class="space-y-6" action="login" method="POST">
            <div>
                <label for="email" class="block text-sm font-medium text-slate-700">Email Address</label>
                <div class="mt-1">
                    <input id="email" name="email" type="email" required value="kusal@gmail.com"
                           class="appearance-none block w-full px-3 py-2 border border-slate-300 rounded-xl shadow-sm placeholder-slate-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 text-sm">
                </div>
            </div>

            <div>
                <label for="password" class="block text-sm font-medium text-slate-700">Password</label>
                <div class="mt-1">
                    <input id="password" name="password" type="password" required value="1234"
                           class="appearance-none block w-full px-3 py-2 border border-slate-300 rounded-xl shadow-sm placeholder-slate-400 focus:outline-none focus:ring-emerald-500 focus:border-emerald-500 text-sm">
                </div>
            </div>

            <div>
                <button type="submit"
                        class="w-full flex justify-center py-3 px-4 border border-transparent rounded-xl shadow-sm text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-slate-500 transition-colors duration-150">
                    Sign In & Initialize Stateful Session
                </button>
            </div>
        </form>

        <div class="mt-6 border-t border-slate-100 pt-4 text-center">
            <span class="text-xs text-slate-400 font-mono">Viva Demo Mode: Active</span>
        </div>
    </div>
</div>

</body>
</html>