let selectedEditHotelId = null; 
let selectedRoomTypeId = null;

document.addEventListener('DOMContentLoaded', () => {
  initGlobalUI();
  initDashboard();
  initRegisterEmployeeForm();
  initUpdateEmployeeModal(); 
  initAllModals();
  initAddGuestModal();
  initAddRoomModal();
  initAddHotelModal();
  initRoomTypeModal();
  initAddReservationModal();
  initTableActions();
});

function initGlobalUI() {
  const openBtns = document.querySelectorAll(".openModal");
  const closeBtns = document.querySelectorAll(".closeModal");
  const overlays = document.querySelectorAll(".modal__overlay");

  const closeModal = (modal) => {
    if (modal) modal.classList.remove("active");
  };

  openBtns.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      const targetSelector = btn.getAttribute("data-target");
      const modal = document.querySelector(targetSelector);
      if (modal) modal.classList.add("active");
    });
  });

  closeBtns.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();
      closeModal(btn.closest(".modal") || btn.closest(".auth-form"));
    });
  });

  overlays.forEach((overlay) => {
    overlay.addEventListener("click", () => {
      closeModal(overlay.closest(".modal"));
    });
  });

  document.querySelectorAll(".popular__card").forEach((card) => {
    card.addEventListener("click", () => {
      const hotelName = card.querySelector(".popular__card__header h4").innerText;
      const modal = document.querySelector("#thisModal");
      if (modal) {
        modal.classList.add("active");
        const modalTitle = modal.querySelector("#modalName");
        if (modalTitle) modalTitle.innerText = hotelName;
      }
    });
  });

  document.querySelectorAll(".input-container.select-menu").forEach((optionMenu) => {
    if (optionMenu.closest('#thisModal1') || optionMenu.id === 'hotel-dropdown' || optionMenu.id === 'edit-hotel-dropdown') return; 

    const selectBtn = optionMenu.querySelector(".input-layout.select-menu");
    const options = optionMenu.querySelectorAll(".option");
    const menuText = optionMenu.querySelector(".text");

    if(selectBtn) {
      selectBtn.addEventListener("click", () => {
        optionMenu.classList.toggle("active");
      });
    }

    options.forEach((option) => {
      option.addEventListener("click", () => {
        let selectedOption = option.querySelector(".option-text").innerText;
        if(menuText) menuText.innerText = selectedOption;
      });
    });
  });
}
//DashBoard
function initDashboard() {
  loadGuests();
  loadNoReservation();
  loadAmount();
  loadPendingReservations();
  loadRecentReservations();
  loadEmployees();
  loadRooms();
  loadRoomTypesList();
  loadPayment();
  loadHotels();
}

async function loadGuests() {
  try {
    const res = await fetch('/api/guests');
    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
    const data = await res.json();
    const el = document.querySelector('.dashboard__list .dashboard__item:nth-child(1) h1');
    const el2 = document.querySelector('#guestTableBody');
    if (el) el.textContent = data.length.toLocaleString();
    if(el2){
      el2.innerHTML = '';
      data.forEach(guest => {
        let dob = "";
        if (guest.account?.dob) {
          const d = new Date(guest.account.dob);
          dob = !isNaN(d.getTime()) ? d.toLocaleDateString("vi-VN") : "";
        }

        const row = document.createElement('tr');

        row.innerHTML = `
          <td>${guest.firstName} ${guest.lastName}</td>
          <td>${dob}</td>
          <td>${guest.account.email}</td>
          <td>${guest.phone}</td>
          <td>${guest.address}</td>
          <td>${guest.account.idNumber}</td>
          <td>${guest.origin}</td>
          <td>
            <button class="action-btn edit-btn" data-id="${guest.id}"><i class="ti-pencil"></i></button>
            <button class="action-btn delete-btn" data-id="${guest.id}"><i class="ti-trash"></i></button>
          </td>
        `;
        el2.appendChild(row);
      });
    }
  } catch (err) {
    console.error('Failed to load guests:', err);
  }
}

async function loadNoReservation() {
  try {
    const res = await fetch('/api/reservations');
    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
    const data = await res.json();
    const el = document.querySelector('.dashboard__list .dashboard__item:nth-child(2) h1');
    if (el) el.textContent = data.length.toLocaleString();
  } catch (err) {
    console.error('Failed to load reservations:', err);
  }
}

async function loadAmount() {
  try {
    const res = await fetch('/api/payments');
    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
    const data = await res.json();
    const el = document.querySelector('.dashboard__list .dashboard__item:nth-child(3) h1');
    if (el) {
      const total = data.reduce((sum, payment) => sum + payment.amount, 0);
      el.textContent = total.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    }
  } catch (err) {
    console.error('Failed to load amount:', err);
  }
}

async function loadPrice(id) {
  try {
    const res = await fetch(`/api/payments/reservation/${id}`);
    if (!res.ok) return null;
    const data = await res.json();
    return data.amount;
  } catch {
    return null;
  }
}

async function loadRecentReservations() {
  try {
    const res = await fetch('/api/reservations');
    if (!res.ok) throw new Error(`HTTP error! status: ${res.status}`);
    const data = await res.json();

    let tbody = null;
    let reservations = [];

    const tbody1 = document.querySelector('#recentTableBody');
    const tbody2 = document.querySelector('#reservationTableBody');

    if (tbody1) {
      tbody=tbody1
      tbody.innerHTML=``
      reservations = data.slice(0, 5);
    } else if (tbody2) {
      tbody=tbody2
      tbody2.innerHTML=``
      reservations = data
    } else return ;

    for (const reservation of reservations) {
      const row = document.createElement('tr');

      const nameTd = document.createElement('td');
      nameTd.textContent = reservation.guestName || 'N/A';
      row.appendChild(nameTd);

      const roomTd = document.createElement('td');
      if (reservation.rooms && reservation.rooms.length > 0) {
        const roomTypeNames = await Promise.all(
          reservation.rooms.map(async (room) => {
            return room.roomNumber;
          })
        );

        roomTd.textContent = roomTypeNames.join(', ');
      } else {
        roomTd.textContent = "N/A";
      }
      row.appendChild(roomTd);

      const hotelTd = document.createElement('td');
      if (reservation.rooms && reservation.rooms.length > 0) {
        const hotel = await Promise.all(
          reservation.rooms.map(async (room) => {
            return room.hotelName;
          })
        );
        hotelTd.textContent = hotel.join(', ');
      } else {
        hotelTd.textContent = "N/A";
      }
      row.appendChild(hotelTd);

      const employeeTd = document.createElement('td');
      employeeTd.textContent = reservation.employeeName || 'N/A';
      row.appendChild(employeeTd);

      const inTd = document.createElement('td');
      inTd.textContent = reservation.checkin ? new Date(reservation.checkin).toLocaleDateString('vi-VN') : 'N/A';
      row.appendChild(inTd);

      const outTd = document.createElement('td');
      outTd.textContent = reservation.checkout ? new Date(reservation.checkout).toLocaleDateString('vi-VN') : 'N/A';
      row.appendChild(outTd);

      const priceTd = document.createElement('td');
      const priceVal = await loadPrice(reservation.id);
      priceTd.textContent = priceVal != null 
        ? Number(priceVal).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' }) 
        : '0₫';
      row.appendChild(priceTd);

      const statusTd = document.createElement('td');
      const statusSpan = document.createElement('span');
      const statusText = (reservation.status || 'Pending');
      
      statusSpan.textContent = statusText;
      statusSpan.classList.add('status');
      
      const lowerStatus = statusText.toLowerCase();
      if (lowerStatus === 'completed') statusSpan.classList.add('success');
      else if (['canceled', 'cancelled', 'no_show'].includes(lowerStatus)) statusSpan.classList.add('cancel');
      else statusSpan.classList.add('pending');

      statusTd.appendChild(statusSpan);
      row.appendChild(statusTd);

      const actionsTd = document.createElement('td');
      actionsTd.innerHTML = `
        <button class="action-btn edit-btn" data-id="${reservation.id}"><i class="ti-pencil"></i></button>
        <button class="action-btn delete-btn" data-id="${reservation.id}"><i class="ti-trash"></i></button>
      `;
      row.appendChild(actionsTd);

      tbody.appendChild(row);

      row.addEventListener("click", (e) => {
        if (e.target.closest('.action-btn')) return
        else window.location.href = `/payments/${reservation.id}`;
      });

    }
  } catch (err) {
      console.error('Failed to load recent reservations:', err);
  }
}

