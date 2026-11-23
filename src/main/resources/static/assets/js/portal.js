const nestApi = '/api';
const springApi = '/api';

const state = {
    accessToken: localStorage.getItem('nestAccessToken') || '',
    refreshToken: localStorage.getItem('nestRefreshToken') || '',
    user: null
};

const adminMenu = [
    { id: 'dashboard', label: 'Dashboard', icon: 'bi-grid-1x2-fill' },
    { id: 'students', label: 'Quản lý sinh viên', icon: 'bi-person-vcard' },
    { id: 'lecturers', label: 'Quản lý giảng viên', icon: 'bi-person-badge' },
    { id: 'academic-years', label: 'Quản lý niên khóa', icon: 'bi-calendar-range' },
    { id: 'school-years', label: 'Năm học & học kỳ', icon: 'bi-calendar3' },
    { id: 'courses', label: 'Quản lý học phần', icon: 'bi-journal-text' },
    { id: 'course-sections', label: 'Quản lý lớp học phần', icon: 'bi-building' },
    { id: 'registrations', label: 'Quản lý đăng ký', icon: 'bi-card-checklist' },
    { id: 'reports', label: 'Thống kê đào tạo', icon: 'bi-bar-chart-line' },
    { id: 'accounts', label: 'Quản lý tài khoản', icon: 'bi-shield-lock' }
];

const studentMenu = [
    { id: 'student-dashboard', label: 'Dashboard', icon: 'bi-grid-1x2-fill' },
    { id: 'open-sections', label: 'Lớp mở đăng ký', icon: 'bi-journal-check' },
    { id: 'my-registrations', label: 'Đăng ký của tôi', icon: 'bi-card-checklist' },
    { id: 'notifications', label: 'Thông báo', icon: 'bi-bell' }
];

const loginScreen = document.getElementById('loginScreen');
const appShell = document.getElementById('appShell');
const loginForm = document.getElementById('loginForm');
const loginStatus = document.getElementById('loginStatus');
const portalMenu = document.getElementById('portalMenu');
const portalContent = document.getElementById('portalContent');
const portalTitle = document.getElementById('portalTitle');
const portalBreadcrumb = document.getElementById('portalBreadcrumb');

async function nestRequest(path, options = {}) {
    const headers = { Accept: 'application/json', ...(options.headers || {}) };
    if (options.body) headers['Content-Type'] = 'application/json';
    if (state.accessToken) headers.Authorization = `Bearer ${state.accessToken}`;
    const response = await fetch(`${nestApi}${path}`, { ...options, headers });
    const payload = await response.json().catch(() => null);
    if (!response.ok) throw new Error(payload?.message || `Yêu cầu thất bại (HTTP ${response.status})`);
    return payload;
}

async function springRequest(path, options = {}) {
    const headers = { Accept: 'application/json', ...(options.headers || {}) };
    if (options.body) headers['Content-Type'] = 'application/json';
    const response = await fetch(`${springApi}${path}`, { ...options, headers });
    if (response.status === 204) return null;
    const payload = await response.json().catch(() => null);
    if (!response.ok) throw new Error(payload?.detail || payload?.message || `Yêu cầu thất bại (HTTP ${response.status})`);
    return payload;
}

loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    loginStatus.textContent = 'Đang đăng nhập...';
    const payload = Object.fromEntries(new FormData(loginForm));
    try {
        const result = await nestRequest('/auth/login', { method: 'POST', body: JSON.stringify(payload) });
        state.accessToken = result.accessToken;
        state.refreshToken = result.refreshToken;
        state.user = result.user;
        localStorage.setItem('nestAccessToken', state.accessToken);
        localStorage.setItem('nestRefreshToken', state.refreshToken);
        showShell();
    } catch (error) {
        loginStatus.textContent = error.message;
    }
});

document.getElementById('forgotPasswordButton').addEventListener('click', () => {
    openPasswordRecoveryDialog();
});

function openPasswordRecoveryDialog() {
    const dialog = document.createElement('dialog');
    dialog.className = 'unified-dialog';
    dialog.innerHTML = `
        <form id="forgotPasswordForm">
            <div class="dialog-heading"><div><small>HỖ TRỢ TÀI KHOẢN</small><h2>Quên mật khẩu</h2></div><button type="button" class="dialog-close forgot-close"><i class="bi bi-x-lg"></i></button></div>
            <div class="dialog-fields" style="grid-template-columns:1fr">
                <div class="dialog-field"><label>Tên đăng nhập, email, mã sinh viên hoặc mã giảng viên</label><input name="identifier" required maxlength="255" placeholder="Ví dụ: SV001 hoặc gv001@..." /></div>
                <div class="dialog-field" id="resetTokenGroup" hidden><label>Mã đặt lại mật khẩu</label><input name="resetToken" maxlength="100" /></div>
                <div class="dialog-field" id="newPasswordGroup" hidden><label>Mật khẩu mới</label><input name="password" type="password" minlength="12" maxlength="72" /></div>
            </div>
            <p id="forgotPasswordStatus" class="dialog-status"></p>
            <div class="dialog-actions"><button type="button" class="outline-button forgot-close">Đóng</button><button type="submit" class="primary-button" id="forgotPasswordSubmit">Gửi yêu cầu</button></div>
        </form>`;
    document.body.append(dialog);
    dialog.showModal();
    dialog.querySelectorAll('.forgot-close').forEach((button) =>
        button.addEventListener('click', () => dialog.close()));
    dialog.addEventListener('close', () => dialog.remove());

    let resetMode = false;
    const form = dialog.querySelector('#forgotPasswordForm');
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        const status = dialog.querySelector('#forgotPasswordStatus');
        const payload = Object.fromEntries(new FormData(form).entries());
        try {
            if (!resetMode) {
                const result = await nestRequest('/auth/forgot-password', {
                    method: 'POST',
                    body: JSON.stringify({ identifier: payload.identifier })
                });
                status.textContent = result.resetToken
                    ? `${result.message} Mã kiểm thử: ${result.resetToken}`
                    : result.message;
                if (result.resetToken) {
                    resetMode = true;
                    dialog.querySelector('#resetTokenGroup').hidden = false;
                    dialog.querySelector('#newPasswordGroup').hidden = false;
                    dialog.querySelector('[name="resetToken"]').value = result.resetToken;
                    dialog.querySelector('#forgotPasswordSubmit').textContent = 'Đặt lại mật khẩu';
                }
            } else {
                await nestRequest('/auth/reset-password', {
                    method: 'POST',
                    body: JSON.stringify({ token: payload.resetToken, password: payload.password })
                });
                status.textContent = 'Đặt lại mật khẩu thành công. Vui lòng đăng nhập bằng mật khẩu mới.';
                dialog.querySelector('#forgotPasswordSubmit').disabled = true;
            }
        } catch (error) {
            status.textContent = error.message;
        }
    });
}

