/* =============================================
   TourKG — Custom JavaScript
   ============================================= */

// ── AUTH MODAL ────────────────────────────────
// Открыть модал программно (вместо data-bs-toggle)
function openAuthModal(tab) {
  const modalEl = document.getElementById('authModal');
  if (!modalEl) return;
  const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
  showAuthTab(tab);
  modal.show();
}

function showAuthTab(tab) {
  const loginPanel    = document.getElementById('tabLogin');
  const registerPanel = document.getElementById('tabRegister');
  const loginBtn      = document.getElementById('tabLoginBtn');
  const registerBtn   = document.getElementById('tabRegisterBtn');
  if (!loginPanel) return;

  if (tab === 'login') {
    loginPanel.style.display = 'block';
    registerPanel.style.display = 'none';
    if (loginBtn)    { loginBtn.classList.add('active');    loginBtn.removeAttribute('style'); }
    if (registerBtn) { registerBtn.classList.remove('active'); registerBtn.removeAttribute('style'); }
  } else {
    loginPanel.style.display = 'none';
    registerPanel.style.display = 'block';
    if (loginBtn)    { loginBtn.classList.remove('active'); loginBtn.removeAttribute('style'); }
    if (registerBtn) { registerBtn.classList.add('active');    registerBtn.removeAttribute('style'); }
  }
  clearAuthAlerts();
}
window.showTab = showAuthTab;

function tkgText(key, fallback) {
  if (window.TKG_I18N) {
    return window.TKG_I18N.translate(key, window.TKG_I18N.getLang()) || fallback;
  }
  return fallback;
}

function clearAuthAlerts() {
  ['authAlert', 'authSuccess'].forEach(id => {
    const el = document.getElementById(id);
    if (el) { el.style.display = 'none'; el.textContent = ''; }
  });
}

function showAuthMessage(msg, type) {
  const id = type === 'success' ? 'authSuccess' : 'authAlert';
  const el = document.getElementById(id);
  if (!el) return;
  el.textContent = msg;
  el.style.display = 'flex';
}

// ── DOM READY ─────────────────────────────────
document.addEventListener('DOMContentLoaded', function () {

  // LOGIN (AJAX)
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async function (e) {
      e.preventDefault();
      const btn = document.getElementById('loginSubmitBtn');
      const orig = btn ? btn.textContent : '';
      if (btn) { btn.textContent = tkgText('auth.loggingIn', 'Входим...'); btn.disabled = true; }
      clearAuthAlerts();

      try {
        const params = new URLSearchParams(new FormData(loginForm));
        const resp = await fetch('/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params.toString(),
          redirect: 'follow'
        });
        if (resp.url && (resp.url.includes('?error') || resp.url.endsWith('/login'))) {
          showAuthMessage(tkgText('auth.invalidLogin', 'Неверный email или пароль'), 'error');
        } else {
          window.location.href = resp.url || '/';
        }
      } catch (_) {
        showAuthMessage(tkgText('auth.connectionErrorRetry', 'Ошибка соединения. Попробуйте ещё раз.'), 'error');
      }
      if (btn) { btn.textContent = orig; btn.disabled = false; }
    });
  }

  // REGISTER (AJAX)
  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
    registerForm.addEventListener('submit', async function (e) {
      e.preventDefault();
      const btn = document.getElementById('registerSubmitBtn');
      const orig = btn ? btn.textContent : '';
      if (btn) { btn.textContent = tkgText('auth.registering', 'Регистрируем...'); btn.disabled = true; }
      clearAuthAlerts();

      try {
        const params = new URLSearchParams(new FormData(registerForm));
        const resp = await fetch('/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params.toString()
        });
        const data = await resp.json().catch(() => ({}));
        if (data.status === 'ok') {
          showAuthMessage(tkgText('auth.registerSuccess', 'Аккаунт создан! Теперь войдите.'), 'success');
          registerForm.reset();
          setTimeout(() => showAuthTab('login'), 1400);
        } else {
          showAuthMessage(data.error || tkgText('auth.registerError', 'Ошибка регистрации'), 'error');
        }
      } catch (_) {
        showAuthMessage(tkgText('auth.connectionError', 'Ошибка соединения'), 'error');
      }
      if (btn) { btn.textContent = orig; btn.disabled = false; }
    });
  }

  // BOOKING WIZARD — initial summary
  updateWizardSummary();

  // DRAG & DROP на фото-зонах
  document.querySelectorAll('.photo-upload-zone').forEach(zone => {
    zone.addEventListener('dragover',  e => { e.preventDefault(); zone.classList.add('drag-over'); });
    zone.addEventListener('dragleave', () => zone.classList.remove('drag-over'));
    zone.addEventListener('drop', e => {
      e.preventDefault();
      zone.classList.remove('drag-over');
      const file = e.dataTransfer.files[0];
      if (!file) return;
      const input = zone.nextElementSibling;
      if (input && input.type === 'file') {
        const dt = new DataTransfer();
        dt.items.add(file);
        input.files = dt.files;
        input.dispatchEvent(new Event('change'));
      }
    });
  });
});

