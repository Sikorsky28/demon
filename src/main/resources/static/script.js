document.addEventListener("DOMContentLoaded", () => {
    // ✅ Регистрация пользователя
    const registerForm = document.getElementById("registerForm");
    if (registerForm) {
        registerForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;

            if (!username || !password) {
                alert("Введите имя пользователя и пароль!");
                return;
            }

            try {
                const response = await fetch("http://localhost:8080/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password })
                });

                const resultText = await response.text();
                console.log("Ответ сервера:", resultText);

                if (response.ok) {
                    alert("Регистрация успешна!");
                    window.location.href = "login.html";
                } else {
                    alert("Ошибка регистрации: " + resultText);
                }
            } catch (error) {
                console.error("Ошибка регистрации:", error);
                alert("Произошла ошибка, попробуйте позже.");
            }
        });
    }

    // ✅ Вход пользователя
    const loginForm = document.getElementById("loginForm");
    if (loginForm) {
        loginForm.addEventListener("submit", async (event) => {
            event.preventDefault();

            const username = document.getElementById("loginUsername").value;
            const password = document.getElementById("loginPassword").value;

            if (!username || !password) {
                alert("Введите логин и пароль!");
                return;
            }

            try {
                const response = await fetch("http://localhost:8080/auth/login", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ username, password })
                });

                const resultText = await response.text();
                console.log("Ответ сервера:", resultText);

                try {
                    const result = JSON.parse(resultText);
                    if (response.ok) {
                        localStorage.setItem("username", username);
                        localStorage.setItem("token", result.token);
                        window.location.href = "dashboard.html";
                    } else {
                        alert("Ошибка входа: " + result.message);
                    }
                } catch (error) {
                    console.error("Ошибка JSON:", error);
                    alert("Ошибка входа: " + resultText);
                }
            } catch (error) {
                console.error("Ошибка запроса:", error);
                alert("Ошибка входа, попробуйте снова.");
            }
        });
    }

    // ✅ Загрузка данных пользователя и рациона в `dashboard.html`
    if (window.location.pathname.includes("dashboard.html")) {
        const username = localStorage.getItem("username");
        const token = localStorage.getItem("token");

        if (!username || !token) {
            alert("Ошибка: требуется авторизация!");
            window.location.href = "login.html";
            return;
        }

        // ✅ Получаем данные пользователя
        fetch(`http://localhost:8080/auth/user/${encodeURIComponent(username)}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(userData => {
            if (userData.username) {
                document.getElementById("userInfo").innerHTML = `
                    <h2>Добро пожаловать, ${userData.username}!</h2>
                    <p>Возраст: ${userData.age}</p>
                    <p>Вес: ${userData.weight} кг</p>
                    <p>Уровень активности: ${userData.activityLevel}</p>
                    <button id="generateMenu">Сгенерировать меню на неделю</button>
                `;

                // ✅ Генерация рациона при нажатии кнопки
                document.getElementById("generateMenu").addEventListener("click", () => {
                    fetch(`http://localhost:8080/mealplan/${userData.age}/${userData.weight}/${userData.activityLevel}`)
                        .then(response => response.text())
                        .then(mealPlan => {
                            document.getElementById("nutritionInfo").innerHTML = `
                                <h2>Ваше меню на неделю:</h2>
                                <p>${mealPlan}</p>
                            `;
                        })
                        .catch(error => {
                            console.error("Ошибка загрузки рациона:", error);
                            alert("Ошибка загрузки рациона, попробуйте позже.");
                        });
                });
            } else {
                alert("Ошибка загрузки данных пользователя!");
            }
        })
        .catch(async error => {
            console.error("Ошибка загрузки данных:", error);
            const responseText = await error.response?.text();
            alert(`Ошибка загрузки данных: ${responseText || "Попробуйте снова."}`);
        });
    }
});
