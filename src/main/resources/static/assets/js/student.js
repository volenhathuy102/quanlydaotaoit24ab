
import { API_ENDPOINTS } from './config.js';

const fields = [
    'studentCode',
    'fullName',
    'email',
    'phone',
    'dateOfBirth',
    'gender',
    'personalIdentificationNumber',
    'dateOfIssue',
    'cardPlace',
    'address',
    'currentAddress',
    'academicYearYear',
    'departmentId',
    'majorId',
    'trainingProgramId',
    'statusId',
    'studentClassessId',
    'admissionYear',
    'isActive'
];

const textToProperty = {
    studentCode: 'code',
    fullName: 'fullName',
    email: 'email',
    phone: 'phone',
    dateOfBirth: 'dateOfBirth',
    gender: 'gender',
    personalIdentificationNumber: 'personalIdentificationNumber',
    dateOfIssue: 'dateOfIssue',
    cardPlace: 'cardPlace',
    address: 'address',
    currentAddress: 'currentAddress',
    academicYearYear: 'academicYearYear',
    departmentId: 'departmentId',
    majorId: 'majorId',
    trainingProgramId: 'trainingProgramId',
    statusId: 'statusId',
    studentClassessId: 'studentClassessId',
    admissionYear: 'admissionYear',
    isActive: 'isActive'
};

let studentClassOptions = [];
let existingStudentCode = null;

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page') || 'list';

    document.getElementById('page-list').style.display =
        page === 'form' ? 'none' : '';

    document.getElementById('page-form').style.display =
        page === 'form' ? '' : 'none';

    initStudentPage();
});

function initStudentPage() {
    const searchForm = document.getElementById('searchForm');
    const studentForm = document.getElementById('studentForm');

    if (document.getElementById('studentTableBody')) {
        loadStudents();

        searchForm?.addEventListener('submit', event => {
            event.preventDefault();
            loadStudents(getValue('keyword'));
        });
    }

    if (studentForm) {
        initStudentForm();
    }
}

async function request(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        const responseText = await response.text();
        let message = responseText || `HTTP ${response.status}`;
        try {
            const body = JSON.parse(responseText);
            message = body.detail || body.message || body.title || message;
        } catch {
            // Keep the response text when the server did not return JSON.
        }
        throw new Error(message);
    }

    if (response.status === 204) return null;

    const contentType = response.headers.get('content-type') || '';
    return contentType.includes('application/json')
        ? response.json()
        : null;
}

async function loadStudents(keyword = '') {
    const body = document.getElementById('studentTableBody');
    if (!body) return;

    body.innerHTML = `
        <tr><td colspan="6" class="text-center py-4">
            Đang tải dữ liệu...
        </td></tr>`;

    try {
        const url = keyword
            ? `${API_ENDPOINTS.STUDENTS}?keyword=${encodeURIComponent(keyword)}`
            : API_ENDPOINTS.STUDENTS;

        const [students, classes] = await Promise.all([
            request(url),
            studentClassOptions.length
                ? Promise.resolve(studentClassOptions)
                : request('/api/student-form-options/classes')
        ]);
        studentClassOptions = classes;
        renderStudents(Array.isArray(students) ? students : []);
    } catch (error) {
        console.error('Lỗi tải sinh viên:', error);
        body.innerHTML = `
            <tr><td colspan="6" class="text-center text-danger py-4">
                Không tải được dữ liệu: ${escapeHtml(error.message)}
            </td></tr>`;
    }
}

function renderStudents(students) {
    const body = document.getElementById('studentTableBody');

    if (!students.length) {
        body.innerHTML = `
            <tr><td colspan="6" class="text-center text-muted py-4">
                Không có dữ liệu sinh viên
            </td></tr>`;
        return;
    }

    body.innerHTML = students.map(student => {
        const id = escapeHtml(student.id);
        const code = student.studentCode ?? student.code;
        const studentClass = studentClassOptions.find(item =>
            normalizeIdentifier(item.id) === normalizeIdentifier(student.studentClassessId));
        const classText = student.className
            ?? student.studentClassName
            ?? (studentClass ? `${studentClass.code} — ${studentClass.name}` : '');

        return `
            <tr>
                <td>${escapeHtml(code)}</td>
                <td>${escapeHtml(student.fullName)}</td>
                <td>${escapeHtml(student.email || 'Chưa cập nhật')}</td>
                <td>${escapeHtml(student.phone || 'Chưa cập nhật')}</td>
                <td>${escapeHtml(classText || 'Chưa cập nhật')}</td>
                <td class="text-end text-nowrap">
                    <a class="btn btn-info btn-sm"
                       href="index.html?page=form&id=${encodeURIComponent(id)}&mode=view"
                       title="Xem">
                        <i class="bi bi-eye"></i>
                    </a>
                    <a class="btn btn-warning btn-sm"
                       href="index.html?page=form&id=${encodeURIComponent(id)}&mode=edit"
                       title="Sửa">
                        <i class="bi bi-pencil-square"></i>
                    </a>
                    <button type="button" class="btn btn-danger btn-sm"
                        onclick="deleteStudent('${id}')" title="Xóa">
                        <i class="bi bi-trash"></i>
                    </button>
                </td>
            </tr>`;
    }).join('');
}

