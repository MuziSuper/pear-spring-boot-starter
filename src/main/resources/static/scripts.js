const baseUrl = window.location.origin;

const queryForm = {
    pos: 0,
    limit: 0,
    keyword: "",
    filters: [],
    orders: [],
    foreignMode: false,
    viewFields: [],
    searchFields: []
  };
  
  $(document).ready(function () {
    let data = {};
    const pageSize = 5
    let currentPage = 1; 
    let totalPages;
    fetchDataAndRender();
    function fetchDataAndRender() {
      postData(`${baseUrl}/admin/user`, queryForm, function (response) {
        console.log('请求成功:', response);
        data = response.data;
        totalPages = Math.ceil(data.totalCount / pageSize);
        console.log(totalPages);
        renderTable(data);
      }, function (jqXHR, textStatus, errorThrown) {
        console.error('请求失败:', textStatus, errorThrown);
        alert('请求失败，请稍后再试。');
      });
    }
  
    function renderTable(data) {
      const tableBody = $("#table-body");
      const tableHead = $("#table-head");
      tableHead.empty(); // 清空表头
      tableBody.empty(); // 清空表格内容
  
      if (data.length === 0) {
        // 如果没有数据，显示空状态
        tableBody.append(`<tr><td colspan="3" class="text-center py-4">暂无数据</td></tr>`);
        return;
      }
  
      // 动态生成表头
      const firstItem = data.items[0];
      let headerRow = `<tr><th class="border border-gray-300 px-4 py-2"><input type="checkbox" id="select-all" class="form-checkbox" /></th>`;
      for (const key in firstItem) {        
        if (firstItem.hasOwnProperty(key)) {
          headerRow += `<th class="border border-gray-300 px-4 py-2">${key}</th>`;
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
    }
  
    // 上一页按钮点击事件
    $('#prev-page').click(function () {
      console.log("243324");
      
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
  

  
    // Select all checkboxes
    $("#select-all").on("change", function () {
      const isChecked = $(this).is(":checked");
      $(".row-checkbox").prop("checked", isChecked);
    });
  
    // Delete selected rows
    $("#delete-btn").on("click", function () {
      $(".row-checkbox:checked").each(function () {
        const rowIndex = $(this).closest("tr").index();
        data.splice(rowIndex, 1);
      });
      fetchDataAndRender();
      $("#select-all").prop("checked", false); // Uncheck "select all" after deletion
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
  });