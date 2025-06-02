document.addEventListener("DOMContentLoaded", () => {
  // --- Регистрация ---
  const registerForm = document.getElementById("registerForm");
  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const username = document.getElementById("username").value;
      const password = document.getElementById("password").value;
      const age = document.getElementById("age").value;
      const weight = document.getElementById("weight").value;
      const activityLevel = document.getElementById("activityLevel").value;

      try {
        const res = await fetch("http://localhost:8080/auth/register", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password, age, weight, activityLevel })
        });

        const text = await res.text();
        if (res.ok) {
          alert("Регистрация успешна!");
          window.location.href = "login.html";
        } else {
          alert("Ошибка: " + text);
        }
      } catch (err) {
        alert("Ошибка запроса");
        console.error(err);
      }
    });
  }

  // --- Вход ---
  const loginForm = document.getElementById("loginForm");
  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const username = document.getElementById("username").value;
      const password = document.getElementById("password").value;

      try {
        const res = await fetch("http://localhost:8080/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ username, password })
        });

        const data = await res.json();

        if (res.ok) {
          localStorage.setItem("jwtToken", data.token);
          localStorage.setItem("refreshToken", data.refreshToken);
          localStorage.setItem("username", username);
          window.location.href = "dashboard.html";
        } else {
          alert("Ошибка входа: " + (data.message || "Неверный логин/пароль"));
        }
      } catch (err) {
        alert("Ошибка сети");
        console.error(err);
      }
    });
  }

  // --- Личный кабинет ---
  if (window.location.pathname.includes("dashboard.html")) {
    const token = localStorage.getItem("jwtToken");
    const refreshToken = localStorage.getItem("refreshToken");
    const username = localStorage.getItem("username");

    if (!token || !username) {
      alert("Вы не авторизованы");
      window.location.href = "login.html";
      return;
    }

    async function fetchWithTokenRetry(url, options) {
      const res = await fetch(url, options);

      if (res.status === 401 && refreshToken) {
        // Попробовать обновить токен
        const refreshed = await fetch("http://localhost:8080/auth/refresh", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ refreshToken })
        });

        if (refreshed.ok) {
          const data = await refreshed.json();
          localStorage.setItem("jwtToken", data.token);

          // Повторить оригинальный запрос с новым токеном
          options.headers["Authorization"] = `Bearer ${data.token}`;
          return fetch(url, options);
        } else {
          alert("Сессия истекла, войдите заново");
          window.location.href = "login.html";
        }
      }

      return res;
    }

    // --- Загрузка данных пользователя ---
    fetchWithTokenRetry(`http://localhost:8080/auth/user/${username}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    })
      .then(res => res.ok ? res.json() : Promise.reject(res))
      .then(data => {
        document.getElementById("userInfo").innerHTML = `
          <h2>Привет, ${data.username}!</h2>
          <p><strong>Возраст:</strong> ${data.age}</p>
          <p><strong>Вес:</strong> ${data.weight}</p>
          <p><strong>Активность:</strong> ${data.activityLevel}</p>
          <button id="generateMenu">Сгенерировать рацион</button>
        `;

        // --- Генерация меню ---
        document.getElementById("generateMenu").addEventListener("click", async () => {
          const token = localStorage.getItem("jwtToken");

          const res = await fetchWithTokenRetry(`http://localhost:8080/mealplan/${data.age}/${data.weight}/${encodeURIComponent(data.activityLevel)}`, {
            method: "GET",
            headers: {
              "Authorization": `Bearer ${token}`,
              "Content-Type": "application/json"
            }
          });

          if (res.ok) {
            const mealPlan = await res.text();
            document.getElementById("nutritionInfo").innerHTML = `
              <h3>📋 Ваше меню:</h3>
              <pre>${mealPlan}</pre>
            `;
          } else {
            alert("Ошибка генерации рациона");
          }
        });
      })
      .catch(async err => {
        const msg = await err.text?.() || "Ошибка";
        alert(msg);
        window.location.href = "login.html";
      });
  }
});
