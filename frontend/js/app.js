(() => {
  const API = '/api';
  const page = document.body.dataset.page || '';
  const money = value => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(Number(value || 0));
  const escapeHtml = value => String(value ?? '').replace(/[&<>"']/g, character => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' })[character]);
  const user = () => {
    try { return JSON.parse(localStorage.getItem('thinkmart.user') || 'null'); }
    catch { return null; }
  };
  const token = () => localStorage.getItem('thinkmart.token');

  async function request(path, options = {}) {
    const headers = { ...(options.body ? { 'Content-Type': 'application/json' } : {}), ...(options.headers || {}) };
    if (token()) headers.Authorization = `Bearer ${token()}`;
    let response;
    try {
      response = await fetch(`${API}${path}`, { ...options, headers });
    } catch {
      throw new Error('ThinkMart could not reach the server. Check that the application is running.');
    }
    const data = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok) {
      if (response.status === 401 && token()) {
        localStorage.removeItem('thinkmart.token');
        localStorage.removeItem('thinkmart.user');
      }
      const fieldError = data?.errors ? Object.values(data.errors)[0] : null;
      throw new Error(fieldError || data?.message || 'Something went wrong. Please try again.');
    }
    return data;
  }

  function showMessage(element, message, success = false) {
    if (!element) return;
    element.textContent = message;
    element.classList.toggle('is-success', success);
    element.classList.add('is-visible');
  }

  function setBusy(button, busy, label = 'Please wait...') {
    if (!button) return;
    if (busy) {
      button.dataset.originalLabel = button.innerHTML;
      button.disabled = true;
      button.innerHTML = `<i class="fa-solid fa-spinner fa-spin" aria-hidden="true"></i> ${label}`;
    } else {
      button.disabled = false;
      button.innerHTML = button.dataset.originalLabel || button.innerHTML;
    }
  }

  function chrome() {
    const currentPage = location.pathname.split('/').pop() || 'index.html';
    const signedInUser = user();
    const authAction = signedInUser
      ? `<a class="header-action" href="Home-Page.html" aria-label="Signed in as ${escapeHtml(signedInUser.fullName)}"><i class="fa-regular fa-user"></i><span class="action-label">${escapeHtml(signedInUser.fullName.split(' ')[0])}</span></a><button class="header-action" type="button" data-logout aria-label="Log out"><i class="fa-solid fa-arrow-right-from-bracket"></i><span class="action-label">Log out</span></button>`
      : `<a class="header-action" href="index.html" aria-label="Sign in"><i class="fa-regular fa-user"></i><span class="action-label">Sign in</span></a>`;
    const links = [
      ['Home-Page.html', 'Home'], ['Products.html', 'Shop'], ['About.html', 'Our story'], ['Contact.html', 'Contact']
    ];
    const navLinks = links.map(([href, label]) => `<a href="${href}" ${currentPage === href ? 'aria-current="page"' : ''}>${label}</a>`).join('');
    const header = document.querySelector('[data-app-header]');
    if (header) {
      header.innerHTML = `<div class="announcement">A little more thoughtful. A lot more <span>&nbsp;you.</span> &nbsp; Shipping is on us</div>
        <header class="site-header"><div class="container header-main">
          <a class="brand" href="Home-Page.html" aria-label="ThinkMart home"><span class="brand-mark">T</span>ThinkMart</a>
          <form class="header-search" data-header-search role="search"><label class="sr-only" for="global-search">Search products</label><input id="global-search" type="search" placeholder="Find something thoughtful..." autocomplete="off"><button type="submit" aria-label="Search"><i class="fa-solid fa-magnifying-glass"></i></button></form>
          <div class="header-actions">${authAction}<a class="header-action" href="Cart.html" aria-label="Shopping cart"><i class="fa-solid fa-bag-shopping"></i><span class="action-label">Cart</span><span class="cart-count" data-cart-count>0</span></a><button class="icon-button mobile-menu-button" type="button" data-mobile-menu aria-label="Open navigation" aria-expanded="false"><i class="fa-solid fa-bars"></i></button></div>
        </div><nav class="nav-row" aria-label="Main navigation"><div class="container nav-inner">${navLinks}<span class="nav-spacer"></span><a class="nav-promo" href="Products.html?sort=price-asc"><i class="fa-solid fa-sparkles"></i> Shop best value</a></div></nav>
        <nav class="container mobile-nav" data-mobile-nav aria-label="Mobile navigation">${navLinks}<a href="Cart.html">Shopping cart</a></nav></header>`;
    }
    const footer = document.querySelector('[data-app-footer]');
    if (footer) {
      footer.innerHTML = `<footer class="site-footer"><div class="container"><div class="footer-grid">
        <div class="footer-brand"><a class="brand" href="Home-Page.html"><span class="brand-mark">T</span>ThinkMart</a><p>Good things, thoughtfully chosen. Discover everyday technology with a little more care behind it.</p></div>
        <div class="footer-column"><h4>Explore</h4><a href="Products.html">All products</a><a href="Products.html?category=laptops">Laptops</a><a href="Products.html?category=mobiles">Mobiles</a><a href="Products.html?category=audio">Audio</a></div>
        <div class="footer-column"><h4>ThinkMart</h4><a href="About.html">Our story</a><a href="Contact.html">Contact us</a><a href="Home-Page.html">Shipping & returns</a></div>
        <div class="footer-column"><h4>Need a hand?</h4><a href="Contact.html">We're here to help</a><a href="mailto:hello@thinkmart.example">hello@thinkmart.example</a><a href="tel:+18005550184">+1 (800) 555-0184</a></div>
        </div><div class="footer-bottom"><span>&copy; ${new Date().getFullYear()} ThinkMart. Thoughtfully yours.</span><div class="footer-socials"><a href="https://www.instagram.com/" aria-label="Instagram"><i class="fa-brands fa-instagram"></i></a><a href="https://www.facebook.com/" aria-label="Facebook"><i class="fa-brands fa-facebook-f"></i></a><a href="https://www.pinterest.com/" aria-label="Pinterest"><i class="fa-brands fa-pinterest-p"></i></a></div></div></div></footer>`;
    }
    document.querySelectorAll('[data-logout]').forEach(button => button.addEventListener('click', () => {
      localStorage.removeItem('thinkmart.token');
      localStorage.removeItem('thinkmart.user');
      location.href = 'index.html';
    }));
    document.querySelector('[data-header-search]')?.addEventListener('submit', event => {
      event.preventDefault();
      const query = document.querySelector('#global-search').value.trim();
      location.href = `Products.html${query ? `?query=${encodeURIComponent(query)}` : ''}`;
    });
    document.querySelector('[data-mobile-menu]')?.addEventListener('click', event => {
      const nav = document.querySelector('[data-mobile-nav]');
      const isOpen = nav.classList.toggle('is-open');
      event.currentTarget.setAttribute('aria-expanded', String(isOpen));
      event.currentTarget.setAttribute('aria-label', isOpen ? 'Close navigation' : 'Open navigation');
      event.currentTarget.innerHTML = `<i class="fa-solid fa-${isOpen ? 'xmark' : 'bars'}"></i>`;
    });
  }

  function requireAuth() {
    if (token()) return true;
    sessionStorage.setItem('thinkmart.next', location.pathname.split('/').pop() + location.search);
    location.href = 'index.html';
    return false;
  }

  async function updateCartCount() {
    const count = document.querySelector('[data-cart-count]');
    if (!count || !token()) return;
    try {
      const cart = await request('/cart');
      count.textContent = cart.items.reduce((sum, item) => sum + item.quantity, 0);
    } catch { count.textContent = '0'; }
  }

  function productCard(product) {
    const discount = Math.max(0, Math.round((1 - Number(product.price) / Number(product.originalPrice)) * 100));
    return `<article class="product-card"><div class="product-image-wrap">
        <img class="product-image" src="${escapeHtml(product.imageUrl)}" alt="${escapeHtml(product.name)}" loading="lazy" data-fallback>
        ${discount ? `<span class="discount-badge">${discount}% off</span>` : ''}
      </div><div class="product-info"><span class="product-category">${escapeHtml(product.category?.name || 'ThinkMart pick')}</span>
        <a class="product-name" href="Products.html?product=${encodeURIComponent(product.slug)}">${escapeHtml(product.name)}</a>
        <div class="product-rating" aria-label="Rated ${escapeHtml(product.rating)} out of 5"><i class="fa-solid fa-star"></i> ${escapeHtml(product.rating)} <span>(${Math.max(18, Math.round(Number(product.rating) * 31))})</span></div>
        <div class="product-buy-row"><div class="product-price">${money(product.price)} <del>${money(product.originalPrice)}</del></div><button class="add-button" type="button" data-add-product="${Number(product.id)}" aria-label="Add ${escapeHtml(product.name)} to cart" title="Add to cart"><i class="fa-solid fa-plus"></i></button></div>
      </div></article>`;
  }

  function bindImageFallbacks(root = document) {
    root.querySelectorAll('img[data-fallback]').forEach(image => image.addEventListener('error', () => {
      image.src = '/assets/product-placeholder.svg';
      image.removeAttribute('data-fallback');
    }, { once: true }));
  }

  function bindAddButtons(root = document) {
    root.querySelectorAll('[data-add-product]').forEach(button => button.addEventListener('click', async () => {
      if (!requireAuth()) return;
      const original = button.innerHTML;
      button.disabled = true;
      button.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';
      try {
        const cart = await request('/cart/items', { method: 'POST', body: JSON.stringify({ productId: Number(button.dataset.addProduct), quantity: 1 }) });
        const count = document.querySelector('[data-cart-count]');
        if (count) count.textContent = cart.items.reduce((sum, item) => sum + item.quantity, 0);
        button.innerHTML = '<i class="fa-solid fa-check"></i>';
        window.setTimeout(() => { button.innerHTML = original; button.disabled = false; }, 900);
      } catch (error) {
        window.alert(error.message);
        button.innerHTML = original;
        button.disabled = false;
      }
    }));
  }

  async function loadProductGrid(container, path, emptyCopy = 'No products found. Try adjusting your search.') {
    if (!container) return [];
    container.innerHTML = '<div class="product-loading"><i class="fa-solid fa-circle-notch fa-spin"></i>Finding your next favorite...</div>';
    try {
      const products = await request(path);
      container.innerHTML = products.length ? products.map(productCard).join('') : `<div class="empty-state"><i class="fa-regular fa-face-smile"></i>${escapeHtml(emptyCopy)}</div>`;
      bindImageFallbacks(container);
      bindAddButtons(container);
      return products;
    } catch (error) {
      container.innerHTML = `<div class="empty-state"><i class="fa-solid fa-cloud"></i>${escapeHtml(error.message)}</div>`;
      return [];
    }
  }

  async function initHome() {
    await Promise.all([
      loadProductGrid(document.querySelector('[data-featured-products]'), '/products?sort=rating'),
      loadProductGrid(document.querySelector('[data-trending-products]'), '/products?sort=newest')
    ]);
  }

  async function initProducts() {
    const grid = document.querySelector('[data-products-grid]');
    const count = document.querySelector('[data-product-count]');
    const params = new URLSearchParams(location.search);
    const productSlug = params.get('product');
    const searchInput = document.querySelector('[data-catalog-search]');
    const sortSelect = document.querySelector('[data-sort-products]');
    if (searchInput) searchInput.value = params.get('query') || '';
    if (sortSelect) sortSelect.value = params.get('sort') || '';
    const categoriesHost = document.querySelector('[data-category-filters]');
    if (productSlug) {
      try {
        const product = await request(`/products/slug/${encodeURIComponent(productSlug)}`);
        grid.innerHTML = `<article class="product-detail"><img class="product-detail-image" src="${escapeHtml(product.imageUrl)}" alt="${escapeHtml(product.name)}" data-fallback><div class="product-detail-copy"><span class="eyebrow">${escapeHtml(product.category.name)}</span><h2>${escapeHtml(product.name)}</h2><div class="product-rating"><i class="fa-solid fa-star"></i> ${escapeHtml(product.rating)} <span>customer rating</span></div><p>${escapeHtml(product.description)}</p><div class="product-detail-price">${money(product.price)} <del>${money(product.originalPrice)}</del></div><p class="muted">${Number(product.stock) > 0 ? `${Number(product.stock)} available` : 'Currently out of stock'}</p><button class="button" type="button" data-add-product="${Number(product.id)}" ${Number(product.stock) < 1 ? 'disabled' : ''}>Add to cart <i class="fa-solid fa-bag-shopping"></i></button><a class="text-link" href="Products.html" style="display:flex;margin-top:18px"><i class="fa-solid fa-arrow-left"></i> Back to all products</a></div></article>`;
        bindImageFallbacks(grid);
        bindAddButtons(grid);
        count.textContent = 'Product details';
      } catch (error) {
        grid.innerHTML = `<div class="empty-state"><i class="fa-solid fa-circle-exclamation"></i>${escapeHtml(error.message)}<p><a class="text-link" href="Products.html">Back to all products</a></p></div>`;
      }
      return;
    }
    try {
      const categories = await request('/products/categories');
      categoriesHost.innerHTML = `<label><input type="radio" name="category" value="" ${!params.get('category') ? 'checked' : ''}> All products</label>` + categories.map(category =>
        `<label><input type="radio" name="category" value="${escapeHtml(category.slug)}" ${params.get('category') === category.slug ? 'checked' : ''}> ${escapeHtml(category.name)}</label>`).join('');
      categoriesHost.querySelectorAll('input').forEach(input => input.addEventListener('change', () => loadCatalog()));
    } catch { categoriesHost.innerHTML = '<span class="muted">Categories are unavailable right now.</span>'; }
    let timer;
    searchInput?.addEventListener('input', () => {
      window.clearTimeout(timer);
      timer = window.setTimeout(loadCatalog, 220);
    });
    sortSelect?.addEventListener('change', loadCatalog);
    async function loadCatalog() {
      const selectedCategory = categoriesHost.querySelector('input:checked')?.value || '';
      const query = searchInput.value.trim();
      const sort = sortSelect.value;
      const searchParams = new URLSearchParams();
      if (query) searchParams.set('query', query);
      if (selectedCategory) searchParams.set('category', selectedCategory);
      if (sort) searchParams.set('sort', sort);
      history.replaceState(null, '', `Products.html${searchParams.size ? `?${searchParams}` : ''}`);
      const products = await loadProductGrid(grid, `/products${searchParams.size ? `?${searchParams}` : ''}`);
      count.textContent = `${products.length} ${products.length === 1 ? 'product' : 'products'}`;
    }
    await loadCatalog();
  }

  function cartLine(item) {
    return `<article class="cart-line" data-cart-item="${Number(item.id)}"><img class="cart-line-image" src="${escapeHtml(item.imageUrl)}" alt="${escapeHtml(item.name)}" data-fallback>
      <div class="cart-line-main"><h3>${escapeHtml(item.name)}</h3><span class="muted">${money(item.price)} each</span></div>
      <div class="quantity-control" aria-label="Quantity"><button type="button" data-quantity="-1" aria-label="Decrease quantity"><i class="fa-solid fa-minus"></i></button><span>${Number(item.quantity)}</span><button type="button" data-quantity="1" aria-label="Increase quantity"><i class="fa-solid fa-plus"></i></button></div>
      <div class="line-price">${money(item.lineTotal)}</div><button class="remove-item" type="button" data-remove-item aria-label="Remove ${escapeHtml(item.name)}"><i class="fa-regular fa-trash-can"></i></button></article>`;
  }

  function renderCart(cart) {
    const lines = document.querySelector('[data-cart-lines]');
    const summary = document.querySelector('[data-cart-summary]');
    const count = document.querySelector('[data-cart-items-count]');
    if (!lines || !summary) return;
    if (!cart.items.length) {
      lines.innerHTML = '<div class="empty-state"><i class="fa-solid fa-bag-shopping"></i><strong>Your cart is waiting for a good find.</strong><p>Take a look around and add something thoughtful.</p><a class="button" href="Products.html">Explore products</a></div>';
      summary.innerHTML = '<h2>Order summary</h2><p class="muted">Add an item to see your total.</p>';
      count.textContent = '0 items';
      return;
    }
    lines.innerHTML = cart.items.map(cartLine).join('');
    bindImageFallbacks(lines);
    count.textContent = `${cart.items.reduce((sum, item) => sum + item.quantity, 0)} ${cart.items.reduce((sum, item) => sum + item.quantity, 0) === 1 ? 'item' : 'items'}`;
    summary.innerHTML = `<h2>Order summary</h2><div class="summary-row"><span>Subtotal</span><strong>${money(cart.subtotal)}</strong></div><div class="summary-row"><span>ThinkMart savings</span><strong style="color:#25835d">-${money(cart.discount)}</strong></div><div class="summary-row"><span>Shipping</span><strong>Free</strong></div><div class="summary-row total"><span>Total</span><span>${money(cart.total)}</span></div><div class="summary-promo"><i class="fa-solid fa-box-open"></i> Easy returns within 30 days.</div><a class="button" href="Checkout.html">Continue to checkout <i class="fa-solid fa-arrow-right"></i></a><div class="secure-note"><i class="fa-solid fa-lock"></i> Secure checkout</div>`;
    lines.querySelectorAll('[data-quantity]').forEach(button => button.addEventListener('click', async () => {
      const article = button.closest('[data-cart-item]');
      const itemId = article.dataset.cartItem;
      const quantity = Number(article.querySelector('.quantity-control span').textContent) + Number(button.dataset.quantity);
      if (quantity < 1) return;
      try { renderCart(await request(`/cart/items/${itemId}`, { method: 'PUT', body: JSON.stringify({ quantity }) })); updateCartCount(); }
      catch (error) { window.alert(error.message); }
    }));
    lines.querySelectorAll('[data-remove-item]').forEach(button => button.addEventListener('click', async () => {
      const itemId = button.closest('[data-cart-item]').dataset.cartItem;
      try { renderCart(await request(`/cart/items/${itemId}`, { method: 'DELETE' })); updateCartCount(); }
      catch (error) { window.alert(error.message); }
    }));
  }

  async function initCart() {
    if (!requireAuth()) return;
    try { renderCart(await request('/cart')); }
    catch (error) { showMessage(document.querySelector('[data-cart-error]'), error.message); }
  }

  function renderOrderConfirmation(order) {
    const host = document.querySelector('[data-checkout-content]');
    host.innerHTML = `<section class="order-success"><i class="fa-solid fa-check" aria-hidden="true"></i><span class="eyebrow">Order confirmed</span><h1>Thank you, ${escapeHtml(order.customerName.split(' ')[0])}.</h1><p>Your order is in good hands. This demo marks payment as confirmed without processing a real charge.</p><div class="order-number">Order #TM-${String(order.id).padStart(6, '0')}</div><p>${order.items.length} ${order.items.length === 1 ? 'item' : 'items'} &middot; ${money(order.total)} total</p><a class="button" href="Products.html">Keep exploring</a></section>`;
  }

  async function initCheckout() {
    if (!requireAuth()) return;
    const host = document.querySelector('[data-checkout-content]');
    try {
      const [cart, currentUser] = await Promise.all([request('/cart'), request('/auth/me')]);
      if (!cart.items.length) {
        host.innerHTML = '<section class="empty-state"><i class="fa-solid fa-bag-shopping"></i><strong>Your cart is empty.</strong><p>Add something you love before checking out.</p><a class="button" href="Products.html">Shop products</a></section>';
        return;
      }
      document.querySelector('[name="customerName"]').value = currentUser.fullName || '';
      document.querySelector('[name="customerEmail"]').value = currentUser.email || '';
      document.querySelector('[name="customerPhone"]').value = currentUser.phone || '';
      document.querySelector('[data-checkout-items]').innerHTML = cart.items.map(item => `<div class="order-mini-item"><img src="${escapeHtml(item.imageUrl)}" alt="" data-fallback><span>${escapeHtml(item.name)} <span class="muted">&times; ${Number(item.quantity)}</span></span><strong>${money(item.lineTotal)}</strong></div>`).join('');
      bindImageFallbacks(document.querySelector('[data-checkout-items]'));
      document.querySelector('[data-checkout-subtotal]').textContent = money(cart.subtotal);
      document.querySelector('[data-checkout-discount]').textContent = `-${money(cart.discount)}`;
      document.querySelector('[data-checkout-total]').textContent = money(cart.total);
      document.querySelector('[data-checkout-form]').addEventListener('submit', async event => {
        event.preventDefault();
        const form = event.currentTarget;
        if (!form.reportValidity()) return;
        const button = form.querySelector('[type="submit"]');
        setBusy(button, true, 'Placing order...');
        try {
          const order = await request('/orders', { method: 'POST', body: JSON.stringify(Object.fromEntries(new FormData(form))) });
          renderOrderConfirmation(order);
          updateCartCount();
        } catch (error) {
          showMessage(document.querySelector('[data-checkout-error]'), error.message);
          setBusy(button, false);
        }
      });
    } catch (error) {
      host.innerHTML = `<div class="empty-state">${escapeHtml(error.message)}</div>`;
    }
  }

  function initAuth() {
    document.querySelectorAll('[data-password-toggle]').forEach(button => button.addEventListener('click', () => {
      const input = document.getElementById(button.dataset.passwordToggle);
      const showing = input.type === 'text';
      input.type = showing ? 'password' : 'text';
      button.innerHTML = `<i class="fa-regular fa-eye${showing ? '' : '-slash'}"></i>`;
      button.setAttribute('aria-label', showing ? 'Show password' : 'Hide password');
    }));
    const registerForm = document.querySelector('[data-register-form]');
    if (registerForm) {
      const password = registerForm.querySelector('[name="password"]');
      const confirm = registerForm.querySelector('[name="confirmPassword"]');
      const strength = document.querySelector('[data-password-strength] span');
      password.addEventListener('input', () => {
        const score = [password.value.length >= 10, /[A-Z]/.test(password.value), /[0-9]/.test(password.value), /[^A-Za-z0-9]/.test(password.value)].filter(Boolean).length;
        strength.style.width = `${score * 25}%`;
        strength.style.background = score < 2 ? '#d85454' : score < 4 ? '#e5a436' : '#25835d';
      });
      registerForm.addEventListener('submit', async event => {
        event.preventDefault();
        const message = document.querySelector('[data-form-message]');
        if (!registerForm.reportValidity()) return;
        if (password.value !== confirm.value) { showMessage(message, 'Your passwords do not match.'); return; }
        const button = registerForm.querySelector('[type="submit"]');
        setBusy(button, true, 'Creating account...');
        try {
          const payload = Object.fromEntries(new FormData(registerForm));
          delete payload.confirmPassword;
          const result = await request('/auth/register', { method: 'POST', body: JSON.stringify(payload) });
          localStorage.setItem('thinkmart.token', result.token);
          localStorage.setItem('thinkmart.user', JSON.stringify(result.user));
          location.href = 'Home-Page.html';
        } catch (error) { showMessage(message, error.message); setBusy(button, false); }
      });
    }
    const loginForm = document.querySelector('[data-login-form]');
    loginForm?.addEventListener('submit', async event => {
      event.preventDefault();
      if (!loginForm.reportValidity()) return;
      const button = loginForm.querySelector('[type="submit"]');
      setBusy(button, true, 'Signing in...');
      try {
        const result = await request('/auth/login', { method: 'POST', body: JSON.stringify(Object.fromEntries(new FormData(loginForm))) });
        localStorage.setItem('thinkmart.token', result.token);
        localStorage.setItem('thinkmart.user', JSON.stringify(result.user));
        const next = sessionStorage.getItem('thinkmart.next');
        sessionStorage.removeItem('thinkmart.next');
        const allowed = ['Products.html', 'Cart.html', 'Checkout.html', 'Home-Page.html'];
        location.href = next && allowed.includes(next.split('?')[0]) ? next : 'Home-Page.html';
      } catch (error) { showMessage(document.querySelector('[data-form-message]'), error.message); setBusy(button, false); }
    });
  }

  function initContact() {
    document.querySelector('[data-contact-form]')?.addEventListener('submit', async event => {
      event.preventDefault();
      const form = event.currentTarget;
      if (!form.reportValidity()) return;
      const button = form.querySelector('[type="submit"]');
      setBusy(button, true, 'Sending message...');
      try {
        await request('/contact', { method: 'POST', body: JSON.stringify(Object.fromEntries(new FormData(form))) });
        form.reset();
        showMessage(document.querySelector('[data-contact-message]'), 'Thanks for reaching out. Your message is with our team.', true);
        setBusy(button, false);
      } catch (error) { showMessage(document.querySelector('[data-contact-message]'), error.message); setBusy(button, false); }
    });
  }

  chrome();
  updateCartCount();
  initAuth();
  if (page === 'home') initHome();
  if (page === 'products') initProducts();
  if (page === 'cart') initCart();
  if (page === 'checkout') initCheckout();
  if (page === 'contact') initContact();
})();