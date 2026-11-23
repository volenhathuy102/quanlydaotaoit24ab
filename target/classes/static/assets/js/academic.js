import { API_ENDPOINTS } from './config.js';

const resources = {
    courses: {
        title: 'Học phần',
        endpoint: API_ENDPOINTS.COURSES,
        columns: ['code', 'name', 'nameEn', 'departmentId', 'credits', 'courseType', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã học phần', required: true },
            { name: 'name', label: 'Tên học phần', required: true },
            { name: 'nameEn', label: 'Tên học phần tiếng Anh' },
            { name: 'departmentId', label: 'Khoa', type: 'lookup', lookup: 'departments', required: true },
            { name: 'credits', label: 'Số tín chỉ', type: 'number', min: 0 },
            { name: 'courseType', label: 'Loại học phần', type: 'select', options: [['required', 'Bắt buộc'], ['elective', 'Tự chọn'], ['practical', 'Thực hành']] },
            { name: 'description', label: 'Mô tả', type: 'textarea', wide: true },
            { name: 'isActive', label: 'Đang hoạt động', type: 'checkbox' }
        ]
    },
    departments: {
        title: 'Khoa',
        endpoint: API_ENDPOINTS.DEPARTMENTS,
        columns: ['code', 'name', 'establishedYear', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã khoa', required: true },
            { name: 'name', label: 'Tên khoa', required: true },
            { name: 'establishedYear', label: 'Ngày thành lập', type: 'date' },
            { name: 'description', label: 'Mô tả', type: 'textarea', wide: true },
            { name: 'isActive', label: 'Đang hoạt động', type: 'checkbox' }
        ]
    },
    majors: {
        title: 'Ngành',
        endpoint: API_ENDPOINTS.MAJORS,
        columns: ['code', 'name', 'departmentId', 'effectiveDate', 'expiryDate', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã ngành', required: true },
            { name: 'name', label: 'Tên ngành', required: true },
            { name: 'departmentId', label: 'Khoa', type: 'lookup', lookup: 'departments', required: true },
            { name: 'effectiveDate', label: 'Ngày hiệu lực', type: 'date' },
            { name: 'expiryDate', label: 'Ngày hết hiệu lực', type: 'date' },
            { name: 'description', label: 'Mô tả', type: 'textarea', wide: true },
            { name: 'isActive', label: 'Đang hoạt động', type: 'checkbox' }
        ]
    },
    'training-programs': {
        title: 'Chương trình đào tạo',
        endpoint: API_ENDPOINTS.TRAINING_PROGRAMS,
        columns: ['code', 'name', 'departmentId', 'majorId', 'admissionYear', 'totalCredits', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã chương trình', required: true },
            { name: 'name', label: 'Tên chương trình', required: true },
            { name: 'departmentId', label: 'Khoa', type: 'lookup', lookup: 'departments', required: true },
            { name: 'majorId', label: 'Ngành', type: 'lookup', lookup: 'majors', filterField: 'departmentId', filterProperty: 'departmentId', required: true },
            { name: 'admissionYear', label: 'Khóa tuyển sinh', type: 'date' },
            { name: 'durationYears', label: 'Thời gian đào tạo (năm)', type: 'number', min: 1 },
            { name: 'totalCredits', label: 'Tổng số tín chỉ', type: 'number', min: 0 },
            { name: 'effectiveDate', label: 'Ngày hiệu lực', type: 'date' },
            { name: 'expiryDate', label: 'Ngày hết hiệu lực', type: 'date' },
            { name: 'status', label: 'Trạng thái', type: 'select', options: [['active', 'Đang áp dụng'], ['draft', 'Dự thảo'], ['closed', 'Đã kết thúc']] },
            { name: 'description', label: 'Mô tả', type: 'textarea', wide: true },
            { name: 'isActive', label: 'Đang hoạt động', type: 'checkbox' }
        ]
    },
    'student-classes': {
        title: 'Lớp sinh viên',
        endpoint: API_ENDPOINTS.STUDENT_CLASSES,
        columns: ['code', 'name', 'academicYearId', 'departmentId', 'majorId', 'trainingProgramId', 'advisorId', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã lớp sinh viên', required: true, placeholder: 'IT24B' },
            { name: 'name', label: 'Tên lớp sinh viên', required: true },
            { name: 'academicYearId', label: 'Niên khóa', type: 'lookup', lookup: 'academic-years', required: true },
            { name: 'departmentId', label: 'Khoa', type: 'lookup', lookup: 'departments', required: true },
            { name: 'majorId', label: 'Ngành', type: 'lookup', lookup: 'majors', filterField: 'departmentId', filterProperty: 'departmentId', required: true },
            { name: 'trainingProgramId', label: 'Chương trình đào tạo', type: 'lookup', lookup: 'training-programs', filterField: 'majorId', filterProperty: 'majorId', required: true },
            { name: 'advisorId', label: 'Cố vấn học tập', type: 'lookup', lookup: 'lecturers' },
            { name: 'isActive', label: 'Đang hoạt động', type: 'checkbox' }
        ]
    },
    lecturers: {
        title: 'Giảng viên',
        endpoint: API_ENDPOINTS.LECTURERS,
        columns: ['lecturerCode', 'fullName', 'email', 'phone', 'departmentId', 'isActive'],
        fields: [
            { name: 'lecturerCode', label: 'Mã giảng viên', required: true },
            { name: 'fullName', label: 'Họ và tên', required: true },
            { name: 'departmentId', label: 'Khoa', type: 'lookup', lookup: 'departments', required: true },
            { name: 'email', label: 'Email', type: 'email' },
            { name: 'phone', label: 'Số điện thoại', type: 'tel' },
            { name: 'gender', label: 'Giới tính', type: 'select', options: [['Nam', 'Nam'], ['Nữ', 'Nữ'], ['Khác', 'Khác']] },
            { name: 'dateOfBirth', label: 'Ngày sinh', type: 'date' },
            { name: 'hireDate', label: 'Ngày tuyển dụng', type: 'date' },
            { name: 'isActive', label: 'Đang hoạt động', type: 'checkbox' }
        ]
    },
    'academic-years': {
        title: 'Niên khóa đào tạo',
        endpoint: API_ENDPOINTS.ACADEMIC_YEARS,
        columns: ['code', 'name', 'year', 'startDate', 'endDate', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã niên khóa', required: true },
            { name: 'name', label: 'Tên niên khóa', required: true },
            { name: 'year', label: 'Niên khóa', placeholder: '2023-2027' },
            { name: 'description', label: 'Mô tả', type: 'textarea', wide: true },
            { name: 'startDate', label: 'Ngày bắt đầu', type: 'date' },
            { name: 'endDate', label: 'Ngày kết thúc', type: 'date' },
            { name: 'isActive', label: 'Đang hiệu lực', type: 'checkbox' }
        ]
    },
    'school-years': {
        title: 'Năm đào tạo',
        endpoint: API_ENDPOINTS.SCHOOL_YEARS,
        columns: ['code', 'name', 'startDate', 'endDate', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã năm học', required: true },
            { name: 'name', label: 'Tên năm học', required: true, placeholder: '2025-2026' },
            { name: 'description', label: 'Mô tả', type: 'textarea' },
            { name: 'note', label: 'Ghi chú', type: 'textarea' },
            { name: 'startDate', label: 'Ngày bắt đầu', type: 'date' },
            { name: 'endDate', label: 'Ngày kết thúc', type: 'date' },
            { name: 'isActive', label: 'Đang hiệu lực', type: 'checkbox' }
        ]
    },
    semesters: {
        title: 'Học kỳ',
        endpoint: API_ENDPOINTS.SEMESTERS,
        columns: ['code', 'name', 'schoolYearName', 'startDate', 'endDate', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã học kỳ', required: true, placeholder: 'HK1_2025_2026' },
            { name: 'name', label: 'Tên học kỳ', required: true },
            { name: 'schoolYearId', label: 'Năm học', type: 'lookup', lookup: 'school-years', required: true },
            { name: 'schoolYearName', label: 'Năm học (hiển thị)', placeholder: '2025-2026' },
            { name: 'startDate', label: 'Ngày bắt đầu', type: 'date' },
            { name: 'endDate', label: 'Ngày kết thúc', type: 'date' },
            { name: 'isActive', label: 'Đang hiệu lực', type: 'checkbox' }
        ]
    },
    'course-sections': {
        title: 'Lớp học phần',
        endpoint: API_ENDPOINTS.COURSE_SECTIONS,
        columns: ['code', 'name', 'courseId', 'semesterId', 'roomId', 'buildingId', 'classType', 'status', 'maxStudents', 'isActive'],
        fields: [
            { name: 'code', label: 'Mã lớp học phần', required: true, placeholder: 'IT101-01' },
            { name: 'name', label: 'Tên lớp học phần', required: true },
            { name: 'courseId', label: 'Học phần', type: 'lookup', lookup: 'courses', required: true },
            { name: 'semesterId', label: 'Học kỳ', type: 'lookup', lookup: 'semesters', required: true },
            { name: 'roomId', label: 'Phòng học', type: 'lookup', lookup: 'rooms' },
            { name: 'buildingId', label: 'Tòa nhà', type: 'lookup', lookup: 'buildings' },
            { name: 'maxStudents', label: 'Sĩ số tối đa', type: 'number', min: 1 },
            { name: 'minStudents', label: 'Sĩ số tối thiểu', type: 'number', min: 0 },
            { name: 'classType', label: 'Loại lớp', type: 'select', options: [['theory', 'Lý thuyết'], ['lab', 'Thực hành'], ['hybrid', 'Kết hợp'], ['repeat', 'Học lại']] },
            { name: 'status', label: 'Trạng thái', type: 'select', options: [['planned', 'Dự kiến'], ['open', 'Đang mở đăng ký'], ['closed', 'Đã đóng'], ['canceled', 'Đã hủy']] },
            { name: 'registrationStart', label: 'Bắt đầu đăng ký', type: 'datetime-local' },
            { name: 'registrationEnd', label: 'Kết thúc đăng ký', type: 'datetime-local' },
            { name: 'note', label: 'Ghi chú', type: 'textarea', wide: true },
            { name: 'isActive', label: 'Đang hiệu lực', type: 'checkbox' }
        ]
    },
    'student-course-sections': {
        title: 'Sinh viên đăng ký học phần',
        endpoint: API_ENDPOINTS.STUDENT_COURSE_SECTIONS,
        columns: ['studentId', 'courseSectionId', 'registrationType', 'status', 'attemptNumber', 'registrationDate'],
        fields: [
            { name: 'studentId', label: 'Sinh viên', type: 'lookup', lookup: 'students', required: true },
            { name: 'courseSectionId', label: 'Lớp học phần', type: 'lookup', lookup: 'course-sections', required: true },
            { name: 'registrationDate', label: 'Ngày đăng ký', type: 'datetime-local' },
            { name: 'registrationType', label: 'Loại đăng ký', type: 'select', options: [['Học lần đầu', 'Học lần đầu'], ['Học lại', 'Học lại'], ['Học cải thiện', 'Học cải thiện'], ['Học thay thế', 'Học thay thế']] },
            { name: 'status', label: 'Trạng thái', type: 'select', options: [['REGISTERED', 'Đã đăng ký'], ['CONFIRMED', 'Đã xác nhận'], ['STUDYING', 'Đang học'], ['COMPLETED', 'Đã hoàn thành'], ['CANCELLED', 'Đã hủy'], ['DROPPED', 'Đã rút học phần']] },
            { name: 'attemptNumber', label: 'Lần học' },
            { name: 'note', label: 'Ghi chú', type: 'textarea', wide: true },
            { name: 'isActive', label: 'Đang hiệu lực', type: 'checkbox' }
        ]
    },
    'lecturer-course-classes': {
        title: 'Phân công giảng viên',
        endpoint: API_ENDPOINTS.LECTURER_COURSE_CLASSES,
        columns: ['lecturerId', 'courseClassId', 'role', 'isActive'],
        fields: [
            { name: 'lecturerId', label: 'Giảng viên', type: 'lookup', lookup: 'lecturers', required: true },
            { name: 'courseClassId', label: 'Lớp học phần', type: 'lookup', lookup: 'course-sections', required: true },
            { name: 'role', label: 'Vai trò', type: 'select', options: [['Giảng viên chính', 'Giảng viên chính'], ['Giảng viên phụ', 'Giảng viên phụ']] },
            { name: 'isActive', label: 'Đang hiệu lực', type: 'checkbox' }
        ]
    }
};

