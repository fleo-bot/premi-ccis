document.getElementById('deleteUserForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    if (confirm(`Are you sure you want to delete the account for Username: ${username}?`)) {
        fetch(`http://localhost:8080/api/users/${username}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (response.ok) {
                alert('Account deleted successfully.');
                document.getElementById('user ID').value = '';
            } else {
                alert('Failed to delete account. User not found or already deleted.');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('An error occurred while deleting the account.');
        });
    }
});
