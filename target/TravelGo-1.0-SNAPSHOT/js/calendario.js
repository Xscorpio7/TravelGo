const dateBtn = document.getElementById("datePickerBtn");
const calendarContainer = document.getElementById("calendarContainer");

let selectedRange = { from: null, to: null };

dateBtn.addEventListener("click", () => {
  calendarContainer.classList.toggle("hidden");
  renderCalendars();
});

function renderCalendars() {
  calendarContainer.innerHTML = "";

  const currentMonth = new Date();
  const nextMonth = new Date(currentMonth);
  nextMonth.setMonth(currentMonth.getMonth() + 1);

  calendarContainer.appendChild(renderCalendar(currentMonth));
  calendarContainer.appendChild(renderCalendar(nextMonth));
}

function renderCalendar(date) {
  const monthEl = document.createElement("div");
  monthEl.className = "calendar-month";

  const monthName = date.toLocaleString("es-ES", { month: "long", year: "numeric" });
  const title = document.createElement("div");
  title.textContent = monthName;
  monthEl.appendChild(title);

  const daysGrid = document.createElement("div");
  daysGrid.className = "calendar-days";

  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
  const startDay = firstDay.getDay(); 

  
  for (let i = 0; i < startDay; i++) {
    daysGrid.appendChild(document.createElement("div"));
  }
  for (let d = 1; d <= lastDay.getDate(); d++) {
    const dayBtn = document.createElement("div");
    dayBtn.className = "calendar-day";
    dayBtn.textContent = d;
  
    const fullDate = new Date(date.getFullYear(), date.getMonth(), d);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
  
    if (fullDate < today) {
      dayBtn.classList.add("disabled");
      dayBtn.style.pointerEvents = "none";
      dayBtn.style.opacity = "0.3";
    } else {
      dayBtn.addEventListener("click", () => selectDate(fullDate));
    }
  
  
    if (selectedRange.from && selectedRange.to) {
      if (fullDate >= selectedRange.from && fullDate <= selectedRange.to) {
        dayBtn.classList.add("selected");
      }
    } else if (
      selectedRange.from &&
      fullDate.toDateString() === selectedRange.from.toDateString()
    ) {
      dayBtn.classList.add("selected");
    }
  
    daysGrid.appendChild(dayBtn);
  }

  monthEl.appendChild(daysGrid);
  return monthEl;
}

function selectDate(date) {
  if (!selectedRange.from || (selectedRange.from && selectedRange.to)) {
    selectedRange.from = date;
    selectedRange.to = null;
  } else if (date < selectedRange.from) {
    selectedRange.to = selectedRange.from;
    selectedRange.from = date;
  } else {
    selectedRange.to = date;
  }

  updateButtonLabel();
  renderCalendars();
}

function updateButtonLabel() {
  const { from, to } = selectedRange;
  if (from && to) {
    const options = { day: "2-digit", month: "short", year: "numeric" };
    const fromStr = from.toLocaleDateString("es-ES", options);
    const toStr = to.toLocaleDateString("es-ES", options);
    dateBtn.textContent = `${fromStr} - ${toStr}`;
  } else if (from) {
    dateBtn.textContent = from.toLocaleDateString("es-ES", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  } else {
    dateBtn.textContent = "Seleccionar fechas";
  }
}
document.addEventListener("DOMContentLoaded", () => {
  const today = new Date();
  selectedRange.from = today;
  updateButtonLabel();
});