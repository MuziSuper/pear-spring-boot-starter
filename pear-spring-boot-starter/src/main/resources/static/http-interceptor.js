/**
 * 全局HTTP请求拦截器
 * 自动添加Authorization头，处理401状态码
 */

// 存储原始的jQuery ajax方法
const originalAjax = $.ajax;

// 重写jQuery的ajax方法
$.ajax = function(options) {
    // 获取token
    const token = localStorage.getItem('token');
    
    // 如果存在token，添加到请求头
    if (token) {
        if (!options.headers) {
            options.headers = {};
        }
        options.headers['Authorization'] = `${token}`;
    }
    
    // 添加全局错误处理
    const originalError = options.error;
    options.error = function(xhr, status, error) {
        // 处理401未授权错误
        if (xhr.status === 401) {
            // 清除本地存储的token
            localStorage.removeItem('token');
            // 跳转到登录页面
            window.location.href = '/auth/login';
            return;
        }
        
        // 调用原始的错误处理函数
        if (originalError) {
            originalError.call(this, xhr, status, error);
        }
    };
    
    // 调用原始的ajax方法
    return originalAjax.call(this, options);
};

// 检查登录状态的函数
function checkLogin() {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = '/auth/login';
        return false;
    }
    return true;
}

// 登出函数
function logout() {
    localStorage.removeItem('token');
    window.location.href = '/auth/login';
}

// 获取当前token
function getToken() {
    return localStorage.getItem('token');
}

// 将函数暴露到全局作用域
window.checkLogin = checkLogin;
window.logout = logout;
window.getToken = getToken;