// ── BOOKING WIZARD ────────────────────────────
function goWizardStep(step) {
  const s1 = document.getElementById('step1');
  const s2 = document.getElementById('step2');
  if (!s1 || !s2) return;
  s1.style.display = step === 1 ? 'block' : 'none';
  s2.style.display = step === 2 ? 'block' : 'none';
  setWizardStepIndicator(step);
  if (step === 2) updateWizardSummary();
}

function setWizardStepIndicator(step) {
  for (let i = 1; i <= 3; i++) {
    const el = document.getElementById('stepNum' + i);
    if (!el) continue;
    el.className = 'wizard-step-num ' + (i < step ? 'done' : i === step ? 'active' : 'waiting');
  }
  for (let i = 1; i <= 2; i++) {
    const line = document.getElementById('stepLine' + i);
    if (line) line.className = 'wizard-step-line' + (i < step ? ' done' : '');
  }
}

function changeParticipants(delta) {
  const input = document.getElementById('participantsInput');
  if (!input) return;
  const max = parseInt(input.getAttribute('data-max') || '10');
  let val = Math.max(1, Math.min(max, parseInt(input.value || '1') + delta));
  input.value = val;
  const disp = document.getElementById('participantsDisplay');
  if (disp) disp.textContent = val;
  updateWizardSummary();
}

function updateWizardSummary() {
  const input   = document.getElementById('participantsInput');
  const totalEl = document.getElementById('summaryTotal');
  const partEl  = document.getElementById('summaryParticipants');
  if (!input || !totalEl) return;
  const count = parseInt(input.value) || 1;
  const price = parseFloat(totalEl.getAttribute('data-price')) || 0;
  if (partEl) partEl.textContent = count + ' ' + tkgText('misc.people', 'чел.');
  totalEl.textContent = (count * price).toLocaleString('ru-RU') + ' сом';
}

// ── PHOTO PREVIEW ─────────────────────────────
function previewImage(input, zoneId, previewId) {
  const file = input.files[0];
  if (!file) return;
  const reader = new FileReader();
  reader.onload = e => {
    const prev = document.getElementById(previewId);
    if (prev) { prev.src = e.target.result; prev.style.display = 'block'; }
  };
  reader.readAsDataURL(file);
}

// ── COUNTER ANIMATION ─────────────────────────
function animateCounters() {
  document.querySelectorAll('.counter-anim').forEach(el => {
    const target = parseInt(el.getAttribute('data-target') || el.textContent, 10);
    if (isNaN(target) || target === 0) return;
    let start = 0;
    const duration = 1200;
    const step = 16;
    const increment = target / (duration / step);
    const timer = setInterval(() => {
      start += increment;
      if (start >= target) {
        el.textContent = target;
        clearInterval(timer);
      } else {
        el.textContent = Math.floor(start);
      }
    }, step);
  });
}

// Run counter animation when elements come into view
if ('IntersectionObserver' in window) {
  const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        animateCounters();
        observer.disconnect();
      }
    });
  }, { threshold: 0.2 });
  document.querySelectorAll('.counter-anim').forEach(el => observer.observe(el));
} else {
  document.addEventListener('DOMContentLoaded', animateCounters);
}

// ── SCROLL REVEAL ──────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
  if (!('IntersectionObserver' in window)) return;
  const revealObserver = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('revealed');
        revealObserver.unobserve(entry.target);
      }
    });
  }, { threshold: 0.1, rootMargin: '0px 0px -40px 0px' });
  document.querySelectorAll('.reveal').forEach(el => revealObserver.observe(el));

  // ── NAVBAR SHADOW ON SCROLL ──────────────────────
  const navbar = document.querySelector('.tkg-navbar');
  if (navbar) {
    window.addEventListener('scroll', () => {
      navbar.classList.toggle('scrolled', window.scrollY > 10);
    }, { passive: true });
  }
});
