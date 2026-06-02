const container = document.querySelector(".container");
const regisBtn = document.querySelector(".register-btn");
const logBtn = document.querySelector(".login-btn");

regisBtn.addEventListener("click", () => container.classList.add("active"));
logBtn.addEventListener("click", () => container.classList.remove("active"));

const loginForm = document.querySelector(".form-box.login form");
const registerForm = document.querySelector(".form-box.register form");

loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = loginForm.querySelector("input[name='username']").value;
    const password = loginForm.querySelector("input[name='password']").value;

    try {
        const res = await fetch(`/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => null);
            alert(errData?.message || "Invalid username or password");
            return;
        }

        const data = await res.json();

        localStorage.setItem("token", data.token);
        localStorage.setItem("role", data.role);
        if (data.employeeId) {
            localStorage.setItem("employeeId", data.employeeId);
        } else if (data.guestId) {
            localStorage.setItem("guestId", data.guestId);
        }

        if (data.role === "GUEST") {
            window.location.href = "/guest";
        } else if (data.role === "EMPLOYEE") {
            window.location.href = "/homepage";
        } else if (data.role === "ADMIN") {
            window.location.href = "/admin";
        } else {
            window.location.href = "/";
        }
    } catch (err) {
        console.error(err);
        alert("Network error!");
    }
});

registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = registerForm.querySelector("input[name='username']").value;
    const email = registerForm.querySelector("input[name='email']").value;
    const password = registerForm.querySelector("input[name='password']").value;

    try {
        const res = await fetch(`/api/auth/register/guest`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                username,
                email,
                password,
            })
        });

        if (!res.ok) {
            const message = await res.text();
            alert(message || "Registration error");
            return;
        }

        const data = await res.json();
        if (data.guestId) localStorage.setItem("guestId", data.guestId);
        if (data.token) localStorage.setItem("token", data.token);
        if (data.role) localStorage.setItem("role", data.role);
        window.location.href = "/guest";

    } catch (err) {
        console.error(err);
        alert("Network error!");
    }
});
