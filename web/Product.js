document.addEventListener("DOMContentLoaded", function () {
  fetch("http://localhost:8080/CraveRush/api/products")
    .then(response => response.json())
    .then(data => {
      const list = document.getElementById("product-list");

      data.forEach(product => {
        const div = document.createElement("div");
        div.className = "product";

        div.innerHTML = `
          <img src="${product.image_path}" alt="${product.name}">
          <h3>${product.name}</h3>
          <p>${product.description}</p>
          <strong>Rs. ${product.price}</strong>
        `;

        list.appendChild(div);
      });
    })
    .catch(error => console.error("❌ Failed to load products:", error));
});