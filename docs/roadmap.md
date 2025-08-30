## Roadmap

Roadmap for the `commerce-kata` project, guide by the main [business flows](`docs/business-flows.md`).

### Stage 1: The Walking Skeleton 💀

- **Focus**: Create the simplest possible end-to-end "buy" flow.

#### Core Functionality Scope
- **Catalog Module**: Seed a few products directly in the code/DB. Create a public API endpoint to list them.
- **Orders Module**: Create an API endpoint that accepts a `productID` and quantity, creating a basic order record in the database. No payment, inventory, or users yet.

#### Observability & Deployment Scope
- **Deployment**: Containerize the application with Docker. Set up a basic CI/CD pipeline (e.g., GitHub Actions) to automatically build and deploy the container to a cloud service (like Google Cloud Run or AWS App Runner).
- **Observability**: Implement structured logging.

---

### Stage 2: Users & Inventory 👤

- **Focus**: Make the core workflow realistic by adding users and stock management.

#### Core Functionality Scope
- **Identity Module**: Add user registration and login (JWT-based). Orders must now be associated with a `userID`.
- **Inventory Module**: Products in the catalog now have a stock count. Placing an order must decrement the stock within a single database transaction.

#### Observability & Deployment Scope
- **Deployment**: Add database migration steps to the CI/CD pipeline.
- **Observability**: Add basic application metrics (e.g., number of orders placed, users registered) using a library like Prometheus. Set up a local Grafana dashboard to view them.

---

### Stage 3: The Pre-Purchase Flow 🛒

- **Focus**: Build the user experience leading up to the checkout.

#### Core Functionality Scope
- **Shopping Cart Module**: Implement endpoints to add/remove items from a cart and view the cart's contents. Persist the cart in the database, linked to a user.
- **Background Job**: Create a simple scheduled job to clear abandoned carts older than 7 days.

#### Observability & Deployment Scope
- **Deployment**: The background job should be part of the deployment, running either as a separate process in the same container or on a schedule (e.g., CronJob).
- **Observability**: Implement distributed tracing (e.g., OpenTelemetry) to trace a user's journey from adding an item to their cart to placing the order.

---

### Stage 4: Handling Money & Notifications 💳

- **Focus**: Integrate payments and introduce asynchronous post-order processing.

#### Core Functionality Scope
- **Billing Module**: Integrate a payment gateway (e.g., Stripe) into the checkout flow. An order is only confirmed after successful payment.
- **Notifications Module**: After an order is placed, enqueue a background job to send a confirmation email (using a mock email service). This decouples email sending from the checkout process.

#### Observability & Deployment Scope
- **Deployment**: Manage secret keys for the payment gateway in the deployment environment.
- **Observability**: Add tracing to the payment processing and monitor the health and latency of the background job queue. Add logs for successful/failed email sends.

---

### Stage 5: Shipping & Search 📦

- **Focus**: Add core features that complete the e-commerce experience.

#### Core Functionality Scope
- **Shipping Module**: Add a basic shipping cost calculator to the checkout process based on a simple logic (e.g., flat rate).
- **Search**: Implement a basic text search capability within the **Catalog Module**'s API.

#### Observability & Deployment Scope
- **Deployment**: No major changes, focus on refining the existing pipeline.
- **Observability**: Create a more comprehensive Grafana dashboard that visualizes the full E2E flow: from cart additions to revenue generated and notifications sent.