// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import VueMarkdown from 'vue-markdown';

Vue.component('vue-markdown', VueMarkdown);
var axios = require('axios')
axios.defaults.baseURL = 'http://8.130.76.6:9000/api'
// 绑定到全局
Vue.prototype.$axios = axios

// 请求拦截器
axios.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['token'] = `${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  response => {
    return response;
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 清除本地存储的 token
      localStorage.removeItem('token');
      // 跳转到登录页或显示错误提示
      ElementUI.MessageBox.alert(error.response.data.msg||'认证失败,请重新登录', '提示', {
        confirmButtonText: '确定',
        callback: () => {
          // 跳转到登录页
          router.push('/login');
        }
      });
    }
    return Promise.reject(error);
  }
);

Vue.config.productionTip = false
Vue.use(ElementUI)
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