async function loadEmployees() {
  try {
    const response = await fetch('/api/employees/');
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const employees = await response.json();
    
    const tableBody = document.querySelector('#employeeTableBody');
    if (!tableBody) {
      console.warn("Employee table body (.employee__table tbody) not found. Data fetched but not displayed.");
      return;
    }

    tableBody.innerHTML = ''; 

    employees.forEach(employee => {
      const row = document.createElement('tr');

      const nameTd = document.createElement('td');
      nameTd.textContent = `${employee.firstname} ${employee.lastname}`; 
      row.appendChild(nameTd);

      const hotelTd = document.createElement('td');
      hotelTd.textContent = employee.hotelName || 'N/A'; 
      row.appendChild(hotelTd);

      const positionTd = document.createElement('td');
      positionTd.textContent = employee.position || 'N/A';
      row.appendChild(positionTd);
      
      const userTd = document.createElement('td');
      userTd.textContent = employee.username || (employee.account ? employee.account.username : 'N/A');
      row.appendChild(userTd);

      const roleTd = document.createElement('td');
      roleTd.textContent = employee.role || (employee.account ? employee.account.role : 'N/A');
      row.appendChild(roleTd);

      const salaryTd = document.createElement('td');
      salaryTd.textContent = employee.salary;
      row.appendChild(salaryTd);
      
      const actionsTd = document.createElement('td');
      actionsTd.innerHTML = `
        <button class="action-btn edit-btn" data-id="${employee.id}"><i class="ti-pencil"></i></button>
        <button class="action-btn delete-btn" data-id="${employee.id}"><i class="ti-trash"></i></button>
      `;
      row.appendChild(actionsTd);

      tableBody.appendChild(row);
  });
  
  initEmployeeTableActions(); 

  } catch (err) {
    console.error('Failed to load employees:', err);
    const tableContainer = document.querySelector('.employee__list__container');
    if (tableContainer) {
        tableContainer.innerHTML = '<p class="error-message">Failed to load employee data. Please check the server.</p>';
    }
  }
}

async function loadHotels() {
  const tableBody = document.querySelector('#hotelTableBody');
  if (!tableBody) return;

  try {
    const response = await fetch('/api/hotels');
    if (!response.ok) throw new Error('Failed to fetch hotels.');

    const hotels = await response.json();
    tableBody.innerHTML = '';

    if (hotels.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="5" class="error-message">No hotels found.</td></tr>`;
      return;
    }

    hotels.forEach(hotel => {
      const row = tableBody.insertRow();
      row.setAttribute('data-id', hotel.id);

      row.innerHTML = `
        <td>${hotel.name}</td>
        <td>${hotel.address}</td>
        <td>${hotel.phone}</td>
        <td>${hotel.email}</td>
        <td>${hotel.rating}</td>
        <td class="actions">
          <button class="action-btn edit-btn" data-id="${hotel.id}"><i class="ti-pencil"></i></button>
          <button class="action-btn delete-btn" data-id="${hotel.id}"><i class="ti-trash"></i></button>
        </td>
      `;

      row.addEventListener("click", (e) => {
        if (e.target.closest('.action-btn')) return
        else window.location.href = `/hotels/${hotel.id}`;;
      });

      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error('Error fetching hotels', error);
    tableBody.innerHTML = `<tr><td colspan="6" class="error-message">Error loading hotel data.</td></tr>`;
  }
}

async function loadRooms() {
  const tableBody = document.querySelector('#roomTableBody');
  if (!tableBody) return;
  const hotelId = document.querySelector("meta[name='hotel-id']").content;

  try {
    const response = await fetch(`/api/rooms/hotels/${hotelId}`);
    if (!response.ok) throw new Error('Failed to fetch rooms.');
    
    const rooms = await response.json();
    tableBody.innerHTML = '';

    if (rooms.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="6" class="error-message">No rooms found.</td></tr>`;
      return;
    }

    rooms.forEach(room => {
      const roomTypeName = room.roomTypeName || 'N/A';
      const status = room.status || 'Free'; 
      
      const row = tableBody.insertRow();
      row.setAttribute('data-id', room.id); 
      
      row.innerHTML = `
        <td>${room.roomNumber}</td>
        <td>${room.hotelName}</td>
        <td>${roomTypeName}</td>
        <td>${room.floor}</td>
        <td>${status}</td>
        <td class="actions">
          <button class="action-btn edit-btn" data-id="${room.id}"><i class="ti-pencil"></i></button>
          <button class="action-btn delete-btn" data-id="${room.id}"><i class="ti-trash"></i></button>
        </td>
      `;
    });
  } catch (error) {
    console.error('Error fetching rooms:', error);
    tableBody.innerHTML = `<tr><td colspan="6" class="error-message">Error loading room data.</td></tr>`;
  }
}

async function loadRoomTypesList() {
  const listContainer = document.querySelector('#roomTypeTableBody');
  if (!listContainer) return;
  
  listContainer.innerHTML = '';
  
  try {
    const response = await fetch(`/api/roomtypes`);
    if (!response.ok) throw new Error('Failed to fetch room types.');

    const roomTypes = await response.json();

    if (roomTypes.length === 0) {
      listContainer.innerHTML = `<p>No room types defined yet.</p>`;
      return;
    }

    roomTypes.forEach(rt => {
      const row = listContainer.insertRow();
      row.setAttribute('data-id', rt.id);
        
      row.innerHTML = `
        <td>${rt.name}</td>
        <td>${rt.basePrice}</td>
        <td>${rt.capacity}</td>
        <td class="actions">
          <button class="action-btn edit-btn" data-id="${rt.id}"><i class="ti-pencil"></i></button>
          <button class="action-btn delete-btn" data-id="${rt.id}"><i class="ti-trash"></i></button>
        </td>
      `;
    });
  } catch (error) {
    console.error('Error fetching room types:', error);
    listContainer.innerHTML = `<p class="error-message">Error loading room types.</p>`;
  }
}

async function loadPayment() {
  const tableBody = document.querySelector('#paymentTableBody');
  const reservationId = document.querySelector("meta[name='reservation-id']")?.content;

  if (!tableBody || !reservationId) return;

  try {
    const response = await fetch(`/api/payments/reservation/${reservationId}`);

    if (!response.ok) {
      throw new Error('Failed to fetch payments');
    }

    const payment = await response.json();

    tableBody.innerHTML = '';

    if (!payment) {
      tableBody.innerHTML = `
        <tr>
          <td colspan="3">No payments found</td>
        </tr>
      `;
      return;
    }
    if (payment.payment_date){
      payday = new Date(payment.payment_date).toLocaleDateString('vi-VN');
    } else {
      payday = "Not Paid Yet"
    }

    const row = tableBody.insertRow();
    row.innerHTML = `
      <td>${payment.reservation.guestName}</td>
      <td>${payment.reservation.rooms.map(room => room.roomNumber).join(', ')}</td>
      <td>${payment.reservation.rooms.map(room => room.hotelName).join(', ')}</td>
      <td>${new Date(payment.reservation.checkin).toLocaleDateString('vi-VN')}</td>
      <td>${new Date(payment.reservation.checkout).toLocaleDateString('vi-VN')}</td>
      <td>${Number(payment.amount).toLocaleString('vi-VN', {
        style: 'currency',
        currency: 'VND'
      })}</td>
      <td>${payday}</td>
      <td>${payment.method}</td>
      <td class="actions">
        <button class="action-btn edit-btn" data-id="${payment.id}"><i class="ti-pencil"></i></button>
      </td>
    `;

  } catch (error) {
    console.error('Error fetching payments:', error);
    tableBody.innerHTML = `
      <tr>
        <td colspan="3" class="error-message">
          Error loading payment data
        </td>
      </tr>
    `;
  }
}

//Form & Modals
function initAllModals() {
  document.querySelectorAll('.openModal').forEach(button => {
      button.addEventListener('click', () => {
        const targetId = button.getAttribute('data-target');
        const modal = document.querySelector(targetId);
        if (modal) {
          modal.classList.add('active');
        }
      });
  });

  document.querySelectorAll('.closeModal').forEach(button => {
    button.addEventListener('click', () => {
      button.closest('.modal').classList.remove('active');
    });
  });
  
  document.querySelectorAll('.modal__overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
      if (e.target === overlay) {
        overlay.closest('.modal').classList.remove('active');
      }
    });
  });
}
  //guest