const columnLabels = {
    code: 'Mã',
    name: 'Tên',
    year: 'Niên khóa',
    startDate: 'Bắt đầu',
    endDate: 'Kết thúc',
    isActive: 'Hiệu lực',
    schoolYearName: 'Năm học',
    semesterId: 'Học kỳ',
    courseId: 'Học phần',
    roomId: 'Phòng học',
    buildingId: 'Tòa nhà',
    departmentId: 'Khoa',
    majorId: 'Ngành',
    trainingProgramId: 'Chương trình đào tạo',
    academicYearId: 'Niên khóa',
    advisorId: 'Cố vấn học tập',
    establishedYear: 'Thành lập',
    classType: 'Loại lớp',
    status: 'Trạng thái',
    maxStudents: 'Sĩ số tối đa',
    studentId: 'Sinh viên',
    courseSectionId: 'Lớp học phần',
    registrationType: 'Loại đăng ký',
    attemptNumber: 'Lần học',
    registrationDate: 'Ngày đăng ký',
    lecturerId: 'Giảng viên',
    courseClassId: 'Lớp học phần',
    role: 'Vai trò',
    lecturerCode: 'Mã giảng viên',
    fullName: 'Họ và tên',
    email: 'Email',
    phone: 'Số điện thoại',
    gender: 'Giới tính',
    dateOfBirth: 'Ngày sinh',
    hireDate: 'Ngày tuyển dụng',
    admissionYear: 'Khóa tuyển sinh',
    durationYears: 'Thời gian đào tạo (năm)',
    totalCredits: 'Tổng số tín chỉ',
    effectiveDate: 'Ngày hiệu lực',
    expiryDate: 'Ngày hết hiệu lực',
    description: 'Mô tả',
    note: 'Ghi chú',
    minStudents: 'Sĩ số tối thiểu',
    registrationStart: 'Bắt đầu đăng ký',
    registrationEnd: 'Kết thúc đăng ký',
    nameEn: 'Tên tiếng Anh',
    credits: 'Số tín chỉ',
    courseType: 'Loại học phần'
};

