$(document).ready(function () {

    // Fetch all items and populate the table
    function fetchItems() {
        $.ajax({
            url: 'http://localhost:8080/Sample01_war_exploded/item',
            method: 'GET',
            success: function (response) {
                console.log('Fetched Data:', response); // Log fetched data to verify
                let data = typeof response === 'string' ? JSON.parse(response) : response;

                const tableBody = $('#itemTableBody');
                tableBody.empty();
                data.forEach(item => {
                    console.log('Item:', item); // Log each item to check qtyOnHand
                    const row = `
                        <tr data-id="${item.id}">
                            <td>${item.id}</td>
                            <td>${item.name}</td>
                            <td>${item.price}</td>
                            <td>${item.qtyOnHand}</td>
                            <td>
                                <button class="btn btn-warning btn-sm edit-item-btn" data-id="${item.id}">Edit</button>
                                <button class="btn btn-danger btn-sm delete-item-btn" data-id="${item.id}">Delete</button>
                            </td>
                        </tr>`;
                    tableBody.append(row);
                });
            },
            error: function () {
                alert('Failed to fetch items!');
            }
        });
    }

    // Add new item
    $('#addItemBtn').on('click', function () {
        const itemData = {
            id: $('#itemCode').val().trim(),
            name: $('#itemName').val().trim(),
            price: $('#itemPrice').val().trim(),
            qtyOnHand: $('#itemQtyOnHand').val().trim(), // Ensure qtyOnHand is included
        };

        console.log('Item Data:', itemData); // Log the data to ensure qtyOnHand is included

        if (!itemData.id || !itemData.name || !itemData.price || !itemData.qtyOnHand) {
            alert('Please fill all fields!');
            return;
        }

        $.ajax({
            url: 'http://localhost:8080/Sample01_war_exploded/item',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(itemData), // Send data as JSON
            success: function () {
                alert('Item added successfully!');
                fetchItems();
                $('#itemCode').val('');
                $('#itemName').val('');
                $('#itemPrice').val('');
                $('#itemQtyOnHand').val('');
            },
            error: function (xhr) {
                console.log("Error details:", xhr.responseText); // Log error response
                alert('Failed to add item!');
            }
        });
    });

    // Edit item button handler
    $(document).on('click', '.edit-item-btn', function () {
        const itemId = $(this).data('id');
        console.log('Fetching item with ID:', itemId); // Log the itemId

        $.ajax({
            url: `http://localhost:8080/Sample01_war_exploded/item?id=${itemId}`,
            method: 'GET',
            success: function (response) {
                console.log(response); // Log the response

                if (response && response.length > 0) {
                    const item = response[0]; // Access the first item in the array

                    // Populate modal fields
                    $('#editItemCode').val(item.id);
                    $('#editItemName').val(item.name);
                    $('#editItemPrice').val(item.price);
                    $('#editItemQtyOnHand').val(item.qtyOnHand);

                    // Show the modal after populating the fields
                    $('#editItemModal').modal('show');
                } else {
                    alert('Item data is invalid!');
                }
            },
            error: function () {
                alert('Failed to fetch item details!');
            }
        });
    });

    // Save edited item
    $('#saveEditedItemBtn').on('click', function (e) {
        e.preventDefault();

        const code = $('#editItemCode').val().trim();
        const name = $('#editItemName').val().trim();
        const price = $('#editItemPrice').val().trim();
        const qtyOnHand = $('#editItemQtyOnHand').val().trim();

        // Validate fields
        if (!code || !name || !price || !qtyOnHand) {
            alert('Please fill all fields before saving!');
            return;
        }

        $.ajax({
            url: `http://localhost:8080/Sample01_war_exploded/item`,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({ id: code, name: name, price: price, qtyOnHand: qtyOnHand }),
            success: function () {
                alert('Item updated successfully!');
                $('#editItemModal').modal('hide');
                fetchItems(); // Refresh the item list
            },
            error: function (xhr) {
                console.log("Error details:", xhr.responseText);
                alert('Failed to update item!');
            }
        });
    });

    // Delete item
    $(document).on('click', '.delete-item-btn', function () {
        const itemId = $(this).data('id');
        if (confirm('Are you sure you want to delete this item?')) {
            $.ajax({
                url: `http://localhost:8080/Sample01_war_exploded/item?id=${itemId}`,
                method: 'DELETE',
                success: function () {
                    alert('Item deleted successfully!');
                    fetchItems();
                },
                error: function (xhr) {
                    console.log("Error details:", xhr.responseText);
                    alert('Failed to delete item!');
                }
            });
        }
    });

    // Initial fetch for items
    fetchItems();
});
