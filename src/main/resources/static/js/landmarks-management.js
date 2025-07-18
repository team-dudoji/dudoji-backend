// Admin's landmarks management script

const modal = document.getElementById('modal');
const modalTitle = document.getElementById('modal-title');
const modalImagePreview = document.getElementById('modal-imagePreview');
const modalImageUrl = document.getElementById('modal-imageUrl'); // 서버로부터 받은 최종 이미지 URL을 저장할 hidden input
const modalImageFile = document.getElementById('modal-imageFile'); // 파일 선택 input
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

document.getElementById('infoForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const file = modalImageFile.files[0];
    let finalImageUrl = modalImageUrl.value;

    if (file) { // new Image
        const imageFormData = new FormData();
        imageFormData.append("image", file);

        try {
            const uploadResponse = await fetch('/api/admin/images/landmarks', {
                method: 'POST',
                body: imageFormData
            });

            if (!uploadResponse.ok) {
                // 예외처리
                const errorText = await uploadResponse.text();
                throw new Error(`Failed upload: ${uploadResponse.status} - ${errorText}`);
            }

            finalImageUrl = await uploadResponse.text();
        } catch (error) {
            console.error('이미지 업로드 오류:', error);
            alert('이미지 업로드 중 오류가 발생했습니다: ' + error.message);
        }
    }
    modalImageUrl.value = finalImageUrl;

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

function previewImage(event) {
    const reader = new FileReader();
    reader.onload = function(){
        modalImagePreview.src = reader.result;
        modalImagePreview.style.display = 'block';
    };
    reader.readAsDataURL(event.target.files[0]);
}
