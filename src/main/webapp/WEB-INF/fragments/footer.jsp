<footer>
  <% Integer visitors = (Integer) application.getAttribute("visitors"); %>
  <% if (visitors == null) visitors = 0; %>
  <% visitors++; %>
  <% application.setAttribute("visitors", visitors); %>
  <h2>clicks counter: <%= (int) application.getAttribute("visitors") %></h2>
</footer>