window.deleteStudent = async function (id) {
    if (!confirm('Bạn có chắc muốn xóa sinh viên này?')) return;

    try {
        await request(`${API_ENDPOINTS.STUDENTS}/${encodeURIComponent(id)}`, {
            method: 'DELETE'
        });

        alert('Đã xử lý yêu cầu xóa sinh viên.');
        await loadStudents(getValue('keyword'));
    } catch (error) {
        console.error('Lỗi xóa sinh viên:', error);
        alert(`Xóa sinh viên thất bại: ${error.message}`);
    }
};


async function initStudentForm() {
    const form = document.getElementById('studentForm');
    if (!form) return;

    const params = new URLSearchParams(location.search);
    const id = params.get('id');
    const mode = params.get('mode') || 'create';

    setupFormMode(mode);

    // Phải tải danh sách trước khi đổ dữ liệu sinh viên vào form.
    await loadSelectOptions();
    document.getElementById('studentClassessId')?.addEventListener('change', syncClassDetailsFromSelection);
    document.getElementById('departmentId')?.addEventListener('change', async () => {
        await loadMajorOptions(getValue('departmentId'));
    });
    document.getElementById('majorId')?.addEventListener('change', async () => {
        await loadProgramOptions(getValue('majorId'));
    });
    document.getElementById('studentCode')?.addEventListener('input', validateStudentCodeInput);

    if ((mode === 'view' || mode === 'edit') && id) {
        await loadStudentDetail(id, mode);
    }

    form.addEventListener('submit', async event => {
        event.preventDefault();

        if (mode !== 'view') {
            await saveStudent(mode, id);
        }
    });
}


function setupFormMode(mode) {
    const titles = {
        create: ['Thêm sinh viên', 'Thêm', 'Thêm sinh viên mới'],
        edit: ['Sửa sinh viên', 'Sửa', 'Cập nhật sinh viên'],
        view: ['Chi tiết sinh viên', 'Xem', 'Thông tin sinh viên']
    };

    const title = titles[mode] || titles.create;

    setText('pageTitle', title[0]);
    setText('pageHeading', title[0]);
    setText('breadcrumbTitle', title[1]);
    setText('formTitle', title[2]);

    const button = document.getElementById('submitButton');
    if (button) {
        button.style.display = mode === 'view' ? 'none' : '';
        button.innerHTML = mode === 'edit'
            ? '<i class="bi bi-check-circle me-1"></i>Lưu thay đổi'
            : '<i class="bi bi-plus-circle me-1"></i>Thêm sinh viên';
    }

    if (mode === 'view') {
        fields.forEach(id => {
            const element = document.getElementById(id);
            if (element) element.disabled = true;
        });
    }
}

async function loadStudentDetail(id, mode) {
    try {
        const student = await request(
            `${API_ENDPOINTS.STUDENTS}/${encodeURIComponent(id)}`
        );

        fillStudentForm(student);
        await syncClassDetailsFromSelection();

        if (mode === 'view') {
            fields.forEach(id => {
                const element = document.getElementById(id);
                if (element) element.disabled = true;
            });
        }
    } catch (error) {
        console.error('Lỗi lấy thông tin:', error);
        alert(`Không lấy được thông tin sinh viên: ${error.message}`);
        location.href = 'index.html';
    }
}

function fillStudentForm(student) {
    fields.forEach(id => {
        const element = document.getElementById(id);
        if (!element) return;

        const property = textToProperty[id];
        let value = student[property];

        if (id === 'studentCode') {
            value = student.studentCode ?? student.code;
        }

        if (id === 'academicYearYear' || id === 'admissionYear') {
            value = value ? String(value).slice(0, 16) : '';
        } else if (id === 'dateOfBirth' || id === 'dateOfIssue') {
            value = value ? String(value).slice(0, 10) : '';
        } else if (id === 'isActive') {
            value = value == null ? 'true' : String(value);
        } else if (value == null) {
            value = '';
        }

        element.value = value;
    });
    existingStudentCode = student.studentCode ?? student.code ?? null;
    validateStudentCodeInput();
}

function getStudentFormData() {
    const student = {};

    fields.forEach(id => {
        const element = document.getElementById(id);
        if (!element) return;

        const property = textToProperty[id];
        let value = element.value.trim();

        if (id === 'isActive') {
            student[property] = value === '' ? null : value === 'true';
        } else if ([
            'departmentId',
            'majorId',
            'trainingProgramId',
            'statusId',
            'studentClassessId'
        ].includes(id)) {
            student[property] = value || null;
        } else {
            student[property] = value || null;
        }
    });

    return student;
}

