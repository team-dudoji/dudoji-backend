// Admin's landmarks management script

const modal = document.getElementById('modal');
const modalTitle = document.getElementById('modal-title');
let modalMode = 'none';
let selectedLandmarkId = null;

// Functions to open and close modal
function openModal(mode, landmarkId = null) {
    modalMode = mode;
    modalTitle.innerText = (mode === 'add') ? '랜드마크 추가' : '랜드마크 수정';
    if (mode === 'update') selectedLandmarkId = landmarkId;
    modal.style.display = 'block';
}

function closeModal() {
    modal.style.display = 'none';
    selectedLandmarkId = null;
    modalMode = 'none';
}

document.getElementById('infoForm').addEventListener('submit', function(event) {
    event.preventDefault();
    if (modalMode === 'add') {
        addLandmark();
    } else if (modalMode === 'update') {
        updateLandmark(selectedLandmarkId);
    }
    closeModal();
});

// Functions to communicate with the backend
function addLandmark() {
    const URL = "/api/admin/landmarks"
    sendLandmarkData(baseURL + URL, 'POST');
}

function updateLandmark(landmarkId) {
    const URL = "/api/admin/landmarks" + '/' + landmarkId
    sendLandmarkData(baseURL + URL, 'PUT');
}

function deleteLandmark(landmarkId) {
    const URL = "/api/admin/landmarks" + '/' + landmarkId
    if (landmarkId) {
        fetch(baseURL + URL, {
            method: 'DELETE'
        }).then(() => {
            location.reload();
        });
    }
}

function sendLandmarkData(url, method) {
    const formData = new FormData(document.getElementById("infoForm"));
    const data = Object.fromEntries(formData.entries());

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    }).then(() => {
        location.reload();
    });
}