document.getElementById('portalLogout').addEventListener('click', async () => {
    if (state.refreshToken) {
        await nestRequest('/auth/logout', {
            method: 'POST',
            body: JSON.stringify({ refreshToken: state.refreshToken })
        }).catch(() => null);
    }
    state.accessToken = '';
    state.refreshToken = '';
    state.user = null;
    localStorage.removeItem('nestAccessToken');
    localStorage.removeItem('nestRefreshToken');
    appShell.hidden = true;
    loginScreen.hidden = false;
    loginForm.reset();
});

async function restoreSession() {
    if (!state.accessToken) return;
    try {
        state.user = await nestRequest('/auth/me');
        showShell();
    } catch {
        state.accessToken = '';
        state.refreshToken = '';
        localStorage.removeItem('nestAccessToken');
        localStorage.removeItem('nestRefreshToken');
    }
}

function showShell() {
    loginScreen.hidden = true;
    appShell.hidden = false;
    const isAdmin = state.user.roles.includes('ADMIN');
    const accountRole = isAdmin ? 'Quản trị viên' : state.user.roles.includes('LECTURER') ? 'Giảng viên' : 'Sinh viên';
    document.getElementById('accountRole').textContent = accountRole;
    document.getElementById('accountName').textContent = state.user.fullName;
    document.getElementById('accountMeta').textContent = translateRoles(state.user.roles);
    document.getElementById('accountInitial').textContent = state.user.fullName.charAt(0).toUpperCase();
    renderMenu(isAdmin);
    navigate(isAdmin ? 'dashboard' : 'student-dashboard');
}

function renderMenu(isAdmin) {
    const items = isAdmin ? adminMenu : studentMenu;
    portalMenu.replaceChildren();
    items.forEach((item) => {
        const button = document.createElement('button');
        button.type = 'button';
        button.className = 'menu-button';
        button.dataset.page = item.id;
        button.innerHTML = `<i class="bi ${item.icon}"></i><span>${escapeHtml(item.label)}</span>`;
        button.addEventListener('click', () => navigate(item.id));
        portalMenu.append(button);
    });
}

async function navigate(pageId) {
    document.querySelectorAll('.menu-button').forEach((button) =>
        button.classList.toggle('active', button.dataset.page === pageId));
    const item = [...adminMenu, ...studentMenu].find((entry) => entry.id === pageId);
    portalTitle.textContent = item?.label || 'Bảng điều khiển';
    portalBreadcrumb.textContent = state.user?.roles.includes('ADMIN') ? 'Quản trị hệ thống' : 'Cổng sinh viên';

    if (pageId === 'dashboard') return renderAdminDashboard();
    if (pageId === 'student-dashboard') return renderStudentDashboard();
    if (pageId === 'students') return renderStudentManager();
    if (pageId === 'lecturers') return renderSimpleResource('/lecturers', 'lecturer');
    if (pageId === 'academic-years') return renderSimpleResource('/academic-years', 'academic-year');
    if (pageId === 'school-years') return renderSchoolYearPage();
    if (pageId === 'courses') return renderSimpleResource('/courses', 'course');
    if (pageId === 'course-sections') return renderSimpleResource('/course-sections', 'course-section');
    if (pageId === 'registrations') return renderRegistrationAdmin();
    if (pageId === 'reports') return renderReports();
    if (pageId === 'accounts') return renderAccountManager();
    if (pageId === 'open-sections') return renderOpenSections();
    if (pageId === 'my-registrations') return renderMyRegistrations();
    if (pageId === 'notifications') return renderNotifications();
}

async function renderAdminDashboard() {
    portalContent.innerHTML = `
        <section class="metric-grid" id="dashboardMetrics">
            ${metricCard('bi-person-vcard', 'Sinh viên', 'students', '—')}
            ${metricCard('bi-person-badge', 'Giảng viên', 'lecturers', '—')}
            ${metricCard('bi-journal-text', 'Học phần', 'courses', '—')}
            ${metricCard('bi-building', 'Lớp học phần', 'course-sections', '—')}
        </section>
        <section class="dashboard-grid">
            <article class="panel-card"><div class="panel-heading"><h2>Hoạt động hệ thống</h2></div>
                <div class="activity-list">
                    <div class="activity-item"><span class="activity-icon green"><i class="bi bi-check-circle"></i></span><div><strong>SQL Server đã kết nối</strong><small>Cơ sở dữ liệu đào tạo đang hoạt động</small></div></div>
                    <div class="activity-item"><span class="activity-icon blue"><i class="bi bi-cloud-check"></i></span><div><strong>NestJS API đang hoạt động</strong><small>Xác thực, đăng ký và thống kê đã sẵn sàng</small></div></div>
                    <div class="activity-item"><span class="activity-icon violet"><i class="bi bi-shield-check"></i></span><div><strong>Tài khoản Admin đã xác thực</strong><small>Toàn quyền quản trị và theo dõi đăng ký</small></div></div>
                </div>
            </article>
            <article class="panel-card"><div class="panel-heading"><h2>Truy cập nhanh</h2></div>
                <div class="quick-grid">
                    ${quickAction('students', 'bi-person-plus', 'Thêm sinh viên', 'Tạo hồ sơ sinh viên')}
                    ${quickAction('lecturers', 'bi-person-badge', 'Giảng viên', 'Danh sách giảng viên')}
                    ${quickAction('course-sections', 'bi-building', 'Lớp học phần', 'Lịch học và sĩ số')}
                    ${quickAction('registrations', 'bi-card-checklist', 'Đăng ký', 'Duyệt và theo dõi')}
                </div>
            </article>
        </section>`;

    for (const metric of [
        ['students', 'students'],
        ['lecturers', 'lecturers'],
        ['courses', 'courses'],
        ['course-sections', 'course-sections']
    ]) {
        try {
            const rows = await springRequest(`/${metric[1]}`);
            const element = portalContent.querySelector(`[data-metric="${metric[0]}"]`);
            if (element) element.textContent = Array.isArray(rows) ? rows.length : 0;
        } catch {
            const element = portalContent.querySelector(`[data-metric="${metric[0]}"]`);
            if (element) element.textContent = '0';
        }
    }
    portalContent.querySelectorAll('[data-navigate]').forEach((button) =>
        button.addEventListener('click', () => navigate(button.dataset.navigate)));
}

