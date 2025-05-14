// 定义一个函数来执行POST请求
function postData(url, data, successCallback, errorCallback) {
    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json', // 设置内容类型为application/json
        data: JSON.stringify(data), // 将数据转换为JSON字符串
        success: function(response) {
            // 请求成功时的回调函数
            if (successCallback) {
                successCallback(response);
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // 请求失败时的回调函数
            if (errorCallback) {
                errorCallback(jqXHR, textStatus, errorThrown);
            }
        }
    });
}