async function saveStudent(mode, id) {
    const student = getStudentFormData();

    if (!student.code) {
        alert('Vui lòng nhập mã sinh viên');
        return;
    }
    if (student.code !== existingStudentCode && !/^SV[0-9]{4,}$/i.test(student.code)) {
        alert('Mã sinh viên mới phải theo mẫu SV0001 (SV và ít nhất 4 chữ số)');
        return;
    }

    if (!student.fullName) {
        alert('Vui lòng nhập họ và tên');
        return;
    }

    const button = document.getElementById('submitButton');
    if (button) button.disabled = true;
    try {
        const isEdit = mode === 'edit' && id;
        const url = isEdit
            ? `${API_ENDPOINTS.STUDENTS}/${encodeURIComponent(id)}`
            : API_ENDPOINTS.STUDENTS;

        await request(url, {
            method: isEdit ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(student)
        });

        alert(isEdit
            ? 'Cập nhật sinh viên thành công'
            : 'Thêm sinh viên thành công');

        location.href = 'index.html';
    } catch (error) {
        console.error('Lỗi lưu sinh viên:', error);
        alert(`Lưu sinh viên thất bại: ${error.message}`);
    } finally {
        if (button) button.disabled = false;
    }
}

function getValue(id) {
    return document.getElementById(id)?.value?.trim() || '';
}

function setText(id, value) {
    const element = document.getElementById(id);
    if (element) element.textContent = value;
}

function escapeHtml(value) {
    if (value == null) return '';

    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function normalizeIdentifier(value) {
    return value == null ? '' : String(value).trim().toLowerCase();
}


async function loadSelectOptions() {
    const options = [
        {
            id: 'studentClassessId',
            url: '/api/student-form-options/classes',
            label: item => `${item.code || ''} - ${item.name || ''}`
        },
        {
            id: 'departmentId',
            url: '/api/student-form-options/departments',
            label: item => `${item.code || ''} - ${item.name || ''}`
        },
        {
            id: 'majorId',
            url: '/api/student-form-options/majors',
            label: item => `${item.code || ''} - ${item.name || ''}`
        },
        {
            id: 'trainingProgramId',
            url: '/api/student-form-options/training-programs',
            label: item => `${item.code || ''} - ${item.name || ''}`
        },
        {
            id: 'statusId',
            url: '/api/student-form-options/statuses',
            label: item => `${item.code || ''} - ${item.name || ''}`
        }
    ];

    const submitButton = document.getElementById('submitButton');
    if (submitButton) submitButton.disabled = true;
    setFormError('');
    try {
        await Promise.all(options.map(async option => {
            const items = await request(option.url);
            if (option.id === 'studentClassessId') studentClassOptions = items;
            populateSelect(option.id, items, option.label);
        }));
        if (submitButton) submitButton.disabled = false;
    } catch (error) {
        console.error('Không tải được dữ liệu cho biểu mẫu sinh viên:', error);
        setFormError(`Không thể tải danh sách lựa chọn. Vui lòng thử tải lại trang. ${error.message}`);
    }
}

async function loadMajorOptions(departmentId) {
    const suffix = departmentId ? `?departmentId=${encodeURIComponent(departmentId)}` : '';
    const items = await request(`/api/student-form-options/majors${suffix}`);
    populateSelect('majorId', items, item => `${item.code || ''} - ${item.name || ''}`);
    await loadProgramOptions('');
}

async function loadProgramOptions(majorId) {
    const suffix = majorId ? `?majorId=${encodeURIComponent(majorId)}` : '';
    const items = await request(`/api/student-form-options/training-programs${suffix}`);
    populateSelect('trainingProgramId', items, item => `${item.code || ''} - ${item.name || ''}`);
}

function populateSelect(id, items, label) {
    const select = document.getElementById(id);
    if (!select) return;
    const current = select.value;
    select.innerHTML = '<option value="">-- Vui lòng chọn --</option>';
    items.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.textContent = label(item);
        select.appendChild(option);
    });
    const matchingOption = items.find(item =>
        normalizeIdentifier(item.id) === normalizeIdentifier(current));
    if (matchingOption) select.value = matchingOption.id;
}

async function syncClassDetailsFromSelection() {
    const selectedClass = studentClassOptions.find(item =>
        normalizeIdentifier(item.id) === normalizeIdentifier(getValue('studentClassessId')));
    if (!selectedClass) return;
    if (selectedClass.departmentId) {
        document.getElementById('departmentId').value = selectedClass.departmentId;
        await loadMajorOptions(selectedClass.departmentId);
    }
    if (selectedClass.majorId) {
        document.getElementById('majorId').value = selectedClass.majorId;
        await loadProgramOptions(selectedClass.majorId);
    }
    if (selectedClass.trainingProgramId) {
        document.getElementById('trainingProgramId').value = selectedClass.trainingProgramId;
    }
}

function validateStudentCodeInput() {
    const input = document.getElementById('studentCode');
    if (!input) return;
    const unchanged = existingStudentCode != null && input.value.trim() === existingStudentCode;
    input.setCustomValidity(unchanged || /^SV[0-9]{4,}$/i.test(input.value.trim())
        ? ''
        : 'Mã sinh viên mới phải có dạng SV0001.');
}

function setFormError(message) {
    const element = document.getElementById('studentFormError');
    if (!element) return;
    element.textContent = message;
    element.classList.toggle('d-none', !message);
}
