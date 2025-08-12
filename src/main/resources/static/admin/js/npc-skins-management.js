document.addEventListener('DOMContentLoaded', function () {
    loadNpcSkins();

    const form = document.getElementById('infoForm');
    form.addEventListener('submit', handleFormSubmit);
});

let currentMode = 'add';
let currentNpcSkinId = null;

async function loadNpcSkins() {
    const response = await fetch('/api/admin/npc-skin');
    const npcSkins = await response.json();

    const tableBody = document.getElementById('npc-skin-table-body');
    tableBody.innerHTML = '';

    npcSkins.forEach(skin => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${skin.npcSkinId}</td>
            <td><img src="${skin.imageUrl}" style="height:64px"/></td>
            <td>${skin.regionId}</td>
            <td>
                <button type="button" onclick='openModal("update", ${JSON.stringify(skin)})'>Update</button>
                <button type="button" onclick="deleteNpcSkin(${skin.npcSkinId})">Delete</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

async function deleteNpcSkin(npcSkinId) {
    if (confirm('Are you sure you want to delete this NPC Skin?')) {
        const response = await fetch(`/api/admin/npc-skin/${npcSkinId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadNpcSkins();
        }
    }
}

function openModal(mode, skin = null) {
    currentMode = mode;
    const modal = document.getElementById('modal');
    const title = document.getElementById('modal-title');
    const form = document.getElementById('infoForm');

    if (mode === 'add') {
        title.textContent = 'Add NPC Skin';
        form.reset();
        currentNpcSkinId = null;
        document.getElementById('modal-imagePreview').style.display = 'none';
        document.getElementById('modal-imageUrl').value = ''; // Clear hidden image URL
    } else if (mode === 'update') {
        title.textContent = 'Update NPC Skin';
        form.imageUrl.value = skin.imageUrl;
        form.regionId.value = skin.regionId;
        document.getElementById('modal-imagePreview').src = skin.imageUrl;
        document.getElementById('modal-imagePreview').style.display = 'block';
        currentNpcSkinId = skin.npcSkinId;
    }

    modal.style.display = 'block';
}

function closeModal() {
    const modal = document.getElementById('modal');
    modal.style.display = 'none';
}

async function handleFormSubmit(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    // Handle image upload
    const imageFile = document.getElementById('modal-imageFile').files[0];
    let finalImageUrl = data.imageUrl; // Use existing URL if no new file

    if (imageFile) {
        try {
            finalImageUrl = await uploadImage(imageFile, '/api/admin/images/npc-skins');
        } catch (error) {
            console.error('Image upload error:', error);
            alert('Image upload failed: ' + error.message);
            return;
        }
    }
    data.imageUrl = finalImageUrl;

    // Convert regionId to a number
    data.regionId = parseInt(data.regionId);

    let url = '/api/admin/npc-skin';
    let method = 'POST';

    if (currentMode === 'update') {
        url = `/api/admin/npc-skin`;
        method = 'PUT';
        data.npcSkinId = currentNpcSkinId;
    }

    const response = await fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    });

    if (response.ok) {
        closeModal();
        loadNpcSkins();
    }
}

function previewImage(event) {
    const reader = new FileReader();
    reader.onload = function(){
        const output = document.getElementById('modal-imagePreview');
        output.src = reader.result;
        output.style.display = 'block';
    };
    reader.readAsDataURL(event.target.files[0]);
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

