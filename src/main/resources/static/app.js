const qs = s => document.querySelector(s)

async function jsonRequest(url, method = 'GET', body = null) {
  const opts = { method, headers: { 'Content-Type': 'application/json' } }
  if (body) opts.body = JSON.stringify(body)
  const res = await fetch(url, opts)
  const txt = await res.text()
  let data
  try { data = JSON.parse(txt) } catch (e) { data = txt }
  return { status: res.status, ok: res.ok, data }
}

let loggedAccount = null

function escapeHtml(s){ if (s===null||s===undefined) return ''; return String(s).replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;'); }

function sanitizeNumberInput(v) { if (v === null || v === undefined) return '' ; let s = String(v).replace(',', '.').trim(); return s }
function isValidNumberString(v) { const s = sanitizeNumberInput(v); if (s === '') return false; const n = Number(s); return !Number.isNaN(n) && Number.isFinite(n) }

function displayResult(selector, res) {
  const out = qs(selector)
  if (!out) return
  out.classList.remove('success', 'error')
  if (!res) { out.textContent = 'Sem resposta do servidor'; out.classList.add('error'); return }
  if (res.data && typeof res.data === 'object' && Array.isArray(res.data.errors) && res.data.errors.length > 0) {
    const parts = res.data.errors.map(e => { try { return (e && e.message) ? e.message : JSON.stringify(e) } catch (x) { return JSON.stringify(e) } })
    out.textContent = parts.join('\n')
    out.classList.add('error')
    return
  }
  if (res.ok) {
    if (res.status === 201 && res.data && typeof res.data === 'object') {
      const a = res.data
      const acct = (a.accountNumber ? `Conta ${a.accountNumber}` : '') + (a.agency ? ` Agência ${a.agency}` : '')
      const client = a.client ? ` (Cliente: ${a.client})` : ''
      out.textContent = `Conta criada com sucesso: ${acct}${client}`.trim()
      out.classList.add('success')
      return
    }
    let msg = ''
    if (res.data && typeof res.data === 'object') {
      if (res.data.message) msg = res.data.message
      else if (res.data.result && res.data.result.message) msg = res.data.result.message
      else if (res.data.data && res.data.data.message) msg = res.data.data.message
      else msg = Object.keys(res.data).length ? JSON.stringify(res.data, null, 2) : 'Operação concluída com sucesso.'
    } else {
      msg = String(res.data || 'Operação concluída com sucesso.')
    }
    out.textContent = msg
    out.classList.add('success')
  } else {
    let msg = ''
    if (res.data && typeof res.data === 'object') {
      if (res.data.message) { msg = res.data.message }
      const details = Array.isArray(res.data.details) ? res.data.details : (Array.isArray(res.data.errors) ? res.data.errors.map(e => e.message || JSON.stringify(e)) : [])
      if (details.length > 0) {
        const list = details.map(d => `- ${d}`).join('\n')
        msg = msg ? (msg + '\n' + list) : list
      }
      if (!msg) msg = JSON.stringify(res.data, null, 2)
    } else {
      msg = String(res.data || `Erro: status ${res.status}`)
    }
    out.textContent = msg
    out.classList.add('error')
  }
}

function showGlobalMessage(text, type = 'success', ttl = 5000) {
  const g = qs('#globalMessage')
  if (!g) return
  g.textContent = text
  g.classList.remove('hidden', 'success', 'error')
  g.classList.add(type)
  if (ttl > 0) setTimeout(() => { g.classList.add('hidden'); g.classList.remove(type); g.textContent = '' }, ttl)
}

function show(selector) { const el = qs(selector); if (el) { el.classList.remove('hidden'); el.style.display = '' } }
function hide(selector) { const el = qs(selector); if (el) { el.classList.add('hidden'); el.style.display = 'none' } }

function showMainView(selector) { document.querySelectorAll('main > *').forEach(el => { el.classList.add('hidden'); el.style.display = 'none' }); const el = qs(selector); if (el) { el.classList.remove('hidden'); el.style.display = '' } }

function setLoggedAccount(account) { loggedAccount = account; const labelEl = qs('#accountLabel'); if (labelEl) labelEl.textContent = account.accountNumber || account.id || account.account || 'Usuario'; document.querySelectorAll('#initialView').forEach(el => { el.classList.add('hidden'); el.style.display = 'none' }); showMainView('#accountView'); const balanceEl = qs('#balanceView'); if (balanceEl) balanceEl.textContent = ''; document.querySelectorAll('#accountView .op').forEach(el => { el.classList.add('hidden'); el.style.display = 'none' }) }

function logout(suppressGlobalMessage) { loggedAccount = null; showMainView('#initialView'); const ca = qs('#createAccountResult'); if (ca) ca.textContent = ''; const li = qs('#loginResult'); if (li) li.textContent = ''; if (!suppressGlobalMessage) showGlobalMessage('Logout realizado com sucesso.', 'success', 4000) }

function safeOn(selector, event, handler) { const el = qs(selector); if (!el) return; el.addEventListener(event, handler) }

safeOn('#btnShowCreate', 'click', () => { hide('#initialView'); showMainView('#createView') })
safeOn('#btnShowLogin', 'click', () => { hide('#initialView'); showMainView('#loginView') })
safeOn('#btnBackFromCreate', 'click', () => { showMainView('#initialView') })
safeOn('#btnBackFromLogin', 'click', () => { showMainView('#initialView') })

safeOn('#createAccountForm', 'submit', async (e) => {
  e.preventDefault();
  const f = e.target
  const body = { agency: f.agency.value, client: f.client.value, initialDeposit: f.initialDeposit.value, limit: f.limit.value, type: f.type.value, password: f.password.value }
  if (body.initialDeposit && !isValidNumberString(body.initialDeposit)) { displayResult('#createAccountResult', { ok: false, status: 400, data: { message: 'Depósito inicial deve ser um número válido.' } }); return }
  if (body.limit && !isValidNumberString(body.limit)) { displayResult('#createAccountResult', { ok: false, status: 400, data: { message: 'Limite deve ser um número válido.' } }); return }
  const r = await jsonRequest('/api/accounts', 'POST', body)
  displayResult('#createAccountResult', r)
  if (r && r.ok) {
    if (r.status === 201 && r.data && typeof r.data === 'object') {
      const a = r.data
      const summary = `Conta criada: ${a.accountNumber || ''} — Agência ${a.agency || ''} (Cliente: ${a.client || ''})`
      showGlobalMessage(summary, 'success', 8000)
      const loginAgency = qs('#loginForm [name=agency]'); if (loginAgency) loginAgency.value = a.agency || ''
      const loginAccount = qs('#loginForm [name=accountNumber]'); if (loginAccount) loginAccount.value = a.accountNumber || ''
    }
    try { e.target.reset() } catch (_) {}
    showMainView('#initialView')
  }
})

safeOn('#loginForm', 'submit', async (e) => {
  e.preventDefault();
  const f = e.target
  const body = { agency: f.agency.value, accountNumber: f.accountNumber.value, password: f.password.value }
  const r = await jsonRequest('/api/auth/login', 'POST', body)
  displayResult('#loginResult', r)
  if (r.ok) {
    const d = r.data
    if (d && (d.accountNumber || d.id || d.account)) { setLoggedAccount(d); try { f.reset() } catch (_) {} }
    else if (d && d.result) { setLoggedAccount(d.result); try { f.reset() } catch (_) {} }
    else {
      const accounts = await jsonRequest('/api/accounts')
      if (accounts.ok && Array.isArray(accounts.data)) {
        const found = accounts.data.find(a => String(a.accountNumber) === String(body.accountNumber) && String(a.agency) === String(body.agency))
        if (found) setLoggedAccount(found)
        try { f.reset() } catch (_) {}
      }
    }
  }
})

safeOn('#depositForm', 'submit', async (e) => {
  e.preventDefault();
  const f = e.target
  if (!loggedAccount) return alert('Faça login antes')
  const raw = f.amount.value
  if (!isValidNumberString(raw)) { displayResult('#depositResult', { ok: false, status: 400, data: { message: 'Valor deve ser um número válido.' } }); return }
  const body = { amount: parseFloat(sanitizeNumberInput(raw)) }
  const r = await jsonRequest(`/api/accounts/${loggedAccount.accountNumber}/deposit`, 'POST', body)
  displayResult('#depositResult', r)
  if (r && r.ok) { try { f.reset() } catch (_) {} }
})

safeOn('#withdrawForm', 'submit', async (e) => {
  e.preventDefault();
  const f = e.target
  if (!loggedAccount) return alert('Faça login antes')
  const raw = f.amount.value
  if (!isValidNumberString(raw)) { displayResult('#withdrawResult', { ok: false, status: 400, data: { message: 'Valor deve ser um número válido.' } }); return }
  const body = { amount: parseFloat(sanitizeNumberInput(raw)), password: f.password.value }
  const r = await jsonRequest(`/api/accounts/${loggedAccount.accountNumber}/withdraw`, 'POST', body)
  displayResult('#withdrawResult', r)
  if (r && r.ok) { try { f.reset() } catch (_) {} }
})

safeOn('#changeLimitForm', 'submit', async (e) => {
  e.preventDefault();
  const f = e.target
  if (!loggedAccount) return alert('Faça login antes')
  const raw = f.newLimit.value
  if (!isValidNumberString(raw)) { displayResult('#changeLimitResult', { ok: false, status: 400, data: { message: 'Novo limite deve ser um número válido.' } }); return }
  const body = { newLimit: parseFloat(sanitizeNumberInput(raw)), password: f.password.value }
  const r = await jsonRequest(`/api/accounts/${loggedAccount.accountNumber}/change-limit`, 'POST', body)
  displayResult('#changeLimitResult', r)
  if (r && r.ok) { try { f.reset() } catch (_) {} }
})

safeOn('#transferForm', 'submit', async (e) => {
  e.preventDefault();
  const f = e.target
  if (!loggedAccount) return alert('Faça login antes')
  const toAccRaw = f.toAccount.value
  const amountRaw = f.amount.value
  if (!toAccRaw || isNaN(Number(toAccRaw))) { displayResult('#transferResult', { ok: false, status: 400, data: { message: 'Conta de destino inválida.' } }); return }
  if (!isValidNumberString(amountRaw)) { displayResult('#transferResult', { ok: false, status: 400, data: { message: 'Valor inválido.' } }); return }
  const body = { fromAccount: loggedAccount.accountNumber, toAccount: parseInt(toAccRaw), amount: parseFloat(sanitizeNumberInput(amountRaw)), password: f.password.value }
  const r = await jsonRequest('/api/transfer', 'POST', body)
  displayResult('#transferResult', r)
  if (r && r.ok) { try { f.reset() } catch (_) {} }
})

safeOn('#btnExportCsv', 'click', async () => { window.open('/api/transactions/export', '_blank') })

document.addEventListener('click', (e) => {
  const btn = e.target.closest && e.target.closest('.account-menu .op-btn')
  if (!btn) return
  const op = btn.getAttribute('data-op')
  if (!op) return
  document.querySelectorAll('#accountView .op').forEach(el => { el.classList.add('hidden'); el.style.display = 'none' })
  const sel = document.getElementById(op)
  if (sel) { sel.classList.remove('hidden'); sel.style.display = '' }
})

safeOn('#btnListAccounts', 'click', async () => {
  const r = await jsonRequest('/api/accounts')
  const out = qs('#accountsList')
  if (!out) return
  if (r.ok && Array.isArray(r.data)) {
    const accounts = r.data
    if (accounts.length === 0) { out.innerHTML = '<div class="muted">Nenhuma conta encontrada.</div>'; return }
    const rows = accounts.map(a => {
      const accNum = escapeHtml(a.accountNumber)
      const agency = escapeHtml(a.agency)
      const client = escapeHtml(a.client)
      const balance = typeof a.balance !== 'undefined' ? escapeHtml(a.balance) : ''
      const limit = typeof a.limit !== 'undefined' ? escapeHtml(a.limit) : (typeof a.limitValue !== 'undefined' ? escapeHtml(a.limitValue) : '')
      const type = escapeHtml(a.type || a.accountType || '')
      return `<tr><td>${accNum}</td><td>${agency}</td><td>${client}</td><td style="text-align:right">${balance}</td><td style="text-align:right">${limit}</td><td>${type}</td></tr>`
    }).join('')
    out.innerHTML = `<div class="accounts-container"><table class="accounts-table" style="width:100%;border-collapse:collapse"><thead><tr><th style="text-align:left">Conta</th><th style="text-align:left">Agência</th><th style="text-align:left">Cliente</th><th style="text-align:right">Saldo</th><th style="text-align:right">Limite</th><th style="text-align:left">Tipo</th></tr></thead><tbody>${rows}</tbody></table></div>`
  } else {
    out.textContent = JSON.stringify(r.data || r, null, 2)
  }
})

safeOn('#btnRefreshBalance', 'click', async () => {
  if (!loggedAccount) return alert('Faça login antes')
  const r = await jsonRequest('/api/accounts')
  const out = qs('#balanceView')
  if (r.ok && Array.isArray(r.data)) {
    const latest = r.data.find(a => String(a.accountNumber) === String(loggedAccount.accountNumber))
    if (latest) {
      loggedAccount = latest
      if (out) out.textContent = `Saldo: ${latest.balance}\nLimite: ${latest.limitValue || latest.limit}`
      const labelEl = qs('#accountLabel'); if (labelEl) labelEl.textContent = latest.accountNumber
    } else { if (out) out.textContent = 'Conta não encontrada' }
  } else { if (out) out.textContent = JSON.stringify(r, null, 2) }
})

safeOn('#btnDeleteAccount', 'click', async () => {
  if (!loggedAccount) return alert('Faça login antes')
  const pwdEl = qs('#deletePassword'); const pwd = pwdEl ? pwdEl.value : ''
  const resp = await fetch(`/api/accounts/${loggedAccount.accountNumber}?password=${encodeURIComponent(pwd)}`, { method: 'DELETE' })
  const txt = await resp.text()
  const out = qs('#deleteResult')
  let parsed = null
  try { parsed = JSON.parse(txt) } catch (_) { parsed = null }
  const resObj = { status: resp.status, ok: resp.ok, data: parsed !== null ? parsed : txt }
  if (out) { displayResult('#deleteResult', resObj) }
  const globalMsg = (parsed && parsed.message) ? parsed.message : (resp.ok ? 'Conta removida com sucesso.' : txt)
  if (resp.ok) {
    showGlobalMessage(globalMsg, 'success', 5000)
    if (pwdEl) try { pwdEl.value = '' } catch (_) {}
    logout(true)
  } else { showGlobalMessage(globalMsg, 'error', 6000) }
})

safeOn('#btnLogout', 'click', () => { logout() })

window.addEventListener('load', () => { showMainView('#initialView'); document.querySelectorAll('#accountView .op').forEach(el => el.classList.add('hidden')) })