const lookupEndpoints = {
    students: API_ENDPOINTS.STUDENTS,
    'course-sections': API_ENDPOINTS.COURSE_SECTIONS,
    'school-years': API_ENDPOINTS.SCHOOL_YEARS,
    semesters: API_ENDPOINTS.SEMESTERS,
    'academic-years': API_ENDPOINTS.ACADEMIC_YEARS,
    courses: `${API_ENDPOINTS.ACADEMIC_FORM_OPTIONS}/courses`,
    rooms: `${API_ENDPOINTS.ACADEMIC_FORM_OPTIONS}/rooms`,
    buildings: `${API_ENDPOINTS.ACADEMIC_FORM_OPTIONS}/buildings`,
    departments: API_ENDPOINTS.DEPARTMENTS,
    majors: API_ENDPOINTS.MAJORS,
    'training-programs': API_ENDPOINTS.TRAINING_PROGRAMS,
    lecturers: API_ENDPOINTS.LECTURERS
};

const columnLookups = {
    courseId: 'courses',
    semesterId: 'semesters',
    roomId: 'rooms',
    buildingId: 'buildings',
    studentId: 'students',
    courseSectionId: 'course-sections',
    courseClassId: 'course-sections',
    lecturerId: 'lecturers',
    departmentId: 'departments',
    majorId: 'majors',
    trainingProgramId: 'training-programs',
    academicYearId: 'academic-years',
    advisorId: 'lecturers'
};

