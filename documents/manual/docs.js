(function () {
  const root = document.documentElement;
  const themeKey = "focusly-docs-theme";
  const posKey = "focusly-docs-theme-fab-position";

  function applyTheme(theme) {
    const next = theme === "dark" ? "dark" : "light";
    root.dataset.theme = next;
    localStorage.setItem(themeKey, next);
    document.querySelectorAll("[data-theme-icon]").forEach((el) => {
      el.textContent = next === "dark" ? "☀" : "☾";
    });
  }

  const savedTheme = localStorage.getItem(themeKey);
  const prefersDark =
    window.matchMedia &&
    window.matchMedia("(prefers-color-scheme: dark)").matches;
  applyTheme(savedTheme || (prefersDark ? "dark" : "light"));

  document.querySelectorAll("[data-copy]").forEach((button) => {
    button.addEventListener("click", async () => {
      const id = button.getAttribute("data-copy");
      const target = document.getElementById(id);
      if (!target) return;
      await navigator.clipboard.writeText(target.innerText);
      const old = button.textContent;
      button.textContent = "Copiado";
      setTimeout(() => (button.textContent = old), 1000);
    });
  });

  const menu = document.querySelector("[data-mobile-menu]");
  if (menu) {
    menu.addEventListener("click", () =>
      document.body.classList.toggle("sidebar-open"),
    );
  }
  document.querySelectorAll(".nav a").forEach((a) => {
    a.addEventListener("click", () =>
      document.body.classList.remove("sidebar-open"),
    );
  });

  const search = document.querySelector("[data-doc-search]");
  if (search) {
    search.addEventListener("input", () => {
      const q = search.value.toLowerCase().trim();
      document.querySelectorAll("[data-searchable]").forEach((item) => {
        item.style.display =
          !q || item.textContent.toLowerCase().includes(q) ? "" : "none";
      });
    });
  }

  const fab = document.createElement("button");
  fab.type = "button";
  fab.className = "theme-fab";
  fab.title = "Alternar tema";
  fab.setAttribute("aria-label", "Alternar tema");
  fab.innerHTML = "<span data-theme-icon></span>";
  document.body.appendChild(fab);
  applyTheme(root.dataset.theme);

  let state = {
    dragging: false,
    moved: false,
    startX: 0,
    startY: 0,
    originX: 0,
    originY: 0,
  };

  function viewportBounds() {
    const rect = fab.getBoundingClientRect();
    const margin = window.innerWidth <= 760 ? 14 : 22;
    return {
      margin,
      maxX: Math.max(margin, window.innerWidth - rect.width - margin),
      maxY: Math.max(margin, window.innerHeight - rect.height - margin),
    };
  }

  function savePosition() {
    const rect = fab.getBoundingClientRect();
    const bounds = viewportBounds();
    const center = rect.left + rect.width / 2;
    const side = center < window.innerWidth / 2 ? "left" : "right";
    const usable = Math.max(1, bounds.maxY - bounds.margin);
    const y = Math.min(1, Math.max(0, (rect.top - bounds.margin) / usable));
    localStorage.setItem(posKey, JSON.stringify({ side, y }));
  }

  function restorePosition() {
    const raw = localStorage.getItem(posKey);
    if (!raw) return;
    try {
      const saved = JSON.parse(raw);
      const bounds = viewportBounds();
      const y =
        bounds.margin +
        Math.min(1, Math.max(0, Number(saved.y) || 0)) *
          (bounds.maxY - bounds.margin);
      fab.style.top = `${y}px`;
      fab.style.bottom = "auto";
      if (saved.side === "left") {
        fab.style.left = `${bounds.margin}px`;
        fab.style.right = "auto";
      } else {
        fab.style.right = `${bounds.margin}px`;
        fab.style.left = "auto";
      }
    } catch (_) {}
  }

  requestAnimationFrame(restorePosition);

  fab.addEventListener("pointerdown", (e) => {
    const rect = fab.getBoundingClientRect();
    state = {
      dragging: true,
      moved: false,
      startX: e.clientX,
      startY: e.clientY,
      originX: rect.left,
      originY: rect.top,
    };
    fab.setPointerCapture(e.pointerId);
  });

  fab.addEventListener("pointermove", (e) => {
    if (!state.dragging) return;
    const dx = e.clientX - state.startX;
    const dy = e.clientY - state.startY;
    if (Math.hypot(dx, dy) > 6) state.moved = true;
    if (!state.moved) return;
    const bounds = viewportBounds();
    const x = Math.min(
      bounds.maxX,
      Math.max(bounds.margin, state.originX + dx),
    );
    const y = Math.min(
      bounds.maxY,
      Math.max(bounds.margin, state.originY + dy),
    );
    fab.classList.add("dragging");
    fab.style.left = `${x}px`;
    fab.style.top = `${y}px`;
    fab.style.right = "auto";
    fab.style.bottom = "auto";
  });

  fab.addEventListener("pointerup", (e) => {
    if (!state.dragging) return;
    state.dragging = false;
    fab.releasePointerCapture(e.pointerId);
    fab.classList.remove("dragging");
    if (!state.moved) {
      applyTheme(root.dataset.theme === "dark" ? "light" : "dark");
      return;
    }
    const rect = fab.getBoundingClientRect();
    const bounds = viewportBounds();
    const side =
      rect.left + rect.width / 2 < window.innerWidth / 2 ? "left" : "right";
    if (side === "left") {
      fab.style.left = `${bounds.margin}px`;
      fab.style.right = "auto";
    } else {
      fab.style.right = `${bounds.margin}px`;
      fab.style.left = "auto";
    }
    savePosition();
  });

  fab.addEventListener("keydown", (e) => {
    if (e.key === "Enter" || e.key === " ") {
      e.preventDefault();
      applyTheme(root.dataset.theme === "dark" ? "light" : "dark");
    }
  });

  window.addEventListener("resize", restorePosition);
})();
