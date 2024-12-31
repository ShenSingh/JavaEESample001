// Load all the items to the itemSelectionTableBody table
function loadItems() {
    $.ajax({
        url: "http://localhost:8080/Sample01_war_exploded/item",
        type: "GET",
        success: function (data) {
            var itemSelectionTableBody = $("#itemSelectionTableBody");
            itemSelectionTableBody.empty();
            $.each(data, function (index, item) {
                itemSelectionTableBody.append(
                    "<tr>" +
                    "<td>" + item.id + "</td>" +
                    "<td>" + item.name + "</td>" +
                    "<td>" + item.price + "</td>" +
                    "<td>" + item.qtyOnHand + "</td>" +
                    "<td><button class='btn btn-primary' data-item='" + JSON.stringify(item) + "' onclick='addItemToOrderFromData(this)'>Add</button></td>" +
                    "</tr>"
                );
            });
        },
        error: function () {
            alert("Failed to load items!");
        }
    });
}

// New function to handle the 'data-item' attribute
function addItemToOrderFromData(button) {
    var selectedItem = JSON.parse($(button).attr('data-item'));  // Deserialize the item data
    addItemToOrder(selectedItem);
}


// Add the selected item to the cartTableBody table
function addItemToOrder(selectedItem) {
    var cartTableBody = $("#cartTableBody");

    // Check if the item is already in the cart
    var existingRow = cartTableBody.find("tr[data-id='" + selectedItem.id + "']");
    if (existingRow.length > 0) {
        // Increment the quantity and update the total price
        var qtyCell = existingRow.find(".qty");
        var qty = parseInt(qtyCell.text(), 10);
        qty += 1;
        qtyCell.text(qty);

        var totalCell = existingRow.find(".total");
        totalCell.text((qty * parseFloat(selectedItem.price)));
    } else {
        // Add a new row for the item
        cartTableBody.append(
            "<tr data-id='" + selectedItem.id + "'>" +
            "<td class='cart_item_code'>" + selectedItem.id + "</td>" +
            "<td>" + selectedItem.name + "</td>" +
            "<td class='price'>" + selectedItem.price + "</td>" +
            "<td>" +
            "<button class='btn btn-sm btn-success' onclick='changeQty(this, 1)'>+</button>" +
            " <span class='qty'>1</span> " +
            "<button class='btn btn-sm btn-danger' onclick='changeQty(this, -1)'>-</button>" +
            "</td>" +
            "<td class='total'>" + selectedItem.price + "</td>" +
            "</tr>"
        );
    }
    updateOrderTotal();
}

// Adjust the quantity and update the total
function changeQty(button, delta) {
    var row = $(button).closest("tr");
    var qtyCell = row.find(".qty");
    var qty = parseInt(qtyCell.text(), 10);

    if (qty + delta > 0) {
        qty += delta;
        qtyCell.text(qty);

        var price = parseFloat(row.find(".price").text());
        var totalCell = row.find(".total");
        totalCell.text((qty * price));
    } else {
        // Remove the row if quantity is reduced to 0
        row.remove();
    }
    updateOrderTotal();
}

// Update the order total in the table
function updateOrderTotal() {
    var total = 0;
    $("#cartTableBody tr").each(function () {
        var totalCell = $(this).find(".total");
        total += parseFloat(totalCell.text());
    });
    $("#orderTotal").text(total);
    calculateBalance(); // Recalculate balance whenever total changes
}

// Calculate the balance based on the cash amount
function calculateBalance() {
    var orderTotal = parseFloat($("#orderTotal").text());
    var cashAmount = parseFloat($("#cashAmount").val());
    var balance = cashAmount - orderTotal;

    $("#balance").text(balance > 0 ? balance : "0.00");
}

// Get the next orderId from the server
// Get the next orderId from the server
function getOrderId() {
    $.ajax({
        url: "http://localhost:8080/Sample01_war_exploded/latest",  // Modify to fetch the latest order ID from the server
        type: "GET",
        success: function (data) {
            var orderId = "O0001";  // Default ID in case no orders exist

            if (data && data.length > 0) {
                // Assuming the last order has the highest ID
                var lastOrderId = data[data.length - 1].id;  // Get the last order from the response
                if (lastOrderId && lastOrderId.startsWith('O')) {
                    var numericPart = lastOrderId.slice(1);  // Remove the 'O' part
                    var numericId = parseInt(numericPart, 10);  // Convert to a number
                    if (!isNaN(numericId)) {
                        numericId += 1;  // Increment the numeric part
                        orderId = "O" + numericId.toString().padStart(4, '0');  // Reformat with leading zeros
                    }
                }
            }

            // Update the displayed order ID
            document.getElementById("orderId").innerHTML = orderId;
        },
        error: function () {
            alert("Failed to fetch the latest order ID!");
        }
    });
}




