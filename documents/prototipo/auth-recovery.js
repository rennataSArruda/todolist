
(function () {
  function byId(id) {
    return document.getElementById(id);
  }

  function showMessage(el, type, text) {
    if (!el) return;
    el.className = "auth-message show " + type;
    el.textContent = text;
  }

  function getToken() {
    return new URLSearchParams(window.location.search).get("token");
  }

  function setupForgotPassword() {
    const form = byId("forgotPasswordForm");
    if (!form) return;

    form.addEventListener("submit", function (event) {
      event.preventDefault();

      const identifier = byId("identifier");
      const message = byId("forgotPasswordMessage");

      if (!identifier || !identifier.value.trim()) {
        showMessage(message, "error", "Informe seu username ou email.");
        return;
      }

      // Protótipo: representa POST /public/auth/forgot-password -> 204 No Content.
      showMessage(
        message,
        "success",
        "Se os dados estiverem corretos, enviaremos as instruções para redefinir sua senha."
      );
    });
  }

  function setupResetPassword() {
    const form = byId("resetPasswordForm");
    if (!form) return;

    const token = getToken();
    const tokenMessage = byId("resetTokenMessage");
    const submit = form.querySelector("button[type='submit']");
    const password = byId("newPassword");
    const confirm = byId("confirmPassword");
    const ruleLength = byId("ruleLength");
    const ruleMatch = byId("ruleMatch");
    const message = byId("resetPasswordMessage");

    if (!token) {
      showMessage(
        tokenMessage,
        "error",
        "O link de redefinição é inválido ou expirou. Solicite uma nova recuperação de senha."
      );
      submit.disabled = true;
    }

    function refreshRules() {
      const lengthOk = password && password.value.length >= 8;
      const matchOk = password && confirm && password.value.length > 0 && password.value === confirm.value;

      if (ruleLength) ruleLength.classList.toggle("valid", !!lengthOk);
      if (ruleMatch) ruleMatch.classList.toggle("valid", !!matchOk);
    }

    if (password) password.addEventListener("input", refreshRules);
    if (confirm) confirm.addEventListener("input", refreshRules);

    form.addEventListener("submit", function (event) {
      event.preventDefault();

      if (!token) {
        showMessage(
          message,
          "error",
          "O link de redefinição é inválido ou expirou. Solicite uma nova recuperação de senha."
        );
        return;
      }

      if (!password.value) {
        showMessage(message, "error", "Informe a nova senha.");
        return;
      }

      if (password.value.length < 8) {
        showMessage(message, "error", "A nova senha deve possuir pelo menos 8 caracteres.");
        return;
      }

      if (password.value !== confirm.value) {
        showMessage(message, "error", "A confirmação da senha deve ser igual à nova senha.");
        return;
      }

      // Protótipo: representa POST /public/auth/reset-password -> 204 No Content.
      sessionStorage.setItem(
        "focusly-login-message",
        "Senha redefinida com sucesso. Faça login com sua nova senha."
      );

      window.location.href = "login.html?passwordReset=success";
    });
  }

  function setupLoginFeedback() {
    const target = byId("loginFeedback");
    if (!target) return;

    const params = new URLSearchParams(window.location.search);
    const message = sessionStorage.getItem("focusly-login-message");

    if (params.get("passwordReset") === "success" || message) {
      target.textContent = message || "Senha redefinida com sucesso. Faça login com sua nova senha.";
      target.hidden = false;
      sessionStorage.removeItem("focusly-login-message");
    }
  }

  function setupPasswordToggles() {
    document.querySelectorAll("[data-password-toggle]").forEach(function (button) {
      button.addEventListener("click", function () {
        const input = byId(button.getAttribute("data-password-toggle"));
        if (!input) return;
        input.type = input.type === "password" ? "text" : "password";
        button.textContent = input.type === "password" ? "◉" : "○";
      });
    });
  }

  setupForgotPassword();
  setupResetPassword();
  setupLoginFeedback();
  setupPasswordToggles();
})();
