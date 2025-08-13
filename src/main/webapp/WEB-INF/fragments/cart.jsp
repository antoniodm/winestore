<h3>CART</h3>
<div id="cart_panel"></div>
<script>
    const CTX = '${pageContext.request.contextPath}';
    const panel = document.getElementById('cart_panel');

    async function renderCart() {
        const res = await fetch(CTX + '/cart', {
            method: 'GET',
            credentials: 'same-origin',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });
        panel.innerHTML = await res.text();
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
        const res = await fetch(CTX + '/cart', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: new URLSearchParams(bodyObj)
        });
        panel.innerHTML = await res.text();

        // dopo il checkout (commit già fatto lato server) ricarica #content
        if (bodyObj.action === 'checkout' && res.ok && res.headers.get('X-Checkout-Done') === '1') {
            await refreshContent();
        }
    }

    document.addEventListener('click', (e) => {
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
</script>

