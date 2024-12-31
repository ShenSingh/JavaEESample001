$(document).ready(function () {
    // Fetch customers from the server on page load
    function fetchCustomers() {
        $.ajax({
            url: 'http://localhost:8080/Sample01_war_exploded/customer',
            method: 'GET',
            success: function (data) {
                const tableBody = $('#customerTableBody');
                tableBody.empty();
                data.forEach(customer => {
                    const row = `
                   <tr data-id="${customer.id}">
                     <td>${customer.id}</td>
                     <td>${customer.name}</td>
                     <td>${customer.email}</td>
                     <td>
                       <button class="btn btn-warning btn-sm edit-btn" data-id="${customer.id}">Edit</button>
                       <button class="btn btn-danger btn-sm delete-btn" data-id="${customer.id}">Delete</button>
                     </td>
                   </tr>`;
                    tableBody.append(row);
                });
            },
            error: function () {
                alert('Failed to fetch customers!');
            }
        });
    }


    // Add customer via AJAX
    $('#addCustomerBtn').on('click', function () {
        const customerId = $('#customerId').val().trim();
        const customerName = $('#customerName').val().trim();
        const customerEmail = $('#customerEmail').val().trim();


        if (customerId && customerName && customerEmail) {
            $.ajax({
                url: 'http://localhost:8080/Sample01_war_exploded/customer',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: customerId,
                    name: customerName,
                    email: customerEmail,
                }),
                success: function () {
                    fetchCustomers();
                    $('#customerId').val('');
                    $('#customerName').val('');
                    $('#customerEmail').val('');
                    alert('Customer added successfully!');
                },
                error: function () {
                    alert('Failed to add customer!');
                }
            });
        } else {
            alert('Please fill out all fields!');
        }
    });


    // Edit button functionality (open modal with customer details)
    $(document).on('click', '.edit-btn', function () {
        const row = $(this).closest('tr');
        const customerId = row.find('td:eq(0)').text();
        const customerName = row.find('td:eq(1)').text();
        const customerEmail = row.find('td:eq(2)').text();


        // Populate modal with customer data
        $('#modalCustomerId').val(customerId);
        $('#modalCustomerName').val(customerName);
        $('#modalCustomerEmail').val(customerEmail);


        // Show modal
        $('#editModal').modal('show');
    });


    // Save changes to customer details
    $('#saveCustomerBtn').on('click', function () {
        const customerId = $('#modalCustomerId').val();
        const customerName = $('#modalCustomerName').val().trim();
        const customerEmail = $('#modalCustomerEmail').val().trim();


        if (customerName && customerEmail) {
            $.ajax({
                url: `http://localhost:8080/Sample01_war_exploded/customer`,
                method: 'PUT',
                contentType: 'application/json',
                data: JSON.stringify({
                    id: customerId,
                    name: customerName,
                    email: customerEmail,
                }),
                success: function () {
                    fetchCustomers();
                    $('#editModal').modal('hide');
                    alert('Customer details updated successfully!');
                },
                error: function () {
                    alert('Failed to update customer!');
                }
            });
        } else {
            alert('Please fill out all fields!');
        }
    });


    // Delete button functionality
    $(document).on('click', '.delete-btn', function () {
        const row = $(this).closest('tr');
        const customerId = row.data('id');


        if (confirm('Are you sure you want to delete this customer?')) {
            $.ajax({
                url: `http://localhost:8080/Sample01_war_exploded/customer?id=${customerId}`,
                method: 'DELETE',
                success: function () {
                    fetchCustomers();
                    alert('Customer deleted successfully!');
                },
                error: function () {
                    alert('Failed to delete customer!');
                }
            });
        }
    });


    // Initial fetch
    fetchCustomers();
});
