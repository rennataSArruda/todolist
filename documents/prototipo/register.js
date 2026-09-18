
(function () {
  const form = document.getElementById("registerForm");
  if (!form) return;

  const password = document.getElementById("registerPassword");
  const confirmPassword = document.getElementById("registerConfirmPassword");
  const message = document.getElementById("registerMessage");
  const ruleLength = document.getElementById("registerRuleLength");
  const ruleMatch = document.getElementById("registerRuleMatch");

  function showMessage(type, text) {
    message.className = "auth-message show " + type;
    message.textContent = text;
  }

  function refreshRules() {
    ruleLength.classList.toggle("valid", password.value.length >= 8);
    ruleMatch.classList.toggle("valid", password.value.length > 0 && password.value === confirmPassword.value);
  }

  password.addEventListener("input", refreshRules);
  confirmPassword.addEventListener("input", refreshRules);

  form.addEventListener("submit", function (event) {
    event.preventDefault();

    const username = document.getElementById("registerUsername").value.trim();
    const email = document.getElementById("registerEmail").value.trim();

    if (!username || !email || !password.value) {
      showMessage("error", "Preencha username, email e senha.");
      return;
    }

    if (password.value.length < 8) {
      showMessage("error", "A senha deve possuir pelo menos 8 caracteres.");
      return;
    }

    if (password.value !== confirmPassword.value) {
      showMessage("error", "A confirmação da senha deve ser igual à senha.");
      return;
    }

    // Protótipo: representa POST /public/auth/register.
    sessionStorage.setItem("focusly-login-message", "Conta criada com sucesso. Faça login para continuar.");
    window.location.href = "login.html?registered=success";
  });
})();
