// Admin's landmarks management script
const modal = document.getElementById('modal');
const modalTitle = document.getElementById('modal-title');

const modalMapImagePreview = document.getElementById('modal-mapImagePreview');
const modalMapImageUrl = document.getElementById('modal-mapImageUrl'); // 서버로부터 받은 최종 이미지 URL을 저장할 hidden input
const modalMapImageFile = document.getElementById('modal-mapImageFile'); // 파일 선택 input

const modalDetailImagePreview = document.getElementById('modal-detailImagePreview');
const modalDetailImageUrl = document.getElementById('modal-detailImageUrl'); // 서버로부터 받은 최종 이미지 URL을 저장할 hidden input
const modalDetailImageFile = document.getElementById('modal-detailImageFile'); // 파일 선택 input

let modalMode = 'none';
let selectedLandmarkId = null;

// Functions to open and close modal
function openModal(mode, landmark = null) {
    modalMode = mode;
    modalTitle.innerText = (mode === 'add') ? '랜드마크 추가' : '랜드마크 수정';
    if (mode === 'update') {
        selectedLandmarkId = landmark.landmarkId;
        document.querySelector('[name="placeName"]').value = landmark.placeName;
        document.querySelector('[name="lat"]').value = landmark.lat;
        document.querySelector('[name="lng"]').value = landmark.lng;
        document.querySelector('[name="content"]').value = landmark.content;
        document.querySelector('[name="address"]').value = landmark.address;

        // 기존 이미지 URL이 있을 경우 preview
        if (landmark.mapImageUrl) {
            const preview = document.getElementById('modal-imagePreview');
            preview.src = landmark.mapImageUrl;
            preview.style.display = 'block';
            document.getElementById('modal-mapImageUrl').value = landmark.mapImageUrl;
            document.getElementById('modal-existingMapImageUrl').value = landmark.mapImageUrl;
        }

        if (landmark.detailImageUrl) {
            const preview = document.getElementById('modal-detailImagePreview');
            preview.src = landmark.detailImageUrl;
            preview.style.display = 'block';
            document.getElementById('modal-detailImageUrl').value = landmark.detailImageUrl;
            document.getElementById('modal-existingDetailImageUrl').value = landmark.detailImageUrl;
        }
    }
    modal.style.display = 'block';

}

function closeModal() {
    modal.style.display = 'none';
    selectedLandmarkId = null;
    modalMode = 'none';
}

document.getElementById('infoForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const mapImageFile = modalMapImageFile.files[0];
    let finalMapImageUrl = modalMapImageUrl.value;

    const detailImageFile = modalDetailImageFile.files[0];
    let finalDetailImageUrl = modalDetailImageUrl.value;

    try {
        if (mapImageFile) {
            finalMapImageUrl = await uploadImage(mapImageFile, '/api/admin/images/landmarks/map');
        }
        if (detailImageFile) {
            finalDetailImageUrl = await uploadImage(detailImageFile, '/api/admin/images/landmarks/detail');
        }
    } catch (error) {
        console.error('이미지 업로드 오류:', error);
        alert('이미지 업로드 중 오류가 발생했습니다: ' + error.message);
        return;
    }

    modalMapImageUrl.value = finalMapImageUrl;
    modalDetailImageUrl.value = finalDetailImageUrl;

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

    console.log(JSON.stringify(data, null, 2)); // Log the data to the console

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

// TODO: 적용 on detail
function previewImage(event, target) {
    const reader = new FileReader();
    switch (target) {
        case 0:
            reader.onload = function(){
                modalMapImagePreview.src = reader.result;
                modalMapImagePreview.style.display = 'block';
            };
            reader.readAsDataURL(event.target.files[0]);
            break;
        case 1:
            reader.onload = function(){
                modalDetailImagePreview.src = reader.result;
                modalDetailImagePreview.style.display = 'block';
            };
            reader.readAsDataURL(event.target.files[0]);
            break;
    }
}

async function uploadImage(file, endpoint) {
    if (!file) return null;

    const formData = new FormData();
    formData.append("image", file);

    const res = await fetch(endpoint, {
        method: "POST",
        body: formData
    });

    if (!res.ok) {
        const errText = await res.text();
        throw new Error(`Upload failed: ${res.status} – ${errText}`);
    }

    return res.text();
}
