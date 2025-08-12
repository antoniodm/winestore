<h3>CART</h3>
<div id="cart_panel"></div>
<script>
    const CTX = '${pageContext.request.contextPath}';
    const panel = document.getElementById('cart_panel');

    async function renderCart() {
        const res = await fetch(CTX + '/cart', {
            credentials: 'same-origin',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });
        panel.innerHTML = await res.text();
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
    }

    document.addEventListener('click', (e) => {
        const addBtn = e.target.closest('.add_to_cart');
        const remBtn = e.target.closest('.remove_from_cart');
        const rstBtn = e.target.closest('.reset_cart');
        if (!addBtn && !remBtn && !rstBtn) return;

        e.preventDefault();
        const btn = addBtn || remBtn || rstBtn;
        const id = btn.dataset.id;
        if (!id && !rstBtn) return;

        if (addBtn)         postCart({ action: 'add', id });
        else if (remBtn)    postCart({ action: 'remove', id });
        else if (rstBtn)    postCart({ action: 'reset' });
    });

    document.addEventListener('DOMContentLoaded', renderCart);
</script>

