const modal = document.getElementById('modal');
const modalTitle = document.getElementById('modal-title');
const modalImagePreview = document.getElementById('modal-imagePreview');
const modalImageUrl = document.getElementById('modal-imageUrl'); // 서버로부터 받은 최종 이미지 URL을 저장할 hidden input
const modalImageFile = document.getElementById('modal-imageFile'); // 파일 선택 input
const infoForm = document.getElementById('infoForm');

let modalMode = 'none';

// Functions to open and close modal
function openModal(mode, landmarkId = null) {
    modalMode = mode;
    modalTitle.innerText = (mode === 'add') ? '핀 스킨 추가' : '핀 스킨 수정';
    modal.style.display = 'block';
}

function closeModal() {
    modal.style.display = 'none';
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
             const uploadResponse = await fetch('/api/admin/images/pin-skins', {
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

    addPinSkin();
    closeModal();
});

// Functions to communicate with the backend
// UPSERT 임
function addPinSkin() {
    const URL = "/api/admin/pin-skins";
    sendPinSkinData(baseURL + URL, 'POST');
}

function deletePinSkin(skinId) {
    const URL = "/api/admin/pin-skins";
    if (skinId) {
        fetch(baseURL + URL + '/' + skinId, {
            method: 'DELETE'
        }).then(() => {
            location.reload();
        });
    }
}

function sendPinSkinData(url, method) {
    const formData = new FormData(document.getElementById("infoForm"));
    const data = Object.fromEntries(formData.entries());

    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(data)
    }).then(() => {
        console.log(url);
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