document.addEventListener('DOMContentLoaded', function () {
    loadNpcs();
    loadNpcSkins();

    const form = document.getElementById('infoForm');
    form.addEventListener('submit', handleFormSubmit);
});

let currentMode = 'add';
let currentNpcId = null;
let npcSkins = [];

async function loadNpcs() {
    const response = await fetch('/api/admin/npcs');
    const npcs = await response.json();

    const tableBody = document.getElementById('npc-table-body');
    tableBody.innerHTML = '';

    npcs.forEach(npc => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${npc.npcId}</td>
            <td>${npc.name}</td>
            <td>${npc.lat}</td>
            <td>${npc.lng}</td>
            <td>${npc.npcDescription}</td>
            <td>${npc.npcScript}</td>
            <td><img src="${npc.imageUrl}" style="height:64px"/></td>
            <td>${npc.npcSkinId}</td>
            <td>${npc.questName}</td>
            <td>
                <button type="button" onclick='openModal("update", ${JSON.stringify(npc)})'>Update</button>
                <button type="button" onclick="deleteNpc(${npc.npcId})">Delete</button>
                <button type="button" onclick="manageQuests(${npc.npcId})">Quests</button>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

async function loadNpcSkins() {
    const response = await fetch('/api/admin/npc-skin');
    npcSkins = await response.json();

    const select = document.getElementById('modal-npcSkinId');
    select.innerHTML = '';

    npcSkins.forEach(skin => {
        const option = document.createElement('option');
        option.value = skin.npcSkinId;
        option.textContent = `ID: ${skin.npcSkinId} - ${skin.imageUrl}`;
        select.appendChild(option);
    });
}

async function deleteNpc(npcId) {
    if (confirm('Are you sure you want to delete this NPC?')) {
        const response = await fetch(`/api/admin/npcs/${npcId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            await loadNpcs();
        }
    }
}

function manageQuests(npcId) {
    window.location.href = `/admin/npc-quest?npcId=${npcId}`;
}

function openModal(mode, npc = null) {
    currentMode = mode;
    const modal = document.getElementById('modal');
    const title = document.getElementById('modal-title');
    const form = document.getElementById('infoForm');

    if (mode === 'add') {
        title.textContent = 'Add NPC';
        form.reset();
        currentNpcId = null;
        document.getElementById('modal-imagePreview').style.display = 'none';
        document.getElementById('modal-imageUrl').value = ''; // Clear hidden image URL
    } else if (mode === 'update') {
        title.textContent = 'Update NPC';
        form.name.value = npc.name;
        form.lat.value = npc.lat;
        form.lng.value = npc.lng;
        form.npcDescription.value = npc.npcDescription;
        form.npcScript.value = npc.npcScript;
        form.imageUrl.value = npc.imageUrl;
        document.getElementById('modal-imagePreview').src = npc.imageUrl;
        document.getElementById('modal-imagePreview').style.display = 'block';
        document.getElementById('modal-npcSkinId').value = npc.npcSkinId;
        currentNpcId = npc.npcId;
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
            finalImageUrl = await uploadImage(imageFile, '/api/admin/images/npcs');
        } catch (error) {
            console.error('Image upload error:', error);
            alert('Image upload failed: ' + error.message);
            return;
        }
    }
    data.imageUrl = finalImageUrl;

    // Convert npcSkinId to a number
    data.npcSkinId = parseInt(data.npcSkinId);

    let url = '/api/admin/npcs';
    let method = 'POST';

    if (currentMode === 'update') {
        url = `/api/admin/npcs`;
        method = 'PUT';
        data.npcId = currentNpcId;
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
        loadNpcs();
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