function metricCard(icon, label, key, value) {
    return `<article class="metric-card"><div class="metric-icon"><i class="bi ${icon}"></i></div><div><strong data-metric="${key}">${value}</strong><span>${label}</span></div></article>`;
}

function quickAction(pageId, icon, title, description) {
    return `<button type="button" class="quick-action" data-navigate="${pageId}"><span><i class="bi ${icon}"></i></span><div><strong>${title}</strong><small>${description}</small></div><i class="bi bi-chevron-right quick-arrow"></i></button>`;
}

async function renderStudentDashboard() {
    portalContent.innerHTML = `
        <section class="metric-grid">
            ${metricCard('bi-journal-check', 'Lớp mở đăng ký', 'open', '—')}
            ${metricCard('bi-card-checklist', 'Đăng ký của tôi', 'mine', '—')}
            ${metricCard('bi-bell', 'Thông báo', 'notifications', '—')}
        </section>
        <section class="dashboard-grid">
            <article class="panel-card"><div class="panel-heading"><h2>Tiện ích sinh viên</h2></div><div class="quick-grid">
                ${quickAction('open-sections', 'bi-journal-check', 'Đăng ký học phần', 'Xem lớp học phần đang mở')}
                ${quickAction('my-registrations', 'bi-card-checklist', 'Lịch đăng ký', 'Theo dõi học phần đã đăng ký')}
                ${quickAction('notifications', 'bi-bell', 'Thông báo', 'Thông báo đăng ký và lịch học')}
            </div></article>
            <article class="panel-card"><div class="panel-heading"><h2>Tài khoản</h2></div>
                <div class="activity-item"><span class="activity-icon green"><i class="bi bi-person-check"></i></span><div><strong>${escapeHtml(state.user.fullName)}</strong><small>Tài khoản sinh viên đang hoạt động</small></div></div>
            </article>
        </section>`;
    try {
        const [open, mine, notifications] = await Promise.all([
            nestRequest('/course-registrations/open-sections').catch(() => []),
            nestRequest('/course-registrations/mine').catch(() => []),
            nestRequest('/notifications').catch(() => [])
        ]);
        portalContent.querySelector('[data-metric="open"]').textContent = open.length;
        portalContent.querySelector('[data-metric="mine"]').textContent = mine.length;
        portalContent.querySelector('[data-metric="notifications"]').textContent = notifications.length;
    } catch {
        // Dashboard metrics remain available as placeholders when an endpoint is unavailable.
    }
    portalContent.querySelectorAll('[data-navigate]').forEach((button) =>
        button.addEventListener('click', () => navigate(button.dataset.navigate)));
}

async function renderStudentManager() {
    const rows = await springRequest('/students').catch(() => []);
    const classes = await springRequest('/student-form-options/classes').catch(() => []);
    const fields = [
        ['code', 'Mã sinh viên'],
        ['fullName', 'Họ và tên'],
        ['email', 'Email'],
        ['phone', 'Số điện thoại']
    ];
    renderResourceShell({
        title: 'Quản lý sinh viên',
        description: 'Thêm, sửa, xóa và tìm kiếm hồ sơ sinh viên.',
        endpoint: '/students',
        rows,
        fields,
        lookupDefinitions: {
            studentClassessId: { rows: classes, codeKey: 'code', nameKey: 'name' }
        },
        addLabel: 'Thêm sinh viên'
    });
}

async function renderSimpleResource(endpoint, kind) {
    const rows = await springRequest(endpoint).catch(() => []);
    let lookupDefinitions = {};
    if (kind === 'lecturer') {
        const departments = await springRequest('/departments').catch(() => []);
        lookupDefinitions = {
            departmentId: { rows: departments, codeKey: 'code', nameKey: 'name' }
        };
    } else if (kind === 'course') {
        const departments = await springRequest('/departments').catch(() => []);
        lookupDefinitions = {
            departmentId: { rows: departments, codeKey: 'code', nameKey: 'name' }
        };
    } else if (kind === 'course-section') {
        const [courses, semesters, rooms] = await Promise.all([
            springRequest('/courses').catch(() => []),
            springRequest('/semesters').catch(() => []),
            springRequest('/academic-form-options/rooms').catch(() => [])
        ]);
        lookupDefinitions = {
            courseId: { rows: courses, codeKey: 'code', nameKey: 'name' },
            semesterId: { rows: semesters, codeKey: 'code', nameKey: 'name' },
            roomId: { rows: rooms, codeKey: 'code', nameKey: 'name' }
        };
    }
    const config = {
        lecturer: {
            title: 'Quản lý giảng viên',
            description: 'Thông tin giảng viên, liên hệ và khoa quản lý.',
            fields: [['lecturerCode', 'Mã giảng viên'], ['fullName', 'Họ và tên'], ['email', 'Email'], ['phone', 'Số điện thoại'], ['departmentId', 'Khoa']],
            addLabel: 'Thêm giảng viên'
        },
        'academic-year': {
            title: 'Quản lý niên khóa',
            description: 'Thiết lập niên khóa và thời gian đào tạo.',
            fields: [['code', 'Mã niên khóa'], ['name', 'Tên niên khóa'], ['year', 'Niên khóa'], ['startDate', 'Ngày bắt đầu'], ['endDate', 'Ngày kết thúc'], ['isActive', 'Trạng thái']],
            addLabel: 'Thêm niên khóa'
        },
        course: {
            title: 'Quản lý học phần',
            description: 'Danh mục học phần, tín chỉ, khoa và loại học phần.',
            fields: [['code', 'Mã học phần'], ['name', 'Tên học phần'], ['nameEn', 'Tên tiếng Anh'], ['credits', 'Số tín chỉ'], ['departmentId', 'Khoa'], ['courseType', 'Loại học phần']],
            addLabel: 'Thêm học phần'
        },
        'course-section': {
            title: 'Quản lý lớp học phần',
            description: 'Giảng viên, sĩ số, phòng học, lịch học và trạng thái lớp.',
            fields: [['code', 'Mã lớp'], ['name', 'Tên lớp'], ['courseId', 'Học phần'], ['semesterId', 'Học kỳ'], ['roomId', 'Phòng học'], ['maxStudents', 'Sĩ số tối đa'], ['status', 'Trạng thái']],
            addLabel: 'Thêm lớp học phần'
        }
    }[kind];
    renderResourceShell({
        ...config,
        endpoint,
        rows,
        lookupDefinitions
    });
}