function initAddGuestModal() {
  const modal = document.querySelector('#addGuestModal')
  if (!modal) return;
  document.querySelector('#confirmAddGuestBtn').addEventListener('click', async () => {
    const firstName = document.querySelector('#guestFirstName').value.trim();
    const lastName = document.querySelector('#guestLastName').value.trim();
    const phone = document.querySelector('#guestPhone').value.trim();
    const email = document.querySelector('#guestEmail').value.trim();
    const username = document.querySelector('#guestUsername').value.trim();
    const password = document.querySelector('#guestPassword').value.trim();
    const address = document.querySelector('#guestAddress').value.trim();
    const origin = document.querySelector('#guestOrigin').value.trim();
    const dob = document.querySelector('#dob').value.trim();
    const idNumber = document.querySelector('#idNumber').value.trim();

    if (!firstName || !lastName || !username || !email) {
      alert("First Name, Last Name, Username, ID Number, and Email are required.");
      return;
    }

    const newGuest = {
      firstName,
      lastName,
      address,
      origin,
      phone,
      account: {
        username,
        email,
        password,
        dob,
        idNumber
      }
    };

    try {
      console.log(newGuest);
      const res = await fetch('/api/guests', {
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newGuest)
      });

      if (!res.ok) throw new Error('Failed to add guest');

      alert("Guest added successfully!");
      document.querySelector('#addGuestModal').classList.remove('active');
      loadGuests();

    } catch (err) {
      console.error(err);
      alert("Error adding guest.");
    }
  });
}

async function openUpdateGuestModal(guestId) {
  const modal = document.querySelector('#updateGuestModal');
  if (!modal) return;

  try {
    const res = await fetch(`/api/guests/${guestId}`);
    if (!res.ok) throw new Error("Failed to fetch guest");

    const guest = await res.json();

    modal.querySelector('#updateGuestId').value = guest.id || "";
    modal.querySelector('#updateAccountId').value = guest.accountId || "";
    modal.querySelector('#updateFirstName').value = guest.firstName || "";
    modal.querySelector('#updateLastName').value = guest.lastName || "";
    modal.querySelector('#updateEmail').value = guest.account.email || "";
    modal.querySelector('#updatePhone').value = guest.phone || "";
    modal.querySelector('#updateAddress').value = guest.address || "";
    modal.querySelector('#updateOrigin').value = guest.origin || "";
    modal.querySelector('#updateDob').value = guest.account.dob || "";
    modal.querySelector('#updateIdNumber').value = guest.account.idNumber || "";

    modal.classList.add('active');

    document.querySelector("#confirmUpdateGuestBtn").addEventListener("click", async () => {
      const id = document.querySelector('#updateGuestId').value;

      const rawDob = document.querySelector('#updateDob').value;

      let dobDateTime = null;
      if (rawDob) {
          dobDateTime = rawDob + "T00:00:00";
      }
  
      const payload = {
        firstName: document.querySelector('#updateFirstName').value,
        lastName: document.querySelector('#updateLastName').value,
        phone: document.querySelector('#updatePhone').value,
        address: document.querySelector('#updateAddress').value,
        origin: document.querySelector('#updateOrigin').value,
        account: {
          email: document.querySelector('#updateEmail').value,
          dob: dobDateTime,
          idNumber: document.querySelector('#updateIdNumber').value
        }
      };

      try {
        const res = await fetch(`/api/guests/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error("Update failed");

        alert("Guest updated successfully!");

        document.querySelector("#updateGuestModal").classList.remove("active");

        loadGuests(); 
      } catch (err) {
        console.error(err);
        alert("Failed to update guest");
      }
    });
  } catch (err) {
    console.error("Error loading guest:", err);
  }
}
  //hotel
function initAddHotelModal() {
  const modal = document.querySelector('#addHotelModal');
  if (!modal) return;
  const confirmBtn = modal.querySelector('#confirmAddHotelBtn');
  confirmBtn.addEventListener('click', async (e) => {
    e.preventDefault();

    const name = modal.querySelector('#addHotelName').value;
    const address = modal.querySelector('#addHotelAddress').value;
    const phone = modal.querySelector('#addHotelPhone').value;
    const email = modal.querySelector('#addHotelEmail').value;
    const rating = modal.querySelector('#addHotelRating').value;

    if (!name || !address || !phone || !email || !rating) {
      alert('Please fill in all required fields.');
      return;
    }

    const newHotel = {
      name: name,
      address: address,
      phone: phone,
      email: email,
      rating: parseFloat(rating)
    };

    try {
      console.log(newHotel);
      const response = await fetch('/api/hotels', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newHotel)
      });
      if (!response.ok) throw new Error('Failed to create hotel.');

      alert('Hotel added successfully!');
      modal.classList.remove('active');
      loadHotels();
    } catch (error) {
      console.error('Error creating hotel:', error);
      alert('Error adding hotel: ' + error.message);
    }
  });
}

async function openUpdateHotelModal(hotelId) {
  const modal = document.querySelector('#updateHotelModal');
  if (!modal) return;
  
  try {
    const res = await fetch(`/api/hotels/${hotelId}`);
    if (!res.ok) throw new Error('Failed to fetch hotel data.');

    const hotel = await res.json();

    modal.querySelector('#updateHotelId').value = hotel.id || '';
    modal.querySelector('#updateHotelName').value = hotel.name || '';
    modal.querySelector('#updateHotelAddress').value = hotel.address || '';
    modal.querySelector('#updateHotelPhone').value = hotel.phone || '';
    modal.querySelector('#updateHotelEmail').value = hotel.email || '';
    modal.querySelector('#updateHotelRating').value = hotel.rating || '';

    modal.classList.add('active');

    document.querySelector("#confirmUpdateHotelBtn").addEventListener("click", async () => {
      const updatedHotel = {
        id: modal.querySelector('#updateHotelId').value,
        name: modal.querySelector('#updateHotelName').value,
        address: modal.querySelector('#updateHotelAddress').value,
        phone: modal.querySelector('#updateHotelPhone').value,
        email: modal.querySelector('#updateHotelEmail').value,
        rating: parseFloat(modal.querySelector('#updateHotelRating').value)
      };
      
      try {
        const response = await fetch(`/api/hotels/${hotelId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedHotel)
        });
        if (!response.ok) throw new Error('Failed to update hotel.');
        
        alert('Hotel updated successfully!');
        
        modal.classList.remove('active');
        
        loadHotels();
      }
      catch (error) {
        console.error('Error updating hotel:', error);
        alert('Error updating hotel: ' + error.message);
      }
    }
    );
  } catch (error) {
    console.error('Error loading hotel data:', error);
    alert('Error loading hotel data: ' + error.message);
  }
}


  //room
async function initAddRoomModal() {
  if (!document.querySelector("meta[name='hotel-id']")) return;
  const hotelId = document.querySelector("meta[name='hotel-id']").content;
  if (!hotelId) return alert('Hotel Id is missing for adding room');

  const modal = document.querySelector('#addRoomModal');
  if (!modal) return;
  
  const confirmBtn = modal.querySelector('#confirmAddRoomBtn');

  const res = await fetch(`/api/roomtypes`);

  const roomTypes = await res.json();
  
  const typeOptions = modal.querySelector('#addRoomTypeOptions');
  const typeText = modal.querySelector('#addRoomTypeText');

  roomTypes.forEach(type => {
    const option = document.createElement('li');
    option.classList.add('option');
    option.dataset.id = type.id;
    option.innerHTML = `<span class="option-text">${type.name}</span>`;
    typeOptions.appendChild(option);
  
    option.addEventListener('click', (e) => {
      e.stopPropagation();
  
      typeText.innerText = type.name;
      typeText.style.color = "#333";
  
      selectedRoomTypeId = type.id;
  
      option.closest('.input-container.select-menu')
        .classList.remove('active');
    });
  });
  
  const typeSelect = modal.querySelector('#addRoomTypeOptions');

  if (typeSelect) typeSelect.addEventListener('click', () => typeSelect.classList.toggle('active'));
  
  confirmBtn.addEventListener('click', async (e) => {
    e.preventDefault();
    
    const roomNumber = modal.querySelector('#addRoomNumber').value;
    const floor = modal.querySelector('#addRoomFloor').value;
    
    if (!roomNumber || !floor || !selectedRoomTypeId || !hotelId) {
      alert('Please select Room Type, and provide Room Number/Floor.');
      return;
    }
    
    const newRoom = {
      roomNumber: roomNumber,
      floor: parseInt(floor),
      status: 'AVAILABLE', 
      hotel: { id: hotelId },
      roomType: { id: selectedRoomTypeId }
    };

    try {
        console.log(newRoom);
        const response = await fetch('/api/rooms', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newRoom)
        });

      if (!response.ok) throw new Error('Failed to create room.');

      alert('Room added successfully!');
      modal.classList.remove('active');
      loadRooms();
    } catch (error) {
      console.error('Error creating room:', error);
      alert('Error adding room: ' + error.message);
    }
  });
}

