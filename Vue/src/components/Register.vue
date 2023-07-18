<template>
    <div class="register">
      <div class="register__card">
        <h2 class="register__title">用户注册</h2>
        <el-form ref="registerForm" :model="registerForm" label-width="80px" class="register__form">
          <el-form-item label="用户名" prop="userName">
            <el-input v-model="registerForm.userName" placeholder="请输入用户名"></el-input>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="register" :loading="registerLoading" class="register__button">注册</el-button>
          </el-form-item>
          <div class="register__links">
            <span @click="toLogin">返回登录</span>
          </div>
        </el-form>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        registerForm: {
          userName: '',
          password: '',
          confirmPassword: ''
        },
        registerLoading: false
      };
    },
    methods: {
      register() {
  this.$refs.registerForm.validate((valid) => {
    if (valid) {
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        this.$message.error('两次输入的密码不一致');
        return;
      }
      this.registerLoading = true;
      // 发送注册请求
      this.$axios.post('/register', this.registerForm)
        .then(response => {
          this.registerLoading = false;
          this.$message(response.data.msg);
          if (response.data.code === 200) {
            // 注册成功后自动登录
            this.$axios.post('/login', {
          userName: this.registerForm.userName,
          password: this.registerForm.password
          })
              .then(response => {
                localStorage.setItem('token', response.data.data.token);
                this.$router.push('/');
              })
              .catch(error => {
                console.error('自动登录失败', error);
              });
          }
        })
        .catch(error => {
          this.registerLoading = false;
          console.error('注册失败', error);
        });
    } else {
      console.log('表单验证失败');
      return false;
    }
  });
},
      toLogin() {
        this.$router.push('/');
      }
    }
  };
  </script>
  
  <style scoped>
  .register {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-image: url(https://scott-gc.oss-cn-hangzhou.aliyuncs.com/img/202306041747308.jpg);
    background-size: cover;
    background-position: center;
  }
  
  .register__card {
    width: 400px;
    background-color: rgba(245, 245, 245, 0.7); /* 背景色透明度调整为 0.9 */
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  
  .register__title {
    text-align: center;
    font-size: 24px;
    margin-bottom: 20px;
    color: #333;
  }
  
  .register__form {
    margin-top: 20px;
  }
  
  .register__button {
    width: 100%;
    border-radius: 20px;
    background-color: #007aff;
  }
  
  .register__links {
    margin-top: 10px;
    text-align: center;
    font-size: 14px;
    color: #999;
    cursor: pointer;
  }
  
  .register__links span {
    margin: 0 10px;
  }
  
  .register__links span:hover {
    text-decoration: underline;
  }
  </style>