async function renderSchoolYearPage() {
    const [schoolYears, semesters] = await Promise.all([
        springRequest('/school-years').catch(() => []),
        springRequest('/semesters').catch(() => [])
    ]);
    const semesterLookup = {
        schoolYearId: { rows: schoolYears, codeKey: 'code', nameKey: 'name' }
    };
    const tabs = [
        { id: 'school-years', title: 'Năm học', rows: schoolYears, fields: [['code', 'Mã năm học'], ['name', 'Tên năm học'], ['startDate', 'Ngày bắt đầu'], ['endDate', 'Ngày kết thúc'], ['isActive', 'Trạng thái']], lookupDefinitions: null },
        { id: 'semesters', title: 'Học kỳ', rows: semesters, fields: [['code', 'Mã học kỳ'], ['name', 'Tên học kỳ'], ['schoolYearId', 'Năm học'], ['startDate', 'Ngày bắt đầu'], ['endDate', 'Ngày kết thúc'], ['isActive', 'Trạng thái']], lookupDefinitions: semesterLookup }
    ];
    renderTabbedResource('Năm học & học kỳ', 'Thiết lập năm học và học kỳ.', tabs);
}

function renderTabbedResource(title, description, tabs) {
    portalContent.innerHTML = `
        <section class="page-heading"><div><h2>${escapeHtml(title)}</h2><p>${escapeHtml(description)}</p></div></section>
        <section class="panel-card resource-card">
            <div class="resource-toolbar">
                <div class="resource-tabs">${tabs.map((tab, index) => `<button type="button" class="resource-tab${index === 0 ? ' active' : ''}" data-tab-index="${index}">${escapeHtml(tab.title)}</button>`).join('')}</div>
            </div>
            <div id="tabbedResourceTable"></div>
        </section>`;
    const tableContainer = document.getElementById('tabbedResourceTable');
    const showTab = (index) => {
        document.querySelectorAll('.resource-tab').forEach((tab, tabIndex) =>
            tab.classList.toggle('active', tabIndex === index));
        tableContainer.innerHTML = renderTable(tabs[index].rows, tabs[index].fields, tabs[index].lookupDefinitions);
    };
    portalContent.querySelectorAll('.resource-tab').forEach((tab) =>
        tab.addEventListener('click', () => showTab(Number(tab.dataset.tabIndex))));
    showTab(0);
}

function renderResourceShell(config) {
    portalContent.innerHTML = `
        <section class="page-heading">
            <div><h2>${escapeHtml(config.title)}</h2><p>${escapeHtml(config.description)}</p></div>
            <button type="button" class="primary-button" id="resourceAdd"><i class="bi bi-plus-lg"></i>${escapeHtml(config.addLabel || 'Thêm mới')}</button>
        </section>
        <section class="panel-card resource-card">
            <div class="resource-toolbar">
                <div class="search-box"><i class="bi bi-search"></i><input id="resourceSearch" type="search" placeholder="Tìm kiếm..." /></div>
                <button type="button" class="outline-button" id="resourceRefresh"><i class="bi bi-arrow-clockwise"></i>Làm mới</button>
            </div>
            <div id="resourceTable">${renderTable(config.rows, config.fields, config.lookupDefinitions)}</div>
        </section>
        <dialog id="resourceDialog" class="unified-dialog">
            <form id="resourceForm">
                <div class="dialog-heading"><div><small>QUẢN LÝ ĐÀO TẠO</small><h2 id="resourceDialogTitle">${escapeHtml(config.addLabel || 'Thêm mới')}</h2></div><button type="button" class="dialog-close" id="resourceDialogClose"><i class="bi bi-x-lg"></i></button></div>
                <p id="resourceDialogStatus" class="dialog-status"></p>
                <div class="dialog-actions"><button type="button" class="outline-button" id="resourceDialogCancel">Hủy</button><button type="submit" class="primary-button">Lưu dữ liệu</button></div>
            </form>
        </dialog>`;

    const search = document.getElementById('resourceSearch');
    search.addEventListener('input', () => {
        const keyword = search.value.trim().toLocaleLowerCase('vi');
        const filtered = config.rows.filter((row) =>
            Object.values(row).some((value) => value != null && String(value).toLocaleLowerCase('vi').includes(keyword)));
        document.getElementById('resourceTable').innerHTML = renderTable(filtered, config.fields, config.lookupDefinitions);
        bindResourceTableActions(config);
    });
    document.getElementById('resourceRefresh').addEventListener('click', async () => {
        const rows = await springRequest(config.endpoint).catch(() => []);
        config.rows = rows;
        document.getElementById('resourceTable').innerHTML = renderTable(rows, config.fields);
        bindResourceTableActions(config);
    });
    bindResourceTableActions(config);

    document.getElementById('resourceAdd').addEventListener('click', () => openResourceDialog(config));
}

function renderTable(rows, fields, lookupDefinitions = null) {
    if (!Array.isArray(rows) || !rows.length) {
        return `<div class="empty-state"><i class="bi bi-inbox"></i><strong>Chưa có dữ liệu</strong><span>Danh sách trống hoặc dữ liệu chưa được tải.</span></div>`;
    }
    const body = rows.map((row) => `
        <tr>
            ${fields.map(([key, label]) => `<td><span class="mobile-label">${escapeHtml(label)}</span>${renderValue(key, row[key], lookupDefinitions)}</td>`).join('')}
            <td class="table-actions"><button type="button" class="icon-button edit" data-edit-id="${escapeHtml(row.id)}" title="Sửa"><i class="bi bi-pencil-square"></i></button><button type="button" class="icon-button delete" data-delete-id="${escapeHtml(row.id)}" title="Xóa"><i class="bi bi-trash"></i></button></td>
        </tr>`).join('');
    return `<div class="table-scroll"><table class="unified-table"><thead><tr>${fields.map(([, label]) => `<th>${escapeHtml(label)}</th>`).join('')}<th class="text-end">Thao tác</th></tr></thead><tbody>${body}</tbody></table></div><div class="table-footer"><span>${rows.length} bản ghi</span></div>`;
}

