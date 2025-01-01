<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Customer & Item Form</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body style="background: linear-gradient(to right, #4a90e2, #50d2c2);">
<!-- Navigation Header -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">ZenVeus</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link active" href="#customerSection">Customer</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#itemSection">Item</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#placeOrderSection">Place Order</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <!-- Customer Section -->
    <section id="customerSection" class="mb-5" style="margin-top: 50px">
        <h3>Customer Form</h3>
        <div class="row mb-2">
            <div class="col-md-4">
                <label for="customerId" class="form-label">Customer ID</label>
                <input type="text" class="form-control" id="customerId" placeholder="Enter Customer ID" style="background-color: transparent">
            </div>
            <div class="col-md-4">
                <label for="customerName" class="form-label">Customer Name</label>
                <input type="text" class="form-control" id="customerName" placeholder="Enter Customer Name" style="background-color: transparent">
            </div>
            <div class="col-md-4">
                <label for="customerEmail" class="form-label">Customer Email</label>
                <input type="email" class="form-control" id="customerEmail" placeholder="Enter Customer Email" style="background-color: transparent">
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 text-end">
                <button class="btn btn-success" id="addCustomerBtn" style="background-color: yellow;color: black">Add Customer</button>
            </div>
        </div>

        <!-- Customer Details Table -->
        <div class="mt-4">
            <table class="table table-bordered">
                <thead class="table-dark">
                <tr>
                    <th>Customer ID</th>
                    <th>Name</th>
                    <th>Email</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="customerTableBody">
                <!-- Dynamic rows will be added here -->
                </tbody>
            </table>
        </div>
    </section>

    <!-- Item Section -->
    <section id="itemSection" class="mb-5" style="display: none; margin-top: 50px">
        <h3>Item Form</h3>
        <div class="row mb-3">
            <div class="col-md-3">
                <label for="itemCode" class="form-label">Item Code</label>
                <input type="text" class="form-control" id="itemCode" placeholder="Enter Item Code" style="background-color: transparent">
            </div>
            <div class="col-md-3">
                <label for="itemName" class="form-label">Item Name</label>
                <input type="text" class="form-control" id="itemName" placeholder="Enter Item Name" style="background-color: transparent">
            </div>
            <div class="col-md-3">
                <label for="itemPrice" class="form-label">Item Price</label>
                <input type="number" class="form-control" id="itemPrice" placeholder="Enter Item Price" style="background-color: transparent">
            </div>
            <div class="col-md-3">
                <label for="itemQtyOnHand" class="form-label">Quantity On Hand</label>
                <input type="number" class="form-control" id="itemQtyOnHand" placeholder="Enter Quantity On Hand" style="background-color: transparent">
            </div>
        </div>
        <div class="row">
            <div class="col-md-12 text-end">
                <button class="btn btn-success" id="addItemBtn" style="background-color: yellow; color: black">Add Item</button >
            </div>
        </div>

        <!-- Item Details Table -->
        <div class="mt-4">
            <table class="table table-bordered">
                <thead class="table-dark">
                <tr>
                    <th>Item Code</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Quantity On Hand</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="itemTableBody">
                <!-- Dynamic rows will be added here -->
                </tbody>
            </table>
        </div>
    </section>

    <!-- Edit Item Modal -->
    <div class="modal fade" id="editItemModal" tabindex="-1" aria-labelledby="editItemModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editItemModalLabel">Edit Item</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="editItemCode" class="form-label">Item Code</label>
                        <input type="text" class="form-control" id="editItemCode" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="editItemName" class="form-label">Item Name</label>
                        <input type="text" class="form-control" id="editItemName">
                    </div>
                    <div class="mb-3">
                        <label for="editItemPrice" class="form-label">Item Price</label>
                        <input type="number" class="form-control" id="editItemPrice">
                    </div>
                    <div class="mb-3">
                        <label for="editItemQtyOnHand" class="form-label">Quantity On Hand</label>
                        <input type="number" class="form-control" id="editItemQtyOnHand">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" id="saveEditedItemBtn">Save Changes</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Customer Modal -->
    <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="editModalLabel">Edit Customer</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="modalCustomerId" class="form-label">Customer ID</label>
                        <input type="text" class="form-control" id="modalCustomerId" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="modalCustomerName" class="form-label">Customer Name</label>
                        <input type="text" class="form-control" id="modalCustomerName" placeholder="Enter Customer Name">
                    </div>
                    <div class="mb-3">
                        <label for="modalCustomerEmail" class="form-label">Customer Email</label>
                        <input type="email" class="form-control" id="modalCustomerEmail" placeholder="Enter Customer Email">
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" id="saveCustomerBtn">Save Changes</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Place Order Section -->
    <section id="placeOrderSection" class="mb-5" style="display: none;">
        <h3>Place Order</h3>
        <form id="placeOrderForm">
            <div class="row mb-3">
                <div class="col-md-4">
                    <label for="orderCustomerId" class="form-label">Customer ID</label>
                    <select class="form-control" id="orderCustomerId">
                        <option value="" disabled selected>Select Customer ID</option>
                    </select>
                </div>

            </div>
        </form>

        <!-- Item Selection Table -->
        <div class="mt-4">
            <h4>Select Items</h4>
            <table class="table table-bordered">
                <thead class="table-dark">
                <tr>
                    <th>Item Code</th>
                    <th>Item Name</th>
                    <th>Price</th>
                    <th>Quantity</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody id="itemSelectionTableBody">
                <!-- Dynamic rows will be loaded here -->
                </tbody>
            </table>
        </div>

        <!-- Cart Table -->
        <div class="mt-4">
            <h4>Cart</h4>
            <h3 id="orderId"></h3>
            <table class="table table-bordered">
                <thead class="table-dark">
                <tr>
                    <th>Item Code</th>
                    <th>Item Name</th>
                    <th>Quantity</th>
                    <th>Price</th>
                    <th>Total</th>
                </tr>
                </thead>
                <tbody id="cartTableBody">
                <!-- Dynamic rows will be added here -->
                </tbody>
            </table>
            <table class="table">
                <thead>
                <tr>
                    <th>Order Total</th>
                    <th>Cash Amount</th>
                    <th>Balance</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td id="orderTotal">0.00</td>
                    <td>
                        <input type="number" id="cashAmount" class="form-control" oninput="calculateBalance()" placeholder="Enter cash amount" style="background-color: transparent">
                    </td>
                    <td id="balance">0.00</td>
                </tr>
                </tbody>
            </table>
            <div class="mt-3">
                <button class="btn btn-success" id="placeOrderBtn">Place Order</button>
            </div>

        </div>

        <!-- All Orders Table -->
        <div class="mt-4">
            <h4>All Orders</h4>
            <table class="table table-bordered">
                <thead class="table-dark">
                <tr>
                    <th>Order ID</th>
                    <th>Customer ID</th>
                    <th>Date</th>
                    <th>Total</th>
                    <th>View</th>
                </tr>
                </thead>
                <tbody id="ordersTableBody">
                <!-- Dynamic rows will be added here -->
                </tbody>
            </table>
        </div>
    </section>
</div>


<!-- Bootstrap JS -->
<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

<!-- Custom JS -->
<script src="assets/js/Customer.js"></script>
<script src="assets/js/Item.js"></script>
<script src="assets/js/Order.js"></script>
<!-- Combined AJAX Script -->
<script>

    // catch the click event on the nav links
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', function (event) {
            // Prevent the default action
            event.preventDefault();

            // Get the section ID from the href attribute
            const sectionId = this.getAttribute('href').replace('#', '');

            // Show the selected section
            showSection(sectionId);

            //change navlink active to selected one
            document.querySelectorAll('.nav-link').forEach(link => {
                link.classList.remove('active');
            });
        });
    });
    function showSection(sectionId) {
        // Hide all sections
        document.getElementById('customerSection').style.display = 'none';
        document.getElementById('itemSection').style.display = 'none';
        document.getElementById('placeOrderSection').style.display = 'none';

        // Show the selected section
        document.getElementById(sectionId).style.display = 'block';

    }
</script>
</body>
</html>
