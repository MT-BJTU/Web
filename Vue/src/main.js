// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
var axios = require('axios')
axios.defaults.baseURL = 'http://172.24.65.59:9000/api'
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


Vue.config.productionTip = false
Vue.use(ElementUI)
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  components: { App },
  template: '<App/>'
})