// Updated function to load orders
function loadOrders() {
    // Fetch all orders from the server
    $.ajax({
        url: "http://localhost:8080/Sample01_war_exploded/order",
        type: "GET",
        success: function (data) {
            var orderTableBody = $("#ordersTableBody");
            orderTableBody.empty();

            // Loop through the data and create rows using template literals
            $.each(data, function (index, order) {
                const row = `
                <tr data-id="${order.id}">
                    <td>${order.id}</td>
                    <td>${order.cust_id}</td>
                    <td>${order.total}</td>
                    <td>${order.date}</td>
                    <td>
                        <button class="btn btn-primary btn-sm view-order-btn" data-id="${order.id}">View</button>
                    </td>
                </tr>`;
                orderTableBody.append(row);
            });

            console.log("Orders successfully loaded.");

            // Attach event listener for 'view-order-btn' buttons after appending the rows
            $(".view-order-btn").click(function () {
                var orderId = $(this).data("id");
                viewOrder(orderId);
            });
        },
        error: function () {
            alert("Failed to load orders!");
        }
    });
}

// Corrected viewOrder function
function viewOrder(orderId) {
    console.log("Viewing order with ID:", orderId);
    // Make an AJAX request to retrieve the order details and items
    $.ajax({
        url: `http://localhost:8080/Sample01_war_exploded/order_view`,  // Modify with the correct endpoint to get order details
        type: "GET",
        success: function (data) {
            // Find the order with the matching ID
            var order = data.find(order => order.id === orderId);

            // Check if the order was found
            if (order) {
                // Prepare order details for SweetAlert
                var orderDetails = `
                    <strong>Order ID:</strong> ${order.id}<br>
                    <strong>Customer ID:</strong> ${order.cust_id}<br>
                    <strong>Total:</strong> ${order.total}<br>
                    <strong>Date:</strong> ${order.date}<br><br>
                    <strong>Items:</strong><br>
                `;

                // Prepare item details
                var itemDetails = order.items.map(function(item) {
                    return `
                        <strong>Item ID:</strong> ${item.item_id}<br>
                        <strong>Quantity:</strong> ${item.qty}<br><br>
                    `;
                }).join('');

                // Combine order details and item details
                var fullDetails = orderDetails + itemDetails;

                // Show SweetAlert with the order and item details
                Swal.fire({
                    title: `Order #${order.id}`,
                    html: fullDetails,
                    icon: 'info',
                    confirmButtonText: 'Close'
                });
            } else {
                alert("Order not found!");
            }
        },
        error: function () {
            alert("Failed to retrieve order details!");
        }
    });
}

// apply placeOrderButtonAction
function placeOrderButtonAction() {
    // Check if the cart is empty
    var cartEmpty = $("#cartTableBody tr").length === 0;
    if (cartEmpty) {
        alert("Your cart is empty. Please add items before placing an order.");
        return;  // Exit the function if the cart is empty
    }

    var order = {
        cust_id: $("#orderCustomerId").val(),
        total: $("#orderTotal").text(),
        date: new Date().toISOString().slice(0, 10)
    };

    // Fetch the items in the cart
    var items = [];
    $("#cartTableBody tr").each(function () {
        var row = $(this);
        items.push({
            item_id: row.find(".cart_item_code").text(),
            qty: parseInt(row.find(".qty").text(), 10)
        });
    });

    // Send the order and items to the server
    $.ajax({
        url: "http://localhost:8080/Sample01_war_exploded/order",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({order: order, items: items}),
        success: function () {
            alert("Order placed successfully!");
            loadOrders();  // Reload the order table
            getOrderId();  // Get the next orderId
            loadItems();  // Reload the items table
        },
        error: function () {
            alert("Failed to place order!");
        }
    });
}


function loadCustomerIds() {
    // Fetch all customer ids from the server
    $.ajax({
        url: "http://localhost:8080/Sample01_war_exploded/customer",
        type: "GET",
        success: function (data) {
            var customerSelection = $("#orderCustomerId");
            customerSelection.empty();
            $.each(data, function (index, customer) {
                customerSelection.append(
                    "<option value='" + customer.id + "'>" + customer.id + "</option>"
                );
            });
        },
        error: function () {
            alert("Failed to load customer ids!");
        }
    });
}

// Call the loadItems function and getOrderId function when the page is ready
$(document).ready(function () {
    loadItems();
    getOrderId();  // Fetch the orderId when the page loads
    loadOrders();  // Fetch all orders when the page loads
    loadCustomerIds();

    // Attach the placeOrderButtonAction to the Place Order button
    $("#placeOrderBtn").click(function () {
        placeOrderButtonAction();
    });
});