function renderValue(key, value, lookupDefinitions = null) {
    if (value == null || value === '') return '<span class="muted-value">Chưa cập nhật</span>';
    if (key === 'isActive') return value ? '<span class="status-badge active">Đang hiệu lực</span>' : '<span class="status-badge muted">Ngừng hiệu lực</span>';
    if (key === 'status') {
        const statusLabels = {
            open: 'Đang mở',
            planned: 'Dự kiến',
            closed: 'Đã đóng',
            canceled: 'Đã hủy',
            cancelled: 'Đã hủy',
            REGISTERED: 'Chờ duyệt',
            CONFIRMED: 'Đã duyệt',
            STUDYING: 'Đang học',
            COMPLETED: 'Hoàn thành',
            DROPPED: 'Đã rút'
        };
        const label = statusLabels[String(value)] || statusLabels[String(value).toUpperCase()] || String(value);
        const kind = ['open', 'CONFIRMED', 'COMPLETED'].includes(String(value)) ? 'active'
            : ['closed', 'canceled', 'cancelled', 'CANCELLED', 'DROPPED'].includes(String(value)) ? 'muted'
            : 'waiting';
        return `<span class="status-badge ${kind}">${escapeHtml(label)}</span>`;
    }
    if (key === 'courseType') {
        const courseLabels = { required: 'Bắt buộc', elective: 'Tự chọn', practical: 'Thực hành', GENERAL: 'Đại cương', MAJOR: 'Chuyên ngành' };
        return escapeHtml(courseLabels[String(value)] || courseLabels[String(value).toUpperCase()] || String(value));
    }
    const lookup = lookupDefinitions?.[key];
    if (lookup && Array.isArray(lookup.rows)) {
        const found = lookup.rows.find((item) => normalizeIdentifier(item.id) === normalizeIdentifier(value));
        if (found) {
            const code = found.code || found.lecturerCode || found.studentCode;
            const name = found.name || found.fullName || found.roomName;
            return escapeHtml([code, name].filter(Boolean).join(' — ') || String(value));
        }
    }
    if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}T/.test(value)) return escapeHtml(value.slice(0, 16).replace('T', ' '));
    return escapeHtml(String(value));
}

function bindResourceTableActions(config) {
    document.querySelectorAll('[data-delete-id]').forEach((button) => {
        button.addEventListener('click', async () => {
            if (!confirm('Xóa mềm bản ghi này?')) return;
            await springRequest(`/${config.endpoint.split('/').filter(Boolean).pop()}/${encodeURIComponent(button.dataset.deleteId)}`, { method: 'DELETE' }).catch((error) => alert(error.message));
            const rows = await springRequest(config.endpoint).catch(() => []);
            config.rows = rows;
            document.getElementById('resourceTable').innerHTML = renderTable(rows, config.fields);
            bindResourceTableActions(config);
        });
    });
    document.querySelectorAll('[data-edit-id]').forEach((button) =>
        button.addEventListener('click', () => openResourceDialog(config, config.rows.find((row) => row.id === button.dataset.editId))));
}

function openResourceDialog(config, record = null) {
    const dialog = document.getElementById('resourceDialog');
    const status = document.getElementById('resourceDialogStatus');
    document.getElementById('resourceDialogTitle').textContent = record ? `Cập nhật ${config.title.toLocaleLowerCase('vi')}` : (config.addLabel || 'Thêm mới');
    const form = document.getElementById('resourceForm');
    const existingFields = form.querySelectorAll('.dialog-fields');
    existingFields.forEach((element) => element.remove());
    const fields = config.fields.map(([key, label]) => `
        <div class="dialog-field"><label>${escapeHtml(label)}</label><input name="${escapeHtml(key)}" value="${escapeHtml(record?.[key] == null ? '' : String(record[key]))}" /></div>`).join('');
    const actions = form.querySelector('.dialog-actions');
    const fieldContainer = document.createElement('div');
    fieldContainer.className = 'dialog-fields';
    fieldContainer.innerHTML = fields;
    form.insertBefore(fieldContainer, actions);
    status.textContent = '';
    dialog.showModal();
    form.onsubmit = async (event) => {
        event.preventDefault();
        const payload = Object.fromEntries(new FormData(form).entries());
        for (const [key, value] of Object.entries(payload)) {
            if (value === '') payload[key] = null;
            else if (/^[-+]?\d+(\.\d+)?$/.test(value)) payload[key] = Number(value);
        }
        try {
            await springRequest(`${config.endpoint}${record ? `/${encodeURIComponent(record.id)}` : ''}`, {
                method: record ? 'PUT' : 'POST',
                body: JSON.stringify(payload)
            });
            dialog.close();
            const rows = await springRequest(config.endpoint).catch(() => []);
            config.rows = rows;
            document.getElementById('resourceTable').innerHTML = renderTable(rows, config.fields);
            bindResourceTableActions(config);
        } catch (error) {
            status.textContent = error.message;
        }
    };
    document.getElementById('resourceDialogClose').onclick = () => dialog.close();
    document.getElementById('resourceDialogCancel').onclick = () => dialog.close();
}

async function renderRegistrationAdmin() {
    portalContent.innerHTML = `
        <section class="page-heading"><div><h2>Quản lý đăng ký</h2><p>Theo dõi danh sách đăng ký và xử lý yêu cầu của sinh viên.</p></div><button type="button" class="outline-button" id="registrationRefresh"><i class="bi bi-arrow-clockwise"></i>Làm mới</button></section>
        <section class="panel-card resource-card"><div class="resource-toolbar"><div class="search-box"><i class="bi bi-search"></i><input id="registrationSearch" type="search" placeholder="Tìm sinh viên, mã lớp, trạng thái..." /></div></div><div id="registrationTable"></div></section>`;
    await loadRegistrationTable();
    document.getElementById('registrationRefresh').addEventListener('click', loadRegistrationTable);
}