const columnValueLabels = {
    courseType: {
        required: 'Bắt buộc',
        elective: 'Tự chọn',
        practical: 'Thực hành'
    },
    registrationType: {
        first: 'Học lần đầu',
        retake: 'Học lại',
        improvement: 'Học cải thiện',
        replacement: 'Học thay thế'
    },
    status: {
        REGISTERED: 'Đã đăng ký',
        CONFIRMED: 'Đã xác nhận',
        STUDYING: 'Đang học',
        COMPLETED: 'Đã hoàn thành',
        CANCELLED: 'Đã hủy',
        DROPPED: 'Đã rút học phần'
    },
    role: {
        primary: 'Giảng viên chính',
        secondary: 'Giảng viên phụ',
        assistant: 'Giảng viên phụ'
    }
};

let activeKey = 'academic-years';
let activeRecords = [];
let editRecord = null;
const lookupData = new Map();

const elements = {
    tabs: document.getElementById('resourceTabs'),
    title: document.getElementById('resourceTitle'),
    hint: document.getElementById('resourceHint'),
    search: document.getElementById('resourceSearch'),
    head: document.getElementById('tableHead'),
    body: document.getElementById('resourceTableBody'),
    error: document.getElementById('resourceError'),
    count: document.getElementById('resultCount'),
    dialog: document.getElementById('recordDialog'),
    form: document.getElementById('recordForm'),
    dialogTitle: document.getElementById('dialogTitle'),
    dialogFields: document.getElementById('dialogFields'),
    formError: document.getElementById('formError'),
    saveButton: document.getElementById('saveButton')
};

