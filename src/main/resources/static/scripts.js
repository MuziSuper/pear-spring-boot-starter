const baseUrl = window.location.origin;

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
    const pageSize = 5;
    let currentPage = 1;
    let totalPages;
    let currentSortField = '';
    let currentSortOrder = 'asc';

    function initFilterFields(fields) {
        const filterSelect = $('#filter-field');
        filterSelect.empty();
        filterSelect.append('<option value="">选择筛选字段</option>');
        fields.forEach(field => {
            filterSelect.append(`<option value="${field}">${field}</option>`);
        });
    }

    function updateQueryForm() {
        queryForm.pos = (currentPage - 1) * pageSize;
        queryForm.keyword = $('#search-input').val();
        
        const filterField = $('#filter-field').val();
        const filterValue = $('#filter-value').val();
        if (filterField && filterValue) {
            queryForm.filters = [{
                name: filterField,
                value: filterValue,
                op: '>'
            }];
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
        updateQueryForm();
        postData(`${baseUrl}/admin/user`, queryForm, function (response) {
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
        <th class="border-b px-4 py-2 text-center">
            <input type="checkbox" id="select-all" class="form-checkbox h-4 w-4" />
        </th>`;
  
      // 初始化筛选字段
      const fields = Object.keys(firstItem);
      initFilterFields(fields);
  
      // 添加表头和排序图标
      for (const key in firstItem) {
        if (firstItem.hasOwnProperty(key)) {
          const isCurrentSort = currentSortField === key;
          headerRow += `
            <th class="border-b px-4 py-2 cursor-pointer select-none" data-field="${key}">
              <div class="header-cell">
                <span class="field-text">${key}</span>
                <span class="sort-arrow ${isCurrentSort ? (currentSortOrder === 'asc' ? 'up active' : 'down active') : 'down'}">&gt;</span>
              </div>
            </th>`;
        }
      }
      headerRow += `</tr>`;
      tableHead.append(headerRow);
  
      // 动态生成表格内容
      const startIndex = (currentPage - 1) * pageSize; // 当前页的起始索引
      const endIndex = startIndex + pageSize; // 当前页的结束索引
      const currentData = data.items.slice(startIndex, endIndex);
  
      currentData.forEach((item) => {
        let row = `<tr class="border-t border-gray-300"><td class="px-4 py-2 text-center"><input type="checkbox" class="row-checkbox form-checkbox"></td>`;
        for (const key in item) {
          if (item.hasOwnProperty(key)) {
            row += `<td class="px-4 py-2">${item[key]}</td>`;
          }
        }
        row += `</tr>`;
        tableBody.append(row);
      });
  
      // 更新分页信息
      $("#page-numbers").text(`${currentPage} / ${totalPages} 页`);
      $("#prev-page").prop("disabled", currentPage === 1); // 如果是第一页，禁用上一页按钮
      $("#next-page").prop("disabled", currentPage === totalPages); // 如果是最后一页，禁用下一页按钮

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
        currentPage--; // 当前页减1
        renderTable(data);
      }
    });
  
    // 下一页按钮点击事件
    $("#next-page").click(function () {
      if (currentPage < totalPages) {
        currentPage++; // 当前页加1
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
  
    // 修改全选框事件处理，使用事件委托
    $(document).on("change", "#select-all", function () {
        const isChecked = $(this).is(":checked");
        $(".row-checkbox").prop("checked", isChecked);
        updateDeleteButtonState();
    });
  
    // 使用事件委托绑定单个复选框变化事件
    $(document).on('change', '.row-checkbox', function() {
        // 检查是否所有的复选框都被选中
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
        const id = row.find("td:eq(1)").text(); // 假设 ID 在第二列，根据实际情况调整
        selectedIds.push(id);
      });
  
      if (selectedIds.length === 0) {
        alert("请选择要删除的数据");
        return;
      }
  
      if (confirm(`确定要删除选中的 ${selectedIds.length} 条数据吗？`)) {
        // 调用删除接口
        postData(`${baseUrl}/admin/user/delete`, { ids: selectedIds }, function (response) {
          if (response.success) {
            alert("删除成功");
            fetchDataAndRender(); // 重新加载数据
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
  
    // 保存按钮逻辑
    $("#save-button").click(function () {
      const key = $("#data-key").val();
      const value = $("#data-value").val();
  
      if (!key || !value) {
        alert("请输入键和值！");
        return;
      }
  
      console.log("保存数据：", { key, value });
      alert("保存成功！");
  
      // 清空表单
      $("#data-key").val("");
      $("#data-value").val("");
    });
  
    // 取消按钮逻辑
    $("#cancel-button").click(function () {
      $("#data-key").val("");
      $("#data-value").val("");
      alert("操作已取消！");
    });

    // 添加搜索输入事件
    $('#search-input').on('input', function() {
        currentPage = 1; // 重置页码
        fetchDataAndRender();
    });

    // 添加筛选按钮点击事件
    $('#apply-filter').click(function() {
        currentPage = 1; // 重置页码
        fetchDataAndRender();
    });

    // 初始化加载数据
    fetchDataAndRender();
});