async function loadRegistrationTable() {
    const rows = await nestRequest('/course-registrations/admin/all').catch(() => []);
    const search = document.getElementById('registrationSearch');
    const render = () => {
        const keyword = search.value.trim().toLocaleLowerCase('vi');
        const filtered = rows.filter((row) => Object.values(row).some((value) =>
            value != null && String(value).toLocaleLowerCase('vi').includes(keyword)));
        document.getElementById('registrationTable').innerHTML = `
            <div class="table-scroll"><table class="unified-table"><thead><tr><th>Sinh viên</th><th>Lớp học phần</th><th>Ngày đăng ký</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr></thead><tbody>
            ${filtered.map((row) => `<tr>
                <td><strong>${escapeHtml(row.studentCode || 'Không rõ mã SV')}</strong><small class="table-subtext">${escapeHtml(row.studentName || '')}</small></td>
                <td><strong>${escapeHtml(row.sectionCode || '')}</strong><small class="table-subtext">${escapeHtml(row.courseName || row.sectionName || '')}</small></td>
                <td>${renderValue('registrationDate', row.registrationDate)}</td>
                <td>${statusBadge(row.status)}</td>
                <td class="table-actions"><button type="button" class="status-action approve" data-status-id="${escapeHtml(row.id)}" data-status="CONFIRMED"><i class="bi bi-check2"></i>Duyệt</button><button type="button" class="status-action reject" data-status-id="${escapeHtml(row.id)}" data-status="CANCELLED"><i class="bi bi-x"></i>Từ chối</button></td>
            </tr>`).join('')}
            </tbody></table></div>${filtered.length ? `<div class="table-footer"><span>${filtered.length} đăng ký</span></div>` : '<div class="empty-state"><i class="bi bi-inbox"></i><strong>Chưa có đăng ký</strong></div>'}`;
        document.querySelectorAll('[data-status-id]').forEach((button) =>
            button.addEventListener('click', async () => {
                await nestRequest(`/course-registrations/admin/${encodeURIComponent(button.dataset.statusId)}/status`, {
                    method: 'PATCH',
                    body: JSON.stringify({ status: button.dataset.status })
                }).catch((error) => alert(error.message));
                await loadRegistrationTable();
            }));
    };
    search.addEventListener('input', render);
    render();
}

function statusBadge(status) {
    const label = {
        REGISTERED: 'Chờ duyệt',
        CONFIRMED: 'Đã duyệt',
        STUDYING: 'Đang học',
        COMPLETED: 'Hoàn thành',
        CANCELLED: 'Đã hủy',
        DROPPED: 'Đã rút'
    }[String(status)] || status || 'Chờ duyệt';
    const kind = String(status).toUpperCase() === 'CONFIRMED' || String(status).toUpperCase() === 'COMPLETED'
        ? 'active'
        : String(status).toUpperCase() === 'CANCELLED' || String(status).toUpperCase() === 'DROPPED'
            ? 'muted'
            : 'waiting';
    return `<span class="status-badge ${kind}">${escapeHtml(label)}</span>`;
}

async function renderReports() {
    portalContent.innerHTML = `
        <section class="page-heading"><div><h2>Thống kê đào tạo</h2><p>Báo cáo đăng ký theo học kỳ, học phần và sĩ số lớp.</p></div><button type="button" class="outline-button" id="reportRefresh"><i class="bi bi-arrow-clockwise"></i>Làm mới</button></section>
        <section class="metric-grid" id="reportMetrics"></section>
        <section class="panel-card resource-card"><div class="panel-heading"><h2>Đăng ký theo lớp học phần</h2></div><div id="reportTable"></div></section>`;
    await loadReports();
    document.getElementById('reportRefresh').addEventListener('click', loadReports);
}

async function loadReports() {
    const [summary, capacity, sections] = await Promise.all([
        nestRequest('/reports/registrations').catch(() => [{}]),
        nestRequest('/reports/course-sections').catch(() => [{}]),
        nestRequest('/reports/registrations-by-section').catch(() => [])
    ]);
    document.getElementById('reportMetrics').innerHTML = `
        ${metricCard('bi-person-vcard', 'Sinh viên đã đăng ký', 'report-students', summary[0]?.registeredStudents ?? 0)}
        ${metricCard('bi-card-checklist', 'Tổng lượt đăng ký', 'report-total', summary[0]?.totalRegistrations ?? 0)}
        ${metricCard('bi-door-open', 'Lớp đang mở', 'report-open', capacity[0]?.openSections ?? 0)}
        ${metricCard('bi-building-x', 'Lớp đã đầy', 'report-full', capacity[0]?.fullSections ?? 0)}
        ${metricCard('bi-person-plus', 'Chỗ còn lại', 'report-seats', capacity[0]?.remainingSeats ?? 0)}
    `;
    document.getElementById('reportTable').innerHTML = `
        <div class="table-scroll"><table class="unified-table"><thead><tr><th>Mã lớp</th><th>Tên lớp</th><th>Sĩ số tối đa</th><th>Đã đăng ký</th><th>Chỗ còn lại</th><th>Trạng thái</th></tr></thead><tbody>
        ${sections.map((row) => `<tr><td><strong>${escapeHtml(row.code)}</strong></td><td>${escapeHtml(row.name)}</td><td>${row.maxStudents ?? 'Không giới hạn'}</td><td>${row.enrolled ?? 0}</td><td>${row.remainingSeats == null ? 'Không giới hạn' : row.remainingSeats}</td><td>${row.isFull ? '<span class="status-badge muted">Đã đầy</span>' : '<span class="status-badge active">Còn chỗ</span>'}</td></tr>`).join('')}
        </tbody></table></div>${sections.length ? `<div class="table-footer"><span>${sections.length} lớp học phần</span></div>` : '<div class="empty-state"><i class="bi bi-bar-chart"></i><strong>Chưa có dữ liệu thống kê</strong></div>'}`;
}

async function renderOpenSections() {
    const sections = await nestRequest('/course-registrations/open-sections').catch(() => []);
    portalContent.innerHTML = `
        <section class="page-heading"><div><h2>Lớp mở đăng ký</h2><p>Chọn học phần phù hợp và kiểm tra điều kiện trước đăng ký.</p></div><button type="button" class="outline-button" id="openRefresh"><i class="bi bi-arrow-clockwise"></i>Làm mới</button></section>
        <section class="section-card-grid" id="openSectionsGrid"></section>`;
    renderOpenSectionCards(sections);
    document.getElementById('openRefresh').addEventListener('click', async () => {
        renderOpenSectionCards(await nestRequest('/course-registrations/open-sections').catch(() => []));
    });
}