async function openUpdateRoomModal(roomId) {
  if (!roomId) return alert("Room Id is missing for update");

  const modal = document.querySelector("#updateRoomModal");
  if (!modal) return;

  const confirmBtn = document.querySelector("#confirmUpdateRoomBtn");

  const room = await fetch(`/api/rooms/${roomId}`).then(res => {
    if (!res.ok) throw new Error("Failed to fetch room data.");
    return res.json();
  });

  modal.querySelector("#updateRoomId").value = room.id || "";
  modal.querySelector("#updateRoomNumber").value = room.roomNumber || "";
  modal.querySelector("#updateRoomFloor").value = room.floor || "";

  const hotelId = document.querySelector("meta[name='hotel-id']").content;
  if (!hotelId) return alert("Hotel Id is missing for updating room");

  let selectedRoomStatus = room.status || "AVAILABLE";
  selectedRoomTypeId = room.roomTypeId || null;

  const statusText = modal.querySelector("#updateRoomStatusText");
  const statusOptions = modal.querySelector("#updateRoomStatusOptions");

  statusText.innerText = room.status || "Select Status";
  statusText.style.color = room.status ? "#333" : "#888";

  statusOptions.innerHTML = `
    <span class="option-text">
      <li class="option" data-status="AVAILABLE">Available</li>
      <li class="option" data-status="OCCUPIED">Occupied</li>
      <li class="option" data-status="MAINTENANCE">Maintenance</li>
    </span>
  `;

  statusOptions.querySelectorAll("li").forEach(option => {
    option.addEventListener("click", (e) => {
      e.stopPropagation();
      statusText.innerText = option.innerText;
      statusText.style.color = "#333";

      selectedRoomStatus = option.dataset.status;

      option.closest(".select-menu").classList.remove("active");
    });
  });

  const statusSelect = modal.querySelector(".select-menu.room-status");
  if (statusSelect) {
    statusSelect.onclick = () => statusSelect.classList.toggle("active");
  }

  const typeText = modal.querySelector("#updateRoomTypeText");
  const typeOptions = modal.querySelector("#updateRoomTypeOptions");

  typeOptions.innerHTML = "";
  typeText.innerText = room.roomTypeName || "Select Room Type";
  typeText.style.color = room.roomTypeName ? "#333" : "#888";

  const roomTypes = await fetch(`/api/roomtypes`).then(res => res.json());

  roomTypes.forEach(type => {
    const option = document.createElement("li");
    option.classList.add("option");
    option.dataset.id = type.id;
    option.innerHTML = `<span class="option-text">${type.name}</span>`;
    typeOptions.appendChild(option);

    option.addEventListener("click", (e) => {
      e.stopPropagation();

      typeText.innerText = type.name;
      typeText.style.color = "#333";

      selectedRoomTypeId = type.id;

      option.closest(".select-menu").classList.remove("active");
    });
  });

  const typeSelect = modal.querySelector(".select-menu.room-type");
  if (typeSelect) {
    typeSelect.onclick = () => typeSelect.classList.toggle("active");
  }

  modal.classList.add("active");

  confirmBtn.onclick = async (e) => {
    e.preventDefault();

    const updatedRoomId = modal.querySelector("#updateRoomId").value;
    const roomNumber = modal.querySelector("#updateRoomNumber").value;
    const floor = modal.querySelector("#updateRoomFloor").value;

    if (!roomNumber || !floor || !selectedRoomTypeId || !hotelId) {
      alert("Please select Room Type and provide Room Number/Floor.");
      return;
    }

    const updateRoom = {
      roomId: updatedRoomId,
      roomNumber,
      floor: parseInt(floor),
      status: selectedRoomStatus,
      hotel: { id: hotelId },
      roomType: { id: selectedRoomTypeId }
    };

    try {
      console.log(updateRoom);

      const response = await fetch(`/api/rooms/${updatedRoomId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updateRoom)
      });

      if (!response.ok) throw new Error("Failed to update room.");

      alert("Room updated successfully!");
      modal.classList.remove("active");
      loadRooms();
    } catch (error) {
      console.error("Error updating room:", error);
      alert("Error updating room: " + error.message);
    }
  };
}

  //room type
function initRoomTypeModal() {
  const modal = document.querySelector('#addRoomTypeModal');
  if (!modal) return;
  const confirmBtn = modal.querySelector('#confirmAddRoomType');
  
  modal.addEventListener('transitionend', (e) => {
    if (e.propertyName === 'opacity' && modal.classList.contains('active')) {
      loadRoomTypesList();
      modal.querySelector('#newRoomTypeName').value = '';
      modal.querySelector('#newRoomTypeBasePrice').value = '';
      modal.querySelector('#newRoomTypeCapacity').value = '';
      modal.querySelector('#newRoomTypeDescription').value = '';
    }
  });

  confirmBtn.addEventListener('click', async (e) => {
    e.preventDefault();
    
    const name = modal.querySelector('#newRoomTypeName').value;
    const basePrice = modal.querySelector('#newRoomTypeBasePrice').value;
    const capacity = modal.querySelector('#newRoomTypeCapacity').value;
    const description = modal.querySelector('#newRoomTypeDescription').value;
    
    if (!name || !basePrice || !capacity) {
      alert('Name, Base Price, and Capacity are required.');
      return;
    }
    
    const newRoomType = {
      name: name,
      basePrice: parseInt(basePrice),
      capacity: parseInt(capacity),
      description: description || null
    };

    try {
      const response = await fetch('/api/roomtypes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newRoomType)
      });

      if (!response.ok) throw new Error('Failed to create room type.');

      alert('Room Type created successfully!');
      modal.classList.remove('active');
      loadRoomTypesList(); 
    } catch (error) {
      console.error('Error creating room type:', error);
      alert('Error creating room type: ' + error.message);
    }
  });
}
  //reservations
async function initAddReservationModal() {
  const modal = document.querySelector('#addReservationModal');
  if (!modal) return;

  const confirmBtn = modal.querySelector('#confirmAddReservation');

  const guestText = modal.querySelector('#addGuestText');
  const hotelText = modal.querySelector('#addHotelText');
  const roomText  = modal.querySelector('#addRoomText');
  const statusText = modal.querySelector('#addStatusText');
  const employeeId = localStorage.getItem("employeeId")

  let selectedGuestId = null;
  let selectedHotelId = null;
  let selectedRoomId = null;
  let selectedStatus = null;

  const guestOptions = modal.querySelector('#addGuestOptions');
  guestOptions.innerHTML = '';

  const guestsRes = await fetch('/api/guests');
  const guests = await guestsRes.json();

  guests.forEach(g => {
    const li = document.createElement('li');
    li.className = 'option';
    li.textContent = `${g.firstName} ${g.lastName}`;

    li.onclick = e => {
      e.stopPropagation();
      selectedGuestId = g.id;
      guestText.textContent = li.textContent;
      guestText.style.color = '#333';
      guestOptions.parentElement.classList.remove('active');
    };

    guestOptions.appendChild(li);
  });

  const hotelOptions = modal.querySelector('#addHotelOptions');
  hotelOptions.innerHTML = '';

  const hotelsRes = await fetch('/api/hotels');
  const hotels = await hotelsRes.json();

  hotels.forEach(h => {
    const li = document.createElement('li');
    li.className = 'option';
    li.textContent = h.name;

    li.onclick = async e => {
      e.stopPropagation();
      selectedRoomId = null;
      selectedHotelId = h.id;
      hotelText.textContent = h.name;
      hotelText.style.color = '#333';
      hotelOptions.parentElement.classList.remove('active');

      // load rooms by hotel
      await loadRoomsByHotel(h.id);
    };

    hotelOptions.appendChild(li);
  });

  async function loadRoomsByHotel(hotelId) {
    const roomOptions = modal.querySelector('#addRoomOptions');
    roomOptions.innerHTML = '';
    roomText.textContent = 'Select Room';
    roomText.style.color = '#999';
    selectedRoomId = null;
  
    const res = await fetch(`/api/rooms/hotels/${hotelId}`);
    const rooms = await res.json();
  
    rooms.forEach(r => {
      if (r.status == "AVAILABLE") {
      const li = document.createElement('li');
      li.className = 'option';
      li.textContent = `Room ${r.roomNumber}`;
  
      li.onclick = e => {
        e.stopPropagation();
        selectedRoomId = r.id;
        roomText.textContent = li.textContent;
        roomText.style.color = '#333';
        roomOptions.parentElement.classList.remove('active');
      };
  
      roomOptions.appendChild(li);
    }});
  }  

  modal.querySelectorAll('#addStatusOptions .option').forEach(opt => {
    opt.onclick = e => {
      e.stopPropagation();
      selectedStatus = opt.dataset.value;
      statusText.textContent = opt.textContent;
      statusText.style.color = '#333';
      opt.closest('.select-menu').classList.remove('active');
    };
  });

  confirmBtn.onclick = async e => {
    e.preventDefault();

    const checkIn  = modal.querySelector('#addCheckin').value;
    const checkOut = modal.querySelector('#addCheckout').value;

    if (!selectedGuestId || !selectedHotelId || !selectedRoomId ||
        !checkIn || !checkOut || !selectedStatus) {
      alert('Please fill all required fields.');
      return;
    }

    const reservation = {
      guest: { id: selectedGuestId },
      reservationRooms: [
        {
          room: {
            id: selectedRoomId
          }
        }
      ],
      checkin: checkIn + 'T14:00:00',
      checkout: checkOut + 'T12:00:00',
      status: selectedStatus,
      employee: { id: employeeId }
    };

    try {
      const res = await fetch('/api/reservations', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(reservation)
      });

      if (!res.ok) throw new Error('Create reservation failed');

      alert('Reservation created successfully!');
      modal.classList.remove('active');

      loadRecentReservations();
    } catch (err) {
      console.error(err);
      alert(err.message);
    }
  };
}
  //payments
  async function openUpdatePaymentModal(paymentId) {
    const modal = document.querySelector('#addPaymentModal');
    if (!modal) return;
  
    modal.classList.add('active');
  
    // Elements
    const methodText = modal.querySelector('#addStatusText');
    const methodOptions = modal.querySelectorAll('#addStatusOptions .option');
    const amountInput = modal.querySelector('#newAmount');
    const dateInput   = modal.querySelector('#newPayDate'); // use datetime-local
    const confirmBtn  = modal.querySelector('#confirmAddPayment'); // make sure button has this ID
  
    let selectedMethod = null;
  
    // Fetch existing payment data
    try {
      const res = await fetch(`/api/payments/${paymentId}`);
      if (!res.ok) throw new Error('Failed to load payment data');
  
      const payment = await res.json();
  
      // Populate modal
      selectedMethod = payment.method;
      methodText.innerText = payment.method;
      methodText.style.color = '#333';
  
      amountInput.value = payment.amount;
      if (payment.payment_date) {
        dateInput.value = new Date(payment.payment_date).toISOString().slice(0,16); // datetime-local format
      }
    } catch (err) {
      console.error(err);
      alert(err.message);
      return;
    }
  
    // Handle method select
    methodOptions.forEach(option => {
      option.addEventListener('click', e => {
        e.stopPropagation();
        selectedMethod = option.dataset.value;
        methodText.innerText = option.textContent.trim();
        methodText.style.color = '#333';
        option.closest('.select-menu').classList.remove('active');
      });
    });
  
    // Handle confirm button click
    confirmBtn.onclick = async e => {
      e.preventDefault();
  
      const selectedReservationId = document.querySelector("meta[name='reservation-id']")?.content;
      if (!selectedReservationId) return alert('Reservation ID not found');
  
      if (!selectedMethod || !amountInput.value) {
        return alert('Please fill all fields');
      }
  
      const updatedPayment = {
        reservation: { id: selectedReservationId },
        method: selectedMethod,
        amount: Number(amountInput.value),
        payment_date: dateInput.value ? dateInput.value : ""
      };
  
      try {
        const res = await fetch(`/api/payments/${paymentId}`, {
          method: 'PUT', // Use PUT for update
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedPayment)
        });
  
        if (!res.ok) throw new Error('Failed to update payment');
  
        alert('Payment updated successfully!');
        modal.classList.remove('active');
  
        // Reset form
        methodText.innerText = 'Select Method';
        methodText.style.color = '';
        amountInput.value = '';
        dateInput.value = '';
        selectedMethod = null;
  
        // Refresh table
        loadPayment();
      } catch (err) {
        console.error(err);
        alert(err.message);
      }
    };
  }
  
async function openUpdateReservationModal(reservationId) {
  const modal = document.querySelector('#updateReservationModal'); // updated
  if (!modal) return;

  modal.classList.add('active');

  const confirmBtn = modal.querySelector('#confirmAddReservation'); // same button
  confirmBtn.textContent = 'Update Reservation';

  // Update modal element selectors
  const guestText  = modal.querySelector('#updateGuestText');
  const hotelText  = modal.querySelector('#updateHotelText');
  const roomText   = modal.querySelector('#updateRoomText');
  const statusText = modal.querySelector('#updateStatusText');

  const checkInInput  = modal.querySelector('#updateCheckin');
  const checkOutInput = modal.querySelector('#updateCheckout');

  const employeeId = localStorage.getItem("employeeId");

  let selectedGuestId = null;
  let selectedHotelId = null;
  let selectedRoomId  = null;
  let selectedStatus  = null;

  // Fetch reservation data
  const res = await fetch(`/api/reservations/${reservationId}`);
  if (!res.ok) {
    alert('Failed to load reservation');
    return;
  }

  const reservation = await res.json();

  selectedGuestId = reservation.guestId;
  guestText.textContent = reservation.guestName;
  guestText.style.color = '#333';

  selectedStatus = reservation.status;
  statusText.textContent = reservation.status;
  statusText.style.color = '#333';

  checkInInput.value  = reservation.checkin.split('T')[0];
  checkOutInput.value = reservation.checkout.split('T')[0];

  const reservationRoom = reservation.rooms[0];
  selectedRoomId = reservationRoom.id;
  selectedHotelId = reservationRoom.hotelId;

  hotelText.textContent = reservationRoom.hotelName;
  hotelText.style.color = '#333';

  await loadRoomsByHotelForUpdate(selectedHotelId);

  roomText.textContent = `Room ${reservationRoom.roomNumber}`;
  roomText.style.color = '#333';

  const hotelOptions = modal.querySelector('#updateHotelOptions');
  hotelOptions.innerHTML = '';

  const hotelsRes = await fetch('/api/hotels');
  const hotels = await hotelsRes.json();

  hotels.forEach(h => {
    const li = document.createElement('li');
    li.className = 'option';
    li.textContent = h.name;

    li.onclick = async e => {
      e.stopPropagation();
      selectedRoomId = null;
      roomText.textContent = "Select Room"
      selectedHotelId = h.id;
      hotelText.textContent = h.name;
      hotelText.style.color = '#333';
      hotelOptions.parentElement.classList.remove('active');

      // load rooms by hotel
      await loadRoomsByHotelForUpdate(h.id);
    };

    hotelOptions.appendChild(li);
  });
  // Handle confirm button click
  confirmBtn.onclick = async e => {
    e.preventDefault();

    if (!selectedGuestId || !selectedHotelId || !selectedRoomId ||
        !checkInInput.value || !checkOutInput.value || !selectedStatus) {
      alert('Please fill all required fields.');
      return;
    }

    const updatedReservation = {
      id: reservationId,
      guest: { id: selectedGuestId },
      reservationRooms: [
        { room: { id: selectedRoomId } }
      ],
      checkin: checkInInput.value + 'T14:00:00',
      checkout: checkOutInput.value + 'T12:00:00',
      status: selectedStatus,
      employee: { id: employeeId }
    };

    try {
      const updateRes = await fetch(`/api/reservations/${reservationId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(updatedReservation)
      });

      if (!updateRes.ok) throw new Error('Update reservation failed');

      alert('Reservation updated successfully!');
      modal.classList.remove('active');
      loadRecentReservations();

    } catch (err) {
      console.error(err);
      alert(err.message);
    }
  };

  // Load rooms for selected hotel
  async function loadRoomsByHotelForUpdate(hotelId) {
    const roomOptions = modal.querySelector('#updateRoomOptions'); // updated
    roomOptions.innerHTML = '';

    const res = await fetch(`/api/rooms/hotels/${hotelId}`);
    const rooms = await res.json();

    rooms.forEach(r => {
      if (r.status === "AVAILABLE" || r.id === selectedRoomId) {
        const li = document.createElement('li');
        li.className = 'option';
        li.textContent = `Room ${r.roomNumber}`;

        li.onclick = e => {
          e.stopPropagation();
          selectedRoomId = r.id;
          roomText.textContent = li.textContent;
          roomText.style.color = '#333';
          roomOptions.parentElement.classList.remove('active');
        };

        roomOptions.appendChild(li);
      }
    });
  }

  // Add status selection logic
  const statusOptions = modal.querySelectorAll('#updateStatusOptions .option');
  statusOptions.forEach(option => {
    option.onclick = e => {
      e.stopPropagation();
      selectedStatus = option.dataset.value;
      statusText.textContent = option.textContent;
      statusText.style.color = '#333';
      option.closest('.select-menu').classList.remove('active');
    };
  });
}


