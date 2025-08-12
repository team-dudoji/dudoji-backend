document.addEventListener('DOMContentLoaded', async function () {
    await Promise.all([loadAvailableQuests(), loadNpcQuests()]);

    document.getElementById('questForm').addEventListener('submit', handleQuestFormSubmit);
    document.getElementById('createQuestForm').addEventListener('submit', handleCreateQuestFormSubmit);
});

let allQuests = [];

async function loadNpcQuests() {
    const res = await fetch(`/api/admin/npcs/${npcId}/quests`);
    const dto = await res.json();

    const tbody = document.getElementById('quest-table-body');
    tbody.innerHTML = '';

    dto.forEach(q => {
        // const parentId = dto.questDependency[q.questId];
        // const parentTitle = parentId ? (allQuests.find(x => x.questId === parentId)?.title || 'N/A') : 'None';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${q.questId}</td>
            <td>${q.title}</td>
            <td>${q.checker}</td>
            <td>${q.goalValue}</td>
            <td>${q.unit}</td>
            <td>${q.questType}</td>
            <td>${q.parentQuestId}</td>
            <td>
                <button onclick="removeQuest(${q.questId})">Remove</button>
                <button onclick="openDependencyModal(${q.questId})">Dependencies</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function loadAvailableQuests() {
    const res = await fetch('/api/admin/quests');
    allQuests = await res.json();

    const questSelect = document.querySelector('#questForm select[name="questId"]');
    questSelect.innerHTML = '';
    allQuests.forEach(q => {
        questSelect.innerHTML += `<option value="${q.questId}">${q.questId} : ${q.title}</option>`;
    });

    const parentSel = document.getElementById('parentQuestSelect');
    const childSel = document.getElementById('childQuestSelect');
    parentSel.innerHTML = '<option value="">None</option>';
    childSel.innerHTML = '<option value="">None</option>';
    allQuests.forEach(q => {
        parentSel.innerHTML += `<option value="${q.questId}">${q.questId} : ${q.title}</option>`;
        childSel.innerHTML += `<option value="${q.questId}">${q.questId} : ${q.title}</option>`;
    });

    const unitSel = document.getElementById('createQuestUnit');
    const typeSel = document.getElementById('createQuestType');
    const units = ['DISTANCE', 'COUNT', 'PERCENTAGE'];
    const types = ['DAILY', 'LANDMARK', 'NPC_MAIN', 'NPC_SUB', 'NPC_EMERGENCY'];

    unitSel.innerHTML = units.map(u => `<option value="${u}">${u}</option>`).join('');
    typeSel.innerHTML = types.map(t => `<option value="${t}">${t}</option>`).join('');
}

async function handleQuestFormSubmit(e) {
    e.preventDefault();
    const questId = e.target.questId.value;
    const res = await fetch(`/api/admin/npc-quest/${npcId}/${questId}`, { method: 'POST' });
    if (res.ok) {
        closeQuestModal();
        await loadNpcQuests();
    } else alert('Failed to add quest.');
}

async function removeQuest(id) {
    if (!confirm('Remove this quest?')) return;
    const res = await fetch(`/api/admin/npc-quest/${npcId}/${id}`, { method: 'DELETE' });
    if (res.ok) loadNpcQuests();
    else alert('Failed to remove quest.');
}

async function handleCreateQuestFormSubmit(e) {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target));
    data.goalValue = parseInt(data.goalValue);
    const res = await fetch('/api/admin/quests', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    if (res.ok) {
        closeCreateQuestModal();
        await loadAvailableQuests();
    } else alert('Failed to create quest.');
}

// Dependency functions
let currentQuestId = null;
async function openDependencyModal(qid) {
    currentQuestId = qid;
    document.getElementById('dependencyModal').style.display = 'block';
    await loadQuestDependencies(qid);
}
function closeDependencyModal() {
    document.getElementById('dependencyModal').style.display = 'none';
    currentQuestId = null;
}
async function loadQuestDependencies(qid) {
    const res = await fetch(`/api/admin/npcs/${npcId}/quests`);
    const dto = await res.json();
    const list = document.getElementById('existingDependenciesList');
    list.innerHTML = '';
    const parentId = dto.questDependency[qid];
    if (parentId) {
        list.innerHTML += `<li>Depends on: ${allQuests.find(q => q.questId === parentId)?.title}</li>`;
    }
    Object.entries(dto.questDependency)
        .filter(([child, parent]) => parent === qid)
        .forEach(([child]) => {
            list.innerHTML += `<li>Parent of: ${allQuests.find(q => q.questId === parseInt(child))?.title}</li>`;
        });
}
async function addDependency() {
    const p = document.getElementById('parentQuestSelect').value;
    const c = document.getElementById('childQuestSelect').value;
    if (!p || !c || p === c) return alert('Invalid selection.');
    const res = await fetch(`/api/admin/npc-quest-dependency/${p}/${c}`, { method: 'POST' });
    if (res.ok){
        await loadNpcQuests();
        // await loadQuestDependencies(currentQuestId);
    }
}
async function removeDependency() {
    const p = document.getElementById('parentQuestSelect').value;
    const c = document.getElementById('childQuestSelect').value;
    if (!p || !c) return alert('Select both.');
    const res = await fetch(`/api/admin/npc-quest-dependency/${p}/${c}`, { method: 'DELETE' });
    if (res.ok) {
        await loadNpcQuests();
        // await loadQuestDependencies(currentQuestId);
    }
}

// Modal open/close
function openQuestModal() { document.getElementById('questModal').style.display = 'block'; }
function closeQuestModal() { document.getElementById('questModal').style.display = 'none'; }
function openCreateQuestModal() { document.getElementById('createQuestModal').style.display = 'block'; }
function closeCreateQuestModal() {
    document.getElementById('createQuestModal').style.display = 'none';
    document.getElementById('createQuestForm').reset();
}
