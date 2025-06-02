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
          localStorage.setItem("username", username);
          window.location.href = "dashboard.html";
        } else {
          alert("Ошибка входа: " + data.message);
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
    const username = localStorage.getItem("username");

    if (!token || !username) {
      alert("Вы не авторизованы");
      window.location.href = "login.html";
      return;
    }

    fetch(`http://localhost:8080/auth/user/${encodeURIComponent(username)}`, {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`,
        "Content-Type": "application/json"
      }
    })
      .then(res => res.ok ? res.json() : Promise.reject(res))
      .then(userData => {
        document.getElementById("userInfo").innerHTML = `
          <h2>Привет, ${userData.username}!</h2>
          <p>Возраст: ${userData.age}</p>
          <p>Вес: ${userData.weight}</p>
          <p>Активность: ${userData.activityLevel}</p>
        `;

        // Обработка кнопки генерации меню
        const generateBtn = document.getElementById("generateMenu");
        if (generateBtn) {
          generateBtn.addEventListener("click", async () => {
            try {
              const mealResponse = await fetch(`http://localhost:8080/mealplan/${userData.age}/${userData.weight}/${userData.activityLevel}`, {
                method: "GET",
                headers: {
                  "Authorization": `Bearer ${token}`
                }
              });

              if (mealResponse.ok) {
                const mealPlan = await mealResponse.text();
                document.getElementById("nutritionInfo").innerHTML = `
                  <h3>Ваш рацион на неделю:</h3>
                  <pre style="white-space: pre-wrap; background-color: #f4f4f4; padding: 1em; border-radius: 6px;">${mealPlan}</pre>
                `;
              } else {
                const errText = await mealResponse.text();
                alert("Ошибка генерации рациона: " + errText);
              }
            } catch (err) {
              console.error("Ошибка генерации рациона:", err);
              alert("Ошибка при получении рациона.");
            }
          });
        }
      })
      .catch(async err => {
        const msg = await err.text?.() || "Ошибка";
        alert(msg);
        window.location.href = "login.html";
      });
  }
});
