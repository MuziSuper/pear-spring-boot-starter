// 从cookie中获取token的函数
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}

// 定义一个函数来执行POST请求
function postData(url, data, successCallback, errorCallback) {
    // 获取token并添加到请求头
    const token = getCookie('token');
    
    $.ajax({
        url: url,
        type: 'POST',
        contentType: 'application/json', // 设置内容类型为application/json
        headers: token ? { 'Authorization': token } : {}, // 添加Authorization头
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