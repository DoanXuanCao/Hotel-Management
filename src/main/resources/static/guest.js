// guest.js – loaded only by guest.html
document.addEventListener('DOMContentLoaded', () => {
  if (!document.getElementById('hotelGrid')) return;

  initGuestModalEvents();
  loadHotelsForGuest();
  loadMyReservations();
});

/* ─── Hotel grid ──────────────────────────────────────────── */
async function loadHotelsForGuest() {
  const grid = document.getElementById('hotelGrid');
  if (!grid) return;

  const imgPool = [
    '/assets/img/hotel-1.jpg',
    '/assets/img/hotel-2.jpg',
    '/assets/img/hotel-3.jpg',
    '/assets/img/hotel-4.jpg',
    '/assets/img/hotel-5.jpg',
    '/assets/img/hotel-6.jpg',
  ];

  try {
    const res = await fetch('/api/hotels');
    if (!res.ok) throw new Error('Failed to fetch hotels');
    const hotels = await res.json();

    grid.innerHTML = '';

    if (hotels.length === 0) {
      grid.innerHTML = '<p style="text-align:center;color:#999;padding:40px;">No hotels available at this time.</p>';
      return;
    }

    hotels.forEach((hotel, idx) => {
      const card = document.createElement('div');
      card.className = 'popular__card';
      card.style.cursor = 'pointer';
      card.dataset.hotelId   = hotel.id;
      card.dataset.hotelName = hotel.name;

      const stars = '★'.repeat(Math.round(hotel.rating || 0)) + '☆'.repeat(5 - Math.round(hotel.rating || 0));

      card.innerHTML = `
        <img src="${imgPool[idx % imgPool.length]}" alt="${hotel.name}"
             onerror="this.style.background='linear-gradient(135deg,#2c3855,#435681)';this.style.height='180px';this.removeAttribute('src')" />
        <div class="popular__content">
          <div class="popular__card__header">
            <h4>${hotel.name}</h4>
            <span style="color:#f59e0b;font-size:13px;">${stars} ${hotel.rating}</span>
          </div>
          <p>${hotel.address || ''}</p>
        </div>
      `;

      card.addEventListener('click', () => openBookingModal(hotel.id, hotel.name));
      grid.appendChild(card);
    });

  } catch (err) {
    console.error('loadHotelsForGuest error:', err);
    grid.innerHTML = '<p style="color:#c0392b;text-align:center;padding:30px;">Could not load hotels. Please try again.</p>';
  }
}

/* ─── Booking modal ───────────────────────────────────────── */
let _bookingSelectedRoomId = null;

function initGuestModalEvents() {
  const modal      = document.getElementById('bookingModal');
  const overlay    = modal?.querySelector('.modal__overlay');
  const closeBtn   = modal?.querySelector('.closeModal');
  const confirmBtn = document.getElementById('confirmBookingBtn');

  overlay?.addEventListener('click', () => modal.classList.remove('active'));
  closeBtn?.addEventListener('click', () => modal.classList.remove('active'));

  confirmBtn?.addEventListener('click', submitBooking);
}

async function openBookingModal(hotelId, hotelName) {
  _bookingSelectedRoomId = null;
  const modal     = document.getElementById('bookingModal');
  const roomText  = document.getElementById('bookingRoomText');
  const optionsList = document.getElementById('bookingRoomOptions');

  document.getElementById('bookingHotelName').textContent = hotelName;
  document.getElementById('bookingCheckin').value  = '';
  document.getElementById('bookingCheckout').value = '';
  document.getElementById('bookingNote').value     = '';
  roomText.textContent    = 'Loading rooms…';
  roomText.style.color    = '#999';
  optionsList.innerHTML   = '';
  modal.classList.add('active');

  try {
    const res   = await fetch(`/api/rooms/hotels/${hotelId}`);
    const rooms = await res.json();
    const available = rooms.filter(r => r.status === 'AVAILABLE');

    roomText.textContent = 'Select a room';

    if (available.length === 0) {
      optionsList.innerHTML = '<li style="padding:10px 12px;color:#999;">No available rooms right now</li>';
      return;
    }

    available.forEach(room => {
      const li = document.createElement('li');
      li.className = 'option';
      const price = Number(room.basePrice || 0).toLocaleString('vi-VN');
      li.innerHTML = `<strong>Room ${room.roomNumber}</strong>&nbsp;
        <span style="color:#666;font-size:12px;">${room.roomTypeName || ''} · ${price}₫/night</span>`;

      li.addEventListener('click', e => {
        e.stopPropagation();
        _bookingSelectedRoomId = room.id;
        roomText.textContent   = `Room ${room.roomNumber} (${room.roomTypeName || ''})`;
        roomText.style.color   = '#333';
        document.getElementById('roomSelectMenu')?.classList.remove('active');
      });
      optionsList.appendChild(li);
    });

  } catch (err) {
    console.error('openBookingModal rooms error:', err);
    optionsList.innerHTML = '<li style="padding:10px 12px;color:#c0392b;">Error loading rooms</li>';
  }
}