document.addEventListener('DOMContentLoaded', () => {
    const requestedResource = new URLSearchParams(window.location.search).get('resource');
    if (requestedResource && resources[requestedResource]) {
        activeKey = requestedResource;
    }

    renderTabs();
    selectResource(activeKey);

    document.getElementById('createButton').addEventListener('click', () => openDialog());
    document.getElementById('refreshButton').addEventListener('click', () => {
        lookupData.clear();
        loadRecords();
    });
    elements.search.addEventListener('input', renderRecords);
    elements.tabs.addEventListener('click', (event) => {
        const tab = event.target.closest('[data-resource]');
        if (tab) {
            selectResource(tab.dataset.resource);
        }
    });
    elements.body.addEventListener('click', handleTableAction);
    document.getElementById('closeDialog').addEventListener('click', closeDialog);
    document.getElementById('cancelDialog').addEventListener('click', closeDialog);
    elements.form.addEventListener('submit', saveRecord);
});

function renderTabs() {
    elements.tabs.innerHTML = Object.entries(resources).map(([key, resource]) => `
        <button class="resource-tab${key === activeKey ? ' active' : ''}" type="button"
                role="tab" aria-selected="${key === activeKey}" data-resource="${key}">
            ${escapeHtml(resource.title)}
        </button>
    `).join('');
}

function selectResource(key) {
    activeKey = key;
    const resource = resources[key];
    elements.title.textContent = resource.title;
    elements.hint.textContent = `Danh sách ${resource.title.toLocaleLowerCase('vi')}`;
    elements.search.value = '';
    renderTabs();
    renderHead();
    loadRecords();
}

async function fetchJson(url, options) {
    const response = await fetch(url, options);
    if (!response.ok) {
        let message = `Yêu cầu thất bại (HTTP ${response.status})`;
        try {
            const body = await response.json();
            message = body.detail || body.message || message;
        } catch {
            // Keep the HTTP status message when the server response is not JSON.
        }
        throw new Error(message);
    }
    if (response.status === 204) {
        return null;
    }
    return response.json();
}

