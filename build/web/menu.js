document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".add-to-cart").forEach((button) => {
    button.addEventListener("click", () => {
      const menuItem = button.closest(".product-card");
      const name = menuItem.querySelector("h3").innerText;
      const price = parseFloat(
        menuItem.querySelector(".price").innerText.replace("Rs. ", "")
      );
      const quantity = parseInt(
        menuItem.querySelector(".quantity-controls span").innerText
      );

      if (quantity > 0) {
        const cart = JSON.parse(localStorage.getItem("cart") || "[]");
        const existingItem = cart.find((item) => item.name === name);

        if (existingItem) {
          existingItem.quantity += quantity;
        } else {
          cart.push({ name, price, quantity });
        }

        localStorage.setItem("cart", JSON.stringify(cart));
        alert(`${quantity} ${name}(s) added to cart!`);
      } else {
        alert("Please select a quantity first.");
      }
    });
  });
});

function increase(btn) {
  const span = btn.parentElement.querySelector("span");
  span.innerText = parseInt(span.innerText) + 1;
}

function decrease(btn) {
  const span = btn.parentElement.querySelector("span");
  if (parseInt(span.innerText) > 0) {
    span.innerText = parseInt(span.innerText) - 1;
  }
}