async function submitBooking() {
  const guestId = localStorage.getItem('guestId');
  if (!guestId) {
    alert('Session expired. Please log in again.');
    window.location.href = '/';
    return;
  }

  const checkin  = document.getElementById('bookingCheckin').value;
  const checkout = document.getElementById('bookingCheckout').value;

  if (!checkin || !checkout) { alert('Please select check-in and check-out dates.'); return; }
  if (checkin >= checkout)   { alert('Check-out must be after check-in.');            return; }
  if (!_bookingSelectedRoomId) { alert('Please select a room.');                      return; }

  const confirmBtn = document.getElementById('confirmBookingBtn');
  confirmBtn.disabled = true;
  confirmBtn.textContent = 'Submitting…';

  try {
    const res = await fetch('/api/reservations', {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        guest:            { id: guestId },
        reservationRooms: [{ room: { id: _bookingSelectedRoomId } }],
        checkin:  checkin  + 'T14:00:00',
        checkout: checkout + 'T12:00:00',
        status:   'PENDING'
      })
    });

    if (!res.ok) throw new Error(await res.text());

    alert('✓ Booking request submitted! Please wait for staff approval.');
    document.getElementById('bookingModal').classList.remove('active');
    loadMyReservations();

  } catch (err) {
    console.error('submitBooking error:', err);
    alert('Failed to submit booking: ' + err.message);
  } finally {
    confirmBtn.disabled = false;
    confirmBtn.textContent = 'Request Booking';
  }
}

/* ─── My Bookings ─────────────────────────────────────────── */
async function loadMyReservations() {
  const list    = document.getElementById('myReservationList');
  if (!list) return;

  const guestId = localStorage.getItem('guestId');
  if (!guestId) {
    list.innerHTML = '<p class="no-reservations">Please log in to see your bookings.</p>';
    return;
  }

  list.innerHTML = '<p class="no-reservations" style="color:#999;">Loading your bookings…</p>';

  try {
    const res  = await fetch(`/api/reservations/guest/${guestId}`);
    if (!res.ok) throw new Error('Failed');
    const data = await res.json();

    list.innerHTML = '';

    if (data.length === 0) {
      list.innerHTML = '<p class="no-reservations">You have no bookings yet. Click a hotel above to get started!</p>';
      return;
    }

    const statusLabel = {
      PENDING:    { label: 'Pending Approval', cls: 'pending'    },
      BOOKED:     { label: 'Confirmed',         cls: 'booked'     },
      CHECKED_IN: { label: 'Checked In',        cls: 'checked_in' },
      COMPLETED:  { label: 'Completed',         cls: 'completed'  },
      CANCELED:   { label: 'Cancelled',         cls: 'canceled'   },
      NO_SHOW:    { label: 'No Show',           cls: 'canceled'   },
    };
    const cancellable = new Set(['PENDING', 'BOOKED']);

    [...data].reverse().forEach(r => {
      const card = document.createElement('div');
      card.className = 'reservation-card';

      const roomInfo  = r.rooms?.length ? r.rooms.map(rm => `Room ${rm.roomNumber}`).join(', ') : 'N/A';
      const hotelInfo = r.rooms?.length ? r.rooms.map(rm => rm.hotelName).join(', ')            : 'N/A';
      const checkin   = r.checkin  ? new Date(r.checkin).toLocaleDateString('vi-VN')  : '—';
      const checkout  = r.checkout ? new Date(r.checkout).toLocaleDateString('vi-VN') : '—';
      const st        = statusLabel[r.status] || { label: r.status, cls: 'pending' };

      const cancelBtn = cancellable.has(r.status)
        ? `<button class="cancel-booking-btn"
              data-id="${r.id}"
              style="margin-top:6px;background:#fee2e2;color:#991b1b;border:none;
                     border-radius:6px;padding:4px 12px;cursor:pointer;font-size:12px;">
             Cancel Booking
           </button>`
        : '';

      card.innerHTML = `
        <div class="res-info">
          <strong>${hotelInfo}</strong>
          <span>${roomInfo}</span>
        </div>
        <div class="res-info">
          <strong>Check-in</strong>
          <span>${checkin}</span>
        </div>
        <div class="res-info">
          <strong>Check-out</strong>
          <span>${checkout}</span>
        </div>
        <div>
          <span class="status-badge ${st.cls}">${st.label}</span>
          ${cancelBtn}
        </div>
      `;

      // Cancel booking handler
      const btn = card.querySelector('.cancel-booking-btn');
      if (btn) {
        btn.addEventListener('click', async () => {
          if (!confirm('Cancel this booking request?')) return;
          btn.disabled = true;
          btn.textContent = 'Cancelling…';
          try {
            const res = await fetch(`/api/reservations/${r.id}/cancel`, { method: 'PUT' });
            if (!res.ok) throw new Error('Failed');
            alert('Booking cancelled successfully.');
            loadMyReservations();
          } catch {
            alert('Could not cancel booking. Please try again.');
            btn.disabled = false;
            btn.textContent = 'Cancel Booking';
          }
        });
      }

      list.appendChild(card);
    });

  } catch (err) {
    console.error('loadMyReservations error:', err);
    list.innerHTML = '<p class="no-reservations" style="color:#c0392b;">Error loading bookings.</p>';
  }
}