function renderOpenSectionCards(sections) {
    const container = document.getElementById('openSectionsGrid');
    container.innerHTML = sections.length ? sections.map((section) => {
        const remaining = section.maxStudents == null ? null : Math.max(section.maxStudents - section.enrolled, 0);
        return `<article class="section-card"><div class="section-card-heading"><span class="section-code">${escapeHtml(section.code)}</span><span class="status-badge active">Đang mở</span></div><h3>${escapeHtml(section.name)}</h3><p>${escapeHtml(section.courseName || 'Học phần')}</p><div class="section-meta"><span><i class="bi bi-mortarboard"></i>${escapeHtml(String(section.credits ?? '—'))} tín chỉ</span><span><i class="bi bi-people"></i>${remaining == null ? 'Không giới hạn' : `${remaining} chỗ còn lại`}</span></div><button type="button" class="primary-button section-enroll" data-section-id="${escapeHtml(section.id)}">Đăng ký học phần</button></article>`;
    }).join('') : '<div class="empty-state wide"><i class="bi bi-journal-x"></i><strong>Hiện không có lớp mở đăng ký</strong><span>Hãy kiểm tra lại khi đến thời gian đăng ký.</span></div>';
    container.querySelectorAll('.section-enroll').forEach((button) =>
        button.addEventListener('click', async () => {
            try {
                await nestRequest('/course-registrations', {
                    method: 'POST',
                    body: JSON.stringify({ courseSectionId: button.dataset.sectionId })
                });
                await renderOpenSections();
            } catch (error) {
                alert(error.message);
            }
        }));
}

async function renderMyRegistrations() {
    const rows = await nestRequest('/course-registrations/mine').catch(() => []);
    portalContent.innerHTML = `
        <section class="page-heading"><div><h2>Đăng ký của tôi</h2><p>Theo dõi trạng thái và hủy đăng ký khi còn cho phép.</p></div></section>
        <section class="panel-card resource-card"><div id="myRegistrationTable">${renderMyRegistrationTable(rows)}</div></section>`;
}

function renderMyRegistrationTable(rows) {
    if (!rows.length) return '<div class="empty-state"><i class="bi bi-card-checklist"></i><strong>Bạn chưa đăng ký học phần</strong></div>';
    return `<div class="table-scroll"><table class="unified-table"><thead><tr><th>Mã lớp</th><th>Tên lớp</th><th>Học phần</th><th>Ngày đăng ký</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr></thead><tbody>${rows.map((row) => `<tr><td><strong>${escapeHtml(row.sectionCode)}</strong></td><td>${escapeHtml(row.sectionName)}</td><td>${escapeHtml(row.courseName || '')}</td><td>${renderValue('registrationDate', row.registrationDate)}</td><td>${statusBadge(row.status)}</td><td class="table-actions"><button type="button" class="status-action reject" data-cancel-id="${escapeHtml(row.id)}"><i class="bi bi-x"></i>Hủy</button></td></tr>`).join('')}</tbody></table></div>`;
}

async function renderNotifications() {
    const rows = await nestRequest('/notifications').catch(() => []);
    portalContent.innerHTML = `
        <section class="page-heading"><div><h2>Thông báo</h2><p>Cập nhật đăng ký, lịch học và thông báo từ quản trị.</p></div></section>
        <section class="panel-card resource-card"><div class="notification-list">${rows.length ? rows.map((row) => `<article class="notification-item ${row.readAt ? '' : 'unread'}"><span class="notification-icon"><i class="bi ${row.type === 'SCHEDULE' ? 'bi-calendar-event' : row.type === 'ANNOUNCEMENT' ? 'bi-megaphone' : 'bi-info-circle'}"></i></span><div><strong>${escapeHtml(row.title)}</strong><p>${escapeHtml(row.content)}</p><small>${renderValue('createdAt', row.createdAt)}</small></div>${row.readAt ? '' : `<button type="button" class="outline-button" data-read-id="${escapeHtml(row.id)}">Đánh dấu đã đọc</button>`}</article>`).join('') : '<div class="empty-state"><i class="bi bi-bell-slash"></i><strong>Chưa có thông báo</strong></div>'}</div></section>`;
    portalContent.querySelectorAll('[data-read-id]').forEach((button) =>
        button.addEventListener('click', async () => {
            await nestRequest(`/notifications/${encodeURIComponent(button.dataset.readId)}/read`, { method: 'PATCH' });
            await renderNotifications();
        }));
}

async function renderAccountManager() {
    portalContent.innerHTML = `
        <section class="page-heading">
            <div><h2>Quản lý tài khoản</h2><p>Tạo tài khoản cho sinh viên/giảng viên, khóa/mở khóa và hỗ trợ đặt lại mật khẩu.</p></div>
            <button type="button" class="primary-button" id="createAccountButton"><i class="bi bi-person-plus"></i>Tạo tài khoản</button>
        </section>
        <section class="panel-card resource-card">
            <div class="resource-toolbar">
                <div class="search-box"><i class="bi bi-search"></i><input id="accountSearch" type="search" placeholder="Tìm tài khoản, email, vai trò..." /></div>
                <button type="button" class="outline-button" id="accountRefresh"><i class="bi bi-arrow-clockwise"></i>Làm mới</button>
            </div>
            <div id="accountTable"></div>
        </section>`;

    await loadAccountTable();
    document.getElementById('accountRefresh').addEventListener('click', loadAccountTable);
    document.getElementById('createAccountButton').addEventListener('click', openCreateAccountDialog);
}

async function loadAccountTable() {
    const accounts = await nestRequest('/admin/users').catch(() => []);
    const search = document.getElementById('accountSearch');
    const render = () => {
        const keyword = search.value.trim().toLocaleLowerCase('vi');
        const filtered = accounts.filter((account) =>
            Object.values(account).some((value) => value != null && String(value).toLocaleLowerCase('vi').includes(keyword)));
        document.getElementById('accountTable').innerHTML = `
            <div class="table-scroll"><table class="unified-table"><thead><tr><th>Tên đăng nhập</th><th>Họ và tên</th><th>Email</th><th>Vai trò</th><th>Liên kết hồ sơ</th><th>Trạng thái</th><th class="text-end">Thao tác</th></tr></thead><tbody>
            ${filtered.map((account) => `
                <tr>
                    <td><strong>${escapeHtml(account.username)}</strong></td>
                    <td>${escapeHtml(account.fullName)}</td>
                    <td>${escapeHtml(account.email || 'Chưa cập nhật')}</td>
                    <td>${translateRoles(String(account.roles || '').split(',').filter(Boolean))}</td>
                    <td>${account.studentId ? 'Sinh viên đã liên kết' : account.lecturerId ? 'Giảng viên đã liên kết' : '<span class="muted-value">Chưa liên kết</span>'}</td>
                    <td>${account.isActive ? '<span class="status-badge active">Đang hoạt động</span>' : '<span class="status-badge muted">Đã khóa</span>'}</td>
                    <td class="table-actions">
                        <button type="button" class="status-action approve" data-account-action="toggle" data-account-id="${escapeHtml(account.id)}" data-account-active="${account.isActive ? '1' : '0'}">${account.isActive ? 'Khóa' : 'Mở khóa'}</button>
                        <button type="button" class="status-action reject" data-account-action="reset" data-account-id="${escapeHtml(account.id)}">Đặt lại mật khẩu</button>
                    </td>
                </tr>`).join('')}
            </tbody></table></div>${filtered.length ? `<div class="table-footer"><span>${filtered.length} tài khoản</span></div>` : '<div class="empty-state"><i class="bi bi-person-lock"></i><strong>Không có tài khoản phù hợp</strong></div>'}`;

        document.querySelectorAll('[data-account-action]').forEach((button) =>
            button.addEventListener('click', async () => {
                if (button.dataset.accountAction === 'toggle') {
                    const active = button.dataset.accountActive !== '1';
                    await nestRequest(`/admin/users/${encodeURIComponent(button.dataset.accountId)}/lock`, {
                        method: 'PATCH',
                        body: JSON.stringify({ active })
                    }).catch((error) => alert(error.message));
                    await loadAccountTable();
                } else {
                    openResetPasswordDialog(button.dataset.accountId);
                }
            }));
    };
    search.addEventListener('input', render);
    render();
}