async function loadRecords() {
    elements.error.classList.add('d-none');
    elements.body.innerHTML = `<tr><td colspan="${resources[activeKey].columns.length + 1}" class="text-center text-secondary py-5"><span class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>Đang tải dữ liệu...</td></tr>`;

    try {
        activeRecords = await fetchJson(resources[activeKey].endpoint);
        const lookups = [...new Set(resources[activeKey].columns.map(column => columnLookups[column]).filter(Boolean))];
        await Promise.all(lookups.map(loadLookup));
        renderRecords();
    } catch (error) {
        console.error('Không thể tải dữ liệu:', error);
        elements.body.innerHTML = `<tr><td colspan="${resources[activeKey].columns.length + 1}" class="text-center text-danger py-5">Không thể tải dữ liệu. ${escapeHtml(error.message)}</td></tr>`;
        elements.count.textContent = 'Không tải được dữ liệu';
    }
}

function renderHead() {
    const columns = resources[activeKey].columns;
    elements.head.innerHTML = columns.map((column) => `<th>${escapeHtml(columnLabels[column] || column)}</th>`).join('')
        + '<th class="text-end">Thao tác</th>';
}

function renderRecords() {
    const columns = resources[activeKey].columns;
    const keyword = elements.search.value.trim().toLocaleLowerCase('vi');
    const records = activeRecords.filter((record) => !keyword
        || Object.values(record).some((value) => value != null && String(value).toLocaleLowerCase('vi').includes(keyword)));

    if (!records.length) {
        elements.body.innerHTML = `<tr><td colspan="${columns.length + 1}" class="text-center text-secondary py-5">${activeRecords.length ? 'Không tìm thấy bản ghi phù hợp.' : 'Chưa có dữ liệu. Hãy thêm bản ghi đầu tiên.'}</td></tr>`;
        elements.count.textContent = `${activeRecords.length} bản ghi`;
        return;
    }

    elements.body.innerHTML = records.map((record) => `
        <tr>
            ${columns.map((column) => `<td>${renderCell(column, record[column])}</td>`).join('')}
            <td class="text-end">
                <div class="table-actions">
                    <button type="button" class="btn btn-sm" title="Sửa" aria-label="Sửa"
                            data-action="edit" data-id="${escapeHtml(record.id)}"><i class="bi bi-pencil-square"></i></button>
                    <button type="button" class="btn btn-sm" title="Xóa mềm" aria-label="Xóa"
                            data-action="delete" data-id="${escapeHtml(record.id)}"><i class="bi bi-trash"></i></button>
                </div>
            </td>
        </tr>
    `).join('');
    elements.count.textContent = `${records.length} / ${activeRecords.length} bản ghi`;
}

function renderCell(column, value) {
    if (value == null || value === '') {
        return '<span class="text-secondary">—</span>';
    }
    if (column === 'isActive') {
        return `<span class="status-pill">${value ? 'Đang hiệu lực' : 'Ngừng hiệu lực'}</span>`;
    }
    const labels = columnValueLabels[column];
    if (labels?.[String(value)]) {
        return escapeHtml(labels[String(value)]);
    }
    if (columnLookups[column]) {
        const lookup = columnLookups[column];
        const record = (lookupData.get(lookup) || []).find((item) =>
            normalizeIdentifier(item.id) === normalizeIdentifier(value));
        if (record) {
            const code = record.code || record.lecturerCode || record.studentCode;
            const name = record.name || record.fullName || record.roomName;
            return escapeHtml([code, name].filter(Boolean).join(' — ') || value);
        }
    }
    return escapeHtml(String(value));
}

async function handleTableAction(event) {
    const button = event.target.closest('[data-action]');
    if (!button) {
        return;
    }
    const { action, id } = button.dataset;
    if (action === 'edit') {
        const record = activeRecords.find((item) => item.id === id);
        if (record) {
            await openDialog(record);
        }
        return;
    }
    if (action === 'delete' && window.confirm('Xóa mềm bản ghi này? Dữ liệu vẫn được lưu trong hệ thống.')) {
        button.disabled = true;
        try {
            await fetchJson(`${resources[activeKey].endpoint}/${encodeURIComponent(id)}`, { method: 'DELETE' });
            lookupData.clear();
            await loadRecords();
        } catch (error) {
            console.error('Không thể xóa bản ghi:', error);
            showError(elements.error, error.message);
            button.disabled = false;
        }
    }
}

