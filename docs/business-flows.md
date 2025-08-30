## Core Business Workflows

This document outlines the primary business workflows for the `commerce-kata` project. The goal is to create a clear,
shared understanding of the key end-to-end processes and to define how different application modules collaborate to
deliver business value.

---

### Workflow: A Customer Buys a Product 🛒

This workflow describes the "happy path" of a customer purchasing an item, detailing the interactions from discovery to
fulfillment.

1. **Discovery**: A user interacts with the **Catalog Module** to browse products and the **Search** functionality to
   find a specific item.
2. **Selection**: The user adds the desired item to their **Shopping Cart Module**.
3. **Checkout**: The user initiates the checkout process.
    - The **Identity Module** handles user login and retrieves saved addresses.
    - The **Shipping Module** calculates shipping costs based on the user's address and cart contents.
4. **Payment**: The **Billing Module** securely processes the payment by communicating with an external payment gateway.
5. **Order Placement**: Upon successful payment, the **Order Module** creates a final, immutable order record.
6. **Post-Order Processing**: The **Order Module** triggers asynchronous actions to complete the fulfillment process.
    - The **Inventory Module** decrements the stock count for the purchased items.
    - The **Notifications Module** enqueues a job to send an "Order Confirmed" email to the customer.

---

### Workflow: The Company Restocks an Item 📦

This internal workflow describes the process of replenishing inventory from a supplier when stock levels are low.

1. **Low Stock Detection**: An automated process within the **Inventory Module** detects that a product's stock has
   fallen below a predefined threshold.
2. **Procurement**: The **Inventory Module** signals the need for restocking. In a more advanced system, this would
   trigger a **Supplier Module** to create a Purchase Order. For this project, we can simulate this with an admin
   action.
3. **Receiving**: When the new stock arrives, an admin uses the **Inventory Module** to record the received goods.
4. **Stock Update**: The **Inventory Module** increments the sellable stock count for the product. The updated count is
   now reflected in the **Catalog Module**, making the product available for purchase again.

---

### Workflow: A Customer Returns a Product ↩️

This workflow describes the process of a customer returning a previously purchased item for a refund.

1. **Return Request**: A customer initiates a return request. This is handled by a **Customer Support Module** (or, in
   our case, can be an admin-triggered action within the **Order Module**).
2. **Item Inspection**: The returned item is received and inspected. An admin updates the status of the return in the
   system.
3. **Refund Processing**: If the return is approved, the **Billing Module** is called to process a refund to the
   customer's original payment method.
4. **Inventory Restock**: The **Inventory Module** is updated. Depending on the condition of the returned item, the
   stock is either added back to the sellable inventory or marked as damaged.
5. **Confirmation**: The **Notifications Module** enqueues a job to send an email to the customer confirming that their
   return has been processed and their refund has been issued.