async function openUpdateRoomTypeModal(roomTypeId) {
  if (!roomTypeId) return alert("Room type ID is missing for update");

  try {
    const response = await fetch(`/api/roomtypes/${roomTypeId}`);
    if (!response.ok) throw new Error('Failed to fetch room type data.');

    const roomType = await response.json();
    
    const modal = document.querySelector('#updateRoomTypeModal');
    if (!modal) return;

    const confirmBtn = modal.querySelector('#confirmUpdateRoomType');

    modal.querySelector('#updateRoomTypeId').value = roomType.id || '';
    modal.querySelector('#updateRoomTypeName').value = roomType.name || '';
    modal.querySelector('#updateRoomTypeBasePrice').value = roomType.basePrice || '';
    modal.querySelector('#updateRoomTypeCapacity').value = roomType.capacity || '';
    modal.querySelector('#updateRoomTypeDescription').value = roomType.description || '';

    modal.classList.add('active');

    confirmBtn.onclick = async (e) => {
      e.preventDefault();

      const updatedRoomType = {
        id: modal.querySelector('#updateRoomTypeId').value,
        name: modal.querySelector('#updateRoomTypeName').value,
        basePrice: parseInt(modal.querySelector('#updateRoomTypeBasePrice').value),
        capacity: parseInt(modal.querySelector('#updateRoomTypeCapacity').value),
        description: modal.querySelector('#updateRoomTypeDescription').value
      };

      try {
        const updateResponse = await fetch(`/api/roomtypes/${roomTypeId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedRoomType)
        });

        if (!updateResponse.ok) throw new Error('Failed to update room type.');

        alert('Room Type updated successfully!');
        modal.classList.remove('active');
        loadRoomTypesList();

      } catch (error) {
        console.error('Error updating room type:', error);
        alert('Error updating room type: ' + error.message);
      }
    };
  } catch (error) {
    console.error('Error updating room type: ', error);
    alert('Error updating room type: ' + error.message);
  }
}
  //employee
async function initRegisterEmployeeForm() {
  const registerContainer = document.querySelector('.auth-form.register-form');
  if (!registerContainer) return;

  const submitBtn = registerContainer.querySelector('.auth__button.register');
  const fnameInput = registerContainer.querySelector('input[name="fname"]');
  const lnameInput = registerContainer.querySelector('input[name="lname"]');
  const usernameInput = registerContainer.querySelector('input[name="username"]');
  const emailInput = registerContainer.querySelector('input[name="email"]');
  const passwordInput = registerContainer.querySelector('input[name="password"]');
  const confirmPassInput = registerContainer.querySelector('input[placeholder="Re-enter password"]');
  
  const hotelDropdown = registerContainer.querySelector('#hotel-dropdown');
  const hotelSelectMenu = hotelDropdown.querySelector('.input-layout.select-menu'); 
  const hotelText = hotelDropdown.querySelector('.text');
  const hotelOptionsList = hotelDropdown.querySelector('.options');
  
  let selectedHotelId = null;

  try {
    hotelText.innerText = "Loading Hotels...";
    
    const res = await fetch('/api/hotels');
    if (!res.ok) throw new Error('Failed to fetch hotels');
    
    const hotels = await res.json();
    
    hotelOptionsList.innerHTML = '';
    
    if (hotels.length === 0) {
      hotelText.innerText = "No Hotels Found";
      return;
    }

    hotels.forEach(hotel => {
      const li = document.createElement('li');
      li.classList.add('option');
      
      const span = document.createElement('span');
      span.classList.add('option-text');
      span.innerText = hotel.name;
      li.appendChild(span);
      hotelOptionsList.appendChild(li);

      li.addEventListener('click', (e) => {
        e.stopPropagation(); 
        hotelText.innerText = hotel.name;
        hotelText.style.color = "#333";
        selectedHotelId = hotel.id; 
        hotelDropdown.classList.remove('active');
      });
    });
    
    hotelText.innerText = "Select Hotel";
  } catch (err) {
    console.error('Hotel Fetch Error:', err);
    hotelText.innerText = "Error loading hotels";
  }

  if (hotelSelectMenu) {
      hotelSelectMenu.addEventListener('click', () => {
        hotelDropdown.classList.toggle('active');
      });
  }


  submitBtn.addEventListener('click', async (e) => {
    e.preventDefault();

    if (!selectedHotelId) {
      alert("Please select a hotel.");
      return;
    }

    if (!fnameInput.value || !usernameInput.value || !emailInput.value) {
      alert("Please fill in all required fields.");
      return;
    }

    if (passwordInput.value !== confirmPassInput.value) {
      alert("Passwords do not match!");
      return;
    }

    if (!emailInput.value.includes('@')) {
      alert("Please enter a valid email.");
      return;
    }
    const formData = {
      firstName: fnameInput.value,
      lastName: lnameInput.value,
      hotel:{
        id: selectedHotelId,
      },
      account: {
        username: usernameInput.value,
        email: emailInput.value,
        password: passwordInput.value
      }
    };

    try {
      const response = await fetch('/api/employees', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (!response.ok) {
        const errorBody = await response.text();
        let errorMessage = `Registration failed: ${response.statusText}`;

        try {
          const errorJson = JSON.parse(errorBody);
          errorMessage = errorJson.message || errorMessage;
        } catch {
          console.error("Server error response (Non-JSON):", errorBody);
        }
        throw new Error(errorMessage);
      }

      const data = await response.json();
      console.log('Employee added:', data);
      alert('Employee added successfully!');

      fnameInput.value = '';
      lnameInput.value = '';
      usernameInput.value = '';
      emailInput.value = '';
      passwordInput.value = '';
      confirmPassInput.value = '';
      
      hotelText.innerText = "Select Hotel";
      selectedHotelId = null;

      registerContainer.closest('.modal').classList.remove('active');
      loadEmployees();
    } catch (error) {
      console.error('Failed to add employee:', error);
      alert(`Failed to add employee: ${error.message}`);
    }
  });
}

async function openEditEmployeeModal(employeeId) {
  const modal = document.querySelector('#editEmployeeModal');
  if (!modal) return;

  try {
    const res = await fetch(`/api/employees/${employeeId}`);
    if (!res.ok) throw new Error('Failed to fetch employee data');
    
    const employee = await res.json();
    
    modal.querySelector('#editEmployeeId').value = employee.id || '';
    modal.querySelector('#editFname').value = employee.firstname || '';
    modal.querySelector('#editLname').value = employee.lastname || '';

    if(modal.querySelector('#editPosition')) modal.querySelector('#editPosition').value = employee.position || '';
    if(modal.querySelector('#editPhone')) modal.querySelector('#editPhone').value = employee.phone || '';
    if(modal.querySelector('#editSalary')) modal.querySelector('#editSalary').value = employee.salary || '';
    if(modal.querySelector('#editHireDate')) modal.querySelector('#editHireDate').value = employee.hireDate || '';

    const accountId = employee.accountId;
    const username = employee.username;
    const email = employee.email;
    const password = employee.password;
    const dob = employee.dob;

    modal.querySelector('#editAccountId').value = accountId;
    modal.querySelector('#editUsername').value = username;
    modal.querySelector('#editEmail').value = email; 
    modal.querySelector('#editPassword').value = password;
    modal.querySelector('#editDob').value = dob ? new Date(dob).toISOString().split('T')[0] : '';
    modal.querySelector('#editIdNumber').value = employee.idNumber || (employee.account ? employee.account.idNumber : '');
    
    const currentHotelId = employee.hotelId ? employee.hotelId : null; 
    selectedEditHotelId = currentHotelId; 
    
    if (modal.fetchEditHotels) {
      await modal.fetchEditHotels(currentHotelId);
    }

    modal.classList.add('active');

  } catch (error) {
    console.error('Error preparing edit modal:', error);
    alert('Could not load employee data for editing. Check API response structure.');
  }
}

function initUpdateEmployeeModal() {
  const modal = document.querySelector('#editEmployeeModal');
  if (!modal) return;

  const submitBtn = modal.querySelector('#updateEmployeeBtn');
  
  const hotelDropdown = modal.querySelector('#edit-hotel-dropdown');
  const hotelSelectMenu = hotelDropdown.querySelector('.input-layout.select-menu');
  const hotelText = modal.querySelector('#editHotelText');
  const hotelOptionsList = modal.querySelector('#editHotelOptions');
  
  async function fetchEditHotels(currentHotelId) {
      try {
          hotelText.innerText = "Loading Hotels...";
          const res = await fetch('/api/hotels');
          if (!res.ok) throw new Error('Failed to fetch hotels');
          
          const hotels = await res.json();
          hotelOptionsList.innerHTML = '';
          
          hotels.forEach(hotel => {
              const li = document.createElement('li');
              li.classList.add('option');
              li.innerHTML = `<span class="option-text">${hotel.name}</span>`;
              hotelOptionsList.appendChild(li);

              if (hotel.id === currentHotelId) {
                  hotelText.innerText = hotel.name;
                  selectedEditHotelId = hotel.id;
              }

              li.addEventListener('click', (e) => {
                  e.stopPropagation();
                  hotelText.innerText = hotel.name;
                  hotelText.style.color = "#333";
                  selectedEditHotelId = hotel.id;
                  hotelDropdown.classList.remove('active');
              });
          });
          if (currentHotelId && !selectedEditHotelId) hotelText.innerText = "Error: Hotel not found";

      } catch (err) {
          console.error('Edit Hotel Fetch Error:', err);
          hotelText.innerText = "Error loading hotels";
      }
  }
  
  if (hotelSelectMenu) {
      hotelSelectMenu.addEventListener('click', () => {
          hotelDropdown.classList.toggle('active');
      });
  }

  submitBtn.addEventListener('click', async (e) => {
      e.preventDefault();

      const employeeId = modal.querySelector('#editEmployeeId').value;
      const accountId = modal.querySelector('#editAccountId').value;
      const updatedData = {
          id: employeeId,
          firstName: modal.querySelector('#editFname').value,
          lastName: modal.querySelector('#editLname').value,
          position: modal.querySelector('#editPosition') ? modal.querySelector('#editPosition').value : null,
          phone: modal.querySelector('#editPhone') ? modal.querySelector('#editPhone').value : null,
          salary: modal.querySelector('#editSalary') ? parseInt(modal.querySelector('#editSalary').value) : null,
          hireDate: modal.querySelector('#editHireDate') ? modal.querySelector('#editHireDate').value : null,
          hotel: {
              id: selectedEditHotelId,
          },
          account: {
              id: accountId,
              username: modal.querySelector('#editUsername').value,
              email: modal.querySelector('#editEmail').value,
              password: modal.querySelector('#editPassword').value,
              dob: modal.querySelector('#editDob').value,
              idNumber: modal.querySelector('#editIdNumber').value
          }
      };

      if (!updatedData.firstName || !updatedData.lastName || !updatedData.account.username || !selectedEditHotelId) {
          alert("Please fill in required fields (Name, Username, Hotel).");
          return;
      }

      await updateEmployee(employeeId, updatedData);
      modal.classList.remove('active');
  });
  
  modal.fetchEditHotels = fetchEditHotels;
}


//Actions
function initTableActions() {
  //rooms
  const roomTableBody = document.querySelector('#roomTableBody');
  if (roomTableBody) {
    roomTableBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      const roomId = target.getAttribute('data-id');

      if (target.classList.contains('delete-btn')) {
        deleteRoom(roomId);
      }

      if (target.classList.contains('edit-btn')) {
        openUpdateRoomModal(roomId);
      }    
    });
  }
  //room types
  const roomTypesBody = document.querySelector('#roomTypeTableBody');
  if(roomTypesBody) {
    roomTypesBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      const roomTypeId = target.getAttribute('data-id');

      if (target.classList.contains('delete-btn')) {
        deleteRoomType(roomTypeId);
      }

      if (target.classList.contains('edit-btn')) {
        openUpdateRoomTypeModal(roomTypeId);
      }    
    })
  }
  //guests
  const guestTableBody = document.querySelector('#guestTableBody');
  if (guestTableBody) {
    guestTableBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      const guestId = target.getAttribute('data-id');

      if (target.classList.contains('edit-btn')) {
        openUpdateGuestModal(guestId); 
      }

      if (target.classList.contains('delete-btn')) {
        deleteGuest(guestId);
      }
    });
  }
  //employee
  const employeeTableBody = document.querySelector('#employeeTableBody');
  if (employeeTableBody) {
    employeeTableBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      const employeeId = target.getAttribute('data-id');

      if (target.classList.contains('delete-btn')) {
        deleteEmployee(employeeId);
      } 
      if (target.classList.contains('edit-btn')) {
        openEditEmployeeModal(employeeId); 
      }
    });
  }
  //hotels
  const hotelTableBody = document.querySelector('#hotelTableBody');
  if (hotelTableBody) {
    hotelTableBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      e.stopPropagation();
      
      const hotelId = target.getAttribute('data-id');

      if (target.classList.contains('edit-btn')) {
        openUpdateHotelModal(hotelId); 
      }
      if (target.classList.contains('delete-btn')) {
        deleteHotel(hotelId);
      }
    });
  }
  //payments
  const paymentTableBody = document.querySelector('#paymentTableBody');
  if (paymentTableBody) {
    paymentTableBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      const paymentId = target.getAttribute('data-id');

      if (target.classList.contains('edit-btn')) {
        openUpdatePaymentModal(paymentId);
      }
    });
  }
  //reservations
  const reservationTableBody = document.querySelector('#reservationTableBody');
  if (reservationTableBody) {
    reservationTableBody.addEventListener('click', (e) => {
      const target = e.target.closest('.action-btn');
      if (!target) return;

      const reservationId = target.getAttribute('data-id');

      if (target.classList.contains('edit-btn')) {
        openUpdateReservationModal(reservationId);
      }
      if (target.classList.contains('delete-btn')) {
        deleteReservation(reservationId);
      }
    });
  }
}
  //employee
async function updateEmployee(employeeId, updatedData) {
  if (!employeeId) return alert("Employee ID is missing for update.");

  try {
    const response = await fetch(`/api/employees/${employeeId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedData)
    });
    if (!response.ok) {
      const errorBody = await response.text();
      let errorMessage = `Update failed: ${response.statusText}`;

      try {
        const errorJson = JSON.parse(errorBody);
        errorMessage = errorJson.message || errorMessage;
      } catch {
        console.error("Server error response (Non-JSON):", errorBody);
      }
      throw new Error(errorMessage);
    }

    alert(`Employee ${employeeId} updated successfully!`);
    console.log(`Employee updated: ${employeeId}`);
    loadEmployees();
  } catch (error) {
    console.error('Failed to update employee:', error);
    alert(`Failed to update employee: ${error.message}`);
  }
}

async function deleteEmployee(employeeId) {
    if (!employeeId) return;

    if (!confirm(`Are you sure you want to delete employee ID: ${employeeId}?`)) {
      return;
    }

    try {
      const response = await fetch(`/api/employees/${employeeId}`, {
        method: 'DELETE',
      });

      if (response.status === 204 || response.ok) {
        alert(`Employee ${employeeId} deleted successfully!`);
        console.log(`Employee deleted: ${employeeId}`);
        loadEmployees();
      } else {
        const errorBody = await response.text();
        let errorMessage = `Deletion failed: ${response.statusText}`;
        try {
          const errorJson = JSON.parse(errorBody);
          errorMessage = errorJson.message || errorMessage;
        } catch {}
        throw new Error(errorMessage);
      }
    } catch (error) {
      console.error('Failed to delete employee:', error);
      alert(`Failed to delete employee: ${error.message}`);
    }
}

function initEmployeeTableActions() {
  const tableBody = document.querySelector('.employee__table tbody');
  if (!tableBody) return;

  tableBody.addEventListener('click', (e) => {
    const target = e.target.closest('.action-btn');
    if (!target) return;

    const employeeId = target.getAttribute('data-id');

    if (target.classList.contains('delete-btn')) {
      deleteEmployee(employeeId);
    } 
    else if (target.classList.contains('edit-btn')) {
      openEditModal(employeeId); 
    }
  });
}

  //room
async function deleteRoom(roomId) {
  const res = await fetch(`/api/rooms/${roomId}`);
  const room = await res.json();
  if (!confirm(`Are you sure you want to delete Room ${room.roomNumber} of ${room.hotelName} ?`)) {
    return;
  }

  try {
    const response = await fetch(`/api/rooms/${roomId}`, {
      method: 'DELETE'
    });

    if (!response.ok) throw new Error('Failed to delete room.');
    
    alert(`Room deleted successfully.`);
    loadRooms();

  } catch (error) {
    console.error('Error deleting room:', error);
    alert('Error deleting room: ' + error.message);
  }
}

  //room types
async function deleteRoomType(roomTypeId) {
  if (!roomTypeId) return alert("Room type ID is missing for update");
  
  try {
    const response = await fetch(`/api/roomtypes/${roomTypeId}`, {
        method: 'DELETE'
    });

    if (!response.ok) throw new Error('Failed to delete room type.');

    alert(`Room Type deleted successfully.`);
    loadRoomTypesList();

  } catch (error) {
    console.error('Error deleting room type:', error);
    alert('Error deleting room type: ' + error.message);
  }
}
  //guest
async function updateGuest(guestId, updatedData) {
  if (!guestId) return alert("Guest ID is missing for update.");

  try {
    const response = await fetch(`/api/guests/${guestId}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedData)
    });
    if (!response.ok) {
      const errorBody = await response.text();
      let errorMessage = `Update failed: ${response.statusText}`;

      try {
        const errorJson = JSON.parse(errorBody);
        errorMessage = errorJson.message || errorMessage;
      } catch {
        console.error("Server error response (Non-JSON):", errorBody);
      }
      throw new Error(errorMessage);
    }

    alert(`Guest ${guestId} updated successfully!`);
    console.log(`Guest updated: ${guestId}`);
    loadGuests();
  } catch (error) {
    console.error('Failed to update guest:', error);
    alert(`Failed to update guest: ${error.message}`);
  }
}

async function deleteGuest(guestId) {
  if (!guestId) return;

  const res = await fetch(`/api/guests/${guestId}`);
  const guest = await res.json();

  if (!confirm(`Are you sure you want to delete ${guest.firstName} ${guest.lastName}?`)) {
    return;
  }

  try {
    const response = await fetch(`/api/guests/${guestId}`, {
      method: 'DELETE'
    });

    if (!response.ok) throw new Error('Failed to delete guest.');

    alert(`Guest deleted successfully.`);
    loadGuests();
  } catch (error) {
    console.error('Error deleting guest:', error);
    alert('Error deleting guest: ' + error.message);
  }
}
  //hotel
async function deleteHotel(hotelId) {
  if (!hotelId) return;

  const res = await fetch(`/api/hotels/${hotelId}`);
  const hotel = await res.json();

  if (!confirm(`Are you sure you want to delete Hotel ${hotel.name} ?`)) {
    return;
  }
  
  try {
    const response = await fetch(`/api/hotels/${hotelId}`, {
      method: 'DELETE'
    });
    if (!response.ok) throw new Error('Failed to delete hotel.');
    
    alert(`Hotel deleted successfully.`);
    loadHotels();
  } catch (error) {
    console.error('Error deleting hotel:', error);
    alert('Error deleting hotel: ' + error.message);
  }
}
  //reservation
async function deleteReservation(reservationId) {
  if (!reservationId) return;

  const res = await fetch(`/api/reservations/${reservationId}`);
  const reservation = await res.json();

  if (!confirm(`Are you sure you want to delete reservation for ${reservation.guestName} on ${reservation.date}?`)) {
    return;
  }

  try {
    const response = await fetch(`/api/reservations/${reservationId}`, {
      method: 'DELETE'
    });
    if (!response.ok) throw new Error('Failed to delete reservation.');

    alert(`Reservation deleted successfully.`);
    loadRecentReservations();
  } catch (error) {
    console.error('Error deleting reservation:', error);
    alert('Error deleting reservation: ' + error.message);
  }
}
  //payment
async function deletePayment(paymentId) {
  if (!paymentId) return;

  const res = await fetch(`/api/payments/${paymentId}`);
  const payment = await res.json();

  const date = new Date(payment.reservation.checkin).toLocaleDateString('vi-VN');

  if (!confirm(`Are you sure you want to delete payment for ${payment.guestName} on ${date}?`)) {
    return;
  }

  try {
    const response = await fetch(`/api/payments/${paymentId}`, {
      method: 'DELETE'
    });
    if (!response.ok) throw new Error('Failed to delete payment.');

    alert(`Payment deleted successfully.`);
    loadRecentReservations();
  } catch (error) {
    console.error('Error deleting payment:', error);
    alert('Error deleting payment: ' + error.message);
  }
}

async function loadPendingReservations() {
  const tbody = document.querySelector('#pendingTableBody');
  const countEl = document.querySelector('#pendingCount');
  if (!tbody) return;

  try {
    const res = await fetch('/api/reservations/pending');
    if (!res.ok) throw new Error('Failed to load pending reservations');
    const data = await res.json();

    if (countEl) countEl.textContent = data.length;
    tbody.innerHTML = '';

    if (data.length === 0) {
      tbody.innerHTML = `<tr><td colspan="6" style="text-align:center;color:#999;padding:20px;">No pending requests</td></tr>`;
      return;
    }

    data.forEach(r => {
      const row = document.createElement('tr');
      const roomInfo = r.rooms && r.rooms.length > 0 ? r.rooms.map(rm => rm.roomNumber).join(', ') : 'N/A';
      const hotelInfo = r.rooms && r.rooms.length > 0 ? r.rooms.map(rm => rm.hotelName).join(', ') : 'N/A';
      const checkin  = r.checkin  ? new Date(r.checkin).toLocaleDateString('vi-VN')  : 'N/A';
      const checkout = r.checkout ? new Date(r.checkout).toLocaleDateString('vi-VN') : 'N/A';

      row.innerHTML = `
        <td>${r.guestName || 'N/A'}</td>
        <td>${roomInfo}</td>
        <td>${hotelInfo}</td>
        <td>${checkin}</td>
        <td>${checkout}</td>
        <td>
          <button class="action-btn approve-btn" data-id="${r.id}" title="Approve"
            style="background:#d4edda;color:#155724;border:none;border-radius:6px;padding:4px 12px;cursor:pointer;margin-right:6px;">
            <i class="ti-check"></i> Approve
          </button>
          <button class="action-btn reject-btn" data-id="${r.id}" title="Reject"
            style="background:#f8d7da;color:#721c24;border:none;border-radius:6px;padding:4px 12px;cursor:pointer;">
            <i class="ti-close"></i> Reject
          </button>
        </td>
      `;
      tbody.appendChild(row);
    });

    tbody.addEventListener('click', async (e) => {
      const btn = e.target.closest('.action-btn');
      if (!btn) return;
      const id = btn.getAttribute('data-id');

      if (btn.classList.contains('approve-btn')) {
        const employeeId = localStorage.getItem('employeeId');
        const res = await fetch(`/api/reservations/${id}/approve`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ employeeId: employeeId || '' })
        });
        if (res.ok) {
          alert('Reservation approved!');
          loadPendingReservations();
          loadRecentReservations();
        } else {
          alert('Failed to approve reservation.');
        }
      }

      if (btn.classList.contains('reject-btn')) {
        if (!confirm('Reject this reservation request?')) return;
        const res = await fetch(`/api/reservations/${id}/reject`, { method: 'PUT' });
        if (res.ok) {
          alert('Reservation rejected.');
          loadPendingReservations();
        } else {
          alert('Failed to reject reservation.');
        }
      }
    }, { once: false });

  } catch (err) {
    console.error('Failed to load pending reservations:', err);
    tbody.innerHTML = `<tr><td colspan="6" class="error-message">Error loading pending requests.</td></tr>`;
  }
}

//Components
async function initializeDropdown(apiUrl, listElement, textElement, initialText, setterCallback) {
  textElement.innerText = "Loading...";
  listElement.innerHTML = '';
  setterCallback(null);

  try {
    const res = await fetch(apiUrl);
    if (!res.ok) throw new Error(`Failed to fetch from ${apiUrl}`);

    const items = await res.json();
    
    items.forEach(item => {
      const id = item.id;
      const name = item.name;
      
      const li = document.createElement('li');
      li.classList.add('option');
      li.innerHTML = `<span class="option-text">${name}</span>`;
      listElement.appendChild(li);

      li.addEventListener('click', (e) => {
        e.stopPropagation();
        textElement.innerText = name;
        textElement.style.color = "#333";
        setterCallback(id);
        li.closest('.input-container.select-menu').classList.remove('active');
      });
    });
    textElement.innerText = initialText;
  } catch (err) {
    console.error(`Dropdown Fetch Error:`, err);
    textElement.innerText = `Error loading ${initialText.toLowerCase()}s`;
    textElement.style.color = "red";
  }
}