// 检查用户是否已登录
function checkLogin() {
    const token = getCookie('token');
    if (!token) {
        window.location.href = '/auth/login';
        return false;
    }
    return true;
}

// 从cookie中获取token的函数
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// 设置全局Ajax默认行为，在请求头中添加Authorization
$.ajaxSetup({
    beforeSend: function(xhr) {
        const token = getCookie('token');
        if (token) {
            xhr.setRequestHeader('Authorization', token);
        }
    }
});

const queryForm = {
    pos: 0,
    keyword: "",
    filters: [],
    orders: [],
    foreignMode: false,
    viewFields: [],
    searchFields: []
};

$(document).ready(function () {
    let data = {};
    let adminJson = {};
    const pageSize = 10;
    let currentPage = 1;
    let totalPages;
    let currentSortField = '';
    let currentSortOrder = 'asc';
    let currentModel = ''; // 当前选中的模型
    let userData = null; // 存储用户数据
    let editableFields = []; // 可编辑字段列表
    let showableFields = []; // 可显示字段列表

    // 从cookie中获取token
    const token = getCookie('token');
      
    if (!token) {
        // 如果没有token，重定向到登录页面
        window.location.href = '/auth/login';
        return;
    }

    // 调用/admin/json接口获取数据
    $.ajax({
        url: '/admin/json',
        method: 'POST',
        success: function(response) {
            if (response) {
                // 将数据存储到adminJson中
                adminJson = response;
                console.log('管理员数据已加载到adminJson中:', adminJson);
                // 初始化侧边栏菜单
                initSidebarMenu();
                
                // 获取用户表的配置信息
                const userModel = adminJson.data.objects.find(obj => obj.tableName === 'user');
                if (userModel) {
                    editableFields = userModel.edits || [];
                    showableFields = userModel.shows || [];
                    // 获取并显示当前用户信息
                    userData = adminJson.data.user;
                    renderUserInfo();
                }
            } else {
                console.error('获取管理员数据失败');
            }
        },
        error: function(xhr, status, error) {
            console.error('请求失败:', error);
            console.error('状态:', status);
            console.error('响应:', xhr.responseText);
            alert('用户未登录自动跳转到登录页面...');
            checkLogin();
        }
    });

    // 渲染用户信息
    function renderUserInfo() {
        const container = $('#user-info-container');
        container.empty();

        showableFields.forEach(field => {
            const value = userData[field] || '';
            const isEditable = editableFields.includes(field);
            
            const fieldDiv = $(`
                <div class="field-item" data-field="${field}">
                    <label class="block text-sm font-medium text-gray-700 mb-1">${field}</label>
                    <div class="field-value ${isEditable ? 'cursor-pointer hover:bg-gray-50' : ''} h-10 px-3 flex items-center rounded-md border ${isEditable ? 'border-gray-200 hover:border-gray-300' : 'border-transparent'} transition-colors duration-200">
                        ${value ? `<span class="text-gray-900">${value}</span>` : '<span class="text-gray-400">未设置</span>'}
                    </div>
                </div>
            `);
            
            container.append(fieldDiv);
        });
    }

    // 字段点击事件
    $(document).on('click', '.field-value', function() {
        const fieldItem = $(this).closest('.field-item');
        const field = fieldItem.data('field');
        
        // 检查字段是否可编辑
        if (!editableFields.includes(field)) {
            return;
        }
        
        // 如果已经是输入框，不需要再次转换
        if (fieldItem.find('input').length > 0) {
            return;
        }
        
        const value = userData[field] || '';
        
        // 创建输入框，保持相同的高度和内边距
        const input = $(`<input type="text" class="w-full text-sm outline-none border border-gray-300 rounded-md h-10 px-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition-all duration-200" value="${value}">`);
        
        // 替换原有的显示元素
        $(this).replaceWith(input);
        input.focus();
        
        // 显示更新按钮
        $('#update-container').removeClass('hidden');
    });

    // 取消编辑
    $('#cancel-edit').click(function() {
        renderUserInfo();
        $('#update-container').addClass('hidden');
    });

    // 更新用户信息
    $('#update-user-info').click(function() {
        const updateData = {};
        let hasChanges = false;

        // 收集所有输入框的值
        $('.field-item').each(function() {
            const field = $(this).data('field');
            const input = $(this).find('input');
            
            if (input.length > 0) {
                const newValue = input.val();
                if (newValue !== userData[field]) {
                    updateData[field] = newValue;
                    hasChanges = true;
                }
            }
        });

        if (!hasChanges) {
            renderUserInfo();
            $('#update-container').addClass('hidden');
            return;
        }

        // 获取用户表的tableName
        const userModel = adminJson.data.objects.find(obj => obj.tableName === 'user');
        if (!userModel) {
            alert('获取用户表信息失败');
            return;
        }

        $.ajax({
            url: `/admin/${userModel.tableName}?id=${userData.id}`,
            method: 'PATCH',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            contentType: 'application/json',
            data: JSON.stringify(updateData),
            success: function(response) {
                if (response.code === 200 || response.success) {  // 添加 success 检查
                    // 更新本地数据
                    Object.assign(userData, updateData);
                    // 显示成功提示
                    const message = $(`
                        <div class="fixed top-4 right-4 bg-green-500 text-white px-6 py-3 rounded-md shadow-lg z-50 animate-fade-in-out">
                            更新成功
                        </div>
                    `);
                    $('body').append(message);
                    setTimeout(() => message.remove(), 2000);
                    // 重新渲染用户信息
                    renderUserInfo();
                    // 隐藏更新按钮
                    $('#update-container').addClass('hidden');
                } else {
                    alert('更新失败：' + (response.message || '未知错误'));
                }
            },
            error: function(xhr) {
                alert('更新失败：' + (xhr.responseJSON?.message || '服务器错误'));
            }
        });
    });

    // 设置图标点击事件
    $('#settings-icon').click(function() {
        $('#data-page').addClass('hidden');
        $('#settings-page').removeClass('hidden');
        $(this).find('i').removeClass('text-gray-500').addClass('text-gray-900');
        // 隐藏分页
        $('#pagination').addClass('hidden');
    });

    // 返回按钮点击事件
    $('#back-to-dashboard').click(function() {
        $('#settings-page').addClass('hidden');
        $('#data-page').removeClass('hidden');
        $('#settings-icon').find('i').removeClass('text-gray-900').addClass('text-gray-500');
        // 显示分页
        $('#pagination').removeClass('hidden');
        // 重新渲染用户信息（取消所有编辑状态）
        renderUserInfo();
        // 隐藏更新按钮
        $('#update-container').addClass('hidden');
    });

    // 侧边栏菜单项点击时也返回数据页面
    $(document).on('click', '#sidebar-menu a', function() {
        $('#settings-page').addClass('hidden');
        $('#data-page').removeClass('hidden');
        $('#settings-icon').find('i').removeClass('text-gray-900').addClass('text-gray-500');
        // 显示分页
        $('#pagination').removeClass('hidden');
        // 重新渲染用户信息（取消所有编辑状态）
        renderUserInfo();
        // 隐藏更新按钮
        $('#update-container').addClass('hidden');
    });

    // 重置QueryForm为初始状态
    function resetQueryForm() {
        // 重置queryForm对象
        queryForm.pos = 0;
        queryForm.keyword = "";
        queryForm.filters = [];
        queryForm.orders = [];
        queryForm.foreignMode = false;
        queryForm.viewFields = [];
        queryForm.searchFields = [];

        // 重置UI元素
        $('#search-input').val('');
        $('#filter-field').val('');
        $('#filter-op').val('=');
        $('#filter-value').val('');
        $('#filter-value-start').val('');
        $('#filter-value-end').val('');
        $('#filter-value-between').hide();
        $('#filter-value').show();

        // 重置分页
        currentPage = 1;
        $("#page-numbers").text('1 / 1 页');
        $("#prev-page").prop("disabled", true);
        $("#next-page").prop("disabled", true);

        // 重置排序
        currentSortField = '';
        currentSortOrder = 'asc';
        $('th').find('.sort-arrow').removeClass('up down active').addClass('down');

        // 重置复选框
        $('#select-all').prop('checked', false);
        $('.row-checkbox').prop('checked', false);
        updateDeleteButtonState();
    }

    // 初始化侧边栏菜单
    function initSidebarMenu() {
        const sidebarMenu = $('#sidebar-menu');
        sidebarMenu.empty();

        if (adminJson.data && adminJson.data.objects) {
            adminJson.data.objects.forEach((item, index) => {
                const menuItem = $(`
                    <li>
                        <a href="#" class="flex items-center text-xl p-4 text-gray-700 hover:bg-gray-100" data-model="${item.tableName}" data-desc="${item.desc}" data-filterables='${JSON.stringify(item.filterables || [])}'>
                            <i class="fa-solid fa-database fa-ms mr-4 ml-2"></i> ${item.tableName}
                        </a>
                    </li>
                `);
                sidebarMenu.append(menuItem);

                // 如果是第一个模型，自动选中
                if (index === 0) {
                    currentModel = item.tableName;
                    $('.text-2xl.ml-16.font-bold.mb-6 h1').text(item.tableName);
                    $('.text-2xl.ml-16.font-bold.mb-6 p').text(item.desc);
                    menuItem.find('a').addClass('bg-gray-100');
                    // 初始化第一个模型的筛选字段
                    initFilterFields(item.filterables || []);
                    fetchDataAndRender();
                }
            });

            // 绑定点击事件
            sidebarMenu.find('a').click(function(e) {
                e.preventDefault();
                const model = $(this).data('model');
                const desc = $(this).data('desc');
                const filterables = $(this).data('filterables') || [];

                // 如果点击的是当前模型，不做任何操作
                if (model === currentModel) {
                    return;
                }

                // 重置QueryForm
                resetQueryForm();
                
                // 更新当前模型
                currentModel = model;
                
                // 更新标题和描述
                $('.text-2xl.ml-16.font-bold.mb-6 h1').text(model);
                $('.text-2xl.ml-16.font-bold.mb-6 p').text(desc);
                
                // 更新选中状态
                sidebarMenu.find('a').removeClass('bg-gray-100');
                $(this).addClass('bg-gray-100');
                
                // 确保创建表单隐藏，表格显示
                $('#create-form-container').addClass('hidden');
                $('#table-container').removeClass('hidden');
                
                // 更新筛选字段
                initFilterFields(filterables);
                
                // 加载数据
                fetchDataAndRender();
            });
        }
    }

    function initFilterFields(filterables) {
        const filterSelect = $('#filter-field');
        filterSelect.empty();
        filterSelect.append('<option value="">选择筛选字段</option>');
        filterables.forEach(field => {
            filterSelect.append(`<option value="${field}">${field}</option>`);
        });
        
        // 重置筛选操作符和值
        $('#filter-op').val('=');
        $('#filter-value').val('');
        $('#filter-value-start').val('');
        $('#filter-value-end').val('');
        $('#filter-value-between').hide();
        $('#filter-value').show();
    }

    function updateQueryForm() {
        queryForm.pos = (currentPage - 1) * pageSize;
        queryForm.keyword = $('#search-input').val();
        
        const filterField = $('#filter-field').val();
        const filterOp = $('#filter-op').val();
        let filterValue = $('#filter-value').val();
        
        if (filterField && filterOp) {
            if (filterOp === 'between') {
                const startValue = $('#filter-value-start').val();
                const endValue = $('#filter-value-end').val();
                if (startValue && endValue) {
                    queryForm.filters = [{
                        name: filterField,
                        value: [startValue, endValue],
                        op: 'between'
                    }];
                }
            } else if (filterValue) {
                queryForm.filters = [{
                    name: filterField,
                    value: filterValue,
                    op: filterOp
                }];
            } else {
                queryForm.filters = [];
            }
        } else {
            queryForm.filters = [];
        }

        if (currentSortField) {
            queryForm.orders = [{
                name: currentSortField,
                op: currentSortOrder
            }];
        } else {
            queryForm.orders = [];
        }
    }

    function fetchDataAndRender() {
        if (!currentModel) return;
        
        updateQueryForm();
        postData(`/admin/${currentModel}`, queryForm, function (response) {
            console.log('请求成功:', response);
            data = response.data;
            totalPages = Math.ceil(data.totalCount / pageSize);
            renderTable(data);
        }, function (jqXHR, textStatus, errorThrown) {
            console.error('请求失败:', textStatus, errorThrown);
            alert('请求失败，请稍后再试。');
        });
    }

    function renderTable(data) {
        const tableBody = $("#table-body");
        const tableHead = $("#table-head");
        tableHead.empty();
        tableBody.empty();

        if (!data.items || data.items.length === 0) {
            tableBody.append(`<tr><td colspan="3" class="text-center py-4">暂无数据</td></tr>`);
            return;
        }

        // 动态生成表头
        const firstItem = data.items[0];
        let headerRow = `<tr>
            <th class="border-b px-4 py-2 text-center" style="width: 50px;">
                <div class="flex items-center justify-center">
                    <input type="checkbox" id="select-all" class="form-checkbox h-4 w-4" />
                </div>
            </th>`;

        // 添加表头和排序图标
        for (const key in firstItem) {
            if (firstItem.hasOwnProperty(key)) {
                const isCurrentSort = currentSortField === key;
                const sortClass = isCurrentSort ? (currentSortOrder === 'asc' ? 'up active' : 'down active') : 'down';
                headerRow += `
                    <th class="border-b px-4 py-2 cursor-pointer select-none" data-field="${key}" style="min-width: 160px;">
                        <div class="flex items-center justify-between overflow-x-auto whitespace-nowrap">
                            <div class="flex-1 text-center">${key}</div>
                            <div class="sort-arrow ${sortClass} flex items-center justify-center ml-2" style="width: 20px; height: 20px; min-width: 20px;">
                                <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <path d="M7 10l5 5 5-5" stroke-linecap="round" stroke-linejoin="round"/>
                                </svg>
                            </div>
                        </div>
                    </th>`;
            }
        }
        headerRow += `</tr>`;
        tableHead.append(headerRow);

        // 动态生成表格内容
        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        const currentData = data.items.slice(startIndex, endIndex);

        currentData.forEach((item) => {
            let row = `<tr class="border-t border-gray-300">
                <td class="px-4 py-2 text-center" style="width: 50px;">
                    <div class="flex items-center justify-center">
                        <input type="checkbox" class="row-checkbox form-checkbox">
                    </div>
                </td>`;
            
            for (const key in item) {
                if (item.hasOwnProperty(key)) {
                    row += `
                        <td class="px-4 py-2" style="width: 200px;">
                            <div class="overflow-x-auto whitespace-nowrap">
                                ${item[key]}
                            </div>
                        </td>`;
                }
            }
            row += `</tr>`;
            tableBody.append(row);
        });

        // 更新分页信息
        $("#page-numbers").text(`${currentPage} / ${totalPages} 页`);
        $("#prev-page").prop("disabled", currentPage === 1);
        $("#next-page").prop("disabled", currentPage === totalPages);

        // 更新删除按钮状态
        updateDeleteButtonState();
        
        // 确保全选框状态与当前选中状态一致
        const allChecked = $('.row-checkbox').length > 0 && 
                          $('.row-checkbox').length === $('.row-checkbox:checked').length;
        $('#select-all').prop('checked', allChecked);
    }

    // 上一页按钮点击事件
    $('#prev-page').click(function () {
        if (currentPage > 1) {
            currentPage--;
            renderTable(data);
        }
    });

    // 下一页按钮点击事件
    $("#next-page").click(function () {
        if (currentPage < totalPages) {
            currentPage++;
            renderTable(data);
        }
    });

    // 修改排序点击事件
    $(document).on('click', 'th[data-field]', function() {
        const field = $(this).data('field');
        const arrow = $(this).find('.sort-arrow');
        
        // 重置其他所有列的排序图标
        $('th').not(this).find('.sort-arrow').removeClass('up down active').addClass('down');
        
        // 更新当前列的排序状态
        if (field === currentSortField) {
            currentSortOrder = currentSortOrder === 'asc' ? 'desc' : 'asc';
            arrow.toggleClass('up down');
        } else {
            currentSortField = field;
            currentSortOrder = 'asc';
            arrow.removeClass('down').addClass('up active');
        }
        
        updateQueryForm();
        fetchDataAndRender();
    });

    // 修改全选框事件处理
    $(document).on("change", "#select-all", function () {
        const isChecked = $(this).is(":checked");
        $(".row-checkbox").prop("checked", isChecked);
        updateDeleteButtonState();
    });

    // 使用事件委托绑定单个复选框变化事件
    $(document).on('change', '.row-checkbox', function() {
        const allChecked = $('.row-checkbox').length === $('.row-checkbox:checked').length;
        $('#select-all').prop('checked', allChecked);
        updateDeleteButtonState();
    });

    // 更新删除按钮状态
    function updateDeleteButtonState() {
        const checkedCount = $('.row-checkbox:checked').length;
        $('#delete-btn').prop('disabled', checkedCount === 0);
    }

    // 修改删除按钮点击事件
    $("#delete-btn").click(function () {
        const selectedIds = [];
        $(".row-checkbox:checked").each(function () {
            const row = $(this).closest("tr");
            const id = row.find("td:eq(1)").text();
            selectedIds.push(id);
        });

        if (selectedIds.length === 0) {
            alert("请选择要删除的数据");
            return;
        }

        if (confirm(`确定要删除选中的 ${selectedIds.length} 条数据吗？`)) {
            postData(`/admin/${currentModel.toLowerCase()}/delete`, { ids: selectedIds }, function (response) {
                if (response.success) {
                    alert("删除成功");
                    fetchDataAndRender();
                } else {
                    alert("删除失败：" + response.message);
                }
            }, function (error) {
                console.error('删除失败:', error);
                alert('删除失败，请稍后再试');
            });
        }
    });

    $("#search-icon").hover(
        function () {
            $("#hover-icon").show();
        },
        function () {
            $("#hover-icon").hide();
        }
    );

    // 移除搜索框的input事件监听
    $('#search-input').off('input');

    // 修改筛选按钮点击事件
    $('#apply-filter').click(function() {
        currentPage = 1;
        fetchDataAndRender();
    });

    // 添加回车键触发筛选
    $('#search-input, #filter-value, #filter-value-start, #filter-value-end').keypress(function(e) {
        if (e.which === 13) { // 回车键的键码是13
            currentPage = 1;
            fetchDataAndRender();
        }
    });

    // 添加筛选操作符变化事件
    $('#filter-op').change(function() {
        const selectedOp = $(this).val();
        if (selectedOp === 'between') {
            $('#filter-value').hide();
            $('#filter-value-between').show();
        } else {
            $('#filter-value').show();
            $('#filter-value-between').hide();
        }
    });

    // 添加筛选字段变化事件
    $('#filter-field').change(function() {
        const selectedField = $(this).val();
        if (!selectedField) {
            $('#filter-op').prop('disabled', true);
            $('#filter-value').prop('disabled', true);
            $('#filter-value-start').prop('disabled', true);
            $('#filter-value-end').prop('disabled', true);
            return;
        }

        $('#filter-op').prop('disabled', false);
        $('#filter-value').prop('disabled', false);
        $('#filter-value-start').prop('disabled', false);
        $('#filter-value-end').prop('disabled', false);

        // 根据字段类型设置不同的筛选选项
        const fieldType = getFieldType(selectedField);
        updateFilterOptions(fieldType);
    });

    function getFieldType(tableName) {
        // 这里可以根据实际需求判断字段类型
        // 例如通过字段名后缀或配置信息来判断
        if (tableName.toLowerCase().includes('time') || tableName.toLowerCase().includes('date')) {
            return 'datetime';
        } else if (tableName.toLowerCase().includes('id')) {
            return 'number';
        } else {
            return 'text';
        }
    }

    function updateFilterOptions(fieldType) {
        const filterOp = $('#filter-op');
        filterOp.empty();

        switch (fieldType) {
            case 'datetime':
                filterOp.append(`
                    <option value="=">=</option>
                    <option value=">">&gt;</option>
                    <option value="<">&lt;</option>
                    <option value=">=">&gt;=</option>
                    <option value="<=">&lt;=</option>
                    <option value="!=">!=</option>
                    <option value="between">介于</option>
                `);
                break;
            case 'number':
                filterOp.append(`
                    <option value="=">=</option>
                    <option value=">">&gt;</option>
                    <option value="<">&lt;</option>
                    <option value=">=">&gt;=</option>
                    <option value="<=">&lt;=</option>
                    <option value="!=">!=</option>
                    <option value="between">介于</option>
                `);
                break;
            case 'text':
                filterOp.append(`
                    <option value="=">=</option>
                    <option value="!=">!=</option>
                    <option value="like">包含</option>
                `);
                break;
        }
    }
    // 创建按钮点击事件
    $('#create-btn').click(function() {
        const currentModelData = adminJson.data.objects.find(obj => obj.tableName === currentModel);
        if (!currentModelData) return;

        $('#table-container').addClass('hidden');
        $('#create-form-container').removeClass('hidden');
        $('#pagination').addClass('hidden'); // 隐藏分页部分
        
        // 使用当前模型的字段信息
        const fields = currentModelData.edits || [];
        const requires = currentModelData.requires || [];
        
        // 清空并重新生成表单字段
        $('#form-fields').empty();
        fields.forEach(field => {
            const isRequired = requires.includes(field);
            const fieldHtml = `
            <div class="form-field">
                <label class="block text-sm font-medium text-gray-700 mb-1">
                ${field} ${isRequired ? '<span class="text-red-500">*</span>' : ''}
                </label>
                <input type="text" name="${field}" 
                class="w-full text-sm outline-none border-2 rounded-md h-8 px-2 border-gray-300 focus:border-gray-400"
                ${isRequired ? 'required' : ''}>
            </div>
            `;
            $('#form-fields').append(fieldHtml);
        });
    });

    // 取消按钮点击事件
    $('#cancel-create').click(function() {
        $('#create-form-container').addClass('hidden');
        $('#table-container').removeClass('hidden');
        $('#pagination').removeClass('hidden'); // 显示分页部分
        $('#create-form')[0].reset(); // 重置表单
    });

    // 表单提交事件
    $('#create-form').submit(function(e) {
        e.preventDefault();
        
        // 收集表单数据
        const formData = {};
        $(this).find('input').each(function() {
            const value = $(this).val().trim();
            if (value) {
                formData[$(this).attr('name')] = value;
            }
        });

        // 获取当前模型的必填字段
        const currentModelData = adminJson.data.objects.find(obj => obj.tableName === currentModel);
        const edits = currentModelData?.edits || [];
        
        // 验证必填字段
        const missingFields = edits.filter(field => !formData[field]);
        
        if (missingFields.length > 0) {
            alert('请填写以下必填字段：' + missingFields.join(', '));
            return;
        }

        // 发送创建请求
        $.ajax({
            url: '/admin/' + currentModel,
            method: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                if (response.code === 200) {
                    alert('创建成功！');
                    // 隐藏创建表单，显示表格和分页
                    $('#create-form-container').addClass('hidden');
                    $('#table-container').removeClass('hidden');
                    $('#pagination').removeClass('hidden');
                    // 重置表单
                    $('#create-form')[0].reset();
                    // 刷新数据
                    fetchDataAndRender();
                } else {
                    alert('创建失败：' + (response.message || '未知错误'));
                }
            },
            error: function(xhr) {
                alert('创建失败：' + (xhr.responseJSON?.message || '服务器错误'));
            }
        });
    });
});