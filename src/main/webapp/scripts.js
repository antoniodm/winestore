'use strict';

/* ============================================================================
 *  Cart & Content Utilities – GPT‑5 annotated version
 *  --------------------------------------------------------------------------
 *  Questo file gestisce:
 *    - refresh parziale del menu utente e del contenuto centrale
 *    - rendering del carrello (GET) e azioni sul carrello (POST)
 *    - gestione submit di form (aggiungi/rimuovi prodotto, registrazione)
 *    - delega di click sui pulsanti del carrello
 *
 *  Nota di progetto:
 *    - Nessuna dipendenza esterna.
 *    - Tutte le richieste sono marcate con 'X-Requested-With: XMLHttpRequest'
 *      per permettere al backend di distinguere le risposte parziali.
 * ============================================================================ */

/**
 * Prefisso di contesto dell’app (es. '/shop'); se window.CTX non esiste, stringa vuota.
 * Mantiene compatibilità con ambienti dove CTX non è definito.
 */
const CTX = (typeof window !== 'undefined' && window.CTX) ? window.CTX : '';

/* ----------------------------------------------------------------------------
 *  Helpers DOM/HTML
 * ---------------------------------------------------------------------------- */

/**
 * Crea un wrapper <div> con dentro l'HTML passato e ritorna l'elemento cercato.
 * @param {string} html - stringa HTML di una pagina/fragment
 * @param {string} selector - CSS selector dell’elemento da estrarre
 * @returns {Element|null}
 */
function extractFromHTML(html, selector) {
    const wrap = document.createElement('div');
    wrap.innerHTML = html;
    return wrap.querySelector(selector);
}

/**
 * Sostituisce l'innerHTML di un target presente nel DOM con quello “fresco”
 * trovato nell’HTML ricevuto dal server. In caso negativo, fa fallback sul reload.
 * @param {string} fromHtml - HTML completo o parziale ricevuto
 * @param {string} selector - selettore dell’elemento (es. '#content')
 */
function swapSection(fromHtml, selector) {
    const fresh = extractFromHTML(fromHtml, selector);
    const target = document.querySelector(selector);
    if (fresh && target) {
        target.innerHTML = fresh.innerHTML;
    } else {
        // Se non riusciamo a trovare la sezione, facciamo un fallback sicuro.
        window.location.reload();
    }
}

/* ----------------------------------------------------------------------------
 *  Fetch helpers
 * ---------------------------------------------------------------------------- */

/**
 * Esegue una fetch e ritorna sempre il body come testo (anche in caso di errore 4xx/5xx),
 * lasciando al chiamante la responsabilità di gestire res.ok.
 * @param {RequestInfo} url
 * @param {RequestInit} options
 * @returns {Promise<{ok: boolean, status: number, text: string}>}
 */
async function fetchText(url, options) {
    const res = await fetch(url, options);
    const text = await res.text();
    return { ok: res.ok, status: res.status, text };
}

/* ----------------------------------------------------------------------------
 *  Refresh parziali (menu utente, contenuto pagina)
 * ---------------------------------------------------------------------------- */

/**
 * Aggiorna la sezione #user_menu usando la versione “fresca” della pagina corrente.
 * Se la sezione non è reperibile, ricarica l’intera pagina.
 */
async function refreshUserMenu() {
    const { text } = await fetchText(window.location.href, {
        credentials: 'same-origin',
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    });
    swapSection(text, '#user_menu');
}

/**
 * Aggiorna la sezione #content usando la versione “fresca” della pagina corrente.
 * Se la sezione non è reperibile, ricarica l’intera pagina.
 */
async function refreshContent() {
    const { text } = await fetchText(window.location.href, {
        credentials: 'same-origin',
        headers: { 'X-Requested-With': 'XMLHttpRequest' }
    });
    swapSection(text, '#dynamic_content');
}

/* ----------------------------------------------------------------------------
 *  Carrello: render (GET) e azioni (POST)
 * ---------------------------------------------------------------------------- */

/**
 * Carica il pannello carrello (#cart_panel) tramite GET CTX + '/cart'.
 * In caso di errore HTTP mostra un messaggio nel pannello.
 */
async function renderCart() {
    const panel = document.getElementById('cart_panel');
    if (!panel) return; // la pagina potrebbe non avere il carrello

    try {
        const { ok, status, text } = await fetchText(CTX + '/cart', {
            method: 'GET',
            credentials: 'same-origin',
            headers: { 'X-Requested-With': 'XMLHttpRequest' }
        });

        if (!ok) {
            panel.innerHTML = `<div class="cart-error">Errore ${status} caricando il carrello.</div>`;
            return;
        }

        panel.innerHTML = text;
    } catch (e) {
        panel.innerHTML = `<div class="cart-error">Errore di rete: ${e}</div>`;
    }
}

/**
 * Invia un'azione al carrello (add/remove/reset/checkout) con body urlencoded.
 * Dopo l’aggiornamento del carrello, aggiorna anche #user_menu e #content.
 * @param {Record<string, string|number|undefined>} bodyObj - es. { action: 'add', id: '123' }
 */
