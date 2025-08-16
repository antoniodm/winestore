const CTX = (typeof window !== 'undefined' && window.CTX) ? window.CTX : '';

async function refreshUserMenu() {
    const res = await fetch(window.location.href, {
        credentials: 'same-origin',
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    });
    const html = await res.text();
    const wrap = document.createElement('div'); wrap.innerHTML = html;
    const fresh = wrap.querySelector('#user_menu');
    const target = document.getElementById('user_menu');
    if (fresh && target) target.innerHTML = fresh.innerHTML; else window.location.reload();
}


async function renderCart() {
    const panel = document.getElementById('cart_panel');
    if (!panel) return;

    try {
        const res = await fetch(CTX + '/cart', {
            method: 'GET',
            credentials: 'same-origin',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });

        if (!res.ok) {
            panel.innerHTML = `<div class="cart-error">Errore ${res.status} caricando il carrello.</div>`;
            return;
        }

        panel.innerHTML = await res.text();
    } catch (e) {
        panel.innerHTML = `<div class="cart-error">Errore di rete: ${e}</div>`;
    }
}

async function refreshContent() {
    const res = await fetch(window.location.href, {
        credentials: 'same-origin',
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    });
    const html = await res.text();
    const wrap = document.createElement('div'); wrap.innerHTML = html;
    const fresh = wrap.querySelector('#content');
    const target = document.getElementById('content');
    if (fresh && target) target.innerHTML = fresh.innerHTML; else window.location.reload();
}

async function postCart(bodyObj) {
    const panel = document.getElementById('cart_panel');
    if (!panel) return;

    try {
        const res = await fetch(CTX + '/cart', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: new URLSearchParams(bodyObj)
        });

        const html = await res.text();

        if (!res.ok) {
            // Non iniettare pagine intere: messaggio compatto
            panel.innerHTML = `<div class="cart-error">Operazione non riuscita (${res.status}).</div>` + (panel.innerHTML || '');
            return;
        }

        panel.innerHTML = html;

        // checkout: opzionale, se usi l’header già impostato lato server
        if (bodyObj.action === 'checkout' && res.headers.get('X-Checkout-Done') === '1') {
            await renderCart(); // o tua refreshContent()
        }
        await refreshUserMenu();
    } catch (e) {
        panel.innerHTML = `<div class="cart-error">Errore di rete: ${e}</div>`;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    const content = document.getElementById('content');
    const responseMessage = document.getElementById('responseMessage');

    if (!content) return;

    content.addEventListener('submit', async (e) => {
        const form = e.target;
        if (!(form instanceof HTMLFormElement)) return;

        // se vuoi gestire via AJAX solo certe form, filtra:
        // if (!e.submitter || e.submitter.id !== 'btnRegister') return;

        e.preventDefault();
        if (!form.reportValidity()) return;

        const body = new URLSearchParams(new FormData(form));
        const res = await fetch(form.action, {
            method: (form.method || 'POST').toUpperCase(),
            body,
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            }
        });

        const html = await res.text();
        if (responseMessage) responseMessage.innerHTML = html;
    }, true);
});


document.addEventListener('click', (e) => {
    console.log("CIAO");
    const btn = e.target.closest('.add_to_cart, .remove_from_cart, .reset_cart, .buy_cart');
    if (!btn) return;
    e.preventDefault();

    const isAdd   = btn.classList.contains('add_to_cart');
    const isRem   = btn.classList.contains('remove_from_cart');
    const isReset = btn.classList.contains('reset_cart');
    const isBuy   = btn.classList.contains('buy_cart');

    const id = btn.dataset.id;

    // l'id serve SOLO per add/remove
    if ((isAdd || isRem) && !id) return;

    const body =
        isAdd   ? { action: 'add',    id } :
            isRem   ? { action: 'remove', id } :
                isReset ? { action: 'reset' } :
                    { action: 'checkout' };

    postCart(body);
});

document.addEventListener('DOMContentLoaded', renderCart);