async function openDialog(record = null) {
    editRecord = record;
    const resource = resources[activeKey];
    elements.dialogTitle.textContent = record ? `Cập nhật ${resource.title.toLocaleLowerCase('vi')}` : `Thêm ${resource.title.toLocaleLowerCase('vi')}`;
    elements.formError.classList.add('d-none');
    elements.saveButton.disabled = true;
    elements.dialogFields.innerHTML = '<div class="text-secondary">Đang tải biểu mẫu...</div>';
    elements.dialog.showModal();

    try {
        const lookups = [...new Set(resource.fields.filter((field) => field.lookup).map((field) => field.lookup))];
        await Promise.all(lookups.map(loadLookup));
        elements.dialogFields.innerHTML = resource.fields.map((field) => renderField(field, record)).join('');
        setupDependentLookups(resource);
        elements.saveButton.disabled = false;
    } catch (error) {
        console.error('Không thể tải dữ liệu tham chiếu:', error);
        showError(elements.formError, error.message);
        elements.dialogFields.innerHTML = '';
    }
}

async function loadLookup(key) {
    if (!lookupData.has(key)) {
        const data = await fetchJson(lookupEndpoints[key]);
        lookupData.set(key, data);
    }
    return lookupData.get(key);
}

function renderField(field, record) {
    const id = `field-${field.name}`;
    const value = record?.[field.name];
    const required = field.required ? ' required' : '';
    const wide = field.wide ? ' field-wide' : '';

    if (field.type === 'checkbox') {
        const checked = value == null ? true : Boolean(value);
        return `<div class="form-check form-switch${wide} pt-2"><input class="form-check-input" type="checkbox" role="switch" id="${id}" name="${field.name}"${checked ? ' checked' : ''}><label class="form-check-label" for="${id}">${escapeHtml(field.label)}</label></div>`;
    }
    if (field.type === 'textarea') {
        return `<div class="field-group${wide}"><label class="form-label" for="${id}">${escapeHtml(field.label)}</label><textarea class="form-control" id="${id}" name="${field.name}" rows="2" maxlength="255" placeholder="${escapeHtml(field.placeholder || '')}">${escapeHtml(value || '')}</textarea></div>`;
    }

    let input;
    if (field.type === 'select' || field.type === 'lookup') {
        const lookupItems = field.type === 'lookup'
            ? (lookupData.get(field.lookup) || []).filter(item => {
                if (!field.filterField) return true;
                const selectedParent = record?.[field.filterField]
                    ?? elements.form.elements.namedItem(field.filterField)?.value;
                return !selectedParent
                    || normalizeIdentifier(item[field.filterProperty]) === normalizeIdentifier(selectedParent);
            })
            : [];
        const options = field.type === 'lookup'
            ? lookupItems.map((item) => [
                item.id,
                [item.code || item.lecturerCode || item.studentCode, item.name || item.fullName || item.roomName].filter(Boolean).join(' — ') || item.id
            ])
            : field.options;
        input = `<select class="form-select" id="${id}" name="${field.name}"${required}><option value="">-- Chọn ${escapeHtml(field.label.toLocaleLowerCase('vi'))} --</option>${options.map(([optionValue, label]) => `<option value="${escapeHtml(optionValue)}"${String(value ?? '') === String(optionValue) ? ' selected' : ''}>${escapeHtml(label)}</option>`).join('')}</select>`;
    } else {
        const type = field.type || 'text';
        const fieldValue = value == null ? '' : type === 'datetime-local' ? String(value).slice(0, 16) : value;
        input = `<input class="form-control" type="${type}" id="${id}" name="${field.name}" value="${escapeHtml(fieldValue)}" placeholder="${escapeHtml(field.placeholder || '')}"${field.min == null ? '' : ` min="${field.min}"`}${field.type === 'uuid' ? ' pattern="[0-9a-fA-F-]{36}" title="Nhập UUID gồm 36 ký tự"' : ''}${required}>`;
    }
    const hint = field.type === 'uuid' ? '<span class="id-field-hint">Nhập mã định danh UUID từ dữ liệu tương ứng.</span>' : '';
    return `<div class="field-group${wide}"><label class="form-label" for="${id}">${escapeHtml(field.label)}${field.required ? ' <span class="text-danger">*</span>' : ''}</label>${input}${hint}</div>`;
}

