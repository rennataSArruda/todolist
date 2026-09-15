
(function () {
  let dragged = null;

  function updateCounts() {
    document.querySelectorAll(".kanban-column").forEach(column => {
      const count = column.querySelectorAll(".kanban-card").length;
      const counter = column.querySelector("[data-kanban-count]");
      if (counter) counter.textContent = String(count);
    });
  }

  document.querySelectorAll(".kanban-card").forEach(card => {
    card.addEventListener("dragstart", function () {
      dragged = card;
      card.classList.add("dragging");
    });

    card.addEventListener("dragend", function () {
      card.classList.remove("dragging");
      document.querySelectorAll(".kanban-column").forEach(c => c.classList.remove("drag-over"));
      dragged = null;
      updateCounts();
    });
  });

  document.querySelectorAll(".kanban-column").forEach(column => {
    column.addEventListener("dragover", function (event) {
      event.preventDefault();
      column.classList.add("drag-over");
    });

    column.addEventListener("dragleave", function (event) {
      if (!column.contains(event.relatedTarget)) {
        column.classList.remove("drag-over");
      }
    });

    column.addEventListener("drop", function (event) {
      event.preventDefault();
      column.classList.remove("drag-over");
      if (!dragged) return;

      const list = column.querySelector(".kanban-list");
      list.appendChild(dragged);
      dragged.dataset.status = column.dataset.status || "";
      updateCounts();
    });
  });

  updateCounts();
})();