async function openCreateAccountDialog() {
    const [students, lecturers] = await Promise.all([
        springRequest('/students').catch(() => []),
        springRequest('/lecturers').catch(() => [])
    ]);
    const dialog = document.createElement('dialog');
    dialog.className = 'unified-dialog';
    dialog.innerHTML = `
        <form id="createManagedAccountForm">
            <div class="dialog-heading"><div><small>QUẢN LÝ TÀI KHOẢN</small><h2>Tạo tài khoản giảng viên/sinh viên</h2></div><button type="button" class="dialog-close account-close"><i class="bi bi-x-lg"></i></button></div>
            <div class="dialog-fields">
                <div class="dialog-field"><label>Loại tài khoản</label><select name="type" id="managedAccountType"><option value="STUDENT">Sinh viên</option><option value="LECTURER">Giảng viên</option></select></div>
                <div class="dialog-field"><label>Hồ sơ</label><select name="identityId" id="managedIdentity" required></select></div>
                <div class="dialog-field"><label>Tên đăng nhập</label><input name="username" required minlength="3" maxlength="100" /></div>
                <div class="dialog-field"><label>Email</label><input name="email" type="email" required maxlength="255" /></div>
                <div class="dialog-field"><label>Mật khẩu</label><input name="password" type="password" required minlength="12" maxlength="72" /></div>
            </div>
            <p id="createManagedStatus" class="dialog-status"></p>
            <div class="dialog-actions"><button type="button" class="outline-button account-close">Hủy</button><button type="submit" class="primary-button">Tạo tài khoản</button></div>
        </form>`;
    document.body.append(dialog);
    dialog.showModal();
    dialog.querySelectorAll('.account-close').forEach((button) => button.addEventListener('click', () => dialog.close()));
    dialog.addEventListener('close', () => dialog.remove());

    const typeSelect = dialog.querySelector('#managedAccountType');
    const identitySelect = dialog.querySelector('#managedIdentity');
    const updateIdentityOptions = () => {
        const identities = typeSelect.value === 'STUDENT' ? students : lecturers;
        identitySelect.innerHTML = identities.map((item) => {
            const code = item.studentCode || item.code || item.lecturerCode;
            const name = item.fullName || item.name;
            return `<option value="${escapeHtml(item.id)}">${escapeHtml([code, name].filter(Boolean).join(' — '))}</option>`;
        }).join('');
    };
    typeSelect.addEventListener('change', updateIdentityOptions);
    updateIdentityOptions();

    dialog.querySelector('#createManagedAccountForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const status = dialog.querySelector('#createManagedStatus');
        const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
        try {
            await nestRequest('/admin/users/create-managed', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            status.textContent = 'Tạo tài khoản thành công.';
            await loadAccountTable();
            setTimeout(() => dialog.close(), 650);
        } catch (error) {
            status.textContent = error.message;
        }
    });
}

function openResetPasswordDialog(userId) {
    const dialog = document.createElement('dialog');
    dialog.className = 'unified-dialog';
    dialog.innerHTML = `
        <form id="adminResetPasswordForm">
            <div class="dialog-heading"><div><small>HỖ TRỢ TÀI KHOẢN</small><h2>Đặt lại mật khẩu</h2></div><button type="button" class="dialog-close reset-close"><i class="bi bi-x-lg"></i></button></div>
            <div class="dialog-fields" style="grid-template-columns:1fr">
                <div class="dialog-field"><label>Mật khẩu mới</label><input name="password" type="password" required minlength="12" maxlength="72" /></div>
                <div class="dialog-field"><label>Ghi chú hỗ trợ</label><input name="detail" maxlength="1000" /></div>
            </div>
            <p id="resetPasswordStatus" class="dialog-status"></p>
            <div class="dialog-actions"><button type="button" class="outline-button reset-close">Hủy</button><button type="submit" class="primary-button">Cập nhật mật khẩu</button></div>
        </form>`;
    document.body.append(dialog);
    dialog.showModal();
    dialog.querySelectorAll('.reset-close').forEach((button) => button.addEventListener('click', () => dialog.close()));
    dialog.addEventListener('close', () => dialog.remove());
    dialog.querySelector('#adminResetPasswordForm').addEventListener('submit', async (event) => {
        event.preventDefault();
        const status = dialog.querySelector('#resetPasswordStatus');
        const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
        try {
            await nestRequest(`/admin/users/${encodeURIComponent(userId)}/reset-password`, {
                method: 'PATCH',
                body: JSON.stringify(payload)
            });
            status.textContent = 'Đặt lại mật khẩu thành công.';
            setTimeout(() => dialog.close(), 650);
        } catch (error) {
            status.textContent = error.message;
        }
    });
}

function escapeHtml(value) {
    const element = document.createElement('span');
    element.textContent = value == null ? '' : String(value);
    return element.innerHTML;
}

function translateRoles(roles) {
    const labels = {
        ADMIN: 'Quản trị viên',
        LECTURER: 'Giảng viên',
        STUDENT: 'Sinh viên'
    };
    return (Array.isArray(roles) ? roles : []).map((role) => labels[String(role).toUpperCase()] || role).join(', ');
}

function normalizeIdentifier(value) {
    return value == null ? '' : String(value).trim().toLowerCase();
}

restoreSession();