function setupDependentLookups(resource) {
    for (const parentField of resource.fields) {
        const parentInput = elements.form.elements.namedItem(parentField.name);
        if (!parentInput) continue;
        parentInput.addEventListener('change', () => {
            for (const field of resource.fields.filter(item => item.filterField === parentField.name)) {
                const select = elements.form.elements.namedItem(field.name);
                const current = select.value;
                const options = (lookupData.get(field.lookup) || []).filter(item =>
                    !parentInput.value
                    || normalizeIdentifier(item[field.filterProperty]) === normalizeIdentifier(parentInput.value));
                select.innerHTML = '<option value="">-- Chọn --</option>' + options.map(item => {
                    const label = [item.code || item.lecturerCode, item.name || item.fullName]
                        .filter(Boolean).join(' — ') || item.id;
                    return `<option value="${escapeHtml(item.id)}">${escapeHtml(label)}</option>`;
                }).join('');
                const matchingOption = options.find(item =>
                    normalizeIdentifier(item.id) === normalizeIdentifier(current));
                if (matchingOption) select.value = matchingOption.id;
            }
        });
    }
}

async function saveRecord(event) {
    event.preventDefault();
    elements.formError.classList.add('d-none');
    const resource = resources[activeKey];
    const payload = {};

    for (const field of resource.fields) {
        const input = elements.form.elements.namedItem(field.name);
        if (field.type === 'checkbox') {
            payload[field.name] = input.checked;
        } else if (input.value !== '') {
            payload[field.name] = field.type === 'number' ? Number(input.value) : input.value;
        } else {
            payload[field.name] = null;
        }
    }

    if (activeKey === 'semesters' && payload.schoolYearId) {
        const year = (lookupData.get('school-years') || []).find((item) =>
            normalizeIdentifier(item.id) === normalizeIdentifier(payload.schoolYearId));
        if (year && !payload.schoolYearName) {
            payload.schoolYearName = year.name || year.code;
        }
    }
    if (activeKey === 'course-sections') {
        if (payload.minStudents != null && payload.maxStudents != null && payload.minStudents > payload.maxStudents) {
            showError(elements.formError, 'Sĩ số tối thiểu không được lớn hơn sĩ số tối đa.');
            return;
        }
        if (payload.registrationStart && payload.registrationEnd && payload.registrationStart > payload.registrationEnd) {
            showError(elements.formError, 'Thời gian bắt đầu đăng ký phải trước thời gian kết thúc.');
            return;
        }
    }
    if (['academic-years', 'school-years', 'semesters'].includes(activeKey)
        && payload.startDate && payload.endDate && payload.startDate > payload.endDate) {
        showError(elements.formError, 'Ngày bắt đầu phải trước hoặc bằng ngày kết thúc.');
        return;
    }

    elements.saveButton.disabled = true;
    try {
        const url = editRecord
            ? `${resource.endpoint}/${encodeURIComponent(editRecord.id)}`
            : resource.endpoint;
        await fetchJson(url, {
            method: editRecord ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        closeDialog();
        lookupData.clear();
        await loadRecords();
    } catch (error) {
        console.error('Không thể lưu bản ghi:', error);
        showError(elements.formError, error.message);
    } finally {
        elements.saveButton.disabled = false;
    }
}

function closeDialog() {
    elements.dialog.close();
    elements.form.reset();
    editRecord = null;
}

function showError(element, message) {
    element.textContent = message || 'Đã xảy ra lỗi.';
    element.classList.remove('d-none');
}

function escapeHtml(value) {
    return String(value ?? '').replace(/[&<>"']/g, (character) => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    })[character]);
}

function normalizeIdentifier(value) {
    return value == null ? '' : String(value).trim().toLowerCase();
}