async function postCart(bodyObj) {
    const panel = document.getElementById('cart_panel');
    if (!panel) return;

    try {
        const { text } = await fetchText(CTX + '/cart', {
            method: 'POST',
            credentials: 'same-origin',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'X-Requested-With': 'XMLHttpRequest'
            },
            body: new URLSearchParams(bodyObj)
        });

        // Aggiorna subito il pannello carrello con l’HTML di risposta.
        panel.innerHTML = text;

        // Mantieni allineata la UI (es. badge carrello, totali nel content, ecc.).
        await refreshUserMenu();
        await refreshContent();
    } catch (e) {
        panel.innerHTML = `<div class="cart-error">Errore di rete: ${e}</div>`;
    }
}

/* ----------------------------------------------------------------------------
 *  Event wiring (submit & click delegation)
 * ---------------------------------------------------------------------------- */

/**
 * Gestione submit nel contenitore #content per:
 *  - Aggiunta/Rimozione prodotti (submitter id: 'add_prod_btn' | 'del_prod_btn')
 *  - Registrazione utente (submitter id: 'btnRegister')
 *
 * Nota:
 *  - Per add/remove si invia FormData (multipart) e si rimpiazza #content con la risposta.
 *  - Per registrazione si invia application/x-www-form-urlencoded e si scrive la risposta
 *    in #responseMessage se presente, altrimenti rimpiazza #content.
 */
function attachContentSubmitHandler() {
    const content = document.getElementById('dynamic_content');
    if (!content) return;

    content.addEventListener('submit', async (e) => {
        const form = e.target;
        if (!(form instanceof HTMLFormElement)) return;

        const submitter = e.submitter;
        if (!submitter) return;

        const submitId = submitter.id;

        // Filtra solo i form previsti
        const isCartAction = (submitId === 'add_prod_btn' || submitId === 'del_prod_btn');
        const isRegister  = (submitId === 'btnRegister');
        const isResurrect  = (submitId === 'recreate_prod_btn');

        if (!isCartAction && !isRegister && !isResurrect) return;

        e.preventDefault();
        if (!form.reportValidity()) return; // rispetta le constraint dei campi

        try {
            if (isCartAction) {
                // Case: Aggiungi/Remove prodotto -> invio FormData (multipart)
                const body = new FormData(form);
                const res = await fetch(form.action, {
                    method: (form.method || 'POST').toUpperCase(),
                    body,
                    credentials: 'same-origin',
                    headers: { 'X-Requested-With': 'XMLHttpRequest' }
                });
                const html = await res.text();
                // Rimpiazza l’intero #content con quanto restituito (listing, messaggi, ecc.)
                content.innerHTML = html;
                return;
            }

            if (isRegister || isResurrect) {
                // Case: Registrazione -> urlencoded + scrittura su #responseMessage (se esiste)
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
                const responseMessage = document.getElementById('responseMessage');
                if (isResurrect) {
                    await refreshContent();
                } else {
                    if (responseMessage) responseMessage.innerHTML = html;
                    else content.innerHTML = html;
                }
            }


        } catch (err) {
            // In caso di errore di rete mostriamo un messaggio user‑friendly in #content.
            content.innerHTML = `<div class="error">Errore durante l'invio del form: ${err}</div>`;
        }
    }, true); // useCapture=true per intercettare prima di altri handler
}

/**
 * Gestisce i click su pulsanti di carrello tramite delega sul document:
 *  - .add_to_cart (richiede data-id)
 *  - .remove_from_cart (richiede data-id)
 *  - .reset_cart
 *  - .buy_cart
 *
 * Costruisce il body a partire dall’azione e delega a postCart().
 */
function attachCartClickHandler() {
    document.addEventListener('click', (e) => {
        const btn = e.target && ((e.target)).closest?.('.add_to_cart, .remove_from_cart, .reset_cart, .buy_cart');
        if (!btn) return;

        e.preventDefault();

        const isAdd   = btn.classList.contains('add_to_cart');
        const isRem   = btn.classList.contains('remove_from_cart');
        const isReset = btn.classList.contains('reset_cart');
        const isBuy   = btn.classList.contains('buy_cart');

        const id = btn.getAttribute('data-id');

        // id obbligatorio solo per add/remove
        if ((isAdd || isRem) && !id) return;

        const body =
            isAdd   ? { action: 'add',     id } :
                isRem   ? { action: 'remove',  id } :
                    isReset ? { action: 'reset' } :
                        { action: 'checkout' };

        postCart(body);
    });
}

/* ----------------------------------------------------------------------------
 *  Bootstrap
 * ---------------------------------------------------------------------------- */

document.addEventListener('DOMContentLoaded', () => {
    // 1) Render iniziale del carrello se presente in pagina.
    renderCart();

    // 2) Wiring degli handler su #content (submit) e su document (click carrello).
    attachContentSubmitHandler();
    attachCartClickHandler();
});
