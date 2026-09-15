
(function () {
  const root = document.documentElement;
  const storageKey = "focusly-theme";

  function apply(theme) {
    const resolved = theme === "dark" ? "dark" : "light";
    root.setAttribute("data-theme", resolved);
    localStorage.setItem(storageKey, resolved);
    document.querySelectorAll("[data-theme-icon]").forEach(el => {
      el.textContent = resolved === "dark" ? "☀" : "☾";
    });
    document.querySelectorAll("[data-theme-text]").forEach(el => {
      el.textContent = resolved === "dark" ? "Modo claro" : "Modo escuro";
    });
    document.querySelectorAll('input[name="theme"]').forEach(input => {
      input.checked = input.value === resolved;
    });
  }

  const saved = localStorage.getItem(storageKey);
  const systemDark = window.matchMedia && window.matchMedia("(prefers-color-scheme: dark)").matches;
  apply(saved || (systemDark ? "dark" : "light"));

  document.addEventListener("click", function (event) {
    const toggle = event.target.closest("[data-theme-toggle]");
    if (!toggle) return;
    const current = root.getAttribute("data-theme") || "light";
    apply(current === "dark" ? "light" : "dark");
  });

  document.addEventListener("change", function (event) {
    if (event.target.matches('input[name="theme"]')) {
      apply(event.target.value);
    }
  });
})();
